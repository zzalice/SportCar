package com.sportcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MqttHelper mqtt;
    TextView t_hi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t_hi = findViewById(R.id.hi);
        final TextView t_pi_temp = findViewById(R.id.pi_temp);
        final Button button_on = findViewById(R.id.button_on);
        final Button button_off = findViewById(R.id.button_off);

        mqtt = new MqttHelper();
        mqttCallback();

        mqtt.startSub();//訂閱
        mqtt.startPub("Android init");//發佈

        button_on.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            mqtt.startPub("Android on");
            }
        });
        button_off.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            mqtt.startPub("Android off");
            }
        });
    }

    private void mqttCallback(){
        mqtt.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // 這裡可以寫重連程式
                System.out.println("Write_reconnect_here");
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
                final String msg = new String(message.getPayload());

                runOnUiThread(new Runnable(){
                    public void run() {
                        t_hi.setText(msg);
                    }
                });

            }
        });
    }
}
