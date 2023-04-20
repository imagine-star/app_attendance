package com.jigong.app_attendance.mainpublic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.jigong.app_attendance.databinding.ActivityInfoManageBinding
import com.jigong.app_attendance.foshan.*
import com.jigong.app_attendance.greendao.DaoMaster
import com.jigong.app_attendance.hunan.HuNanService
import com.jigong.app_attendance.info.User
import com.jigong.app_attendance.jiangmen.JiangMenService
import com.jigong.app_attendance.longminggong.LongMingGongService
import com.jigong.app_attendance.meishan.MeiShanService
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
            doNext()
            true
        }

        val intent = getServiceIntent()
        if (intent != null) {
            startService(intent)
        } else {
            showToastMsgShort("启动服务失败，未选择平台")
            startActivity(Intent(this, SelectPlatformActivity::class.java))
            finish()
        }
    }

    private fun doNext() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0x66)
        } else {
            val intent = Intent(this, ShowLogActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0x66) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, ShowLogActivity::class.java)
                startActivity(intent)
            } else { //申请拒绝
                Toast.makeText(this, "您已拒绝读写权限，...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getServiceIntent() = when (User.getInstance().joinCity) {
        "279" -> Intent(this, JiangMenService::class.java)
        "283" -> Intent(this, FoShanService::class.java)
        "300" -> Intent(this, HuNanService::class.java)
        "301" -> Intent(this, LongMingGongService::class.java)
        "306" -> Intent(this, MeiShanService::class.java)
        else -> null
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
        val intent = getServiceIntent()
        if (intent != null) {
            stopService(intent)
        }
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