package com.mobile.andrew.dissertationtest.db;

public final class DistanceKanjiPair implements Comparable<DistanceKanjiPair>
{
    private Character kanji;
    private float distance;

    DistanceKanjiPair(Character kanji, float distance) {
        this.kanji = kanji;
        this.distance = distance;
    }

    public Character getKanji() {
        return kanji;
    }

    @Override
    public int compareTo(DistanceKanjiPair distanceKanjiPair) {
        return Float.compare(distance, distanceKanjiPair.distance);
    }
}
