package commons.events;

import commons.Joker;
import commons.Question;

import java.util.Set;

public class NewQuestionEvent extends Event {

    private Question question;
    private Set<Joker> disabledJokers;
    private int questionIndex;

    /**
     * Create a new NewQuestionEvent with a Question.
     *
     * @param question      The quiz Question
     * @param disabledJokers The set of disabled jokers
     * @param questionIndex The index of the current question
     */
    public NewQuestionEvent(Question question, Set<Joker> disabledJokers, int questionIndex) {
        this.question = question;
        this.questionIndex = questionIndex;
        this.disabledJokers = disabledJokers;
    }

    /**
     * Get the question of the NewQuestionEvent.
     *
     * @return The question
     */
    public Question getQuestion() {
        return question;
    }


    /**
     * Get the index of the question from the NewQuestionEvent.
     *
     * @return The index
     */
    public int getQuestionIndex() {
        return questionIndex;
    }
    /**
     * Get the set of disabled jokers.
     *
     * @return The set of disabled jokers
     */
    public Set<Joker> getDisabledJokers() {
        return disabledJokers;
    }
}
