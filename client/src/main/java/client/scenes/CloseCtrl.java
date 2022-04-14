package client.scenes;

import client.utils.ServerUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.inject.Inject;

public class CloseCtrl {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @FXML
    private Button okButton;

    @FXML
    private Button noButton;

    /**
     * Create a new AdminPanelCtrl.
     *
     * @param server The ServerUtil instance to connect to the backend actions.
     * @param mainCtrl The Mainctrl to control the scenes.
     */
    @Inject
    public CloseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Closes all windows.
     *
     * @param actionEvent confirm event.
     */
    public void closeWindow(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * Get back to game.
     *
     * @param actionEvent confirm event.
     */
    public void comeBack(ActionEvent actionEvent) {
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }
}
