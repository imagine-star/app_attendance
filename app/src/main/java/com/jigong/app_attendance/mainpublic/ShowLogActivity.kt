package com.jigong.app_attendance.mainpublic

import android.os.Bundle
import android.os.Environment
import com.jigong.app_attendance.databinding.ActivityShowLogBinding
import java.io.File

class ShowLogActivity : BaseActivity() {

    private lateinit var binding: ActivityShowLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val fileName = "政府平台错误日志.text"
        val myFile = File(filePath, fileName)
        if (myFile.exists()) {
            val logcat: String = myFile.readText()
            binding.logInfo.text = logcat
        }

        binding.back.setOnClickListener {
            finish()
        }

    }
}