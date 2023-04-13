package com.jigong.app_attendance.mainpublic

/**
 * @Author LiuHaoQi
 * @Description 需要对比的各类参数定义，方便理解
 * @Date 2023/4/13 9:27
 */
object GlobalStatusCode {
    /**
     * machineType考勤机器方向为进
     */
    const val machineIn = "02"

    /**
     * machineType考勤机方向为出
     */
    const val machineOut = "01"

    /**
     * isAdmin管理人员类型
     */
    const val MANAGER = "1"

    /**
     * isAdmin普通工人类型
     */
    const val WORKER = "2"

    /**
     * gender男
     */
    const val MAN = "1"

    /**
     * gender女
     */
    const val WOMAN = "0"
}