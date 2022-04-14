package server.entities;

import commons.IngamePlayer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizCenterTest {

    @Test
    void getAllGames() {
        QuizCenter qc = new QuizCenter();
        assertTrue(qc.getAllGames() != null);
        assertTrue(qc.getAllGames().size() == 0);
    }

    @Test
    void getAmount() {
        QuizCenter qc = new QuizCenter();
        assertTrue(qc.getAmount() == 0);
    }

    @Test
    void addSinglePlayerGame() {
        QuizCenter qc = new QuizCenter();
        assertTrue(qc.getAmount() == 0);
        qc.addSinglePlayerGame(new IngamePlayer("pl1"));
        qc.addSinglePlayerGame(new IngamePlayer("pl2"));
        assertTrue(qc.getAmount() == 2);
    }

    @Test
    void addMultiPlayerGame() {
        QuizCenter qc = new QuizCenter();
        assertTrue(qc.getAmount() == 0);
        IngamePlayer pl1 = new IngamePlayer("pl1");
        IngamePlayer pl2 = new IngamePlayer("pl2");
        List<IngamePlayer> pls = Arrays.asList(pl1, pl2);
        qc.addMultiPlayerGame(pls);
        qc.addMultiPlayerGame(pls);
        assertTrue(qc.getAmount() == 2);
    }

    @Test
    void getPlayerQuiz() {
        QuizCenter qc = new QuizCenter();
        IngamePlayer pl1 = new IngamePlayer("pl1");
        IngamePlayer pl2 = new IngamePlayer("pl2");
        List<IngamePlayer> pls = Arrays.asList(pl1, pl2);
        qc.addMultiPlayerGame(pls);
        assertEquals(qc.getPlayerQuiz(pl1), qc.getPlayerQuiz(pl2));
    }

    @Test
    void testEquals() {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        IngamePlayer pl2 = new IngamePlayer("pl2");
        List<IngamePlayer> pls = Arrays.asList(pl1, pl2);
        QuizCenter qc1 = new QuizCenter();
        qc1.addSinglePlayerGame(pl1);
        QuizCenter qc2 = new QuizCenter();
        qc2.addMultiPlayerGame(pls);
        assertNotEquals(qc1, qc2);
        assertEquals(qc1, qc1);
    }

    @Test
    void testHashCode() {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        IngamePlayer pl2 = new IngamePlayer("pl2");
        List<IngamePlayer> pls = Arrays.asList(pl1, pl2);
        QuizCenter qc1 = new QuizCenter();
        qc1.addSinglePlayerGame(pl2);
        QuizCenter qc2 = new QuizCenter();
        qc2.addMultiPlayerGame(pls);
        assertFalse(qc1.hashCode() == qc2.hashCode());
        assertTrue(qc1.hashCode() == qc1.hashCode());
    }
}
