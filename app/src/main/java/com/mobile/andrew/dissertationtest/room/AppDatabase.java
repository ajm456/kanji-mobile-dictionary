package com.mobile.andrew.dissertationtest.room;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mobile.andrew.dissertationtest.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Database(entities = {KanjiData.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase
{
    public final static boolean PRESERVE_DICTIONARY = true;

    private static AppDatabase instance;

    public abstract KanjiDao kanjiDao();

    public static AppDatabase getInstance() {
        if(instance == null) {
            synchronized(AppDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(App.getContext(), AppDatabase.class, "database.db").allowMainThreadQueries().build();
                }
            }
        }

        return instance;
    }

    public static void openDb(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(!prefs.getBoolean("instantiated", false)) {
            InputStream in;
            OutputStream out;

            try {
                in = context.getAssets().open("database.db");
                File outFile = new File(context.getDatabasePath("dummy.db").getParent() + "/database.db");
                out = new FileOutputStream(outFile);

                byte[] buffer = new byte[1024];
                int read;
                while((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor prefsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            prefsEditor.putBoolean("instantiated", true);
            prefsEditor.apply();
        }
    }
}
