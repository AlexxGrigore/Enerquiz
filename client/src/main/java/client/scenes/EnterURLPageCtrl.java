package client.scenes;

import client.utils.ServerUtils;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.util.Duration;

import javax.inject.Inject;
import javafx.scene.image.ImageView;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterURLPageCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String url;
    private final int expireTime = 2;
    @FXML
    private TextField inputURL;

    @FXML
    private Button submitURLButton;

    @FXML
    private TextField errorText;

    @FXML
    private ImageView themeImageView;

    /**
     * Constructor.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EnterURLPageCtrl(ServerUtils server, MainCtrl mainCtrl) throws FileNotFoundException {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.url = null;
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
        Image themeImage = new Image("@../../client/images/themePic.png");
        themeImageView.setImage(themeImage);
    }
    /**
     * Method for joining the server.
     */
    public void join() {
        try {
            url = inputURL.getText();

                    System.out.println("Now is the time when I get the URL and connect to server");
                    server.connect(url);
                    mainCtrl.showGamemode();


        } catch (WebApplicationException e) {
            webApplicationExceptionAlert(e);
            return;
        } finally {
            wrongURL();
            clear();
        }

    }

    private void clear() {
        inputURL.clear();
    }

    /**
     * This method shows errorText for 'expireTime' seconds.
     */
    public void showError() {
        errorText.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(expireTime));
        visiblePause.setOnFinished(event -> errorText.setVisible(false));
        visiblePause.play();
    }

    /**
     * Method when the URL is wrong.
     */
    public void wrongURL() {
        errorText.setText("Enter valid URL");
        showError();
    }

    /**
     * Method when the URL isn't added.
     */
    public void noURL() {
        errorText.setText("You need to enter URL first.");
        showError();
    }

    /**
     * WebApplicationException alert.
     * @param e
     */
    public void webApplicationExceptionAlert(WebApplicationException e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
