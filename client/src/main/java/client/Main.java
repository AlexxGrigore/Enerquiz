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
package client;


import static com.google.inject.Guice.createInjector;


import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.GameLeaderboardCtrl;
import client.scenes.EnterNameSpCtrl;
import client.scenes.GamemodeChooseCtrl;
import client.scenes.IngameCtrl;
import client.scenes.QueueAndEnterNameMpCtrl;
import client.scenes.EnterURLPageCtrl;
import client.scenes.EstimationQuestionCtrl;
import client.scenes.HelpCtrl;
import client.scenes.MainCtrl;
import client.scenes.MultipleChoiceQuestionCtrl;
import client.scenes.MultipleActivitiesQuestionCtrl;
import client.scenes.AdminActivitiesCtrl;
import client.scenes.AddActivityCtrl;
import client.scenes.AdminPanelCtrl;
import client.scenes.TransitionSceneCtrl;
import client.scenes.GlobalLeaderBoardCtrl;
import client.scenes.DeleteActivityCtrl;
import client.scenes.CloseCtrl;
import client.scenes.EditQuestionTypesCtrl;

import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var enterName = FXML.load(EnterNameSpCtrl.class, "client", "scenes", "EnterNameSinglePlayer.fxml");
        var help = FXML.load(HelpCtrl.class, "client", "scenes", "Help.fxml");
        var enterNameMp = FXML.load(QueueAndEnterNameMpCtrl.class, "client", "scenes", "EnterNameMultiPlayer.fxml");
        var ingame = FXML.load(IngameCtrl.class, "client", "scenes", "Ingame.fxml");
        var gamemodeChoosing = FXML.load(GamemodeChooseCtrl.class, "client", "scenes", "GamemodeChoose.fxml");
        var finalLeaderboard = FXML.load(GameLeaderboardCtrl.class, "client", "scenes", "final-leaderboard.fxml");
        var intermediaryLeaderboard =
                FXML.load(GameLeaderboardCtrl.class, "client", "scenes", "intermediate-leaderboard.fxml");
        var queue = FXML.load(QueueAndEnterNameMpCtrl.class, "client", "scenes", "Queue.fxml");
        var enterURL = FXML.load(EnterURLPageCtrl.class, "client", "scenes", "EnterURLPage.fxml");
        var multipleChoiceQuestion = FXML.load(MultipleChoiceQuestionCtrl.class, "client", "scenes",
                "MultipleChoiceQuestion.fxml");
        var estimationQuestion = FXML.load(EstimationQuestionCtrl.class, "client", "scenes",
                "EstimationQuestion.fxml");

        var multipleActivitiesQuestion = FXML.load(MultipleActivitiesQuestionCtrl.class, "client", "scenes",
                "MultipleActivitiesQuestion.fxml");

        var globalLeaderBoard = FXML.load(GlobalLeaderBoardCtrl.class, "client", "scenes",
                "GlobalLeaderboard.fxml");

        var transitionScene = FXML.load(TransitionSceneCtrl.class, "client", "scenes",
                "TransitionScene.fxml");

        var adminPanelScene = FXML.load(AdminPanelCtrl.class, "client", "scenes",
                "AdminPanel.fxml");

        var adminActivitiesScene = FXML.load(AdminActivitiesCtrl.class, "client",
                "scenes", "AdminActivities.fxml");

        var addActivityScene = FXML.load(AddActivityCtrl.class, "client",
                "scenes", "AddActivity.fxml");

        var deleteActivityScene = FXML.load(DeleteActivityCtrl.class, "client",
                "scenes", "DeleteActivity.fxml");


        var closeConfirmScene = FXML.load(CloseCtrl.class, "client",
                "scenes", "CloseConfirm.fxml");

        var editQuestionTypesScene = FXML.load(EditQuestionTypesCtrl.class,
                "client", "scenes", "EditQuestionTypes.fxml");


        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        mainCtrl.initialize(primaryStage, queue, enterURL, ingame,
                gamemodeChoosing, enterName, enterNameMp, help, multipleChoiceQuestion, estimationQuestion,
                multipleActivitiesQuestion, transitionScene, globalLeaderBoard, adminPanelScene, adminActivitiesScene,

                addActivityScene, deleteActivityScene, closeConfirmScene, editQuestionTypesScene);

    }

    public static client.MyFXML getFXML() {
        return FXML;
    }

    public static Injector getInjector() {
        return INJECTOR;
    }
}

