package com.jigong.app_attendance.socket;

import android.text.TextUtils;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.socket.nio.NioClient;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuting
 * @date 2022/10/25
 */
public class WorkerAttendanceTestSocket {

    private static NioClient client;

    private static volatile String result = "";

    public static synchronized Map<Boolean, String> sendWorkerAttendance(String info, NioClient client) {
        Map<Boolean, String> map = new HashMap<>(1);
        WorkerAttendanceTestSocket.client = client;
        WorkerAttendanceTestSocket.listen();
        if (TextUtils.isEmpty(info)) {
            return null;
        }
        try {
            byte[] m = com.jigong.app_attendance.utils.HexUtil.hexStringToBytes(info);
            client.write(BufferUtil.create(m));
            Integer count = 1;
            while (true) {
                if(StrUtil.isNotBlank(result)) {
                    String t=result;
                    String cmd = t.substring(28, 32);
                    if ("5003".equals(cmd)) {
                        System.out.println("接收到上传人员考勤信息返回数据" + result);
                        int lenth = Integer.parseInt(com.jigong.app_attendance.utils.HexUtil.reverseString(result.substring(2, 10)), 16);
                        String resultContent = result.substring(64, 64 + lenth * 2);
                        String code = result.substring(64 + lenth * 2, 66 + lenth * 2);
                        if ("00".equals(code)) {
                            System.out.println("上传人员考勤信息成功！");
                            map.put(true, "上传成功");
                        } else {
                            System.out.println("上传人员考勤信息失败！原因是：" + com.jigong.app_attendance.utils.HexUtil.hexStringToString(resultContent));
                            map.put(false, com.jigong.app_attendance.utils.HexUtil.hexStringToString(resultContent));
                        }
                    }
                    break;
                }
                if(count%5==0) {
                    System.out.println("无返回值");
                    break;
                }
                //Thread.sleep(1000);
                Thread.sleep(80);
                WorkerAttendanceTestSocket.listen();
                count++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void listen() {
        client.setChannelHandler((sc) -> {
            ByteBuffer readBuffer = ByteBuffer.allocate(5120);
            int readBytes = sc.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = HexUtil.encodeHexStr(bytes, false);
                if (StrUtil.isNotEmpty(body) && body.length() >= 32) {
                    String cmd = body.substring(28, 32);
                    if ("5003".equals(cmd)) {
                        result = body;
                    }
                }
            } else if (readBytes < 0) {
                sc.close();
            }
        });
        client.listen();
    }

}
