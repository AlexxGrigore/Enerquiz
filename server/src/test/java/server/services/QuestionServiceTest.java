package server.services;

import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import server.api.ActivityController;
import server.database.ActivityRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
//import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class QuestionServiceTest {

    private ActivityController activityController;
    private ActivityService activityService;
    private ActivityRepository activityRepository;
    private QuestionService questionService;
    private List<Activity> activities;

    @BeforeEach
    public void setup() {
        Random r = new Random(0);

        activities = IntStream.range(0, 80)
                .mapToObj(i -> new Activity("Activity " + i, i * 100))
                .collect(Collectors.toList());

        activityRepository = mock(ActivityRepository.class);
        activityService = mock(ActivityService.class);

        when(activityRepository.count()).thenReturn((long) activities.size());
        when(activityRepository.findAll()).thenReturn(activities);
        when(activityService.getAll()).thenReturn(activities);

        for (int i = 0; i < activities.size(); i++) {
            when(activityRepository.getById((long) i)).thenReturn(activities.get(i));
        }

        activityController = new ActivityController(r, activityService, activityRepository);
        questionService = mock(QuestionService.class);
    }

//    @Test
//    void generateRandomQuestion() {
//        System.out.println(questionService.generateRandomQuestions());
//        assertEquals(20, questionService.generateRandomQuestions().size()); //I didn't know how to test this.
//    }
//
//    @Test
//    void generateMAQuestion() {
//    }
//
//    @Test
//    void getAll() {
//
//    }
}
