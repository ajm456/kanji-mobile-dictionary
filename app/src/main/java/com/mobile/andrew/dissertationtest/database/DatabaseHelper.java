package com.mobile.andrew.dissertationtest.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mobile.andrew.dissertationtest.App;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLInput;
import java.util.ArrayList;

/**
 * Standard Android database helper class. Obeys the singleton design pattern.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static DatabaseHelper instance;

    private static String DB_PATH;
    private static String DB_NAME = "kanjiDB.db";

    /**
     * Standard singleton private constructor. Sets the database path string variable.
     *
     * @param context   The context from which the object is being initialized.
     */
    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /**
     * Standard singleton instance provider.
     *
     * @param context   The context from which the instance is being requested.
     * @return          A non-null DatabaseHelper instance guaranteeing a single point of entry
     *                  to the database across the application.
     */
    public synchronized static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Checks to see if a database is already present in the filesystem; if not, copies over the
     * data from the assets folder into a new database file in the device storage.
     */
    public void createDatabase() {
        boolean dbExist = checkDatabase();

        if(dbExist) {
            Log.d(TAG, "Database already exists! Will NOT copy into");
        } else {
            // By calling this method an empty database will be created into the default system
            // path of your application so we can overwrite that with our database
            Log.d(TAG, "Database does not exist! Copying data across");
            getReadableDatabase();
            copyDatabase();
        }
    }

    public ArrayList<Character> queryKanjiWithScores(float complexity, float curviness, float symmetricity, float tolerance) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Character> result = new ArrayList<Character>();

        String selection = "Complexity BETWEEN ? AND ? AND " +
                "Curviness BETWEEN ? AND ? AND " +
                "Symmetricity BETWEEN ? AND ?";

        String[] selectionArgs = {
                Float.toString(complexity - tolerance), Float.toString(curviness + tolerance),
                Float.toString(curviness - tolerance), Float.toString(curviness + tolerance),
                Float.toString(symmetricity - tolerance), Float.toString(symmetricity + tolerance)
        };

        Cursor cursor = db.query(
                "Kanji",
                new String[] { "Character" },
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String strKanji = cursor.getString(0);
            if(strKanji.length() > 1) {
                Log.e(TAG, "Fatal error! Got kanji character with length of more than 1 character from database!");
                System.exit(-1);
            }
            char kanji = strKanji.charAt(0);
            result.add(kanji);
        }

        return result;
    }

    /**
     * Checks to see whether or not a database file is present in device storage.
     *
     * @return  Whether or not a database file is present in device storage.
     */
    private boolean checkDatabase() {
        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch(SQLiteException e) {
            // Database doesn't exist yet
        }

        if(checkDb != null) {
            checkDb.close();
        }

        return checkDb != null;
    }

    /**
     * Copies the contents of the database file stored in the assets folder over into device
     * storage.
     */
    private void copyDatabase() {
        InputStream in;
        OutputStream out;

        try {
            // Open your local db as the input stream
            in = App.getContext().getAssets().open("databases/" + DB_NAME);

            // Open the empty db as the output stream
            out = new FileOutputStream(DB_PATH);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while( (length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            // Close streams
            out.flush();
            out.close();
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
