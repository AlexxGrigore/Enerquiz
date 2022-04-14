package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MultipleActivitiesQuestionTest {

    private Activity a1;
    private Activity a2;
    private Activity a3;
    private Activity a4;
    private MultipleActivitiesQuestion multipleActivitiesQuestion;
    private ArrayList<Activity> activityList;

    @BeforeEach
    void setUp() {
        a1 = new Activity("wash", 69);
        a2 = new Activity("dry", 420);
        a3 = new Activity("blender", 69420);
        a4 = new Activity("blender", "somewhere", 69420);
        activityList = new ArrayList<>();
        activityList.add(a1);
        activityList.add(a2);
        activityList.add(a3);
        Random random = new Random(2);
        multipleActivitiesQuestion = MultipleActivitiesQuestion.generateQuestion(activityList, random);
    }

    @Test
    void generateQuestion() {
        assertTrue(multipleActivitiesQuestion.answer == 2);
    }

    @Test
    void getVariants() {
        assertEquals(3, multipleActivitiesQuestion.getVariants().size());
    }

    @Test
    void setVariants() {
        multipleActivitiesQuestion = new MultipleActivitiesQuestion("text", 3, null);
        multipleActivitiesQuestion.setVariants(activityList);
        assertEquals(3, multipleActivitiesQuestion.getVariants().size());
    }

    @Test
    void testEquals() {
        MultipleActivitiesQuestion multipleActivitiesQuestion =
                new MultipleActivitiesQuestion("text", 3, activityList);
        MultipleActivitiesQuestion multipleActivitiesQuestion2
                = new MultipleActivitiesQuestion("text", 3, activityList);
        MultipleActivitiesQuestion multipleActivitiesQuestion3
                = new MultipleActivitiesQuestion("text232", 23, null);

        assertEquals(multipleActivitiesQuestion, multipleActivitiesQuestion);
        assertEquals(multipleActivitiesQuestion, multipleActivitiesQuestion2);
        assertNotEquals(multipleActivitiesQuestion, multipleActivitiesQuestion3);

    }

    @Test
    void testToString() {
        multipleActivitiesQuestion = new MultipleActivitiesQuestion("text", 3, activityList);
        assertEquals("MultipleActivitiesQuestion{variants=[Activity{id=0, type='wash', amountWatts=69},"
                + " Activity{id=0, type='dry', amountWatts=420}, Activity{id=0, type='blender', amountWatts=69420}],"
                + " text='text', answer=3}", multipleActivitiesQuestion.toString());
    }

    @Test
    void giveAmountOfPoints() {
        List<Activity> variants = new ArrayList<>();
        variants.add(new Activity("cooking", 256));
        variants.add(new Activity("looking", 30));
        variants.add(new Activity("dancing", 66));
        MultipleActivitiesQuestion multipleActivitiesQuestion
                = new MultipleActivitiesQuestion("How much?", 1, variants);

        assertEquals(55, multipleActivitiesQuestion.giveAmountOfPoints(1, 0.5));
        assertEquals(100, multipleActivitiesQuestion.giveAmountOfPoints(1, 0.9));
        assertEquals(0, multipleActivitiesQuestion.giveAmountOfPoints(0, 1));
    }

    @Test
    void getImagePathTest() {
        assertEquals("somewhere", a4.getImagePath());
    }
}
