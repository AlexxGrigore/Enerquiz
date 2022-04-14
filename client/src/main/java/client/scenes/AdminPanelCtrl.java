package client.scenes;

import client.utils.ServerUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminPanelCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @FXML
    private Button editActivities;

    @FXML
    private Button editQuestionTypes;

    @FXML
    private ImageView activitiesImageView;

    @FXML
    private ImageView questionTypeImageView;

    @FXML
    private Button backButton;

    /**
     * Create a new AdminPanelCtrl.
     *
     * @param server The ServerUtil instance to connect to the backend actions.
     * @param mainCtrl The Mainctrl to control the scenes.
     */
    @Inject
    public AdminPanelCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setImages();
    }

    /**
     * Set the images and icon for the scene.
     */
    public void setImages() {
        Image activitiesImage = new Image("@../../client/images/Edit_Activities.png");
        Image backImage = new Image("@../../client/images/back.png");
        Image questionTypeImage = new Image("@../../client/images/Edit_Question_types.png");

        ImageView backImageView = new ImageView(backImage);
        activitiesImageView.setImage(activitiesImage);
        questionTypeImageView.setImage(questionTypeImage);

        backImageView.setFitHeight(75);
        backImageView.setPreserveRatio(true);
        backButton.setGraphic(backImageView);
    }

    /**
     * Method for going back to previous scene.
     */
    public void back() {
            mainCtrl.showGamemode();
    }

    /**
     * Show the scene [ Admin Panel for the activities ].
     */
    public void goAdminActivities() {
        mainCtrl.showAdminActivities();
    }

    /**
     * Show the scene [ Admin Panel for the question types ].
     */
    public void goAdminQuestionTypes() {
        mainCtrl.showEditQuestionTypes();
    }

}
