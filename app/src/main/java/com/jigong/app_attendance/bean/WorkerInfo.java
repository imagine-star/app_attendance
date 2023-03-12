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
    private byte[] picURI;//人员照片 uri 地址
    private boolean getInfo = false;//是否从平台获取到了工人信息，false未获取到，true已获取到

    @Generated(hash = 35714920)
    public WorkerInfo(Long id, String customId, String workerId, String workerCode,
            String name, String idNumber, String personType, String gender, String idCard,
            String cardType, String birthday, byte[] picURI, boolean getInfo) {
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

}
