package com.jigong.app_attendance.foshan;

import android.text.TextUtils;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.socket.nio.NioClient;

import java.nio.ByteBuffer;

/**
 * @author yuting
 * @date 2022/10/27
 */
public class HeartSocket {

    private static NioClient client;

    private static volatile String result = "";

    public static synchronized void sendHeartInfo(String info, NioClient client) {
        HeartSocket.client = client;
        if (TextUtils.isEmpty(info)) {
            return;
        }
        try {
            byte[] m = HexUtil.hexStringToBytes(info);
            client.write(BufferUtil.create(m));
            Integer count = 1;
            while (true) {
                if (StrUtil.isNotBlank(result)) {
                    String t = result;
                    String cmd = t.substring(28, 32);
                    if ("FFFF".equals(cmd)) {
                        System.out.println("接收到心跳返回数据");
                    }
                    break;
                }
                if (count % 10 == 0) {
                    System.out.println("设备心跳无返回值");
                    break;
                }
                //Thread.sleep(1000);
                Thread.sleep(80);
                HeartSocket.listen();
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listen() {
        client.setChannelHandler((sc) -> {
            ByteBuffer readBuffer = ByteBuffer.allocate(5120);
            int readBytes = sc.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = cn.hutool.core.util.HexUtil.encodeHexStr(bytes, false);
                if (StrUtil.isNotEmpty(body) && body.length() >= 32) {
                    String cmd = body.substring(28, 32);
                    if ("FFFF".equals(cmd)) {
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
