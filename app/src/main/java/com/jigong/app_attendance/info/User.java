package com.jigong.app_attendance.info;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 13:36
 */
public class User {

    private final String USER_NAME = "USER_NAME";
    private final String PASS_WORD = "PASS_WORD";
    private final String IN_DEVICE_NO = "IN_DEVICE_NO";
    private final String OUT_DEVICE_NO = "OUT_DEVICE_NO";
    private final String PROJECT_ID = "PROJECT_ID";
    private final String PROJECT_NAME = "PROJECT_NAME";
    private final String IN_ONLINE = "IN_ONLINE";
    private final String OUT_ONLINE = "OUT_ONLINE";
    private final String LOGIN = "LOGIN";
    private final String TOKEN = "TOKEN";
    private final String ACCOUNT = "ACCOUNT";
    private final String ROWID = "ROWID";
    private final String SIGN_DATE = "SIGN_DATE";

    private final SavedSharedPreferences mPreferences;

    private static User mInstance;

    public static User getInstance() {
        if (null == mInstance) {
            mInstance = new User();
        }
        return mInstance;
    }

    private User() {
        super();
        mPreferences = SavedSharedPreferences.getInstance();
    }

    public String getSignDate() {
        return mPreferences.getStringValue(SIGN_DATE, "");
    }

    public void setSignDate(String signDate) {
        mPreferences.setStringValue(SIGN_DATE, signDate);
    }

    public String getRowId() {
        return mPreferences.getStringValue(ROWID, "0");
    }

    public void setRowId(String rowId) {
        mPreferences.setStringValue(ROWID, rowId);
    }

    public String getAccount() {
        return mPreferences.getStringValue(ACCOUNT);
    }

    public void setAccount(String account) {
        mPreferences.setStringValue(ACCOUNT, account);
    }

    public String getToken() {
        return mPreferences.getStringValue(TOKEN);
    }

    public void setToken(String token) {
        mPreferences.setStringValue(TOKEN, token);
    }

    public boolean getLogin() {
        return mPreferences.getBooleanValue(LOGIN, false);
    }

    public void setLogin(boolean login) {
        mPreferences.setBooleanValue(LOGIN, login);
    }

    public boolean getInOnline() {
        return mPreferences.getBooleanValue(IN_ONLINE, false);
    }

    public void setInOnline(boolean inOnline) {
        mPreferences.setBooleanValue(IN_ONLINE, inOnline);
    }

    public boolean getOutOnline() {
        return mPreferences.getBooleanValue(OUT_ONLINE, false);
    }

    public void setOutOnline(boolean outOnline) {
        mPreferences.setBooleanValue(OUT_ONLINE, outOnline);
    }

    public String getProjectName() {
        return mPreferences.getStringValue(PROJECT_NAME);
    }

    public void setProjectName(String projectName) {
        mPreferences.setStringValue(PROJECT_NAME, projectName);
    }

    public String getProjectId() {
        return mPreferences.getStringValue(PROJECT_ID);
    }

    public void setProjectId(String projectId) {
        mPreferences.setStringValue(PROJECT_ID, projectId);
    }

    public String getInDeviceNo() {
        return mPreferences.getStringValue(IN_DEVICE_NO);
    }

    public void setInDeviceNo(String inDeviceNo) {
        mPreferences.setStringValue(IN_DEVICE_NO, inDeviceNo);
    }

    public String getOutDeviceNo() {
        return mPreferences.getStringValue(OUT_DEVICE_NO);
    }

    public void setOutDeviceNo(String outDeviceNo) {
        mPreferences.setStringValue(OUT_DEVICE_NO, outDeviceNo);
    }

    public String getUserName() {
        return mPreferences.getStringValue(USER_NAME);
    }

    public void setUserName(String userName) {
        mPreferences.setStringValue(USER_NAME, userName);
    }

    public String getPassWord() {
        return mPreferences.getStringValue(PASS_WORD);
    }

    public void setPassWord(String passWord) {
        mPreferences.setStringValue(PASS_WORD, passWord);
    }

    public void clearAll() {
        mPreferences.clearAll();
    }
}
