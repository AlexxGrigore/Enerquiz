package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class DeleteActivityCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<Activity> data;

    @FXML
    private Label activityID;

    @FXML
    private TextField idInput;

    @FXML
    private Button searchButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorText;

    @FXML
    private Label successText;

    @FXML
    private TableView<Activity> table;

    @FXML
    private TableColumn<Activity, Long> colID;

    @FXML
    private TableColumn<Activity, String> colActivityText;

    @FXML
    private TableColumn<Activity, Long> colAmountWh;

    @FXML
    private TableColumn<Activity, String> colImagePath;

    /**
     * Create a new DeleteActivityCtrl.
     *
     * @param server The ServerUtil instance to connect to the backend actions.
     * @param mainCtrl The Mainctrl to control the scenes.
     */
    @Inject
    public DeleteActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colID.setCellValueFactory(ac -> new SimpleLongProperty(ac.getValue().getId()).asObject());
        colActivityText.setCellValueFactory(ac -> new SimpleStringProperty(ac.getValue().getActivityQuestion()));
        colAmountWh.setCellValueFactory(ac ->  new SimpleLongProperty(ac.getValue().getAmountWh()).asObject());
        colImagePath.setCellValueFactory(ac -> new SimpleStringProperty(ac.getValue().getImagePath()));
    }

    /**
     * This method show the activity visually to the client, based on the id they input.
     * If there is no activity found based on the given id? Empty table is shown.
     * This way, the client can see which activity it is.
     * So, they can decide if they really want to delete it.
     */
    public void showActivity() {
        hideMessages();
        long id = idCheck(idInput.getText());
        if (id == -1) {
            return;
        }
        Activity activity = server.getActivityById(id);
        if (activity == null) {
            this.errorText.setText("No activity found, based on the given id");
            this.errorText.setVisible(true);
            return;
        }
        ArrayList<Activity> containter = new ArrayList<>();
        containter.add(activity);
        data = FXCollections.observableList(containter);
        table.setItems(data);
    }

    /**
     * Delete the activity based on the given id.
     * If no activity is found in the database based on the given id. Errortext is shown.
     * If the deletion is done successfully, Successtext is shown.
     */
    public void delete() {
        hideMessages();
        table.setItems(null);
        long id = idCheck(idInput.getText());
        if (id == -1) {
            return;
        }

        Activity activity = server.deleteActivityById(id);
        if (activity == null) {
            this.errorText.setText("No activity found, based on the given id");
            this.errorText.setVisible(true);
        } else {
            this.successText.setVisible(true);
        }
    }

    /**
     * Close the pop-up window.
     * Clear the messages and inputs, so when the pop-up is reopened it is clean.
     */
    public void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        clearFields();
        hideMessages();
        ArrayList<Activity> containter = new ArrayList<>();
        data = FXCollections.observableList(containter);
        table.setItems(data);
        stage.close();
    }

    /**
     * Clear the input fields.
     */
    public void clearFields() {
        this.idInput.clear();
    }


    /**
     * As a user, move mouse if very energy consuming.
     * So I make this scene responsive when particular key's are press.
     * Enter - add the message, based on the field inputs.
     * ESCAPE - close the window.
     * @param e a Key that is pressed by the user.
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER -> delete();
            case ESCAPE -> cancel();
            default -> {
                break;
            }
        }
    }

    /**
     * Make the Errortext and the Successtext invisible.
     */
    public void hideMessages() {
        this.errorText.setVisible(false);
        this.successText.setVisible(false);
    }

    /**
     * Check if the input is actually an valid number.
     * @param idString The input string.
     * @return The number format of the input string, OR -1 if the input is invalid.
     */
    public long idCheck(String idString) {
        try {
            long id = Long.parseLong(idString);
            if (id < 1) {
                throw new IllegalArgumentException();
            }
            return id;
        } catch (NumberFormatException e) {
            this.errorText.setText("Invalid ID");
            this.errorText.setVisible(true);
            return -1;
        } catch (IllegalArgumentException e) {
            this.errorText.setText("This ID is not in the database");
            this.errorText.setVisible(true);
            return -1;
        }
    }
}

