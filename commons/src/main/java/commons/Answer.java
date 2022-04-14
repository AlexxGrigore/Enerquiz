package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Answer {

    private IngamePlayer player;
    private long answer;
    private double fractionTimeLeft;

    /**
     * Default constructor. Used for deserialization.
     */
    public Answer() {}

    /**
     * Create a new Answer object.
     *
     * @param answer The player's answer
     * @param fractionTimeLeft The time the player had left to answer (between 0 and 1, inclusive)
     * @param player The player who submitted this answer. Use null if there is no player.
     */
    public Answer(long answer, double fractionTimeLeft, IngamePlayer player) {
        this.answer = answer;
        this.fractionTimeLeft = fractionTimeLeft;
        this.player = player;
    }

    /**
     * Get the player's answer.
     *
     * @return The player's answer
     */
    public long getAnswer() {
        return answer;
    }

    /**
     * Get the time the player had left to answer (between 0 and 1, inclusive).
     *
     * @return The fraction of time left
     */
    public double getFractionTimeLeft() {
        return fractionTimeLeft;
    }

    /**
     * Get the player who submitted this answer.
     *
     * @return The player
     */
    public IngamePlayer getPlayer() {
        return player;
    }

    /**
     * Set the player who submitted this answer.
     *
     * @param player The player
     */
    public void setPlayer(IngamePlayer player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
