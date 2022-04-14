package commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import commons.events.IntermediaryLeaderboardEvent;
import commons.events.NewQuestionEvent;
import commons.events.FinalLeaderboardEvent;
import commons.events.Event;
import commons.events.PlayerListUpdateEvent;
import commons.events.TimeLeftUpdateEvent;
import commons.events.ShowAnswersEvent;

public class GsonConfig {

    private Gson gson;

    public GsonConfig() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(
                        RuntimeTypeAdapterFactory.of(Question.class)
                                .registerSubtype(MultipleChoiceQuestion.class)
                                .registerSubtype(EstimationQuestion.class)
                                .registerSubtype(MultipleActivitiesQuestion.class)
                )
                .registerTypeAdapterFactory(
                        RuntimeTypeAdapterFactory.of(Event.class)
                                .registerSubtype(NewQuestionEvent.class)
                                .registerSubtype(IntermediaryLeaderboardEvent.class)
                                .registerSubtype(FinalLeaderboardEvent.class)
                                .registerSubtype(PlayerListUpdateEvent.class)
                                .registerSubtype(ShowAnswersEvent.class)
                                .registerSubtype(TimeLeftUpdateEvent.class)
                )
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}
