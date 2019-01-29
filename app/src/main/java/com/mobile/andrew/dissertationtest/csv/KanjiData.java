package com.mobile.andrew.dissertationtest.csv;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public final class KanjiData
{
    private static final String TAG = KanjiData.class.getSimpleName();

    private static KanjiData instance;

    private static HashMap<Float[], Character> dict;

    private KanjiData(Context context) {
        dict = new HashMap<>();
        loadCsvIntoDict(context);
    }

    public static KanjiData getInstance(Context context) {
        if(instance == null) {
            instance = new KanjiData(context);
        }
        return instance;
    }

    public ArrayList<Character> queryDict(Float[] scores, float tolerance) {
        TreeMap<Float, Character> matches = new TreeMap<>();
        for(Float[] key : dict.keySet()) {
            float distance = 0f;
            boolean match = true, empty = true;
            for(int i = 0; i < scores.length; i++) {
                // If score is negative, ignore it
                if(scores[i] >= 0f) {
                    // Confirm we have not been sent an empty score query
                    empty = false;
                    if(Math.abs(scores[i] - key[i]) > tolerance) {
                        // As soon as a score is found to not be within tolerance, exit and report
                        // a non-match
                        match = false;
                        break;
                    } else {
                        distance += Math.abs(scores[i] - key[i]);
                    }
                }
            }

            // If sent an empty score, return an empty list of characters
            if(empty) {
                return new ArrayList<Character>();
            }

            if(match) {
                // Divide to find distance from submitted score and key
                distance /= 5.0f;

                // Insert matching character with distance into matches list
                matches.put(distance, dict.get(key));
            }
        }

        return new ArrayList<>(matches.values());
    }

    public String dictToString() {
        return dict.toString();
    }

    private static void loadCsvIntoDict(Context context) {
        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open("kanji.csv"));
            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            while((line = reader.readLine()) != null) {
                // Split line by csv
                String[] data = line.split(",");

                // Get scores - first five elements in array
                String[] strScores = Arrays.copyOfRange(data, 0, data.length - 1);

                // Convert string scores to floats
                Float[] scores = new Float[strScores.length - 1];
                for(int i = 0; i < strScores.length - 1; i++) {
                    scores[i] = Float.valueOf(strScores[i]);
                }

                // Get kanji - last element in array
                Character kanji = data[data.length - 1].charAt(0);

                // Add scores / kanji pair to dictionary
                dict.put(scores, kanji);
            }

            // Tidy up
            is.close();
            reader.close();
        } catch(IOException e) {
            Log.e(TAG, "Fatal error! IOException when reading from kanji csv file");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
