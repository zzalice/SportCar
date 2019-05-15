package com.sportcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView t_hi = findViewById(R.id.hi);
        final TextView t_pi_temp = findViewById(R.id.pi_temp);
        final Button button_on = findViewById(R.id.button_on);
        final Button button_off = findViewById(R.id.button_off);

        button_on.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                t_hi.setText("LED On");
            }
        });
        button_off.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                t_hi.setText("LED Off");
            }
        });
    }
}
