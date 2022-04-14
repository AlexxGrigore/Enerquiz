package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelpCtrl implements Initializable {

    @FXML
    private Button closeHelpButton;

    @FXML
    private GridPane gridPaneHelp;

    @FXML
    public Pane helpPane;

    /**
     * Constructor.
     */
    public HelpCtrl() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // To see the labels, call the function setHelpLabels(whatType) with the type you want.
//        setHelpLabels();
        setImages();
    }

    /**
     * Making labels for help window and styling them.
     *
     * @param text Text that will be written on the label.
     * @return label
     */
    public Label makeHelpLabels(String text) {
        Label label = new Label("X");
        label.setTextFill(Color.BLACK);
        label.setPrefWidth(520);
        label.setWrapText(true);
        label.setPrefHeight(65);
        label.setFont(new Font("Verdana", 16));
        label.setText(text);
        label.setPadding(new Insets(0, 15, 0, 10));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /**
     * Makes a new Background.
     * This method is here to make code a bit smaller.
     *
     * @param red
     * @param blue
     * @param green
     * @return Background.
     */
    public Background makeBackground(int red, int blue, int green) {
        return new Background(new BackgroundFill(Color.rgb(red, blue, green, 1),
                new CornerRadii(0), new Insets(0)));
    }

    /**
     * Closes Pane p.
     */
    public void closeHelp() {
        helpPane.setVisible(false);
    }

    /**
     * Setting labels into GridPane.
     * example:  helpCtrl.setHelpLabels(HelpCtrl.HelpType.ESTIMATION);
     *
     * @param helpType the type of labels you need
     */
    public void setHelpLabels(HelpType helpType) {
        int i = 0;
        ArrayList<Label> labels = new ArrayList<>();
        switch (helpType) {
            case QUEUE:
                labels = makeQueueLabels();
                break;
            case ESTIMATION:
                labels = makeEstimationQuestionLabels();
                break;
            case MULTIPLE_CHOICE:
                labels = makeMCQuestionLabels();
                break;
            case MULTIPLE_ACTIVITY:
                labels = makeMAQuestionLabels();
                break;
            default:
                throw new IllegalArgumentException();
        }

        Background backgroundBlue = makeBackground(42, 42, 42);
        Background backgroundWhite = makeBackground(100, 100, 100);
        for (Label label : labels) {
            label.setStyle("-fx-text-fill: WHITE");
            if (i % 2 == 0) {
                label.setBackground(backgroundBlue);
            } else {
                label.setBackground(backgroundWhite);
            }
            gridPaneHelp.add(labels.get(i), 0, i);
            i++;
        }
    }

    /**
     * Creates a list of labels mean for the queue scene.
     *
     * @return the list of labels
     */
    public ArrayList<Label> makeQueueLabels() {
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(makeHelpLabels("Any of the players that wait in the queue are able to initiate a game"));
        labels.add(makeHelpLabels("There will be 20 questions"));
        labels.add(makeHelpLabels("You have 20 seconds to respond to each question"));
        labels.add(makeHelpLabels("You will be able to use each joker only once per game"));
        labels.add(makeHelpLabels("No Google :)"));
        return labels;
    }

    /**
     * Creates a list of labels mean for the estimation question scene.
     *
     * @return the list of labels
     */
    public ArrayList<Label> makeEstimationQuestionLabels() {
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(makeHelpLabels("Estimate how much energy is consumed for the given activity"));
        labels.add(makeHelpLabels("You will get partial points, depending on how good is your answer"));
        labels.add(makeHelpLabels("1st Joker -> -10 seconds of everyone's time"));
        labels.add(makeHelpLabels("2nd Joker -> doubles the points for this question"));
        labels.add(makeHelpLabels("3rd Joker -> give a hint for the answer"));
        labels.add(makeHelpLabels("You can use each joker only once per game"));
        labels.add(makeHelpLabels("No Google :)"));
        return labels;
    }

    /**
     * Creates a list of labels meant for the multiple choice question scene.
     *
     * @return the list of labels
     */
    public ArrayList<Label> makeMCQuestionLabels() {
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(makeHelpLabels("Chose the answer that you think is correct"));
        labels.add(makeHelpLabels("1st Joker -> -10 seconds of everyone's time"));
        labels.add(makeHelpLabels("2nd Joker -> doubles the points for this question"));
        labels.add(makeHelpLabels("3rd Joker -> remove one incorrect answer"));
        labels.add(makeHelpLabels("You can use each joker only once per game"));
        labels.add(makeHelpLabels("No partial points"));
        labels.add(makeHelpLabels("No Google :)"));
        return labels;
    }

    /**
     * Creates a list of labels meant for the multiple activities question scene.
     *
     * @return the list of labels
     */
    public ArrayList<Label> makeMAQuestionLabels() {
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(makeHelpLabels("Chose the activity that you think is the right one"));
        labels.add(makeHelpLabels("1st Joker -> -10 seconds of everyone's time"));
        labels.add(makeHelpLabels("2nd Joker -> doubles the points for this question"));
        labels.add(makeHelpLabels("3rd Joker -> remove one incorrect answer"));
        labels.add(makeHelpLabels("You can use each joker only once per game"));
        labels.add(makeHelpLabels("No partial points"));
        labels.add(makeHelpLabels("No Google :)"));
        return labels;
    }

    /**
     * Method for setting images everywhere.
     * It's called when the scene is shown so that images are there at the start.
     */
    public void setImages() {
        Image helpExitImage = new Image("@../../client/images/x-icon.png");
        ImageView helpExitImageView = new ImageView(helpExitImage);
        helpExitImageView.setFitHeight(30);
        helpExitImageView.setFitWidth(30);
        helpExitImageView.setPreserveRatio(true);
        closeHelpButton.setGraphic(helpExitImageView);
    }


    public enum HelpType {
        QUEUE,
        ESTIMATION,
        MULTIPLE_CHOICE,
        MULTIPLE_ACTIVITY
    }

}
