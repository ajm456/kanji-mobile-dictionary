package com.mobile.andrew.dissertationtest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobile.andrew.dissertationtest.database.DatabaseHelper;

import java.util.ArrayList;

public class SliderInputActivity extends AppCompatActivity
{
    private static final String TAG = SliderInputActivity.class.getSimpleName();

    //TODO: Checkboxes to enable/disable sliders
    private static final byte COMPLEX_ENABLED = 1;
    private static final byte CURVI_ENABLED = 2;
    private static final byte SYMM_ENABLED = 4;

    private static final float INIT_SEARCH_TOLERANCE = 0.3f;

    private SeekBar sbComplexity, sbCurviness, sbSymmetricity;
    private CheckBox cbComplexity, cbCurvienss, cbSymmetricity;
    //TODO: Display thumb value on SeekBars

    private float searchTolerance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_input);

        searchTolerance = INIT_SEARCH_TOLERANCE;

        sbComplexity = findViewById(R.id.seekbar_sliderinput_complexity);
        sbCurviness = findViewById(R.id.seekbar_sliderinput_curviness);
        sbSymmetricity = findViewById(R.id.seekbar_sliderinput_symmetricity);
        Button btnSubmit = findViewById(R.id.button_sliderinput_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Convert sliders into scores and perform DB query
                sbComplexity = findViewById(R.id.seekbar_sliderinput_complexity);
                sbCurviness = findViewById(R.id.seekbar_sliderinput_curviness);
                sbSymmetricity = findViewById(R.id.seekbar_sliderinput_symmetricity);

                float complexityScore = sbComplexity.getProgress() / 10.0f;
                float curvinessScore = sbCurviness.getProgress() / 10.0f;
                float symmetricityScore = sbSymmetricity.getProgress() / 10.0f;

                DatabaseHelper db = DatabaseHelper.getInstance(SliderInputActivity.this);
                ArrayList<Character> results = db.queryKanjiWithScores(complexityScore, curvinessScore, symmetricityScore, searchTolerance);
                Log.d(TAG, "Querying database with scores: (" + complexityScore + ", " + curvinessScore + ", " + symmetricityScore + ")");
                Log.d(TAG, results.toString());
            }
        });

        sbComplexity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float complexityScore = sbComplexity.getProgress() / 10.0f;
                float curvinessScore = sbCurviness.getProgress() / 10.0f;
                float symmetricityScore = sbSymmetricity.getProgress() / 10.0f;

                DatabaseHelper db = DatabaseHelper.getInstance(SliderInputActivity.this);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
