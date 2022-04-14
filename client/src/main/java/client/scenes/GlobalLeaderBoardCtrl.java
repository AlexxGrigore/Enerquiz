package client.scenes;

import client.utils.ServerUtils;
import commons.GlobalLeaderBoardEntry;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class GlobalLeaderBoardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button backButton;


    @FXML
    private ImageView singleFirstMedal;

    @FXML
    private GridPane multiPlayerBoard;

    @FXML
    private ImageView singleSecondMedal;

    @FXML
    private ImageView singleThirdMedal;

    @FXML
    private ImageView multiFirstMedal;

    @FXML
    private ImageView multiSecondMedal;

    @FXML
    private ImageView multiThirdMedal;

    @FXML
    private GridPane singlePlayerBoard;

    /**
     *
     * Constructor.
     * @param server
     * @param mainCtrl
     */

    @Inject
    public GlobalLeaderBoardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setImages();
    }

    /**
     * sets the images in the scene.
     *
     */
    public void setImages() {
        Image gold = new Image("@../../client/images/gold medal.png");
        Image silver = new Image("@../../client/images/silver medal.png");
        Image bronze = new Image("@../../client/images/bronze.png");
        Image backImage = new Image("@../../client/images/back.png");
        multiFirstMedal.setImage(gold);
        multiSecondMedal.setImage(silver);
        multiThirdMedal.setImage(bronze);
        singleFirstMedal.setImage(gold);
        singleSecondMedal.setImage(silver);
        singleThirdMedal.setImage(bronze);
        ImageView imageView = new ImageView(backImage);
        imageView.setFitHeight(75);
        imageView.setPreserveRatio(true);
        backButton.setGraphic(imageView);
    }

    /**
     * executed when the back button is pressed.
     * takes one back to choose gamemode
     */
    public void back() {
        //backend related stuff

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
     * sets the leaderboard, the database for this does not exist yet.
     *
     */
    public void setLeaderboard() {
        List<GlobalLeaderBoardEntry> singlePlayer = server.getGlobalSinglePlayerLeaderBoard();
        List<GlobalLeaderBoardEntry> multiPlayer = server.getGlobalMultiPlayerLeaderBoard();

        for (int i = 0; i < singlePlayer.size(); i++) {
            if (singlePlayer.get(i).playerName != null) {
                ((Label) singlePlayerBoard.getChildren().get(5 + i)).setText("  " + singlePlayer.get(i).playerName);
                ((Label) singlePlayerBoard.getChildren().get(10 + i)).setText(
                        String.valueOf(singlePlayer.get(i).playerScore) + " pts");
            }
        }
        for (int i = 0; i < multiPlayer.size(); i++) {
            if (multiPlayer.get(i).playerName != null) {
                ((Label) multiPlayerBoard.getChildren().get(5 + i)).setText(
                        "  " + multiPlayer.get(i).playerName);
                ((Label) multiPlayerBoard.getChildren().get(10 + i)).setText(
                        String.valueOf(multiPlayer.get(i).playerScore) + " pts");
            }
        }







    }



}
