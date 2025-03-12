package com.example.japonnews;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        instance = this;
    }
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}