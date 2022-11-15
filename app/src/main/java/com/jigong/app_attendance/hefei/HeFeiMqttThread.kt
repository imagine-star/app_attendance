package com.jigong.app_attendance.hefei

import android.os.SystemClock
import com.jigong.app_attendance.MyApplication
import com.jigong.app_attendance.info.PublicTopicAddress
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.utils.doPostJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong

/**
 * @Author LiuHaoQi
 * @Description
 * @Date 2022/11/10 16:19
 */
class HeFeiMqttThread : Thread() {

    /*
    * 控制线程变量，置true关闭线程
    * */
    var stop = false;
    /*
    * 工人信息上传至济工网平台的定时器与间隔时间管理
    * */
    private val uploadWorkerTimeSpace = 1000 * 60 * 3;
    private val uploadWorkerTime = AtomicLong(0)
    /*
    * 向济工网平台获取工人考勤的定时器与间隔时间管理
    * */
    private val getAttendanceTimeSpace = 1000 * 60 * 3;
    private val getAttendanceTime = AtomicLong(0)

    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao

    override fun run() {
        super.run()
        while (!stop) {
            /*
            * 向济工网平台上传工人信息（工人信息表不为空时调用），三分钟左右一次
            * */
            if (SystemClock.uptimeMillis() - uploadWorkerTime.get() > uploadWorkerTimeSpace) {
                if (workerInfoDao.queryBuilder().count() > 0) {
                    pushWorkerInfo()
                }
                uploadWorkerTime.set(SystemClock.uptimeMillis())
            }
            /*
            * 向济工网平台获取工人考勤信息，三分钟左右一次
            * */
            if (SystemClock.uptimeMillis() - getAttendanceTime.get() > getAttendanceTimeSpace) {
                getWorkerAttendance()
                getAttendanceTime.set(SystemClock.uptimeMillis())
            }
        }
    }

    fun pushWorkerInfo() {
        val dataMap = HashMap<String, Any>()
        dataMap["2"] = "2"
        dealWorkerInfo(doPostJson(PublicTopicAddress.GET_WORKER, dataMap))
    }

    fun dealWorkerInfo(infoString: String) {

    }

    fun getWorkerAttendance() {

    }

}