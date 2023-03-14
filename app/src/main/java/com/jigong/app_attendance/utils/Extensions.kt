package com.jigong.app_attendance.info

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author LiuHaoQi
 * @Description 拓展函数
 * @Date 2023/3/14 10:17
 */

fun String.addEnthusiasm(amount: Int = 1) = this + "!".repeat(amount)

val String.numVowels
    get() = count { "aeiouy".contains(it) }

infix fun String?.printWithDefault(default: String) = println(this ?: default)

fun <T> T.easyPrint() {
    println(this)
}
@SuppressLint("SimpleDateFormat")
fun <T> T.printAndLog() {
    println(this)
    val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val fileName = "政府平台错误日志.text"
    val myFile = File(filePath, fileName)
    myFile.appendText("${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date().time)}：$this\n")
}

fun Float.point(size: Int = 2): Float {
    val format = DecimalFormat("#.##")
    //舍弃规则，RoundingMode.FLOOR表示直接舍弃。
    format.roundingMode = RoundingMode.FLOOR
    return format.format(this).toFloat()
}