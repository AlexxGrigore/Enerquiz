package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void getText() {
        Question question = new EstimationQuestion("text", 69);
        assertEquals("text", question.getText());
    }

    @Test
    void getAnswer() {
        Question question = new EstimationQuestion("text", 69);
        assertEquals(69, question.getAnswer());
    }

    @Test
    void setText() {
        Question question = new EstimationQuestion("text", 69);
        question.setText("nope");
        assertEquals("nope", question.getText());
    }

    @Test
    void setAnswer() {
        Question question = new EstimationQuestion("text", 69);
        question.setAnswer(420);
        assertEquals(420, question.getAnswer());
    }

    @Test
    void checkAnswer() {
        Question question = new EstimationQuestion("text", 69);

        assertFalse(question.checkAnswer(420));
        assertTrue(question.checkAnswer(69));
    }

    @Test
    void testEquals() {
        Question question = new EstimationQuestion("text", 69);
        Question question2 = new EstimationQuestion("text", 69);
        Question question3 = new EstimationQuestion("texty", 692);

        assertEquals(question, question);
        assertEquals(question, question2);
        assertNotEquals(question, question3);
    }

    @Test
    void testToString() {
        Question question = new EstimationQuestion("text", 69);
        assertEquals("Question{text='text', answer=69}", question.toString());
    }

    @Test
    void giveAmountOfPoints() {
        Question question = new EstimationQuestion("How much?", 25);
        assertEquals(100, question.giveAmountOfPoints(25, 1));
        assertEquals(0, question.giveAmountOfPoints(25, 0));
    }
}
