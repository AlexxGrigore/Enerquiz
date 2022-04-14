package commons;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MultipleActivitiesQuestion extends Question {

    private List<Activity> variants;
    private static final int NUMBER_OF_VARIANTS = 3;



    /**
     * The constructor.
     *
     * @param text     the text of the question
     * @param answer   the index if the correct variant
     * @param variants the list of variants
     */
    public MultipleActivitiesQuestion(String text, long answer, List<Activity> variants) {
        this.text = text;
        this.answer = answer;
        this.variants = variants;
    }

    /**
     * Generates a multiple choice question from a given activity. This type of questions is split in two other types:
     * 1)select the activity that uses the least amount of energy
     * 2)select the activity that uses the most amount of energy
     * The generator returns a question of this type,
     * but the decision regarding which one of those two should be selected is chosen randomly.
     *
     * @param activities an array of activities that has to contain at least 3 different activities
     * @param random     a random object
     * @return the estimation question
     */
    public static MultipleActivitiesQuestion generateQuestion(List<Activity> activities, Random random) {
        if (activities.size() < NUMBER_OF_VARIANTS) {
            throw new IllegalArgumentException();
        }

        int rand = random.nextInt(3);

        if (rand == 1) {
            String questionText = "Which of these activities consumes the MOST energy?";
            long answer = 0;
            long max = 0;
            int index = 0;
            for (int i = 0; i < NUMBER_OF_VARIANTS; i++) {
                if (activities.get(i).amountWh > max) {
                    max = activities.get(i).amountWh;
                    index = i;
                }
            }
            answer = index;

            return new MultipleActivitiesQuestion(questionText, answer, activities);
        }
        String questionText = "Which of these activities consumes the LEAST energy?";
        long answer;
        long min = Long.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < NUMBER_OF_VARIANTS; i++) {
            if (activities.get(i).amountWh < min) {
                min = activities.get(i).amountWh;
                index = i;
            }
        }
        answer = index;

        return new MultipleActivitiesQuestion(questionText, answer, activities);
    }

    /**
     * Getter for the list of variants.
     *
     * @return the list of variants
     */
    public List<Activity> getVariants() {
        return variants;
    }

    /**
     * Setter for the list of variants.
     *
     * @param variants the new list of variants
     */
    public void setVariants(List<Activity> variants) {
        this.variants = variants;
    }

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
        if (!super.equals(o)) {
            return false;
        }
        MultipleActivitiesQuestion that = (MultipleActivitiesQuestion) o;
        return Objects.equals(variants, that.variants);
    }

    /**
     * Hasing function (is completely useless for us, but the checkstyle forces me to put it).
     *
     * @return the hash value
     */
    @Override
    public int hashCode() {
        return Objects.hash(variants);
    }

    /**
     * Creates a human-readable form of the object.
     *
     * @return the string that contains the human-readable form
     */
    @Override
    public String toString() {
        return "MultipleActivitiesQuestion{" + "variants=" + variants
                + ", text='" + text + '\'' + ", answer=" + answer + '}';
    }

    /**
     * Returns the amount of points that should be assigned.
     * If the time is 0 or the answer is wrong, then the player does not receive any points
     * The number of points is the interval [0,100]
     *
     * @param answer the player's answer
     * @param time   time (as a double in the interval[0,1])
     * @return the amount of points
     */
    @Override
    public int giveAmountOfPoints(long answer, double time) {
        if (time == 0) {
            return 0;
        }
        if (!checkAnswer(answer)) {
            return 0;
        }
        double accuracy = 100;
        if (time >= 0.9) {
            time = 1;
        } else {
            time *= 1 / 0.9;
        }
        return (int) (accuracy * time);
    }


}
