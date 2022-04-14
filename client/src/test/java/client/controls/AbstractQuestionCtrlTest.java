package client.controls;


import client.scenes.MultipleChoiceQuestionCtrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractQuestionCtrlTest {

    MultipleChoiceQuestionCtrl multipleChoiceQuestionCtrl;

    @BeforeEach
    public void setup() {
        multipleChoiceQuestionCtrl = new MultipleChoiceQuestionCtrl(null, null, null);
    }

    @Test
    void answerToRemoved0() {
        assertNotEquals(0, multipleChoiceQuestionCtrl.answerToRemoved(0));
    }

    @Test
    void answerToRemoved1() {
        assertNotEquals(1, multipleChoiceQuestionCtrl.answerToRemoved(1));
    }

    @Test
    void answerToRemoved2() {
        assertNotEquals(2, multipleChoiceQuestionCtrl.answerToRemoved(2));
    }
}
