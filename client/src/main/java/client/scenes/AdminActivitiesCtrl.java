package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;


import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminActivitiesCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<Activity> data;

    @FXML
    private Text successText;

    @FXML
    private Button backButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteByIdButton;

    @FXML
    private Button deleteBySelectionButton;

    @FXML
    private Button defaultdatabaseButton;

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
     * Create a new AdminActivitiesCtrl.
     *
     * @param server The ServerUtil instance to connect to the backend actions.
     * @param mainCtrl The Mainctrl to control the scenes.
     */
    @Inject
    public AdminActivitiesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colID.setCellValueFactory(ac -> new SimpleLongProperty(ac.getValue().getId()).asObject());
        colActivityText.setCellValueFactory(ac -> new SimpleStringProperty(ac.getValue().getActivityQuestion()));
        colAmountWh.setCellValueFactory(ac ->  new SimpleLongProperty(ac.getValue().getAmountWh()).asObject());
        colImagePath.setCellValueFactory(ac -> new SimpleStringProperty(ac.getValue().getImagePath()));
        Image backImage = new Image("@../../client/images/back.png");
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitHeight(75);
        backImageView.setPreserveRatio(true);
        backButton.setGraphic(backImageView);

        table.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }

    /**
     * Show the pop-up window for add an activity purpose.
     */
    public void addActivity() {
        mainCtrl.showAddActivity();
        successText.setText("");
    }

    /**
     * Show the pop-up window for delete an activity purpose.
     */
    public void deleteActivity() {
        mainCtrl.showDeleteActivity();
        successText.setText("");
    }

    /**
     * Enable the selection on the table, and add a button that deleted all the activities that are selected.
     * Sometimes the table isn't updated after the operation, so some activity might already be deleted while
     * still being selected to delete. This case, add a dynamic message that tells the user how many of the
     * selected activities are deleted.
     */
    public void deleteBySelection() {
        successText.setVisible(false);
        long counter = 0;
        if (table.getSelectionModel().getSelectedItems() != null
                && table.getSelectionModel().getSelectedItems().size() != 0) {
            long numberSelected = table.getSelectionModel().getSelectedItems().size();
            for (Activity activity : table.getSelectionModel().getSelectedItems()) {
                if (server.deleteActivityById(activity.getId()) != null) {
                    counter++;
                }
            }
            String activityPlural = " activity deleted";
            if (numberSelected >= 2) {
                activityPlural = " activities deleted ";
            }
            String message = counter + " of " + numberSelected + activityPlural;
            successText.setText(message);
            successText.setVisible(true);
        }
        refresh();
    }


    /**
     * Load the default database.
     */
    public void loadDefaultDatabase() {
        this.server.loadDefaultDatabase();
        this.successText.setText("Default database loaded successfully");
        this.successText.setVisible(true);
        refresh();
    }

    /**
     * A method that refreshes the content in the table.
     *
     * Which is very useful after adding or deleting an activity.
     */
    public void refresh() {
        var activities = server.getAllActivities();
        data = FXCollections.observableList(activities);
        table.setItems(data);
    }


    /**
     * Method for going back to previous scene.
     */
    public void back() {
        this.successText.setVisible(false);
        mainCtrl.showAdminPanel();
    }
}
