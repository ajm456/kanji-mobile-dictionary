package com.mobile.andrew.dissertationtest.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Database access object containing all the queries required during the app's lifetime.
 */
@Dao
public interface KanjiDao
{
    /**
     * Fetches the score for the given kanji.
     *
     * @param kanji The kanji whose score is being searched for.
     * @return      A list of {@link Score} objects - this will never be longer than length 1.
     */
    @Query("SELECT Complexity, Symmetricity, Diagonality FROM Data WHERE Character LIKE :kanji")
    List<Score> getScore(String kanji);

    /**
     * Fetches all stored kanji data.
     *
     * @return  A list of all kanji data in the database.
     */
    @Query("SELECT * FROM Data")
    List<KanjiData> getAll();

    /**
     * Fetches all favourited kanji data.
     *
     * @return  A list of all favourited kanji and their data in the database.
     */
    @Query("SELECT * FROM Data WHERE Favourited = 1")
    List<KanjiData> getFavourites();

    /**
     * Updates the favourited value of the given kanji character.
     *
     * @param kanji             The character whose favourited value is being updated.
     * @param favouritedVal     The new value for the given character's favourited field.
     */
    @Query("UPDATE Data SET Favourited = :favouritedVal WHERE Character LIKE :kanji")
    void updateFavourited(String kanji, int favouritedVal);

    /**
     * Fetches the favourited value for the given kanji character.
     *
     * @param kanji The character whose favourited value is being requested.
     * @return      The stored favourited value of the given character.
     */
    @Query("SELECT Favourited FROM Data WHERE Character LIKE :kanji")
    int isFavourited(String kanji);

    /**
     * Inserts a list of kanji data into the database, replacing any rows with conflicting
     * characters.
     *
     * @param data  A list of kanji data to be inserted into the Data table of the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<KanjiData> data);
}
