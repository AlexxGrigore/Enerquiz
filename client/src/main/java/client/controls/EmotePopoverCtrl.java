package client.controls;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EmotePopoverCtrl {

    private @FXML ImageView imageView;

    /**
     * Set the image of the emote popover.
     *
     * @param image The image to display
     */
    public void setImage(Image image) {
        this.imageView.setImage(image);
    }

}
