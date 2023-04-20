package com.jigong.app_attendance.info;

import java.util.Date;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 13:36
 */
public class User {

    private final String userName = "userName";
    private final String password = "password";
    private final String collectionDevice = "collectionDevice";
    private final String inDeviceNo = "inDeviceNo";
    private final String outDeviceNo = "outDeviceNo";
    private final String projectId = "projectId";
    private final String projectName = "projectName";
    private final String inOnline = "inOnline";
    private final String outOnline = "outOnline";
    private final String login = "login";
    private final String joinCity = "joinCity";
    private final String rowId = "rowId";
    private final String getWorkerRowId = "getWorkerRowId";
    private final String signDate = "signDate";
    private final String gomeetToken = "gomeetToken";
    private final String joinCode = "joinCode";
    private final String joinPassword = "joinPassword";
    private final String joinProject = "joinProject";
    private final String joinDevice = "joinDevice";
    private final String joinPlatform = "joinPlatform";
    private final String developKey = "developKey";
    private final String developSecret = "developSecret";
    private final String deleteTime = "deleteTime";
    private final String loginPassword = "loginPassword";

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
        return mPreferences.getStringValue(signDate, "");
    }

    public void setSignDate(String signDate) {
        mPreferences.setStringValue(this.signDate, signDate);
    }

    public String getRowId() {
        return mPreferences.getStringValue(rowId, "0");
    }

    public void setRowId(String rowId) {
        mPreferences.setStringValue(this.rowId, rowId);
    }

    public String getGetWorkerRowId() {
        return mPreferences.getStringValue(getWorkerRowId, "0");
    }

    public void setGetWorkerRowId(String getWorkerRowId) {
        mPreferences.setStringValue(this.getWorkerRowId, getWorkerRowId);
    }

    public String getJoinCity() {
        return mPreferences.getStringValue(joinCity);
    }

    public void setJoinCity(String joinCity) {
        mPreferences.setStringValue(this.joinCity, joinCity);
    }

    public String getJoinCode() {
        return mPreferences.getStringValue(joinCode);
    }

    public void setJoinCode(String joinCode) {
        mPreferences.setStringValue(this.joinCode, joinCode);
    }

    public boolean getLogin() {
        return mPreferences.getBooleanValue(login, false);
    }

    public void setLogin(boolean login) {
        mPreferences.setBooleanValue(this.login, login);
    }

    public boolean getInOnline() {
        return mPreferences.getBooleanValue(inOnline, false);
    }

    public void setInOnline(boolean inOnline) {
        mPreferences.setBooleanValue(this.inOnline, inOnline);
        if (inOnlineChangeListener != null) {
            inOnlineChangeListener.onChange();
        }
    }

    public boolean getOutOnline() {
        return mPreferences.getBooleanValue(outOnline, false);
    }

    public void setOutOnline(boolean outOnline) {
        mPreferences.setBooleanValue(this.outOnline, outOnline);
        if (outOnlineChangeListener != null) {
            outOnlineChangeListener.onChange();
        }
    }

    public String getProjectName() {
        return mPreferences.getStringValue(projectName);
    }

    public void setProjectName(String projectName) {
        mPreferences.setStringValue(this.projectName, projectName);
    }

    public String getProjectId() {
        return mPreferences.getStringValue(projectId);
    }

    public void setProjectId(String projectId) {
        mPreferences.setStringValue(this.projectId, projectId);
    }

    public String getInDeviceNo() {
        return mPreferences.getStringValue(inDeviceNo);
    }

    public void setInDeviceNo(String inDeviceNo) {
        mPreferences.setStringValue(this.inDeviceNo, inDeviceNo);
    }

    public String getOutDeviceNo() {
        return mPreferences.getStringValue(outDeviceNo);
    }

    public void setOutDeviceNo(String outDeviceNo) {
        mPreferences.setStringValue(this.outDeviceNo, outDeviceNo);
    }

    public String getUserName() {
        return mPreferences.getStringValue(userName);
    }

    public void setUserName(String userName) {
        mPreferences.setStringValue(this.userName, userName);
    }

    public String getPassWord() {
        return mPreferences.getStringValue(password);
    }

    public void setPassWord(String passWord) {
        mPreferences.setStringValue(password, passWord);
    }

    public void setGomeetToken(String gomeetToken) {
        mPreferences.setStringValue(this.gomeetToken, gomeetToken);
    }

    public String getGomeetToken() {
        return mPreferences.getStringValue(gomeetToken);
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

    public Long getDeleteTime() {
        return mPreferences.getLongValue(deleteTime, 0);
    }

    public void setDeleteTime(Long deleteTime) {
        mPreferences.setLongValue(this.deleteTime, deleteTime);
    }

    public String getLoginPassword() {
        return mPreferences.getStringValue(loginPassword, "");
    }

    public void setLoginPassword(String loginPassword) {
        mPreferences.setStringValue(this.loginPassword, loginPassword);
    }

    public String getCollectionDevice() {
        return mPreferences.getStringValue(collectionDevice);
    }

    public void setCollectionDevice(String collectionDevice) {
        mPreferences.setStringValue(this.collectionDevice, collectionDevice);
    }

    public void clearAll() {
        mPreferences.clearAll();
    }
}
