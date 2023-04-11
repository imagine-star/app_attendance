package com.jigong.app_attendance.utils

import android.text.TextUtils
import com.jigong.app_attendance.info.printAndLog
import org.json.JSONObject

/**
 * @Author LiuHaoQi
 * @Description 做各种检验工作的文件
 * @Date 2022/11/15 10:27
 */

/*
* 返回值检验
* */
fun checkResult(title: String, result: String): Boolean {
    if (!TextUtils.isEmpty(result)) {
        val jsonObject = JSONObject(result)
        val respCode = JsonUtils.getJsonValue(jsonObject, "respCode", "")
        return if (respCode.equals("1")) {
            true
        } else {
            val entry = JsonUtils.getJSONObject(jsonObject, "entry")
            val respMsg = JsonUtils.getJsonValue(entry, "respMsg", "")
            "济工网接口${title}请求失败返回：$respMsg".printAndLog()
            false
        }
    }
    return false
}
/*
* 返回值检验
* */
fun getMsg(result: String): String {
    if (!TextUtils.isEmpty(result)) {
        val jsonObject = JSONObject(result)
        val entry = JsonUtils.getJSONObject(jsonObject, "entry")
        return JsonUtils.getJsonValue(entry, "respMsg", "")
    }
    return ""
}

/*
* 返回值检验
* */
fun checkLogin(result: String): Boolean {
    if (!TextUtils.isEmpty(result)) {
        val jsonObject = JSONObject(result)
        val respCode = JsonUtils.getJsonValue(jsonObject, "respCode", "")
        if (respCode == "-99") {
            return false
        }
    }
    return true
}