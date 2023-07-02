package com.example.quiz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }
}

