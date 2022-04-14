package server.api;

import commons.IngamePlayer;
import commons.events.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.entities.ServerGame;
import server.services.GameService;
import server.utils.LongPoll;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class EventControllerTest {


    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private GameService gameService;
    private ServerGame game;
    private IngamePlayer pl1;
    private CompletableFuture completableFuture;
    private LongPoll longpol;

    @BeforeEach
    void init() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        gameService = mock(GameService.class);
        game = mock(ServerGame.class);
        completableFuture = mock(CompletableFuture.class);
        longpol = mock(LongPoll.class);
        pl1 = new IngamePlayer("pl1");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(pl1);
        when(gameService.getGame(pl1)).thenReturn(game);


    }

    @Test
    void sessionNull() throws IOException {
        when(request.getSession(false)).thenReturn(null);
        EventController controller = new EventController(gameService);

        CompletableFuture<List<Event>> comp = controller.poll(request, response);

        assertNull(comp);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "No session");
    }

    @Test
    void senderNull() throws IOException {
        when(session.getAttribute("user")).thenReturn(null);
        EventController controller = new EventController(gameService);

        CompletableFuture<List<Event>> comp = controller.poll(request, response);

        assertNull(comp);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "No user associated with session");
    }

    @Test
    void gameNull() throws IOException {
        when(gameService.getGame(pl1)).thenReturn(null);
        EventController controller = new EventController(gameService);

        CompletableFuture<List<Event>> comp = controller.poll(request, response);

        assertNull(comp);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "User not in game");
    }

    @Test
    void poll() throws IOException {
        when(game.getEventLongPoll()).thenReturn(longpol);
        when(longpol.poll(pl1, false)).thenReturn(completableFuture);
        EventController controller = new EventController(gameService);

        CompletableFuture<List<Event>> comp = controller.poll(request, response);

        assertEquals(comp, completableFuture);
    }
}
