package com.mobile.andrew.dissertationtest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest
{
    @Rule
    public final ActivityTestRule<HomeActivity> rule = new ActivityTestRule<>(HomeActivity.class);

    private final Activity[] currentActivity = new Activity[1];

    private void monitorCurrentActivity() {
        rule.getActivity().getApplication()
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityResumed(Activity activity) {
                        currentActivity[0] = activity;
                    }

                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

                    @Override
                    public void onActivityStarted(Activity activity) {}

                    @Override
                    public void onActivityPaused(Activity activity) {}

                    @Override
                    public void onActivityStopped(Activity activity) {}

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

                    @Override
                    public void onActivityDestroyed(Activity activity) {}
                });
    }

    private Activity getCurrentActivity() {
        return currentActivity[0];
    }

    @LargeTest
    @Test
    public void testOpenDetailsActivity() throws Exception {
        // Set up activity monitoring callback
        monitorCurrentActivity();

        // Set the search tolerance to max to test as many kanji as feasible
        SeekBar sbTolerance = rule.getActivity().findViewById(R.id.seekbar_home_searchtolerance);
        sbTolerance.setProgress(sbTolerance.getMax());

        // Sleep thread while waiting for adapter to update - not best practice but perfectly
        // acceptable in this scenario
        Thread.sleep(1000);

        // Get the number of items in the adapter
        RecyclerView rv = rule.getActivity().findViewById(R.id.recycleview_home_results);
        KanjiListAdapter kanjiListAdapter = (KanjiListAdapter) rv.getAdapter();
        int numItems = Objects.requireNonNull(kanjiListAdapter).getItemCount();

        // Test clicking on each one
        for(int i = 0; i < numItems; i++) {
            // Get the character stored in position i in the list
            String itemCharacter = kanjiListAdapter.getItem(i).character;

            // Click on the item
            onView(withId(R.id.recycleview_home_results))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));

            // Check that the KanjiDetails root layout is being displayed
            onView((withId(R.id.root_kanjidetails))).check(matches(isDisplayed()));

            // Check that the correct kanji is being displayed
            TextView tvCharacter = getCurrentActivity().findViewById(R.id.text_kanjidetails_character);
            assertEquals(tvCharacter.getText(), itemCharacter);

            // Navigate back to home activity
            pressBack();
        }
    }

    @Test
    public void testNavigateToFavourites() {
        // Try and navigate to favourites
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(rule.getActivity().getString(R.string.home_menuitem_favourites)))
                .perform(click());
        // Check favourites view is displayed
        onView(withId(R.id.root_favourites)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToCalibration() {
        // Try and navigate to calibration
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(rule.getActivity().getString(R.string.home_menuitem_calibrate)))
                .perform(click());
        // Check calibration view is displayed
        onView(withId(R.id.root_calibration)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoResultsHint() {
        // Perform a search that will return no results
        SeekBar sbComp = rule.getActivity().findViewById(R.id.seekbar_home_complexity);
        SeekBar sbSymm = rule.getActivity().findViewById(R.id.seekbar_home_symm);
        SeekBar sbDiag = rule.getActivity().findViewById(R.id.seekbar_home_diagonality);
        SeekBar sbTolerance = rule.getActivity().findViewById(R.id.seekbar_home_searchtolerance);

        sbComp.setProgress(sbComp.getMin());
        sbSymm.setProgress(sbSymm.getMin());
        sbDiag.setProgress(sbDiag.getMin());
        sbTolerance.setProgress(sbTolerance.getMin());

        // Check the no results hint is displayed
        onView(withId(R.id.text_home_noresultshint)).check(matches(isDisplayed()));

        // Perform a search that will return a result
        sbComp.setProgress(sbComp.getMax() / 2);
        sbSymm.setProgress(sbSymm.getMax() / 2);
        sbDiag.setProgress(sbDiag.getMax() / 2);
        sbTolerance.setProgress(sbTolerance.getMax());

        // Check the no results hint is gone
        onView(withId(R.id.text_home_noresultshint)).check(matches(not(isDisplayed())));
    }
}
