package com.example.android.news;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int splash_time_out = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(splashIntent);
                finish();
            }
        }, splash_time_out);
    }
}
