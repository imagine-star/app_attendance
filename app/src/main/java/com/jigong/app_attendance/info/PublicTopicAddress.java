package com.jigong.app_attendance.info;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 13:51
 */
public class PublicTopicAddress {

    /*
     * 济工网平台请求前缀
     * */
//    public static String SERVER = "http://sign.gongyoumishu.com/";
    public static String SERVER = "http://139.196.157.209:9001/";

    public static String GOMEET = "gomeetsign/";

    public static String HTTP_SERVER = SERVER + GOMEET;

    /*
     *济工网平台登录
     * */
    public static String LOGIN = "webapi/handPad/platform/login/v1";

    /*
     *济工网平台上传工人信息
     * */
    public static String GET_WORKER = "";

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
