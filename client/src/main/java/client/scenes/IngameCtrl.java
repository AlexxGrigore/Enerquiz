package client.scenes;

import client.Main;
import client.controls.AbstractQuestionCtrl;
import client.controls.EmotePopoverCtrl;
import client.controls.LeaderboardEntryCtrl;
import client.event.EventManager;

import commons.IngamePlayer;
import commons.Question;
import commons.MultipleActivitiesQuestion;
import commons.EstimationQuestion;
import commons.events.FinalLeaderboardEvent;
import commons.events.IntermediaryLeaderboardEvent;
import commons.MultipleChoiceQuestion;
import commons.events.NewQuestionEvent;
import client.hooks.SceneLifecycle;
import client.utils.Emote;
import client.utils.ServerUtils;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import commons.events.PlayerListUpdateEvent;
import commons.events.ShowAnswersEvent;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;


public class IngameCtrl implements Initializable, SceneLifecycle {

    private final ServerUtils serverUtils;
    private final EventManager eventManager;
    private static final Duration TRANSITION_DURATION = Duration.seconds(4);
    private int currentQuestionIndex;


    @FXML
    public FlowPane questionContainer;

    @FXML
    private VBox leaderboardEntryBox;

    @FXML
    private FlowPane emoteContainer;

    @FXML
    private FlowPane answerOptionContainer;

    @FXML
    private Pane emotePopoverContainer;

    @FXML
    public AnchorPane wholeScene;

    private EstimationQuestionCtrl estimationQuestionCtrl;
    private Scene estimationQuestion;

    //holds the in-game leaderboards while they are appearing.
    @FXML
    public AnchorPane mainPane;

    @FXML
    private javafx.scene.control.Button nextScene;

    @FXML
    private StackPane parentContainer;


    private MultipleActivitiesQuestionCtrl multipleActivitiesQuestionCtrl;
    private Scene multipleActivitiesQuestion;

    private AbstractQuestionCtrl currentQuestionCtrl;


    Map<IngamePlayer, Pair<LeaderboardEntryCtrl, Parent>> entryMap = new HashMap<>();

    private GameLeaderboardCtrl finalLeaderboardCtrl;
    private Scene finalLeaderboard;
    private Parent finalLeaderboardParent;

    private GameLeaderboardCtrl intermediaryLeaderboardCtrl;
    private Scene intermediaryLeaderboard;
    private Parent intermediaryLeaderboardParent;

    private AtomicBoolean isPollingEmotes = new AtomicBoolean(false);
    private AtomicBoolean isPollingAnswerOptions = new AtomicBoolean(false);


    /**
     * Create a new IngameCtrl.
     *
     * @param serverUtils  The ServerUtils instance used to send REST API calls
     * @param eventManager The EventManager instance used to register to events
     */
    @Inject
    public IngameCtrl(ServerUtils serverUtils, EventManager eventManager) {
        this.serverUtils = serverUtils;
        this.eventManager = eventManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Emote emote : Emote.values()) {
            this.addEmoteButton(emote);
        }
        Pair<GameLeaderboardCtrl, Parent> finalLeaderboard = Main.getFXML().load(GameLeaderboardCtrl.class,
                "client", "scenes", "final-leaderboard.fxml");
        Pair<GameLeaderboardCtrl, Parent> intermediaryLeaderboard = Main.getFXML().load(GameLeaderboardCtrl.class,
                "client", "scenes", "intermediate-leaderboard.fxml");
        this.intermediaryLeaderboardCtrl = intermediaryLeaderboard.getKey();
        this.intermediaryLeaderboard = new Scene(intermediaryLeaderboard.getValue());
        this.intermediaryLeaderboardParent = intermediaryLeaderboard.getValue();
        this.intermediaryLeaderboardParent.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/intermediary-leaderboard-scene.css").toExternalForm());
        this.intermediaryLeaderboardCtrl.setImages();
        this.intermediaryLeaderboard.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/intermediary-leaderboard-scene.css").toExternalForm());

        this.finalLeaderboardCtrl = finalLeaderboard.getKey();
        this.finalLeaderboard = new Scene(finalLeaderboard.getValue());
        this.finalLeaderboardParent = finalLeaderboard.getValue();
        this.finalLeaderboardParent.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/final-leaderboard-scene.css").toExternalForm());
        this.finalLeaderboardCtrl.setImages();
        this.finalLeaderboard.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/final-leaderboard-scene.css").toExternalForm());
        currentQuestionIndex = -1;
    }

    /**
     * Execute the {@link SceneLifecycle#onSceneHide(Scene, Scene)} hook on the previous question and remove it from
     * the scene.
     */
    public void removePreviousQuestion() {
        if (currentQuestionCtrl != null) {
            currentQuestionCtrl.onSceneHide(null, null);
            currentQuestionCtrl = null;
        }
        wholeScene.getChildren().clear();
    }

    /**
     * Method for showing the intermediary leaderboard on its event.
     *
     * @param intermediaryLeaderboardEvent
     */
    @Subscribe
    public void showIntermediaryLeaderboard(IntermediaryLeaderboardEvent intermediaryLeaderboardEvent) {
        intermediaryLeaderboardCtrl.setOnContinueQuiz(this::hideLeaderboard);
        Platform.runLater(() -> {
            this.intermediaryLeaderboardCtrl.fillLeaderboard();
            mainPane.setPickOnBounds(true);
            mainPane.getChildren().clear();
            mainPane.getChildren().add(intermediaryLeaderboardParent);
            mainPane.getChildren().clear();
            mainPane.getChildren().add(intermediaryLeaderboardParent);
            System.out.println(intermediaryLeaderboardEvent);
            removePreviousQuestion();
        });
    }

    /**
     * Method for showing the final leaderboard on its event.
     *
     * @param finalLeaderboardEvent
     */
    @Subscribe
    public void showFinalLeaderboard(FinalLeaderboardEvent finalLeaderboardEvent) {
        finalLeaderboardCtrl.setOnFinishedQuiz(this::hideLeaderboard);
        Platform.runLater(() -> {
            this.finalLeaderboardCtrl.fillLeaderboard();
            mainPane.setPickOnBounds(true);
            mainPane.getChildren().clear();
            mainPane.getChildren().add(finalLeaderboardParent);
            mainPane.getChildren().clear();
            mainPane.getChildren().add(finalLeaderboardParent);
            System.out.println(finalLeaderboardParent);
            removePreviousQuestion();
        });
    }

    /**
     * Method used to hide the leaderboard.
     */
    public void hideLeaderboard() {
        mainPane.setPickOnBounds(false);
        mainPane.getChildren().clear();
    }

    /**
     * Shows multiple choice question in the subScene.
     *
     * @param question a multiple choice-question from the server.
     */
    public void showMultipleChoiceQuestion(MultipleChoiceQuestion question) {
        Pair<MultipleChoiceQuestionCtrl, Parent> multipleChoiceQuestion =
                Main.getFXML().load(MultipleChoiceQuestionCtrl.class, "client",
                        "scenes", "MultipleChoiceQuestion.fxml");

        MultipleChoiceQuestionCtrl ctrl = multipleChoiceQuestion.getKey();
        Parent parent = multipleChoiceQuestion.getValue();

        parent.getStylesheets().addAll(
                this.getClass().getResource("/client/stylesheets/multiple_choice_question.css").toExternalForm());

        ctrl.setQuestion(question);

        removePreviousQuestion();
        wholeScene.getChildren().add(parent);
        currentQuestionCtrl = ctrl;
        currentQuestionCtrl.onSceneShow(null, null);

        ctrl.startQuestion();

        ctrl.setQuestionIndexLabel(currentQuestionIndex + 2);
    }

    /**
     * Shows estimating question in the subScene.
     *
     * @param question Question that will be shown.
     */
    public void showEstimateQuestion(EstimationQuestion question) {
        Pair<EstimationQuestionCtrl, Parent> estimationQuestion =
                Main.getFXML().load(EstimationQuestionCtrl.class, "client", "scenes", "EstimationQuestion.fxml");

        EstimationQuestionCtrl ctrl = estimationQuestion.getKey();
        Parent parent = estimationQuestion.getValue();

        parent.getStylesheets().addAll(
                this.getClass().getResource("/client/stylesheets/estimation-question.css").toExternalForm());

        ctrl.setQuestion(question);

        removePreviousQuestion();
        wholeScene.getChildren().add(parent);
        currentQuestionCtrl = ctrl;
        currentQuestionCtrl.onSceneShow(null, null);

        ctrl.startQuestion();

        ctrl.setQuestionIndexLabel(currentQuestionIndex + 2);
    }

    /**
     * Shows multiple activities question in the subScene.
     *
     * @param question Question that will be shown.
     */
    public void showMultipleActivitiesQuestion(MultipleActivitiesQuestion question) {
        Pair<MultipleActivitiesQuestionCtrl, Parent> multipleActivitiesQuestion =
                Main.getFXML().load(MultipleActivitiesQuestionCtrl.class, "client",
                        "scenes", "MultipleActivitiesQuestion.fxml");

        MultipleActivitiesQuestionCtrl ctrl = multipleActivitiesQuestion.getKey();
        Parent parent = multipleActivitiesQuestion.getValue();

        parent.getStylesheets().addAll(
                this.getClass().getResource("/client/stylesheets/multiple_activities_question.css").toExternalForm());

        ctrl.setQuestion(question);

        removePreviousQuestion();
        wholeScene.getChildren().add(parent);
        currentQuestionCtrl = ctrl;
        currentQuestionCtrl.onSceneShow(null, null);

        ctrl.startQuestion();

        ctrl.setQuestionIndexLabel(currentQuestionIndex + 2);
    }


    /**
     * Add a player to  the leaderboard.
     *
     * @param player The player
     */
    public void addPlayer(IngamePlayer player) {
        System.out.println(player);
        System.out.println(player.getName());

        Pair<LeaderboardEntryCtrl, Parent> entryPair =
                Main.getFXML().load(LeaderboardEntryCtrl.class, "client", "controls", "LeaderboardEntry.fxml");
        entryMap.put(player, entryPair);

        entryPair.getKey().setPlayer(player);
        leaderboardEntryBox.getChildren().add(entryPair.getValue());
    }

    /**
     * Remove a player from the leaderboard.
     *
     * @param player The player
     * @return `true` the player has successfully been removed, `false` otherwise
     */
    public boolean removePlayer(IngamePlayer player) {
        if (!entryMap.containsKey(player)) {
            return false;
        }

        Pair<LeaderboardEntryCtrl, Parent> entryPair = entryMap.get(player);
        leaderboardEntryBox.getChildren().remove(entryPair.getValue());
        entryMap.remove(player);

        return true;
    }

    /**
     * Clear the leaderboard and add all players of the update.
     *
     * @param playerListUpdateEvent The update event with the updated list of players
     */
    @Subscribe
    public void onPlayerListUpdate(PlayerListUpdateEvent playerListUpdateEvent) {
        // Sync with UI thread
        Platform.runLater(() -> {

            List<IngamePlayer> auxPlayers = entryMap.keySet().stream().toList();
            for (IngamePlayer player : auxPlayers) {
                this.removePlayer(player);
            }

            for (IngamePlayer player : playerListUpdateEvent.getPlayerList()) {
                this.addPlayer(player);
            }
        });
    }

    /**
     * Add a new button with the emote's image and send a REST API call on click.
     *
     * @param emote The emote add a button for
     */
    public void addEmoteButton(Emote emote) {
        ImageView img = new ImageView(emote.getImage());
        img.setOnMouseClicked((MouseEvent e) -> {
            this.sendEmote(emote);
        });

        emoteContainer.getChildren().add(img);
    }

    /**
     * Send an emote to the server to be broadcasted to all players.
     *
     * @param emote The emote to send
     */
    public void sendEmote(Emote emote) {
        System.out.println("Sending emote " + emote.name());
        serverUtils.sendEmote(emote);
    }


    /**
     * Show an emote in the scene.
     *
     * @param playerName The name of the player that sent the emote
     * @param emote      The emote sent by the player
     */
    public void showEmote(String playerName, Emote emote) {
        System.out.println("Showing emote " + emote.name() + " sent by " + playerName);

        entryMap.forEach((player, entryPair) -> {
            if (!player.getName().equals(playerName)) {
                return;
            }

            System.out.println(entryPair.getValue().boundsInParentProperty().get());

            Pair<EmotePopoverCtrl, Parent> emotePair =
                    Main.getFXML().load(EmotePopoverCtrl.class, "client", "controls", "EmotePopover.fxml");

            EmotePopoverCtrl emotePopoverCtrl = emotePair.getKey();
            Parent emotePopoverParent = emotePair.getValue();

            emotePopoverCtrl.setImage(emote.getImage());

            // Lambda that puts the emote popover in the correct position
            Runnable updatePopoverPosition = () -> {
                Bounds sceneBounds = entryPair.getValue().localToScene(entryPair.getValue().getBoundsInLocal());

                emotePopoverParent.setTranslateY(sceneBounds.getCenterY() - 42.0 / 2);
            };

            // Listen to changes in the leaderboard entry's position in the scene
            ChangeListener<Transform> onLocalToSceneTransformChange = (observable, oldValue, newValue) -> {
                updatePopoverPosition.run();
            };
            entryPair.getValue().localToSceneTransformProperty().addListener(onLocalToSceneTransformChange);

            // Fade out animation
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), emotePopoverParent);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            SequentialTransition sequentialTransition = new SequentialTransition(
                    new PauseTransition(Duration.seconds(2)),
                    fadeTransition
            );
            sequentialTransition.setOnFinished(event -> {
                entryPair.getValue().localToSceneTransformProperty().removeListener(onLocalToSceneTransformChange);
                emotePopoverContainer.getChildren().remove(emotePopoverParent);
            });

            // Adding children can only be done on the FX application thread
            Platform.runLater(() -> {
                emotePopoverContainer.getChildren().add(emotePopoverParent);
                updatePopoverPosition.run();
                sequentialTransition.play();
            });

        });
    }


    /**
     * Start the loop that polls for pending emotes.
     */
    public void startPollingEmotes() {
        // Prevent concurrent polling
        if (isPollingEmotes.get()) {
            return;
        }

        isPollingEmotes.set(true);

        pollEmotes();
    }

    /**
     * Stop the loop that polls for pending emotes.
     */
    public void stopPollingEmotes() {
        isPollingEmotes.set(false);
    }

    /**
     * Immediately poll for pending emotes. Automatically polls again when finished and {@link #isPollingEmotes} is
     * true.
     */
    public void pollEmotes() {
        new Thread(() -> {
            try {
                System.out.println("Polling");

                if (!isPollingEmotes.get()) {
                    System.out.println("Polling stopped");
                    return;
                }

                this.serverUtils.pollEmotes().forEach(this::showEmote);
                System.out.println("Polling complete");

                pollEmotes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onSceneShow(Scene oldScene, Scene newScene) {
        startPollingEmotes();
        this.eventManager.getEventBus().register(this);
        this.eventManager.startPollingEvents();
    }

    @Override
    public void onSceneHide(Scene oldScene, Scene newScene) {
        stopPollingEmotes();
        this.eventManager.getEventBus().unregister(this);
    }

    /**
     * Ran when a NewQuestionEvent is posted on the event bus.
     *
     * @param newQuestionEvent The NewQuestionEvent event
     */
    @Subscribe
    public void onNewQuestion(NewQuestionEvent newQuestionEvent) {
        Platform.runLater(() -> {
            wholeScene.opacityProperty().set(1);
            Question question = newQuestionEvent.getQuestion(); // this line listens to questionEvent

            if (question instanceof MultipleChoiceQuestion) {
                showMultipleChoiceQuestion((MultipleChoiceQuestion) question);
            } else if (question instanceof EstimationQuestion) {
                showEstimateQuestion((EstimationQuestion) question);
            } else if (question instanceof MultipleActivitiesQuestion) {
                showMultipleActivitiesQuestion((MultipleActivitiesQuestion) question);
            } else {
                System.out.println("It shouldn't reach here, if you see this printed. Something is wrong");
            }

            currentQuestionIndex = newQuestionEvent.getQuestionIndex();

            if (currentQuestionIndex + 2 > 20) {
                currentQuestionIndex %= 20;
            }

            System.out.println("New question received: " + newQuestionEvent);

            System.out.println("Disabled jokers: " + newQuestionEvent.getDisabledJokers());
            currentQuestionCtrl.setDisabledJokers(newQuestionEvent.getDisabledJokers());
            // System.out.println("New question received: " + newQuest ionEvent);
            hideLeaderboard();
        });

    }

//    /**
//     * This function make a SMOOTH transition form the Ingame scene to another scene
//     * THIS IS KEPT JUST IN CASE WE HAVE TIME LEFT TO MAKE IT LOOK NICER
//     * For testing purpose, I made the transition to the "EnterURLPade"
//     */
//    public void transitionNextScene(){
//        System.out.println("Button pressed!");
//        var auxiliar
//                = Main.getFXML().load(EnterURLPageCtrl.class, "client", "scenes", "EstimationQuestion.fxml");
//
//        Parent root = auxiliar.getValue();
//        Scene scene = wholeScene.getScene();
//        root.translateYProperty().set(scene.getHeight());
//        parentContainer.getChildren().add(root);
//
//        Timeline timeline = new Timeline();
//        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
//        KeyFrame kf = new KeyFrame(Duration.seconds(2), kv);
//        timeline.getKeyFrames().add(kf);
//        timeline.setOnFinished(event -> {
//            parentContainer.getChildren().remove(wholeScene.getChildren());
//        });
//        timeline.play();
//        showEstimateQuestion(new EstimationQuestion("How much?", 69));
//    }

    /**
     * This function make a transition form one question to another one.
     * The next question scene is not loaded, only the text of the question is changed
     */
    public void transitionNextScene() {
        System.out.println("Button pressed!");
        var auxiliar
                = Main.getFXML().load(EnterURLPageCtrl.class, "client", "scenes", "TransitionScene.fxml");
        wholeScene.getChildren().add(auxiliar.getValue());

        PauseTransition pause = new PauseTransition(TRANSITION_DURATION);
        pause.setOnFinished(e -> {
            wholeScene.getChildren().remove(auxiliar.getValue());
            showEstimateQuestion(new EstimationQuestion("Hou much??", 69));
        });
        pause.play();
    }

    /**
     * Called when the client receives a ShowAnswersEvent. Waits a couple seconds, then reduces the opacity of the
     * question scene to zero.
     *
     * @param showAnswersEvent The ShowAnswersEvent
     */
    @Subscribe
    public void onShowAnswersEvent(ShowAnswersEvent showAnswersEvent) {
        Platform.runLater(() -> {
            new Timeline(
                    new KeyFrame(Duration.seconds(3), new KeyValue(wholeScene.opacityProperty(), 1)),
                    new KeyFrame(Duration.seconds(3.5), new KeyValue(wholeScene.opacityProperty(), 0))
            ).play();
        });
    }

}
