package client.controls;

import commons.IngamePlayer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LeaderboardEntryCtrl {

    private @FXML Label position;
    private @FXML Label name;
    private @FXML Label points;

    private IngamePlayer player;

    /**
     * Populate the labels with the player's information.
     *
     * @param player The player to populate the labels with
     */
    public void setPlayer(IngamePlayer player) {
        this.player = player;

        this.setName(this.player.getName());
        this.setPoints(this.player.getPoints());
        this.setPosition(this.player.getPosition());
    }

    /**
     * Set the leaderboard entry's position label.
     *
     * @param position The position
     */
    public void setPosition(int position) {
        this.position.setText("#" + position);
    }

    /**
     * Set the leaderboard entry's name label.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name.setText(name);
    }

    /**
     * Set the leaderboard entry's points label.
     *
     * @param points The points
     */
    public void setPoints(int points) {
        this.points.setText(points + " points");
    }

    /**
     * Get the player of this leaderboard entry.
     *
     * @return The player
     */
    public IngamePlayer getPlayer() {
        return player;
    }
}
