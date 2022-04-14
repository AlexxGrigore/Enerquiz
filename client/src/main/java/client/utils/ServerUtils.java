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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;

import commons.GlobalLeaderBoardEntry;

import commons.Activity;
import client.event.EventManager;
import com.google.inject.Inject;
import commons.Answer;
import commons.IngamePlayer;
import commons.Joker;
import commons.Question;
import jakarta.ws.rs.client.Client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.ClientConfig;

public class ServerUtils {
    private String serverUrl = "";
    private Client client = ClientBuilder.newClient(new ClientConfig().register(GsonJerseyProvider.class));
    private NewCookie sessionCookie;

    @Inject
    private EventManager eventManager;

    /**
     * Get the server URL.
     *
     * @return The server URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Get the Jakarta client used to send network requests.
     *
     * @return The Jakarta client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Get the session cookie.
     *
     * @return The session cookie
     */
    public NewCookie getSessionCookie() {
        return sessionCookie;
    }

    /**
     * Send an activity to the server to store in the database.
     *
     * @param activity The activity to be added to the database.
     * @return The same activity but returned by the server which might have changed.
     */
    public Activity addActivity(Activity activity) {
        return client
                .target(this.serverUrl).path("api/activities/add")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    /**
     * Send the player's answer to the server using a PUT request. Sending another answer will replace the previous
     * answer.
     *
     * @param answer The player's answer
     * @return The response of the request
     */
    public Response submitAnswer(Answer answer) {
        return client
                .target(this.serverUrl).path("/api/game/answer")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .put(Entity.entity(answer, APPLICATION_JSON));
    }

    /**
     * Connect to the server and start a new session. The JSESSIONID cookie received from the server is stored in
     * {@link #sessionCookie}.
     *
     * @param url The URL of the server to connect to
     * @return The response of the request
     */
    public Response connect(String url) {
        this.serverUrl = url;

        Response response = client
                .target(this.serverUrl).path("api/connect")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.text(""));

        System.out.println(response.getStatus());
        sessionCookie = response.getCookies().get("JSESSIONID");
        System.out.println("Received JSESSIONID cookie: " + sessionCookie);

        return response;
    }

    /**
     * Set the player's name for the current session started by {@link #connect(String)}.
     *
     * @param name The display name to set
     * @return The response of the request
     */
    public Response setName(String name) {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.putSingle("name", name);

        Response response = client
                .target(this.serverUrl).path("api/set-name")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.form(form));

        System.out.println(response.hasEntity());

        return response;
    }

    /**
     * Set the player's name for the current session started by {@link #connect(String)}.
     *
     * @param name The display name to set
     * @return The response of the request
     */
    public Response changeName(String name) {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        form.putSingle("name", name);

        Response response = client
                .target(this.serverUrl).path("api/change-name")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.form(form));

        System.out.println(response.hasEntity());

        return response;
    }

    /**
     * Send an emote to the server to be broadcasted to all players.
     *
     * @param emote The emote to send
     * @return The response of the request
     */
    public Response sendEmote(Emote emote) {
        return client
                .target(this.serverUrl).path("api/emotes/send/" + emote.name())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Get all new emotes since the last poll using long polling.
     *
     * @return A map of player names to emotes the players sent
     */
    public Map<String, Emote> pollEmotes() {
        return client
                .target(this.serverUrl).path("api/emotes/poll")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .get(new GenericType<Map<String, Emote>>() {
                });
    }

    /**
     * Player joins queue for the multiplayer game.
     *
     * @return http response.
     */
    public Response joinQueue() {
        return client
                .target(this.serverUrl).path("api/queue/add-player")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Player leaves queue for the multiplayer game.
     *
     * @return http response.
     */
    public Response leaveQueue() {
        return client
                .target(this.serverUrl).path("api/queue/leave-queue")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Get all players in a game the sender is in.
     *
     * @return A list of players.
     */
    public List<IngamePlayer> getPlayers() {
        return client
                .target(this.serverUrl).path("api/game/players")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .get(new GenericType<List<IngamePlayer>>() {});
    }

    /**
     * Checks if player is in the game.
     * @return players list.
     */
    public boolean isPlaying() {
        boolean status = client
                .target(this.serverUrl).path("api/queue/get-status")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .get(new GenericType<Boolean>() {});
        return status;
    }


    /**
     * Request a list of entries to the global singleplayer leaderboard from the server's database.
     *
     * @return The list of the 5 best singleplayer entries.
     */
    public List<GlobalLeaderBoardEntry> getGlobalSinglePlayerLeaderBoard() {
        return client //
                .target(this.serverUrl).path("api/leaderboard/singleplayer") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<GlobalLeaderBoardEntry>>() {
                });
    }

    /**
     * Request a list of entries to the global multiplayer leaderboard from the server's database.
     *
     * @return The list of the 5 best multiplayer entries.
     */
    public List<GlobalLeaderBoardEntry> getGlobalMultiPlayerLeaderBoard() {
        return client //
                .target(this.serverUrl).path("api/leaderboard/multiplayer") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<GlobalLeaderBoardEntry>>() {
                });
    }

    /**
     * Get a random activity from the database.
     *
     * @return A random activity.
     */
    public Activity getRandomActivity() {
        return client
                .target(this.serverUrl).path("api/activities/random-activity")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .get(new GenericType<Activity>() {
                });
    }

    /**
     * Get a list of questions.
     *
     * @return A list of random questions.
     */
    public List<Question> getQuestions() {
        System.out.println("Server Utils Get Questions Is Last Printed");
        return client
                .target(this.serverUrl).path("api/game/questions")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .get(new GenericType<List<Question>>() {
                });
    }

    /**
     * Starts multiplayer game.
     *
     * @return response object.
     */
    public Response startGame() {
        return client
                .target(this.serverUrl).path("api/queue/start-game")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Starts single player game.
     *
     * @return single player.
     */
    public Response startSingle() {
        return client
                .target(this.serverUrl).path("api/queue/start-single")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Continue game after the intermediary leaderboard.
     *
     * @return response Object.
     */
    public Response continueGame() {
        return client
                .target(this.serverUrl).path("api/game/intermediary-leaderboard")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Gets players in the queue.
     *
     * @return players list.
     */
    public List<IngamePlayer> getQueue() {
        List<IngamePlayer> players = client
                .target(this.serverUrl).path("api/queue/get-queue")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<IngamePlayer>>() {
                });
        return players;
    }

    /**
     * <<<<<<< HEAD
     * Gets all activities from the database.
     *
     * @return a list contains all the activities in the database.
     */
    public List<Activity> getAllActivities() {
        List<Activity> activities = client.
                target(this.serverUrl).path("api/activities/find-all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Activity>>() {});
        return activities;
    }

    /**
     * Get activity from the database based on the id.
     * If the activity is found in the database return the activity.
     * Else NULL is returned.
     *
     * @param id The primary key of the activity.
     * @return The activity or NULL.
     */
    public Activity getActivityById(long id) {
        Activity activity = client
                .target(this.serverUrl).path("api/activities/" + id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Activity>() {});
        return activity;
    }

    /**
     * Play a joker.
     *
     * @param joker The joker to play
     * @return The response of the request
     */
    public Response playJoker(Joker joker) {
        return client
                .target(this.serverUrl).path("api/game/play-joker/" + joker)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .cookie(sessionCookie)
                .post(Entity.text(""));
    }

    /**
     * Delete activity in the database based on the id.
     * If the activity is found in the database return the activity and delete it.
     * Else NULL is returned.
     *
     * @param id The primary key of the activity.
     * @return The deleted activity or NULL.
     */
    public Activity deleteActivityById(long id) {
        Activity activity = client
                .target(this.serverUrl).path("api/activities/" + id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<Activity>() {});
        return activity;
    }

    /**
     * Load the default activity database of the server.
     * Executes 3 query.
     * First the primary key are reset.
     * Second the table is dropped.
     * Last the table is recreated.
     * More detail see ActivityRepository.
     */
    public void loadDefaultDatabase() {
        String dummy = client
                .target(this.serverUrl).path("api/activities/load-default-database")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity("Load the database please", APPLICATION_JSON), String.class);

    }

    /**
     * This method get the current types of questions that are generated by the server.
     * @return The boolean array which indicates if a specific type of question is
     * generated by the server.
     */
    public boolean[] getTypes() {
        boolean[] types = client
                .target(this.serverUrl).path("api/questions/types")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<boolean[]>() {});
        return types;
    }

    /**
     * This method send a boolean array to the server to update the type of questions
     * that are generated by the server.
     * @param types The boolean array which indicates if a specific type of question is
     * generated by the server.
     */
    public void updateTypes(boolean[] types) {
         client.target(this.serverUrl).path("api/questions/update-types")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(types, APPLICATION_JSON), String.class);
    }
}
