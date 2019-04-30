package com.mobile.andrew.dissertationtest.asyncTasks;

import android.os.AsyncTask;

import com.mobile.andrew.dissertationtest.room.AppDatabase;

/**
 * {@link AsyncTask} extension which asynchronously updates the favourited value of a character in
 * the database.
 */
public class UpdateFavouritedTask extends AsyncTask<UpdateFavouritedParams, Void, Void>
{
    @Override
    protected Void doInBackground(UpdateFavouritedParams... params) {
        String character = params[0].character;
        int favourited = params[0].favourited;
        AppDatabase db = AppDatabase.getInstance();
        // If the favourited value is -1, then the value of favourited was not passed by the calling
        // method - we must read the character's favourited value from the database before updating
        if(favourited == -1) {
            favourited = db.kanjiDao().isFavourited(character);
        }
        db.kanjiDao().updateFavourited(character, favourited);

        return null;
    }
}
