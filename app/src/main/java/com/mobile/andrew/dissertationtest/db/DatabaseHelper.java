package com.mobile.andrew.dissertationtest.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static String DB_NAME = "database.db";
    private static String DB_PATH = "";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;

        // Use dummy db to get database directory path
        if(DB_PATH.isEmpty()) {
            String dummyName = "dummy.db";
            DB_PATH = context.getDatabasePath(dummyName).getParent() + "/";
        }

        if(notInStorage()) {
            Log.d(TAG, "Database not in storage - transferring from assets!");
            copyDatabase();
        }
    }

    private boolean notInStorage() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return !dbFile.exists();
    }

    private void copyDatabase() {
        InputStream in;
        OutputStream out;

        try {
            in = context.getAssets().open(DB_NAME);
            String filename = DB_PATH + DB_NAME;
            out = new FileOutputStream(filename);

            byte[] buffer = new byte[1024];
            int length = 0;
            while((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
