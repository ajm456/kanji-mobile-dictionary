package com.mobile.andrew.dissertationtest;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.andrew.dissertationtest.asyncTasks.GetFavouritesParams;
import com.mobile.andrew.dissertationtest.asyncTasks.GetFavouritesTask;

import java.util.Objects;

/**
 * Activity for displaying a list of all currently favourited kanji characters.
 */
public class FavouritesActivity extends AppCompatActivity
{
    private TextView tvNoResultsHint;
    private KanjiListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        initUi();

        // Get favourites list from the database
        GetFavouritesParams params = new GetFavouritesParams(adapter, tvNoResultsHint);
        new GetFavouritesTask().execute(params);
    }

    /**
     * Initialises and sets up the UI elements in this activity's layout.
     */
    private void initUi() {
        RecyclerView rvFavourites = findViewById(R.id.recyclerview_favourites);
        rvFavourites.setHasFixedSize(true);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KanjiListAdapter(this);
        rvFavourites.setAdapter(adapter);
        // Add a callback helper for deleting list items by swiping
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(rvFavourites);
        tvNoResultsHint = findViewById(R.id.text_favourites_noresultshint);

        Toolbar toolbar = findViewById(R.id.toolbar_favourites);
        toolbar.setTitle(R.string.favourites_title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Manually checks the data adapter and updates the visibility of the no results hint
     * accordingly.
     */
    public void checkHint() {
        if(adapter.getItemCount() == 0) {
            tvNoResultsHint.setVisibility(View.VISIBLE);
        } else {
            tvNoResultsHint.setVisibility(View.GONE);
        }
    }
}
