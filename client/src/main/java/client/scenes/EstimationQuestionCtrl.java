package client.scenes;

import client.Main;
import client.controls.AbstractQuestionCtrl;
import client.event.EventManager;
import client.utils.ServerUtils;
import commons.Answer;
import commons.EstimationQuestion;
import commons.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EstimationQuestionCtrl extends AbstractQuestionCtrl implements Initializable {


    @FXML
    private Label questionText;

    @FXML
    private Label answerLabel;

    @FXML
    private TextField answerText;

    @FXML
    private Button submitAnswerButton;

    @FXML
    private Pane helpPane;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView imageQuestion;


    private HelpCtrl helpCtrl;
    private Scene help;

    /**
     * Create a new EstimationQuestionCtrl.
     *
     * @param serverUtils  The ServerUtils instance
     * @param mainCtrl     The MainCtrl instance
     * @param eventManager The EventManager instance
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, EventManager eventManager) {
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
        helpCtrl.setHelpLabels(HelpCtrl.HelpType.ESTIMATION);
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
        EstimationQuestion estimationQuestion = (EstimationQuestion) question;
        setImages(estimationQuestion);
        questionText.setText(estimationQuestion.getText());
        this.correctAnswer = (long) estimationQuestion.getAnswer();

    }

    /**
     * Set Image for the question based on the image path of the activity where the question are made from.
     * If no image can be found with the given image path, default image will be supllied.
     *
     * @param estimationQuestion The estimationQuestion
     */
    public void setImages(EstimationQuestion estimationQuestion) {
        Image questionImage = new Image(getServerUrl() + estimationQuestion.getImagePath());
        if (questionImage.isError()) {
            questionImage = new Image("@../../client/images/themePic.png");
        }
        this.imageQuestion.setImage(questionImage);
        centerImage(imageQuestion);
    }

    @Override
    public void removeAnswer() {

    }

    /**
     * Submits the answer.
     */
    public void submitAnswerAction() throws NumberFormatException {
        try {
            answerLabel.setText("");
            submitAnswer(Long.parseLong(answerText.getText()));
        } catch (NumberFormatException e) {
            answerLabel.setText("Please enter a number!");
            answerLabel.setStyle("-fx-text-fill: RED");
        }

        // Show the answer has been submitted, while showing the user can submit more answer. The previous answer will
        // be muted.
        answerText.setPromptText(answerText.getText());
        answerText.clear();
    }

    @Override
    public void showAnswers(List<Answer> submittedAnswers) {
//        Answer answer = submittedAnswers.get(0);
//        answer.getPlayer();
        answerLabel.setText("The correct answer is: " + String.valueOf(correctAnswer));
        answerLabel.setStyle("-fx-text-fill: GREEN");

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
}
