package com.jigong.app_attendance.jiangmen;

import android.text.TextUtils;

import com.jigong.app_attendance.mainpublic.MyApplication;
import com.jigong.app_attendance.bean.WorkerInfo;
import com.jigong.app_attendance.greendao.WorkerInfoDao;
import com.jigong.app_attendance.info.User;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.socket.nio.NioClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuting
 * @date 2022/10/21
 */
public class WorkerCodeSocketTest {

    private static NioClient client;

    private static volatile String result = "";

    public static String sendMsg(String info, NioClient client) {
        WorkerCodeSocketTest.client = client;
        StringBuilder resultString = new StringBuilder();
        if (TextUtils.isEmpty(info)) {
            return "获取白名单无请求信息，直接返回";
        }
        WorkerInfoDao workerInfoDao = MyApplication.getApplication().getDaoSession().getWorkerInfoDao();
        List<WorkerInfo> workerInfos = workerInfoDao.queryBuilder().list();
        List<String> idNumberList = new ArrayList<>();
        if (workerInfos != null) {
            for (WorkerInfo workerInfo : workerInfos) {
                idNumberList.add(workerInfo.getIdNumber());
            }
        }
        try {
            byte[] m = com.jigong.app_attendance.foshan.HexUtil.hexStringToBytes(info);
            client.write(BufferUtil.create(m));
            WorkerCodeSocketTest.listen();
//            Thread.sleep(1000);
            Integer count = 1;
            while (true) {
//                WorkerCodeSocketTest.listen();
//                result = "01000000000000000000000000015103793B694293B84BDDB0C6534F448C441501010100000000000000000000000001FFFF793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C44150001";
                if (StrUtil.isNotBlank(WorkerCodeSocketTest.result)) {
//                    result = "01000000000000000000000000015103793B694293B84BDDB0C6534F448C441501010100000000000000000000000001FFFF793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C4415000101000000000000000000000000015003793B694293B84BDDB0C6534F448C44150001";
                    String workerCode = "";
                    String name = "";
                    String idNumber = "";
                    int lenth = Integer.parseInt(com.jigong.app_attendance.foshan.HexUtil.reverseString(result.substring(2, 10)), 16);
                    boolean overRange = false;
                    String code = null;
                    try {
                        code = result.substring(64 + lenth * 2, 66 + lenth * 2);
                    } catch (Exception e) {
                        if (64 + lenth * 2 > result.length()) {
                            System.out.println("获取信息不完整");
                            overRange = true;
                        }
                    }
                    if ("00".equals(code) || overRange) {
                        System.out.println("获取白名单成功, projectId=" + User.getInstance().getProjectId() + ", 报文= $result" + result);
                        String t = result;
                        String resultContent = t.substring(272);
                        int length = resultContent.length();
                        int index = 0;
                        while (true) {
                            workerCode = resultContent.substring(index, index + 8);
                            name = HexUtil.decodeHexStr(resultContent.substring(index + 8, index + 28), StandardCharsets.UTF_8);
                            idNumber = HexUtil.decodeHexStr(resultContent.substring(index + 28, index + 64), StandardCharsets.US_ASCII);
                            try {
                                WorkerInfo workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idNumber)).unique();
                                if (workerInfo == null) {
                                    workerInfo = new WorkerInfo();
                                    workerInfo.setWorkerCode(workerCode);
                                    workerInfo.setName(name);
                                    workerInfo.setIdNumber(idNumber);
                                    workerInfo.setHasPush(false);
                                    workerInfoDao.insert(workerInfo);
                                }
                                if (!workerInfo.getGetInfo()) {
                                    resultString.append(BaseSocket.getWorkerInfo(idNumber, client)).append("\n");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            index = index + 82;
                            length = length - 82;
                            if (length < 20) {
                                break;
                            }
                        }
                    } else {
                        String resultContent = result.substring(64, 64 + (lenth) * 2);
                        resultString = new StringBuilder("获取白名单失败, projectId=" + User.getInstance().getProjectId() +
                                ", 原因是：" + com.jigong.app_attendance.foshan.HexUtil.hexStringToString(resultContent) +
                                ", 返回报文: " + result);
                        break;
                    }
                    break;
                }
                if (count % 50 == 0) {
                    System.out.println("获取白名单无返回值");
                    break;
                }
                count++;
                Thread.sleep(80);
                WorkerCodeSocketTest.listen();
            }
        } catch (IORuntimeException e) {
            if (client != null) {
                client.close();
            }
            WorkerCodeSocketTest.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString.toString();
    }


    public static void listen() {
        if (!client.getChannel().isConnected()) {
            return;
        }
        client.setChannelHandler((sc) -> {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 50);
            int readBytes = sc.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = HexUtil.encodeHexStr(bytes, false);
                int i = 1;
                while (true) {
                    if (!"01".equals(body.substring(body.length() - 3, body.length() - 1))) {
//                        Thread.sleep(800L);
                        ByteBuffer allocate = ByteBuffer.allocate(1024 * 50);
                        int read = sc.read(allocate);
//                        while (true) {
                        if (read > 0) {
                            allocate.flip();
                            byte[] bytes1 = new byte[allocate.remaining()];
                            allocate.get(bytes1);
                            String s = HexUtil.encodeHexStr(bytes1, false);
                            body = body + s;
//                                break;
                        }
//                        }
                    }
                    if ("01".equals(body.substring(body.length() - 3, body.length() - 1))) {
                        break;
                    }
                    if (i % 10 == 0) {
                        break;
                    }
                    i++;
                }
                if (StrUtil.isNotEmpty(body) && body.length() >= 32) {
                    String cmd = body.substring(28, 32);
                    if ("5103".equals(cmd)) {
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
