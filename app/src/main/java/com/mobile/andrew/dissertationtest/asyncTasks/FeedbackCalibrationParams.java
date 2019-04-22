package com.mobile.andrew.dissertationtest.asyncTasks;

public class FeedbackCalibrationParams
{
    float[] searchedScore;
    String character;

    public FeedbackCalibrationParams(float[] searchedScore, String character) {
        this.searchedScore = searchedScore;
        this.character = character;
    }
}
