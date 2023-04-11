package com.jigong.app_attendance.hunan

import android.content.Intent
import android.os.IBinder
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.bean.WorkerInfo
import com.jigong.app_attendance.greendao.WorkerInfoDao
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.info.printAndLog
import com.jigong.app_attendance.mainpublic.BaseService
import com.jigong.app_attendance.mainpublic.MyApplication
import com.jigong.app_attendance.utils.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.TimerTask

class HuNanService : BaseService() {

    private val mainScope = MainScope()

    private val time3: Long = 1000 * 60 * 3

    private val limitCount = 50
    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
    private val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao

    @DelicateCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        "服务已开始".printAndLog()
        mainScope.launch(Dispatchers.Main) {
            run()
        }
    }

    private val applyId = "ac798e8381e642289526cac9df8ceb2d"
    private val supplierCode = "486611842647461888"
    private val deviceId = "84E0F42EB34F1608"
    private val deviceToken = "b8769cd488054367b33cff35e816d89c"

    private suspend fun run() = withContext(Dispatchers.IO) {

        timer.schedule(object : TimerTask() {
            override fun run() {
                val map: MutableMap<String, String> = mutableMapOf()
                map["applyId"] = applyId
                map["supplierCode"] = supplierCode
                map["deviceId"] = deviceId
                map["deviceToken"] = deviceToken
                val result = doPostJson(applyId, "${HuNanPublicInfo.SEVER_PORT}/${HuNanPublicInfo.GET_STAFF_INFO_BY_SN}", map)
                if (dealResult("平台获取考勤设备任务", result)) {
                    dealWorkerInfo(result)
                }
            }
        }, 0, time3)

        /*
        * 向济工网平台上传工人信息（工人信息表不为空时调用），三分钟左右一次
        * */
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (workerInfoDao.count() > 0) {
                    pushWorkerInfo()
                }
            }
        }, 0, time3)

        /*
        * 向济工网平台获取工人考勤信息，三分钟左右一次
        * */
        timer.schedule(object : TimerTask() {
            override fun run() {
                getWorkerAttendance()
            }
        }, 0, time3)

        timer.schedule(object : TimerTask() {
            override fun run() {
                val attendanceList = if (attendanceInfoDao.count() > limitCount) {
                    attendanceInfoDao.queryBuilder().limit(limitCount).list()
                } else {
                    attendanceInfoDao.loadAll()
                }
                attendanceList.forEach {
                    val map: MutableMap<String, Any> = mutableMapOf()
                    map["applyId"] = applyId
                    map["supplierCode"] = supplierCode
                    map["deviceId"] = deviceId
                    map["deviceToken"] = deviceToken
                    val dataMap: MutableMap<String, String> = mutableMapOf()
                    dataMap["workRole"] = it.workRole
                    dataMap["woreType"] = it.woreType
                    dataMap["channel"] = if (it.machineType.equals("02")) User.getInstance().inDeviceNo else User.getInstance().outDeviceNo
                    dataMap["recogType"] = "001"
                    dataMap["swipeTime"] = DateUtils.date2Str(Date(), DateUtils.FORMAT_YYYY_MM_DD_HH_MM_SS)
                    dataMap["idCardNumber"] = it.idNumber
                    dataMap["userId"] = it.workerCode
                    dataMap["direction"] = if (it.machineType.equals("02")) "01" else "02"
                    dataMap["photo"] = com.jigong.app_attendance.foshan.ConverUtils.netSourceToBase64(it.normalSignImage, "GET")
                    map["data"] = dataMap
                    val result = doPostJson(applyId, "${HuNanPublicInfo.SEVER_PORT}/${HuNanPublicInfo.GET_STAFF_INFO_BY_SN}", map)
                    if (dealResult("设备推送考勤数据", result)) {
                        attendanceInfoDao.delete(it)
                    }
                }
            }
        }, 0, time3)

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
                            attendanceInfo.machineType = JsonUtils.getJsonValue(dataObject, "machineType", "")
                            attendanceInfo.normalSignImage = JsonUtils.getJsonValue(dataObject, "normalSignImage", "")
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

    private fun pushWorkerInfo() {
        val workerList = if (workerInfoDao.count() > limitCount) {
            workerInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            workerInfoDao.loadAll()
        }
        workerList.forEach {
            if (it.hasPush) {
                return@forEach
            }
            if (it.idNumber.isNullOrEmpty() || it.workerCode.isNullOrEmpty() || it.name.isNullOrEmpty()) {
                return@forEach
            }
            val map = HashMap<String, Any>()
            map["joinCity"] = User.getInstance().joinCity
            map["projectId"] = User.getInstance().projectId
            val listMap = ArrayList<Map<String, Any>>()
            val dataMap = HashMap<String, Any>()
            dataMap["idNumber"] = it.idNumber
            dataMap["thirdNo"] = it.workerCode
            dataMap["name"] = it.name
            if (it.picURI != null && it.picURI.isNotEmpty()) {
                dataMap["photo"] = it.picURI
            }
            listMap.add(dataMap)
            map["workerList"] = listMap
            val pushInfo = doPostJson(GlobalCode.UPLOAD_WORKER_FOSHAN, map)
            if (checkResult(GlobalCode.UPLOAD_WORKER_FOSHAN, pushInfo)) {
                it.hasPush = true
                workerInfoDao.update(it)
            }
        }
    }

    private fun dealWorkerInfo(result: String) {
        val jsonObject = JSONObject(result)
        val data = JsonUtils.getJSONObject(jsonObject, "data")
        val cmd = JsonUtils.getJsonValue(data, "cmd", "21")
        val taskId = JsonUtils.getJsonValue(data, "taskId", "")
        if (cmd.equals("21")) {
            val jsonArray = JsonUtils.getJSONArray(data, "taskData")
            if (jsonArray != null && jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    val taskData = jsonArray.getJSONObject(i)
                    val idCardNumber = JsonUtils.getJsonValue(taskData, "idCardNumber", "")
                    if (idCardNumber.isNullOrEmpty()) {
                        continue
                    } else {
                        val workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idCardNumber)).unique()
                        if (workerInfo == null) {
                            val newInfo = WorkerInfo()
                            newInfo.workerCode = JsonUtils.getJsonValue(taskData, "userId", "")
                            newInfo.name = JsonUtils.getJsonValue(taskData, "name", "")
                            newInfo.isLeader = JsonUtils.getJsonValue(taskData, "isLeader", "")
                            newInfo.workRole = JsonUtils.getJsonValue(taskData, "workRole", "")
                            newInfo.woreType = JsonUtils.getJsonValue(taskData, "woreType", "")
                            newInfo.idNumber = idCardNumber
                            newInfo.faceImage = JsonUtils.getJsonValue(taskData, "faceImage", "")

                            newInfo.hasPush = false
                            newInfo.present = true
                            workerInfoDao.insert(newInfo)
                        } else {
                            workerInfo.workerCode = JsonUtils.getJsonValue(taskData, "userId", "")
                            workerInfo.name = JsonUtils.getJsonValue(taskData, "name", "")
                            workerInfo.isLeader = JsonUtils.getJsonValue(taskData, "isLeader", "")
                            workerInfo.workRole = JsonUtils.getJsonValue(taskData, "workRole", "")
                            workerInfo.woreType = JsonUtils.getJsonValue(taskData, "woreType", "")
                            workerInfo.idNumber = idCardNumber
                            workerInfo.faceImage = JsonUtils.getJsonValue(taskData, "faceImage", "")

                            workerInfo.hasPush = false
                            workerInfo.present = true
                            workerInfoDao.update(workerInfo)
                        }
                    }
                }
            }
        } else {
            val jsonArray = JsonUtils.getJSONArray(data, "taskData")
            for (i in 0 until jsonArray.length()) {
                val taskData = jsonArray.getJSONObject(i)
                val idCardNumber = JsonUtils.getJsonValue(taskData, "idCardNumber", "")
                if (idCardNumber.isNullOrEmpty()) {
                    continue
                } else {
                    val workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idCardNumber)).unique()
                    if (workerInfo == null) {
                        val newInfo = WorkerInfo()
                        newInfo.workerCode = JsonUtils.getJsonValue(taskData, "userId", "")
                        newInfo.idNumber = idCardNumber
                        newInfo.hasPush = false
                        newInfo.present = false
                        workerInfoDao.insert(newInfo)
                    } else {
                        workerInfo.workerCode = JsonUtils.getJsonValue(taskData, "userId", "")
                        workerInfo.idNumber = idCardNumber
                        workerInfo.hasPush = false
                        workerInfo.present = false
                        workerInfoDao.update(workerInfo)
                    }
                }
            }
        }
        completeTask(taskId, cmd)
    }

    private fun completeTask(taskId: String, cmd: String) {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["applyId"] = applyId
        map["supplierCode"] = supplierCode
        map["deviceId"] = deviceId
        map["deviceToken"] = deviceToken
        val dataMap: MutableMap<String, Any> = mutableMapOf()
        dataMap["taskId"] = taskId
        dataMap["cmd"] = cmd
        val resultMap: MutableMap<String, String> = mutableMapOf()
        resultMap["result_code"] = "0"
        resultMap["result_desc"] = "下发人员信息成功"
        dataMap["result"] = resultMap
        map["data"] = dataMap
        val result = doPostJson(applyId, "${HuNanPublicInfo.SEVER_PORT}/${HuNanPublicInfo.UPDATE_STATE_BY_ID}", map)
        dealResult("完成考勤设备任务 ", result)
    }

    private fun dealResult(title: String, result: String): Boolean {
        if (result.isEmpty()) {
            "${title}：请求失败无返回".printAndLog()
            return false
        }
        val jsonObject = JSONObject(result)
        val code = JsonUtils.getJsonValue(jsonObject, "code", "1")
        return if (code.equals("0")) {
            true
        } else {
            val msg = JsonUtils.getJsonValue(jsonObject, "msg", "请求失败无信息")
            "$title：$msg".printAndLog()
            false
        }
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