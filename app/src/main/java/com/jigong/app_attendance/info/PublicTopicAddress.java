package com.jigong.app_attendance.info;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 13:51
 */
public class PublicTopicAddress {

    public static final String HOST = "219.130.221.10";
    public static final int PORT = 20028;

    /*
     * 济工网平台请求前缀
     * */
    public static String SERVER = "http://sign.gongyoumishu.com/";
//    public static String SERVER = "http://192.168.3.139:9001/";

    public static String GOMEET = "gomeetsign/";

    public static String HTTP_SERVER = SERVER + GOMEET;

    /*
     *济工网平台登录
     * */
    public static String LOGIN_FOSHAN = "webapi/handPad/platform/noProjectLogin/v1";

    /*
     *济工网平台上传工人信息
     * */
    public static String UPLOAD_WORKER_FOSHAN = "api/worker/uploadWorker/v1";

    /*
     *项目考勤查询
     * */
    public static String QUERY_PROJECT_SIGN_LIST_FOSHAN = "api/sign/queryProjectSignList/v1";

    /*
     * 合肥平台指令前缀
     * */
    public static String TOPIC_PREFIX = "mqtt/" + User.getInstance().getUserName() + "/";

    /*
     * 向合肥平台推送上线/下线通知
     * */
    public static String BASIC_PUSH = "mqtt/" + User.getInstance().getUserName() + "/basic";

    /*
     * 向合肥平台推送心跳通知
     * */
    public static String HEARTBEAT_PUSH = "mqtt/" + User.getInstance().getUserName() + "/heartbeat";

}
