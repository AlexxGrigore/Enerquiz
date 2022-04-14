package client.scenes;

import client.Main;
import client.controls.AbstractQuestionCtrl;
import client.event.EventManager;
import client.utils.ServerUtils;
import commons.Answer;
import commons.MultipleActivitiesQuestion;
import commons.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MultipleActivitiesQuestionCtrl extends AbstractQuestionCtrl implements Initializable {

    private long correctAnswer;

    @FXML
    private Button exitButton;

    @FXML
    private Button answerA;

    @FXML
    private Button answerB;

    @FXML
    private Button answerC;

    @FXML
    private ImageView imageA;

    @FXML
    private ImageView imageB;

    @FXML
    private ImageView imageC;

    @FXML
    private ImageView questionImage;

    @FXML
    private Button helpButton;

    @FXML
    private Label question;

    // <-------------- Help Menu ----------> //
    @FXML
    private Pane helpPane;

    private HelpCtrl helpCtrl;
    private Scene help;
    private long givenAnswer;
    private Button givenAnswerButton;
// <-------------- Help Menu ----------> //

    /**
     * Create a new MultipleActivitiesQuestionCtrl.
     *
     * @param serverUtils  The Server instance
     * @param mainCtrl     The MainCtrl instance
     * @param eventManager The EventManager instance
     */
    @Inject
    public MultipleActivitiesQuestionCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, EventManager eventManager) {
        super(serverUtils, mainCtrl, eventManager);
        this.correctAnswer = 0;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Pair<HelpCtrl, Parent> help = Main.getFXML().load(HelpCtrl.class, "client", "scenes", "Help.fxml");
        HelpCtrl helpController = help.getKey();
        Scene helpScene = new Scene(help.getValue());
        this.helpCtrl = helpController;
        this.help = helpScene;
        this.addHelp();
        helpCtrl.helpPane.setVisible(false);
        helpCtrl.setHelpLabels(HelpCtrl.HelpType.MULTIPLE_ACTIVITY);
        givenAnswer = -1;
    }

    /**
     * Method that adds helpScene to helpPane.
     */
    public void addHelp() {
        Parent helpParent = help.getRoot();
        helpPane.getChildren().add(helpParent);
    }

    /**
     * Shows help window.
     */
    public void showHelp() {
        helpCtrl.helpPane.setVisible(true);
    }

    @Override
    public void removeAnswer() {
        int toBeRemoved = answerToRemoved((int) correctAnswer);
        switch (toBeRemoved) {
            case 0:
                answerA.setVisible(false);
                imageA.setVisible(false);
                break;
            case 1:
                answerB.setVisible(false);
                imageB.setVisible(false);
                break;
            case 2:
                answerC.setVisible(false);
                imageC.setVisible(false);
                break;
            default: throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Executed when the exit button is pressed.
     * back to select game mode
     */
    @Override
    public void exit() {
        super.exit();
    }

    /**
     * Show the correct answer and answers submitted by players.
     *
     * @param submittedAnswers A map of players to their answers
     */
    @Override
    public void showAnswers(List<Answer> submittedAnswers) {
        answerA.setDisable(true);
        answerB.setDisable(true);
        answerC.setDisable(true);
        resetAnswerAppearance();

        ///TODO: We need to set a value for the correct answer. At the moment, it is null.
        if (givenAnswer == -1 || givenAnswer == correctAnswer) {
            switch ((int) correctAnswer) {
                case 0 -> {
                    answerA.getStyleClass().add("correct_answer_button");
                }
                case 1 -> {
                    answerB.getStyleClass().add("correct_answer_button");
                }
                case 2 -> {
                    answerC.getStyleClass().add("correct_answer_button");
                }
                default -> System.out.println("There is an error with the showAnswer() function");
            }
            return;
        }

        Button correctButton = switch ((int) correctAnswer) {
            case 0 -> answerA;
            case 1 -> answerB;
            default -> answerC;
        };
        correctButton.getStyleClass().add("correct_answer_button");
        if (givenAnswerButton != null) {
            givenAnswerButton.getStyleClass().add("wrong_answer_button");
        }

    }

    /**
     * Displays the question.
     * Puts the right answer randomly to one of the buttons.
     * Set the image(s) of the question, if no image is found via the given url.
     * Then a default image will be supplied.
     *
     * @param question a question
     */
    @Override
    public void setQuestion(Question question) {
        MultipleActivitiesQuestion multipleActivitiesQuestion = (MultipleActivitiesQuestion) question;
        setImages(multipleActivitiesQuestion);

        answerA.setText(multipleActivitiesQuestion.getVariants().get(0).getActivityQuestion());
        answerB.setText(multipleActivitiesQuestion.getVariants().get(1).getActivityQuestion());
        answerC.setText(multipleActivitiesQuestion.getVariants().get(2).getActivityQuestion());

        this.question.setText(multipleActivitiesQuestion.getText());

        givenAnswer = -1;

        this.correctAnswer = multipleActivitiesQuestion.getAnswer();

        System.out.println("---------->CORRECT ANSWER: " + this.correctAnswer);
    }

    /**
     * Set Image for the question based on the image path of the activity where the question are made from.
     * If no image can be found with the given image path, default image will be supllied.
     *
     * @param multipleActivitiesQuestion The multiple activity question.
     */
    public void setImages(MultipleActivitiesQuestion multipleActivitiesQuestion) {
        Image constantImage = new Image("@../../client/images/multiple_activities_question.png");
        questionImage.setImage(constantImage);

        Image imageForA = new Image(getServerUrl() + multipleActivitiesQuestion.getVariants().get(0).getImagePath());
        Image imageForB = new Image(getServerUrl() + multipleActivitiesQuestion.getVariants().get(1).getImagePath());
        Image imageForC = new Image(getServerUrl() + multipleActivitiesQuestion.getVariants().get(2).getImagePath());
        if (imageForA.isError()) {
            imageForA = new Image("@../../client/images/themePic.png");
        } else if (imageForB.isError()) {
            imageForB = new Image("@../../client/images/themePic.png");
        } else if (imageForC.isError()) {
            imageForC = new Image("@../../client/images/themePic.png");
        }

        imageA.setImage(imageForA);
        imageB.setImage(imageForB);
        imageC.setImage(imageForC);

        centerImage(imageA);
        centerImage(imageB);
        centerImage(imageC);
        centerImage(questionImage);
    }

    /**
     * Submits the answer A.
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

    /**
     * Submits the answerA.
>>>>>>> develop
     */
    public void submitAnswerActionA() {
        submitAnswer(0);
        resetAnswerAppearance();
        answerA.getStyleClass().add("pressed_answer_button");
        givenAnswerButton = answerA;
        givenAnswer = 0;
    }

    /**
     * Submit answer B.
     */
    public void submitAnswerActionB() {
        submitAnswer(1);
        resetAnswerAppearance();

        answerB.getStyleClass().add("pressed_answer_button");
        givenAnswerButton = answerB;
        givenAnswer = 1;

    }

    /**
     * Submit answer C.
     */
    public void submitAnswerActionC() {
        submitAnswer(2);
        resetAnswerAppearance();
        answerC.getStyleClass().add("pressed_answer_button");
        givenAnswerButton = answerC;
        givenAnswer = 2;

    }

    /**
     * Reset the button style to the default one.
     */
    public void resetAnswerAppearance() {
        answerA.getStyleClass().remove("pressed_answer_button");
        answerA.getStyleClass().add("button_answer");

        answerB.getStyleClass().remove("pressed_answer_button");
        answerB.getStyleClass().add("button_answer");

        answerC.getStyleClass().remove("pressed_answer_button");
        answerC.getStyleClass().add("button_answer");


    }

}
