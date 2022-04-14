package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngamePlayerTest {

    @Test
    void constructor() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
    }

    @Test
    void getName() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        assertEquals("TheLegend27", player.getName());
    }

    @Test
    void setName() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        assertEquals("TheLegend27", player.getName());
        player.setName("Jeroen");
        assertEquals("Jeroen", player.getName());
    }

    @Test
    void getPointsInitialZero() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        assertEquals(0, player.getPoints());
    }

    @Test
    void setPointsAndGetPoints() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        player.setPoints(69);
        assertEquals(69, player.getPoints());
    }

    @Test
    void addPointsOnce() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        assertEquals(0, player.getPoints());
        assertEquals(42, player.addPoints(42));
        assertEquals(42, player.getPoints());
    }

    @Test
    void addPointsTwice() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        assertEquals(0, player.getPoints());
        assertEquals(42, player.addPoints(42));
        assertEquals(42, player.getPoints());
        assertEquals(100, player.addPoints(58));
        assertEquals(100, player.getPoints());
    }

    @Test
    void getPositionInitialOne() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        assertEquals(1, player.getPosition());
    }

    @Test
    void setPositionAndGetPosition() {
        IngamePlayer player = new IngamePlayer("TheLegend27");
        player.setPosition(10);
        assertEquals(10, player.getPosition());
    }
}
