package com.sportcar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHelper {

    public static MqttClient client;


    public static String myTopic = "control/pwm";

    public static MqttConnectOptions options;

    public static String mqttHost = "tcp://192.168.1.101";//改為自己的MQTT SERVER IP

    public  MqttHelper(){
        try {
            client = new MqttClient(mqttHost, "linkU", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
//            options.setUserName("xxxxx");//如果有帳號，可以這裡設定
//            options.setPassword("xxxxxxx".toCharArray());//如果有密碼，可以這裡設定
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // 這裡可以寫重連程式
                    System.out.println("這裡寫重連");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("發送完成 --" + token.isComplete());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // subscribe后得到的訊息如下
                    System.out.println("主题 : " + topic);
                    System.out.println("Qos : " + message.getQos());
                    System.out.println("内容 : " + new String(message.getPayload()));
                    
                }
            });

            client.connect(options);


        }catch(MqttException me) {

            me.printStackTrace();
        }


    }


    public MqttHelper(String h, String t){
        myTopic = t;
        mqttHost = "tcp://" + h;
        try {
            client = new MqttClient(mqttHost, "linkU", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
//            options.setUserName("xxxxx");//如果有帳號，可以這裡設定
//
//            options.setPassword("xxxxxxx".toCharArray());//如果有密碼，可以這裡設定

            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                    System.out.println("這裡寫重連");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("發送完成 --" + token.isComplete());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    System.out.println("主题 : " + topic);
                    System.out.println("Qos : " + message.getQos());
                    System.out.println("内容 : " + new String(message.getPayload()));
                }
            });
            client.connect(options);//連線囉！
        }catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    //訂閱
    public static void startSub(){


        try {

            int[] Qos = {1};
            String[] topic1 = {myTopic};
            client.subscribe(topic1, Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
    //發佈
    public static void startPub(String m){

        try {

            MqttTopic topic = client.getTopic(myTopic);

            MqttMessage message = new MqttMessage(m.getBytes());message.setQos(0);

            client.publish(myTopic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}