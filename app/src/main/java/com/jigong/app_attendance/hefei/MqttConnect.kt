package com.jigong.app_attendance.hefei

import com.alibaba.fastjson.JSON
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.info.PublicTopicAddress
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
fun mqttStart(deviceNo: String) = MyMqttClient.getInstance(User.getInstance().userName, User.getInstance().passWord, deviceNo)

/*
* 订阅函数调用简化
* */
fun get(topic: String, deviceNo: String) = MyMqttClient.subTopic(topic, User.getInstance().userName, User.getInstance().passWord, deviceNo)

/*
* 推送函数调用简化
* */
fun push(topic: String, data: String, deviceNo: String) = MyMqttClient.pub(topic, data, User.getInstance().userName, User.getInstance().passWord, deviceNo)

/*
* Mqtt订阅设备在线主题
* */
fun getBasic(deviceNo: String) = get(PublicTopicAddress.TOPIC_PREFIX + deviceNo, deviceNo)

/*
* Mqtt推送设备在线相关信息
* */
suspend fun pushBasicOnline(deviceNo: String) = withContext(Dispatchers.IO) {
        val getMap = async {
                getOnlineDataMap(deviceNo)
        }
        push(PublicTopicAddress.BASIC_PUSH, JSON.toJSONString(getMap.await()), deviceNo)
}

/*
* Mqtt推送设备心跳
* */
fun pushHeartbeat(deviceNo: String) = push(PublicTopicAddress.HEARTBEAT_PUSH, JSON.toJSONString(getHeartbeatDataMap(deviceNo)), deviceNo)

/*
* Mqtt推送设备下线相关信息
* */
fun pushBasicOffline(deviceNo: String) = push(PublicTopicAddress.BASIC_PUSH, JSON.toJSONString(getOfflineDataMap(deviceNo)), deviceNo)

/*
* Mqtt推送指令回复--接收下发人员信息
* */
fun pushReplyWorkerInfo(messageId: String, sucNum: Int, errNum: Int, deviceNo: String) =
        push(PublicTopicAddress.TOPIC_PREFIX + deviceNo + "/Ack", JSON.toJSONString(getReplyGetWorkerInfoDataMap(deviceNo, messageId, sucNum, errNum)), deviceNo)

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
        push(PublicTopicAddress.TOPIC_PREFIX + deviceNo + "/Rec", JSON.toJSONString(getMap.await()), deviceNo)
}