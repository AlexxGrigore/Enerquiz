package server.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    @Test
    void getId() {
        long amount = 0;
        Quiz q1 = new Quiz(amount++, false);
        Quiz q2 = new Quiz(amount++, false);
        Quiz q3 = new Quiz(amount++, true);
        assertTrue(q1.getId() == 0);
        assertTrue(q2.getId() == 1);
        assertFalse(q1.getId() == q3.getId());
    }

    @Test
    void getIsMultiPlayer() {
        long amount = 0;
        Quiz q1 = new Quiz(amount++, false);
        Quiz q2 = new Quiz(amount++, false);
        Quiz q3 = new Quiz(amount++, true);
        assertFalse(q1.getIsMultiPlayer());
        assertTrue(q3.getIsMultiPlayer());
        assertTrue(q1.getIsMultiPlayer() == q2.getIsMultiPlayer());
        assertFalse(q1.getIsMultiPlayer() == q3.getIsMultiPlayer());
    }
    @Test
    void testEquals() {
        long amount = 0;
        Quiz q1 = new Quiz(amount++, false);
        Quiz q2 = new Quiz(amount++, false);
        assertEquals(q1, q1);
        assertNotEquals(q1, q2);
    }

    @Test
    void testHashCode() {
        long amount = 0;
        Quiz q1 = new Quiz(amount++, false);
        Quiz q2 = new Quiz(amount++, false);
        assertTrue(q1.hashCode() == q1.hashCode());
        assertFalse(q1.hashCode() == q2.hashCode());
    }

}
