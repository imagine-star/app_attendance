package com.jigong.app_attendance.hefei;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ds
 * @description: mqtt客户端连
 * @date 2022/10/27 14:32
 */
public class MyMqttClient {
    /***
     * @description: mqtt 连接地址
     * @author ds
     * @date: 2022/10/27 14:32
     */
    private static String HOST = "tcp://hfzjy.xcx.ditop.top:15408";

    /***
     * @description: 用于存放所有已经产生的 mqtt 连接
     * @author ds
     * @date: 2022/10/27 14:33
     */
    private static volatile Map<String, MqttClient> mqttClients = new ConcurrentHashMap<>();

    /***
     * @description: 用于存放所有已经产生的 mqtt 连接信息 对应的已订阅的主题
     * @author ds
     * @date: 2022/10/27 14:34
     */
    public static volatile Map<String, Set<String>> subscribeds = new ConcurrentHashMap<>();


    /***
     * @description: 初始化 mqtt 连接
     * @param:
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @return: org.eclipse.paho.client.mqttv3.MqttClient 连接对象
     * @author
     * @date: 2022/10/27 14:34
     */
    public static MqttClient getInstance(String userName, String passWord, String clientId) throws Exception {
        String key = userName + "_" + passWord + "_" + clientId;
        //查看缓存中是否存在连接对象
        MqttClient mqttClient = mqttClients.get(key);

        //连接对象不存在 ，或者没连接时，重新创建并连接
        if (mqttClient == null || !mqttClient.isConnected()) {
            synchronized (MyMqttClient.class) {
                MqttClient connect = MyMqttClient.connect(userName, passWord, clientId);
                //存放连接
                mqttClients.put(key, connect);
                //获取之前已经订阅过的主题
                Set<String> subscribed = subscribeds.get(key);
                if (subscribed == null) {
                    subscribed = new HashSet<>();
                    subscribeds.put(key, subscribed);
                }
                if (!subscribed.isEmpty() && connect.isConnected()) {
                    //重新订阅之前的主题
                    for (String topic : subscribed) {
                        connect.subscribe(topic, 0);
                    }
                }
            }
        }
        return mqttClients.get(key);
    }


    /***
     * @description: 判断 mqtt 是否连接
     * @param:
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @return: boolean
     * @author
     * @date: 2022/10/27 15:06
     */
    public static boolean isConnect(String userName, String passWord, String clientId) {
        String key = userName + "_" + passWord + "_" + clientId;
        MqttClient mqttClient = mqttClients.get(key);
        if (mqttClient == null || !mqttClient.isConnected()) {
            return false;
        }
        return true;
    }

    /***
     * @description: 判断 mqtt 是否连接
     * @param:
     * @param key  键
     * @return: boolean
     * @author
     * @date: 2022/10/27 15:07
     */
    public static boolean isConnect(String key) {
        MqttClient mqttClient = mqttClients.get(key);
        if (mqttClient != null && mqttClient.isConnected()) {
            return true;
        }
        return false;
    }

    /***
     * @description: 创建连接
     * @param:
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @return: org.eclipse.paho.client.mqttv3.MqttClient
     * @author
     * @date: 2022/10/27 14:46
     */
    private static MqttClient connect(String userName, String passWord, String clientId) throws Exception {
        //初始化连接设置对象
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        //true可以安全地使用内存持久性作为客户端断开连接时清除的所有状态
        mqttConnectOptions.setCleanSession(true);
        //设置连接超时
        mqttConnectOptions.setConnectionTimeout(30);
        // 设置连接的用户名
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setKeepAliveInterval(60);
        // 设置连接的密码
        mqttConnectOptions.setPassword(passWord.toCharArray());
        MqttClient client = new MqttClient(HOST, clientId, new MemoryPersistence());
        try {
            //执行回调
            client.setCallback(new MyCallBack());
            //创建连接
            client.connect(mqttConnectOptions);
        } catch (MqttException e) {
            Log.i("TAG", "MQTT 链接异常" + e);
            client.close();
        }
        return client;
    }

    /***
     * @description: 订阅某个主题  qos 默认为 1
     * @param:
     * @param topic 消息主题
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @return: void
     * @author ds
     * @date: 2022/10/27 14:37
     */
    public static void subTopic(String topic, String userName, String passWord, String clientId) throws Exception {
        getInstance(userName, passWord, clientId).subscribe(topic, 0);
        String key = userName + "_" + passWord + "_" + clientId;
        Set<String> set = subscribeds.get(key);
        if (set == null || set.isEmpty()) {
            set = new HashSet<>(1);
        }
        set.add(topic);
        subscribeds.put(key, set);
    }

    /***
     * @description: 订阅某个主题
     * @param:
     * @param topic 消息主题
     * @param gos 消息质量
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @return: void
     * @author ds
     * @date: 2022/10/27 14:37
     */
    public static void subTopic(String topic, int gos, String userName, String passWord, String clientId) throws Exception {
        getInstance(userName, passWord, clientId).subscribe(topic, gos);
        String key = userName + "_" + passWord + "_" + clientId;
        Set<String> set = subscribeds.get(key);
        if (set == null || set.isEmpty()) {
            set = new HashSet<>(1);
        }
        set.add(topic);
        subscribeds.put(key, set);
    }


    /***
     * @description: 向某个主题发布消息 默认qos：1
     * @param:
     * @param topic 消息主题
     * @param msg 消息
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @return: void
     * @author
     * @date: 2022/10/27 14:40
     */
    public static void pub(String topic, String msg, String userName, String passWord, String clientId) throws Exception {
        Thread.sleep(1000);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = MyMqttClient.getInstance(userName, passWord, clientId).getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /***
     * @description: 向某个主题发布消息
     * @param:
     * @param topic 消息主题
     * @param msg 消息
     * @param userName 用户名
     * @param passWord 密码
     * @param clientId
     * @param qos 消息质量
     * @return: void
     * @author
     * @date: 2022/10/27 14:40
     */
    public static void pub(String topic, String msg, int qos, String userName, String passWord, String clientId) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = MyMqttClient.getInstance(userName, passWord, clientId).getTopic(topic);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }
}