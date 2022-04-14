package server.api;

import commons.IngamePlayer;
import commons.events.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.entities.ServerGame;
import server.services.GameService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final GameService gameService;

    /**
     * Create a new EventController.
     *
     * @param gameService The GameService instance
     */
    public EventController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Get all pending events for the sender using long polling. Once an event has been polled, polling again will not
     * include this event anymore.
     *
     * @param request The http servlet request with the sender's session information
     * @param response The http servlet response with the status code and error message
     * @return A deferred result which will contain all events since the last poll
     * @throws IOException If sending the error fails
     */
    @GetMapping("/poll")
    public CompletableFuture<List<Event>> poll(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No session");
            return null;
        }

        IngamePlayer sender = (IngamePlayer) session.getAttribute("user");
        if (sender == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No user associated with session");
            return null;
        }

        ServerGame game = gameService.getGame(sender);
        if (game == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not in game");
            return null;
        }

        return game.getEventLongPoll().poll(sender, false);
    }

}
