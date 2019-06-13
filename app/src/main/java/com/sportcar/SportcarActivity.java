package com.sportcar;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;

public class SportcarActivity extends AppCompatActivity {
    private static String topicFrontWheel = "control/frontWheel";
    private static String topicBackWheel = "control/backWheel";
    private static String topicTemp = "info/temp";
    private static String topicBattery = "info/battery";

    private int[] speed = {65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,
                           83,
                           86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110};
    private int[] direction = {60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,
                               79,
                               80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95};
    private int straight = 19;

    MqttHelper mqtt;
    private SeekBar seekBar_speed, seekBar_direction;
    private TextView textView_temperature, textView_battery;
    private Button btn_left, btn_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportcar);

        // intent
        Intent intent = getIntent();
        String broker_ip = intent.getStringExtra("borker_ip");

        // web view
        WebView web_view = findViewById(R.id.web_view);
        WebSettings set = web_view.getSettings();
        set.setJavaScriptEnabled(true);
        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);
        web_view.loadUrl("http://"+broker_ip+":8080/?action=stream");

        // connecting mqtt broker
        mqtt = new MqttHelper(broker_ip);
        mqttCallback();



        // temperature
        mqtt.startSub(topicTemp);
        mqtt.startSub(topicBattery);

        // widget
        textView_temperature = findViewById(R.id.textView_temperature);
        textView_battery = findViewById(R.id.textView_battery);
        btn_left = findViewById(R.id.btn_bit_left);
        btn_left.setOnClickListener(onLeftClick);
        btn_right = findViewById(R.id.btn_bit_right);
        btn_right.setOnClickListener(onRightClick);
        seekBar_speed = findViewById(R.id.seekBar_speed);
        seekBar_speed.setOnSeekBarChangeListener(onSpeedSeekBarChange);
        seekBar_direction = findViewById(R.id.seekBar_direction);
        seekBar_direction.setOnSeekBarChangeListener(onDirectionSeekBarChange);
    }

    private SeekBar.OnSeekBarChangeListener onSpeedSeekBarChange
            = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            //停止拖曳時觸發事件
            seekBar.setProgress(16);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
            //開始拖曳時觸發事件
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            //拖曳途中觸發事件，回傳參數 progress 告知目前拖曳數值
            mqtt.startPub(topicBackWheel, ""+speed[progress]);
        }
    };

    private SeekBar.OnSeekBarChangeListener onDirectionSeekBarChange
            = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            //停止拖曳時觸發事件
            seekBar.setProgress(straight);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
            //開始拖曳時觸發事件
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            //拖曳途中觸發事件，回傳參數 progress 告知目前拖曳數值
            mqtt.startPub(topicFrontWheel, ""+direction[progress]);
        }
    };

    private Button.OnClickListener onLeftClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            straight++;
            seekBar_direction.setProgress(straight);
        }
    };

    private Button.OnClickListener onRightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            straight--;
            seekBar_direction.setProgress(straight);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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
                        if(Objects.equals(topic, topicBattery)){
                            textView_battery.setText(msg+"%");
                        } else if (Objects.equals(topic, topicTemp)){
                            textView_temperature.setText(msg+"℃");
                        }
                    }
                });

            }
        });
    }
}
