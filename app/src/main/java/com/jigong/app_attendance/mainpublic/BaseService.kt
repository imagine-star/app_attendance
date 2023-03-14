package com.jigong.app_attendance.mainpublic

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import kotlinx.coroutines.Job
import java.util.Timer


open class BaseService : Service() {

    private var wakeLock: WakeLock? = null
    var timer = Timer(false)

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            BaseService::class.java.name
        )
        wakeLock?.acquire()
    }

    override fun onDestroy() {
        if (wakeLock != null) {
            wakeLock?.release();
            wakeLock = null;
        }
        super.onDestroy();
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

}