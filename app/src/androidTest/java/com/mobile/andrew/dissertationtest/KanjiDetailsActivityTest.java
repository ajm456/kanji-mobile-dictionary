package com.mobile.andrew.dissertationtest;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.mobile.andrew.dissertationtest.room.KanjiData;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class KanjiDetailsActivityTest
{
    private static final KanjiData mockData = new KanjiData("foo", "foo", "foo", "foo", 1, "foo", false, 0f, 0f, 0f);

    @Rule
    public final ActivityTestRule<KanjiDetailsActivity> rule = new ActivityTestRule<KanjiDetailsActivity>(KanjiDetailsActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra("data", mockData);
            return intent;
        }
    };

    /**
     * Converts a Drawable object into a Bitmap - used for checking equality between Drawables.
     *
     * @param drawable  The Drawable object being converted into a Bitmap.
     * @return          A Bitmap of the given Drawable object.
     */
    private static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if(drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    /**
     * Checks whether two Drawables are equal or not. Uses their ConstantState property to check
     * for equivalence, and compares bitmaps of the two Drawables to avoid false negative as
     * detailed <a href="https://spotandroid.com/2017/02/15/android-tricks-how-to-compare-two-drawables/">here</a>.
     *
     * @param d1    The first Drawable being compared.
     * @param d2    The second Drawable being compared.
     * @return      Whether or not the two given Drawable objects are equal.
     */
    private static boolean areDrawablesEqual(Drawable d1, Drawable d2) {
        Drawable.ConstantState s1 = d1.getConstantState();
        Drawable.ConstantState s2 = d2.getConstantState();
        return (s1 != null && s1.equals(s2)
                || getBitmap(d1).sameAs(getBitmap(d2)));
    }

    @Test
    public void testFavouriteButtonIcon() {
        ActionMenuItemView button = rule.getActivity().findViewById(R.id.menuitem_favourites_favouritebutton);

        // Test the icon starts off empty
        assertTrue(areDrawablesEqual(Objects.requireNonNull(rule.getActivity().getDrawable(R.drawable.ic_star_empty)), button.getItemData().getIcon()));

        // Press the favourites button
        onView(withId(R.id.menuitem_favourites_favouritebutton)).perform(click());

        // Test the icon is now filled
        assertTrue(areDrawablesEqual(Objects.requireNonNull(rule.getActivity().getDrawable(R.drawable.ic_star_filled)), button.getItemData().getIcon()));

        // Press it again to test going back to empty
        onView(withId(R.id.menuitem_favourites_favouritebutton)).perform(click());
        assertTrue(areDrawablesEqual(Objects.requireNonNull(rule.getActivity().getDrawable(R.drawable.ic_star_empty)), button.getItemData().getIcon()));
    }

    @Test
    public void testOpenUrl() {
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData("https://jisho.org/search/" + mockData.character + "%20%23kanji"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.text_kanjidetails_jisho)).perform(click());
        intended(expectedIntent);
        Intents.release();
    }
}
