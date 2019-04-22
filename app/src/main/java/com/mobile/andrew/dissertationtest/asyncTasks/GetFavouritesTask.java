package com.mobile.andrew.dissertationtest.asyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.mobile.andrew.dissertationtest.App;
import com.mobile.andrew.dissertationtest.KanjiListAdapter;
import com.mobile.andrew.dissertationtest.R;
import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiData;

import java.lang.ref.WeakReference;
import java.util.List;

public class GetFavouritesTask extends AsyncTask<GetFavouritesParams, Void, List<KanjiData>>
{
    private KanjiListAdapter adapter;
    private WeakReference<TextView> tvNumResultsRef, tvNoResultsHintRef;

    @Override
    protected List<KanjiData> doInBackground(GetFavouritesParams... params) {
        adapter = params[0].adapter;
        tvNumResultsRef = params[0].tvNumResultsRef;
        tvNoResultsHintRef = params[0].tvNoResultsHintRef;

        AppDatabase db = AppDatabase.getInstance();
        return db.kanjiDao().getFavourites();
    }

    @Override
    protected void onPostExecute(List<KanjiData> searchResults) {
        adapter.updateData(searchResults, new float[] { -1f, -1f, -1f });

        TextView tvNumResults = tvNumResultsRef.get();
        tvNumResults.setText(App.getContext().getString(R.string.all_numresults,
                searchResults.size()));

        TextView tvNoResultsHint = tvNoResultsHintRef.get();
        if(searchResults.isEmpty()) {
            tvNoResultsHint.setVisibility(View.VISIBLE);
        } else {
            tvNoResultsHint.setVisibility(View.GONE);
        }
    }
}
