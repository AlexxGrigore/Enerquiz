package commons;

import java.util.Objects;

public abstract class Question {

    protected String text;
    protected long answer;

    /**
     * Empty constructor.
     */
    public Question() {
        text = "";
        answer = 0;
    }

    /**
     * The default constructor.
     *
     * @param text   the text of the question
     * @param answer the answer of the question
     */
    public Question(String text, long answer) {
        this.text = text;
        this.answer = answer;
    }

    /**
     * Getter for the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Getter for the answer.
     *
     * @return the answer
     */
    public long getAnswer() {
        return answer;
    }

    /**
     * Set the text.
     *
     * @param text the new value
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Set the answer.
     *
     * @param answer the new value
     */
    public void setAnswer(int answer) {
        this.answer = answer;
    }

    /**
     * Checks if a given answer is correct or not.
     *
     * @param givenAnswer the given answer
     * @return true iff it's corect
     */
    public boolean checkAnswer(long givenAnswer) {
        return answer == givenAnswer;
    }

    /**
     * This method returns the amount of points that should be assigned.
     * The number of points in the interval [0,100]
     *
     * @param answer the player's answer
     * @param time   time (as a double in the interval[0,1])
     * @return the amount of points
     */
    public abstract int giveAmountOfPoints(long answer, double time);

    /**
     * Compares a given object with the current object.
     *
     * @param o the object with which we compare
     * @return true iff they have the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question = (Question) o;
        return answer == question.answer && Objects.equals(text, question.text);
    }

    /**
     * Hasing function (is completely useless for us, but the checkstyle forces me to put it).
     *
     * @return the hash value
     */
    @Override
    public int hashCode() {
        return Objects.hash(text, answer);
    }

    /**
     * Creates a human-readable form of the object.
     *
     * @return the string that contains the human-readable form
     */
    @Override
    public String toString() {
        return "Question{"
                + "text='" + text + '\''
                + ", answer=" + answer + '}';
    }

}
