package com.jigong.app_attendance.hefei

import android.content.Intent
import android.os.*
import com.jigong.app_attendance.BaseActivity
import com.jigong.app_attendance.MainActivity
import com.jigong.app_attendance.MyApplication
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.databinding.ActivityInfoManageBinding
import com.jigong.app_attendance.info.PublicTopicAddress
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.utils.JsonUtils
import com.jigong.app_attendance.utils.checkResult
import com.jigong.app_attendance.utils.doPostJson
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask

class InfoManageActivity : BaseActivity() {

    private lateinit var binding: ActivityInfoManageBinding

    private val limitCount = 50;
    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
    private val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao

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
                        if (isFinishing) {
                            cancel()
                        }
                        if (workerInfoDao.queryBuilder().count() > 0) {
                            pushWorkerInfo()
                        }
                    }
                }, 0, 30 * 1000)
                /*
                * 向济工网平台获取工人考勤信息，三分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                        }
                        getWorkerAttendance()
                    }
                }, 0, 30 * 1000)
                /*
                * 向合肥平台推送工人考勤信息（考勤信息表不为空时调用），三分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                        }
                        if (attendanceInfoDao.queryBuilder().count() > 0) {
                            pushWorkerAttendance()
                        }
                    }
                }, 0, 30 * 1000)
            }
        }
    }

    private fun pushWorkerAttendance() = runBlocking {
        launch(Dispatchers.IO) {
            val attendanceList = if (attendanceInfoDao.count() > limitCount) {
                attendanceInfoDao.queryBuilder().limit(limitCount).list()
            } else {
                attendanceInfoDao.loadAll()
            }
            attendanceList.forEach {
                pushAttendance(
                    it,
                    if (it.machineType == "02") User.getInstance().inDeviceNo else User.getInstance().outDeviceNo
                )
            }
        }
    }

    private fun pushWorkerInfo() = runBlocking {
        launch(Dispatchers.IO) {
            val workerList = if (workerInfoDao.count() > limitCount) {
                workerInfoDao.queryBuilder().limit(limitCount).list()
            } else {
                workerInfoDao.loadAll()
            }
            workerList.forEach {
                val map = HashMap<String, Any>()
                map["joinCity"] = User.getInstance().account
                map["projectId"] = User.getInstance().projectId
                val listMap = ArrayList<Map<String, String>>()
                val dataMap = HashMap<String, String>()
                dataMap["idNumber"] = it.idCard
                dataMap["name"] = it.name
                dataMap["photo"] = it.picURI
                listMap.add(dataMap)
                map["workerList"] = listMap
                val pushInfo = async {
                    doPostJson(PublicTopicAddress.UPLOAD_WORKER, map)
                }
                if (checkResult(pushInfo.await())) {
                    workerInfoDao.delete(it)
                }
            }
        }
    }

    private fun dealAttendanceInfo(infoString: String) {
        if (checkResult(infoString)) {
            val jsonObject = JSONObject(infoString)
            val entry = JsonUtils.getJSONObject(jsonObject, "entry")
            if (entry != null) {
                val rowId = JsonUtils.getJsonValue(entry, "queryRowId", "0")
                User.getInstance().rowId = rowId
                val jsonArray = JsonUtils.getJSONArray(entry, "result")
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {
                        val dataObject = jsonArray.getJSONObject(i)
                        if (dataObject != null) {
                            val attendanceInfo = AttendanceInfo()
                            attendanceInfo.attendanceId = JsonUtils.getJsonValue(dataObject, "attendanceId", "")
                            attendanceInfo.checkinTime = JsonUtils.getJsonValue(dataObject, "checkinTime", "")
                            attendanceInfo.deviceSerialNo = JsonUtils.getJsonValue(dataObject, "deviceSerialNo", "")
                            attendanceInfo.machineType = JsonUtils.getJsonValue(dataObject, "machineType", "")
                            attendanceInfo.normalSignImage = JsonUtils.getJsonValue(dataObject, "normalSignImage", "")
                            attendanceInfo.projectId = JsonUtils.getJsonValue(dataObject, "projectId", "")
                            attendanceInfo.redSignImage = JsonUtils.getJsonValue(dataObject, "redSignImage", "")
                            attendanceInfo.subcontractorId = JsonUtils.getJsonValue(dataObject, "subcontractorId", "")
                            attendanceInfo.temperature = JsonUtils.getJsonValue(dataObject, "temperature", "")
                            attendanceInfo.workerId = JsonUtils.getJsonValue(dataObject, "workerId", "")
                            attendanceInfo.workerName = JsonUtils.getJsonValue(dataObject, "workerName", "")
                            attendanceInfo.idNumber = JsonUtils.getJsonValue(dataObject, "idNumber", "")
                            attendanceInfoDao.insert(attendanceInfo)
                        }
                    }
                }
            }
        }
    }

    private fun getWorkerAttendance() = runBlocking {
        launch(Dispatchers.IO) {
            val map = HashMap<String, Any>()
            map["projectId"] = User.getInstance().projectId
            map["queryRowId"] = User.getInstance().rowId
            map["signDate"] = ""
            val getAttendance = async {
                doPostJson(PublicTopicAddress.QUERY_PROJECT_SIGN_LIST, map)
            }
            dealAttendanceInfo(getAttendance.await())
        }
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
        runBlocking {
            launch(Dispatchers.IO) {
                getOffline(User.getInstance().inDeviceNo)
                getOffline(User.getInstance().outDeviceNo)
            }.join()
            super.onDestroy()
        }
    }

}