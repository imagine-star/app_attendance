package com.jigong.app_attendance.longminggong;

import android.text.TextUtils;

import com.jigong.app_attendance.bean.WorkerInfo;
import com.jigong.app_attendance.greendao.WorkerInfoDao;
import com.jigong.app_attendance.info.User;
import com.jigong.app_attendance.mainpublic.MyApplication;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.socket.nio.NioClient;

/**
 * @author yuting
 * @date 2022/12/20
 */
public class WorkerInfoSocket {

    private static NioClient client;

    private static volatile String result = "";

    public synchronized static String sendMsg(String info, String recordId, NioClient client) {
        WorkerInfoSocket.client = client;
        WorkerInfoSocket.listen();
        String resultString = null;
        if (TextUtils.isEmpty(info)) {
            return "获取工人信息无请求信息，直接返回";
        }
        try {
            byte[] m = com.jigong.app_attendance.longminggong.HexUtil.hexStringToBytes(info);
            client.write(BufferUtil.create(m));
            WorkerInfoSocket.listen();
            Integer count = 1;
            while (true) {
                if (StrUtil.isNotBlank(WorkerInfoSocket.result)) {
                    String workerCode = "";
                    String name = "";
                    String idNumber = "";
                    String gender = "";
                    int lenth = Integer.parseInt(com.jigong.app_attendance.longminggong.HexUtil.reverseString(result.substring(2, 10)), 16);
                    String code = result.substring(64 + lenth * 2, 66 + lenth * 2);
                    if ("00".equals(code) || lenth > 100) {
                        resultString = "获取人员特征信息成功, projectId=" + User.getInstance().getProjectId();
                        String t = result;
                        String resultContent = t.substring(64);
                        workerCode = resultContent.substring(0, 8);
                        name = HexUtil.decodeHexStr(resultContent.substring(8, 68), StandardCharsets.UTF_8);
                        idNumber = HexUtil.decodeHexStr(resultContent.substring(68, 104), StandardCharsets.US_ASCII);
                        gender = HexUtil.decodeHexStr(resultContent.substring(106, 110), StandardCharsets.US_ASCII);
                        String string = com.jigong.app_attendance.longminggong.HexUtil.reverseString(resultContent.substring(670, 678));
                        int glLong = Integer.parseInt(string, 16);
                        byte[] bytes = HexUtil.decodeHex(resultContent.substring(678, 678 + (glLong * 2)));
//                        String glImage = Base64Encoder.encode(bytes);
                        WorkerInfoDao workerInfoDao = MyApplication.getApplication().getDaoSession().getWorkerInfoDao();
                        WorkerInfo workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idNumber)).unique();
                        if (workerInfo == null) {
                            workerInfo = new WorkerInfo();
                            workerInfo.setWorkerCode(workerCode);
                            workerInfo.setName(name);
                            workerInfo.setIdNumber(idNumber);
                            workerInfo.setGender(gender);
                            workerInfo.setPicURI(bytes);
                            workerInfo.setGetInfo(true);
                            workerInfo.setHasPush(false);
                            workerInfoDao.insert(workerInfo);
                        } else {
                            workerInfo.setWorkerCode(workerCode);
                            workerInfo.setName(name);
                            workerInfo.setIdNumber(idNumber);
                            workerInfo.setGender(gender);
                            workerInfo.setPicURI(bytes);
                            workerInfo.setGetInfo(true);
                            workerInfo.setHasPush(false);
                            workerInfoDao.update(workerInfo);
                        }
                    } else {
                        String resultContent = result.substring(64, 64 + (lenth) * 2);
                        resultString = "获取人员信息失败, projectId=" + User.getInstance().getProjectId() +
                                ", 原因是：" + com.jigong.app_attendance.longminggong.HexUtil.hexStringToString(resultContent) +
                                "，身份证号：" + recordId +
                                ", 返回报文: " + result;
                        break;
                    }
                    break;
                }
                if (count % 10 == 0) {
                    resultString = "获取人员特征值无返回值";
                    break;
                }
                count++;
                Thread.sleep(80);
                WorkerInfoSocket.listen();
            }
        } catch (IORuntimeException e) {
            if (client != null) {
                client.close();
            }
            WorkerInfoSocket.listen();
        } catch (Exception e) {
//            logger.error("error:", e);
        }
        result = "";
        return resultString;
    }

    public synchronized static void sendMsgTest(NioClient client) {
        String info = "01330000000000000000000000014D03AF5D144AB54D484DB71DA55DBEDF593C61663564313434616235346434383464623731646135356462656466353933633434303632333139363930353034343731380A0001";
        WorkerInfoSocket.client = client;
        WorkerInfoSocket.listen();
        if (TextUtils.isEmpty(info)) {
            return;
        }
        try {
            byte[] m = com.jigong.app_attendance.longminggong.HexUtil.hexStringToBytes(info);
            client.write(BufferUtil.create(m));
            WorkerInfoSocket.listen();
            Integer count = 1;
            while (true) {
                if (StrUtil.isNotBlank(WorkerInfoSocket.result)) {
                    String workerCode = "";
                    String name = "";
                    String idNumber = "";
                    String gender = "";
                    int lenth = Integer.parseInt(com.jigong.app_attendance.longminggong.HexUtil.reverseString(result.substring(2, 10)), 16);
                    String code = result.substring(64 + lenth * 2, 66 + lenth * 2);
                    if ("00".equals(code) || lenth > 100) {
                        System.out.println("获取人员特征信息成功, projectId=" + User.getInstance().getProjectId());
                        String t = result;
                        String resultContent = t.substring(64);
                        workerCode = resultContent.substring(0, 8);
                        name = HexUtil.decodeHexStr(resultContent.substring(8, 68), StandardCharsets.UTF_8);
                        idNumber = HexUtil.decodeHexStr(resultContent.substring(68, 104), StandardCharsets.US_ASCII);
                        gender = HexUtil.decodeHexStr(resultContent.substring(106, 110), StandardCharsets.US_ASCII);
                        String string = com.jigong.app_attendance.longminggong.HexUtil.reverseString(resultContent.substring(670, 678));
                        int glLong = Integer.parseInt(string, 16);
                        byte[] bytes = HexUtil.decodeHex(resultContent.substring(678, 678 + (glLong * 2)));
//                        String glImage = Base64Encoder.encode(bytes);
                        WorkerInfoDao workerInfoDao = MyApplication.getApplication().getDaoSession().getWorkerInfoDao();
                        WorkerInfo workerInfo = workerInfoDao.queryBuilder().where(WorkerInfoDao.Properties.IdNumber.eq(idNumber)).unique();
                        if (workerInfo == null) {
                            workerInfo = new WorkerInfo();
                            workerInfo.setWorkerCode(workerCode);
                            workerInfo.setName(name);
                            workerInfo.setIdNumber(idNumber);
                            workerInfo.setGender(gender);
                            workerInfo.setPicURI(bytes);
                            workerInfoDao.insert(workerInfo);
                        } else {
                            workerInfo.setWorkerCode(workerCode);
                            workerInfo.setName(name);
                            workerInfo.setIdNumber(idNumber);
                            workerInfo.setGender(gender);
                            workerInfo.setPicURI(bytes);
                            workerInfoDao.update(workerInfo);
                        }
                    } else {
                        String resultContent = result.substring(64, 64 + (lenth) * 2);
                        System.out.println("获取人员信息失败, projectId=" + User.getInstance().getProjectId() +
                                ", 原因是：" + com.jigong.app_attendance.longminggong.HexUtil.hexStringToString(resultContent) +
                                ", 返回报文: " + result);
                        break;
                    }
                    break;
                }
                if (count % 10 == 0) {
                    System.out.println("获取人员特征值无返回值");
                    break;
                }
                count++;
                Thread.sleep(80);
                WorkerInfoSocket.listen();
            }
        } catch (IORuntimeException e) {
            if (client != null) {
                client.close();
            }
            WorkerInfoSocket.listen();
        } catch (Exception e) {
//            logger.error("error:", e);
        }
        result = "";
    }


    public static void listen() {
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
                        Thread.sleep(80L);
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
                    if ("4D03".equals(cmd)) {
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
