package com.mobile.andrew.dissertationtest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mobile.andrew.dissertationtest.db.DatabaseContract;
import com.mobile.andrew.dissertationtest.db.DatabaseHelper;
import com.mobile.andrew.dissertationtest.db.KanjiData;

import java.util.ArrayList;

class FavouritesActivity extends AppCompatActivity
{
    private static final String TAG = FavouritesActivity.class.getSimpleName();

    private RecyclerView rvFavourites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        initUi();

        // Get favourites list from the database
        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
        String[] projection = new String[] { DatabaseContract.COLUMN_NAME_CHARACTER,
                DatabaseContract.COLUMN_NAME_MEANINGS, DatabaseContract.COLUMN_NAME_KUN,
                DatabaseContract.COLUMN_NAME_ON, DatabaseContract.COLUMN_NAME_NUMSTROKES,
                DatabaseContract.COLUMN_NAME_JLPT };
        String selection = DatabaseContract.COLUMN_NAME_FAVOURITED + " = ?";
        String[] selectionArgs = new String[] { "1" };

        Cursor result = db.query(
                DatabaseContract.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        Log.d(TAG, "Found " + result.getCount() + " favourites!");
        ArrayList<KanjiData> favouritesData = new ArrayList<>(result.getCount());
        while(result.moveToNext()) {
            char kanji = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_CHARACTER)).charAt(0);
            String[] meanings = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_MEANINGS)).split(", ");
            String kunReadingsStr = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_KUN));
            String[] kunReadings;
            if(kunReadingsStr != null) {
                kunReadings = kunReadingsStr.split(",");
            } else {
                kunReadings = new String[] {};
            }
            String onReadingsStr = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_ON));
            String[] onReadings;
            if(onReadingsStr != null) {
                onReadings = onReadingsStr.split(",");
            } else {
                onReadings = new String[] {};
            }
            int numStrokes = result.getInt(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_NUMSTROKES));
            String jlptLevel = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_JLPT));
            favouritesData.add(new KanjiData(kanji, jlptLevel, meanings, kunReadings, onReadings, numStrokes, true));
        }
        result.close();

        rvFavourites.setAdapter(new FavouritesAdapter(favouritesData));
    }

    private void initUi() {
        rvFavourites = findViewById(R.id.recyclerview_favourites);
        rvFavourites.setHasFixedSize(true);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar_favourites);
        toolbar.setTitle(R.string.favourites_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }
}
