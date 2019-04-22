package com.mobile.andrew.dissertationtest.asyncTasks;

import android.os.AsyncTask;

import com.mobile.andrew.dissertationtest.room.AppDatabase;

public class UpdateFavouritedTask extends AsyncTask<UpdateFavouritedParams, Void, Void>
{
    @Override
    protected Void doInBackground(UpdateFavouritedParams... params) {
        String character = params[0].character;
        int favourited = params[0].favourited;
        AppDatabase db = AppDatabase.getInstance();
        db.kanjiDao().updateFavourited(character, favourited);

        return null;
    }
}
