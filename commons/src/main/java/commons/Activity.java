package commons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    public long id;

    @Expose
    @SerializedName(value = "title")
    public String activityText;

    @Expose
    @SerializedName(value = "image_path")
    public String imagePath;

    @Expose
    @SerializedName(value = "consumption_in_wh")
    public long amountWh;

    /**
     * Default Constructor.
     */
    @SuppressWarnings("unused")
    public Activity() {
        // for object mapper
    }

    /**
     * Useful constructor.
     *
     * @param activityQuestion The question of this activity.
     * @param imagePath        The path of an image relates to this activity.
     * @param amountWh         The amount of wh this activity will consume in the given time.
     */
    public Activity(String activityQuestion, String imagePath, long amountWh) {
        this.activityText = activityQuestion;
        this.imagePath = imagePath;
        this.amountWh = amountWh;
    }

    /**
     * Constructor without image path.
     *
     * @param activityQuestion The question of this activity.
     * @param amountWh         The amount of wh this activity will consume in the given time.
     */
    public Activity(String activityQuestion, long amountWh) {
        this.activityText = activityQuestion;
        this.imagePath = "";
        this.amountWh = amountWh;
    }

    /**
     * Construtor used to test the getId() method.
     *
     * @param id The id of the Activity.
     */
    public Activity(long id) {
        this.id = id;
    }

    /**
     * A getter to get the question of an activity.
     *
     * @return a String which contains the question of an activity.
     */
    public String getActivityQuestion() {
        return activityText;
    }

    /**
     * A getter to get the image path of a image that relates to an activity.
     *
     * @return a String which contains the image path.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * A getter to get the consumption in wh of an activity for a given time.
     *
     * @return a Long which contains the power consumption of an activity.
     */
    public long getAmountWh() {
        return amountWh;
    }

    /**
     * A getter to get the ID of an activity.
     * @return a Long which represents the ID of an activity.
     */
    public long getId() {
        return id;
    }

    /**
     * Compares an activity with the current object.
     *
     * @param obj the given activity
     * @return true if they have the same attributes
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Hash function.
     *
     * @return the hashed value
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a human-readable form.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Activity{" + "id=" + id + ", type='" + activityText + '\'' + ", amountWatts=" + amountWh + '}';
    }

}
