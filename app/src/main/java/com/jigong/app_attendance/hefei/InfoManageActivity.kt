package com.jigong.app_attendance.hefei

import android.content.Intent
import android.os.*
import com.jigong.app_attendance.BaseActivity
import com.jigong.app_attendance.MainActivity
import com.jigong.app_attendance.MyApplication
import com.jigong.app_attendance.databinding.ActivityInfoManageBinding
import com.jigong.app_attendance.info.PublicTopicAddress
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.utils.doPostJson
import kotlinx.coroutines.*
import java.util.Timer
import java.util.TimerTask

class InfoManageActivity : BaseActivity() {

    private lateinit var binding: ActivityInfoManageBinding
    private val thread = HeFeiMqttThread()

    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoManageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
        runBlocking {
            launch(Dispatchers.IO) {
                getOnline(User.getInstance().inDeviceNo)
                getOnline(User.getInstance().outDeviceNo)
            }.join()
            launch(Dispatchers.IO) {
                while (true) {
                    if (User.getInstance().inOnline && User.getInstance().outOnline) {
                        println("平台上线请求成功")
                        println("平台上线结束，开始对济工网平台进行数据处理")
                        binding.connectStatus.text = "连接成功"
                        break
                    }
                }
                /*
                * 向济工网平台上传工人信息（工人信息表不为空时调用），三分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        launch(Dispatchers.IO) {
                            if (workerInfoDao.queryBuilder().count() > 0) {
                                val getInfo = async {
                                    pushWorkerInfo()
                                }
                                dealWorkerInfo(getInfo.await())
                            }
                        }
                    }
                }, 0, 30 * 1000)
                /*
                * 向济工网平台获取工人考勤信息，三分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        getWorkerAttendance()
                    }
                }, 0, 30 * 1000)
            }
        }
    }

    private fun pushWorkerInfo(): String {
        val dataMap = HashMap<String, Any>()
        dataMap["2"] = "2"
        return doPostJson(PublicTopicAddress.GET_WORKER, dataMap)
    }

    private fun dealWorkerInfo(infoString: String) {

    }

    private fun getWorkerAttendance() {

    }

    /*
    * 请求设备上线，设备开始心跳
    * */
    private fun getOnline(deviceNo: String) {
        mqttStart(deviceNo)
        getBasic(deviceNo)
        pushBasicOnline(deviceNo)
        /*
        * 设备心跳定时器，30秒左右请求一次
        * */
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (isFinishing) {
                    cancel()
                }
                pushHeartbeat(deviceNo)
            }
        }, 30 * 1000, 30 * 1000)
    }

    /*
    * 设备下线请求
    * */
    private fun getOffline(deviceNo: String) = pushBasicOffline(deviceNo)

    private fun initView() {
        binding.projectName.text = User.getInstance().projectName
        binding.signOut.setOnClickListener {
            thread.stop = true
            signOut()
        }
        binding.inDeviceNo.text = User.getInstance().inDeviceNo
        binding.outDeviceNo.text = User.getInstance().outDeviceNo
        binding.connectStatus.text =
            if (User.getInstance().inOnline && User.getInstance().outOnline) "连接成功" else "连接中"
        binding.version.text = versionCode
    }

    /*
    * 退出登录的相应处理，应有下线请求、数据清除、跳转至登录
    * */
    private fun signOut() = runBlocking {
        launch(Dispatchers.IO) {
            getOffline(User.getInstance().inDeviceNo)
            getOffline(User.getInstance().outDeviceNo)
        }.join()
        clearData()
        startActivity(Intent(baseContext, MainActivity::class.java))
        finish()
    }

    private fun clearData() {
        User.getInstance().clearAll()
    }

    override fun onDestroy() {
        thread.stop = true
        super.onDestroy()
    }

}