package client.scenes;
import client.event.EventManager;
import client.utils.ServerUtils;
import commons.IngamePlayer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

public class GameLeaderboardCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private final EventManager eventManager;

    @FXML
    private ImageView goldImageView;

    @FXML
    private ImageView silverImageView;

    @FXML
    private ImageView bronzeImageView;

    @FXML
    private Label firstPlayerName;

    @FXML
    private Label secondPlayerName;

    @FXML
    private Label thirdPlayerName;

    @FXML
    private Label firstPlayerPoints;

    @FXML
    private Label secondPlayerPoints;

    @FXML
    private Label thirdPlayerPoints;

    @FXML
    private Button backToLeaderboardButton;

    @FXML
    private Button playAgainButton;

    @FXML
    private Button continueButton;

    @FXML
    private Button exitButton;

    private Runnable onContinueQuiz;
    private Runnable onFinishedQuiz;

    /**
     * Constructor for creating new FinalLeaderboardCtrl.
     * @param serverUtils The ServerUtils instance used to send REST API calls
     * @param mainCtrl Main Controller instance
     * @param eventManager The EventManager instance
     */
    @Inject
    public GameLeaderboardCtrl(ServerUtils serverUtils, MainCtrl mainCtrl, EventManager eventManager) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        this.eventManager = eventManager;
    }

    /**
     * Method that sets all medal images.
     */
    public void setImages() {
        Image goldImage = new Image("@../../client/images/gold medal.png");
        goldImageView.setImage(goldImage);
        Image silverImage = new Image("@../../client/images/silver medal.png");
        silverImageView.setImage(silverImage);
        Image bronzeImage = new Image("@../../client/images/bronze.png");
        bronzeImageView.setImage(bronzeImage);
    }

    /**
     * Method that fills the information into leaderboard.
     * Asks for sorted list of top3 players.
     */
    public void fillLeaderboard() {
        List<IngamePlayer> topPlayers = returnTopPlayers(serverUtils.getPlayers());
        firstPlayerName.setText(topPlayers.get(0).getName());
        firstPlayerPoints.setText(topPlayers.get(0).getPoints() + " pts");
        if (topPlayers.size() > 1) {
            secondPlayerName.setText(topPlayers.get(1).getName());
            secondPlayerPoints.setText(topPlayers.get(1).getPoints() + " pts");
        } else {
            secondPlayerName.setText("");
            secondPlayerPoints.setText("");
        }
        if (topPlayers.size() > 2) {
            thirdPlayerName.setText(topPlayers.get(2).getName());
            thirdPlayerPoints.setText(topPlayers.get(2).getPoints() + " pts");
        } else {
            thirdPlayerName.setText("");
            thirdPlayerPoints.setText("");
        }
    }

    /**
     * Method that returns an unmodifyable list of at most 3 players, sorted on their position.
     * @param allPlayers List of all players in the game.
     * @return topPlayers Sorted list of top 3 players.
     */
    public List<IngamePlayer> returnTopPlayers(List<IngamePlayer> allPlayers) {
        return allPlayers.stream().sorted(Comparator.comparingInt(IngamePlayer::getPosition)).limit(3).toList();
    }


    /**
     * On exitButton press goes to choosing gamemode.
     */
    public void exit() {
        mainCtrl.showGamemode();
        eventManager.stopPollingEvents();
    }

    /**
     * On backToLeaderboardButton press goes to Leaderboard which atm doesn't exist.
     */
    public void backToLeaderboard() {
        if (this.onFinishedQuiz != null) {
            mainCtrl.showGlobalLeaderBoard();
            eventManager.stopPollingEvents();
            Platform.runLater(this.onFinishedQuiz);
        }
    }

    /**
     * On exit playAgainButton press goes to Queue.
     */
    public void playAgain() {
        if (this.onFinishedQuiz != null) {
            mainCtrl.showGamemode();
            eventManager.stopPollingEvents();
            Platform.runLater(this.onFinishedQuiz);
        }
    }

    /**
     * Method that continues quiz on the button press.
     */
    public void continueQuiz() {
        if (this.onContinueQuiz != null) {
            serverUtils.continueGame();
            Platform.runLater(this.onContinueQuiz);
        }
    }

    /**
     * This method is used to initialize Runnable for continuing the quiz.
     * @param onContinueQuiz Runnable instance of method, in this case used to hide leaderboard
     *                       and continue game.
     */
    public void setOnContinueQuiz(Runnable onContinueQuiz) {
        this.onContinueQuiz = onContinueQuiz;
    }

    /**
     * This method is used to initialize Runnable for ending the quiz.
     * @param onFinishedQuiz Runnable instance of method, in this case used to hide leaderboard.
     */
    public void setOnFinishedQuiz(Runnable onFinishedQuiz) {
        this.onFinishedQuiz = onFinishedQuiz;
    }
}
