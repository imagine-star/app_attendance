package com.jigong.app_attendance.meishan

/**
 * @Author LiuHaoQi
 * @Description 眉山政府平台直连相关数据
 * @Date 2023/4/11 15:02
 */
object MeiShanServer {

    /**
     * 请求地址前缀
     */
    private const val serverUrl = "https://zhgd.msjsgl.com/smzkq/"

    /**
     * 心跳接口
     */
    const val online = "${serverUrl}Service/DevicePacketWebSvr.assx/onLine"

    /**
     * 获取下发数据
     */
    const val getTaskData = "${serverUrl}Service/DevicePacketWebSvr.assx/getTaskData"

    /**
     * 反馈处理状态接口
     */
    const val retResult = "${serverUrl}Service/DevicePacketWebSvr.assx/retResult"

}