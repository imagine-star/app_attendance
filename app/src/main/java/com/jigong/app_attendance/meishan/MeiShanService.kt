package com.jigong.app_attendance.meishan

import android.content.Intent
import android.os.IBinder
import cn.hutool.core.util.CharsetUtil
import cn.hutool.crypto.digest.HMac
import cn.hutool.crypto.digest.HmacAlgorithm
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.info.printAndLog
import com.jigong.app_attendance.mainpublic.BaseService
import com.jigong.app_attendance.utils.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap
import java.util.TimerTask

class MeiShanService : BaseService() {

    private val mainScope = MainScope()

    /**
     * 心跳时间，10分钟一次
     */
    private val time10: Long = 1000 * 60 * 10

    override fun onCreate() {
        super.onCreate()
        "服务已开始".printAndLog()
        mainScope.launch(Dispatchers.Main) {
            run()
        }
    }

    private suspend fun run() = withContext(Dispatchers.IO) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                deviceHeartbeat(User.getInstance().inDeviceNo, User.getInstance().joinPassword)
                deviceHeartbeat(User.getInstance().outDeviceNo, User.getInstance().joinDevice)
            }
        }, 0, time10)
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
                    "o" -> {
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
                                if (checkResult(GlobalCode.OUT_PROJECT_WORKER, pushInfo)) {
                                    //接口返回成功
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