package commons;


import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Objects;

public class MultipleChoiceQuestion extends Question {

    private long[] variants;
    private final int numberOfVariants = 3;
    private String imagePath;

    /**
     * Empty constructor.
     */
    public MultipleChoiceQuestion() {
        super();
        variants = new long[numberOfVariants];
    }

    /**
     * The default constructor.
     *
     * @param text   the text of the question
     * @param answer the answer of the question
     */
    public MultipleChoiceQuestion(String text, long answer) {
        super(text, answer);
        variants = new long[numberOfVariants];
        generateVariants();
    }

    /**
     * The constructor that is actually used, which has a String containing the image path.
     * @param text      the text of the question
     * @param answer    the answer of the question
     * @param imagePath the image of the question
     */
    public MultipleChoiceQuestion(String text, long answer, String imagePath) {
        super(text, answer);
        variants = new long[numberOfVariants];
        this.imagePath = imagePath;
        generateVariants();
    }

    /**
     * Generates the variants for the multiple choice.
     */
    public void generateVariants() {
        Random random = new Random();
        int correctVariant = random.nextInt(numberOfVariants);
        Set<Long> answers = new HashSet<>();
        answers.add(answer); // add the answer to the set, to get absolute no duplication.
        while (answers.size() != 4) { // 3 generated answers needed + 1 correct answer.
            answers.add(generateWrongAnswer(answer));
        }
        answers.remove(answer); // we don't need it in the set anymore after duplication is eliminated.
        Long[] tmp = answers.toArray(new Long[3]);
        for (int i = 0; i < variants.length; i++) {
            variants[i] = tmp[i];
        }
        variants[correctVariant] = answer;
    }

    /**
     * Generate wrong answers based on the correct answer.
     * This function is able to generate wrong answers that's hard to see difference compare to the
     * correct answer. By dealing with the trailing zero's.
     * @param number The correct answer, where the wrong answers are generated from.
     * @return A wrong Answer.
     */
    public long generateWrongAnswer(long number) {
        String numberToString = Long.toString(number);
        long trailingFactor = 1;

        // calculating the size of trailing 0 part of a number;
        for (int i = numberToString.length() - 1; i >= 0; i--) {
            if (numberToString.length() == 1) {
                break;
            }
            if (numberToString.charAt(i) == '0') {
                trailingFactor *= 10;
            } else {
                break; // end of trailing zero's.
            }
        }

        // add 2 to prevent the correct answer == 0;
        // This step calculate the part without the trailing 0's
        // Only apply randomness to this part to keep the generated wrong answer
        // are in the same fashion with the correct answer.
        long tmp = number / trailingFactor;
        if (tmp == 0) {
            tmp += 2;
        }
        tmp = Math.round(3 * Math.random() * tmp) * trailingFactor;
        return tmp;
    }

    /**
     * Generates a multiple choice question from a given activity.
     *
     * @param activity the activity
     * @return the estimation question
     */
    public static MultipleChoiceQuestion generateQuestion(Activity activity) {
        String questionText = "How much energy are consumed by this activity?\n\n"
                + activity.activityText;
        return new MultipleChoiceQuestion(questionText, activity.amountWh, activity.imagePath);
    }

    /**
     * Getter for the array of variants.
     *
     * @return the variants
     */
    public long[] getVariants() {
        return variants;
    }

    /**
     * Getter for the number of variants.
     *
     * @return the number of variants
     */
    public int getNumberOfVariants() {
        return numberOfVariants;
    }

    /**
     * Setter for the number of variants.
     *
     * @param variants the variants
     */
    public void setVariants(long[] variants) {
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
        MultipleChoiceQuestion that = (MultipleChoiceQuestion) o;
        return Arrays.equals(variants, that.variants);
    }

    /**
     * Having function (is completely useless for us, but the checkstyle forces me to put it).
     *
     * @return the hash value
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(numberOfVariants);
        result = 31 * result + Arrays.hashCode(variants);
        return result;
    }

    /**
     * Creates a human-readable form of the object.
     *
     * @return the string that contains the human-readable form
     */
    @Override
    public String toString() {
        return "MultipleChoiceQuestion{" + "variants=" + Arrays.toString(variants)
                + ", numberOfVariants=" + numberOfVariants + ", text='" + text + '\''
                + ", answer=" + answer + '}';
    }

    /**
     * Returns the amount of points that should be assigned.
     * If the time is 0 or the answer is wrong, then the player does not receive any points
     * The number of points are in the interval [0,100]
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

    /**
     * A getter to get the image path of the question.
     * @return The string containing the image path of the question.
     */
    public String getImagePath() {
        return this.imagePath;
    }
}
