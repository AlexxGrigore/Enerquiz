package commons;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class GlobalLeaderBoardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String playerName;

    public int playerScore;

    public boolean isMultiplayer;

    private GlobalLeaderBoardEntry() {};

    /**
     *
     * Constructor.
     *
     * @param playerName
     * @param playerScore
     * @param isMultiplayer
     */
    public GlobalLeaderBoardEntry(String playerName, int playerScore, boolean isMultiplayer) {

        this.playerName = playerName;
        this.playerScore = playerScore;
        this.isMultiplayer = isMultiplayer;
    }



    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


}
