package client.scenes;

import client.hooks.SceneLifecycle;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class EditQuestionTypesCtrl implements Initializable, SceneLifecycle {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private boolean[] questionTypes;

    @FXML
    private CheckBox estimationCheck;

    @FXML
    private CheckBox multipleChoiceCheck;

    @FXML
    private CheckBox multipleActivityCheck;

    @FXML
    private ImageView esImageView;

    @FXML
    private ImageView maImageView;

    @FXML
    private ImageView mcImageView;

    @FXML
    private Button applyButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageText;

    /**
     * Create a new EditQuestionTypesCtrl.
     *
     * @param server The ServerUtil instance to connect to the backend actions.
     * @param mainCtrl The Mainctrl to control the scenes.
     */
    @Inject
    public EditQuestionTypesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.questionTypes = new boolean[3];
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image backImage = new Image("@../../client/images/back.png");
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitHeight(75);
        backImageView.setPreserveRatio(true);
        backButton.setGraphic(backImageView);
        backButton.setText("");
        setImages();
    }

    /**
     * This method keep the checkboxes's status synchronized with the server.
     */
    public void setChecked() {
        boolean[] types = this.server.getTypes();
        estimationCheck.setSelected(types[0]);
        multipleActivityCheck.setSelected(types[1]);
        multipleChoiceCheck.setSelected(types[2]);
        setImages();
    }


    /**
     * This method does the job to switch the images depends on the checkbox status.
     * If the check box of one question type is checked, image of that question type is with color.
     * Otherwise, the black and white image is shown.
     */
    public void setImages() {
        Image es;
        Image ma;
        Image mc;
        if (estimationCheck.isSelected()) {
            es = new Image("@../../client/images/admin_types/ES_Color.png");
        } else {
            es = new Image("@../../client/images/admin_types/ES_noColor.png");
        }
        if (multipleActivityCheck.isSelected()) {
            ma = new Image("@../../client/images/admin_types/MA_Color.png");
        } else {
            ma = new Image("@../../client/images/admin_types/MA_noColor.png");
        }
        if (multipleChoiceCheck.isSelected()) {
            mc = new Image("@../../client/images/admin_types/MC_Color.png");
        } else {
            mc = new Image("@../../client/images/admin_types/MC_noColor.png");
        }
        esImageView.setImage(es);
        maImageView.setImage(ma);
        mcImageView.setImage(mc);
    }

    /**
     * This method takes the method that user's choice and send it to the server.
     * To prevent the doom of the server, the client side also does the check if no
     * question type is chosen. In that case, the error message is set to visible to
     * notify the user.
     */
    public void takeTypes() {
        this.messageText.setVisible(false);
        questionTypes[0] = estimationCheck.isSelected();
        questionTypes[1] = multipleActivityCheck.isSelected();
        questionTypes[2] = multipleChoiceCheck.isSelected();

        if (!(questionTypes[0] | questionTypes[1] | questionTypes[2])) {
            this.messageText.setText("You have to at least enable ONE question type.");
            this.messageText.setVisible(true);
            this.messageText.setStyle("-fx-text-fill: red");
            return;
        }

        this.messageText.setText("Changes applied successfully.");
        this.messageText.setStyle("-fx-text-fill: rgb(0, 216, 0);");
        this.messageText.setVisible(true);
        server.updateTypes(questionTypes);
    }

    /**
     * Goes back to the scene [ Admin Panel ].
     */
    public void back() {
        this.messageText.setVisible(false);
        mainCtrl.showAdminPanel();
    }

    @Override
    public void onSceneShow(Scene oldScene, Scene newScene) {
        setChecked();
    }

    @Override
    public void onSceneHide(Scene oldScene, Scene newScene) {}
}
