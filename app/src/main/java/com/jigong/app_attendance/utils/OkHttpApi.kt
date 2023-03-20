package com.jigong.app_attendance.utils

import android.annotation.SuppressLint
import com.alibaba.fastjson.JSON
import com.jigong.app_attendance.info.GlobalCode
import com.jigong.app_attendance.info.User
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @Author LiuHaoQi
 * @Description okhttp网络请求
 * @Date 2022/11/14 9:32
 */

val TYPE_JSON = "application/json; charset=utf-8".toMediaType()
val TYPE_NORMAL = "text/plain; charset=utf-8".toMediaType()
val TYPE_PICTURE = "multipart/form-data; charset=utf-8".toMediaType()
val TYPE_IMAGE = "image/*".toMediaType()

fun doGet() {
    val url = "" //创建request请求对象
    val request = Request.Builder().url(url) //.method()方法与.get()方法选取1种即可
            .method("GET", null).build() //创建call并调用enqueue()方法实现网络请求
    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
        }

        override fun onResponse(call: Call, response: Response) {
        }
    })
}

fun doPostJson(header: String, url: String, params: Map<String, Any>): String {
    val jsonData = JSON.toJSON(params)
    val requestBody = jsonData.toString().toRequestBody(TYPE_JSON)
    val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS) //设置连接超时时间
            .readTimeout(30, TimeUnit.SECONDS) //设置读取超时时间
            .build() //设置读取超时时间 //创建request请求对象
    val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("applyId", header)
            .build() //创建call并调用enqueue()方法实现网络请求
    try {
        val response = okHttpClient.newCall(request).execute()
        return response.body?.string().toString()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ""
}

@SuppressLint("HardwareIds")
fun doPostJson(url: String, params: Map<String, Any>): String {
    val targetUrl = GlobalCode.HTTP_SERVER + url
    val jsonData = JSON.toJSON(params)
    val requestBody = jsonData.toString().toRequestBody(TYPE_JSON)
    val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS) //设置连接超时时间
            .readTimeout(30, TimeUnit.SECONDS) //设置读取超时时间
            .build() //设置读取超时时间 //创建request请求对象
    val request = Request.Builder()
            .addHeader("token", User.getInstance().gomeetToken)
            .addHeader("sn", android.os.Build.SERIAL)
            .url(targetUrl)
            .post(requestBody)
            .build()
    try {
        val response = okHttpClient.newCall(request).execute()
        return response.body?.string().toString()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ""
}