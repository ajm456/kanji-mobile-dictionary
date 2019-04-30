package com.mobile.andrew.dissertationtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class CalibrationActivityTest
{
    private String[] calibrationChars;

    @Rule
    public final ActivityTestRule<CalibrationActivity> rule = new ActivityTestRule<>(CalibrationActivity.class, true, false);

    @Before
    public void setUp() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext())
                .edit();
        edit.putBoolean("first_launch", true);
        edit.commit();
        rule.launchActivity(new Intent());

        calibrationChars = CalibrationActivity.CALIBRATION_CHARACTERS;
    }

    @Test
    public void testNavigation_forward() {
        for (String calibrationChar : calibrationChars) {
            // Check that the correct character is being displayed
            onView(withId(R.id.text_calibration_kanji))
                    .check(matches(withText(containsString(calibrationChar))));

            // Press next - load next character
            onView(withId(R.id.button_calibration_next)).perform(click());
        }

        // Check that the home activity has launched
        onView(withId(R.id.root_home)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigation_back() {
        // Move to end of characters
        for(int n = 0; n < calibrationChars.length - 1; n++) {
            onView(withId(R.id.button_calibration_next)).perform(click());
        }

        // Move back to the first character
        for(int i = calibrationChars.length - 2; i > 0; i--) {
            onView(withId(R.id.button_calibration_next)).perform(click());
            // Check the correct character is being displayed
            onView(withId(R.id.text_calibration_kanji))
                    .check(matches(withText(containsString(calibrationChars[i]))));
        }
    }
}
