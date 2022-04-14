package server.services;

import commons.Activity;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.util.List;

@Service
public class ActivityService {

    private ActivityRepository activityRepository;

    /**
     * Constructor for ActivityService.
     * @param activityRepository takes a ActivityRepository, since this service needs to be able
     *                           to save and list the entries in the database.
     * It also has a List that holds all activities in the database, this makes the duplication check faster.
     */
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * A method that returns all entries in the activity table in the database. (Used in the activity controller)
     * @return a List that contains all the entries in the activity table.
     */
    public List<Activity> getAll() {
        return this.activityRepository.findAll();
    }

    /**
     * A method that saves a given Activity to the database.
     * @param activity an Activity to be saved.
     * @return the saved Activity .
     */
    public Activity save(Activity activity) {
        return this.activityRepository.save(activity);
    }

    /**
     * A getter to get the activityRepository.
     * @return the activityRepository.
     */
    public ActivityRepository getActivityRepository() {
        return this.activityRepository;
    }
}
