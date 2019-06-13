package com.sportcar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText eText_mqtt_broker_ip = findViewById(R.id.eText_mqtt_broker_ip);
        Button btn_connect_mqtt_broker = findViewById(R.id.btn_connect_mqtt_broker);

        btn_connect_mqtt_broker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String borker_ip = eText_mqtt_broker_ip.getText().toString();

                Intent intent = new Intent(MainActivity.this, SportcarActivity.class);
                intent.putExtra("borker_ip", borker_ip);
                startActivity( intent );
            }
        });

    }

}
