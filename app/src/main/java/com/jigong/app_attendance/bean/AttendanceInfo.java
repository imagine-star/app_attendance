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

    @Generated(hash = 747837634)
    public AttendanceInfo(Long id) {
        this.id = id;
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

}
