package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 * A simple class that holds information about a player in an active game.
 */
public class IngamePlayer {

    private String name;
    private int points;
    private int position;

    /**
     * Create a new IngamePlayer.
     *
     * @param name The name
     */
    public IngamePlayer(String name) {
        this.name = name;
        this.points = 0;
        this.position = 1;
    }

    /**
     * Basic constructor.
     */
    public IngamePlayer() {
    }

    /**
     * Get the player's name.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the player's name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Get the amount of points the player has.
     *
     * @return The amount of points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Set the amount of points the player has.
     *
     * @param points The amount of points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Add an amount of points to the player's points.
     *
     * @param points The amount of points to add
     * @return The amount of points the player has after adding the new points
     */
    public int addPoints(int points) {
        this.points += points;

        return this.points;
    }

    /**
     * Get the position of the player.
     * The player in the leading position has a position of 1. Multiple players can have the same position.
     *
     * @return The position
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Set the position of the player.
     * The player in the leading position has a position of 1. Multiple players can have the same position.
     *
     * @param position The position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
