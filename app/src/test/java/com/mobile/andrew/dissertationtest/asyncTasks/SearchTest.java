package com.mobile.andrew.dissertationtest.asyncTasks;

import com.mobile.andrew.dissertationtest.room.KanjiData;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class SearchTest
{
    private List<KanjiData> dummyData;

    @Before
    public void generateDummyData() {
        dummyData = new ArrayList<>(1);
        for(int i = 0; i <= 10; i++) {
            float complexity = i*0.1f;
            float symmetricity = i*0.1f;
            float diagonality = i*0.1f;
            dummyData.add(new KanjiData(Integer.toString(i), "foo", "foo", "foo", 1, "foo", false, complexity, symmetricity, diagonality));
        }
    }

    @Test
    public void testTolerance_zero() {
        float[] searchScores = new float[] { 0.5f, 0.5f, 0.5f };
        float tolerance = 0f;
        List<KanjiData> results = SearchTask.euclideanFilter(dummyData, searchScores, tolerance);

        // This should return a list of length one containing just the 5th character
        assertEquals(1, results.size());
        assertEquals(results.get(0), dummyData.get(5));
    }

    @Test
    public void testTolerance_twentyFive() {
        float[] searchScores = new float[] { 0.5f, 0.5f, 0.5f };
        float tolerance = (float)Math.sqrt(3) / 4f;
        List<KanjiData> results = SearchTask.euclideanFilter(dummyData, searchScores, tolerance);

        // This should return the characters [3, 4, 5, 6, 7]
        assertEquals(5, results.size());
        assertThat(results, containsInAnyOrder(dummyData.subList(3, 8).toArray()));
    }

    @Test
    public void testTolerance_fifty() {
        float[] searchScores = new float[] { 0.5f, 0.5f, 0.5f };
        float tolerance = (float)Math.sqrt(3) / 2f;
        List<KanjiData> results = SearchTask.euclideanFilter(dummyData, searchScores, tolerance);

        // Since our search scores are in the middle of the range, a fifty percent tolerance should
        // return everything
        assertEquals(11, results.size());
        assertThat(results, containsInAnyOrder(dummyData.toArray()));
    }

    @Test
    public void testSearchScores_lowBoundary() {
        float[] searchScoresLow = new float[] { 0f, 0f, 0f };
        float tolerance = (float)Math.sqrt(3) / 4f;
        List<KanjiData> results = SearchTask.euclideanFilter(dummyData, searchScoresLow, tolerance);

        // This should return the characters [0, 1, 2]
        assertEquals(3, results.size());
        assertThat(results, is(dummyData.subList(0, 3)));
    }

    @Test
    public void testSearchScores_highBoundary() {
        float[] searchScoresLow = new float[] { 1f, 1f, 1f };
        float tolerance = (float)Math.sqrt(3) / 4f; // 25% tolerance
        List<KanjiData> results = SearchTask.euclideanFilter(dummyData, searchScoresLow, tolerance);

        // This should return the characters [8, 9, 10]
        assertEquals(3, results.size());
        assertThat(results, containsInAnyOrder(dummyData.subList(8, 11).toArray()));
    }

    @Test
    public void testSorting() {
        float[] searchScores = new float[] { 0.51f, 0.51f, 0.51f };
        float tolerance = (float)Math.sqrt(3) / 4f; // 25% tolerance
        List<KanjiData> results = SearchTask.euclideanFilter(dummyData, searchScores, tolerance);

        // The resulting list should be: [5, 6, 4, 7, 3]
        assertEquals(dummyData.get(5), results.get(0));
        assertEquals(dummyData.get(6), results.get(1));
        assertEquals(dummyData.get(4), results.get(2));
        assertEquals(dummyData.get(7), results.get(3));
        assertEquals(dummyData.get(3), results.get(4));
    }
}