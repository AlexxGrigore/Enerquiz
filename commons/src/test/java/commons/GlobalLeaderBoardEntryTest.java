package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalLeaderBoardEntryTest {

    @Test
    void testEquals() {
       GlobalLeaderBoardEntry test = new GlobalLeaderBoardEntry("Huwe", 111, true);
       assertTrue(test.equals(new GlobalLeaderBoardEntry("Huwe", 111, true)));
    }

    @Test
    void testHashCode() {
        GlobalLeaderBoardEntry test = new GlobalLeaderBoardEntry("Huwe", 111, true);
        assertEquals(test.hashCode(), (new GlobalLeaderBoardEntry("Huwe", 111, true)).hashCode());
    }


}




