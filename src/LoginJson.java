import com.google.gson.annotations.SerializedName;

/**
 * Created by Risid on 2017/6/6.
 */
public class LoginJson {

    private UserToken userToken;

    public UserToken getUserToken() {
        return userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

    public class UserToken {

        @SerializedName("oauth_token")
        private String oauthToken;

        @SerializedName("oauth_token_secret")
        private String oauthTokenSecret;

        private String uid;

        public String getOauthToken() {
            return oauthToken;
        }

        public void setOauthToken(String oauthToken) {
            this.oauthToken = oauthToken;
        }

        public String getOauthTokenSecret() {
            return oauthTokenSecret;
        }

        public void setOauthTokenSecret(String oauthTokenSecret) {
            this.oauthTokenSecret = oauthTokenSecret;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }

}
