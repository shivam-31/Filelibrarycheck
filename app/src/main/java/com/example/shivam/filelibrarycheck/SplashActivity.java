package com.example.shivam.filelibrarycheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View easySplashScreenView = new EasySplashScreen(this)
                .withFullScreen()
                .withTargetActivity(LoginActivity.class)
                .withSplashTimeOut(3000)
                .withLogo(R.drawable.tick_green)
                .withBackgroundResource(android.R.color.white)
                //.withHeaderText("Header")
              //  .withFooterText("Copyright 2016")
                //.withB foreLogoText("CC")
               // .withLogo(R.drawable.logo)
                //.withAfterLogoText("Attendance App")
                .create();

        setContentView(easySplashScreenView);
    }
}
