package server.entities;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Quiz {

    private long id;
    private Boolean isMultiPlayer;

    /**
     * This creates a new Quiz instance.
     * @param id A long value that represent the ID of the quiz.
     * @param isMultiPlayer A boolean value that is
     *                      true if the Quiz game instance is multiplayer,
     *                      false if the Quiz game instance is single player.
     */
    public Quiz(long id, Boolean isMultiPlayer) {
        this.id = id;
        this.isMultiPlayer = isMultiPlayer;
    }

    /**
     * A getter to get the id of a Quiz game instance.
     * @return a long value, that represent the gameId of a Quiz game instance.
     */
    public long getId() {
        return this.id;
    }

    /**
     * A getter to get the gameMode of a Quiz game instance.
     * @return a boolean value. True : Multiplayer. False : Singleplayer.
     */
    public Boolean getIsMultiPlayer() {
        return this.isMultiPlayer;
    }

    /**
     * A toString methode.
     * @return a string representation of a Quiz game instance.
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * A equals methode.
     * @param o An Object to compare with a Quiz game instance.
     * @return True if they are equals, otherwise false is returned.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Quiz) {
            Quiz that = (Quiz) o;
            return this.id == that.id && this.isMultiPlayer == that.isMultiPlayer;
        }
        return false;
    }

    /**
     * A hashCode method.
     * @return An int value that represents the hashCode of a Quiz game instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, isMultiPlayer);
    }
}
