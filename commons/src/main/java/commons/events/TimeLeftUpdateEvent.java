package commons.events;

public class TimeLeftUpdateEvent extends Event {

    private final double timeLeft;

    /**
     * Create a new TimeLeftUpdateEvent.
     *
     * @param timeLeft The updated amount of time left to answer the question
     */
    public TimeLeftUpdateEvent(double timeLeft) {
        this.timeLeft = timeLeft;
    }

    /**
     * Get the updated amount of time left to answer the question.
     *
     * @return The updated time left
     */
    public double getTimeLeft() {
        return timeLeft;
    }

}
