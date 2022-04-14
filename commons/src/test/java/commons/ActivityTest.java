package commons;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    private Activity a;
    private Activity a2;
    private Activity a3;

    @BeforeEach
    public void setUp() {
        a = new Activity("Wash clothes", "folder", 69);
        a2 = new Activity("Wash clothes", "folder", 69);
        a3 = new Activity(69);
    }

    @Test
    void testConstructor() {
        assertNotNull(a);
        assertNotNull(a2);
    }

    @Test
    void testEquals() {
        assertEquals(a, a);
    }

    @Test
    void testEquals2() {
        assertEquals(a, a2);
    }

    @Test
    void testEquals3() {
        Activity a3 = new Activity("Wash ", "folder", 69);
        assertNotEquals(a, a3);
    }

    @Test
    void getTitle() {
        assertEquals("Wash clothes", a.getActivityQuestion());
        assertNotEquals("Wash apple", a.getActivityQuestion());
    }

    @Test
    void getImagePath() {
        assertEquals("folder", a2.getImagePath());
        assertNotEquals("in usb-stick", a2.getImagePath());
    }

    @Test
    void getAmountWatts() {
        assertEquals(69, a.getAmountWh());
        assertEquals(a.getAmountWh(), a2.getAmountWh());
    }

    @Test
    void getId() {
        assertEquals(69, a3.getId());
        assertNotEquals(70, a3.getId());
    }

}
