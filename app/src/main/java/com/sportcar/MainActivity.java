package com.sportcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static String topicPWM = "control/pwm";
    private static String topicTemp = "car/temp";

    MqttHelper mqtt;
    TextView t_hi;
    TextView t_pi_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t_hi = findViewById(R.id.hi);
        t_pi_temp = findViewById(R.id.pi_temp);
        final Button button_on = findViewById(R.id.button_on);
        final Button button_off = findViewById(R.id.button_off);

        mqtt = new MqttHelper();
        mqttCallback();

        // PWM
        mqtt.startSub(topicPWM);
        mqtt.startPub(topicPWM,"Android init");

        // temperature
        mqtt.startSub(topicTemp);

        button_on.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            mqtt.startPub(topicPWM, "Android on");
            }
        });
        button_off.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            mqtt.startPub(topicPWM, "Android off");
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
            public void messageArrived(final String topic, MqttMessage message) throws Exception {
                // subscribe后得到的訊息如下
                System.out.println("主题 : " + topic);
                System.out.println("Qos : " + message.getQos());
                System.out.println("内容 : " + new String(message.getPayload()));

                final String msg = new String(message.getPayload());


                runOnUiThread(new Runnable(){
                    public void run() {
                        // TODO: use switch
                        if(Objects.equals(topic, topicPWM)){
                            t_hi.setText(msg);
                        } else if (Objects.equals(topic, topicTemp)){
                            t_pi_temp.setText(msg);
                        }
                    }
                });

            }
        });
    }
}
