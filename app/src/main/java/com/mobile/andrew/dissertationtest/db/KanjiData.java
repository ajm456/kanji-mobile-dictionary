package com.mobile.andrew.dissertationtest.db;

import android.os.Parcel;
import android.os.Parcelable;

public class KanjiData implements Parcelable
{
    public char kanji;
    public String jlptLevel;
    public String[] meanings, kunReadings, onReadings;
    public int numStrokes;
    public boolean favourited;

    public KanjiData(char kanji, String jlptLevel, String[] meanings, String[] kunReadings,
                     String[] onReadings, int numStrokes, boolean favourited) {
        this.kanji = kanji;
        this.jlptLevel = jlptLevel;
        this.meanings = meanings;
        this.kunReadings = kunReadings;
        this.onReadings = onReadings;
        this.numStrokes = numStrokes;
        this.favourited = favourited;
    }

    public KanjiData(KanjiData source) {
        this.kanji = source.kanji;
        this.jlptLevel = source.jlptLevel;
        this.meanings = source.meanings;
        this.kunReadings = source.kunReadings;
        this.onReadings = source.onReadings;
        this.numStrokes = source.numStrokes;
        this.favourited = source.favourited;
    }

    protected KanjiData(Parcel in) {
        kanji = (char) in.readInt();
        jlptLevel = in.readString();
        meanings = in.createStringArray();
        kunReadings = in.createStringArray();
        onReadings = in.createStringArray();
        numStrokes = in.readInt();
        favourited = in.readByte() == 1;
    }

    public static final Creator<KanjiData> CREATOR = new Creator<KanjiData>() {
        @Override
        public KanjiData createFromParcel(Parcel in) {
            return new KanjiData(in);
        }

        @Override
        public KanjiData[] newArray(int size) {
            return new KanjiData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((int) kanji);
        dest.writeString(jlptLevel);
        dest.writeStringArray(meanings);
        dest.writeStringArray(kunReadings);
        dest.writeStringArray(onReadings);
        dest.writeInt(numStrokes);
        dest.writeByte((byte) (favourited ? 1 : 0));
    }
}
