package server.api;

import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.PlayerRepository;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PlayerControllerTest {

    private Random random;
    private PlayerController playerController;
    private PlayerRepository playerRepository;

    @BeforeEach
    void setup() {
        playerRepository = mock(PlayerRepository.class);
        random = mock(Random.class);
        playerController = new PlayerController(random, playerRepository);

    }

    @Test
    void getAll() {
        List<Player> players = List.of(
                new Player("f1", "l1"),
                new Player("f2", "l2")
        );
        when(playerRepository.findAll()).thenReturn(players);
        List<Player> result = playerController.getAll();
        assertEquals(result, players);
    }
}
