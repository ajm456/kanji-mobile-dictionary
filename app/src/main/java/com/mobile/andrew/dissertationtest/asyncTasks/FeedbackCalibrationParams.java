package com.mobile.andrew.dissertationtest.asyncTasks;

/**
 * Wrapper data class for parameters passed to {@link FeedbackCalibrationTask}.
 */
public class FeedbackCalibrationParams
{
    final float[] searchedScore;
    final String character;

    public FeedbackCalibrationParams(float[] searchedScore, String character) {
        this.searchedScore = searchedScore;
        this.character = character;
    }
}
