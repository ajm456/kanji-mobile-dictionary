package com.mobile.andrew.dissertationtest.asyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.mobile.andrew.dissertationtest.KanjiListAdapter;
import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiData;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * {@link AsyncTask} extension which asynchronously fetches a list of all favourited kanji
 * characters currently in the database and updates UI elements accordingly.
 */
public class GetFavouritesTask extends AsyncTask<GetFavouritesParams, Void, List<KanjiData>>
{
    private KanjiListAdapter adapter;
    private WeakReference<TextView> tvNoResultsHintRef;

    /**
     * Uses a given {@link GetFavouritesParams} object to fetch a list of all favourited kanji
     * currently in the database.
     *
     * @param params    A {@link GetFavouritesParams} object containing the data required to
     *                  perform this task.
     * @return          A list of {@link KanjiData} containing data about every favourited kanji in
     *                  the database.
     */
    @Override
    protected List<KanjiData> doInBackground(GetFavouritesParams... params) {
        adapter = params[0].adapter;
        tvNoResultsHintRef = params[0].tvNoResultsHintRef;

        AppDatabase db = AppDatabase.getInstance();
        return db.kanjiDao().getFavourites();
    }

    /**
     * Updates the given Adapter with the fetched data and adjusts the visibility of a given no
     * results hint.
     *
     * @param searchResults A list of {@link KanjiData} objects resulting from the fetch.
     */
    @Override
    protected void onPostExecute(List<KanjiData> searchResults) {
        adapter.updateData(searchResults);

        TextView tvNoResultsHint = tvNoResultsHintRef.get();
        if(searchResults.isEmpty()) {
            tvNoResultsHint.setVisibility(View.VISIBLE);
        } else {
            tvNoResultsHint.setVisibility(View.GONE);
        }
    }
}
