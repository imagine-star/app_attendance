package com.jigong.app_attendance.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/11 13:33
 */
@Entity
public class AttendanceInfo {

    @Id
    private Long id;
    private String attendanceId;
    private String checkinTime;
    private String deviceSerialNo;
    private String machineType;
    private String normalSignImage;
    private String projectId;
    private String redSignImage;
    private String subcontractorId;
    private String temperature;
    private String workerId;
    private String workerName;
    private String idNumber;

    @Generated(hash = 580934522)
    public AttendanceInfo(Long id, String attendanceId, String checkinTime,
            String deviceSerialNo, String machineType, String normalSignImage,
            String projectId, String redSignImage, String subcontractorId,
            String temperature, String workerId, String workerName,
            String idNumber) {
        this.id = id;
        this.attendanceId = attendanceId;
        this.checkinTime = checkinTime;
        this.deviceSerialNo = deviceSerialNo;
        this.machineType = machineType;
        this.normalSignImage = normalSignImage;
        this.projectId = projectId;
        this.redSignImage = redSignImage;
        this.subcontractorId = subcontractorId;
        this.temperature = temperature;
        this.workerId = workerId;
        this.workerName = workerName;
        this.idNumber = idNumber;
    }

    @Generated(hash = 495039263)
    public AttendanceInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttendanceId() {
        return this.attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getDeviceSerialNo() {
        return this.deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }

    public String getMachineType() {
        return this.machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getNormalSignImage() {
        return this.normalSignImage;
    }

    public void setNormalSignImage(String normalSignImage) {
        this.normalSignImage = normalSignImage;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRedSignImage() {
        return this.redSignImage;
    }

    public void setRedSignImage(String redSignImage) {
        this.redSignImage = redSignImage;
    }

    public String getSubcontractorId() {
        return this.subcontractorId;
    }

    public void setSubcontractorId(String subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWorkerId() {
        return this.workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return this.workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getCheckinTime() {
        return this.checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

}
