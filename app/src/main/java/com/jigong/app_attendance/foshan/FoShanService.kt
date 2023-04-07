package com.jigong.app_attendance.foshan

import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import cn.hutool.socket.nio.NioClient
import com.jigong.app_attendance.mainpublic.BaseService
import com.jigong.app_attendance.mainpublic.MyApplication
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.info.printAndLog
import com.jigong.app_attendance.utils.JsonUtils
import com.jigong.app_attendance.utils.checkResult
import com.jigong.app_attendance.utils.doPostJson
import com.nanchen.compresshelper.CompressHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class FoShanService : BaseService() {

    private val mainScope = MainScope()

    private val limitCount = 50
    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
    private val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao

    private var nioClientIn: NioClient? = null
    private var nioClientOut: NioClient? = null

    private val time3: Long = 1000 * 60 * 3
    private val time5: Long = 1000 * 60 * 5
    private val time30: Long = 1000 * 60 * 60
    private val timeLogin: Long = 1000 * 60 * 60 * 2

    @DelicateCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        "服务已开始".printAndLog()
        mainScope.launch(Dispatchers.Main) {
            run()
        }
    }

    private suspend fun run() = withContext(Dispatchers.IO) {
        try {
            while (true) {
                try {
                    val client = async(Dispatchers.IO) {
                        login(User.getInstance().inDeviceNo)
                    }
                    nioClientIn = client.await()
                    val clientOut = async(Dispatchers.IO) {
                        login(User.getInstance().outDeviceNo)
                    }
                    nioClientOut = clientOut.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (nioClientIn != null) User.getInstance().inOnline = true
                if (nioClientOut != null) User.getInstance().outOnline = true
                if (nioClientIn != null && nioClientOut != null) {
                    break
                }
            }
            timer.schedule(object : TimerTask() {
                override fun run() {
                    try {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, timeLogin, timeLogin)
            /*
            * 向平台发送心跳，5分钟左右一次
            * */
            timer.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        if (nioClientIn != null) {
                            BaseSocket.sendHeartInfo(nioClientIn)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        if (nioClientOut != null) {
                            BaseSocket.sendHeartInfo(nioClientOut)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, 0, time5)
            /*
            * 获取工人信息，60分钟左右一次
            * */
            timer.schedule(object : TimerTask() {
                override fun run() { //获取人员特征信息
                    if (nioClientIn != null) {
                        BaseSocket.getWorkerCode2(nioClientIn).printAndLog()
                    }
                }
            }, 0, time30)
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
            /*
            * 获取工人信息，3分钟左右一次
            * */
            timer.schedule(object : TimerTask() {
                override fun run() { //考勤上传
                    if (attendanceInfoDao.queryBuilder().count() > 0) {
                        if (nioClientIn != null && nioClientOut != null) {
                            uploadAttendanceList(nioClientIn, nioClientOut)
                        }
                    }
                }
            }, 0, time3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun login(deviceNo: String): NioClient {
        val stringObjectMap = BaseSocket.doLoginandHeartbeat2(User.getInstance().joinCode, deviceNo)
        val login = stringObjectMap["flag"] as Boolean?
        val client = stringObjectMap["client"]
        if (!login!!) {
            ("设备登录失败! projectId=" + User.getInstance().projectId + ", projectName=" + User.getInstance().projectName).printAndLog()
        }
        return client as NioClient
    }

    fun uploadAttendanceList(clientIn: NioClient?, clientOut: NioClient?) {
        val attendanceList = if (attendanceInfoDao.count() > limitCount) {
            attendanceInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            attendanceInfoDao.loadAll()
        }
        attendanceList.forEach {
            val workerCode: String = it.workerCode
            val date: String = com.jigong.app_attendance.utils.DateUtils.date2Str(com.jigong.app_attendance.utils.DateUtils.str2Date(it.checkinTime, "yyyy-MM-dd HH:mm:ss"), "yyyyMMddHHmmss")
            val imageToByte = getImageBytes(it.normalSignImage)
            if (TextUtils.isEmpty(workerCode)) {
                "${it.workerName}身份证号：${it.idNumber}未上传考勤(工人编号未下拉)".printAndLog()
                if (!TextUtils.isEmpty(it.normalSignImage)) {
                    File(GlobalCode.FILE_PATH, it.normalSignImage).delete()
                }
                attendanceInfoDao.delete(it)
                return@forEach
            }
            if (TextUtils.isEmpty(it.normalSignImage) || imageToByte == null) {
                "${it.workerName}身份证号：${it.idNumber}未上传考勤(考勤图片未完善)".printAndLog()
                attendanceInfoDao.delete(it)
                return@forEach
            }
            try {
                val booleanStringMap = BaseSocket.sendAttendance(workerCode, date, imageToByte, if (it.machineType.equals("02")) clientIn else clientOut)
                if (booleanStringMap.isEmpty() || booleanStringMap.containsKey(false)) {
                    ("${it.workerName}身份证号：${it.idNumber}，人员考勤上传失败, 平台返回:" + booleanStringMap[false]).printAndLog()
                } else {
                    File(GlobalCode.FILE_PATH, it.normalSignImage).delete()
                    attendanceInfoDao.delete(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getImageBytes(fileName: String?): ByteArray? {
        if (fileName.isNullOrEmpty()) {
            return null
        }
        try {
            val oldFile = File(GlobalCode.FILE_PATH, fileName)
            if (!oldFile.exists()) {
                return null
            }
            val file = CompressHelper.getDefault(this).compressToFile(oldFile);
            var inputStream: FileInputStream? = null
            try {
                inputStream = FileInputStream(file)
                val byteData = ByteArray(inputStream.available())
                inputStream.read(byteData, 0, byteData.size)
                inputStream.close()
                return byteData
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun pushWorkerInfo() {
        val workerList = if (workerInfoDao.count() > limitCount) {
            workerInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            workerInfoDao.loadAll()
        }
        workerList.forEach {
            if (!it.getInfo || it.hasPush) {
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
                            val imageUrl = JsonUtils.getJsonValue(dataObject, "normalSignImage", "")
                            if (!imageUrl.isNullOrEmpty()) {
                                attendanceInfo.normalSignImage = ConverUtils.netSourceToFile(imageUrl, "GET")
                            } else {
                                attendanceInfo.normalSignImage = ""
                            }
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

    private fun getWorkerAttendance() {
        val map = HashMap<String, Any>()
        map["projectId"] = User.getInstance().projectId
        map["queryRowId"] = User.getInstance().rowId
        map["signDate"] = User.getInstance().signDate
        map["joinCity"] = User.getInstance().joinCity
        dealAttendanceInfo(doPostJson(GlobalCode.QUERY_PROJECT_SIGN_LIST_FOSHAN, map))
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