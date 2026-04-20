package com.example.booksummary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
            boolean check = sharedPreferences.getBoolean("flag",false);
            Intent intent;
            if (check){
                
                intent = new Intent(SplashScreen.this, HomePage.class);

            } else{
                intent = new Intent(SplashScreen.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        },3000);
    }
}