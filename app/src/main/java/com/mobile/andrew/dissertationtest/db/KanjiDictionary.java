package com.mobile.andrew.dissertationtest.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobile.andrew.dissertationtest.App;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton pattern class used for querying kanji characters and their scores. Loads kanji
 * characters and scores from a CSV file into local memory.
 */
public final class KanjiDictionary
{
    private static final String TAG = KanjiDictionary.class.getSimpleName();

    private static KanjiDictionary instance;

    private static volatile HashMap<ScoreKey, ArrayList<Character>> dict = new HashMap<>();
    private static ArrayList<KanjiData> info = new ArrayList<>();
    private static ReentrantLock dictLock = new ReentrantLock();
    private static boolean loaded = false;

    private KanjiDictionary() {}

    /**
     * Thread-safe singleton instance getter method. Uses lazy initialisation as well as a double
     * check locking pattern.
     *
     * @return      A reference to the application's KanjiDictionary object.
     */
    public static KanjiDictionary getInstance() {
        if(instance == null) {
            synchronized(KanjiDictionary.class) {
                if(instance == null) {
                    instance = new KanjiDictionary();
                }
            }
        }
        return instance;
    }

    /**
     * Queries the dictionary of kanji characters with a set of attribute scores and tolerance
     * range. All characters whose scores are within the given tolerance of the given scores
     * vector are returned with their related info; conversely, if a single attribute of a character
     * in the dictionary is not within tolerance from the corresponding attribute in the submitted
     * score vector, the character is not returned. The returned list of kanji characters is
     * guaranteed to be sorted by their distance from the score vector, nearest first.
     *
     * @param scores        A score vector to compare characters in the dictionary against.
     * @param tolerance     A scalar value indicated the maximum distance dictionary kanji
     *                      characters' attributes can be from the score vector.
     *
     * @return              An ArrayList containing a KanjiData object for each kanji character
     *                      within tolerance distance from the submitted score vector. This list is
     *                      sorted by distance, ascending.
     */
    public ArrayList<KanjiData> queryDict(Float[] scores, float tolerance) {
        ArrayList<DistanceKanjiPair> matches = new ArrayList<>();
        dictLock.lock();
        for(ScoreKey key : dict.keySet()) {
            float distance = 0f;
            boolean match = true, empty = true;
            for(int i = 0; i < scores.length; i++) {
                // If score is negative, ignore it
                if(scores[i] >= 0f) {
                    // Confirm we have not been sent an empty score query
                    empty = false;
                    if(Math.abs(scores[i] - key.get(i)) > tolerance) {
                        // As soon as a score is found to not be within tolerance, exit and report
                        // a non-match
                        match = false;
                        break;
                    } else {
                        distance += Math.abs(scores[i] - key.get(i));
                    }
                }
            }

            // If sent an empty score, return an empty list of characters
            if(empty) {
                dictLock.unlock();
                return new ArrayList<>();
            }

            if(match) {
                // Divide to find distance from submitted score and key
                distance /= scores.length;

                // Insert matching characters with distance into matches list
                for(Character c : dict.get(key)) {
                    matches.add(new DistanceKanjiPair(c, distance));
                }
            }
        }
        dictLock.unlock();

        Collections.sort(matches);
        ArrayList<Character> characters = new ArrayList<>(matches.size());
        for(int i = 0; i < matches.size(); i++) {
            characters.add(matches.get(i).getKanji());
        }

        // Convert the characters into KanjiData objects
        ArrayList<KanjiData> kanjiData = new ArrayList<>(characters.size());
        for(KanjiData d : info) {
            if(characters.contains(d.kanji)) {
                kanjiData.add(new KanjiData(d));
            }
        }
        return kanjiData;
    }

    /**
     * Converts the HashMap of kanji characters and scores into a readable String form.
     *
     * @return      A String representation of the kanji dictionary HashMap.
     */
    public String dictToString() {
        return dict.toString();
    }

    /**
     * Adjusts the key scores in the kanji dictionary based on how close a user's inputted scores
     * were to what their selected result was.
     *
     * The point here is to ensure that the user and the dictionary both agree on how to quantify
     * kanji attributes. If the user finds a character they want but have described it as having
     * complexity of 0.7 whereas the dictionary stored it as 0.6, the complexity value of every
     * kanji in the dictionary is increased by a small amount.
     *
     * @param inputtedScores    The attribute score vector the user used to search with.
     * @param selectedKanji     The resultant kanji character the user selected.
     */
    public void improveDictScores(float[] inputtedScores, Character selectedKanji, float dampening) {
        Log.d(TAG, "Updating dictionary with scores ["+inputtedScores[0] + ","+inputtedScores[1]+","+inputtedScores[2]+"]");
        Log.d(TAG, "Dictionary is currently: " + dictToString());

        // First, find the score currently stored for the resultant kanji
        ArrayList<Character> charactersWithResultIn = null;
        Collection<ArrayList<Character>> allCharacters = dict.values();
        for(ArrayList<Character> characters : allCharacters) {
            if(characters.contains(selectedKanji)) {
                charactersWithResultIn = characters;
            }
        }
        if(charactersWithResultIn == null) {
            Log.e(TAG, "Could not find submitted kanji in dict values!");
            System.exit(-1);
        }

        ScoreKey actualKey = null;
        for(ScoreKey key : dict.keySet()) {
            if(dict.get(key).equals(charactersWithResultIn)) {
                actualKey = key;
            }
        }
        if(actualKey == null) {
            Log.e(TAG, "Could not find key for submitted kanji in dict keys!");
            System.exit(-1);
        }

        // Calculate the adjustment vector between the actual key and the submitted scores
        Float[] adjust = new Float[inputtedScores.length];
        for(int i = 0; i < adjust.length; i++) {
            adjust[i] = (inputtedScores[i] - actualKey.get(i)) * dampening;
        }

        // Adjust the dictionary key values accordingly.
        // Since keys are immutable in HashMaps by definition, we must replace the current
        // dictionary object. This has the potential to be a slow operation, so we will do this
        // off of the UI thread.
        new Thread(new Runnable() {

            private final String TAG = KanjiDictionary.class.getSimpleName() + " : DictionaryUpdater";

            private Float[] adjust;

            Runnable init(Float[] adjust) {
                this.adjust = adjust;
                return this;
            }

            @Override
            public void run() {
                HashMap<ScoreKey, ArrayList<Character>> newDict = new HashMap<>();
                for(ScoreKey key : dict.keySet()) {
                    ScoreKey newKey = new ScoreKey(key, adjust);
                    newDict.put(newKey, new ArrayList<>(dict.get(key)));
                }

                // Safely update the new dictionary
                dictLock.lock();
                dict = newDict;
                dictLock.unlock();

                Log.d(TAG, "Successfully updated the dictionary with improved values!");
                Log.d(TAG, "Dictionary is now: " + dictToString());
                Log.d(TAG, "Starting a service to save the new dictionary!");
                saveDictToDatabase();
            }
        }.init(adjust)).start();
    }

    /**
     * Changes the value of a kanji character's "favourited" column to the value specified in
     * shouldFavourite.
     *
     * @param kanji             The character who's being (un)favourited.
     * @param shouldFavourite   Whether or not the character is to be favourited.
     * @param context           The context object of the calling activity.
     */
    public void changeFavourite(String kanji, boolean shouldFavourite, Context context) {
        // Create the ContentValues object to update the table with
        ContentValues values = new ContentValues();
        int favouriteValue = shouldFavourite ? 1 : 0;
        values.put(DatabaseContract.COLUMN_NAME_FAVOURITED, favouriteValue);
        // Create the filter clause
        String selection = DatabaseContract.COLUMN_NAME_CHARACTER + " LIKE ?";
        String[] selectionArgs = { kanji };

        // Update the data array list
        for(KanjiData d : info) {
            if(d.kanji == kanji.charAt(0)) {
                d.favourited = shouldFavourite;
            }
        }

        // Update the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numRowsChanged = db.update(
                DatabaseContract.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        if(numRowsChanged != 1) {
            throw new SQLException("When changing a favourite value, update more / fewer than 1 row!");
        }
    }

    public static void loadDatabaseIntoDict(Context context) {
        // If we've already loaded kanji from the database, exit the method - prevents any errors
        // occurring from accidentally invoking this method twice in runtime
        if(loaded) {
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query(
                DatabaseContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while(result.moveToNext()) {
            // First get scores to create the dict key
            Float[] scores = new Float[3];
            scores[0] = result.getFloat(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_COMPLEXITY));
            scores[1] = result.getFloat(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_SYMM));
            scores[2] = result.getFloat(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_DIAG));
            ScoreKey key = new ScoreKey(scores);

            // Get the character
            Character kanji = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_CHARACTER)).charAt(0);

            // Add scores / kanji pair to the dictionary
            if(dict.containsKey(key)) {
                dict.get(key).add(kanji);
            } else {
                ArrayList<Character> l = new ArrayList<>();
                l.add(kanji);
                dict.put(key, l);
            }

            // Store the related info for the character
            String[] meanings = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_MEANINGS)).split(", ");
            String kunReadingsStr = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_KUN));
            String[] kunReadings;
            if(kunReadingsStr != null) {
                kunReadings = kunReadingsStr.split(",");
            } else {
                kunReadings = new String[] {};
            }
            String onReadingsStr = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_ON));
            String[] onReadings;
            if(onReadingsStr != null) {
                onReadings = onReadingsStr.split(",");
            } else {
                onReadings = new String[] {};
            }
            int numStrokes = result.getInt(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_NUMSTROKES));
            String jlptLevel = result.getString(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_JLPT));
            boolean favourited = (result.getInt(result.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_FAVOURITED)) == 1);
            info.add(new KanjiData(kanji, jlptLevel, meanings, kunReadings, onReadings, numStrokes, favourited));
        }
        result.close();
        loaded = true;
    }

    private void saveDictToDatabase() {
        Context context = App.getContext();
        Intent intent = new Intent(context, DictToStorageService.class);
        intent.putExtra("dict", dict);
        context.startService(intent);
    }
}
