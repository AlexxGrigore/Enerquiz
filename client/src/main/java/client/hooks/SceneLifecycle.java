package client.hooks;

import javafx.scene.Scene;

public interface SceneLifecycle {

    /**
     * Hook that gets executed when this controller's scene is shown.
     *
     * @param oldScene The previous scene that's no longer shown
     * @param newScene The current scene that's now being shown
     */
    void onSceneShow(Scene oldScene, Scene newScene);

    /**
     * Hook that gets executed when this controller's scene is hidden.
     *
     * @param oldScene The previous scene that's no longer shown
     * @param newScene The current scene that's now being shown
     */
    void onSceneHide(Scene oldScene, Scene newScene);

}
