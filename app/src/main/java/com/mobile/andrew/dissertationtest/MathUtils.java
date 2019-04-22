package com.mobile.andrew.dissertationtest;

public class MathUtils
{
    public static float percentageToEuclidean(float percentMin, float percentMax, int progress, int progressMax) {
        float percentOfEuclideanMax = percentMin + (float)progress * ((percentMax - percentMin) / (float)progressMax);
        return percentOfEuclideanMax * 1.732051f;
    }
}
