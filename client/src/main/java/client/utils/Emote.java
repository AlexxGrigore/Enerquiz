package client.utils;

import javafx.scene.image.Image;

public enum Emote {
    ANGRY("@../../client/images/emotes/angry.png"),
    ASTONISHED("@../../client/images/emotes/astonished.png"),
    HEART("@../../client/images/emotes/heart.png");

    private Image image;

    /**
     * Create a new Emote with an image.
     *
     * @param url The url of the image
     */
    Emote(String url) {
        this.image = new Image(url, 56, 56, true, true);
    }

    /**
     * Get the image of the Emote.
     *
     * @return The image
     */
    public Image getImage() {
        return this.image;
    }
}
