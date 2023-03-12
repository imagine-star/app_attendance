package com.jigong.app_attendance.hefei

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import cn.hutool.socket.nio.NioClient
import com.jigong.app_attendance.BaseActivity
import com.jigong.app_attendance.MainActivity
import com.jigong.app_attendance.MyApplication
import com.jigong.app_attendance.bean.AttendanceInfo
import com.jigong.app_attendance.databinding.ActivityInfoManageBinding
import com.jigong.app_attendance.foshan.BaseSocket
import com.jigong.app_attendance.foshan.ShowNoInfoWorkerActivity
import com.jigong.app_attendance.foshan.WorkerInfoSocket
import com.jigong.app_attendance.info.PublicTopicAddress
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.utils.JsonUtils
import com.jigong.app_attendance.utils.checkLogin
import com.jigong.app_attendance.utils.checkResult
import com.jigong.app_attendance.utils.doPostJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.SocketException
import java.util.*


class InfoManageActivity : BaseActivity() {

    private lateinit var binding: ActivityInfoManageBinding

    private val limitCount = 50
    private val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
    private val attendanceInfoDao = MyApplication.getApplication().daoSession.attendanceInfoDao

    private var nioClientIn: NioClient? = null
    private var nioClientOut: NioClient? = null

    private val time3: Long = 1000 * 60 * 3
    private val time5: Long = 1000 * 60 * 5
    private val time30: Long = 1000 * 60 * 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoManageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
        if (TextUtils.isEmpty(User.getInstance().token)) {
            showToastMsgShort("该项目密钥未完善! projectId=" + User.getInstance().projectId + ", projectName=" + User.getInstance().projectName)
            return
        }
        binding.managerTitle.setOnClickListener {
            val intent = Intent(this, ShowNoInfoWorkerActivity::class.java)
            startActivity(intent)
        }
        lifecycleScope.launch {
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
                    if (nioClientIn != null && nioClientOut != null) {
                        println("平台上线请求成功")
                        println("平台上线结束，开始对济工网平台进行数据处理")
                        launch(Dispatchers.Main) {
                            binding.connectStatus.text = "连接成功"
                        }
                        break
                    }
                }
                /*
                * 像平台发送心跳，5分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                            return
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
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
                    }
                }, 0, time5)
                /*
                * 获取工人信息，3分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                            return
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                //获取人员特征信息
                                if (nioClientIn != null) {
                                    BaseSocket.getWorkerCode2(nioClientIn)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }, 0, time30)
                /*
                * 向济工网平台上传工人信息（工人信息表不为空时调用），三分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                            return
                        }
                        if (workerInfoDao.queryBuilder().count() > 0) {
                            val workerList = workerInfoDao.queryBuilder().list();
                            lifecycleScope.launch {
//                                pushWorkerInfo()
                            }
                        }
                    }
                }, 0, time3)
                /*
                * 向济工网平台获取工人考勤信息，三分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                            return
                        }
                        lifecycleScope.launch {
//                            getWorkerAttendance()
                        }
                    }
                }, 0, time3)
                /*
                * 获取工人信息，3分钟左右一次
                * */
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isFinishing) {
                            cancel()
                            return
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                //考勤上传
                                if (attendanceInfoDao.queryBuilder().count() > 0) {
                                    if (nioClientIn != null && nioClientOut != null) {
                                        uploadAttendanceList(nioClientIn, nioClientOut)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }, 0, time3)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun login(deviceNo: String): NioClient {
        val stringObjectMap = BaseSocket.doLoginandHeartbeat2(
            User.getInstance().token,
            deviceNo
        )
        val login = stringObjectMap["flag"] as Boolean?
        val client = stringObjectMap["client"]
        if (!login!!) {
            showToastMsgShort("设备登录失败! projectId=" + User.getInstance().projectId + ", projectName=" + User.getInstance().projectName)
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
            val date: String = com.jigong.app_attendance.utils.DateUtils.date2Str(
                com.jigong.app_attendance.utils.DateUtils.str2Date(
                    it.checkinTime,
                    "yyyy-MM-dd HH:mm:ss"
                ), "yyyyMMddHHmmss"
            )
            val imageToByte =
                getImageBytes(it.normalSignImage)
            if (TextUtils.isEmpty(workerCode)) {
                println("工人编号未下拉")
            }
            if (TextUtils.isEmpty(it.normalSignImage)) {
                println("考勤图片未完善")
            }
            try {
                val booleanStringMap =
                    BaseSocket.sendAttendance(
                        workerCode,
                        date,
                        imageToByte,
                        if (it.machineType.equals("02")) clientIn else clientOut
                    )
                if (booleanStringMap.isEmpty() || booleanStringMap.containsKey(false)) {
                    println("人员考勤上传失败, 平台返回:" + booleanStringMap[false])
                } else {
                    attendanceInfoDao.delete(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getImageBytes(filePath: String?): ByteArray? {
        try {
            val file = File(filePath)
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

    private suspend fun pushWorkerAttendance() = withContext(Dispatchers.IO) {
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

    private suspend fun pushWorkerInfo() = withContext(Dispatchers.IO) {
        val workerList = if (workerInfoDao.count() > limitCount) {
            workerInfoDao.queryBuilder().limit(limitCount).list()
        } else {
            workerInfoDao.loadAll()
        }
        workerList.forEach {
            if (!it.getInfo) {
                return@forEach
            }
            val map = HashMap<String, Any>()
            map["joinCity"] = User.getInstance().account
            map["projectId"] = User.getInstance().projectId
            val listMap = ArrayList<Map<String, Any>>()
            val dataMap = HashMap<String, Any>()
            dataMap["idNumber"] = it.idCard
            dataMap["thirdNo"] = it.workerCode
            dataMap["name"] = it.name
            dataMap["photo"] = it.picURI
            listMap.add(dataMap)
            map["workerList"] = listMap
            val pushInfo = async {
                doPostJson(PublicTopicAddress.UPLOAD_WORKER_FOSHAN, map)
            }
            if (checkResult(pushInfo.await())) {
                workerInfoDao.delete(it)
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
                val signDate = JsonUtils.getJsonValue(entry, "signDate", "0")
                User.getInstance().signDate = signDate
                val jsonArray = JsonUtils.getJSONArray(entry, "result")
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {
                        val dataObject = jsonArray.getJSONObject(i)
                        if (dataObject != null) {
                            val attendanceInfo = AttendanceInfo()
                            attendanceInfo.attendanceId =
                                JsonUtils.getJsonValue(dataObject, "attendanceId", "")
                            attendanceInfo.checkinTime =
                                JsonUtils.getJsonValue(dataObject, "checkinTime", "")
                            attendanceInfo.deviceSerialNo =
                                JsonUtils.getJsonValue(dataObject, "deviceSerialNo", "")
                            attendanceInfo.idNumber =
                                JsonUtils.getJsonValue(dataObject, "idNumber", "")
                            attendanceInfo.machineType =
                                JsonUtils.getJsonValue(dataObject, "machineType", "")
                            attendanceInfo.normalSignImage =
                                JsonUtils.getJsonValue(dataObject, "normalSignImage", "")
                            attendanceInfo.projectId =
                                JsonUtils.getJsonValue(dataObject, "projectId", "")
                            attendanceInfo.subcontractorId =
                                JsonUtils.getJsonValue(dataObject, "subcontractorId", "")
                            attendanceInfo.temperature =
                                JsonUtils.getJsonValue(dataObject, "temperature", "")
                            attendanceInfo.workerCode =
                                JsonUtils.getJsonValue(dataObject, "thirdNo", "")
                            attendanceInfo.workerId =
                                JsonUtils.getJsonValue(dataObject, "workerId", "")
                            attendanceInfo.workerName =
                                JsonUtils.getJsonValue(dataObject, "workerName", "")
                            attendanceInfoDao.insert(attendanceInfo)
                        }
                    }
                }
            }
        } else if (!checkLogin(infoString)) {
            lifecycleScope.launch {
                signOut()
            }
        }
    }

    private suspend fun getWorkerAttendance() = withContext(Dispatchers.IO) {
        val map = HashMap<String, Any>()
        map["projectId"] = User.getInstance().projectId
        map["queryRowId"] = User.getInstance().rowId
        map["signDate"] = User.getInstance().signDate
        val getAttendance = async {
            doPostJson(PublicTopicAddress.QUERY_PROJECT_SIGN_LIST_FOSHAN, map)
        }
        dealAttendanceInfo(getAttendance.await())
    }

    /*
    * 请求设备上线，设备开始心跳
    * */
    private suspend fun getOnline(deviceNo: String) = withContext(Dispatchers.IO) {
        try {
            mqttStart(deviceNo)
            getBasic(deviceNo)
            pushBasicOnline(deviceNo)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: EOFException) {
            e.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        /*
        * 设备心跳定时器，30秒左右请求一次
        * */
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (isFinishing) {
                    cancel()
                }
                lifecycleScope.launch {
                    try {
                        pushHeartbeat(deviceNo)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: EOFException) {
                        e.printStackTrace()
                    } catch (e: SocketException) {
                        e.printStackTrace()
                    }
                }
            }
        }, 30 * 1000, 30 * 1000)
    }

    /*
    * 设备下线请求
    * */
    private suspend fun getOffline(deviceNo: String) = withContext(Dispatchers.IO) {
        pushBasicOffline(deviceNo)
    }

    private fun initView() {
        binding.projectName.text = User.getInstance().projectName
        binding.signOut.setOnClickListener {
            lifecycleScope.launch {
                signOut()
            }
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
    private suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            getOffline(User.getInstance().inDeviceNo)
            getOffline(User.getInstance().outDeviceNo)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        clearData()
        startActivity(Intent(baseContext, MainActivity::class.java))
        finish()
    }

    private fun clearData() {
        User.getInstance().clearAll()
    }

}