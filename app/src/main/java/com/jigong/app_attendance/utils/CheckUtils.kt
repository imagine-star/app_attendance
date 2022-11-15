package com.jigong.app_attendance.utils

import android.text.TextUtils
import org.json.JSONObject

/**
 * @Author LiuHaoQi
 * @Description 做各种检验工作的文件
 * @Date 2022/11/15 10:27
 */

/*
* 返回值检验
* */
fun checkResult(result: String): Boolean {
    if (!TextUtils.isEmpty(result)) {
        val jsonObject = JSONObject(result)
        val respCode = JsonUtils.getJsonValue(jsonObject, "respCode", "")
        if (respCode == "1") {
            return true
        }
    }
    return false
}