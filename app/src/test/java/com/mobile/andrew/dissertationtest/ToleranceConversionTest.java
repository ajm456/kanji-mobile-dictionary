package com.mobile.andrew.dissertationtest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToleranceConversionTest
{
    private final static float PERCENT_MIN = 0f;
    private final static float PERCENT_MAX = 1f;
    private final static int PROGRESS_MAX = 10;

    @Test
    public void testZeroProgress() {
        float result = MathUtils.percentageToEuclidean(PERCENT_MIN, PERCENT_MAX, 0, PROGRESS_MAX);
        // A progress of zero should always give zero tolerance
        assertEquals(0f, result, 0f);
    }

    @Test
    public void testHalfProgress() {
        float result = MathUtils.percentageToEuclidean(PERCENT_MIN, PERCENT_MAX, 5, PROGRESS_MAX);
        // Since the max tolerance is sqrt(3), 50% progress should give sqrt(3)/2
        assertEquals((float)Math.sqrt(3)/2f, result, 0.001f);
    }

    @Test
    public void testFullProgress() {
        float result = MathUtils.percentageToEuclidean(PERCENT_MIN, PERCENT_MAX, 10, PROGRESS_MAX);
        // A progress of one hundred should give the max tolerance: sqrt(3)
        assertEquals((float)Math.sqrt(3), result, 0.001f);
    }
}
