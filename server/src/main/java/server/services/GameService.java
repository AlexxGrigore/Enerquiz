package server.services;

import commons.IngamePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.entities.ServerGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class GameService {

    private Map<IngamePlayer, ServerGame> gamesMap = new HashMap<>();

    /**
     * Create a new GameService.
     */
    @Autowired
    public GameService() {}

    /**
     * Create a new GameService with an existing map of players to games.
     *
     * @param gamesMap The map of players to games
     */
    public GameService(Map<IngamePlayer, ServerGame> gamesMap) {
        this.gamesMap = gamesMap;
    }

    /**
     * Get a map of all players to games.
     *
     * @return The map of all players to games
     */
    public Map<IngamePlayer, ServerGame> getGamesMap() {
        return this.gamesMap;
    }

    /**
     * Add a game to the map of players to games.
     *
     * @param game The game with players to add
     */
    public void addGame(ServerGame game) {
        game.getPlayers().forEach(p -> gamesMap.put(p, game));
    }

    /**
     * Get the game the player is currently in.
     *
     * @param player The player to get the game of
     * @return The game the player is currently in
     */
    public ServerGame getGame(IngamePlayer player) {
        return gamesMap.get(player);
    }

    /**
     * Get the set of all players currently playing a game.
     *
     * @return The set of all players
     */
    public Set<IngamePlayer> getAllPlayers() {
        return this.gamesMap.keySet();
    }

    /**
     * Get the set of all games players are currently playing.
     *
     * @return The set of all games
     */
    public Set<ServerGame> getAllGames() {
        return new HashSet<>(this.gamesMap.values());
    }

}
