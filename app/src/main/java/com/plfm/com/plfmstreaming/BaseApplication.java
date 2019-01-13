package com.plfm.com.plfmstreaming;

import android.app.Application;
import android.content.SharedPreferences;


public class BaseApplication extends Application {
    public static SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);


    }
}
