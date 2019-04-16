package com.mobile.andrew.dissertationtest.db;

import android.provider.BaseColumns;

public final class DatabaseContract implements BaseColumns
{
    private DatabaseContract() {}

    public static final String TABLE_NAME = "Data";
    public static final String COLUMN_NAME_CHARACTER = "Character";
    public static final String COLUMN_NAME_MEANINGS = "Meanings";
    public static final String COLUMN_NAME_KUN = "KunReadings";
    public static final String COLUMN_NAME_ON = "OnReadings";
    public static final String COLUMN_NAME_NUMSTROKES = "NumStrokes";
    public static final String COLUMN_NAME_JLPT = "JlptLevel";
    public static final String COLUMN_NAME_FAVOURITED = "Favourited";
    public static final String COLUMN_NAME_COMPLEXITY = "Complexity";
    public static final String COLUMN_NAME_SYMM = "Symmetricity";
    public static final String COLUMN_NAME_DIAG = "Diagonality";
}
