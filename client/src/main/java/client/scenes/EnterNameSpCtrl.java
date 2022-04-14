package client.scenes;

import client.utils.ServerUtils;
import commons.IngamePlayer;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.util.Duration;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterNameSpCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String nameEntered;

    @FXML
    private TextField name;

    @FXML
    private Button startGameButton;

    @FXML
    private Label errorText;

    @FXML
    private ImageView themeImageView;

    @FXML
    private Button backButton;

    private int expireTime = 2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setImages();
    }
    /**
     * Constructor.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EnterNameSpCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.nameEntered = null;
    }

    /**
     * Method for setting Theme image.
     */
    public void setImages() {
        Image themeImage = new Image("@../../client/images/themePic.png");
        themeImageView.setImage(themeImage);
        Image backImage = new Image("@../../client/images/back.png");
        ImageView imageView = new ImageView(backImage);
        imageView.setFitHeight(75);
        imageView.setPreserveRatio(true);
        backButton.setGraphic(imageView);
    }

    /**
     * This method shows errorText for 'expireTime' seconds.
     *
     * @param textError
     */
    public void showError(String textError) {
        errorText.setText(textError);
        errorText.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(expireTime));
        visiblePause.setOnFinished(event -> errorText.setVisible(false));
        visiblePause.play();
    }

    /**
     * Method directly starting the game for single player.
     * The function above is the old version of this function, but it was sending you directly to the queue.
     * I don't think we need that for the single player mode.
     */
    public void startGame() { //This method is not ready because I have to see how backend will turn out.
        try {
            nameEntered = name.getText();
            if (nameEntered.length() == 0 /*|| is taken*/) {
                showError("Please enter a name!");
            } else {
                mainCtrl.updateMemorizedName(nameEntered);
                server.setName(nameEntered);
                server.startSingle();
                mainCtrl.showIngame();
                mainCtrl.getIngameCtrl().addPlayer(new IngamePlayer(nameEntered));
                // sp game starts
            }
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clear();
    }

    /**
     * Method for going back to previous scene.
     */
    public void back() {
        try {
            mainCtrl.showGamemode();
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    /**
     * Set the text field with memorized name.
     * @param name The name that will be set to the text field.
     */
    public void setTextFieldName(String name) {
        this.name.setText(name);
    }

    private void clear() {
        name.clear();
    }
}
