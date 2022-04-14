package exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NameExceptionTest {
    @Test
    public void testConstructor() {
        NameException exc = new NameException("msg");
        assertTrue(exc instanceof Exception);
    }
}
