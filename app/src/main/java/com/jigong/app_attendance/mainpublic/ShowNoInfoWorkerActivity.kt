package com.jigong.app_attendance.mainpublic

import android.annotation.SuppressLint
import android.os.Bundle
import com.jigong.app_attendance.databinding.ActivityShowNoInfoWorkerBinding

class ShowNoInfoWorkerActivity : BaseActivity() {

    private lateinit var binding: ActivityShowNoInfoWorkerBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowNoInfoWorkerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val workerInfoDao = MyApplication.getApplication().daoSession.workerInfoDao
        val workerList = workerInfoDao.queryBuilder().list()

        var info = ""
        var infoNum = 0
        var noInfo = ""
        var noInfoNum = 0
        workerList?.forEach {
            if (!it.getInfo) {
                noInfo += "平台编码：${it.workerCode}\n" +
                        "工人姓名：${it.name}\n" +
                        "工人身份证号：${it.idNumber}\n\n"
                noInfoNum++
            } else {
                info += "平台编码：${it.workerCode}\n" +
                        "工人姓名：${it.name}\n" +
                        "工人身份证号：${it.idNumber}\n\n"
                infoNum++
            }
        }
        binding.workerInfo.text = "获取成功的工人($infoNum)：\n\n$info"
        binding.workerNoInfo.text = "获取失败的工人($noInfoNum)：\n\n$noInfo"

        binding.back.setOnClickListener {
            finish()
        }

    }
}