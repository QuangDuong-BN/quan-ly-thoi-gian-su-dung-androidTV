package com.example.appblockr;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //cài đặt chế độ ban đêm
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide(); //ẩn actionbar
        //khởi tạo logo khi chương trình đang chạy trong 2s
        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(900)
                .withBackgroundColor(Color.parseColor("#495867"))
                .withLogo(R.mipmap.ic_launcher_foreground)
                .withAfterLogoText("AppBlockr");
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreenView = config.create();
        setContentView(easySplashScreenView); //xem nội dung của hoạt động
    }
}