package client.controls;

import client.event.EventManager;
import client.hooks.SceneLifecycle;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.common.eventbus.Subscribe;
import commons.Answer;
import commons.Joker;
import commons.Question;
import commons.events.ShowAnswersEvent;
import commons.events.TimeLeftUpdateEvent;
import jakarta.ws.rs.core.Response;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * abstract class for question controllers.
 */
public abstract class AbstractQuestionCtrl implements SceneLifecycle {

    public static final double QUESTION_DURATION = 20.0d;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button reduceTimeJokerButton;

    @FXML
    private Button doublePointsJokerButton;

    @FXML
    private Button removeIncorrectAnswerJokerButton;

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final EventManager eventManager;
    private Question currentQuestion;
    protected long correctAnswer;


    @FXML
    private Label questionIndexLabel;

    private Timeline progressBarTimeline;

    /**
     * Create a new AbstractQuestionCtrl.
     *
     * @param serverUtils  The ServerUtils instance
     * @param mainCtrl     The MainCtrl instance
     * @param eventManager The EventManager instance
     */
    public AbstractQuestionCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, EventManager eventManager) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        this.eventManager = eventManager;
    }


    /**
     * This method will show the question, set the images and answers.
     *
     * @param question the question that will be displayed
     */
    public void setQuestion(Question question) {
        currentQuestion = question;
    }

    /**
     * Starts the progress bar.
     */
    public void startQuestion() {
        setTimeLeft(QUESTION_DURATION);
    }

    /**
     * Called when the client receives a TimeLeftUpdateEvent.
     *
     * @param timeLeftUpdateEvent The TimeLeftUpdateEvent
     */
    @Subscribe
    public void onTimeLeftUpdateEvent(TimeLeftUpdateEvent timeLeftUpdateEvent) {
        setTimeLeft(timeLeftUpdateEvent.getTimeLeft());
    }

    /**
     * Controls the progress bar, let it run down from a certain amount of time and execute {@link #onTimeout()} when
     * the time has run out.
     *
     * @param timeLeft The time the player has to answer
     */
    public void setTimeLeft(double timeLeft) {

        if (progressBarTimeline != null) {
            progressBarTimeline.pause();
        }

        progressBarTimeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), timeLeft / QUESTION_DURATION)
                ),
                new KeyFrame(
                        Duration.seconds(timeLeft),
                        e -> onTimeout(),
                        new KeyValue(progressBar.progressProperty(), 0)
                )
        );

        progressBarTimeline.play();
    }

    /**
     * Submit an answer to the server.
     *
     * @param answer The answer
     */
    public void submitAnswer(long answer) {
        Response response = serverUtils.submitAnswer(new Answer(answer, progressBar.progressProperty().get(), null));
        System.out.println("Submit answers response: " + response);

        if (response.getStatusInfo().getFamily() == Response.Status.Family.CLIENT_ERROR) {
            int statusCode = response.getStatusInfo().getStatusCode();
            String reasonPhrase = response.getStatusInfo().getReasonPhrase();

            System.out.println("Submit answers failed: " + statusCode + " " + reasonPhrase
                    + (response.hasEntity() ? ": " + response.readEntity(String.class) : ""));
        }
    }

    /**
     * Method that leaves the quiz on the exit button press.
     */
    public void exit() {
        mainCtrl.showGamemode();
        eventManager.stopPollingEvents();
    }

    /**
     * Called when the time runs out on the client, but the server hasn't said the round is over yet. Can be used to
     * disable buttons, for example.
     */
    public void onTimeout() {
    }

    /**
     * Called when the client receives a ShowAnswersEvent. Calls {@link #showAnswers} later, in the UI thread.
     *
     * @param showAnswersEvent The ShowAnswersEvent
     */
    @Subscribe
    public void onShowAnswersEvent(ShowAnswersEvent showAnswersEvent) {
        Platform.runLater(() -> {
            System.out.println("Received answers: " + showAnswersEvent.getSubmittedAnswers());
            showAnswers(showAnswersEvent.getSubmittedAnswers());
        });
    }

    /**
     * Show the correct answer and answers submitted by players.
     *
     * @param submittedAnswers A map of players to their answers
     */
    public abstract void showAnswers(List<Answer> submittedAnswers);

    /**
     * Play the {@link Joker#REDUCE_TIME} joker. If successful, disable the joker button.
     */
    public void playReduceTimeJoker() {
        Response response = serverUtils.playJoker(Joker.REDUCE_TIME);

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            reduceTimeJokerButton.setDisable(true);
        }
    }

    /**
     * This method returns index of answer that should be removed.
     * @param correctAnswer Index of correct answer.
     * @return Index of answer to be removed.
     */
    public int answerToRemoved(int correctAnswer) {
        Random r = new Random();
        int toBeRemoved = 0;
        int index = r.nextInt(2);
        if (correctAnswer > index) {
            toBeRemoved = index;
        } else {
            toBeRemoved = index + 1;
        }
        return toBeRemoved;
    }

    /**
     * Play the {@link Joker#REMOVE_INCORRECT_ANSWER} joker. If successful, disable the joker button.
     */
    public void removeIncorrectAnswer() {
        Response response = serverUtils.playJoker(Joker.REMOVE_INCORRECT_ANSWER);

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            removeIncorrectAnswerJokerButton.setDisable(true);
        }
        removeAnswer();
    }

    /**
     * This method will be used to actually remove the answer in the scene.
     */
    public abstract void removeAnswer();

    /**
     * Play the {@link Joker#DOUBLE_POINTS} joker. If successful, disable the joker button.
     */
    public void playDoublePointsJoker() {
        Response response = serverUtils.playJoker(Joker.DOUBLE_POINTS);

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            doublePointsJokerButton.setDisable(true);
        }
    }

    /**
     * Set the jokers that are disabled. If a joker is in the set, it's button will be disabled. If it is not, it's
     * button will be enabled.
     *
     * @param disabledJokers The set of disabled jokers
     */
    public void setDisabledJokers(Set<Joker> disabledJokers) {
        reduceTimeJokerButton.setDisable(disabledJokers.contains(Joker.REDUCE_TIME));
        doublePointsJokerButton.setDisable(disabledJokers.contains(Joker.DOUBLE_POINTS));
        removeIncorrectAnswerJokerButton.setDisable(disabledJokers.contains(Joker.REMOVE_INCORRECT_ANSWER));
    }

    @Override
    public void onSceneShow(Scene oldScene, Scene newScene) {
        System.out.println("Registered");
        this.eventManager.getEventBus().register(this);
    }

    @Override
    public void onSceneHide(Scene oldScene, Scene newScene) {
        System.out.println("Unregistered");
        this.eventManager.getEventBus().unregister(this);
    }

    /**
     * Sets the label that shows on what question is the player now.
     *
     * @param currentQuestionIndex the new index
     */
    public void setQuestionIndexLabel(int currentQuestionIndex) {
        String text = currentQuestionIndex + " / 20";
        questionIndexLabel.setText(text);
    }

    /**
     * A getter to get the url of the server.
     * @return The string representation of the server url.
     */
    public String getServerUrl() {
        return this.serverUtils.getServerUrl();
    }

    /**
     * Function that center the image in the imageView, which makes it prettier.
     *
     * @param imageView a Image view that holds an image.
     */
    public void centerImage(ImageView imageView) {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double offset = 0;
            if (ratioX >= ratioY) {
                offset = ratioY;
            } else {
                offset = ratioX;
            }

            w = img.getWidth() * offset;
            h = img.getHeight() * offset;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);
        }
    }
}
