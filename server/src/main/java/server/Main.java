/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import commons.Activity;
import commons.ActivityReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;



import server.services.ActivityService;

import java.util.List;


@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * This only executes further to the read function when the database is empty.
     * This runner is automatically ran by the start of the server.
     * @param activityService It takes a activityService to interact with the database.
     * @return a CommandlineRunner that executes the codes.
     */
    @Bean
    CommandLineRunner runner(ActivityService activityService) {
        return args -> {
            // read json file to a List of activities (with Gson) and save it to the database

            ActivityReader ar = new ActivityReader();

            ar.readActivities();

            List<Activity> activitiesBank = ar.getActivityBank();
            List<String> activitiesInTheDatabase =
                    activityService
                            .getAll()
                            .stream()
                            .map(x -> x.getActivityQuestion())
                            .toList();

//            System.out.println(activityService.getAll().size()); // <-- debug purpose, to monitor the amounts

            for (Activity activity : activitiesBank) {
                if (!activitiesInTheDatabase.contains(activity.getActivityQuestion())) {
                    activityService.save(activity);
                }
            }
        };
    }


}
