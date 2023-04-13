package com.jigong.app_attendance.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 14:14
 */
@Entity
public class WorkerInfo {

    @Id
    private Long id;
    private String customId;//人员唯一 ID(最长 48 字符), 如果设备存在该id,则为修改操作,否则为新增操作
    private String workerId;
    private String workerCode;//佛山平台工人编号
    private String name;//人员姓名(最长 48 字符)
    private String idNumber;
    private String personType;//人员类型 0: 白名单 1: 黑名单
    private String gender;//人员性别 0: 男 1: 女
    private String idCard;//证件号码(最长 32 字符)
    private String cardType;//(可选)证件类型：0 身份证
    private String birthday;//(可选)生日: 格式 1960-01-31
    private byte[] picURI;//人员照片（字节流）
    private boolean getInfo = false;//是否从平台获取到了工人信息，false未获取到，true已获取到
    private boolean hasPush = false;//是否已上传济工网平台，false未上传，true已上传
    /*
    * 湖南新增
    * */
    private String isLeader;//是否领导
    private String workRole;//人员类型
    private String woreType;//岗位类型
    private String faceImage;//人员照片（base64）
    private boolean present = true;//人员是否在场，此字段作为人员是否需要删除凭证，false时删除
    /*
    * 眉山新增
    * */
    private String nation;//民族
    private String address;//住址
    private String idissue;//发证机关
    private String idperiod;//证件有效期
    private String idphoto;//身份证照片

    @Generated(hash = 1041479349)
    public WorkerInfo(Long id, String customId, String workerId, String workerCode,
            String name, String idNumber, String personType, String gender, String idCard,
            String cardType, String birthday, byte[] picURI, boolean getInfo, boolean hasPush,
            String isLeader, String workRole, String woreType, String faceImage,
            boolean present, String nation, String address, String idissue, String idperiod,
            String idphoto) {
        this.id = id;
        this.customId = customId;
        this.workerId = workerId;
        this.workerCode = workerCode;
        this.name = name;
        this.idNumber = idNumber;
        this.personType = personType;
        this.gender = gender;
        this.idCard = idCard;
        this.cardType = cardType;
        this.birthday = birthday;
        this.picURI = picURI;
        this.getInfo = getInfo;
        this.hasPush = hasPush;
        this.isLeader = isLeader;
        this.workRole = workRole;
        this.woreType = woreType;
        this.faceImage = faceImage;
        this.present = present;
        this.nation = nation;
        this.address = address;
        this.idissue = idissue;
        this.idperiod = idperiod;
        this.idphoto = idphoto;
    }

    @Generated(hash = 1000580303)
    public WorkerInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomId() {
        return this.customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getWorkerId() {
        return this.workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerCode() {
        return this.workerCode;
    }

    public void setWorkerCode(String workerCode) {
        this.workerCode = workerCode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPersonType() {
        return this.personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public byte[] getPicURI() {
        return this.picURI;
    }

    public void setPicURI(byte[] picURI) {
        this.picURI = picURI;
    }

    public boolean getGetInfo() {
        return this.getInfo;
    }

    public void setGetInfo(boolean getInfo) {
        this.getInfo = getInfo;
    }

    public boolean getHasPush() {
        return this.hasPush;
    }

    public void setHasPush(boolean hasPush) {
        this.hasPush = hasPush;
    }

    public String getIsLeader() {
        return this.isLeader;
    }

    public void setIsLeader(String isLeader) {
        this.isLeader = isLeader;
    }

    public String getWorkRole() {
        return this.workRole;
    }

    public void setWorkRole(String workRole) {
        this.workRole = workRole;
    }

    public String getWoreType() {
        return this.woreType;
    }

    public void setWoreType(String woreType) {
        this.woreType = woreType;
    }

    public String getFaceImage() {
        return this.faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public boolean getPresent() {
        return this.present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getNation() {
        return this.nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getIdissue() {
        return this.idissue;
    }

    public void setIdissue(String idissue) {
        this.idissue = idissue;
    }

    public String getIdperiod() {
        return this.idperiod;
    }

    public void setIdperiod(String idperiod) {
        this.idperiod = idperiod;
    }

    public String getIdphoto() {
        return this.idphoto;
    }

    public void setIdphoto(String idphoto) {
        this.idphoto = idphoto;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
