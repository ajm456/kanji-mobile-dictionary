package com.mobile.andrew.dissertationtest.asyncTasks;

import android.os.AsyncTask;

import com.mobile.andrew.dissertationtest.room.AppDatabase;
import com.mobile.andrew.dissertationtest.room.KanjiData;

import java.util.List;

public class FeedbackCalibrationTask extends AsyncTask<FeedbackCalibrationParams, Void, Void>
{
    static final float FEEDBACK_DAMPENING = 0.1f;

    @Override
    protected Void doInBackground(FeedbackCalibrationParams... params) {
        float[] searchedScore = params[0].searchedScore;
        String character = params[0].character;
        AppDatabase db = AppDatabase.getInstance();

        // Get the scores of the character searched for
        float[] characterScore = db.kanjiDao().getScore(character).get(0).toFloatArray();

        // Calculate the difference vector
        float[] diff = calculateDiffVector(searchedScore, characterScore);

        // Get the current database values
        List<KanjiData> data = db.kanjiDao().getAll();

        // Update the scores of each row
        updateScores(data, diff);

        // Insert the updated list back into the database
        db.kanjiDao().insertAll(data);

        return null;
    }

    static float[] calculateDiffVector(float[] searchedScore, float[] characterScore) {
        float[] diff = new float[3];
        if(searchedScore[0] == -1f) {
            diff[0] = 0f;
        } else {
            diff[0] = (searchedScore[0] - characterScore[0]) * FEEDBACK_DAMPENING;
        }
        if(searchedScore[1] == -1f) {
            diff[1] = 0f;
        } else {
            diff[1] = (searchedScore[1] - characterScore[1]) * FEEDBACK_DAMPENING;
        }
        if(searchedScore[2] == -1f) {
            diff[2] = 0f;
        } else {
            diff[2] = (searchedScore[2] - characterScore[2]) * FEEDBACK_DAMPENING;
        }

        return diff;
    }

    static void updateScores(List<KanjiData> currVals, float[] diff) {
        for(int i = 0; i < currVals.size(); i++) {
            if(currVals.get(i).complexity + diff[0] > 1f) {
                currVals.get(i).complexity = 1f;
            } else if(currVals.get(i).complexity + diff[0] < 0f) {
                currVals.get(i).complexity = 0f;
            } else {
                currVals.get(i).complexity += diff[0];
            }

            if(currVals.get(i).symmetricity + diff[1] > 1f) {
                currVals.get(i).symmetricity = 1f;
            } else if(currVals.get(i).symmetricity + diff[1] < 0f) {
                currVals.get(i).symmetricity = 0f;
            } else {
                currVals.get(i).symmetricity += diff[1];
            }

            if(currVals.get(i).diagonality + diff[2] > 1f) {
                currVals.get(i).diagonality = 1f;
            } else if(currVals.get(i).diagonality + diff[2] < 0f) {
                currVals.get(i).diagonality = 0f;
            } else {
                currVals.get(i).diagonality += diff[2];
            }
        }
    }
}
