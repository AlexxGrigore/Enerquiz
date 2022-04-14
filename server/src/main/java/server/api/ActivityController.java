package server.api;

import commons.Activity;
import commons.ActivityReader;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import server.database.ActivityRepository;
import server.services.ActivityService;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final Random random;
    private ActivityRepository activityRepository;
    private ActivityService activityService;

    /**
     * Constructor of the controller.
     * @param random a random
     * @param activityService a Service that do tasks like (find all entries and save entries)
     * @param activityRepository a Service that do tasks like (find all entries and save entries)
     */
    public ActivityController(Random random, ActivityService activityService, ActivityRepository activityRepository) {
        this.random = random;
        this.activityService = activityService;
        this.activityRepository = activityRepository;
    }

    /**
     * Returns all the activities.
     * @return  a list with all the activities
     */
    @GetMapping("find-all")
    public List<Activity> getAll() {
        return activityService.getAll();
    }

    /**
     * Returns a random activity.
     * Since the ID could be not continuous, randomize the id by the size of the database will not always work.
     * Therefore, we use a query to prevent such situation.
     *
     * @return the activity
     */
    @GetMapping("/random-activity")
    public ResponseEntity<Activity> getRandomActivity() {
        List<Long> idForActivities = activityRepository.getAllId();
        int index = random.nextInt((int) idForActivities.size());
        return ResponseEntity.of(activityRepository.findById(idForActivities.get(index)));
    }

    /**
     * Save the given activity to the database.
     * Checks if the activity is valid, is not really needed.
     * Because it was already checked, before it is sent from the client side.
     * So no check for the initial field's value, checked if it is null anyway.
     *
     * @param activity An activity to be saved in the database.
     * @return the successfully saved activity to the database.
     */
    @PostMapping("add")
    public ResponseEntity<Activity> add(@RequestBody Activity activity) {

        if (activity == null) {
            return ResponseEntity.badRequest().build();
        }

        Activity saved = activityRepository.save(activity);
        return ResponseEntity.ok(saved);
    }

    /**
     * Get the activity from the database, based on the provided id.
     * If there is no activity in the database that corresponds to the given id, NULL is returned.
     * @param id An long which represents the primary key of activities in the database.
     * @return NULL or the found activity.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getById(@PathVariable("id") long id) {
        if (id < 0 || !activityRepository.existsById(id)) {
            return null;
        }
        return ResponseEntity.of(activityRepository.findById(id));
    }

    /**
     * Delete the activity in the database, based on the provided id.
     * If there is no activity in the database that corresponds to the given id, NULL is returned.
     * @param id An long which represents the primary key of activities in the database.
     * @return NULL or the deleted activity.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Activity> deleteById(@PathVariable("id") long id) {
        if (id < 0 || !activityRepository.existsById(id)) {
            return null;
        }
        ResponseEntity<Activity> deleted = ResponseEntity.of(activityRepository.findById(id));
        activityRepository.deleteById(id);
        return deleted;
    }

    /**
     * Drop the current table in the database, and re-read the default JSON file to load the default database.
     * @return A String, not actually needed, but since this is a get mapping.
     * I need ask something to get these codes be executed. So in the ServerUtil class. I just Ask for a sting.
     */
    @PostMapping("/load-default-database")
    public String loadDefaultDatabase() {
        activityRepository.resetSeq();
        activityRepository.dropTable();
        activityRepository.reCreateTable();

        ActivityReader ar = new ActivityReader();

        ar.readActivities();

        List<Activity> activitiesBank = ar.getActivityBank();

        for (Activity activity : activitiesBank) {
                activityService.save(activity);
        }
        return "Done";
    }
}
