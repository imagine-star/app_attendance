package com.jigong.app_attendance.mainpublic

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.jigong.app_attendance.databinding.ActivityInfoManageBinding
import com.jigong.app_attendance.foshan.*
import com.jigong.app_attendance.greendao.DaoMaster
import com.jigong.app_attendance.hunan.HuNanService
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.info.easyPrint
import com.jigong.app_attendance.info.printAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class InfoManageActivity : BaseActivity() {

    private lateinit var binding: ActivityInfoManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoManageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView() //        if (TextUtils.isEmpty(User.getInstance().joinCode)) {
        //            showToastMsgShort("该项目密钥未完善! projectId=" + User.getInstance().projectId + ", projectName=" + User.getInstance().projectName)
        //            return
        //        }
        binding.managerTitle.setOnClickListener {
            val intent = Intent(this, ShowNoInfoWorkerActivity::class.java)
            startActivity(intent)
        }
        binding.managerTitle.setOnLongClickListener {
            val intent = Intent(this, ShowLogActivity::class.java)
            startActivity(intent)
            true
        }

        val intent = getServiceIntent()
        if (intent != null) {
            startService(intent)
        } else {
            "启动服务失败".printAndLog()
        }
    }

    private fun getServiceIntent(): Intent? {
        when (User.getInstance().account) {
            "283" -> return Intent(this, FoShanService::class.java)
            "300" -> return Intent(this, HuNanService::class.java)
        }
        return null
    }

    private fun initView() {
        binding.projectName.text = User.getInstance().projectName
        binding.signOut.setOnClickListener {
            signOut()
        }
        binding.inDeviceNo.text = User.getInstance().inDeviceNo
        binding.outDeviceNo.text = User.getInstance().outDeviceNo
        User.setInOnlineChangeListener {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.connectStatus.text = if (User.getInstance().inOnline && User.getInstance().outOnline) "连接成功" else "连接中"
            }
        }
        User.setOutOnlineChangeListener {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.connectStatus.text = if (User.getInstance().inOnline && User.getInstance().outOnline) "连接成功" else "连接中"
            }
        }
        binding.version.text = versionCode
    }

    /*
    * 退出登录的相应处理，应有下线请求、数据清除、跳转至登录
    * */
    private fun signOut() {
        clearData()
        startActivity(Intent(this, SelectPlatformActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        stopService(Intent(this, FoShanService::class.java))
        super.onDestroy()
    }

    private fun clearData() {
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val fileName = "政府平台错误日志.text"
        val myFile = File(filePath, fileName)
        myFile.delete()

        User.getInstance().clearAll()

        val daoMaster = DaoMaster(MyApplication.getApplication().db)
        DaoMaster.dropAllTables(daoMaster.database, true);
        DaoMaster.createAllTables(daoMaster.database, true);

    }

}