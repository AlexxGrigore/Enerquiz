package client.scenes;

import client.Main;
import client.hooks.SceneLifecycle;
import client.utils.ServerUtils;
import commons.IngamePlayer;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.WHITE;

public class QueueAndEnterNameMpCtrl implements Initializable, SceneLifecycle {

    private final ServerUtils server;
    protected final MainCtrl mainCtrl;
    private String nameEntered;

    @FXML
    private Button startGameButton;

    @FXML
    private Button changeNameButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button closeHelpButton;

    @FXML
    private Label errorText;

    @FXML
    private TextField name;

    @FXML
    private GridPane gridPane;

    @FXML
    private Pane helpPane;

    @FXML
    private Button backButton;

    public HashMap<Pair<Integer, Integer>, Label> queueMap = new HashMap<>();
    private HelpCtrl helpCtrl;
    private Scene help;
    private int expireTime = 2;

    private AtomicBoolean isPolling = new AtomicBoolean(false);

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setBackImage();
        Pair<HelpCtrl, Parent> help = Main.getFXML().load(HelpCtrl.class, "client", "scenes", "Help.fxml");
        this.help = new Scene(help.getValue());
        this.helpCtrl = help.getKey();

        helpCtrl.setHelpLabels(HelpCtrl.HelpType.QUEUE);
    }

    /**
     * Constructor.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public QueueAndEnterNameMpCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super();
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.nameEntered = null;
    }

    /**
     * Method for setting images everywhere.
     * It's called when the scene is shown so that images are there at the start.
     */
    public void setImages() {

    }

    /**
     * Method for setting images everywhere.
     * It's called when the scene is shown so that images are there at the start.
     */
    public void setBackImage() {
        Image backImage = new Image("@../../client/images/back.png");
        ImageView imageView = new ImageView(backImage);
        imageView.setFitHeight(75);
        imageView.setPreserveRatio(true);
        backButton.setText("");
        backButton.setGraphic(imageView);
    }

    /**
     * Laods player queue.
     */
    public void loadQueue() {
        clearLabels();
        List<IngamePlayer> players = server.getQueue();
        for (IngamePlayer pl : players) {
            playerJoined(pl);
        }
    }

    /**
     * Method for showing error.
     * @param textError The text to display in the error textField.
     */
    public void showError(String textError) {
        errorText.setText(textError);
        errorText.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(expireTime));
        visiblePause.setOnFinished(event -> errorText.setVisible(false));
        visiblePause.play();
    }

    /**
     * Clear the whole GridPane.
     */
    public void clearLabels() {
        gridPane.getChildren().clear();
        queueMap = new HashMap<>();
    }

    /**
     * Creates a label that's going to be put in the grid pane.
     * @param player The player to get the name of
     * @return label The created label with the player's name
     */
    public Label createPlayerNameLabel(IngamePlayer player) {
        Label label = new Label();
        label.setText(player.getName());
        label.setAlignment(Pos.CENTER);
        label.setMinWidth(366.66);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(WHITE);
        label.setFont(new Font("Verdana", 28));
        return label;
    }

    /**
     * Starts polling queue updates.
     */
    public void startPolling() {
        if (isPolling.get()) {
            return;
        }

        isPolling.set(true);

        pollLoop();
    }

    /**
     * Stops polling queue updates.
     */
    public void stopPolling() {
        isPolling.set(false);
    }

    /**
     * Polling loop for the queue updates.
     */
    public void pollLoop() {
        new Thread(() -> {
            try {
                if (!isPolling.get()) {
                    return;
                }
                Platform.runLater(() -> {
                    loadQueue();
                    backgroundStart();
                });
                Thread.sleep(1000);
                pollLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * This method will start the game. // I still don't know how
     */
    public void startGame() {
        this.server.startGame();
        stopPolling();
        mainCtrl.showIngame();
    }

    /**
     * Start if player is ingame.
     */
    public void backgroundStart() {
        if (server.isPlaying()) {
            stopPolling();
            mainCtrl.showIngame();
        }
    }

    /**
     * This method will change scene to queue.
     */
    public void startGameMultiPlayer() {
        try {
            nameEntered = name.getText();
            List<String> inQueue = server.getQueue().stream().map(player -> player.getName()).collect(
                    Collectors.toList());
            if (nameEntered.length() == 0) {
                showError("Please enter a name!");
                // comment for my teammates
            } else if (inQueue.contains(nameEntered)) {
                showError("Name already taken");
            } else {
                mainCtrl.updateMemorizedName(nameEntered);
                server.setName(nameEntered);
                server.joinQueue();
                mainCtrl.showQueue(); // mp game starts
            }
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
        clear();
    }

    /**
     * Method called when player joined.
     * @param player The player that just joined
     */
    public void playerJoined(IngamePlayer player) {
        Label label = createPlayerNameLabel(player);

        outerloop:
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                Pair<Integer, Integer> keyPair = new Pair<>(j, i);
                if (!queueMap.containsKey(keyPair)) {
                    gridPane.add(label, j, i);
                    queueMap.put(keyPair, label);
                    break outerloop;
                }
            }
        }
    }

    /**
     * Method for starting the session after URL is entered.
     */
    public void changeName() {
        nameEntered = name.getText();
        List<String> inQueue = server.getQueue().stream().map(player -> player.getName()).collect(Collectors.toList());

        if (nameEntered.length() == 0) {
            showError("Wrong input!");
        } else if (inQueue.contains(nameEntered)) {
            showError("Name already taken");
        }  else {
            server.changeName(nameEntered);
            loadQueue();
        }
    }

    /**
     * Method for going back to previous scene for Queue page.
     */
    public void back() {
        try {
            server.leaveQueue();
            mainCtrl.showEnterNameMp();
            stopPolling();
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    /**
     * Method for going back to previous scene for EnterNameMultiPlayer page.
     */
    public void backMp() {
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
     * When game starts this method should be called.
     * It clears the name box.
     */
    private void clear() {
        name.clear();
    }

    /**
     * Method that adds helpScene to helpPane.
     */
    public void addHelp() {
        Parent helpParent = help.getRoot();
        helpPane.getChildren().add(helpParent);
    }

    /**
     * Shows help window.
     */
    public void showHelp() {
        this.helpPane.getChildren().clear();
        this.addHelp();
//        this.helpPane.getChildren().clear(); // I don't know what to say...
//        this.addHelp();
        this.helpCtrl.helpPane.setVisible(false);
        helpCtrl.helpPane.setVisible(true);
    }

    @Override
    public void onSceneShow(Scene oldScene, Scene newScene) {
        startPolling();
    }

    @Override
    public void onSceneHide(Scene oldScene, Scene newScene) {
        stopPolling();
    }

    /**
     * Leaves queue and stops pooling during the shut down.
     */
    public void shutdown() {
        stopPolling();
        server.leaveQueue();
        Platform.exit();
    }

    /**
     * Set the text field with memorized name.
     * @param name The name that will be set to the text field.
     */
    public void setTextFieldName(String name) {
        this.name.setText(name);
    }

}
