package com.grupp32.freeelo;

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
        final Button btn = (Button) findViewById(R.id.okButton);
        final TextView txt = (TextView) findViewById(R.id.summonerName);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }
}
