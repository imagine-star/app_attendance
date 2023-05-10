package com.jigong.app_attendance.jiangmen

import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.bean.WorkerInfo
import com.jigong.app_attendance.greendao.WorkerInfoDao
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.info.easyPrint
import com.jigong.app_attendance.info.printAndLog
import com.jigong.app_attendance.mainpublic.BaseService
import com.jigong.app_attendance.mainpublic.MyApplication
import com.jigong.app_attendance.utils.JsonUtils
import com.jigong.app_attendance.utils.checkResult
import com.jigong.app_attendance.utils.doPostJson
import com.nanchen.compresshelper.CompressHelper
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable
import com.xuhao.didi.core.iocore.interfaces.ISendable
import com.xuhao.didi.core.pojo.OriginalData
import com.xuhao.didi.core.protocol.IReaderProtocol
import com.xuhao.didi.socket.client.sdk.OkSocket
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.TimerTask

class JiangMenService : BaseService() {

    private val mainScope = MainScope()

    private val limitCount = 50
    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
    private val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao

    private val time3: Long = 1000 * 60 * 3
    private val time5: Long = 1000 * 60 * 5
    private val time30: Long = 1000 * 60 * 60
    private val timeLogin: Long = 1000 * 60 * 60 * 2

    private val LOGIN_IN = "4B03"
    private val HEART_BEAT = "FFFF"
    private val WORKER_CODE = "5103"
    private val WORKER_INFO = "4D03"
    private val UPLOAD_ATTENDANCE = "5003"

    override fun onCreate() {
        super.onCreate()
        "服务已开始".printAndLog()
        mainScope.launch(Dispatchers.IO) {
            initSocket()
        }
    }

    private suspend fun run() = withContext(Dispatchers.IO) {
        try {
//            timer.schedule(object : TimerTask() {
//                override fun run() {
//                    login()
//                }
//            }, timeLogin, timeLogin)
            /*
            * 向平台发送心跳，5分钟左右一次
            * */
            timer.schedule(object : TimerTask() {
                override fun run() {
                    val info = "0100000000000000000000000001FFFF" + User.getInstance().projectId + "0001"
                    inManager.send { HexUtil.hexStringToBytes(info) }
                }
            }, 0, time5)
            /*
            * 获取工人信息，60分钟左右一次
            * */
            timer.schedule(object : TimerTask() {
                override fun run() { //获取人员特征信息
                    val content = "FFFF" + "FFFFFFFF"
                    val length = Integer.toHexString((content.toByteArray().size + 2) / 2)
                    sendData(length, content, WORKER_CODE)
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
            * 上传工人考勤，3分钟左右一次
            * */
            timer.schedule(object : TimerTask() {
                override fun run() { //考勤上传
                    if (attendanceInfoDao.queryBuilder().count() > 0) {
                        uploadAttendanceList()
                    }
                }
            }, 0, time3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun login() {
        inLogin = true
        val deviceIdToHex = HexUtil.strTo16FullLength(User.getInstance().inDeviceNo, 64)
        val companyCodeToHex = HexUtil.strTo16FullLength(User.getInstance().joinCode, 64)
        val content = companyCodeToHex + deviceIdToHex
        val length = Integer.toHexString((content.toByteArray().size + 2) / 2)
        sendData(length, content, LOGIN_IN)
    }

    private lateinit var inManager: IConnectionManager

    /**
     * 初始化socket连接
     */
    private fun initSocket() {

        val inConnect = ConnectionInfo(JiangMenServer.HOST, JiangMenServer.PORT)
        inManager = OkSocket.open(inConnect)
        val option = inManager.option
        val inBuilder = OkSocketOptions.Builder(option)
//        inBuilder.setMaxReadDataMB(10)
        inBuilder.setIOThreadMode(OkSocketOptions.IOThreadMode.SIMPLEX)
//        inBuilder.setWritePackageBytes(1000000)
        inBuilder.setConnectTimeoutSecond(20000)
        inBuilder.setReaderProtocol(object : IReaderProtocol {
            override fun getHeaderLength(): Int {
                return 32
            }

            override fun getBodyLength(header: ByteArray?, byteOrder: ByteOrder?): Int {
                if (header != null && header.isNotEmpty()) {
                    val body = HexUtil.BinaryToHexString(header)
                    if (!body.isNullOrEmpty()) {
                        val length = HexUtil.reverseString(body.substring(2, 10)).toInt(16)
                        return length + 2
                    }
                }
                return 0
            }

        })
        inManager.option(inBuilder.build())
        inManager.registerReceiver(object : SocketActionAdapter() {
            override fun onSocketConnectionSuccess(info: ConnectionInfo?, action: String?) {
                super.onSocketConnectionSuccess(info, action)
                login()
            }

            override fun onSocketWriteResponse(info: ConnectionInfo?, action: String?, data: ISendable?) {
                super.onSocketWriteResponse(info, action, data)
            }

            override fun onSocketReadResponse(info: ConnectionInfo?, action: String?, data: OriginalData?) {
                super.onSocketReadResponse(info, action, data)
                if (data != null) {
                    val headerByte = data.headBytes
                    val bodyByte = data.bodyBytes
                    if (headerByte != null && headerByte.isNotEmpty()) {
                        val header = HexUtil.BinaryToHexString(headerByte)
                        if (!header.isNullOrEmpty()) {
                            val cmd = header.substring(28, 32)
                            if (bodyByte != null && bodyByte.isNotEmpty()) {
                                val body = HexUtil.BinaryToHexString(bodyByte)
                                if (!body.isNullOrEmpty()) {
                                    when (cmd) {
                                        LOGIN_IN -> dealLogin(body)
                                        HEART_BEAT -> "接收到心跳返回数据$body".easyPrint()
                                        WORKER_CODE -> {
                                            if (body.isNotEmpty()) {
                                                val code = body.substring(body.length - 4, body.length - 2)
                                                if ("00" == code) {
                                                    "获取白名单成功, projectId=${User.getInstance().projectId}, 报文=$body".easyPrint()
                                                    val resultContent = body.substring(208)
                                                    dealWorkerCode(resultContent)
                                                } else {
                                                    val resultContent = body.substring(0, body.length - 4)
                                                    "获取白名单失败, projectId=${User.getInstance().projectId}, 原因是：${HexUtil.hexStringToString(resultContent)}, 返回报文: $body".printAndLog()
                                                }
                                            }
                                        }
                                        WORKER_INFO -> dealWorkerInfo(body)
                                        UPLOAD_ATTENDANCE -> dealAttendance(body)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onSocketIOThreadShutdown(action: String?, e: java.lang.Exception?) {
                super.onSocketIOThreadShutdown(action, e)
            }

            override fun onSocketConnectionFailed(info: ConnectionInfo?, action: String?, e: java.lang.Exception?) {
                super.onSocketConnectionFailed(info, action, e)
            }

            override fun onSocketDisconnection(info: ConnectionInfo?, action: String?, e: java.lang.Exception?) {
                super.onSocketDisconnection(info, action, e)
            }

            override fun onPulseSend(info: ConnectionInfo?, data: IPulseSendable?) {
                super.onPulseSend(info, data)
            }
        })
        inManager.connect()

    }

    private fun dealAttendance(body: String?) {
        if (!body.isNullOrEmpty()) {
            "接收到上传人员考勤信息返回数据$body".easyPrint()
            val length = HexUtil.reverseString(body.substring(2, 10)).toInt(16)
            val resultContent = body.substring(0, length - 4)
            val code = body.substring(body.length - 4, body.length - 2)
            if ("00" == code) {
                if (attendanceInfo != null) {
                    "姓名：${attendanceInfo!!.workerName}，身份证号：${attendanceInfo!!.idNumber}，上传人员考勤信息成功！".printAndLog()
                    attendanceInfoDao.delete(attendanceInfo)
                    attendanceInfo = null
                }
            } else {
                "上传人员考勤信息失败！原因是：${HexUtil.hexStringToString(resultContent)}".printAndLog()
            }
        }
        uploadAttendanceList()
    }

    private fun dealWorkerInfo(body: String?) {
        if (!body.isNullOrEmpty()) {
            var workerCode = ""
            var name: String? = ""
            var idNumber: String? = ""
            var gender: String? = ""
            val length = body.length - 4
            val code = body.substring(body.length - 4, body.length - 2)
            if ("00" == code || length > 100) {
                println("获取人员特征信息成功, projectId=" + User.getInstance().projectId)
                val resultContent = body.substring(64)
                workerCode = resultContent.substring(0, 8)
                name = cn.hutool.core.util.HexUtil.decodeHexStr(resultContent.substring(8, 68), StandardCharsets.UTF_8)
                idNumber = cn.hutool.core.util.HexUtil.decodeHexStr(resultContent.substring(68, 104), StandardCharsets.US_ASCII)
                gender = cn.hutool.core.util.HexUtil.decodeHexStr(resultContent.substring(106, 110), StandardCharsets.US_ASCII)
                val string = HexUtil.reverseString(resultContent.substring(670, 678))
                val glLong = string.toInt(16)
                val bytes = cn.hutool.core.util.HexUtil.decodeHex(resultContent.substring(678, 678 + glLong * 2))
                val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
                var workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idNumber)).unique()
                if (workerInfo == null) {
                    workerInfo = WorkerInfo()
                    workerInfo.workerCode = workerCode
                    workerInfo.name = name
                    workerInfo.idNumber = idNumber
                    workerInfo.gender = gender
                    workerInfo.picURI = bytes
                    workerInfo.getInfo = true
                    workerInfo.hasPush = false
                    workerInfoDao.insert(workerInfo)
                } else {
                    workerInfo.workerCode = workerCode
                    workerInfo.name = name
                    workerInfo.idNumber = idNumber
                    workerInfo.gender = gender
                    workerInfo.picURI = bytes
                    workerInfo.getInfo = true
                    workerInfo.hasPush = false
                    workerInfoDao.update(workerInfo)
                }
            } else {
                val resultContent = body.substring(0, length - 4)
                "获取人员信息失败, projectId=${User.getInstance().projectId}, 原因是：${HexUtil.hexStringToString(resultContent)}, 返回报文: $body"
            }
        }
        getWorkerInfo()
    }

    private fun dealWorkerCode(resultContent: String) {
        var workerCode = ""
        var name: String? = ""
        var idNumber: String? = ""
        var length = resultContent.length
        var index = 0
        while (true) {
            workerCode = resultContent.substring(index, index + 8)
            name = cn.hutool.core.util.HexUtil.decodeHexStr(resultContent.substring(index + 8, index + 28), StandardCharsets.UTF_8).trim()
            idNumber = cn.hutool.core.util.HexUtil.decodeHexStr(resultContent.substring(index + 28, index + 64), StandardCharsets.US_ASCII)
            try {
                var workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idNumber)).unique()
                if (workerInfo == null) {
                    workerInfo = WorkerInfo()
                    workerInfo.workerCode = workerCode
                    workerInfo.name = name
                    workerInfo.idNumber = idNumber
                    workerInfo.getInfo = false
                    workerInfo.hasPush = false
                    workerInfo.present = true
                    workerInfoDao.insert(workerInfo)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            index += 82
            length -= 82
            if (length < 20) {
                break
            }
        }
        getWorkerInfo()
    }

    private fun getWorkerInfo() {
        val workerInfoList = workerInfoDao.queryBuilder().list()
        workerInfoList.forEach {
            if (!it.getInfo) {
                val idNumberToHex = HexUtil.strTo16FullLength(it.idNumber, 36)
                val deviceIdToHex = HexUtil.strTo16FullLength(User.getInstance().inDeviceNo, 64)
                val content = deviceIdToHex + idNumberToHex
                var length = HexUtil.byte2Hex(HexUtil.unlong2H4bytes((content.length / 2 + 1).toLong()))
                length = HexUtil.fullLength(length, 8)
                sendData(length, content, WORKER_INFO)
                return
            }
        }
    }

    private var inLogin = true

    private fun dealLogin(body: String) {
        "接收到设备登录返回数据$body".printAndLog()
        val code = body.substring(body.length - 4, body.length - 2)
        if ("00" == code) {
            "设备登陆成功！".printAndLog()

            mainScope.launch(Dispatchers.Main) {
                run()
            }

            //            if (inLogin) {
            //                User.getInstance().inOnline = true
            //                inLogin = false
            //                val deviceIdToHex = HexUtil.strTo16FullLength(User.getInstance().outDeviceNo, 64)
            //                val companyCodeToHex = HexUtil.strTo16FullLength(User.getInstance().joinCode, 64)
            //                val content = companyCodeToHex + deviceIdToHex
            //                val dataLength = Integer.toHexString((content.toByteArray().size + 2) / 2)
            //                sendData(dataLength, content, LOGIN_IN)
            //            } else {
            //                User.getInstance().outOnline = true
            //                if (User.getInstance().inOnline && User.getInstance().outOnline) {
            //                    mainScope.launch(Dispatchers.Main) {
            //                        run()
            //                    }
            //                }
            //            }
        } else {
            val resultContent = body.substring(0, body.length - 4)
            "设备登陆失败！原因是：${HexUtil.hexStringToString(resultContent)}".printAndLog()
        }

    }

    private fun sendData(length: String, content: String, cmd: String) { //设备登陆
        val info = "01" +  //开始标记
                HexUtil.full8(length) +  //长度 LEN
                "00000000" +  //分包顺序索引
                "00000000" +  //分包总数
                "01" +  //版本
                cmd +  //命令
                User.getInstance().projectId.uppercase(Locale.ROOT) +
                content +
                HexUtil.getBCC(content.toByteArray()) +  //xor运算
                "01" +  //状态
                "01" //结束标记
        inManager.send(SendData(info))
        Thread.sleep(1000)
    }

    inner class SendData(val info: String) : ISendable {

        override fun parse(): ByteArray {
            return HexUtil.hexStringToBytes(info)
        }
    }

    private var attendanceInfo: AttendanceInfo? = null

    fun uploadAttendanceList() {
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
                "${it.workerName}，身份证号：${it.idNumber}未上传考勤(工人编号未下拉)".printAndLog()
                if (!TextUtils.isEmpty(it.normalSignImage)) {
                    File(GlobalCode.FILE_PATH, it.normalSignImage).delete()
                }
                attendanceInfoDao.delete(it)
                return@forEach
            }
            if (TextUtils.isEmpty(it.normalSignImage) || imageToByte == null) {
                "${it.workerName}，身份证号：${it.idNumber}未上传考勤(考勤图片未完善)".printAndLog()
                attendanceInfoDao.delete(it)
                return@forEach
            }
            attendanceInfo = it
            val imageToHex = HexUtil.BinaryToHexString(imageToByte)
            var imageLength = HexUtil.byte2Hex(HexUtil.unlong2H4bytes((imageToHex.length / 2).toLong()))
            imageLength = HexUtil.fullLength(imageLength, 8)
            val content = workerCode + date + "06" + imageLength + imageToHex
            var lenth = HexUtil.byte2Hex(HexUtil.unlong2H4bytes((content.length / 2 + 1).toLong()))
            lenth = HexUtil.fullLength(lenth, 8)
            sendData(lenth, content, UPLOAD_ATTENDANCE)
            return
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
            val file = CompressHelper.Builder(this)
                    .setQuality(10)
                    .build()
                    .compressToFile(oldFile)
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