package client.scenes;

import client.utils.ServerUtils;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class GamemodeChooseCtrl implements Initializable {


    //    this is something backend related
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button singleplayerButton;

    @FXML
    private Button multiplayerButton;

    @FXML
    private ImageView spImage;

    @FXML
    private ImageView mpImage;

    @FXML
    private Button leaderboardButton;

    @FXML
    private Button backButton;

    @FXML
    private Button adminButton;

    /**
     * Constructor.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public GamemodeChooseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setImages();
    }

    /**
     * Method for setting Theme image.
     */
    public void setImages() {
        Image singlePlayerImage = new Image("@../../client/images/single_back.png");
        spImage.setPreserveRatio(true);
        spImage.setImage(singlePlayerImage);
        Image multiPlayerImage = new Image("@../../client/images/multiplayer.png");
        mpImage.setPreserveRatio(true);
        mpImage.setImage(multiPlayerImage);
        Image backImage = new Image("@../../client/images/back.png");
        ImageView imageView = new ImageView(backImage);
        imageView.setFitHeight(75);
        imageView.setPreserveRatio(true);
        backButton.setGraphic(imageView);

        Image adminImage = new Image("@../../client/images/admin_logo.png");
        ImageView adminImageView = new ImageView(adminImage);
        adminImageView.setFitHeight(110);
        adminImageView.setFitWidth(90);
        adminButton.setGraphic(adminImageView);
        adminButton.setBackground(null);
    }
    /**
     * This method will be for going to "Leaderboard scene" oce that scene is made.
     */
    public void goToLeaderboard() {
        mainCtrl.showGlobalLeaderBoard();
        System.out.println("Go to leaderboard");
    }

    /**
     * Method for going back to previous scene.
     */
    public void back() {
        try {
            mainCtrl.showEnterURL();
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    /**
     * This method is for going to "EnterNameSinglePlayer" scene on the press of Single-player button.
     */
    public void spChosen() {
        try {
            mainCtrl.showEnterNameSp();
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    /**
     * This method is for going to "EnterNameSinglePlayer" scene on the press of Single-player button.
     */
    public void mpChosen() {
        try {
            mainCtrl.showEnterNameMp();
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    /**
     * This method is for going to "Admin Panel" scene on the press of Admin logo.
     */
    public void goToAdminPanel() {
        try {
            mainCtrl.showAdminPanel();
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }
}
