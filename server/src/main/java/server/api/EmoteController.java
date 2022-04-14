package server.api;

import commons.IngamePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/emotes")
public class EmoteController {

    private final Map<IngamePlayer, Map<String, String>> pendingEmotes = new HashMap<>();
    private final Map<IngamePlayer, CompletableFuture<Map<String, String>>> pendingPolling = new HashMap<>();

    private long pollTimeout = 10_000L;

    /**
     * Create a new EmoteController.
     */
    @Autowired
    public EmoteController() {}

    /**
     * Create a new EmoteController with a polling timeout.
     *
     * @param pollTimeout The timeout in milliseconds after which polling requests will be forcefully responded to
     */
    public EmoteController(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    /**
     * Add an emote to the pending emotes of all players.
     *
     * @param request The http servlet request with the sender's session information
     * @param emote The emote to add
     * @return 200 OK if an emote is provided, 400 BAD REQUEST otherwise
     */
    @PostMapping("/send/{emote}")
    public ResponseEntity<Void> send(HttpServletRequest request, @PathVariable("emote") String emote) {

        HttpSession session = request.getSession(false);
        if (emote == null || session == null) {
            return ResponseEntity.badRequest().build();
        }

        IngamePlayer sender = (IngamePlayer) session.getAttribute("user");
        if (sender == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!pendingEmotes.containsKey(sender)) {
            pendingEmotes.put(sender, new HashMap<>());
        }

        pendingEmotes.values().forEach(emotes -> emotes.put(sender.getName(), emote));
        pendingPolling.forEach((poller, polling) -> {
            if (pendingEmotes.containsKey(poller)) {
                polling.complete(pendingEmotes.get(poller));
            } else {
                polling.complete(new HashMap<>());
            }

            // Clear pending emotes
            pendingEmotes.put(poller, new HashMap<>());
        });
        pendingPolling.clear();

        return ResponseEntity.ok().build();
    }

    /**
     * Get all pending emotes for the sender using long polling. Once an emote has been polled, polling again will not
     * include this emote anymore.
     *
     * @param request The http servlet request with the sender's session information
     * @return A deferred result which will contain all emotes since the last poll
     */
    @GetMapping("/poll")
    public CompletableFuture<Map<String, String>> poll(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        IngamePlayer poller = (IngamePlayer) session.getAttribute("user");

        // Close any concurrent polling requests, as we don't want to send any double data.
        if (pendingPolling.containsKey(poller)) {
            pendingPolling.get(poller).complete(new HashMap<>());
        }

        if (!pendingEmotes.containsKey(poller)) {
            pendingEmotes.put(poller, new HashMap<>());
        }
        Map<String, String> emotes = pendingEmotes.get(poller);

        if (!emotes.isEmpty()) {
            pendingEmotes.put(poller, new HashMap<>());
            return CompletableFuture.completedFuture(emotes);
        }



        CompletableFuture<Map<String, String>> completableFuture = new CompletableFuture<>();

        pendingPolling.put(poller, completableFuture);

        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(this.pollTimeout);
            } catch (InterruptedException e) {}

            completableFuture.complete(new HashMap<>());
            pendingPolling.remove(poller, completableFuture);
        }).start();

        return completableFuture;
    }

}
