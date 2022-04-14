package server.entities;

import commons.IngamePlayer;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class QuizCenter {

    private List<Quiz> allGames;
    private long amount;
    HashMap<IngamePlayer, Quiz> playersmp;

    /**
     * A Constructor for quizcenter.
     * It has a list that stores all quiz games.
     * It has long value 'amount' to give the quiz instance an id.
     */
    public QuizCenter() {
        this.allGames = new ArrayList<>();
        this.amount = 0;
        this.playersmp = new HashMap<IngamePlayer, Quiz>();
    }

    /**
     * A getter method to get the list of all games stored in the gamecenter.
     * @return A List that stores all games.
     */
    public List<Quiz> getAllGames() {
        return this.allGames;
    }

    /**
     * A getter method to get the amount of games in the gamecenter.
     * @return A long value that represents amount of quiz games ever initialized.
     */
    public long getAmount() {
        return this.amount;
    }

    /**
     * A method that creates a new SinglePlayerGame.
     * @param player game player.
     */
    public void addSinglePlayerGame(IngamePlayer player) {
        Quiz nq = new Quiz(++amount, false);
        this.playersmp.put(player, nq);
        this.allGames.add(nq);
    }

    /**
     * A method that creates a new MultiPlayerGame.
     * @param players game players.
     */
    public void addMultiPlayerGame(List<IngamePlayer> players) {
        Quiz nq = new Quiz(++amount, false);
        for (IngamePlayer pl : players) {
            this.playersmp.put(pl, nq);
        }
        this.allGames.add(nq);
    }

    /**
     * Provides quiz corresponding to the player.
     * @param player player.
     * @return quiz object.
     */
    public Quiz getPlayerQuiz(IngamePlayer player) {
        return this.playersmp.get(player);
    }

    /**
     * A toString method for the quizcenter.
     * I use StringBuilder instead of String, it performs much faster than string.
     * @return a String representation for the quizcenter.
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * An equals method.
     * @param o An object to be compared.
     * @return true if the tobe-compared quizcenter equals
     * to the instance that calls this method.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof QuizCenter) {
            QuizCenter that = (QuizCenter) o;
            return Objects.equals(this.allGames, that.allGames) && Objects.equals(this.playersmp, that.playersmp);
        }
        return false;
    }

    /**
     * A hashCode method.
     * @return An int value that represents the hashCode of a Quiz center instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(allGames, amount, playersmp);
    }
}
