package com.jigong.app_attendance.info;

import android.os.Environment;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 13:51
 */
public class GlobalCode {
    /**
     * 考勤图片储存地址
     */
    public static final String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/attendance";

    /**
     * 人脸图片储存地址
     */
    public static final String WORKER_PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/worker";

    /**
     * 济工网平台请求前缀
     */
    public static String SERVER = "http://sign.gongyoumishu.com/";
//    public static String SERVER = "http://192.168.3.139:9001/";

    public static String GOMEET = "gomeetsign/";

    public static String HTTP_SERVER = SERVER + GOMEET;

    /**
     * 济工网平台登录
     */
    public static String LOGIN_FOSHAN = "webapi/handPad/platform/noProjectLogin/v1";

    /**
     * 济工网平台上传工人信息
     */
    public static String UPLOAD_WORKER_FOSHAN = "api/worker/uploadWorker/v1";

    /**
     * 济工网平台获取工人信息
     */
    public static String QUERY_TB_WORKER_BY_PROJECT_ID = "webapi/offline/tianbo/queryTBWorkerByProject/v3";

    /**
     * 济工网平台上传工人信息退场
     */
    public static String OUT_PROJECT_WORKER = "api/worker/outProjectWorker/v1";

    /**
     * 项目考勤查询
     */
    public static String QUERY_PROJECT_SIGN_LIST_FOSHAN = "api/sign/queryProjectSignList/v1";

    /**
     * 修改第三方工人编号
     */
    public static String UPDATE_WORKER_THRID_NO = "api/worker/updateWorkerThirdNo/v1";

}
