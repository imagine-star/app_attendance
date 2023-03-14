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
    private final String ACCOUNT = "ACCOUNT";
    private final String ROWID = "ROWID";
    private final String SIGN_DATE = "SIGN_DATE";
    private final String GOMEET_TOKEN = "GOMEET_TOKEN";
    private final String joinCode = "joinCode";
    private final String joinPassword = "joinPassword";
    private final String joinProject = "joinProject";
    private final String joinDevice = "joinDevice";
    private final String joinPlatform = "joinPlatform";
    private final String developKey = "developKey";
    private final String developSecret = "developSecret";

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

    public interface InOnlineChangeListener {	// 创建interface类
        void onChange();    // 值改变
    }
    private static InOnlineChangeListener inOnlineChangeListener;	// 声明interface接口
    public static void setInOnlineChangeListener(InOnlineChangeListener onChange){	// 创建setListener方法
        inOnlineChangeListener = onChange;
    }

    public interface OutOnlineChangeListener {	// 创建interface类
        void onChange();    // 值改变
    }
    private static OutOnlineChangeListener outOnlineChangeListener;	// 声明interface接口
    public static void setOutOnlineChangeListener(OutOnlineChangeListener onChange){	// 创建setListener方法
        outOnlineChangeListener = onChange;
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

    public String getJoinCode() {
        return mPreferences.getStringValue(joinCode);
    }

    public void setJoinCode(String joinCode) {
        mPreferences.setStringValue(this.joinCode, joinCode);
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
        if (inOnlineChangeListener != null) {
            inOnlineChangeListener.onChange();
        }
    }

    public boolean getOutOnline() {
        return mPreferences.getBooleanValue(OUT_ONLINE, false);
    }

    public void setOutOnline(boolean outOnline) {
        mPreferences.setBooleanValue(OUT_ONLINE, outOnline);
        if (outOnlineChangeListener != null) {
            outOnlineChangeListener.onChange();
        }
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

    public void setGomeetToken(String gomeetToken) {
        mPreferences.setStringValue(GOMEET_TOKEN, gomeetToken);
    }

    public String getGomeetToken() {
        return mPreferences.getStringValue(GOMEET_TOKEN);
    }

    public String getJoinPassword() {
        return mPreferences.getStringValue(joinPassword);
    }

    public void setJoinPassword(String joinPassword) {
        mPreferences.setStringValue(this.joinPassword, joinPassword);
    }

    public String getJoinProject() {
        return mPreferences.getStringValue(joinProject);
    }

    public void setJoinProject(String joinProject) {
        mPreferences.setStringValue(this.joinProject, joinProject);
    }

    public String getJoinDevice() {
        return mPreferences.getStringValue(joinDevice);
    }

    public void setJoinDevice(String joinDevice) {
        mPreferences.setStringValue(this.joinDevice, joinDevice);
    }

    public String getJoinPlatform() {
        return mPreferences.getStringValue(joinPlatform);
    }

    public void setJoinPlatform(String joinPlatform) {
        mPreferences.setStringValue(this.joinPlatform, joinPlatform);
    }

    public String getDevelopKey() {
        return mPreferences.getStringValue(developKey);
    }

    public void setDevelopKey(String developKey) {
        mPreferences.setStringValue(this.developKey, developKey);
    }

    public String getDevelopSecret() {
        return mPreferences.getStringValue(developSecret);
    }

    public void setDevelopSecret(String developSecret) {
        mPreferences.setStringValue(this.developSecret, developSecret);
    }

    public void clearAll() {
        mPreferences.clearAll();
    }
}
