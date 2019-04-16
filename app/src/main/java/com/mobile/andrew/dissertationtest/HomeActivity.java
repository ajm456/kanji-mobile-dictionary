package com.mobile.andrew.dissertationtest;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobile.andrew.dissertationtest.db.KanjiData;
import com.mobile.andrew.dissertationtest.db.KanjiDictionary;

import java.util.ArrayList;

//TODO: Remove items from favourites list (maybe slide to side?)
public class HomeActivity extends AppCompatActivity
{
    public final static boolean PRESERVE_DICTIONARY = true;

    private SeekBar.OnSeekBarChangeListener sliderListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            rvResults.smoothScrollToPosition(0);
            KanjiDictionary dict = KanjiDictionary.getInstance();
            ArrayList<KanjiData> searchResults = dict.queryDict(getSliderScores(), getTolerance());
            resultsAdapter.updateData(searchResults, getSliderScores());
            tvNumResults.setText(getString(R.string.sliderinput_numresults, searchResults.size()));
            if(searchResults.isEmpty()) {
                tvNoResultsHint.setVisibility(View.VISIBLE);
            } else {
                tvNoResultsHint.setVisibility(View.GONE);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final float MAX_SEARCH_TOLERANCE = 0.4f;
    private static final float MIN_SEARCH_TOLERANCE = 0.1f;

    private SeekBar sbComplexity, sbSymmetricity, sbDiagonality, sbTolerance;
    private CheckBox cbComplexity, cbSymmetricity, cbDiagonality;
    private RecyclerView rvResults;
    private TextView tvComplexityMin, tvComplexityMax, tvSymmMin, tvSymmMax, tvNumResults,
            tvNoResultsHint;
    private KanjiResultsAdapter resultsAdapter;

    private boolean fullscreenedResults = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliderinput);

        // Setup UI
        initUi();

        // Perform initial kanji load from CSV into memory
        KanjiDictionary.loadDatabaseIntoDict(this);

        // Populate kanji list with results for initial slider values
        KanjiDictionary dict = KanjiDictionary.getInstance();
        ArrayList<KanjiData> searchResults = dict.queryDict(getSliderScores(), getTolerance());
        resultsAdapter = new KanjiResultsAdapter(searchResults, getSliderScores(), this);
        rvResults.setAdapter(resultsAdapter);
        tvNumResults.setText(getString(R.string.sliderinput_numresults, searchResults.size()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        KanjiDictionary dict = KanjiDictionary.getInstance();
        resultsAdapter.updateData(dict.queryDict(getSliderScores(), getTolerance()), getSliderScores());
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
        sbComplexity = findViewById(R.id.seekbar_sliderinput_complexity);
        sbSymmetricity = findViewById(R.id.seekbar_sliderinput_symm);
        sbDiagonality = findViewById(R.id.seekbar_sliderinput_strokecurv);
        cbComplexity = findViewById(R.id.checkbox_sliderinput_complexity);
        cbSymmetricity = findViewById(R.id.checkbox_sliderinput_symm);
        cbDiagonality = findViewById(R.id.checkbox_sliderinput_strokecurv);
        tvComplexityMin = findViewById(R.id.text_sliderinput_complexityminval);
        tvComplexityMax = findViewById(R.id.text_sliderinput_complexitymaxval);
        tvSymmMin = findViewById(R.id.text_sliderinput_symmminval);
        tvSymmMax = findViewById(R.id.text_sliderinput_symmmaxval);
        tvNumResults = findViewById(R.id.text_sliderinput_numresults);
        tvNoResultsHint = findViewById(R.id.text_sliderinput_noresultshint);
        sbTolerance = findViewById(R.id.seekbar_sliderinput_searchtolerance);

        Toolbar toolbar = findViewById(R.id.toolbar_sliderinput);
        toolbar.setTitle(R.string.sliderinput_toolbartitle);
        setSupportActionBar(toolbar);

        // Set event listeners
        sbComplexity.setOnSeekBarChangeListener(sliderListener);
        sbSymmetricity.setOnSeekBarChangeListener(sliderListener);
        sbDiagonality.setOnSeekBarChangeListener(sliderListener);
        sbTolerance.setOnSeekBarChangeListener(sliderListener);
        final Button btnResultsSize = findViewById(R.id.button_sliderinput_resultssize);
        final ConstraintLayout layout = findViewById(R.id.constraintlayout_sliderinput_base);
        btnResultsSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvResults.stopScroll();

                // Fade in/out elements that will be behind the full screened results view
                if(fullscreenedResults) {
                    AlphaAnimation fadeOut = new AlphaAnimation(0f, 1f);
                    fadeOut.setDuration(200);
                    fadeOut.setFillAfter(true);
                    sbComplexity.startAnimation(fadeOut);
                    tvComplexityMin.startAnimation(fadeOut);
                    tvComplexityMax.startAnimation(fadeOut);
                    cbComplexity.startAnimation(fadeOut);
                    sbSymmetricity.startAnimation(fadeOut);
                    tvSymmMin.startAnimation(fadeOut);
                    tvSymmMax.startAnimation(fadeOut);
                    cbSymmetricity.startAnimation(fadeOut);
                } else {
                    AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                    fadeOut.setDuration(200);
                    fadeOut.setFillAfter(true);
                    sbComplexity.startAnimation(fadeOut);
                    tvComplexityMin.startAnimation(fadeOut);
                    tvComplexityMax.startAnimation(fadeOut);
                    cbComplexity.startAnimation(fadeOut);
                    sbSymmetricity.startAnimation(fadeOut);
                    tvSymmMin.startAnimation(fadeOut);
                    tvSymmMax.startAnimation(fadeOut);
                    cbSymmetricity.startAnimation(fadeOut);
                }

                ConstraintSet constraints = new ConstraintSet();
                constraints.clone(layout);
                TransitionManager.beginDelayedTransition(layout);
                if(fullscreenedResults) {
                    constraints.connect(R.id.button_sliderinput_resultssize, ConstraintSet.TOP, R.id.guideline_sliderinput_results, ConstraintSet.BOTTOM);
                } else {
                    constraints.connect(R.id.button_sliderinput_resultssize, ConstraintSet.TOP, R.id.toolbar_sliderinput, ConstraintSet.BOTTOM);
                }
                fullscreenedResults = !fullscreenedResults;
                constraints.applyTo(layout);
                btnResultsSize.setText(fullscreenedResults ? getString(R.string.sliderinput_minimisearrow) : getString(R.string.sliderinput_fullscreenarrow));
            }
        });

        // Configure RecyclerView's UI properties
        rvResults = findViewById(R.id.recycleview_sliderinput_results);
        rvResults.setHasFixedSize(false);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private Float[] getSliderScores() {
        float complexityScore = cbComplexity.isChecked() ? sbComplexity.getProgress() / 10.0f : -1f;
        float symmetricityScore = cbSymmetricity.isChecked() ? sbSymmetricity.getProgress() / 10.0f : -1f;
        float strokeCurvinessScore = cbDiagonality.isChecked() ? sbDiagonality.getProgress() / 10.0f : -1f;

        return new Float[] { complexityScore, symmetricityScore, strokeCurvinessScore };
    }

    private float getTolerance() {
        return MIN_SEARCH_TOLERANCE + (float)sbTolerance.getProgress() / sbTolerance.getMax() * (MAX_SEARCH_TOLERANCE - MIN_SEARCH_TOLERANCE);
    }
}
