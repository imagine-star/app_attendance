package com.jigong.app_attendance.hunan

import android.content.Intent
import android.os.IBinder
import com.jigong.app_attendance.info.easyPrint
import com.jigong.app_attendance.info.printAndLog
import com.jigong.app_attendance.mainpublic.BaseService
import com.jigong.app_attendance.utils.doPostJson
import kotlinx.coroutines.*
import java.util.TimerTask

class HuNanService : BaseService() {

    private val mainScope = MainScope()

    private val time3: Long = 1000 * 60 * 3

    @DelicateCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        "服务已开始，协程空间已注册".printAndLog()
        mainScope.launch(Dispatchers.Main) {
            run()
        }
    }

    private suspend fun run() = withContext(Dispatchers.IO) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                val map: MutableMap<String, String> = mutableMapOf()
                val applyId = "ac798e8381e642289526cac9df8ceb2d"
                val applyIdEn = AESUtil.encrypt("ac798e8381e642289526cac9df8ceb2d", "12ff140bd41a4dd7b696f1b880512e67", "12ff140bd41a4dd7b696f1b880512e67".substring(0, 16))
                map.put("applyId", applyId)
                map.put("supplierCode", "486611842647461888")
                map.put("deviceId", "84E0F42EB34F1608")
                map.put("deviceToken", "b8769cd488054367b33cff35e816d89c")
                val result = doPostJson(applyId, "${HuNanPublicInfo.SEVER_PORT}/${HuNanPublicInfo.GET_STAFF_INFO_BY_SN}", map)
                result.easyPrint()
            }
        }, 0, time3)
    }

    override fun onDestroy() {
        mainScope.cancel()
        "服务已销毁，协程空间已注销".printAndLog()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}