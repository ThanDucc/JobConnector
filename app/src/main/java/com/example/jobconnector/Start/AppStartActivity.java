package com.example.jobconnector.Start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.jobconnector.R;

public class AppStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(new Intent(AppStartActivity.this, SplashActivity.class));
                finish();
            }
        }, 3000);
    }
}