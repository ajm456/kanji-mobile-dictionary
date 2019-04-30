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

/**
 * This activity acts as a home screen for the app. It is where users can perform searches for
 * kanji characters, and it provides a menu for navigation to {@link CalibrationActivity} and
 * {@link FavouritesActivity}.
 */
public class HomeActivity extends AppCompatActivity
{
    /**
     * {@link android.widget.SeekBar.OnSeekBarChangeListener} implementation for handling when the
     * sliders are moved.
     */
    private final SeekBar.OnSeekBarChangeListener sliderListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            // When a slider is adjusted, trigger a kanji search by executing a SearchTask
            rvResults.smoothScrollToPosition(0);
            SearchParams params = new SearchParams(getSliderScores(),
                    percentageToEuclidean(MIN_SEARCH_TOLERANCE_PERCENT, MAX_SEARCH_TOLERANCE_PERCENT,
                            sbTolerance.getProgress(), sbTolerance.getMax()), resultsAdapter, tvNumResults, tvNoResultsHint);
            new SearchTask().execute(params);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    /**
     * The maximum percentage (0-1, inclusive) of the maximum potential tolerance (sqrt(3)) that the
     * tolerance slider allows users to set. For instance, if this value was 0.5, then a tolerance
     * slider at maximum progress would give searches a tolerance of 0.5 * sqrt(3).
     */
    private static final float MAX_SEARCH_TOLERANCE_PERCENT = 0.5f;
    /**
     * The minimum percentage (0-1, inclusive) of the maximum potential tolerance (sqrt(3)) that the
     * tolerance slider allows users to set. For instance, if this value was 0.5, then a tolerance
     * slider at minimum progress would give searches a tolerance of 0.5 * sqrt(3).
     */
    private static final float MIN_SEARCH_TOLERANCE_PERCENT = 0.1f;

    private SeekBar sbComplexity, sbSymmetricity, sbDiagonality, sbTolerance;
    private RecyclerView rvResults;
    private TextView tvComplexityMin, tvComplexityMax, tvSymmMin, tvSymmMax, tvNumResults,
            tvNoResultsHint;
    private KanjiListAdapter resultsAdapter;

    /**
     * Flag for whether or not the results list has been maximised.
     */
    private boolean fullscreenedResults = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUi();

        // Populate kanji list with results for initial slider values
        SearchParams params = new SearchParams(getSliderScores(),
                percentageToEuclidean(MIN_SEARCH_TOLERANCE_PERCENT, MAX_SEARCH_TOLERANCE_PERCENT,
                        sbTolerance.getProgress(), sbTolerance.getMax()), resultsAdapter, tvNumResults, tvNoResultsHint);
        new SearchTask().execute(params);
    }

    @Override
    protected void onResume() {
        // On app resume, trigger a search with the slider values to ensure the list is kept
        // consistent
        super.onResume();
        SearchParams params = new SearchParams(getSliderScores(),
                percentageToEuclidean(MIN_SEARCH_TOLERANCE_PERCENT, MAX_SEARCH_TOLERANCE_PERCENT,
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
            // Navigate to the favourites activity
            Intent intent = new Intent(this, FavouritesActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        } else if(item.getItemId() == R.id.menuitem_calibrate) {
            // Navigate to the calibration activity
            Intent intent = new Intent(this, CalibrationActivity.class);
            // Indicates to the calibration activity that it was launched from the home activity,
            // and not from being the app's first launch
            intent.putExtra("from_home", true);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialises and sets up the UI elements in this activity's layout.
     */
    private void initUi() {
        // Initialise UI element objects
        sbComplexity = findViewById(R.id.seekbar_home_complexity);
        sbSymmetricity = findViewById(R.id.seekbar_home_symm);
        sbDiagonality = findViewById(R.id.seekbar_home_diagonality);
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
        final ConstraintLayout layout = findViewById(R.id.root_home);
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

            // Adjust the constraints so that the results list is connected to the correct view
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
        resultsAdapter = new KanjiListAdapter(this);
        rvResults.setAdapter(resultsAdapter);
    }

    /**
     * Converts the current slider progress values into a single float array.
     *
     * @return  A float array of the current slider scores.
     */
    private float[] getSliderScores() {
        float complexityScore = sbComplexity.getProgress() / 10.0f;
        float symmetricityScore = sbSymmetricity.getProgress() / 10.0f;
        float strokeCurvinessScore = sbDiagonality.getProgress() / 10.0f;

        return new float[] { complexityScore, symmetricityScore, strokeCurvinessScore };
    }

    /**
     * Converts a progress value with the given progress maximum into a percentage between the given
     * minimum and maximum values of the maximum total tolerance.
     *
     * The maximum distance (and thus tolerance) possible in this app is sqrt(3), and the percentMin
     * and percentMax arguments specify the range of allowed percentages - this enables the seekbar
     * to have a constant progress max and interval. For example, a progress of 5 with a progress
     * max of 10, and a percent min of 0 and max of 0.5, means that 0.25 of the maximum tolerance
     * will be returned: sqrt(3)/4.
     *
     * @param percentMin    The percentage of the maximum possible tolerance a progress of 0
     *                      indicates.
     * @param percentMax    The percentage of the maximum possible tolerance a progress equal to the
     *                      progress maximum indicates.
     * @param progress      The seekbar's progress value.
     * @param progressMax   The maximum seekbar progress value.
     * @return              A tolerance value to be used in Euclidean filtering.
     */
    static float percentageToEuclidean(float percentMin, float percentMax, int progress, int progressMax) {
        float percentOfEuclideanMax = percentMin + (float)progress * ((percentMax - percentMin) / (float)progressMax);
        return percentOfEuclideanMax * 1.732051f; // approx. sqrt(3)
    }
}
