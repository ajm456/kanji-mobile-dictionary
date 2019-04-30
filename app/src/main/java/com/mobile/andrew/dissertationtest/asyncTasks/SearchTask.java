package com.mobile.andrew.dissertationtest.asyncTasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.mobile.andrew.dissertationtest.App;
import com.mobile.andrew.dissertationtest.KanjiListAdapter;
import com.mobile.andrew.dissertationtest.R;
import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@link AsyncTask} extension which asynchronously performs a score-based kanji search on the
 * database.
 */
public class SearchTask extends AsyncTask<SearchParams, Void, List<KanjiData>>
{
    private float[] searchScores;
    private WeakReference<TextView> tvNumResultsRef, tvNoResultsHintRef;
    private KanjiListAdapter adapter;

    /**
     * Uses a given {@link SearchParams} object to perform a score-based kanji search on the
     * database. Uses a Euclidean filter to return characters that are within a given tolerance of
     * a given score, sorted by distance ascending.
     *
     * @param params    A {@link SearchParams} object containing the data needed to perform a
     *                  search.
     * @return          A list of {@link KanjiData} objects that are valid search results.
     */
    @Override
    protected List<KanjiData> doInBackground(SearchParams... params) {
        searchScores = params[0].scores;
        float tolerance = params[0].tolerance;
        adapter = params[0].adapter;
        tvNumResultsRef = params[0].tvNumResultsRef;
        tvNoResultsHintRef = params[0].tvNoResultsHintRef;

        AppDatabase db = AppDatabase.getInstance();

        // Get all kanji data rows
        List<KanjiData> data = db.kanjiDao().getAll();
        // Filter and return the data
        return euclideanFilter(data, searchScores, tolerance);
    }

    /**
     * Updates UI elements according to the results of the search.
     *
     * @param searchResults The results of the search.
     */
    @Override
    protected void onPostExecute(List<KanjiData> searchResults) {
        adapter.updateData(searchResults, searchScores);

        TextView tvNumResults = tvNumResultsRef.get();
        tvNumResults.setText(App.getContext().getString(R.string.home_numresults,
                searchResults.size()));

        TextView tvNoResultsHint = tvNoResultsHintRef.get();
        if(searchResults.isEmpty()) {
            tvNoResultsHint.setVisibility(View.VISIBLE);
        } else {
            tvNoResultsHint.setVisibility(View.GONE);
        }
    }

    /**
     * Filters a list of {@link KanjiData} objects, removing any that are further away from the
     * given score vector than the given tolerance.
     *
     * @param data          The list of {@link KanjiData} objects being filtered.
     * @param searchScores  The score vector searched for.
     * @param tolerance     The maximum distance from the given score vector any returned result
     *                      can be.
     * @return              A list of filtered {@link KanjiData} objects; the results of the search.
     */
    static List<KanjiData> euclideanFilter(List<KanjiData> data, float[] searchScores,
                                           float tolerance) {
        // Create list for holding indexes and their distances (used to sort)
        List<Pair<Integer, Double>> validIndexes = new ArrayList<>(data.size());
        // Remove items where the squared Euclidean distance is greater than the squared tolerance
        for(int i = 0; i < data.size(); i++) {
            float[] kanjiScores = new float[] { data.get(i).complexity, data.get(i).symmetricity,
                    data.get(i).diagonality };
            // Calculate the squared Euclidean distance
            double distSquared = Math.pow(searchScores[0] - kanjiScores[0], 2)
                    + Math.pow(searchScores[1] - kanjiScores[1], 2)
                    + Math.pow(searchScores[2] - kanjiScores[2], 2);
            // Remove if further away - account for floating point precision errors
            if(Math.pow(tolerance, 2) - distSquared > -0.001) {
                validIndexes.add(new Pair<>(i, distSquared));
            }
        }
        // Sort indexes by distance
        validIndexes.sort( (p1, p2) -> Objects.requireNonNull(p1.second).compareTo(Objects.requireNonNull(p2.second)));

        // Build the list of values to return
        List<KanjiData> validData = new ArrayList<>(validIndexes.size());
        for(Pair p : validIndexes) {
            validData.add(data.get((Integer)(p.first)));
        }

        // Return the filtered data
        return validData;
    }
}
