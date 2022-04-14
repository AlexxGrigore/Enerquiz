package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class TransitionSceneCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private ImageView gifImageView;


    /**
     * Constructor.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public TransitionSceneCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Method for setting Theme image.
     */
    public void setImages() {
        Image themeImage = new Image("@../../client/images/earthBurns.gif");
        gifImageView.setImage(themeImage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setImages();
    }
}
