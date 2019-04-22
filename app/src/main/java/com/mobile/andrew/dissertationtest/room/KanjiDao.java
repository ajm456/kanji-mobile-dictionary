package com.mobile.andrew.dissertationtest.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface KanjiDao
{
    @Query("SELECT Complexity, Symmetricity, Diagonality FROM Data WHERE Character LIKE :kanji")
    List<Score> getScore(String kanji);

    @Query("SELECT * FROM Data")
    List<KanjiData> getAll();

    @Query("SELECT * FROM Data WHERE Favourited = 1")
    List<KanjiData> getFavourites();

    @Query("UPDATE Data SET Favourited = :favouritedVal WHERE Character LIKE :kanji")
    void updateFavourited(String kanji, int favouritedVal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<KanjiData> data);
}
