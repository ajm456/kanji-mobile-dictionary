package com.mobile.andrew.dissertationtest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class App extends Application
{
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getContext() {
        return App.context;
    }
}
