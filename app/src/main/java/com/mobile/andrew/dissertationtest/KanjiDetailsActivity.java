package com.mobile.andrew.dissertationtest;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.andrew.dissertationtest.db.KanjiData;
import com.mobile.andrew.dissertationtest.db.KanjiDictionary;

import java.util.Locale;

public class KanjiDetailsActivity extends AppCompatActivity
{
    private static final String TAG = KanjiDetailsActivity.class.getSimpleName();

    private KanjiData kanjiData;
    private Menu menu;
    private boolean favourited;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanjidetails);

        // Receive the kanji data to display from the intent
        kanjiData = getIntent().getParcelableExtra("data");
        favourited = kanjiData.favourited;

        initUi();

        // Update the dictionary
        if(!HomeActivity.PRESERVE_DICTIONARY) {
            KanjiDictionary dict = KanjiDictionary.getInstance();
            float searchedComplexScore = getIntent().getFloatExtra("complex_score", -1f);
            float searchedSymmScore = getIntent().getFloatExtra("symm_score", -1f);
            float searchedStrokeCurvScore = getIntent().getFloatExtra("strokeCurv_score", -1f);
            float[] searchedScores = new float[] { searchedComplexScore, searchedSymmScore,
                    searchedStrokeCurvScore };
            dict.improveDictScores(searchedScores, kanjiData.kanji, 0.1f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites_toolbar, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(favourited) {
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star_filled));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!favourited) {
            item.setIcon(getDrawable(R.drawable.ic_star_filled));
            favourited = true;
            Toast.makeText(KanjiDetailsActivity.this, "Added to favourites!", Toast.LENGTH_SHORT).show();
        } else {
            item.setIcon(getDrawable(R.drawable.ic_star_empty));
            favourited = false;
            Toast.makeText(KanjiDetailsActivity.this, "Removed from favourites!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KanjiDictionary.getInstance().changeFavourite(Character.toString(kanjiData.kanji), favourited, this);
    }

    private void initUi() {
        TextView tvCharacter = findViewById(R.id.kanjiDetails_tv_character);
        TextView tvMeanings = findViewById(R.id.kanjiDetails_tv_translations);
        TextView tvKunReadings = findViewById(R.id.kanjiDetails_tv_kunVals);
        TextView tvOnReadings = findViewById(R.id.kanjiDetails_tv_onVals);
        TextView tvNumStrokes = findViewById(R.id.kanjiDetails_tv_numStrokesVal);
        TextView tvJlptLevel = findViewById(R.id.kanjiDetails_tv_jlptLevelVal);
        TextView tvJishoLink = findViewById(R.id.kanjiDetails_tv_jishoLink);

        Toolbar toolbar = findViewById(R.id.toolbar_kanjidetails);
        toolbar.setTitle(R.string.kanjiresults_toolbartitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        // Display the kanji data in views
        tvCharacter.setText(Character.toString(kanjiData.kanji));
        tvMeanings.setText(TextUtils.join(", ", kanjiData.meanings));
        tvKunReadings.setText(TextUtils.join(", ", kanjiData.kunReadings));
        tvOnReadings.setText(TextUtils.join(", ", kanjiData.onReadings));
        tvNumStrokes.setText(String.format(Locale.UK, "%d", kanjiData.numStrokes));
        tvJlptLevel.setText(kanjiData.jlptLevel);

        // Enable the link to Jisho's page on the character
        tvJishoLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jisho.org/search/" + kanjiData.kanji + "%20%23kanji"));
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
