package server.api;

import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.ActivityRepository;
import server.services.ActivityService;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActivityControllerTest {


    private ActivityService activityService;
    private ActivityRepository activityRepository;
    private Random random;

    @BeforeEach
    void init() {
        random = mock(Random.class);
        activityService = mock(ActivityService.class);
        activityRepository = mock(ActivityRepository.class);
    }

    @Test
    void getAll() {
        List<Activity> activities = List.of(
                new Activity("q2", 3534),
                new Activity("q1", 34),
                new Activity("q3", 34)
        );
        when(activityService.getAll()).thenReturn(activities);

        ActivityController controller = new ActivityController(random, activityService, activityRepository);
        List<Activity> result = controller.getAll();

        assertEquals(result, activities);

    }

    @Test
    void getRandomActivity() {
        Activity act = new Activity("question", "path", 9);

        when(activityRepository.count()).thenReturn(343L);
        when(random.nextInt(343)).thenReturn(0);
        when(activityRepository.findById(0L)).thenReturn(Optional.of(act));
        ArrayList<Long> testDummy = new ArrayList<>();
        testDummy.add(0L);
        when(activityRepository.getAllId()).thenReturn(testDummy);
        ActivityController controller = new ActivityController(random, activityService, activityRepository);

        ResponseEntity<Activity> reponce = controller.getRandomActivity();
        assertEquals(reponce, ResponseEntity.of(Optional.of(act)));

    }
}
