package server.api;

import commons.GlobalLeaderBoardEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.GlobalLeaderBoardRepository;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GlobalLeaderBoardControllerTest {
    private Random random;
    private GlobalLeaderBoardRepository repository;
    private GlobalLeaderBoardEntry entry;
    private GlobalLeaderBoardController controller;

    @BeforeEach
    void setup() {
        random = mock(Random.class);
        repository = mock(GlobalLeaderBoardRepository.class);
        entry = mock(GlobalLeaderBoardEntry.class);

    }

    @Test
    void getSinglePlayerLeaderBoard() {
        when(repository.findTopFiveSinglePlayer()).thenReturn(List.of(entry));
        controller = new GlobalLeaderBoardController(random, repository);

        List<GlobalLeaderBoardEntry> entries = controller.getSinglePlayerLeaderBoard();

        assertEquals(entries.get(0), entry);

    }

    @Test
    void getMultiPlayerLeaderBoard() {
        when(repository.findTopFiveMultiPlayer()).thenReturn(List.of(entry));
        controller = new GlobalLeaderBoardController(random, repository);

        List<GlobalLeaderBoardEntry> entries = controller.getMultiPlayerLeaderBoard();

        assertEquals(entries.get(0), entry);
    }
}
