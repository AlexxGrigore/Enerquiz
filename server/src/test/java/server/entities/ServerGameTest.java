package server.entities;

import commons.Answer;
import commons.IngamePlayer;
import commons.Joker;
import commons.MultipleChoiceQuestion;
import commons.Question;
import commons.events.Event;
import commons.events.TimeLeftUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.GlobalLeaderBoardService;
import server.services.QuestionService;
import server.utils.LongPoll;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ServerGameTest {

    private List<IngamePlayer> players;
    private QuestionService questionService;
    private GlobalLeaderBoardService globalLeaderBoardService;
    private IngamePlayer playerA;
    private IngamePlayer playerB;
    private IngamePlayer playerC;
    private IngamePlayer playerD;
    private LongPoll<IngamePlayer, Event> eventLongPoll;
    private ServerGame game;

    @BeforeEach
    public void setup() {
        questionService = mock(QuestionService.class);
        globalLeaderBoardService = mock(GlobalLeaderBoardService.class);
        when(questionService.generateRandomQuestions()).thenReturn(
                IntStream.range(0, 20)
                        .mapToObj(index -> new MultipleChoiceQuestion("Question " + (index + 1), 0))
                        .collect(Collectors.toList())
        );

        players = new ArrayList<>();

        playerA = new IngamePlayer("Player A");
        playerB = new IngamePlayer("Player B");
        playerC = new IngamePlayer("Player C");
        playerD = new IngamePlayer("Player D");

        players.add(playerA);
        players.add(playerB);
        players.add(playerC);
        players.add(playerD);

        eventLongPoll = new LongPoll<>();

        game = new ServerGame(players, questionService, globalLeaderBoardService, eventLongPoll, true);
    }

    @Test
    void getPlayers() {
        assertEquals(players, game.getPlayers());
    }

    @Test
    void updatePlayerPositionsSorted() {
        playerA.setPoints(100);
        playerB.setPoints(200);
        playerC.setPoints(10);
        playerD.setPoints(5);

        game.updatePlayerPositions();

        assertEquals(2, playerA.getPosition());
        assertEquals(1, playerB.getPosition());
        assertEquals(3, playerC.getPosition());
        assertEquals(4, playerD.getPosition());
    }

    @Test
    void updatePlayerPositionsAllZeroPoints() {
        playerA.setPoints(0);
        playerB.setPoints(0);
        playerC.setPoints(0);
        playerD.setPoints(0);

        game.updatePlayerPositions();

        assertEquals(1, playerA.getPosition());
        assertEquals(1, playerB.getPosition());
        assertEquals(1, playerC.getPosition());
        assertEquals(1, playerD.getPosition());
    }

    @Test
    void updatePlayerPositionsOneWithPoints() {
        playerA.setPoints(0);
        playerB.setPoints(0);
        playerC.setPoints(100);
        playerD.setPoints(0);

        game.updatePlayerPositions();

        assertEquals(2, playerA.getPosition());
        assertEquals(2, playerB.getPosition());
        assertEquals(1, playerC.getPosition());
        assertEquals(2, playerD.getPosition());
    }

    @Test
    void updatePlayerPositionsStable() {
        playerA.setPoints(400);
        playerB.setPoints(200);
        playerC.setPoints(300);
        playerD.setPoints(400);

        game.updatePlayerPositions();

        assertEquals(playerA, game.getPlayers().get(0));
        assertEquals(playerB, game.getPlayers().get(1));
        assertEquals(playerC, game.getPlayers().get(2));
        assertEquals(playerD, game.getPlayers().get(3));
    }

    @Test
    void setQuestionTimeLeftEvent() {
        eventLongPoll.startTracking(playerA);

        game.setQuestionTimeLeft(20);

        CompletableFuture<List<Event>> polled = eventLongPoll.poll(playerA, true);
        assertTrue(polled.isDone());

        Event event = polled.getNow(null).get(0);
        assertInstanceOf(TimeLeftUpdateEvent.class, event);

        assertEquals(20, ((TimeLeftUpdateEvent) event).getTimeLeft());
    }

    @Test
    void setQuestionTimeLeftNanoTime() {
        long expected = System.nanoTime() + 20 * 1_000_000_000L;
        game.setQuestionTimeLeft(20);

        double actual = game.getQuestionEndsAtNanoTime();

        assertTrue(actual >= expected, "At least some time should have passed since computing the "
                + "expected result. expected=" + expected + " actual=" + actual);

        assertTrue(actual <= expected + 10_000_000L, "Actual should be within 10ms of the expected result.");
    }

    @Test
    void getQuestionEndsAtNanoTimeInitial() {
        assertEquals(-1, game.getQuestionEndsAtNanoTime());
    }

    @Test
    void getQuestionEndsAtNanoTimeAfterSetQuestionTimeLeft() {
        game.setQuestionTimeLeft(20);
        assertNotEquals(-1, game.getQuestionEndsAtNanoTime());
    }

    @Test
    void getQuestionTimeLeftNoQuestion() {
        assertEquals(ServerGame.QUESTION_DURATION, game.getQuestionTimeLeft());
    }

    @Test
    void getQuestionTimeLeftActive() {
        game.setQuestionTimeLeft(20);

        double actual = game.getQuestionTimeLeft();
        assertTrue(actual <= 20);
        assertTrue(actual >= 20 - 0.01);
    }

    @Test
    void getQuestionTimeLeftEnded() {
        game.setQuestionTimeLeft(0);

        double actual = game.getQuestionTimeLeft();
        assertTrue(actual <= 0);
        assertTrue(actual >= 0 - 0.01);
    }

    @Test
    void reduceTimeFromTwentySeconds() {
        game.setQuestionTimeLeft(20);
        game.reduceTime();

        double actual = game.getQuestionTimeLeft();
        double expected = 20.0 * 2 / 3;

        assertTrue(actual <= expected);
        assertTrue(actual >= expected - 0.01);
    }

    @Test
    void reduceTimeNoChangeBelowThreeSeconds() {
        game.setQuestionTimeLeft(2);
        game.reduceTime();

        double actual = game.getQuestionTimeLeft();
        double expected = 2;

        assertTrue(actual <= expected);
        assertTrue(actual >= expected - 0.01);
    }

    @Test
    void playJokerReduceTime() {
        game.setQuestionTimeLeft(20);
        game.playJoker(playerA, Joker.REDUCE_TIME);

        double actual = game.getQuestionTimeLeft();
        double expected = 20.0 * 2 / 3;

        assertTrue(actual <= expected);
        assertTrue(actual >= expected - 0.01);
    }

    @Test
    void getPlayedJokersEmpty() {
        assertEquals(Set.of(), game.getPlayedJokers(playerA));
    }

    @Test
    void getPlayedJokersOneJoker() {
        game.playJoker(playerA, Joker.REDUCE_TIME);

        assertEquals(Set.of(Joker.REDUCE_TIME), game.getPlayedJokers(playerA));
    }

    @Test
    void getPlayedJokersOthersNotAffected() {
        game.playJoker(playerA, Joker.REDUCE_TIME);

        assertEquals(Set.of(Joker.REDUCE_TIME), game.getPlayedJokers(playerA));

        assertEquals(Set.of(), game.getPlayedJokers(playerB));
        assertEquals(Set.of(), game.getPlayedJokers(playerC));
        assertEquals(Set.of(), game.getPlayedJokers(playerD));
    }

    @Test
    void getCurrentQuestionNoQuestionYet() {
        assertThrows(IndexOutOfBoundsException.class, game::getCurrentQuestion);
    }

    @Test
    void getCurrentQuestionFirstQuestion() {
        game.start();
        assertInstanceOf(Question.class, game.getCurrentQuestion());
    }

    @Test
    void getPointsMultiplierNotSet() {
        assertEquals(1, game.getPointsMultiplier(playerA));
    }

    @Test
    void setPointsMultiplier() {
        game.setPointsMultiplier(playerA, 2);

        assertEquals(2, game.getPointsMultiplier(playerA));
    }

    @Test
    void playJokerDoublePointsPlayer() {
        game.playJoker(playerA, Joker.DOUBLE_POINTS);

        assertEquals(2, game.getPointsMultiplier(playerA));
    }

    @Test
    void playJokerDoublePointsNotAffectsOthers() {
        game.playJoker(playerA, Joker.DOUBLE_POINTS);

        assertEquals(1, game.getPointsMultiplier(playerB));
    }

    @Test
    void nextQuestionClearsPointsModifiers() {
        game.start();

        game.setPointsMultiplier(playerA, 2);

        game.nextQuestion();

        assertEquals(1, game.getPointsMultiplier(playerA));
    }

    @Test
    void pointsMultiplierAppliedOnCorrectAnswer() {
        game.start();

        Answer correctAnswer = new Answer(0, 1, playerA);

        int pointsStart = playerA.getPoints();
        game.submitAnswer(playerA, correctAnswer);
        game.showQuestionAnswers();
        int pointsGained = playerA.getPoints() - pointsStart;

        game.nextQuestion();

        pointsStart = playerA.getPoints();
        game.setPointsMultiplier(playerA, 2);
        game.submitAnswer(playerA, correctAnswer);
        game.showQuestionAnswers();
        int multipliedPointsGained = playerA.getPoints() - pointsStart;

        assertEquals(2 * pointsGained, multipliedPointsGained);
    }

    @Test
    void onePlayerSubmittedDoesNotSetTimeLeftToZero() {
        game.start();

        game.submitAnswer(playerA, new Answer(0, 1.0d, playerA));

        double actual = game.getQuestionTimeLeft();
        assertTrue(actual <= 20);
        assertTrue(actual >= 20 - 0.01);
    }

    @Test
    void allPlayersSubmittedSetsTimeLeftToZero() {
        game.start();

        players.forEach(player -> game.submitAnswer(player, new Answer(0, 1.0d, player)));

        assertTrue(game.getQuestionTimeLeft() < 1);
    }
}
