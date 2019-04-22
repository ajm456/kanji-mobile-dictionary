package com.mobile.andrew.dissertationtest.asyncTasks;

import android.widget.TextView;

import com.mobile.andrew.dissertationtest.KanjiListAdapter;

import java.lang.ref.WeakReference;

public class SearchParams
{
    float[] scores;
    float tolerance;
    KanjiListAdapter adapter;
    WeakReference<TextView> tvNumResultsRef, tvNoResultsHintRef;

    public SearchParams(float[] scores, float tolerance, KanjiListAdapter adapter,
                        TextView tvNumResults, TextView tvNoResultsHint) {
        this.scores = scores;
        this.tolerance = tolerance;
        this.adapter = adapter;
        tvNumResultsRef = new WeakReference<>(tvNumResults);
        tvNoResultsHintRef = new WeakReference<>(tvNoResultsHint);
    }
}
