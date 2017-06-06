import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        Scanner in =new Scanner(System.in);
        // 密码登录
        System.out.println("用户名：");
        String user = in.nextLine();
        System.out.println("密码");
        String pwd = in.nextLine();
        PuUtils puUtils = new PuUtils(user, pwd);

        // token抓包调试登录
//        String oauthToken = "";
//        String oauthTokenSecret = "";
//
//        PuUtils puUtils = new PuUtils(oauthToken, oauthTokenSecret, false);

        // 打印可签到列表
        System.out.println("可签到的事件：");
        puUtils.getCanSignEventLists();
        // 显示活动报名用户
//        puUtils.showAllEventUser("");
        // 全部签到
        System.out.println("输入要全部签到的事件的id：");

        puUtils.sign(in.nextLine());

        System.out.println("签到完成");
        in.next();
    }
}