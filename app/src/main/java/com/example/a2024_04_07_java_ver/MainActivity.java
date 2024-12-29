package com.example.a2024_04_07_java_ver;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button scanQRBtn;
    private Button reportQRBtn;
    private Activity view;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String text = PreferenceManager.getString(this,"Count");
        if (text.equals("")) {
            PreferenceManager.setString(this, "Count", "0");
        }

        scanQRBtn = (Button) findViewById(R.id.scanQR);
        reportQRBtn = (Button) findViewById(R.id.reportQR);


        scanQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ScanQR.class);
                startActivity(intent);
            }
        });

        reportQRBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, Report_URL_Activity.class);
                startActivity(intent);
            }
        });


    }





}
