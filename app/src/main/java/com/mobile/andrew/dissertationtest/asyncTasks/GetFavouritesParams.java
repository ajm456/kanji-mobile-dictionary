package com.mobile.andrew.dissertationtest.asyncTasks;

import android.widget.TextView;

import com.mobile.andrew.dissertationtest.KanjiListAdapter;

import java.lang.ref.WeakReference;

/**
 * Wrapper data class for parameters passed to {@link GetFavouritesTask}.
 */
public class GetFavouritesParams
{
    final KanjiListAdapter adapter;
    final WeakReference<TextView> tvNoResultsHintRef;

    public GetFavouritesParams(KanjiListAdapter adapter, TextView tvNoResultsHint) {
        this.adapter = adapter;
        tvNoResultsHintRef = new WeakReference<>(tvNoResultsHint);
    }
}
