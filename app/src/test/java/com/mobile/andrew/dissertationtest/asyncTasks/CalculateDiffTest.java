package com.mobile.andrew.dissertationtest.asyncTasks;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class CalculateDiffTest
{
    @Test
    public void testCalculateDiff_standard() {
        float[] searchedScore = new float[] { 0.5f, 0.5f, 0.5f };
        float[] characterScore = new float[] { 0.3f, 0.5f, 0.7f };
        float dampening = FeedbackCalibrationTask.FEEDBACK_DAMPENING;
        float[] diff = FeedbackCalibrationTask.calculateDiffVector(searchedScore, characterScore);

        // calculateDiff() calculates what adjustments need to be made to bring the character scores
        // closer to the searched score, so the result should be equal to
        // dampening * (searchedScore - characterScore)
        assertArrayEquals(new float[] { dampening * 0.2f, 0f, dampening * -0.2f }, diff, 0.001f);
    }

    @Test
    public void testCalculateDiff_disabledAttributes() {
        float[] searchedScore = new float[] { -1f, 0.5f, -1f };
        float[] characterScore = new float[] { 0.3f, 0.6f, 0.7f };
        float dampening = FeedbackCalibrationTask.FEEDBACK_DAMPENING;
        float[] diff = FeedbackCalibrationTask.calculateDiffVector(searchedScore, characterScore);

        // Any attributes with a searched value of -1 are treated as disabled and so not taken into
        // consideration when calculating how to update the database scores
        assertArrayEquals(new float[] { 0f, dampening * -0.1f, 0f }, diff, 0.001f);
    }
}
