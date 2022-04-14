package commons.events;

import commons.Answer;

import java.util.List;

public class ShowAnswersEvent extends Event {

    private final List<Answer> submittedAnswers;

    /**
     * Create a new ShowAnswersEvent with the answers submitted by players.
     *
     * @param submittedAnswers A map of players to their submitted answers
     */
    public ShowAnswersEvent(List<Answer> submittedAnswers) {
        this.submittedAnswers = submittedAnswers;
    }

    /**
     * Get the answers submitted by players.
     *
     * @return A map of players to their submitted answers
     */
    public List<Answer> getSubmittedAnswers() {
        return submittedAnswers;
    }

}
