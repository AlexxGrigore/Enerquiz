package commons.events;

import commons.IngamePlayer;

import java.util.List;

public class FinalLeaderboardEvent extends Event {

    private List<IngamePlayer> playerList;

    /**
     * Create a new player list update event.
     *
     * @param playerList The list of players
     */
    public FinalLeaderboardEvent(List<IngamePlayer> playerList) {
        this.playerList = playerList;
    }

    /**
     * Get the list of players.
     *
     * @return The list of players
     */
    public List<IngamePlayer> getPlayerList() {
        return playerList;
    }
}
