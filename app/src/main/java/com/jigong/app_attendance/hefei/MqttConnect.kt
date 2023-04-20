package com.jigong.app_attendance.hefei

import com.alibaba.fastjson.JSON
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.utils.*
import kotlinx.coroutines.*

/**
 * @Author LiuHaoQi
 * @Description 一些连接服务的请求函数
 * @Date 2022/11/10 16:11
 */

/*
* Mqtt连接初始化
* */
suspend fun mqttStart(deviceNo: String) = withContext(Dispatchers.IO) {
        MyMqttClient.getInstance(User.getInstance().userName, User.getInstance().passWord, deviceNo)
}

/*
* 订阅函数调用简化
* */
suspend fun get(topic: String, deviceNo: String) = withContext(Dispatchers.IO) {
        MyMqttClient.subTopic(topic, User.getInstance().userName, User.getInstance().passWord, deviceNo)
}

/*
* 推送函数调用简化
* */
suspend fun push(topic: String, data: String, deviceNo: String) = withContext(Dispatchers.IO) {
        MyMqttClient.pub(topic, data, User.getInstance().userName, User.getInstance().passWord, deviceNo)
}

/*
* Mqtt订阅设备在线主题
* */
suspend fun getBasic(deviceNo: String) = withContext(Dispatchers.IO) {
        get(HeFeiServer.TOPIC_PREFIX + deviceNo, deviceNo)
}

/*
* Mqtt推送设备在线相关信息
* */
suspend fun pushBasicOnline(deviceNo: String) = withContext(Dispatchers.IO) {
        val getMap = async {
                getOnlineDataMap(deviceNo)
        }
        push(HeFeiServer.BASIC_PUSH, JSON.toJSONString(getMap.await()), deviceNo)
}

/*
* Mqtt推送设备心跳
* */
suspend fun pushHeartbeat(deviceNo: String) = withContext(Dispatchers.IO) {
        push(HeFeiServer.HEARTBEAT_PUSH, JSON.toJSONString(getHeartbeatDataMap(deviceNo)), deviceNo)
}

/*
* Mqtt推送设备下线相关信息
* */
suspend fun pushBasicOffline(deviceNo: String) = withContext(Dispatchers.IO) {
        push(HeFeiServer.BASIC_PUSH, JSON.toJSONString(getOfflineDataMap(deviceNo)), deviceNo)
}

/*
* Mqtt推送指令回复--接收下发人员信息
* */
suspend fun pushReplyWorkerInfo(messageId: String, sucNum: Int, errNum: Int, deviceNo: String) = withContext(Dispatchers.IO) {
        push(HeFeiServer.TOPIC_PREFIX + deviceNo + "/Ack", JSON.toJSONString(getReplyGetWorkerInfoDataMap(deviceNo, messageId, sucNum, errNum)), deviceNo)
}

/*
* Mqtt向平台推送考勤数据
* */
suspend fun pushAttendance(attendanceInfo: AttendanceInfo, deviceNo: String) = withContext(Dispatchers.IO) {
        /*
        * 由于上传参数中有需要网络获取的图片数据
        * 所以在这里使用协程下载图片
        * */
        val getMap = async {
                getAttendanceDataMap(deviceNo, attendanceInfo)
        }
        push(HeFeiServer.TOPIC_PREFIX + deviceNo + "/Rec", JSON.toJSONString(getMap.await()), deviceNo)
}