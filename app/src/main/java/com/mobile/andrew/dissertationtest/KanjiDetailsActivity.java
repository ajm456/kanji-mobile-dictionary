package com.mobile.andrew.dissertationtest;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mobile.andrew.dissertationtest.asyncTasks.FeedbackCalibrationParams;
import com.mobile.andrew.dissertationtest.asyncTasks.FeedbackCalibrationTask;
import com.mobile.andrew.dissertationtest.asyncTasks.UpdateFavouritedParams;
import com.mobile.andrew.dissertationtest.asyncTasks.UpdateFavouritedTask;
import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiData;

import java.util.Locale;
import java.util.Objects;

/**
 * This activity displays all relevant information about a single kanji character. It also allows
 * the user to add (or remove) the character from their favourites, to be displayed in
 * {@link FavouritesActivity}.
 */
public class KanjiDetailsActivity extends AppCompatActivity
{
    private KanjiData kanjiData;
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
        if(!AppDatabase.PRESERVE_DICTIONARY) {
            // Get the searched scores from the activity intent
            float searchedComplexScore = getIntent().getFloatExtra("complex_score", -1f);
            float searchedSymmScore = getIntent().getFloatExtra("symm_score", -1f);
            float searchedDiagScore = getIntent().getFloatExtra("diag_score", -1f);
            float[] searchedScore = new float[] { searchedComplexScore, searchedSymmScore,
                    searchedDiagScore };
            // Create a task params object
            FeedbackCalibrationParams params = new FeedbackCalibrationParams(searchedScore, kanjiData.character);
            // Start a feedback calibration task
            new FeedbackCalibrationTask().execute(params);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Adjust the icon of the favourite star to match whether or not the character is currently
        // favourited
        if(favourited) {
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star_filled));
        } else {
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star_empty));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // (Un)favourite the character when the favourite star button is clicked
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
        // Update the current favourited value in the database for this character
        super.onPause();
        UpdateFavouritedParams params = new UpdateFavouritedParams(kanjiData.character, (favourited ? 1 : 0));
        new UpdateFavouritedTask().execute(params);
    }

    /**
     * Initialises and sets up the UI elements in this activity's layout.
     */
    private void initUi() {
        TextView tvCharacter = findViewById(R.id.text_kanjidetails_character);
        TextView tvMeanings = findViewById(R.id.text_kanjidetails_meanings);
        TextView tvKunReadings = findViewById(R.id.text_kanjidetails_kunvals);
        TextView tvOnReadings = findViewById(R.id.text_kanjidetails_onvals);
        TextView tvNumStrokes = findViewById(R.id.text_kanjidetails_strokesval);
        TextView tvJlptLevel = findViewById(R.id.text_kanjidetails_jlptval);
        TextView tvJishoLink = findViewById(R.id.text_kanjidetails_jisho);

        Toolbar toolbar = findViewById(R.id.toolbar_kanjidetails);
        toolbar.setTitle(R.string.kanjidetails_toolbartitle);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeActionContentDescription(R.string.all_navigateupdescription);
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
        //noinspection deprecation
        Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        // Display the kanji data in views
        tvCharacter.setText(kanjiData.character);
        tvMeanings.setText(kanjiData.meanings);
        tvKunReadings.setText(kanjiData.kunReadings);
        tvOnReadings.setText(kanjiData.onReadings);
        tvNumStrokes.setText(String.format(Locale.UK, "%d", kanjiData.numStrokes));
        tvJlptLevel.setText(kanjiData.jlptLevel);

        // Enable the link to Jisho's page on the character
        tvJishoLink.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jisho.org/search/" + kanjiData.character + "%20%23kanji"));
            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }
}
