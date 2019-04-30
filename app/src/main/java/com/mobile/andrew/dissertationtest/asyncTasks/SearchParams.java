package com.mobile.andrew.dissertationtest.asyncTasks;

import android.widget.TextView;

import com.mobile.andrew.dissertationtest.KanjiListAdapter;

import java.lang.ref.WeakReference;

/**
 * Wrapper data class for parameters passed to {@link SearchTask}.
 */
public class SearchParams
{
    final float[] scores;
    final float tolerance;
    final KanjiListAdapter adapter;
    final WeakReference<TextView> tvNumResultsRef;
    final WeakReference<TextView> tvNoResultsHintRef;

    public SearchParams(float[] scores, float tolerance, KanjiListAdapter adapter,
                        TextView tvNumResults, TextView tvNoResultsHint) {
        this.scores = scores;
        this.tolerance = tolerance;
        this.adapter = adapter;
        tvNumResultsRef = new WeakReference<>(tvNumResults);
        tvNoResultsHintRef = new WeakReference<>(tvNoResultsHint);
    }
}
