package com.mobile.andrew.dissertationtest;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mobile.andrew.dissertationtest.asyncTasks.GetFavouritesParams;
import com.mobile.andrew.dissertationtest.asyncTasks.GetFavouritesTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouritesActivity extends AppCompatActivity
{
    private RecyclerView rvFavourites;
    private TextView tvNumResults, tvNoResultsHint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        initUi();

        KanjiListAdapter adapter = new KanjiListAdapter(this);
        rvFavourites.setAdapter(adapter);

        // Get favourites list from the database
        GetFavouritesParams params = new GetFavouritesParams(adapter,
                tvNumResults, tvNoResultsHint);
        new GetFavouritesTask().execute(params);
    }

    private void initUi() {
        rvFavourites = findViewById(R.id.recyclerview_favourites);
        rvFavourites.setHasFixedSize(true);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        tvNumResults = findViewById(R.id.text_favourites_numresults);
        tvNoResultsHint = findViewById(R.id.text_favourites_noresultshint);

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
