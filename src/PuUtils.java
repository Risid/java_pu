import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Risid on 2017/6/6.
 * @author Risid
 * Pu工具类
 */
public class PuUtils {

    /** 每个PuUtils只会有初始化一个cookie */
    private String cookie;
    /** 第一个token */
    private String oauthToken;
    /** 第二个token */
    private String oauthTokenSecret;
    /** 未签到人数 */
    private List<EventUser> eventUserList = new ArrayList<>();
    /** 最大活动人数 */
    private static final int PEOPLE_MAX = 1000;
    /** host */
    private static final String BASE_URL = "http://pocketuni.net";
    /** 登录 */
    private static final String LOGIN_URL = "/index.php?app=api&mod=Sitelist&act=login";
    /** 获取可签到事件 */
    private static final String SIGN_EVENT_URL = "/index.php?app=api&mod=Event&act=getCanSignEventLists";
    /** 获取活动报名用户 */
    private static final String EVENT_USER_URL = "/index.php?app=api&mod=Event&act=eventUser";
    /** 在线签到 */
    private static final String SIGN_ONLINE_URL = "/index.php?app=api&mod=Event&act=signOnline";


    /**
     * 用户名密码登录
     * @param user
     *          用户名
     * @param pwd
     *          密码
     */
    public PuUtils(String user, String pwd) {
        initCookie();
        login(user, pwd);
    }

    /**
     * 功能测试，使用token登录
     * @param oauthToken
     *          token 1
     * @param oauthTokenSecret
     *          token 2
     * @param isDebug
     *          是否打印一条Debug信息
     */
    public PuUtils(String oauthToken, String oauthTokenSecret, boolean isDebug){
        initCookie();
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
        if (isDebug){
            System.out.println("--------------Debug!-------------");
        }
    }

    private void initCookie(){
        this.cookie = getCookie(BASE_URL);
    }


    /**
     * 打印某个活动的所有报名人员.
     * 注：活动人数不应超过PEOPLE_MAX所设定的值.
     * @param actId
     *          活动id
     */
    public void showAllEventUser(String actId){
        // 每页请求数是10
        getEventUser(actId, PEOPLE_MAX / 10, false);
    }

    /**
     * 私有方法.
     * 获取某个活动的报名人员
     * @param actId
     *          活动id
     * @param pageMax
     *          最大请求页面数
     * @param isSelect
     *          是否筛选出未签到人员并传入eventUserList
     */
    private void getEventUser(String actId, int pageMax, boolean isSelect){
        List<EventUser> allUserList = new ArrayList<>();
        Gson gson = new Gson();
        // List type 用于Json解析
        Type listType = new TypeToken<List<EventUser>>(){}.getType();
        // 页数从1开始
        int i = 1;
        while (true){
            if (i > pageMax){
                // 超出最大页面跳出
                break;
            }
            RequestBody requestBody = new FormBody.Builder()
                    .add("oauth_token", oauthToken)
                    .add("oauth_token_secret", oauthTokenSecret)
                    // 一个页面包含的用户数
                    .add("count", "10")
                    .add("id", actId)
                    .add("page", String.valueOf(i))
                    .build();
            i++;
            String resultStr = getData(BASE_URL + EVENT_USER_URL, requestBody);
            if (resultStr == null){
                // 当resultStr传入失败时，再次尝试
                i--;
                continue;
            }

            List<EventUser> temp;
            try {
                temp = gson.fromJson(resultStr, listType);
            }catch (ClassCastException e){
                break;
            }

            if (temp == null || temp.size() == 0){
                break;
            }
            // 解析成功后，放入allUserList
            allUserList.addAll(temp);
            try {
                // 延时200毫秒
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        for (int j = 0; j < allUserList.size(); j++) {
            if (isSelect){
                if (allUserList.get(j).getStatus().equals("1")) {
                    // 未签到的status值为1
                    this.eventUserList.add(allUserList.get(j));
                }
            }else {
                String status = "WRONG!!!!!!";
                if (allUserList.get(j).getStatus().equals("2")) {
                    status = "YES";
                }
                if (allUserList.get(j).getStatus().equals("1")) {
                    status = "NO";
                }
                System.out.println(allUserList.get(j).getRealName() + "\t" + status);
            }

        }

    }

    /**
     * 获取未签到的人数
     * 并添加到eventUserList
     * @param actId
     *          活动id
     */
    private void getNoSignEventUser(String actId){
        this.eventUserList.clear();
        getEventUser(actId, 100, true);

    }


    /**
     * 签到
     * @param actId
     *          活动id
     */
    public void sign(String actId){
        // 先获取未签到的人数
        getNoSignEventUser(actId);
        if (this.eventUserList.isEmpty()){
            return;
        }
        Gson gson = new Gson();

        for (int i = 0; i < eventUserList.size(); i++) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("oauth_token", oauthToken)
                    .add("oauth_token_secret", oauthTokenSecret)
                    .add("type", "1")
                    .add("actiId", actId)
                    .add("userId", eventUserList.get(i).getUid())
                    .build();
            String resultStr = getData(BASE_URL + SIGN_ONLINE_URL, requestBody);
            SignStatus signStatus = gson.fromJson(resultStr, SignStatus.class);
            System.out.println(eventUserList.get(i).getUid() + "\t" + signStatus.getMsg());
            try {
                // 延时200毫秒
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 登录的目的是获取2个token
     * oauthToken
     * oauthTokenSecret
     */
    private void login(String user, String pwd){
        RequestBody requestBody = new FormBody.Builder()
                .add("email", user + "@tyut.com")
                .add("client", "1")
                .add("password", pwd)
                .build();

        String resultStr = getData(BASE_URL + LOGIN_URL, requestBody);


        Gson gson = new Gson();

        LoginJson loginJson = gson.fromJson(resultStr, LoginJson.class);

        oauthToken = loginJson.getUserToken().getOauthToken();

        oauthTokenSecret = loginJson.getUserToken().getOauthTokenSecret();
        System.out.println("oauthToken=" + oauthToken);
        System.out.println("oauthTokenSecret=" + oauthTokenSecret);

    }


    /**
     * 获取当前账户所有的活动签到权限列表
     */
    public void getCanSignEventLists(){
        RequestBody requestBody = new FormBody.Builder()
                .add("oauth_token", oauthToken)
                .add("oauth_token_secret", oauthTokenSecret)
                .build();
        String resultStr = getData(BASE_URL + SIGN_EVENT_URL, requestBody);

        Gson gson = new Gson();
        Type listType = new TypeToken<List<SignListJson>>(){}.getType();
        List<SignListJson> signListJsonList = gson.fromJson(resultStr, listType);
        for (int i = 0; i < signListJsonList.size(); i++) {
            System.out.println(signListJsonList.get(i).getName() + "\t" + signListJsonList.get(i).getActivityId());
        }


    }

    /**
     * 获取Cookie
     * @param url
     *          网页url
     * @return 无论请求成功与否，返回cookie
     */

    public String getCookie(String url){
        OkHttpClient client = new OkHttpClient();
        OkHttpClient client1 = client.newBuilder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).build();
        Response response;
        String result = null;
        try {
            response = client1.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.header("Set-Cookie");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 以post方式发送请求
     * @param url
     *          网页
     * @param formBody
     *          post表单
     * @return  如果成功，则返回数据
     *          不成功返回空的字符串
     */
    private String getData(String url, RequestBody formBody) {

        OkHttpClient client = new OkHttpClient();
        OkHttpClient client1 = client.newBuilder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie",cookie)
                .addHeader("User-Agent", "client:Android version:6.3.0 Product:MI 4LTE OsVersion:6.0.1")
                .post(formBody)
                .build();
        Response response;

        String result = null;
        try {
            response = client1.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
