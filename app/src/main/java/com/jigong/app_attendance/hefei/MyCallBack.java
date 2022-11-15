package com.jigong.app_attendance.hefei;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Map;
import java.util.Set;

/**
 * @author ds
 * @description: 接收消息回调函数
 * @date 2022/10/27 15:26
 */
public class MyCallBack implements MqttCallback {

    /**
     * 断连后重新连接并订阅相关主题
     *
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {
        Map<String, Set<String>> subscribeds = MyMqttClient.subscribeds;
        for (String subscribed : subscribeds.keySet()) {
            //当 mqtt 当前是连接状态时，直接跳过
            if (MyMqttClient.isConnect(subscribed)) {
                continue;
            }
//            log.info("当前连接《" + subscribed + "》已经掉线，准备重新连接。。。。。。");
            String[] split = subscribed.split("_");
            //当 mqtt 未连接时，执行重连 ，十次以内，或者重连上为止
            int cout = 1;
            do {
                connectMqtt(split);
                if (cout > 10) {
//                    log.info("当前连接《" + subscribed + "》已经掉线，多次重试后放弃。。。。。。");
                    return;
                }

//                log.info("当前连接《" + subscribed + "》正在进行第  " + cout + "  次重新连接。。。。。。");
                cout++;
            } while (!MyMqttClient.isConnect(subscribed));

//            log.info("当前连接《" + subscribed + "》已经重新连接。。。。。。");
        }
    }

    /***
     * @description: 重连 mqtt
     * @param:
     * @param split
     * @return: void
     * @author
     * @date: 2022/10/27 15:12
     */
    private void connectMqtt(String[] split) {
        try {
            MyMqttClient.getInstance(split[0], split[1], split[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消费消息
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());
        Log.i("TAG", "主题名：" + topic);
        Log.i("TAG", "订阅消息：" + msg);
        DealReturnDataKt.dealManager(topic.substring(topic.lastIndexOf("/") + 1), msg);
        if (!msg.contains("EditPersonsNew")) {
            return;
        }
    }

    /**
     * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
     *
     * @param token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            Log.i("TAG", "发布消息成功" + token.isComplete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}