package com.mobile.andrew.dissertationtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.mobile.andrew.dissertationtest.room.KanjiData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class KanjiDetailsActivityTest
{
    @Rule
    public ActivityTestRule<KanjiDetailsActivity> rule = new ActivityTestRule<KanjiDetailsActivity>(KanjiDetailsActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra("data", new KanjiData("foo", "foo", "foo", "foo", 1, "foo", false, 0f, 0f, 0f));
            return intent;
        }
    };

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

    private static boolean areDrawablesEqual(Drawable d1, Drawable d2) {
        Drawable.ConstantState s1 = d1.getConstantState();
        Drawable.ConstantState s2 = d2.getConstantState();
        return (s1 != null && s2 != null && s1.equals(s2)
                || getBitmap(d1).sameAs(getBitmap(d2)));
    }

    @Test
    public void testFavouriteButtonIcon() {
        ActionMenuItemView button = rule.getActivity().findViewById(R.id.menuitem_favourites_favouritebutton);

        // Test the icon starts off empty
        assertTrue(areDrawablesEqual(rule.getActivity().getDrawable(R.drawable.ic_star_empty), button.getItemData().getIcon()));

        // Press the favourites button
        onView(withId(R.id.menuitem_favourites_favouritebutton)).perform(click());

        // Test the icon is now filled
        assertTrue(areDrawablesEqual(rule.getActivity().getDrawable(R.drawable.ic_star_filled), button.getItemData().getIcon()));

        // Press it again to test going back to empty
        onView(withId(R.id.menuitem_favourites_favouritebutton)).perform(click());
        assertTrue(areDrawablesEqual(rule.getActivity().getDrawable(R.drawable.ic_star_empty), button.getItemData().getIcon()));
    }
}
