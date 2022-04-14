package client.event;

import client.utils.ServerUtils;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import commons.GsonConfig;
import commons.events.Event;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class EventManager implements EventListener {

    private final ServerUtils serverUtils;
    private final EventBus eventBus;
    private final GsonConfig gsonConfig;

    private final AtomicBoolean isPollingEvents = new AtomicBoolean(false);

    /**
     * Create a new EventManager. It is recommended to use dependency injection.
     * @param serverUtils The ServerUtils instance
     * @param eventBus The EventBus instance
     * @param gsonConfig The GsonConfig instance
     */
    @Inject
    public EventManager(ServerUtils serverUtils, EventBus eventBus, GsonConfig gsonConfig) {
        this.serverUtils = serverUtils;
        this.eventBus = eventBus;
        this.gsonConfig = gsonConfig;
    }

    /**
     * Start the loop that polls for pending events.
     */
    public void startPollingEvents() {
        // Prevent concurrent polling
        if (isPollingEvents.get()) {
            return;
        }

        isPollingEvents.set(true);

        pollEvents();
    }

    /**
     * Stop the loop that polls for pending events.
     */
    public void stopPollingEvents() {
        isPollingEvents.set(false);
    }

    /**
     * Immediately poll for pending events. Automatically polls again when finished and {@link #isPollingEvents} is
     * true.
     */
    private void pollEvents() {
        new Thread(() -> {
            try {
                if (!isPollingEvents.get()) {
                    return;
                }

                sendPollRequest().forEach(eventBus::post);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pollEvents();
            }
        }).start();
    }

    /**
     * Send a request to the server to get all new events since the last poll using long polling.
     *
     * @return A list of new events
     */
    private List<Event> sendPollRequest() {
        System.out.println("Polling for events using cookie " + serverUtils.getSessionCookie());

        Response response = serverUtils.getClient()
                .target(serverUtils.getServerUrl()).path("api/events/poll")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(serverUtils.getSessionCookie())
                .get();

        if (response.getStatusInfo().getFamily() == Response.Status.Family.CLIENT_ERROR) {
            int statusCode = response.getStatusInfo().getStatusCode();
            String reasonPhrase = response.getStatusInfo().getReasonPhrase();

            System.out.println("Polling for events failed: " + statusCode + " " + reasonPhrase
                    + (response.hasEntity() ? ": " + response.readEntity(String.class) : ""));

            // Wait some time to prevent spamming requests if they keep failing
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return new ArrayList<>();
        }

        return response.readEntity(new GenericType<List<Event>>() {});
    }

    /**
     * Get the EventBus used for events.
     *
     * @return The EventBus
     */
    public EventBus getEventBus() {
        return eventBus;
    }
}
