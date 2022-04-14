package server.entities;


import commons.Answer;
import commons.IngamePlayer;
import commons.Joker;
import commons.Question;
import commons.GlobalLeaderBoardEntry;
import commons.events.Event;
import commons.events.FinalLeaderboardEvent;
import commons.events.IntermediaryLeaderboardEvent;
import commons.events.NewQuestionEvent;
import commons.events.PlayerListUpdateEvent;
import commons.events.ShowAnswersEvent;
import commons.events.TimeLeftUpdateEvent;
import org.apache.commons.lang3.NotImplementedException;
import server.services.GlobalLeaderBoardService;
import server.services.QuestionService;
import server.utils.LongPoll;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ServerGame {

    private enum LeaderboardState {
        NOT_SHOWN,
        BEING_SHOWN,
        ALREADY_SHOWN
    }
    public static final double QUESTION_DURATION = 20.0d;

    private final List<IngamePlayer> players;
    private final List<Question> questions;
    private final QuestionService questionService;
    private final GlobalLeaderBoardService globalLeaderBoardService;
    private LongPoll<IngamePlayer, Event> eventLongPoll = new LongPoll<>();
    private final boolean isMultiplayer;
    private int currentQuestionIndex = -1;
    private LeaderboardState intermediaryLeaderboardState = LeaderboardState.NOT_SHOWN;
    private LeaderboardState finalLeaderboardState = LeaderboardState.NOT_SHOWN;

    private Map<IngamePlayer, Answer> submittedAnswers = new HashMap<>();
    private long questionEndsAtNanoTime = -1;

    private final Map<IngamePlayer, Set<Joker>> playedJokers = new HashMap<>();
    private final Map<IngamePlayer, Double> pointsMultiplier = new HashMap<>();


    /**
     * Create a new ServerGame.
     *
     * @param players The list of players in this game
     * @param questionService The questionService
     * @param globalLeaderBoardService The Global Leaderboard Service
     * @param isMultiplayer true/false if game is multi-player/single-player respectively.
     */
    public ServerGame(List<IngamePlayer> players, QuestionService questionService,
                      GlobalLeaderBoardService globalLeaderBoardService, boolean isMultiplayer) {
        this.players = players;
        this.questionService = questionService;
        this.globalLeaderBoardService = globalLeaderBoardService;
        this.questions = this.questionService.generateRandomQuestions();
        this.isMultiplayer = isMultiplayer;
    }

    /**
     * Create a new ServerGame with a specified LongPoll instance, useful for testing.
     *
     * @param players         The list of players in this game
     * @param questionService The questionService
     * @param eventLongPoll   The LongPoll instance used for events
     * @param globalLeaderBoardService The Global Leaderboard Service
     * @param isMultiplayer true/false if game is multi-player/single-player respectively.
     */
    public ServerGame(List<IngamePlayer> players,
                      QuestionService questionService, GlobalLeaderBoardService globalLeaderBoardService,
                      LongPoll<IngamePlayer, Event> eventLongPoll, boolean isMultiplayer) {
        this.players = players;
        this.questionService = questionService;
        this.eventLongPoll = eventLongPoll;
        this.questions = questionService.generateRandomQuestions();
        this.globalLeaderBoardService = globalLeaderBoardService;
        this.isMultiplayer = isMultiplayer;
    }

    /**
     * Get all players in this game.
     *
     * @return The list of players
     */
    public List<IngamePlayer> getPlayers() {
        return players;
    }

    /**
     * Get all questions in this game.
     *
     * @return The list of all questions.
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Get the LongPoll for this game's events.
     *
     * @return The LongPoll
     */
    public LongPoll<IngamePlayer, Event> getEventLongPoll() {
        return eventLongPoll;
    }

    /**
     * Update the position of all players. Sets a player's position using {@link IngamePlayer#getPosition()}. Does not
     * change the order of players in the player list returned by {@link #getPlayers()}.
     */
    public void updatePlayerPositions() {
        List<Integer> points = this.players.stream()
                .map(p -> p.getPoints())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
        this.players.forEach(p -> p.setPosition(points.indexOf(p.getPoints()) + 1));
    }

    /**
     * Start the game.
     */
    public void start() {
        players.forEach(eventLongPoll::startTracking);
//        updatePlayerPositions();
        nextQuestion();
    }

    /**
     * Show the next question. Increments {@link #currentQuestionIndex}, sends a {@link PlayerListUpdateEvent} and a
     * {@link NewQuestionEvent}, then calls {@link #startQuestion()}.
     * If the question is number 10, Intermediary leaderboard is shown after it
     * In case it's number 20, Final leaderboard is shown and Results are put in global leaderboard repository.
     */
    public void nextQuestion() {
        if (currentQuestionIndex == 9 && intermediaryLeaderboardState == LeaderboardState.NOT_SHOWN) {
            eventLongPoll.reply(new IntermediaryLeaderboardEvent(players));
            intermediaryLeaderboardState = LeaderboardState.BEING_SHOWN;
            return;
        } else if (currentQuestionIndex == 19) {
            for (IngamePlayer player : getPlayers()) {
                GlobalLeaderBoardEntry glEntry = new GlobalLeaderBoardEntry(player.getName(),
                        player.getPoints(), isMultiplayer);
                this.globalLeaderBoardService.add(glEntry);
            }
            eventLongPoll.reply(new FinalLeaderboardEvent(players));
            return;
        }

        currentQuestionIndex++;

        // Remove points multipliers of the previous question
        pointsMultiplier.clear();

        // Send the list of players to the clients
        eventLongPoll.replySilently(new PlayerListUpdateEvent(players));

        // Send the question
        players.forEach(player -> {
            eventLongPoll.replySilently(player, new NewQuestionEvent(getCurrentQuestion(), getPlayedJokers(player),
                    currentQuestionIndex));
        });

        // Replies with a TimeLeftUpdateEvent
        startQuestion();
    }

    /**
     * This method is used to prevent multiple players from calling next question at the same time.
     * It checks if one player already clicked and with that prevents that problem.
     */
    public void continueAfterLeaderboard() {
        if (intermediaryLeaderboardState == LeaderboardState.BEING_SHOWN) {
            intermediaryLeaderboardState = LeaderboardState.ALREADY_SHOWN;
            nextQuestion();
        }
    }

    /**
     * Get the current question of the game.
     *
     * @return The current question
     */
    public Question getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    /**
     * Set the question time left to {@link #QUESTION_DURATION} and start the question timeout loop. Show the question
     * answers after this timeout has passed.
     */
    public void startQuestion() {
        setQuestionTimeLeft(QUESTION_DURATION);

        new Thread(() -> {
            // Wait until the question is over, with an additional delay of 0.5 seconds
            while (System.nanoTime() < questionEndsAtNanoTime + 1_000_000_000L * 0.5) {
                try {
                    // Repeatedly sleep for a short time so changes to `endsAtNanoTime` are handled
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            showQuestionAnswers();
        }).start();
    }

    /**
     * Send a {@link TimeLeftUpdateEvent} and update the timeout value.
     *
     * @param secondsLeft The updated amount of seconds left to answer the question
     */
    public void setQuestionTimeLeft(double secondsLeft) {
        questionEndsAtNanoTime = (long) (System.nanoTime() + secondsLeft * 1_000_000_000L);

        eventLongPoll.reply(new TimeLeftUpdateEvent(secondsLeft));
    }

    /**
     * Get the amount of time left to answer the question in seconds.
     *
     * @return The amount of time in seconds left, which is negative for questions that have already ended,
     * or {@link #QUESTION_DURATION} if no question has been started yet
     */
    public double getQuestionTimeLeft() {
        if (questionEndsAtNanoTime == -1) {
            return QUESTION_DURATION;
        }
        return Math.max((questionEndsAtNanoTime - System.nanoTime()) / 1_000_000_000d, 0);
    }

    /**
     * Get the nano time at which the question ends.
     *
     * @return The nano time
     */
    public long getQuestionEndsAtNanoTime() {
        return questionEndsAtNanoTime;
    }

    /**
     * Send a {@link ShowAnswersEvent} and assign a new {@link #submittedAnswers} map.
     */
    public void showQuestionAnswers() {
        List<Answer> answerList = submittedAnswers.values().stream().toList();
        for (Answer ans : answerList) {
            IngamePlayer player = ans.getPlayer();
            long answer = ans.getAnswer();
            double time = ans.getFractionTimeLeft();
            Question question = getCurrentQuestion();
            player.addPoints((int) (question.giveAmountOfPoints(answer, time) * getPointsMultiplier(player)));
        }
        this.updatePlayerPositions();
        eventLongPoll.replySilently(new PlayerListUpdateEvent(players));

        System.out.println("ShowAnswersEvent");
        eventLongPoll.reply(new ShowAnswersEvent(answerList));

        new Thread(() -> {
            // Wait after answer is shown for additional 2 seconds for player to read answer
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Clear the submitted answers map
            submittedAnswers = new HashMap<>();
            nextQuestion();
        }).start();
    }

    /**
     * Submit an answer for a player.
     *
     * @param player The player to submit the answer for
     * @param answer The player's answer
     */
    public void submitAnswer(IngamePlayer player, Answer answer) {
        answer.setPlayer(player);

        this.submittedAnswers.put(player, answer);

        if (submittedAnswers.keySet().containsAll(players)) {
            // Set the time to 0 to leave half a second for players that have some latency to still submit a different
            // answer.
            setQuestionTimeLeft(0);
        }
    }

    /**
     * Reduce the amount of time left to answer the question.
     */
    public void reduceTime() {
        double current = getQuestionTimeLeft();
        double reduced = current * 2 / 3;

        // Make sure there's some time left for players to answer, but never increase the time
        double updated = Math.max(reduced, Math.min(current, 3));

        setQuestionTimeLeft(updated);
    }

    /**
     * Set the points multiplier for the current question for a player.
     *
     * @param player     The player to set the points multiplier for
     * @param multiplier The points multiplier
     */
    public void setPointsMultiplier(IngamePlayer player, double multiplier) {
        pointsMultiplier.put(player, multiplier);
    }

    /**
     * Get the points multiplier for the current question for a player.
     *
     * @param player The player to get the points multiplier for
     * @return The points multiplier, 1.0 if not set
     */
    public double getPointsMultiplier(IngamePlayer player) {
        return pointsMultiplier.getOrDefault(player, 1.0d);
    }

    /**
     * Play a joker for a player.
     *
     * @param player The player to play the joker for
     * @param joker  The joker to play
     */
    public void playJoker(IngamePlayer player, Joker joker) {
        switch (joker) {
            case REDUCE_TIME -> reduceTime();
            case REMOVE_INCORRECT_ANSWER -> {
            }
            case DOUBLE_POINTS -> setPointsMultiplier(player, 2);
            default -> throw new NotImplementedException();
        }

        getPlayedJokers(player).add(joker);
    }

    /**
     * Get the jokers a player has already played.
     *
     * @param player The player to get the played jokers of
     * @return The set of played jokers
     */
    public Set<Joker> getPlayedJokers(IngamePlayer player) {
        playedJokers.putIfAbsent(player, new HashSet<>());
        return playedJokers.get(player);
    }

}
