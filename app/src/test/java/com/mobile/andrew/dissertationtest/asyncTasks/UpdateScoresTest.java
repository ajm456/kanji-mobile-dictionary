package com.mobile.andrew.dissertationtest.asyncTasks;

import com.mobile.andrew.dissertationtest.room.KanjiData;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UpdateScoresTest
{
    @Test
    public void testUpdateScores_standard() {
        // Generate some dummy data - scores from [0,0,0] tp [0.4,0.4,0.4] in increments of 0.1
        List<KanjiData> dummyData = new ArrayList<>(5);
        for(int i = 0; i < 5; i++) {
            float complexity = i*0.1f;
            float symmetricity = i*0.1f;
            float diagonality = i*0.1f;
            dummyData.add(new KanjiData(Integer.toString(i), "foo", "foo", "foo", 1, "foo", false, complexity, symmetricity, diagonality));
        }

        // Generate a test diff value
        float[] diff = new float[] { 0.1f, 0.1f, 0.1f };

        // Apply diff to the dummy data
        FeedbackCalibrationTask.updateScores(dummyData, diff);

        // Check that all scores have increased by 0.1
        for(int i = 0; i < 5; i++) {
            assertEquals(i*0.1f + 0.1f, dummyData.get(i).complexity, 0.0001f);
            assertEquals(i*0.1f + 0.1f, dummyData.get(i).symmetricity, 0.0001f);
            assertEquals(i*0.1f + 0.1f, dummyData.get(i).diagonality, 0.0001f);
        }
    }

    @Test
    public void testUpdateScores_lowBoundary() {
        // Generate some dummy data - scores from [0,0,0] tp [0.4,0.4,0.4] in increments of 0.1
        List<KanjiData> dummyData = new ArrayList<>(5);
        for(int i = 0; i < 5; i++) {
            float complexity = i*0.1f;
            float symmetricity = i*0.1f;
            float diagonality = i*0.1f;
            dummyData.add(new KanjiData(Integer.toString(i), "foo", "foo", "foo", 1, "foo", false, complexity, symmetricity, diagonality));
        }

        // Generate a negative diff value to check that scores below 0 do not occur
        float[] diff = new float[] { -0.2f, -0.2f, -0.2f };

        // Apply diff to the dummy data
        FeedbackCalibrationTask.updateScores(dummyData, diff);

        // Check that the first two scores were clamped at [0,0,0] and that the others have
        // decreased by 0.2 across their attributes
        assertEquals(0f, dummyData.get(0).complexity, 0.0001f);
        assertEquals(0f, dummyData.get(0).symmetricity, 0.0001f);
        assertEquals(0f, dummyData.get(0).diagonality, 0.0001f);

        assertEquals(0f, dummyData.get(1).complexity, 0.0001f);
        assertEquals(0f, dummyData.get(1).symmetricity, 0.0001f);
        assertEquals(0f, dummyData.get(1).diagonality, 0.0001f);

        for(int i = 2; i < 5; i++) {
            assertEquals(i*0.1f - 0.2f, dummyData.get(i).complexity, 0.0001f);
            assertEquals(i*0.1f - 0.2f, dummyData.get(i).symmetricity, 0.0001f);
            assertEquals(i*0.1f - 0.2f, dummyData.get(i).diagonality, 0.0001f);
        }
    }

    @Test
    public void testUpdateScores_highBoundary() {
        // Generate some dummy data - scores from [0,0,0] tp [0.4,0.4,0.4] in increments of 0.1
        List<KanjiData> dummyData = new ArrayList<>(5);
        for(int i = 6; i <= 10; i++) {
            float complexity = i*0.1f;
            float symmetricity = i*0.1f;
            float diagonality = i*0.1f;
            dummyData.add(new KanjiData(Integer.toString(i), "foo", "foo", "foo", 1, "foo", false, complexity, symmetricity, diagonality));
        }

        // Generate a positive diff value to check that scores above 1 do not occur
        float[] diff = new float[] { 0.2f, 0.2f, 0.2f };

        // Apply diff to the dummy data
        FeedbackCalibrationTask.updateScores(dummyData, diff);

        // Check that the last two scores were clamped at [1,1,1] and that the others have
        // increased by 0.2 across their attributes
        for(int i = 0; i <= 2; i++) {
            assertEquals(0.6f + i*0.1f + 0.2f, dummyData.get(i).complexity, 0.0001f);
            assertEquals(0.6f + i*0.1f + 0.2f, dummyData.get(i).symmetricity, 0.0001f);
            assertEquals(0.6f + i*0.1f + 0.2f, dummyData.get(i).diagonality, 0.0001f);
        }

        assertEquals(1f, dummyData.get(3).complexity, 0.0001f);
        assertEquals(1f, dummyData.get(3).symmetricity, 0.0001f);
        assertEquals(1f, dummyData.get(3).diagonality, 0.0001f);

        assertEquals(1f, dummyData.get(4).complexity, 0.0001f);
        assertEquals(1f, dummyData.get(4).symmetricity, 0.0001f);
        assertEquals(1f, dummyData.get(4).diagonality, 0.0001f);
    }
}
