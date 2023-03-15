package com.jigong.app_attendance.longminggong;


import com.jigong.app_attendance.info.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.hutool.socket.nio.NioClient;

/**
 * @author EDZ
 */
public class BaseSocket {

    public static Map<String, Object> doLoginandHeartbeat2(String companyCode, String deviceId) {
        Map<String, Object> map = new HashMap<>();
        Boolean flag = false;
        try {
            String deviceIdToHex = HexUtil.strTo16FullLength(deviceId, 64);
            String companyCodeToHex = HexUtil.strTo16FullLength(companyCode, 64);
            String content = companyCodeToHex + deviceIdToHex;
            content = HexUtil.strTo16(companyCode + deviceId);
            //长度=content.lenth+xor 转16进制
            String lenth = Integer.toHexString((content.getBytes().length + 2) / 2);
            //设备登陆
            String info2 = "01" +//开始标记
                    HexUtil.full8(lenth) +//长度 LEN
                    "00000000" +//分包顺序索引
                    "00000000" +//分包总数
                    "01" +//版本
                    "4B03" +//命令
                    User.getInstance().getProjectId().toUpperCase(Locale.ROOT) +
                    content +
                    HexUtil.getBCC(content.getBytes()) +//xor运算
                    "00" +//状态
                    "01";//结束标记
            //设备登陆
            map = SocketTest.loginDevice(info2);
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getWorkerCode2(NioClient client) {
        String deviceIdToHex = HexUtil.strTo16FullLength(User.getInstance().getInDeviceNo(), 64);
        String date = com.jigong.app_attendance.utils.DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
        String content = deviceIdToHex + date;
        String lenth = Integer.toHexString((content.getBytes().length + 2) / 2);
        String info = "01" +//开始标记
                HexUtil.full8(lenth) +//长度 LEN
                "00000000" +//分包顺序索引
                "00000000" +//分包总数
                "01" +//版本
                "5103" +//命令
                User.getInstance().getProjectId().toUpperCase(Locale.ROOT) +
                //content
                content +
                HexUtil.getBCC(content.getBytes()) +//xor运算
                "00" +//状态
                "01";//结束标记
        return WorkerCodeSocketTest.sendMsg(info, client);
    }

    public static Map<Boolean, String> sendAttendance(String workerCode, String date, byte[] image, NioClient client) {
        String imageToHex = HexUtil.BinaryToHexString(image);
        String imageLength = HexUtil.byte2Hex(HexUtil.unlong2H4bytes((imageToHex.length() / 2)));
        imageLength = HexUtil.fullLength(imageLength, 8);
        String content = workerCode + date + "06" + imageLength + imageToHex;
        String lenth = HexUtil.byte2Hex(HexUtil.unlong2H4bytes((content.length() / 2) + 1));
        lenth = HexUtil.fullLength(lenth, 8);
        //考勤的报文组装
        String info = "01" +//开始标记
                lenth +//长度 LEN
                "00000000" +//分包顺序索引
                "00000000" +//分包总数
                "01" +//版本
                "5003" +//命令
                User.getInstance().getProjectId().toUpperCase(Locale.ROOT) +
                //content
                content +
                HexUtil.getBCC(content.getBytes()) +//xor运算
                "00" +//状态
                "01";//结束标记
        return WorkerAttendanceTestSocket.sendWorkerAttendance(info, client);
    }

    public static String getWorkerInfo(String idNumber, NioClient client) {
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String idNumberToHex = HexUtil.strTo16FullLength(idNumber, 36);
        String deviceIdToHex = HexUtil.strTo16FullLength(User.getInstance().getInDeviceNo(), 64);
        String content = deviceIdToHex + idNumberToHex;
        String length = HexUtil.byte2Hex(HexUtil.unlong2H4bytes((content.length() / 2) + 1));
        length = HexUtil.fullLength(length, 8);
        String info = "01" +//开始标记
                length +//长度 LEN
                "00000000" +//分包顺序索引
                "00000000" +//分包总数
                "01" +//版本
                "4D03" +//命令
                User.getInstance().getProjectId().toUpperCase(Locale.ROOT) +
                //content
                content +
                HexUtil.getBCC(content.getBytes()) +//xor运算
                "00" +//状态
                "01";//结束标记
        return WorkerInfoSocket.sendMsg(info, idNumber, client);
    }

    public static void sendHeartInfo(NioClient nioClient) {
        String info = "0100000000000000000000000001FFFF" + User.getInstance().getProjectId() + "0001";
        HeartSocket.sendHeartInfo(info, nioClient);
    }

}
