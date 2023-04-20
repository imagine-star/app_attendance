package com.jigong.app_attendance.utils

import android.text.TextUtils
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.hefei.HeFeiServer
import com.jigong.app_attendance.info.User
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author LiuHaoQi
 * @Description 处理上传时的数据
 * @Date 2022/11/10 14:39
 */

/*
* 向合肥平台发送上线通知时传送的数据
* */
fun getOnlineDataMap(deviceNo: String): Map<String, Any> {
    val infoMap = mapOf(
        Pair("facesluiceId", deviceNo),
        Pair("username", User.getInstance().userName),
        Pair("ip", getNetIp()),
        Pair("time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
    )
    val operator = "Online"
    val sign = SignTest.getSign(0, HeFeiServer.BASIC_PUSH + operator + deviceNo)
    return mapOf(
        Pair("operator", operator),
        Pair("clientSign", sign),
        Pair("info", infoMap)
    )
}

/*
* 向合肥平台发送心跳通知时传送的数据
* */
fun getHeartbeatDataMap(deviceNo: String): Map<String, Any> {
    val infoMap = mapOf(
        Pair("facesluiceId", deviceNo),
        Pair("time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
    )
    val operator = "HeartBeat"
    val sign = SignTest.getSign(0, HeFeiServer.BASIC_PUSH + operator + deviceNo)
    return mapOf(
        Pair("operator", operator),
        Pair("clientSign", sign),
        Pair("info", infoMap)
    )
}

/*
* 向合肥平台发送下线通知时传送的数据
* */
fun getOfflineDataMap(deviceNo: String): Map<String, Any> {
    val infoMap = mapOf(
        Pair("facesluiceId", deviceNo)
    )
    val operator = "Offline"
    val sign = SignTest.getSign(0, HeFeiServer.BASIC_PUSH + operator + deviceNo)
    return mapOf(
        Pair("operator", operator),
        Pair("clientSign", sign),
        Pair("info", infoMap)
    )
}

/*
* 调用下发工人信息指令回复时上传的数据
* */
fun getReplyGetWorkerInfoDataMap(
    deviceNo: String,
    messageId: String,
    sucNum: Int,
    errNum: Int
): Map<String, Any> {
    val infoMap = mapOf(
        Pair("facesluiceId", deviceNo),
        Pair("AddErrNum", errNum),
        Pair("AddSucNum", sucNum),
        Pair("result", "ok")
    )
    val operator = "EditPersonsNew-Ack"
    return mapOf(
        Pair("operator", operator),
        Pair("messageId", messageId),
        Pair("info", infoMap)
    )
}

/*
* 向合肥平台上传考勤数据的数据
* */
fun getAttendanceDataMap(deviceNo: String, attendanceInfo: AttendanceInfo): Map<String, Any> {
    val infoMap = mapOf(
        Pair("facesluiceId", deviceNo),//考勤设备号 id
        Pair("customId", attendanceInfo.idNumber),//人员唯一 ID(最长 48 字符), 如果设备存在该 id,则为修改操作,否则为新增操作
        Pair("RecordID", attendanceInfo.attendanceId),//记录 ID
        Pair("personName", attendanceInfo.workerName),//人员姓名(最长 48 字符)
        Pair("idCard", attendanceInfo.idNumber),//证件号码
        Pair("Sendintime", "0"),//0: 非实时数据(不在 10 秒 内的考勤记录) 1: 实时数据(10 秒内的考勤记录)
        Pair("uniqueId", deviceNo),//设备的唯一 ID,每个设备要求不同(如固件 ID,cpuID,mac 地址等) (最长 128 字符)
        Pair("time", attendanceInfo.checkinTime),//考勤时间 如 2022-07-06 10:02:16
        Pair("liveDetect", "T"),//活体验证结果,T:通过 F:未通过 N:未开启
        Pair(
            "direction",
            if (attendanceInfo.machineType == "02") "enter" else "exit"
        ),//出入口方向进口:"enter" ，出口:"exit" ,无方向: "unknown"
        Pair(
            "pic",
            if (TextUtils.isEmpty(attendanceInfo.normalSignImage)) "" else ConverUtils.netSourceToBase64(
                attendanceInfo.normalSignImage,
                "GET"
            )
        )//考勤照片(base64 图片, jpg 格式,最大 64K,分辨率最大 320x320 )

    )
    val operator = "RecPush"
    val sign = SignTest.getSign(
            0,
            HeFeiServer.TOPIC_PREFIX + deviceNo + "/Rec" +
                operator + attendanceInfo.idNumber + attendanceInfo.attendanceId + deviceNo + attendanceInfo.checkinTime +
                if (TextUtils.isEmpty(attendanceInfo.normalSignImage)) "0" else "1"
    )
    return mapOf(
        Pair("operator", operator),
        Pair("clientSign", sign),
        Pair("info", infoMap)
    )
}

fun getNetIp(): String? {
    var ip = ""
    var inputStream: InputStream? = null
    try {
        val infoUrl = URL("http://pv.sohu.com/cityjson?ie=utf-8")
        val connection = infoUrl.openConnection()
        val httpConnection = connection as HttpURLConnection
        val responseCode = httpConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = httpConnection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream, "gb2312"))
            val builder = StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            inputStream.close()
            val start1 = builder.indexOf(":")
            val str1 = builder.substring(start1 + 1)
            val start2 = str1.indexOf("\"")
            val str2 = str1.substring(start2 + 1)
            val start3 = str2.indexOf("\"")
            ip = str2.substring(0, start3)
            return ip
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}