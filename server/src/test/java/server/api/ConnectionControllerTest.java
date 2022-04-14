package server.api;

import commons.IngamePlayer;
import exceptions.NameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ConnectionControllerTest {

    private ConnectionController controller;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setup() {
        controller = new ConnectionController();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
    }

    @Test
    void createSession() throws IOException {
        when(session.getId()).thenReturn("Sesid");

        String res = controller.createSession(request);

        assertEquals("Sesid", res);
    }

    @Test
    void setName() throws NameException {
        String res = controller.setName(request, response, "Name");

        assertEquals("Name", res);
    }

    @Test
    void changeName() throws NameException {
        IngamePlayer pl1 = new IngamePlayer("pl1");
        when(session.getAttribute("user")).thenReturn(pl1);

        String res = controller.changeName(request, response, "newname");

        assertEquals(res, "newname");
        assertEquals(pl1.getName(), "newname");
    }
}
