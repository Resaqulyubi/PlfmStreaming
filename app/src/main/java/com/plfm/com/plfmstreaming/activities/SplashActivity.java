package com.plfm.com.plfmstreaming.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.plfm.com.plfmstreaming.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check jika session login buka main activity otherwish activity login

//                if (Const.isLogin()){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                } else {
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                }

                finish();
            }
        }, 3000);
    }
}
