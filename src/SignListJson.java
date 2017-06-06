import com.google.gson.annotations.SerializedName;

/**
 * Created by Risid on 2017/6/6.
 */
public class SignListJson {
    @SerializedName("actiId")
    private String activityId;

    private String name;

    private String time;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
