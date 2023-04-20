package com.jigong.app_attendance.hefei

import com.jigong.app_attendance.info.User

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2023/4/20 16:25
 */
object HeFeiServer {
    /**
     * 合肥平台指令前缀
     */
    var TOPIC_PREFIX = "mqtt/" + User.getInstance().userName + "/"

    /**
     * 向合肥平台推送上线/下线通知
     */
    var BASIC_PUSH = "mqtt/" + User.getInstance().userName + "/basic"

    /**
     * 向合肥平台推送心跳通知
     */
    var HEARTBEAT_PUSH = "mqtt/" + User.getInstance().userName + "/heartbeat"
}