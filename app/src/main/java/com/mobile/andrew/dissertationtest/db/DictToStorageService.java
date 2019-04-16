package com.mobile.andrew.dissertationtest.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DictToStorageService extends IntentService
{
    private final static String TAG = DictToStorageService.class.getSimpleName();

    public DictToStorageService() {
        super(DictToStorageService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Starting background service to save dictionary to internal storage");
                HashMap<ScoreKey, ArrayList<Character>> dict = (HashMap) intent.getSerializableExtra("dict");
                SQLiteDatabase db = new DatabaseHelper(DictToStorageService.this).getWritableDatabase();

                String selection = DatabaseContract.COLUMN_NAME_CHARACTER + " = ?";
                for(ScoreKey key : dict.keySet()) {
                    ArrayList<Character> kanjis = dict.get(key);
                    for(Character kanji : kanjis) {
                        ContentValues values = new ContentValues();
                        String[] selectionArgs = { kanji.toString() };
                        values.put(DatabaseContract.COLUMN_NAME_COMPLEXITY, key.get(0));
                        values.put(DatabaseContract.COLUMN_NAME_SYMM, key.get(1));
                        values.put(DatabaseContract.COLUMN_NAME_DIAG, key.get(2));

                        db.update(DatabaseContract.TABLE_NAME, values, selection, selectionArgs);
                    }

                }
            }
        }).start();
    }
}
