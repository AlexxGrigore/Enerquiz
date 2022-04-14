package commons;

public class EstimationQuestion extends Question {

    private String imagePath;

    /**
     * Empty constructor.
     */
    public EstimationQuestion() {
        super();
    }

    /**
     * The default constructor.
     *
     * @param text   the text of the question
     * @param answer the answer of the question
     */
    public EstimationQuestion(String text, long answer) {
        super(text, answer);
    }

    /**
     * The constructor that is actually used, which has a String containing the image path.
     * @param text      the text of the question
     * @param answer    the answer of the question
     * @param imagePath the image of the question
     */
    public EstimationQuestion(String text, long answer, String imagePath) {
        super(text, answer);
        this.imagePath = imagePath;
    }

    /**
     * Generates an estimation question from a given activity.
     *
     * @param activity the activity
     * @return the estimation question
     */
    public static EstimationQuestion generateQuestion(Activity activity) {
        String questionText = "Estimate the energy consumption of this activity!\n\n"
                + activity.activityText;
        return new EstimationQuestion(questionText, activity.amountWh, activity.imagePath);
    }


    /**
     * The accuracy is given as a percentage. It's between 0% and 100%. If the answer is not relevant, it will be 0%.
     *
     * @param givenAnswer the given answer
     * @return the accuracy percentage
     */
    public int answerAccuracy(long givenAnswer) {
        long aux = answer;
        long p = 1;
        while (aux > 0) {
            aux /= 10;
            p *= 10;
        }
        float difference = Math.abs(givenAnswer - answer);
        difference /= p;
        difference *= 100;

        if (difference > 100) {
            return 0;
        }

        return 100 - ((int) difference);
    }

    /**
     * Returns the amount of points that should be assigned.
     * If the time is 0 or the answer is wrong, then the player does not receive any points.
     * The number of points is the interval [0,100].
     * It depends on the answer accuracy.
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

        //if the player answer in a reasonable amount if time, points will not be deducted
        if (time >= 0.9) {
            time = 1;
        } else {
            time *= 1 / 0.9;
        }
        long accuracy = answerAccuracy(answer);
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
