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
package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.hooks.SceneLifecycle;
import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;

public class MainCtrl {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private String memorizedName;

    private Stage primaryStage;
    private Stage popUpStage;

    private EnterNameSpCtrl enterNameSinglePlayerCtrl;
    private Scene enterName;

    private QueueAndEnterNameMpCtrl queueCtrl;
    private Scene queue;

    private IngameCtrl ingameCtrl;
    private Scene ingame;

    private GamemodeChooseCtrl gamemodeCtrl;
    private Scene gamemode;

    private QueueAndEnterNameMpCtrl enterNameMpCtrl;
    private Scene enterNameMp;

    private EnterURLPageCtrl enterURLCtrl;
    private Scene enterURL;

    private GlobalLeaderBoardCtrl globalLeaderBoardCtrl;
    private Scene globalLeaderBoard;

    private TransitionSceneCtrl transitionSceneCtrl;
    private Scene transitionScene;

    private AdminPanelCtrl adminPanelCtrl;
    private Scene adminPanelScene;

    private AdminActivitiesCtrl adminActivitiesCtrl;
    private Scene adminActivitiesScene;

    private AddActivityCtrl addActivityCtrl;
    private Scene addActivityScene;

    private DeleteActivityCtrl deleteActivityCtrl;
    private Scene deleteActivityScene;


    private CloseCtrl closeConfirmCtrl;
    private Scene closeConfirmScene;

    private EditQuestionTypesCtrl editQuestionTypesCtrl;
    private Scene editQuestionTypesScene;

    public void initialize(Stage primaryStage,
                           Pair<QueueAndEnterNameMpCtrl, Parent> queue,
                           Pair<EnterURLPageCtrl, Parent> enterURL,
                           Pair<IngameCtrl, Parent> ingame,
                           Pair<GamemodeChooseCtrl, Parent> gamemodeChoose,
                           Pair<EnterNameSpCtrl, Parent> nameEntering,
                           Pair<QueueAndEnterNameMpCtrl, Parent> nameEnteringMp,
                           Pair<HelpCtrl, Parent> help,
                           Pair<MultipleChoiceQuestionCtrl, Parent> multipleChoiceQuestion,
                           Pair<EstimationQuestionCtrl, Parent> estimationQuestion,
                           Pair<MultipleActivitiesQuestionCtrl, Parent> multipleActivitiesQuestion,
                           Pair<TransitionSceneCtrl, Parent> transitionQuestion,
                           Pair<GlobalLeaderBoardCtrl, Parent> globalLeaderBoard,
                           Pair<AdminPanelCtrl, Parent> adminPanel,
                           Pair<AdminActivitiesCtrl, Parent> adminActivities,
                           Pair<AddActivityCtrl, Parent> addActivity,
                           Pair<DeleteActivityCtrl, Parent> deleteActivity,
                           Pair<CloseCtrl, Parent> closeWindow,
                           Pair<EditQuestionTypesCtrl, Parent> editQuestionTypes) {
        this.primaryStage = primaryStage;
        this.popUpStage = new Stage();

        this.closeConfirmCtrl = closeWindow.getKey();
        this.closeConfirmScene = new Scene(closeWindow.getValue());

        this.enterNameSinglePlayerCtrl = nameEntering.getKey();
        this.enterName = new Scene(nameEntering.getValue());
        this.enterName.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/theme.css").toExternalForm());


        this.enterNameMpCtrl = nameEnteringMp.getKey();
        this.enterNameMp = new Scene(nameEnteringMp.getValue());
        this.enterNameMp.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/theme.css").toExternalForm());


        this.queueCtrl = queue.getKey();
        this.queue = new Scene(queue.getValue());
        this.queueCtrl.setImages();
        this.queue.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/theme.css").toExternalForm());

        registerSceneLifecycleHook(this.queueCtrl, this.queue);


        this.ingameCtrl = ingame.getKey();
        this.ingame = new Scene(ingame.getValue());
        this.ingame.getStylesheets().addAll(
                this.getClass().getResource("/client/stylesheets/ingame.css").toExternalForm(),
                this.getClass().getResource("/client/stylesheets/leaderboard-entry.css").toExternalForm(),
                this.getClass().getResource("/client/stylesheets/emote-popover.css").toExternalForm()
        );
        registerSceneLifecycleHook(this.ingameCtrl, this.ingame);
        this.ingameCtrl.mainPane.setPickOnBounds(false);


        this.gamemodeCtrl = gamemodeChoose.getKey();
        this.gamemode = new Scene(gamemodeChoose.getValue());
        this.gamemode.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/theme.css").toExternalForm());


        this.enterURLCtrl = enterURL.getKey();
        this.enterURL = new Scene(enterURL.getValue());
        this.enterURL.getStylesheets().add(this.getClass()
                .getResource("/client/stylesheets/theme.css").toExternalForm());


        this.transitionSceneCtrl = transitionQuestion.getKey();
        this.transitionScene = new Scene(transitionQuestion.getValue());

        this.globalLeaderBoardCtrl = globalLeaderBoard.getKey();
        this.globalLeaderBoard = new Scene(globalLeaderBoard.getValue());

        this.globalLeaderBoard.getStylesheets().add(
                this.getClass().getResource("/client/stylesheets/global-leaderboard.css").toExternalForm());

        this.adminPanelCtrl = adminPanel.getKey();
        this.adminPanelScene = new Scene(adminPanel.getValue());
        this.adminPanelScene.getStylesheets().add(
                this.getClass().getResource("/client/stylesheets/theme.css").toExternalForm());


        this.adminActivitiesCtrl = adminActivities.getKey();
        this.adminActivitiesScene = new Scene(adminActivities.getValue());
        this.adminActivitiesScene.getStylesheets().add(
                this.getClass().getResource("/client/stylesheets/admin_activity.css").toExternalForm());


        this.addActivityCtrl = addActivity.getKey();
        this.addActivityScene = new Scene(addActivity.getValue());
        this.addActivityScene.getStylesheets().add(
                this.getClass().getResource("/client/stylesheets/admin_activity.css").toExternalForm());

        this.deleteActivityCtrl = deleteActivity.getKey();
        this.deleteActivityScene = new Scene(deleteActivity.getValue());
        this.deleteActivityScene.getStylesheets().add(
                this.getClass().getResource("/client/stylesheets/admin_activity.css").toExternalForm());

        this.editQuestionTypesCtrl = editQuestionTypes.getKey();
        this.editQuestionTypesScene = new Scene(editQuestionTypes.getValue());
        this.editQuestionTypesScene.getStylesheets().add(
                this.getClass().getResource("/client/stylesheets/admin_question_types.css").toExternalForm());
        registerSceneLifecycleHook(this.editQuestionTypesCtrl, this.editQuestionTypesScene);

        showEnterURL();
        primaryStage.getIcons().add(new Image("@../../client/images/themePic.png"));
        primaryStage.setOnCloseRequest(event -> {
            Stage window = new Stage();
            window.setScene(closeConfirmScene);
            window.show();
            event.consume();
        });

        this.memorizedName = null;

        primaryStage.show();
    }

    public void showEnterNameSp() {
        primaryStage.setTitle("Enter Name");
        primaryStage.setScene(enterName);
        if (memorizedName != null) {
            enterNameSinglePlayerCtrl.setTextFieldName(memorizedName);
        }
    }

    public void showEnterNameMp() {
        primaryStage.setTitle("Enter Name");
        enterNameMpCtrl.queueMap.clear();
        enterNameMpCtrl.clearLabels();
        primaryStage.setScene(enterNameMp);
        if (memorizedName != null) {
            enterNameMpCtrl.setTextFieldName(memorizedName);
        }
    }

    public void showQueue() {
        primaryStage.setTitle("Waiting room");
        queueCtrl.clearLabels();
        queueCtrl.loadQueue();
        queueCtrl.queueMap.clear();
        primaryStage.setScene(queue);
    }

    public void showIngame() {
        primaryStage.setTitle("Ingame");
        primaryStage.setScene(ingame);
    }

    public void showGamemode() {
        primaryStage.setTitle("Choose Gamemode");
        primaryStage.setScene(gamemode);
    }

    public void showEnterURL() {
        primaryStage.setTitle("Enter URL");
        primaryStage.setScene(enterURL);
    }

    public void showGlobalLeaderBoard() {
        primaryStage.setTitle("Global Leaderboard");
        this.globalLeaderBoardCtrl.setLeaderboard();
        primaryStage.setScene(globalLeaderBoard);
    }

    public void showAddActivity() {
        if (popUpStage.getScene() == deleteActivityScene) {
            deleteActivityCtrl.cancel();
        }
        popUpStage.setTitle("Add Activity");
        popUpStage.getIcons().add(new Image("@../../client/images/Edit_Activities.png"));
        popUpStage.setScene(addActivityScene);
        addActivityCtrl.hideMessages(); // error text must be hidden when reopen the pop up
        addActivityScene.setOnKeyPressed(e -> addActivityCtrl.keyPressed(e));
        popUpStage.show();
    }

    public void showDeleteActivity() {
        if (popUpStage.getScene() == addActivityScene) {
            addActivityCtrl.cancel(); // otherwise, the field in that pop up aren't reset.
        }
        popUpStage.setTitle("Delete Activity");
        popUpStage.getIcons().add(new Image("@../../client/images/Edit_Activities.png"));
        popUpStage.setScene(deleteActivityScene);
        deleteActivityCtrl.hideMessages();
        deleteActivityScene.setOnKeyPressed(e -> deleteActivityCtrl.keyPressed(e));
        popUpStage.show();
    }

    public void showAdminPanel() {
        primaryStage.setTitle("Admin Panel");
        primaryStage.setScene(adminPanelScene);
    }

    public void showAdminActivities() {
        primaryStage.setTitle("Admin Panel For Activities");
        primaryStage.setScene(adminActivitiesScene);
        adminActivitiesCtrl.refresh();
    }

    public void showEditQuestionTypes() {
        primaryStage.setTitle("Admin Panel for Question Types");
        primaryStage.setScene(editQuestionTypesScene);
        editQuestionTypesCtrl.setChecked();
    }

    public void registerSceneLifecycleHook(SceneLifecycle controller, Scene scene) {
        primaryStage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == scene) {
                controller.onSceneShow(oldScene, newScene);
            } else if (oldScene == scene) {
                controller.onSceneHide(oldScene, newScene);
            }
        });
    }

    public IngameCtrl getIngameCtrl() {
        return ingameCtrl;
    }

    public void updateMemorizedName(String newName) {
        this.memorizedName = newName;
    }
}
