package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstimationQuestionTest {

    @Test
    void generateQuestion() {
        EstimationQuestion estimationQuestion =
                EstimationQuestion.generateQuestion(new Activity("Using the dish washer", 69));
        assertEquals("Estimate the energy consumption of this activity!\n\n"
                        + "Using the dish washer",
                estimationQuestion.text);
        assertEquals(69, estimationQuestion.answer);
    }

    @Test
    void giveAmountOfPoints() {
        EstimationQuestion estimationQuestion = new EstimationQuestion("How much?", 69);
        assertEquals(55, estimationQuestion.giveAmountOfPoints(69, 0.5));
        assertEquals(100, estimationQuestion.giveAmountOfPoints(69, 1));
        assertEquals(17, estimationQuestion.giveAmountOfPoints(0, 0.5));
        assertEquals(0, estimationQuestion.giveAmountOfPoints(1000, 0.5));
        assertEquals(0, estimationQuestion.giveAmountOfPoints(-1000, 0.5));
        assertEquals(99, estimationQuestion.giveAmountOfPoints(70, 1));
        assertEquals(55, estimationQuestion.giveAmountOfPoints(70, 0.5));
    }

    @Test
    void answerAccuracy() {
        EstimationQuestion estimationQuestion = new EstimationQuestion("How much?", 69);

        assertEquals(74, estimationQuestion.answerAccuracy(43));
        assertEquals(0, estimationQuestion.answerAccuracy(1000));
        assertEquals(0, estimationQuestion.answerAccuracy(-1000));
        assertEquals(100, estimationQuestion.answerAccuracy(69));
        assertEquals(31, estimationQuestion.answerAccuracy(0));
    }

    @Test
    void getImagePathTest() {
        EstimationQuestion estimationQuestion = new EstimationQuestion("How much?",
                69, "somewhere");
        assertEquals("somewhere", estimationQuestion.getImagePath());
    }
}
