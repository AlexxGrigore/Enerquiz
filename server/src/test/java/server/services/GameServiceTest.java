package server.services;

import commons.IngamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.entities.ServerGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameServiceTest {

    private Map<IngamePlayer, ServerGame> gamesMap;
    private IngamePlayer playerA;
    private IngamePlayer playerB;
    private IngamePlayer playerC;
    private IngamePlayer playerD;
    private List<IngamePlayer> players;
    private QuestionService questionService;
    private GlobalLeaderBoardService globalLeaderBoardService;
    private ServerGame game;
    private GameService service;

    @BeforeEach
    public void setup() {
        questionService = mock(QuestionService.class);
        globalLeaderBoardService = mock(GlobalLeaderBoardService.class);
        gamesMap = new HashMap<>();
        playerA = new IngamePlayer("Player A");
        playerB = new IngamePlayer("Player B");
        playerC = new IngamePlayer("Player C");
        playerD = new IngamePlayer("Player D");
        players = List.of(playerA, playerB, playerC, playerD);
        game = new ServerGame(players, questionService, globalLeaderBoardService, true);
        service = new GameService(gamesMap);
    }

    @Test
    void getGamesMap() {
        assertSame(gamesMap, service.getGamesMap());
    }

    @Test
    void addGame() {
        service.addGame(game);

        players.forEach(p -> assertTrue(gamesMap.containsKey(p)));
    }

    @Test
    void addGameReplacesPreviousGameOfPlayer() {
        ServerGame gameA = new ServerGame(List.of(playerA), questionService, globalLeaderBoardService, true);
        service.addGame(gameA);

        assertSame(gameA, gamesMap.get(playerA));

        ServerGame gameB = new ServerGame(List.of(playerA), questionService, globalLeaderBoardService, true);
        service.addGame(gameB);

        assertSame(gameB, gamesMap.get(playerA));

    }

    @Test
    void getGame() {
        service.addGame(game);

        players.forEach(p -> assertSame(game, service.getGame(p)));
    }

    @Test
    void getAllPlayers() {
        service.addGame(game);

        Set<IngamePlayer> playerSet = new HashSet<>(players);

        assertEquals(playerSet, service.getAllPlayers());
    }

    @Test
    void getAllGamesOne() {
        service.addGame(game);

        assertEquals(Set.of(game), service.getAllGames());
    }

    @Test
    void getAllGamesTwo() {
        ServerGame gameA = new ServerGame(List.of(playerA, playerB), questionService, globalLeaderBoardService, true);
        ServerGame gameB = new ServerGame(List.of(playerC, playerD), questionService, globalLeaderBoardService, true);

        service.addGame(gameA);
        service.addGame(gameB);

        assertEquals(Set.of(gameA, gameB), service.getAllGames());
    }

    @Test
    void getAllGamesRestart() {
        ServerGame gameA = new ServerGame(players, questionService, globalLeaderBoardService, true);
        service.addGame(gameA);

        ServerGame gameB = new ServerGame(players, questionService, globalLeaderBoardService, true);
        service.addGame(gameB);

        assertEquals(Set.of(gameB), service.getAllGames());
    }
}
