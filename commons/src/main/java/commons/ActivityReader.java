package commons;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ActivityReader {

    private List<Activity> activityBank;
            String askForPath = "\n"
                    + "\t------------------------------------------------------\n"
                    + "\t| Please Enter the path for the activities JSON file |\n"
                    + "\t------------------------------------------------------\n";

    /**
     * A constructor for the ActivityReader.
     */
    public ActivityReader() {
        this.activityBank = new ArrayList<>();
    }

    /**
     * A reader to read the big json file which contains a big json array consists out of activities.
     * This method will read the json file and put the activities in a list.
     * This method has a hardcoded path, so you must place the activities.json file
     * outside the git folder to make it work.
     */
    public void readActivities() {
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Reader reader = new FileReader("../activities.json");
            activityBank = Arrays.stream(gson.fromJson(reader, Activity[].class)).toList();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * A reader to read a json file which contains questions.
     * This method will ask for a path.
     */
    public void readActivitiesWithProvidedPath() {
        try {
            System.out.println(askForPath);
            Scanner sc = new Scanner(System.in);
            String path = sc.nextLine();
            Reader readerThatAsksPath = new FileReader(path);
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            activityBank = Arrays.stream(gson.fromJson(readerThatAsksPath, Activity[].class)).toList();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * A getter to get the activity bank.
     * @return a List which contains all activities.
     */
    public List<Activity> getActivityBank() {
        return activityBank;
    }
}
