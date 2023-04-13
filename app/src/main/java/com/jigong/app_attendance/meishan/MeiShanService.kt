package com.jigong.app_attendance.meishan

import android.content.Intent
import android.os.IBinder
import cn.hutool.core.util.CharsetUtil
import cn.hutool.crypto.digest.HMac
import cn.hutool.crypto.digest.HmacAlgorithm
import com.alibaba.fastjson.JSON
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.bean.WorkerInfo
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.info.printAndLog
import com.jigong.app_attendance.mainpublic.BaseService
import com.jigong.app_attendance.mainpublic.GlobalStatusCode
import com.jigong.app_attendance.mainpublic.MyApplication
import com.jigong.app_attendance.utils.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*


class MeiShanService : BaseService() {

    private val mainScope = MainScope()

    /**
     * 从考勤表内一次性取出的最大考勤数量
     */
    private val limitCount = 50
    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
    private val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao

    private val time3: Long = 1000 * 60 * 3

    /**
     * 心跳时间，10分钟一次
     */
    private val time10: Long = 1000 * 60 * 10

    /**
     * 上传考勤，15分钟一次
     */
    private val timeAttendance: Long = 1000 * 60 * 15

    /**
     * 向平台上传工人信息，30分钟一次
     */
    private val timeWorker: Long = 1000 * 60 * 30

    override fun onCreate() {
        super.onCreate()
        "服务已开始".printAndLog()
        mainScope.launch(Dispatchers.Main) {
            run()
        }
    }

    private suspend fun run() = withContext(Dispatchers.IO) {
        /**
         * 向济工网平台获取工人信息，三分钟一次
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                getWorkerInfo()
            }
        }, 0, time3)
        /**
         * 向政府平台上传工人信息
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (workerInfoDao.count() > 0) {
                    uploadWorker()
                }
            }
        }, 0, timeWorker)
        /**
         * 向济工网平台上传工人code
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (workerInfoDao.count() > 0) {
                    uploadWorkerCode()
                }
            }
        }, 0, timeWorker)
        /**
         * 向济工网平台获取工人考勤信息，三分钟左右一次
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                getWorkerAttendance()
            }
        }, 0, time3)
        /**
         * 向平台上传考勤
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (attendanceInfoDao.count() > 0) {
                    uploadAttendance()
                }
            }
        }, 0, timeAttendance)
        /**
         * 进场设备心跳
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                deviceHeartbeat(User.getInstance().inDeviceNo, User.getInstance().joinPassword)
            }
        }, 0, time10)
        /**
         * 出场设备心跳
         */
        timer.schedule(object : TimerTask() {
            override fun run() {
                deviceHeartbeat(User.getInstance().outDeviceNo, User.getInstance().joinDevice)
            }
        }, 0, time10)
    }

    private fun uploadWorkerCode() {
        val workerList = if (workerInfoDao.count() > limitCount) {
            workerInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            workerInfoDao.loadAll()
        }
        workerList.forEach {
            if (it.workerCode.isNullOrEmpty()) {
                return@forEach
            }
            val map = mutableMapOf<String, Any>()
            map["idNumber"] = it.idNumber
            map["joinCity"] = User.getInstance().joinCity
            map["projectId"] = User.getInstance().projectId
            map["thirdNo"] = it.workerCode
            val result = doPostJson(GlobalCode.UPDATE_WORKER_THRID_NO, map)
            if (checkResult(GlobalCode.QUERY_TB_WORKER_BY_PROJECT_ID, result)) {
                workerInfoDao.delete(it)
            }
        }
    }

    private fun uploadWorker() {
        val workerList = if (workerInfoDao.count() > limitCount) {
            workerInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            workerInfoDao.loadAll()
        }
        workerList.forEach {
            if (!it.workerCode.isNullOrEmpty()) {
                return@forEach
            }
            val map = LinkedHashMap<String, String>()
            map["Idno"] = SM4IotUtil.encrypt(User.getInstance().joinCode, it.idNumber)
            map["Name"] = it.name
            map["gender"] = if (it.gender == GlobalStatusCode.MAN) "1" else "2"
            map["nation"] = it.nation
            map["birthday"] = deallWithBirthday(it.birthday)
            map["address"] = it.address
            map["idissue"] = it.idissue
            map["idperiod"] = it.idperiod.replace(".", "")
            map["idphoto"] = SM4IotUtil.encrypt(User.getInstance().joinCode, it.idphoto)
            map["photo"] = SM4IotUtil.encrypt(User.getInstance().joinCode, it.faceImage)
            map["userType"] = if (it.woreType == GlobalStatusCode.MANAGER) "2" else "1"
            map["sn"] = User.getInstance().collectionDevice
            map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
            map["sign"] = getSign(User.getInstance().joinCode, map)
            var doNext = true
            map.values.forEach { child ->
                if (child.isEmpty()) {
                    doNext = false
                }
            }
            if (doNext) {
                val result = doPost(MeiShanServer.registData, map)
                if (result.isEmpty()) {
                    "sn：${User.getInstance().collectionDevice} name：${it.name} 工人信息上传失败，平台方无返回".printAndLog()
                } else {
                    try {
                        val resultObject = JSONObject(result)
                        when (JsonUtils.getJsonValue(resultObject, "result", "")) {
                            "0" -> {
                                val resultContent = JsonUtils.getJsonValue(resultObject, "content", "")
                                val decrypt = SM4IotUtil.decrypt(User.getInstance().joinCode, resultContent)
                                val jsonObject = JSONObject(decrypt)
                                val userId = jsonObject.getString("userId")
                                if (userId.isNullOrEmpty()) {
                                    "sn：${User.getInstance().collectionDevice} name：${it.name} 人员上传无id返回，平台返回：${result}".printAndLog()
                                } else {
                                    it.workerCode = userId
                                    workerInfoDao.update(it)
                                    "sn：${User.getInstance().collectionDevice} name：${it.name} 工人信息上传成功".printAndLog()
                                }
                            }
                            else -> "sn：${User.getInstance().collectionDevice} name：${it.name} 工人信息上传失败，平台返回：${result}".printAndLog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun deallWithBirthday(birthday: String): String {
        if (birthday.length < 8) {
            return ""
        }
        val sb = StringBuilder(birthday)
        sb.insert(4, "-").insert(7, "-")
        return sb.toString()
    }

    private fun getWorkerInfo() {
        val map = HashMap<String, Any>()
        map["deviceSerialNo"] = getDeviceSN()
        map["joinCity"] = User.getInstance().joinCity
        map["projectId"] = User.getInstance().projectId
        map["rowId"] = User.getInstance().getWorkerRowId
        dealWorkerInfo(doPostJson(GlobalCode.QUERY_TB_WORKER_BY_PROJECT_ID, map))
    }

    private fun dealWorkerInfo(infoString: String) {
        if (checkResult(GlobalCode.QUERY_TB_WORKER_BY_PROJECT_ID, infoString)) {
            val jsonObject = JSONObject(infoString)
            val entry = JsonUtils.getJSONObject(jsonObject, "entry")
            if (entry != null) {
                val getWorkerRowId = JsonUtils.getJsonValue(entry, "rowId", "0")
                User.getInstance().getWorkerRowId = getWorkerRowId
                val jsonArray = JsonUtils.getJSONArray(entry, "result")
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {
                        val dataObject = jsonArray.getJSONObject(i)
                        if (dataObject != null) {
                            val workerInfo = WorkerInfo()
                            workerInfo.idNumber = JsonUtils.getJsonValue(dataObject, "idNumber", "")
                            workerInfo.name = JsonUtils.getJsonValue(dataObject, "name", "")
                            workerInfo.gender = JsonUtils.getJsonValue(dataObject, "sex", "")
                            workerInfo.nation = JsonUtils.getJsonValue(dataObject, "minor", "")
                            workerInfo.birthday = JsonUtils.getJsonValue(dataObject, "birthday", "")
                            workerInfo.address = JsonUtils.getJsonValue(dataObject, "address", "")
                            workerInfo.idissue = JsonUtils.getJsonValue(dataObject, "retreat", "")
                            workerInfo.idperiod = JsonUtils.getJsonValue(dataObject, "expiryDay", "")
                            val idPhoto = JsonUtils.getJsonValue(dataObject, "idFront", "")
                            if (!idPhoto.isNullOrEmpty()) {
                                workerInfo.idphoto = ConverUtils.netSourceToBase64(idPhoto, "GET")
                            } else {
                                workerInfo.idphoto = ""
                            }
                            val imageUrl = JsonUtils.getJsonValue(dataObject, "headImage", "")
                            if (!imageUrl.isNullOrEmpty()) {
                                workerInfo.faceImage = ConverUtils.netSourceToBase64(imageUrl, "GET")
                            } else {
                                workerInfo.faceImage = ""
                            }
                            workerInfo.woreType = JsonUtils.getJsonValue(dataObject, "admin", "")
                            workerInfoDao.insert(workerInfo)
                        }
                    }
                }
            }
        }
    }

    private fun uploadAttendance() {
        val attendanceList = if (attendanceInfoDao.count() > limitCount) {
            attendanceInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            attendanceInfoDao.loadAll()
        }
        attendanceList.forEach {
            val deviceId = if (it.machineType == GlobalStatusCode.machineOut) User.getInstance().outDeviceNo else User.getInstance().inDeviceNo
            val secretKey = if (it.machineType == GlobalStatusCode.machineOut) User.getInstance().joinDevice else User.getInstance().joinPassword
            val map = LinkedHashMap<String, String>()
            map["sn"] = deviceId
            val data = LinkedHashMap<String, String>()
            data["sn"] = deviceId
            data["userId"] = it.workerCode
            data["type"] = if (it.machineType == GlobalStatusCode.machineIn) "1" else "2"
            data["recogTime"] = it.checkinTime
            val dataList = mutableListOf<LinkedHashMap<String, String>>()
            dataList.add(data)
            map["data"] = SM4IotUtil.encrypt(secretKey, JSON.toJSONString(dataList))
            map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
            map["sign"] = getSign(secretKey, map)
            val result = doPostJsonNoHeader(MeiShanServer.uploadData, map)
            if (result.isEmpty()) {
                "sn：${deviceId} name：${it.workerName} 考勤上传失败，平台方无返回".printAndLog()
            } else {
                try {
                    val resultObject = JSONObject(result)
                    when (JsonUtils.getJsonValue(resultObject, "result", "")) {
                        "0" -> {
                            attendanceInfoDao.delete(it)
                            "sn：${deviceId} name：${it.workerName} 考勤上传成功".printAndLog()
                        }
                        else -> "sn：${deviceId} name：${it.workerName} 考勤上传失败，平台返回：${result}".printAndLog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getWorkerAttendance() {
        val map = HashMap<String, Any>()
        map["projectId"] = User.getInstance().projectId
        map["queryRowId"] = User.getInstance().rowId
        map["signDate"] = User.getInstance().signDate
        map["joinCity"] = User.getInstance().joinCity
        dealAttendanceInfo(doPostJson(GlobalCode.QUERY_PROJECT_SIGN_LIST_FOSHAN, map))
    }

    private fun dealAttendanceInfo(infoString: String) {
        if (checkResult(GlobalCode.QUERY_PROJECT_SIGN_LIST_FOSHAN, infoString)) {
            val jsonObject = JSONObject(infoString)
            val entry = JsonUtils.getJSONObject(jsonObject, "entry")
            if (entry != null) {
                val rowId = JsonUtils.getJsonValue(entry, "queryRowId", "0")
                User.getInstance().rowId = rowId
                val signDate = JsonUtils.getJsonValue(entry, "signDate", "0")
                User.getInstance().signDate = signDate
                val jsonArray = JsonUtils.getJSONArray(entry, "result")
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {
                        val dataObject = jsonArray.getJSONObject(i)
                        if (dataObject != null) {
                            val attendanceInfo = AttendanceInfo()
                            attendanceInfo.attendanceId = JsonUtils.getJsonValue(dataObject, "attendanceId", "")
                            attendanceInfo.checkinTime = JsonUtils.getJsonValue(dataObject, "checkinTime", "")
                            attendanceInfo.deviceSerialNo = JsonUtils.getJsonValue(dataObject, "deviceSerialNo", "")
                            attendanceInfo.idNumber = JsonUtils.getJsonValue(dataObject, "idNumber", "")
                            attendanceInfo.machineType = JsonUtils.getJsonValue(dataObject, "machineType", "") //                            attendanceInfo.normalSignImage = JsonUtils.getJsonValue(dataObject, "normalSignImage", "")
                            attendanceInfo.projectId = JsonUtils.getJsonValue(dataObject, "projectId", "")
                            attendanceInfo.subcontractorId = JsonUtils.getJsonValue(dataObject, "subcontractorId", "")
                            attendanceInfo.temperature = JsonUtils.getJsonValue(dataObject, "temperature", "")
                            attendanceInfo.workerCode = JsonUtils.getJsonValue(dataObject, "thirdId", "")
                            attendanceInfo.workerId = JsonUtils.getJsonValue(dataObject, "workerId", "")
                            attendanceInfo.workerName = JsonUtils.getJsonValue(dataObject, "workerName", "")
                            attendanceInfoDao.insert(attendanceInfo)
                        }
                    }
                }
            }
        }
    }

    private fun deviceHeartbeat(deviceId: String, secretKey: String) {
        val map = LinkedHashMap<String, String>()
        map["sn"] = deviceId
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = getSign(secretKey, map)
        val result = doPost(MeiShanServer.online, map)
        if (result.isEmpty()) {
            "心跳接口请求无返回  sn：$deviceId".printAndLog()
        } else {
            try {
                val resultObject = JSONObject(result)
                when (JsonUtils.getJsonValue(resultObject, "result", "")) {
                    "0" -> "心跳上传成功, 无任务返回  sn：$deviceId".printAndLog()
                    "1" -> {
                        val resultContent = JsonUtils.getJsonValue(resultObject, "content", "")
                        val decrypt = SM4IotUtil.decrypt(secretKey, resultContent)
                        val jsonArray = JSONArray(decrypt)
                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                when (JsonUtils.getJsonValue(jsonObject, "type", "")) {
                                    "1" -> dealWithWorkerOut(jsonObject, deviceId, secretKey)
                                    "2" -> dealWithWorkerIn(jsonObject, deviceId, secretKey)
                                }
                            }
                        }
                    }
                    else -> "心跳上传失败  sn：$deviceId  平台返回：$result".printAndLog()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 当平台下发入场任务时，忽略处理
     * 只需要像平台返回处理成功即可
     * 因为工人数据是我们推送到平台的，所以不需要处理
     *
     * @param jsonObject
     * @param deviceId
     * @param secretKey
     */
    private fun dealWithWorkerIn(jsonObject: JSONObject, deviceId: String, secretKey: String) {
        val map = LinkedHashMap<String, String>()
        map["sn"] = deviceId
        map["id"] = JsonUtils.getJsonValue(jsonObject, "id", "")
        map["result"] = "0"
        map["msg"] = "下发成功"
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = getSign(secretKey, map)
        val result = doPost(MeiShanServer.retResult, map)
        "sn：${deviceId}  人员下发反馈数据结果：$result".printAndLog()
    }

    /**
     * 当平台下发退场任务时，向济工网平台请求工人退场
     * 并且向平台返回处理成功
     *
     * @param jsonObject
     * @param deviceId
     * @param secretKey
     */
    private fun dealWithWorkerOut(jsonObject: JSONObject, deviceId: String, secretKey: String) {
        val map = LinkedHashMap<String, String>()
        map["sn"] = deviceId
        map["id"] = JsonUtils.getJsonValue(jsonObject, "id", "")
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = getSign(secretKey, map)
        val result = doPost(MeiShanServer.getTaskData, map)
        if (result.isEmpty()) {
            "获取退场任务失败  sn：$deviceId".printAndLog()
        } else {
            try {
                val resultObject = JSONObject(result)
                when (JsonUtils.getJsonValue(resultObject, "result", "")) {
                    "0" -> {
                        val resultContent = JsonUtils.getJsonValue(resultObject, "content", "")
                        val decrypt = SM4IotUtil.decrypt(secretKey, resultContent)
                        val jsonArray = JSONArray(decrypt)
                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                val itemObject = jsonArray.getJSONObject(i)
                                val userId = JsonUtils.getJsonValue(itemObject, "userId", "")
                                val outMap = HashMap<String, Any>()
                                outMap["joinCity"] = User.getInstance().joinCity
                                outMap["projectId"] = User.getInstance().projectId
                                outMap["thirdNo"] = userId
                                val pushInfo = doPostJson(GlobalCode.OUT_PROJECT_WORKER, outMap)
                                if (checkResult(GlobalCode.OUT_PROJECT_WORKER, pushInfo)) { //接口返回成功
                                    val success = LinkedHashMap<String, String>()
                                    success["sn"] = deviceId
                                    success["id"] = JsonUtils.getJsonValue(jsonObject, "id", "")
                                    success["result"] = "0"
                                    success["msg"] = "退场成功"
                                    success["timestamp"] = (System.currentTimeMillis() / 1000).toString()
                                    success["sign"] = getSign(secretKey, success)
                                    val successResult = doPost(MeiShanServer.retResult, success)
                                    "sn：$deviceId 人员退场反馈数据结果：$successResult".printAndLog()
                                } else {
                                    val default = LinkedHashMap<String, String>()
                                    default["sn"] = deviceId
                                    default["id"] = JsonUtils.getJsonValue(jsonObject, "id", "")
                                    default["result"] = "1"
                                    default["msg"] = getMsg(pushInfo)
                                    default["timestamp"] = (System.currentTimeMillis() / 1000).toString()
                                    default["sign"] = getSign(secretKey, default)
                                    val defaultResult = doPost(MeiShanServer.retResult, default)
                                    "sn：$deviceId 人员退场反馈数据结果：$defaultResult".printAndLog()
                                }
                            }
                        }
                    }
                    else -> "sn：${deviceId}  获取下发数据失败  平台返回：$result".printAndLog()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSign(key: String, map: LinkedHashMap<String, String>): String {
        val contentBuilder = StringBuilder()
        map.values.forEach {
            contentBuilder.append(it)
        }
        val content = contentBuilder.toString()
        val bytes = key.toByteArray(StandardCharsets.UTF_8)
        val hMac = HMac(HmacAlgorithm.HmacMD5, bytes)
        return hMac.digestHex(content, CharsetUtil.CHARSET_UTF_8)
    }

    override fun onDestroy() {
        mainScope.cancel()
        "服务已销毁".printAndLog()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}