package com.jigong.app_attendance.longminggong;


import com.jigong.app_attendance.info.GlobalCode;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.socket.nio.NioClient;

public class SocketTest {
    private static NioClient client = null;

    public final static String keepinfo = "0100000000000000000000000001FFFFEFBFBD1C79EFBFBDEFBFBD4531EFBFBD0001";

    private volatile static String rootResult = "";

    private volatile static Boolean flagOne = false;

    private volatile static Boolean flagTwo = false;

    public static void connect() {
        client = new NioClient(GlobalCode.HOST, GlobalCode.PORT);
    }

    public static void listen() {
        client.setChannelHandler((sc) -> {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            //从channel读数据到缓冲区
            int readBytes = sc.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = HexUtil.BinaryToHexString(bytes);
                String result = body;
                Console.log("[{}]: {}", sc.getRemoteAddress(), body);
                if (StrUtil.isNotEmpty(result)) {
                    String cmd = "";
                    if (result.length() >= 32) {
                        cmd = result.substring(28, 32);
                    }
                    if ("4B03".equals(cmd)) {
                        rootResult = body;
                    }

                    if ("3C03".equals(cmd)) {
//                        logger.info("接收到考勤返回数据" + body);
                        int lenth = Integer.parseInt(HexUtil.reverseString(body.substring(2, 10)), 16);
                        String resultContent = body.substring(64, 64 + lenth * 2);
                        String code = body.substring(64 + lenth * 2, 66 + lenth * 2);
                        if ("00".equals(code)) {
//                            logger.info("上传考勤成功！");
                        } else {
//                            logger.info("上传考勤失败！原因是：" + HexUtil.hexStringToString(resultContent));
                        }
                    } else if ("FFFF".equals(cmd)) {
//                        logger.info("接收到心跳返回数据" + body);
                    }
                }
            } else if (readBytes < 0) {
                sc.close();
            }
        });
        client.listen();
    }

    public synchronized static Boolean sendMsg(String info) {
        if (client == null) {
            SocketTest.connect();
            SocketTest.listen();
        }
        if (StrUtil.isNotBlank(info)) {
            byte[] m = HexUtil.hexStringToBytes(info);
//            logger.info("发送报文！", m);
            try {
                client.write(BufferUtil.create(m));
            } catch (IORuntimeException e) {
//                logger.error("socket发送消息异常", e);
                if (client != null) {
                    client.close();
                }
                SocketTest.connect();
                SocketTest.listen();
            }
        }
        return true;
    }

    public synchronized static Map<String, Object> loginDevice(String info) {
        Map<String, Object> map = new HashMap<>();
        Boolean flag = false;
        if (client == null || !client.getChannel().isConnected()) {
//            logger.info("设备触发重新登录");
            SocketTest.connect();
            SocketTest.listen();
        }
        map.put("client", client);
        if (StrUtil.isNotBlank(info)) {
            byte[] m = HexUtil.hexStringToBytes(info);
//            logger.info("发送报文！" + info);
            try {
                rootResult = "";
                client.write(BufferUtil.create(m));
                Integer count = 1;
                while (true) {
                    if (StrUtil.isNotBlank(rootResult)) {
                        String t = rootResult;
                        String cmd = t.substring(28, 32);
                        if ("4B03".equals(cmd)) {
                            System.out.println("接收到设备登录返回数据" + rootResult);
                            int lenth = Integer.parseInt(HexUtil.reverseString(rootResult.substring(2, 10)), 16);
                            String resultContent = rootResult.substring(64, 64 + (lenth) * 2);
                            String code = rootResult.substring(rootResult.length() - 4, rootResult.length() - 2);
                            if ("00".equals(code)) {
                                System.out.println("设备登陆成功！");
                                flag = true;
                                map.put("flag", flag);
                            } else {
                                System.out.println("设备登陆失败！" + "原因是：" + HexUtil.hexStringToString(resultContent));
                                map.put("flag", flag);
                            }
                        }
                        break;
                    }
                    if (count % 5 == 0) {
                        System.out.println("设备登陆无返回值");
                        break;
                    }
                    //Thread.sleep(1000);
                    Thread.sleep(80);
                    SocketTest.connect();
                    SocketTest.listen();
                    count++;
                }
            } catch (IORuntimeException e) {
//                logger.error("socket发送消息异常", e);
                if (client != null) {
                    client.close();
                }
                SocketTest.connect();
                SocketTest.listen();
            } catch (Exception e) {
//                logger.error("error:", e);
            }
        }
        rootResult = "";
        return map;
    }

}
