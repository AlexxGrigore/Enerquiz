package server.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class LongPoll<K, V> {

    private final Map<K, List<V>> pendingMessages = new HashMap<>();
    private final Map<K, CompletableFuture<List<V>>> pendingPolling = new HashMap<>();

    private long pollTimeout = 10_000L;

    /**
     * Create a new LongPoll with a timeout of 10 seconds.
     *
     * @param <K> The type of the key used to keep track of pending messages
     * @param <T> The type of the messages
     */
    public <K, T> LongPoll() {}

    /**
     * Create a new LongPoll with a timeout.
     *
     * @param pollTimeout The timeout in milliseconds after which polling requests will be forcefully responded to
     * @param <K> The type of the key used to keep track of pending messages
     * @param <T> The type of the messages
     */
    public <K, T> LongPoll(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }



    /**
     * Start tracking messages for a given key.
     *
     * @param key The key to start tracking messages for
     */
    public void startTracking(K key) {
        if (!pendingMessages.containsKey(key)) {
            pendingMessages.put(key, new ArrayList<>());
        }
    }

    /**
     * Stop tracking messages for a given key.
     *
     * @param key The key to stop tracking messages for
     */
    public void stopTracking(K key) {
        pendingMessages.remove(key);

        CompletableFuture<List<V>> pending = pendingPolling.remove(key);
        if (pending != null) {
            pending.complete(new ArrayList<>());
        }
    }

    /**
     * Get all keys that messages are being tracked for.
     *
     * @return The set of all keys
     */
    public Set<K> getTracking() {
        return pendingMessages.keySet();
    }



    /**
     * Add a message to all tracked keys and complete all pending polls.
     *
     * @param value The message
     */
    public void reply(V value) {
        replySilently(value);
        completeAll();
    }

    /**
     * Add a collection of messages to all tracked keys and complete all pending polls.
     *
     * @param values The collection of messages
     */
    public void reply(Collection<V> values) {
        replySilently(values);
        completeAll();
    }

    /**
     * Add a message to the tracked key and complete the pending poll, if present.
     *
     * @param key The tracked key to reply the message to
     * @param value The message
     */
    public void reply(K key, V value) {
        replySilently(key, value);
        complete(key);
    }

    /**
     * Add a message to all tracked keys without completing pending polls.
     *
     * @param value The message
     */
    public void replySilently(V value) {
        pendingMessages.forEach((key, messages) -> messages.add(value));
    }

    /**
     * Add a collection of messages to all tracked keys without completing pending polls.
     *
     * @param values The collection of messages
     */
    public void replySilently(Collection<V> values) {
        pendingMessages.forEach((key, messages) -> messages.addAll(values));
    }

    /**
     * Add a message to the tracked key without completing a possible pending poll.
     *
     * @param key The tracked key to reply the message to
     * @param value The message
     */
    public void replySilently(K key, V value) {
        if (!pendingMessages.containsKey(key)) {
            throw new IllegalArgumentException("Key " + key + "is not being tracked!");
        }

        pendingMessages.get(key).add(value);
    }

    /**
     * Complete the pending poll for a key, if present.
     *
     * @param key The key to complete the possible pending poll for
     */
    public void complete(K key) {
        if (!pendingMessages.containsKey(key)) {
            throw new IllegalArgumentException("Key " + key + "is not being tracked!");
        }

        if (!pendingPolling.containsKey(key)) {
            return;
        }

        CompletableFuture<List<V>> completableFuture = pendingPolling.get(key);
        completableFuture.complete(pendingMessages.get(key));

        pendingPolling.remove(key, completableFuture);
        pendingMessages.put(key, new ArrayList<>());
    }

    /**
     * Complete all pending polls.
     */
    public void completeAll() {
        pendingPolling.forEach((key, completableFuture) -> {
            completableFuture.complete(pendingMessages.get(key));
            pendingMessages.put(key, new ArrayList<>());
        });
        pendingPolling.clear();
    }

    /**
     * Get all pending messages for all keys.
     *
     * @return The map of keys to the list of pending messages
     */
    public Map<K, List<V>> getPendingMessages() {
        return pendingMessages;
    }

    /**
     * Get all pending messages for a specific key.
     *
     * @param key The key to get the pending messages of
     * @return The list of pending messages
     */
    public List<V> getPendingMessages(K key) {
        return pendingMessages.get(key);
    }



    /**
     * Get all pending messages for the key using long polling. If there are pending messages, the completable future
     * will immediately be completed. Otherwise, the completable future will be completed as soon as new messages are
     * available or the time runs out. Once a message has been polled for a specific key, polling again will not
     * include this message anymore.
     *
     * @param key The key to get all pending messages for
     * @param mustBeTracking When true, only poll if the key is being tracked. Always polls when false.
     * @return A completable future of a list of all incoming messages. Returns null when mustBeTracking is true and
     *         the key is not being tracked.
     */
    public CompletableFuture<List<V>> poll(K key, boolean mustBeTracking) {
        if (!pendingMessages.containsKey(key)) {
            if (mustBeTracking) {
                return null;
            } else {
                startTracking(key);
            }
        }


        List<V> messages = pendingMessages.get(key);
        if (!messages.isEmpty()) {
            pendingMessages.put(key, new ArrayList<>());
            return CompletableFuture.completedFuture(messages);
        }


        CompletableFuture<List<V>> completableFuture = new CompletableFuture<>();
        pendingPolling.put(key, completableFuture);

        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(this.pollTimeout);
            } catch (InterruptedException e) {}

            completableFuture.complete(new ArrayList<>());
            pendingPolling.remove(key, completableFuture);
        }).start();

        return completableFuture;
    }

}
