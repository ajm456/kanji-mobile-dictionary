package com.mobile.andrew.dissertationtest;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiDao;
import com.mobile.andrew.dissertationtest.room.KanjiData;
import com.mobile.andrew.dissertationtest.room.Score;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest
{
    private KanjiDao kanjiDao;
    private AppDatabase db;
    private List<KanjiData> dummyData;

    @Before
    public void createDb() {
        // Create Room database
        Context context = ApplicationProvider.getApplicationContext();
        // Use an in-memory database to isolate test from implementation
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        kanjiDao = db.kanjiDao();

        // Generate dummy data
        dummyData = new ArrayList<>(5);

        // Standard kanji data
        dummyData.add(new KanjiData("一", "one, one radical (no.1)", "ひと-,ひと.つ", "イチ,イツ", 1, "N5", false, 0.08f, 0.94f, 0.0f));
        // Favourited
        dummyData.add(new KanjiData("七", "seven", "なな,なな.つ,なの", "シチ", 2, "N5", true, 0.28f, 0.04f, 0.13f));
        // No kun readings
        dummyData.add(new KanjiData("校", "exam, school, printing, proof, correction", null, "コウ,キョウ", 10, "N5", false, 0.68f, 0.24f, 0.68f));
        // No on readings
        dummyData.add(new KanjiData("行", "going, journey, carry out, conduct, act, line, row, bank", "い.く,ゆ.く,-ゆ.き,-ゆき,-い.き,-いき,おこな.う,おこ.なう", null, 6, "N5", false, 0.48f, 0.09f, 0.28f));
        // No JLPT level
        dummyData.add(new KanjiData("分", "part, minute of time, segment, share, degree, one's lot, duty, understand, know, rate, 1%, chances, shaku/100", "わ.ける,わ.け,わ.かれる,わ.かる,わ.かつ", "ブン,フン,ブ", 4, null, false, 0.48f, 0.44f, 0.78f));

        // Insert dummy data into database
        kanjiDao.insertAll(dummyData);
    }

    @Test
    public void testInsertAndGetAll() {
        // Get list of inserted users
        List<KanjiData> retrievedList = kanjiDao.getAll();

        // Check that the inserted list and retrieved list are the same
        assertEquals(dummyData, retrievedList);
    }

    @Test
    public void testGetScore() {
        for(KanjiData d : dummyData) {
            // Grab the score for each character
            List<Score> scoreList = kanjiDao.getScore(d.character);

            // There should only be one score for one character
            assertEquals(scoreList.size(), 1);

            // Check that the returned score is equal to the dummy character's
            assertArrayEquals(scoreList.get(0).toFloatArray(), new float[] { d.complexity, d.symmetricity, d.diagonality }, 0f);
        }
    }

    @Test
    public void testGetFavourited() {
        // Fetch all favourited characters
        List<KanjiData> favouritesList = kanjiDao.getFavourites();

        // There is only one favourited character in the dummy data: the 2nd item
        assertEquals(favouritesList.size(), 1);
        assertEquals(favouritesList.get(0), dummyData.get(1));
    }

    @Test
    public void testAddFavourite() {
        // Update the favourited status for the 一 entry
        kanjiDao.updateFavourited("一", 1);
        dummyData.get(0).favourited = true;

        // Fetching favourites should now give a list of size two containing the 一 row
        List<KanjiData> favouritesList = kanjiDao.getFavourites();
        assertEquals(favouritesList.size(), 2);
        assertThat(favouritesList, hasItem(dummyData.get(0)));
    }

    @Test
    public void testRemoveFavouite() {
        // Remove the favourited status from the 七 row
        kanjiDao.updateFavourited("七", 0);

        // Fetching favourites should now give an empty list
        List<KanjiData> favouritesList = kanjiDao.getFavourites();
        assertTrue(favouritesList.isEmpty());
    }

    @After
    public void closeDb() {
        db.close();
    }
}
