package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.ResourceBundle;

public class AddActivityCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField activityTextInput;

    @FXML
    private TextField whInput;

    @FXML
    private TextField imagePathInpyt;

    @FXML
    private Label errorText;

    @FXML
    private Label successText;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    /**
     * Create a new AddActivityCtrl.
     *
     * @param server The ServerUtil instance to connect to the backend actions.
     * @param mainCtrl The Mainctrl to control the scenes.
     */
    @Inject
    public AddActivityCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.successText.setVisible(false);
        this.errorText.setVisible(false);
    }

    /**
     * Add an activity to the database, if its field are valid.
     */
    public void add() {
        Activity activity = getInputActivity();

        if (!activityCheck(activity)) {
            return;
        }
        server.addActivity(activity);
        clearFields();
    }

    /**
     * Close the pop-up window.
     * Clear the messages and inputs, so when the pop-up is reopened it is clean.
     */
    public void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        clearFields();
        hideMessages();
        stage.close();
    }

    /**
     * Get the field value, from the inputs field.
     * Checks the field value's before pass this to the add() method.
     * If any field value is invalid, an error message is shown and NULL is returned.
     * If the fields are valid, show the successful message and an Activity Object is returned.
     * @return NULL or an Activity Object.
     */
    public Activity getInputActivity() {
        boolean flag = false;
        try {
            String activityText = activityTextInput.getText();
            String imagePath = imagePathInpyt.getText();
            String amountWhString = whInput.getText();

            if (activityText.length() == 0 || imagePath.length() == 0
                    || amountWhString.length() == 0) {
                throw new InputMismatchException();
            }

            long amoutWh = Long.parseLong(amountWhString);

            errorText.setVisible(false);
            successText.setVisible(true);

            if (flag) {
                return null;
            }
            return new Activity(activityText, imagePath, amoutWh);

        } catch (NumberFormatException e) {
            flag = true;
            errorText.setText("Please enter a number for amount Wh");
            successText.setVisible(false);
            errorText.setVisible(true);

        } catch (InputMismatchException e) {
            flag = true;
            errorText.setText("Please fill all three fields");
            successText.setVisible(false);
            errorText.setVisible(true);
        }
        return null;
    }

    /**
     * Clear the value's of the input fields.
     * Used when closing the window.
     */
    public void clearFields() {
        activityTextInput.clear();
        whInput.clear();
        imagePathInpyt.clear();
    }

    /**
     * Hide the error- and successful message's.
     */
    public void hideMessages() {
        this.errorText.setVisible(false);
        this.successText.setVisible(false);
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
            case ENTER -> add();
            case ESCAPE -> cancel();
            default -> {
                break;
            }
        }
    }

    /**
     * Check if an activity is valid, before send it to the server side to store it.
     * @param activity an Activity to check.
     * @return true if field are valid and activity is not null, otherwise false is returned.
     */
    public boolean activityCheck(Activity activity) {
        // TODO: Not check image path, because if no image is provided. Or the image path is invalid.
        //  (File not found error thrown by the image inject part)
        //  we should provide a default question image.
        //  For example use the logo as the image.
        if (activity == null || activity.getActivityQuestion().length() == 0) {
            return false;
        }
        return true;
    }

}
