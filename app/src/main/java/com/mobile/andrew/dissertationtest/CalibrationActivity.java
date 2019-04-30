package com.mobile.andrew.dissertationtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiData;

import java.util.List;

/**
 * Activity for performing a user-driven, low-dampening calibration of the database's kanji scores.
 * This activity is automatically shown on the user's first launch of the app, and can later be
 * accessed via an options menu.
 */
public class CalibrationActivity extends AppCompatActivity
{
    /**
     * {@link android.view.View.OnClickListener} implementation for displaying info about the
     * different attributes when a tooltip is clicked.
     */
    private class TooltipClickListener implements View.OnClickListener
    {
        private final String message;

        TooltipClickListener(String message) {
            this.message = message;
        }

        @Override
        public void onClick(View v) {
            // Apply a fade-out to every view descended from the layout root
            for(int i = 0; i < clRoot.getChildCount(); i++) {
                if(clRoot.getChildAt(i).getId() != R.id.text_calibration_tooltip) {
                    AlphaAnimation animation = new AlphaAnimation(1.0f, 0.1f);
                    animation.setDuration(300);
                    animation.setFillAfter(true);
                    clRoot.getChildAt(i).startAnimation(animation);
                }
            }

            // Apply a fade-in to the tooltip text
            AlphaAnimation animation = new AlphaAnimation(0f, 1.0f);
            animation.setDuration(300);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    tvTooltip.setText(message);
                    tvTooltip.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            tvTooltip.startAnimation(animation);
            displayingTooltip = true;
        }
    }

    /**
     * The characters used to calibrate against. The user is shown each one in turn and their score
     * is compared to the stored database score for the displayed character.
     */
    static final String[] CALIBRATION_CHARACTERS = { "十", "山" };
    /**
     * Dampening multiplier applied to the calibration - the higher the value, the lower the amount
     * the database scores are affected by.
     */
    private static final float DAMPENING = 0.3f;

    private ConstraintLayout clRoot;
    private TextView tvCharacter, tvTooltip;
    private SeekBar sbComplexity, sbSymmetricity, sbDiagonality;

    /**
     * Index of the calibration character being displayed
     */
    private int characterNumber = 0;
    private boolean displayingTooltip = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if extras were set: if so, activity was launched from home activity as opposed
        // to from launch
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            setContentView(R.layout.activity_calibration);
            initUi();
        } else {
            // Check if this is the first time the user is launching the app
            if(!PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("first_launch", true)) {
                // If this is not the first launch, transition straight to the home activity
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                finishAfterTransition();
            } else {
                setContentView(R.layout.activity_calibration);
                initUi();
            }
        }
    }

    /**
     * Initialises and sets up the UI elements in this activity's layout.
     */
    private void initUi() {
        clRoot = findViewById(R.id.root_calibration);
        tvCharacter = findViewById(R.id.text_calibration_kanji);
        tvTooltip = findViewById(R.id.text_calibration_tooltip);
        sbComplexity = findViewById(R.id.seekbar_calibration_complexity);
        sbSymmetricity = findViewById(R.id.seekbar_calibration_symmetricity);
        sbDiagonality = findViewById(R.id.seekbar_calibration_diagonality);

        ImageView ivComplexity = findViewById(R.id.image_calibration_complexityhelp);
        ImageView ivSymmetricity = findViewById(R.id.image_calibration_symmetricityhelp);
        ImageView ivDiagonality = findViewById(R.id.image_calibration_diagonalityhelp);
        final Button btnBack = findViewById(R.id.button_calibration_back);
        Button btnNext = findViewById(R.id.button_calibration_next);

        // Set the initial kanji character
        tvCharacter.setText(String.valueOf(CALIBRATION_CHARACTERS[characterNumber]));

        // Set the image click listeners
        ivComplexity.setOnClickListener(new TooltipClickListener(getResources().getString(R.string.calibrate_complexitytooltip)));
        ivSymmetricity.setOnClickListener(new TooltipClickListener(getResources().getString(R.string.calibrate_symmetricitytooltip)));
        ivDiagonality.setOnClickListener(new TooltipClickListener(getResources().getString(R.string.calibrate_diagonalitytooltip)));


        // Set the tooltip to reset on click if it's displayed
        tvTooltip.setOnClickListener(v -> {
            if(displayingTooltip) {
                // Apply a fade-in to every descendant of the layout root
                for(int i = 0; i < clRoot.getChildCount(); i++) {
                    if(clRoot.getChildAt(i).getId() != R.id.text_calibration_tooltip) {
                        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
                        animation.setDuration(300);
                        animation.setFillAfter(true);
                        clRoot.getChildAt(i).startAnimation(animation);
                    }
                }
                // Apply a fade-out to the tooltip
                AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
                animation.setDuration(300);
                animation.setFillAfter(false);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvTooltip.setVisibility(View.GONE);
                        tvTooltip.setText("");
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                tvTooltip.startAnimation(animation);
                displayingTooltip = false;
            }
        });

        // Set the next and back button listeners
        btnNext.setOnClickListener(v -> {
            if(!AppDatabase.PRESERVE_DICTIONARY) {
                // Grab the scores currently in each slider
                float complexityVal = sbComplexity.getProgress() / 10f;
                float symmVal = sbSymmetricity.getProgress() / 10f;
                float diagVal = sbDiagonality.getProgress() / 10f;

                /* Synchronously update the database scores */
                // Grab the current database scores for the character
                AppDatabase db = AppDatabase.getInstance();
                float[] actualScores = db.kanjiDao().getScore(CALIBRATION_CHARACTERS[characterNumber]).get(0).toFloatArray();

                // Calculate the difference vector
                float[] diff = new float[] {
                        (complexityVal - actualScores[0]) * DAMPENING,
                        (symmVal - actualScores[1]) * DAMPENING,
                        (diagVal - actualScores[2]) * DAMPENING
                };

                // Get the current database values
                List<KanjiData> allUsers = db.kanjiDao().getAll();
                // Update the scores of each row, clamping within the range 0-1
                for(int i = 0; i < allUsers.size(); i++) {
                    if(allUsers.get(i).complexity + diff[0] > 1f) {
                        allUsers.get(i).complexity = 1f;
                    } else if(allUsers.get(i).complexity + diff[0] < 0f) {
                        allUsers.get(i).complexity = 0f;
                    } else {
                        allUsers.get(i).complexity += diff[0];
                    }

                    if(allUsers.get(i).symmetricity + diff[1] > 1f) {
                        allUsers.get(i).symmetricity = 1f;
                    } else if(allUsers.get(i).symmetricity + diff[1] < 0f) {
                        allUsers.get(i).symmetricity = 0f;
                    } else {
                        allUsers.get(i).symmetricity += diff[1];
                    }

                    if(allUsers.get(i).diagonality + diff[2] > 1f) {
                        allUsers.get(i).diagonality = 1f;
                    } else if(allUsers.get(i).diagonality + diff[2] < 0f) {
                        allUsers.get(i).diagonality = 0f;
                    } else {
                        allUsers.get(i).diagonality += diff[2];
                    }
                }

                // Insert the updated list back into the database
                db.kanjiDao().insertAll(allUsers);
            }

            // If at the final character, start the main activity
            if(characterNumber == CALIBRATION_CHARACTERS.length - 1) {
                // No longer show this activity on launch
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean("first_launch", false);
                editor.apply();

                Intent intent = new Intent(CalibrationActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CalibrationActivity.this).toBundle());
                finishAfterTransition();
            } else {
                // Update the character index, ready to display the next calibration character
                characterNumber++;

                // Enable the back button
                if(!btnBack.isEnabled()) {
                    btnBack.setEnabled(true);
                }

                // Animate the old character out
                ObjectAnimator translateOut = ObjectAnimator.ofFloat(tvCharacter, View.TRANSLATION_X, -200f);
                translateOut.setDuration(200);
                ValueAnimator fadeOut = ObjectAnimator.ofFloat(tvCharacter, View.ALPHA, 1f, 0f);
                fadeOut.setDuration(200);
                translateOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tvCharacter.setText(CALIBRATION_CHARACTERS[characterNumber]);
                        tvCharacter.setTranslationX(200f);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });

                // Animate the new character in
                ObjectAnimator translateIn = ObjectAnimator.ofFloat(tvCharacter, View.TRANSLATION_X, 0f);
                translateIn.setDuration(200);
                ValueAnimator fadeIn = ObjectAnimator.ofFloat(tvCharacter, View.ALPHA, 0f, 1f);
                fadeIn.setDuration(200);

                // Group the animations and start them
                AnimatorSet animationSet = new AnimatorSet();
                animationSet.play(translateOut).with(fadeOut).before(translateIn);
                animationSet.play(translateIn).with(fadeIn);
                animationSet.start();
            }
        });

        btnBack.setOnClickListener(v -> {
            // Update the character number to the previous calibration character
            characterNumber--;

            // Disable the back button if we're back at the first character
            if(characterNumber == 0) {
                btnBack.setEnabled(false);
            }

            // Animate back to the previous character
            ObjectAnimator translateOut = ObjectAnimator.ofFloat(tvCharacter, View.TRANSLATION_X, 200f);
            translateOut.setDuration(200);
            ValueAnimator fadeOut = ObjectAnimator.ofFloat(tvCharacter, View.ALPHA, 1f, 0f);
            fadeOut.setDuration(200);
            translateOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvCharacter.setText(CALIBRATION_CHARACTERS[characterNumber]);
                    tvCharacter.setTranslationX(-200f);
                }

                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            // Animate the new character in
            ObjectAnimator translateIn = ObjectAnimator.ofFloat(tvCharacter, View.TRANSLATION_X, 0f);
            translateIn.setDuration(200);
            ValueAnimator fadeIn = ObjectAnimator.ofFloat(tvCharacter, View.ALPHA, 0f, 1f);
            fadeIn.setDuration(200);

            // Group the animations and start them
            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(translateOut).with(fadeOut).before(translateIn);
            animationSet.play(translateIn).with(fadeIn);
            animationSet.start();
        });
    }
}
