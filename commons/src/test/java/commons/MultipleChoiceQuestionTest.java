package commons;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MultipleChoiceQuestionTest {

    @Test
    void generateVariants() {
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion("text", 69);
        long[] variants = multipleChoiceQuestion.getVariants();
        assertEquals(3, variants.length);
        boolean ok = false;
        for (int i = 0; i < variants.length; i++) {
            if (variants[i] == 69) {
                ok = true;
            }
        }
        assertTrue(ok);
    }

    @RepeatedTest(1000)
    void generateRangeTest() {
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion("text", 213);
        long[] variants = multipleChoiceQuestion.getVariants();
        Set<Long> answersPool = new HashSet<>();
        for (int i = 0; i < variants.length; i++) {
            answersPool.add(variants[i]);
        }
        assertEquals(answersPool.size(), 3); // test no duplicate
        // WrongAnswer are generated with 3*Math.random()*(correctanswer)
        for (int i = 0; i < variants.length; i++) {
            double tmp = variants[i];
            System.out.println(tmp);
            assertTrue((tmp / (multipleChoiceQuestion.answer + 2)) <= 3
                    && (tmp / (multipleChoiceQuestion.answer + 2)) >= 0);
        }
    }

    @Test
    void randomVariant() {
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion("text", 69);
        long[] variants = multipleChoiceQuestion.getVariants();
        assertEquals(3, variants.length);
        boolean ok = false;
        for (int i = 0; i < variants.length; i++) {
            if (variants[i] != 69) {
                ok = true;
            }
        }
        assertTrue(ok);
    }

    @Test
    void trailingZeroTest() {
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion("text", 2310);
        long[] variants = multipleChoiceQuestion.getVariants();
        long[] trailingZero = new long[3];
        int correctIndex = -1; // I have no reference to the index of the correct answer.

        for (int i = 0; i < variants.length; i++) {
            if (variants[i] == multipleChoiceQuestion.answer) {
                correctIndex = i;
            }
            long trailingCounter = 0;
            String numberString = Long.toString(variants[0]);
            for (int j = numberString.length(); j >= 0; j--) {
                if (numberString.charAt(0) == '0') {
                    trailingCounter++;
                }
                break;
            }
            trailingZero[i] = trailingCounter;
        }

        for (int i = 0; i < variants.length; i++) {
            if (i == correctIndex) {
                continue;
            }
            assertTrue(trailingZero[i] >= trailingZero[correctIndex]);
            // if they are in the similar fashion,
            // the wrong answer must have at least the same amount of trailing zero's.
        }
    }

    @Test
    void generateQuestion() {
        MultipleChoiceQuestion multipleChoiceQuestion = MultipleChoiceQuestion.generateQuestion(
                new Activity("using the phone", 420));
        assertEquals("How much energy are consumed by this activity?\n\n"
                + "using the phone", multipleChoiceQuestion.text);
        assertEquals(420, multipleChoiceQuestion.answer);
    }

    @Test
    void getVariants() {
        MultipleChoiceQuestion multipleChoiceQuestion = MultipleChoiceQuestion.generateQuestion(
                new Activity("using the phone", 420));
        assertEquals(3, multipleChoiceQuestion.getVariants().length);
    }

    @Test
    void getNumberOfVariants() {
        MultipleChoiceQuestion multipleChoiceQuestion
                = new MultipleChoiceQuestion("using the phone", 420);
        assertEquals(3, multipleChoiceQuestion.getNumberOfVariants());
    }

    @Test
    void setVariants() {
        MultipleChoiceQuestion multipleChoiceQuestion
                = new MultipleChoiceQuestion("using the phone", 420);
        long[] aux = {69, 420, 1024, 42};
        multipleChoiceQuestion.setVariants(aux);
        assertEquals(4, multipleChoiceQuestion.getVariants().length);
    }

    @Test
    void testEquals() {
        MultipleChoiceQuestion multipleChoiceQuestion
                = new MultipleChoiceQuestion("using the phone", 420);
        MultipleChoiceQuestion multipleChoiceQuestion3
                = new MultipleChoiceQuestion("using the 2312312phone", 420);

        assertEquals(multipleChoiceQuestion, multipleChoiceQuestion);
        assertNotEquals(multipleChoiceQuestion, multipleChoiceQuestion3);

    }

    @Test
    void testToString() {
        MultipleChoiceQuestion multipleChoiceQuestion
                = MultipleChoiceQuestion.generateQuestion(
                new Activity("using the phone", 420));
        System.out.println(multipleChoiceQuestion.toString());
        assertTrue(multipleChoiceQuestion.toString().contains("How much energy are consumed "
                + "by this activity?\n\n"));
    }

    @Test
    void testGiveAmountOfPoints() {
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion("How much?", 1);
        assertEquals(0, multipleChoiceQuestion.giveAmountOfPoints(0, 1));
        assertEquals(0, multipleChoiceQuestion.giveAmountOfPoints(1, 0));
        assertEquals(100, multipleChoiceQuestion.giveAmountOfPoints(1, 1));
        assertEquals(55, multipleChoiceQuestion.giveAmountOfPoints(1, 0.5));
    }
}
