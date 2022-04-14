package server.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class LongPollTest {

    private LongPoll<Integer, String> longPoll;

    private long pollTimeout = 250;

    @BeforeEach
    public void setup() {
        longPoll = new LongPoll<Integer, String>(pollTimeout);
    }

    @Test
    void startTracking() {
        assertTrue(longPoll.getTracking().isEmpty());

        longPoll.startTracking(0);
        longPoll.startTracking(1);
        longPoll.startTracking(2);

        assertTrue(longPoll.getTracking().contains(0));
        assertTrue(longPoll.getTracking().contains(1));
        assertTrue(longPoll.getTracking().contains(2));

        assertEquals(3, longPoll.getTracking().size());
    }

    @Test
    void startTrackingDuplicateKeys() {
        assertTrue(longPoll.getTracking().isEmpty());

        longPoll.startTracking(0);
        assertEquals(1, longPoll.getTracking().size());
        longPoll.startTracking(0);
        assertEquals(1, longPoll.getTracking().size());
    }

    @Test
    void stopTracking() {
        longPoll.startTracking(0);
        assertTrue(longPoll.getTracking().contains(0));

        longPoll.stopTracking(0);
        assertTrue(longPoll.getTracking().isEmpty());
    }

    @Test
    void stopTrackingRedundant() {
        assertTrue(longPoll.getTracking().isEmpty());

        longPoll.stopTracking(0);
        assertTrue(longPoll.getTracking().isEmpty());

        longPoll.startTracking(0);
        assertTrue(longPoll.getTracking().contains(0));

        longPoll.stopTracking(0);
        assertTrue(longPoll.getTracking().isEmpty());
    }

    @Test
    void getTracking() {
        assertEquals(Set.of(), longPoll.getTracking());

        longPoll.startTracking(0);
        longPoll.startTracking(1);
        longPoll.startTracking(2);

        assertEquals(Set.of(0, 1, 2), longPoll.getTracking());
    }

    @Test
    void reply() {
        longPoll.reply("Hello");
    }

    @Test
    void pollDoesTimeout() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        List<String> messages = poll.get(2 * pollTimeout, TimeUnit.MILLISECONDS);
        assertTrue(messages.isEmpty());
    }

    @Test
    void pollDoesTimeoutMultiple() throws InterruptedException {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(0, false);

        Thread.sleep(2 * pollTimeout);

        assertTrue(poll0.isDone());
        assertTrue(poll1.isDone());
        assertTrue(poll2.isDone());
    }

    @Test
    void replyBeforeTrackingNotDone() {
        longPoll.reply("Hello");

        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        assertFalse(poll.isDone());
    }

    @Test
    void replyThenPollImmediatelyCompleted() {
        longPoll.startTracking(0);
        longPoll.reply("Hello");

        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        assertTrue(poll.isDone());
    }

    @Test
    void replyCompletesPendingPoll() {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        assertFalse(poll.isDone());

        longPoll.reply("Hello");

        assertTrue(poll.isDone());
    }

    @Test
    void replyThenPollGivesSentMessage() throws ExecutionException, InterruptedException {
        longPoll.startTracking(0);
        longPoll.reply("Hello");

        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        assertEquals(List.of("Hello"), poll.get());
    }

    @Test
    void replyTwiceThenPollGivesSentMessages() throws ExecutionException, InterruptedException {
        longPoll.startTracking(0);
        longPoll.reply("Hello");
        longPoll.reply("World");

        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        assertEquals(List.of("Hello", "World"), poll.get());
    }

    @Test
    void pollThenReplyGivesSentMessage() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.reply("Hello");

        assertEquals(List.of("Hello"), poll.get());
    }

    @Test
    void pollThenReplyCollectionGivesSentMessages() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.reply(List.of("Hello", "World"));

        assertEquals(List.of("Hello", "World"), poll.get());
    }

    @Test
    void pollReplyPollTimeoutRemovesSecondPollRaceCondition() throws InterruptedException {
        // Poll first, so there aren't any pending messages yet and a timeout will be queued.
        CompletableFuture<List<String>> firstPoll = longPoll.poll(0, false);

        // Reply with a message, so the first poll is completed. The timeout will still be running.
        longPoll.reply("Hello");
        assertTrue(() -> firstPoll.isDone());

        // Sleep until the timeout is almost over.
        Thread.sleep(pollTimeout * 2 / 3);

        // Poll a second time. This one should time out.
        CompletableFuture<List<String>> secondPoll = longPoll.poll(0, false);

        // Sleep until the first timeout is over but the second is still running.
        Thread.sleep(pollTimeout * 2 / 3);

        // Replying should immediately complete the second poll.
        longPoll.reply("Hello");
        assertTrue(() -> secondPoll.isDone());
    }

    @Test
    void completeAllDone() {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.completeAll();

        assertTrue(poll0.isDone());
        assertTrue(poll1.isDone());
        assertTrue(poll2.isDone());
    }

    @Test
    void completeAllEmpty() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.completeAll();

        assertTrue(poll0.get().isEmpty());
        assertTrue(poll1.get().isEmpty());
        assertTrue(poll2.get().isEmpty());
    }

    @Test
    void replyGivesAllSentMessage() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.reply("Hello");

        assertEquals(List.of("Hello"), poll0.get());
        assertEquals(List.of("Hello"), poll1.get());
        assertEquals(List.of("Hello"), poll2.get());
    }

    @Test
    void replyCollectionGivesAllSentMessages() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.reply(List.of("Hello", "World"));

        assertEquals(List.of("Hello", "World"), poll0.get());
        assertEquals(List.of("Hello", "World"), poll1.get());
        assertEquals(List.of("Hello", "World"), poll2.get());
    }

    @Test
    void pollMustBeTrackingTrueWhenNotTrackingReturnsNull() {
        assertNull(longPoll.poll(0, true));
    }

    @Test
    void pollMustBeTrackingTrueWhenTrackingReturnsNotNull() {
        longPoll.startTracking(0);
        assertNotNull(longPoll.poll(0, true));
    }

    @Test
    void stopTrackingCompletesPendingPoll() {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.stopTracking(0);

        assertTrue(poll.isDone());
    }

    @Test
    void stopTrackingPendingPollReturnsEmpty() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.stopTracking(0);

        assertTrue(poll.get().isEmpty());
    }

    @Test
    void replySilentlyDoesNotComplete() {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently("Hello");

        assertFalse(poll.isDone());
    }

    @Test
    void replySilentlyCollectionDoesNotComplete() {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently(List.of("Hello", "World"));

        assertFalse(poll.isDone());
    }

    @Test
    void replySilentlyThenReplyGivesAllMessages() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently("Hello");
        longPoll.reply("World");

        assertEquals(List.of("Hello", "World"), poll.get());
    }

    @Test
    void replySilentlyCollectionThenReplyGivesAllMessages() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently(List.of("Hello", "World"));
        longPoll.reply("!");

        assertEquals(List.of("Hello", "World", "!"), poll.get());
    }

    @Test
    void pollThenReplyKeyValueGivesMessageForKey() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.reply(0, "Hello");

        assertEquals(List.of("Hello"), poll.get());
    }

    @Test
    void pollThenReplyKeyValueCompletesForKeyOnly() {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.reply(0, "Hello");

        assertTrue(poll0.isDone());
        assertFalse(poll1.isDone());
        assertFalse(poll2.isDone());
    }

    @Test
    void replySilentlyKeyValueThenReplyKeyValueGivesMessageForKey() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently(0, "Hello");
        longPoll.reply(0, "World");

        assertEquals(List.of("Hello", "World"), poll.get());
    }

    @Test
    void replySilentlyKeyValueThenReplyKeyValueGivesMessagesForKeyOnly()
            throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.replySilently(0, "Hello");
        longPoll.reply(0, "World");

        assertEquals(List.of("Hello", "World"), poll0.get());
        assertFalse(poll1.isDone());
        assertFalse(poll2.isDone());
    }

    @Test
    void pollThenCompleteKeyValueCompletesForKey() throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.complete(0);

        assertTrue(poll.isDone());
    }

    @Test
    void pollThenCompleteKeyValueCompletesForKeyOnly() {
        CompletableFuture<List<String>> poll0 = longPoll.poll(0, false);
        CompletableFuture<List<String>> poll1 = longPoll.poll(1, false);
        CompletableFuture<List<String>> poll2 = longPoll.poll(2, false);

        longPoll.complete(0);

        assertTrue(poll0.isDone());
        assertFalse(poll1.isDone());
        assertFalse(poll2.isDone());
    }

    @Test
    void getPendingMessagesKeyEmpty() {
        longPoll.startTracking(0);

        assertTrue(longPoll.getPendingMessages(0).isEmpty());
    }

    @Test
    void getPendingMessagesKeyNotEmpty() {
        longPoll.startTracking(0);

        longPoll.replySilently("Hello");

        assertFalse(longPoll.getPendingMessages(0).isEmpty());
    }

    @Test
    void getPendingMessagesKeySentMessage() {
        longPoll.startTracking(0);

        longPoll.replySilently("Hello");

        assertEquals(List.of("Hello"), longPoll.getPendingMessages(0));
    }

    @Test
    void getPendingMessagesAllKeysEmpty() {
        longPoll.startTracking(0);
        longPoll.startTracking(1);
        longPoll.startTracking(2);

        assertTrue(longPoll.getPendingMessages(0).isEmpty());
        assertTrue(longPoll.getPendingMessages(1).isEmpty());
        assertTrue(longPoll.getPendingMessages(2).isEmpty());
    }

    @Test
    void getPendingMessagesAllKeysNotEmpty() {
        longPoll.startTracking(0);
        longPoll.startTracking(1);
        longPoll.startTracking(2);

        longPoll.replySilently("Hello");

        assertFalse(longPoll.getPendingMessages(0).isEmpty());
        assertFalse(longPoll.getPendingMessages(1).isEmpty());
        assertFalse(longPoll.getPendingMessages(2).isEmpty());
    }

    @Test
    void getPendingMessagesAllKeysSentMessage() {
        longPoll.startTracking(0);
        longPoll.startTracking(1);
        longPoll.startTracking(2);

        longPoll.replySilently("Hello");

        assertEquals(List.of("Hello"), longPoll.getPendingMessages(0));
        assertEquals(List.of("Hello"), longPoll.getPendingMessages(1));
        assertEquals(List.of("Hello"), longPoll.getPendingMessages(2));
    }

    @Test
    void pollThenReplyKeyClearsPendingMessages() {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently(0, "Hello");
        longPoll.reply(0, "World");

        assertTrue(longPoll.getPendingMessages(0).isEmpty());
    }

    @Test
    void pollTheneplyAllClearsPendingMessages() {
        CompletableFuture<List<String>> poll = longPoll.poll(0, false);

        longPoll.replySilently("Hello");
        longPoll.reply("World");

        assertTrue(longPoll.getPendingMessages(0).isEmpty());
    }
}
