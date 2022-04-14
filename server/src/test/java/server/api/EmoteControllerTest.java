package server.api;

import commons.IngamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmoteControllerTest {

    private EmoteController controller;
    private HttpServletRequest request;
    private HttpSession session;

    private long pollTimeout = 250;

    @BeforeEach
    public void setup() {
        controller = new EmoteController(pollTimeout);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);

        IngamePlayer player = new IngamePlayer("Jeroen");
        session.setAttribute("user", player);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(player);

        assertSame(player, session.getAttribute("user"));
    }

    @Test
    void pollDoesTimeout() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Map<String, String>> result = controller.poll(request);

        Map<String, String> emotes = result.get(2 * pollTimeout, MILLISECONDS);
        assertTrue(() -> emotes.isEmpty());
    }

    @Test
    void send() {
        assertEquals(HttpStatus.OK, controller.send(request, "HEART").getStatusCode());
    }

    @Test
    void sendAndPollIsDone() {
        assertEquals(HttpStatus.OK, controller.send(request, "HEART").getStatusCode());

        CompletableFuture<Map<String, String>> result = controller.poll(request);

        assertTrue(() -> result.isDone());
    }

    @Test
    void sendCompletesPendingPoll() {
        CompletableFuture<Map<String, String>> poll = controller.poll(request);

        assertFalse(poll.isDone());

        controller.send(request, "HEART");

        assertTrue(poll.isDone());
    }

    @Test
    void sendThenPollImmediatelyCompleted() {
        controller.send(request, "HEART");

        CompletableFuture<Map<String, String>> poll = controller.poll(request);

        assertTrue(poll.isDone());
    }

    @Test
    void sendThenPollGivesSentEmote() throws ExecutionException, InterruptedException {
        controller.send(request, "HEART");

        CompletableFuture<Map<String, String>> poll = controller.poll(request);

        Map<String, String> expectedEmotes = new HashMap<>();
        expectedEmotes.put("Jeroen", "HEART");
        assertEquals(expectedEmotes, poll.get());
    }

    @Test
    void sendTwiceThenPollGivesSentEmote() throws ExecutionException, InterruptedException {
        controller.send(request, "HEART");
        controller.send(request, "ANGRY");

        CompletableFuture<Map<String, String>> poll = controller.poll(request);

        Map<String, String> expectedEmotes = new HashMap<>();
        expectedEmotes.put("Jeroen", "ANGRY");
        assertEquals(expectedEmotes, poll.get());
    }

    @Test
    void pollThenSendGivesSentEmote() throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, String>> poll = controller.poll(request);

        controller.send(request, "HEART");

        Map<String, String> expectedEmotes = new HashMap<>();
        expectedEmotes.put("Jeroen", "HEART");
        assertEquals(expectedEmotes, poll.get());
    }

    @Test
    void pollSendPollTimeoutRemovesSecondPollRaceCondition() throws InterruptedException {
        // Poll first, so there aren't any pending emotes yet and a timeout will be queued.
        CompletableFuture<Map<String, String>> firstPoll = controller.poll(request);

        // Send an emote, so the first poll is completed. The timeout will still be running.
        controller.send(request, "HEART");
        assertTrue(() -> firstPoll.isDone());

        // Sleep until the timeout is almost over.
        Thread.sleep(pollTimeout * 2 / 3);

        // Poll a second time. This one should time out.
        CompletableFuture<Map<String, String>> secondPoll = controller.poll(request);

        // Sleep until the first timeout is over but the second is still running.
        Thread.sleep(pollTimeout * 2 / 3);

        // Sending an emote should immediately complete the second poll.
        controller.send(request, "HEART");
        assertTrue(() -> secondPoll.isDone());
    }
}
