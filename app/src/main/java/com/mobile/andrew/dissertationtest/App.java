package com.mobile.andrew.dissertationtest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.mobile.andrew.dissertationtest.room.AppDatabase;

/**
 * An {@link Application} extension class providing a global app context object. Since the
 * application context lasts for the entire lifecycle of the app, there is no danger of a memory
 * leak.
 */
public class App extends Application
{
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        // Copy database from assets to database folder
        AppDatabase.openDb(context);
    }

    public static Context getContext() {
        return App.context;
    }
}
