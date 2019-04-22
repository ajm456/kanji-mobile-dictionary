package com.mobile.andrew.dissertationtest;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.andrew.dissertationtest.asyncTasks.SearchParams;
import com.mobile.andrew.dissertationtest.asyncTasks.SearchTask;

//TODO: Remove items from favourites list (maybe slide to side?)
public class HomeActivity extends AppCompatActivity
{

    private SeekBar.OnSeekBarChangeListener sliderListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            rvResults.smoothScrollToPosition(0);
            SearchParams params = new SearchParams(getSliderScores(),
                    MathUtils.percentageToEuclidean(MIN_SEARCH_TOLERANCE_PERCENT, MAX_SEARCH_TOLERANCE_PERCENT,
                            sbTolerance.getProgress(), sbTolerance.getMax()), resultsAdapter, tvNumResults, tvNoResultsHint);
            new SearchTask().execute(params);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final float MAX_SEARCH_TOLERANCE_PERCENT = 0.4f;
    private static final float MIN_SEARCH_TOLERANCE_PERCENT = 0.1f;

    private SeekBar sbComplexity, sbSymmetricity, sbDiagonality, sbTolerance;
    private RecyclerView rvResults;
    private TextView tvComplexityMin, tvComplexityMax, tvSymmMin, tvSymmMax, tvNumResults,
            tvNoResultsHint;
    private KanjiListAdapter resultsAdapter;

    private boolean fullscreenedResults = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setup UI
        initUi();

        // Populate kanji list with results for initial slider values
        resultsAdapter = new KanjiListAdapter(this);
        rvResults.setAdapter(resultsAdapter);
        SearchParams params = new SearchParams(getSliderScores(),
                MathUtils.percentageToEuclidean(MIN_SEARCH_TOLERANCE_PERCENT, MAX_SEARCH_TOLERANCE_PERCENT,
                        sbTolerance.getProgress(), sbTolerance.getMax()), resultsAdapter, tvNumResults, tvNoResultsHint);
        new SearchTask().execute(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SearchParams params = new SearchParams(getSliderScores(),
                MathUtils.percentageToEuclidean(MIN_SEARCH_TOLERANCE_PERCENT, MAX_SEARCH_TOLERANCE_PERCENT,
                        sbTolerance.getProgress(), sbTolerance.getMax()), resultsAdapter, tvNumResults, tvNoResultsHint);
        new SearchTask().execute(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuitem_favourites) {
            Intent intent = new Intent(this, FavouritesActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        } else if(item.getItemId() == R.id.menuitem_calibrate) {
            Intent intent = new Intent(this, CalibrationActivity.class);
            intent.putExtra("from_home", true);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialises and configures UI element objects displayed in this activity.
     */
    private void initUi() {
        // Initialise UI element objects
        sbComplexity = findViewById(R.id.seekbar_home_complexity);
        sbSymmetricity = findViewById(R.id.seekbar_home_symm);
        sbDiagonality = findViewById(R.id.seekbar_home_strokecurv);
        tvComplexityMin = findViewById(R.id.text_home_complexityminval);
        tvComplexityMax = findViewById(R.id.text_home_complexitymaxval);
        tvSymmMin = findViewById(R.id.text_home_symmminval);
        tvSymmMax = findViewById(R.id.text_home_symmmaxval);
        tvNumResults = findViewById(R.id.text_home_numresults);
        tvNoResultsHint = findViewById(R.id.text_home_noresultshint);
        sbTolerance = findViewById(R.id.seekbar_home_searchtolerance);

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle(R.string.home_toolbartitle);
        setSupportActionBar(toolbar);

        // Set event listeners
        sbComplexity.setOnSeekBarChangeListener(sliderListener);
        sbSymmetricity.setOnSeekBarChangeListener(sliderListener);
        sbDiagonality.setOnSeekBarChangeListener(sliderListener);
        sbTolerance.setOnSeekBarChangeListener(sliderListener);
        final Button btnResultsSize = findViewById(R.id.button_home_resultssize);
        final ConstraintLayout layout = findViewById(R.id.constraintlayout_home_base);
        btnResultsSize.setOnClickListener(v -> {
            rvResults.stopScroll();

            // Fade in/out elements that will be behind the full screened results view
            if(fullscreenedResults) {
                AlphaAnimation fadeOut = new AlphaAnimation(0f, 1f);
                fadeOut.setDuration(200);
                fadeOut.setFillAfter(true);
                sbComplexity.startAnimation(fadeOut);
                tvComplexityMin.startAnimation(fadeOut);
                tvComplexityMax.startAnimation(fadeOut);
                sbSymmetricity.startAnimation(fadeOut);
                tvSymmMin.startAnimation(fadeOut);
                tvSymmMax.startAnimation(fadeOut);
            } else {
                AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                fadeOut.setDuration(200);
                fadeOut.setFillAfter(true);
                sbComplexity.startAnimation(fadeOut);
                tvComplexityMin.startAnimation(fadeOut);
                tvComplexityMax.startAnimation(fadeOut);
                sbSymmetricity.startAnimation(fadeOut);
                tvSymmMin.startAnimation(fadeOut);
                tvSymmMax.startAnimation(fadeOut);
            }

            ConstraintSet constraints = new ConstraintSet();
            constraints.clone(layout);
            TransitionManager.beginDelayedTransition(layout);
            if(fullscreenedResults) {
                constraints.connect(R.id.button_home_resultssize, ConstraintSet.TOP, R.id.guideline_home_results, ConstraintSet.BOTTOM);
            } else {
                constraints.connect(R.id.button_home_resultssize, ConstraintSet.TOP, R.id.toolbar_home, ConstraintSet.BOTTOM);
            }
            fullscreenedResults = !fullscreenedResults;
            constraints.applyTo(layout);
            btnResultsSize.setText(fullscreenedResults ? getString(R.string.home_minimisearrow) : getString(R.string.home_fullscreenarrow));
        });

        // Configure RecyclerView's UI properties
        rvResults = findViewById(R.id.recycleview_home_results);
        rvResults.setHasFixedSize(false);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private float[] getSliderScores() {
        float complexityScore = sbComplexity.getProgress() / 10.0f;
        float symmetricityScore = sbSymmetricity.getProgress() / 10.0f;
        float strokeCurvinessScore = sbDiagonality.getProgress() / 10.0f;

        return new float[] { complexityScore, symmetricityScore, strokeCurvinessScore };
    }
}
