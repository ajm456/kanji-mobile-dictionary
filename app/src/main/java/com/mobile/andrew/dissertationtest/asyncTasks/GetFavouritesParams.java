package com.mobile.andrew.dissertationtest.asyncTasks;

import android.widget.TextView;

import com.mobile.andrew.dissertationtest.KanjiListAdapter;

import java.lang.ref.WeakReference;

public class GetFavouritesParams
{
    KanjiListAdapter adapter;
    WeakReference<TextView> tvNumResultsRef, tvNoResultsHintRef;

    public GetFavouritesParams(KanjiListAdapter adapter, TextView tvNumResults,
                               TextView tvNoResultsHint) {
        this.adapter = adapter;
        tvNumResultsRef = new WeakReference<>(tvNumResults);
        tvNoResultsHintRef = new WeakReference<>(tvNoResultsHint);
    }
}
