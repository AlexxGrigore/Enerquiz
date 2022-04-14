package client.scenes;

import client.Main;
import client.controls.AbstractQuestionCtrl;
import client.event.EventManager;
import client.utils.ServerUtils;
import commons.Answer;
import commons.MultipleChoiceQuestion;
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
import java.util.Scanner;

public class MultipleChoiceQuestionCtrl extends AbstractQuestionCtrl implements Initializable {

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
    private Button helpButton;

    @FXML
    private Label questionText;

    @FXML
    private ImageView imageQuestion;

    @FXML
    private Pane helpPane;
    private HelpCtrl helpCtrl;
    private Scene help;

    private Button givenAnswerButton;
    private long givenAnswer;

    /**
     * Create a new MultipleChoiceQuestionCtrl.
     *
     * @param serverUtils  The ServerUtils instance
     * @param mainCtrl     The MainCtrl instance
     * @param eventManager The EventManager instance
     */
    @Inject
    public MultipleChoiceQuestionCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, EventManager eventManager) {
        super(serverUtils, mainCtrl, eventManager);
        this.correctAnswer = 0;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Pair<HelpCtrl, Parent> help = Main.getFXML().load(HelpCtrl.class,
                "client", "scenes", "Help.fxml");
        HelpCtrl helpController = help.getKey();
        Scene helpscene = new Scene(help.getValue());
        this.helpCtrl = helpController;
        this.help = helpscene;
        this.addHelp();
        helpCtrl.helpPane.setVisible(false);
        helpCtrl.setHelpLabels(HelpCtrl.HelpType.MULTIPLE_CHOICE);
        givenAnswer = -1;
    }

    /**
     * Method that adds the helpScene to the helpPane.
     */
    public void addHelp() {
        Parent helpParent = help.getRoot();
        helpPane.getChildren().addAll(helpParent);
    }

    /**
     * Shows the help window.
     */
    public void showHelp() {
        helpCtrl.helpPane.setVisible(true);
    }

    @Override
    public void showAnswers(List<Answer> submittedAnswers) {
        answerA.setDisable(true);
        answerB.setDisable(true);
        answerC.setDisable(true);

        resetAnswerAppearance();

        if (givenAnswer == correctAnswer) {
            givenAnswerButton.getStyleClass().add("correct_answer_button");
            return;
        }

        Button correctButton;
        if (correctAnswer == new Scanner(answerA.getText()).nextLong()) {
            correctButton = answerA;
        } else if (correctAnswer == new Scanner(answerA.getText()).nextLong()) {
            correctButton = answerB;
        } else {
            correctButton = answerC;
        }
        correctButton.getStyleClass().add("correct_answer_button");
        if (givenAnswer != -1) {
            givenAnswerButton.getStyleClass().add("wrong_answer_button");
        }
    }

    @Override
    public void removeAnswer() {
        int toBeRemoved = answerToRemoved((int) correctAnswer);
        switch (toBeRemoved) {
            case 0 -> answerA.setVisible(false);
            case 1 -> answerB.setVisible(false);
            case 2 -> answerC.setVisible(false);
            default -> throw new IndexOutOfBoundsException();
        }
    }

    /**
     * executed when the exit button is pressed.
     * back to select game mode
     */
    @Override
    public void exit() {
        super.exit();
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
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
        setImages(multipleChoiceQuestion);

        questionText.setText(question.getText());
        answerA.setText(multipleChoiceQuestion.getVariants()[0] + " Wh");
        answerB.setText(multipleChoiceQuestion.getVariants()[1] + " Wh");
        answerC.setText(multipleChoiceQuestion.getVariants()[2] + " Wh");
        this.correctAnswer = (long) multipleChoiceQuestion.getAnswer();
        givenAnswer = -1;
    }

    /**
     * Set Image for the question based on the image path of the activity where the question are made from.
     * If no image can be found with the given image path, default image will be supllied.
     *
     * @param multipleChoiceQuestion The multipleChoice question
     */
    public void setImages(MultipleChoiceQuestion multipleChoiceQuestion) {
        Image questionImage = new Image(getServerUrl() + multipleChoiceQuestion.getImagePath());
        if (questionImage.isError()) {
            questionImage = new Image("@../../client/images/themePic.png");
        }
        imageQuestion.setImage(questionImage);
        centerImage(imageQuestion);
    }

    /**
     * Submits the answerA.
     */
    public void submitAnswerActionA() {
        submitAnswer(new Scanner(answerA.getText()).nextLong());
        resetAnswerAppearance();
        answerA.getStyleClass().add("pressed_answer_button");
        givenAnswerButton = answerA;
        givenAnswer = new Scanner(givenAnswerButton.getText()).nextLong();
    }

    /**
     * Submit answer B.
     */
    public void submitAnswerActionB() {
        submitAnswer(new Scanner(answerB.getText()).nextLong());
        resetAnswerAppearance();
        answerB.getStyleClass().add("pressed_answer_button");
        givenAnswerButton = answerB;
        givenAnswer = new Scanner(givenAnswerButton.getText()).nextLong();

    }

    /**
     * Submit answer C.
     */
    public void submitAnswerActionC() {
        submitAnswer(new Scanner(answerC.getText()).nextLong());
        resetAnswerAppearance();
        answerC.getStyleClass().add("pressed_answer_button");
        givenAnswerButton = answerC;
        givenAnswer = new Scanner(givenAnswerButton.getText()).nextLong();

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
