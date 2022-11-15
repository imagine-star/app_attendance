package com.jigong.app_attendance.hefei

import android.text.TextUtils
import com.jigong.app_attendance.MyApplication
import com.jigong.app_attendance.bean.WorkerInfo
import com.jigong.app_attendance.greendao.AttendanceInfoDao
import com.jigong.app_attendance.greendao.WorkerInfoDao
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.utils.JsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author LiuHaoQi
 * @Description 处理服务端返回的数据
 * @Date 2022/11/10 17:23
 */

/*
* 订阅上线请求返回值的标识
* */
const val ONLINE = "Online-Ack"
/*
* 订阅获取工人信息返回值的标识
* */
const val GET_WORKER = "EditPersonsNew"
/*
* 上传考勤返回消息
* */
const val ATTENDANCE_RESULT = "PushAck"

/*
* 服务返回数据分发中心，通过此函数处理不同类型的数据返回
* 简单处理直接在此函数中处理，，例如改变上线状态
* 复杂处理另起函数处理逻辑，避免中心过于臃肿
* */
fun dealManager(deviceNo: String, message: String) {

    val jsonObject = JSONObject(message)
    val operator = JsonUtils.getJsonValue(jsonObject, "operator", "")
    when (operator) {
        //设备上线请求的返回，据此修改设备在线状态为在线，让等待的行为进入循环
        ONLINE -> dealOnline(deviceNo, jsonObject)
        GET_WORKER -> dealWorkerInfo(deviceNo, jsonObject)
        ATTENDANCE_RESULT -> deleteAttendance(jsonObject)
        else -> return
    }
}

/*
* 上传考勤返回相应的成功后，应该将对应考勤删除
* */
private fun deleteAttendance(jsonObject: JSONObject) {
    val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao
    val info = JsonUtils.getJSONObject(jsonObject, "info")
    if (info != null) {
        val recordId = JsonUtils.getJsonValue(info, "SnapOrRecordID", "")
        if (!TextUtils.isEmpty(recordId)) {
            val attendanceInfo = attendanceInfoDao.queryBuilder().where(AttendanceInfoDao.Properties.AttendanceId.eq(recordId)).unique()
            attendanceInfoDao.delete(attendanceInfo)
        }
    }
}

/*
* 接收到下发工人信息指令后
* 将工人入库/信息修改
* 操作结束后调用对应指令回复
* */
private fun dealWorkerInfo(deviceNo: String, jsonObject: JSONObject) {
    val messageId = JsonUtils.getJsonValue(jsonObject, "messageId", "")
    val jsonArray = JsonUtils.getJSONArray(jsonObject, "info")
    val sucNum = jsonArray.length()
    var errNum = sucNum
    if (jsonArray != null && jsonArray.length() > 0) {
        val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
        for (i in 0 until jsonArray.length()) {
            val workerInfo = WorkerInfo()
            val infoObject = jsonArray.getJSONObject(i)
            workerInfo.customId = JsonUtils.getJsonValue(infoObject, "customId", "")
            workerInfo.name = JsonUtils.getJsonValue(infoObject, "name", "")
            workerInfo.personType = JsonUtils.getJsonValue(infoObject, "personType", "0")
            workerInfo.gender = JsonUtils.getJsonValue(infoObject, "gender", "1")
            workerInfo.idCard = JsonUtils.getJsonValue(infoObject, "idCard", "")
            workerInfo.cardType = JsonUtils.getJsonValue(infoObject, "cardType", "")
            workerInfo.birthday = JsonUtils.getJsonValue(infoObject, "birthday", "")
            workerInfo.picURI = JsonUtils.getJsonValue(infoObject, "picURI", "")
            val queryWorker = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.CustomId.eq(workerInfo.customId)).unique()
            if (queryWorker == null) {
                workerInfoDao.insert(workerInfo)
            } else {
                workerInfo.id = queryWorker.id
                workerInfoDao.update(workerInfo)
            }
            errNum--
        }
    }
    runBlocking {
        launch(Dispatchers.IO) {
            pushReplyWorkerInfo(messageId, sucNum, errNum, deviceNo)
        }
    }
}

private fun dealOnline(deviceNo: String, jsonObject: JSONObject) {
    val info = JsonUtils.getJSONObject(jsonObject, "info")
    if (info != null) {
        val result = JsonUtils.getJsonValue(info, "result", "")
        if ("ok" == result) {
            if (User.getInstance().inDeviceNo == deviceNo) {
                User.getInstance().inOnline = true
            }
            if (User.getInstance().outDeviceNo == deviceNo) {
                User.getInstance().outOnline = true
            }
        }
    }
}