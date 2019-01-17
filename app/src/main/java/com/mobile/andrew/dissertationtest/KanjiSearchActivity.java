package com.mobile.andrew.dissertationtest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobile.andrew.dissertationtest.database.DatabaseHelper;

import java.util.ArrayList;


public class KanjiSearchActivity extends AppCompatActivity
{
    private static final String TAG = "KanjiSearchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_search);

        // Grab the search phrase put in this activity's intent
        Intent intent = getIntent();
        ArrayList<String> query = intent.getStringArrayListExtra("phrases");

        // Search with the phrase provided
        performSearch(query);
    }

    private void performSearch(ArrayList<String> query) {
        float[] queryScore = phrasesToScore(query);
    }

    private float[] phrasesToScore(ArrayList<String> phrases) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "Kanji",
                new String[] { "Character" },
                null,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            String character = cursor.getString(cursor.getColumnIndexOrThrow("Character"));
            Log.d(TAG, "I found the following character: " + character);
        }
        cursor.close();

        return null;
    }
}
