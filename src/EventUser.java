import com.google.gson.annotations.SerializedName;

/**
 * Created by Risid on 2017/6/6.
 *
 */
public class EventUser {
    private String uid;
    @SerializedName("realName")
    private String realName;
    private String status;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
