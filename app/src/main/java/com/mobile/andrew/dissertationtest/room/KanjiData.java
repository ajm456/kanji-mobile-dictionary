package com.mobile.andrew.dissertationtest.room;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Data")
public class KanjiData implements Parcelable
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Character")
    public String character;

    @ColumnInfo(name = "Meanings")
    @NonNull
    public String meanings;

    @ColumnInfo(name = "KunReadings")
    public String kunReadings;

    @ColumnInfo(name = "OnReadings")
    public String onReadings;

    @ColumnInfo(name = "NumStrokes")
    @NonNull
    public Integer numStrokes;

    @ColumnInfo(name = "JlptLevel")
    public String jlptLevel;

    @ColumnInfo(name = "Favourited")
    @NonNull
    public Boolean favourited;

    @ColumnInfo(name = "Complexity")
    @NonNull
    public Float complexity;
    @ColumnInfo(name = "Symmetricity")
    @NonNull
    public Float symmetricity;
    @ColumnInfo(name = "Diagonality")
    @NonNull
    public Float diagonality;

    public KanjiData(@NonNull String character, @NonNull String meanings, String kunReadings, String onReadings, int numStrokes, String jlptLevel,
                     boolean favourited, float complexity,
                     float symmetricity, float diagonality) {
        this.character = character;
        this.meanings = meanings;
        this.kunReadings = kunReadings;
        this.onReadings = onReadings;
        this.numStrokes = numStrokes;
        this.jlptLevel = jlptLevel;
        this.favourited = favourited;
        this.complexity = complexity;
        this.symmetricity = symmetricity;
        this.diagonality = diagonality;
    }

    protected KanjiData(Parcel in) {
        character = Objects.requireNonNull(in.readString());
        meanings = Objects.requireNonNull(in.readString());
        kunReadings = in.readString();
        onReadings = in.readString();
        numStrokes = in.readInt();
        jlptLevel = in.readString();
        favourited = in.readByte() == 1;
        complexity = in.readFloat();
        symmetricity = in.readFloat();
        diagonality = in.readFloat();
    }

    public String[] getMeaningsArr() {
        return meanings.split(", ");
    }

    public String[] getKunReadingsArr() {
        String[] kunReadingsArr;
        if(kunReadings != null) {
            kunReadingsArr = kunReadings.split(",");
        } else {
            kunReadingsArr = new String[] {};
        }
        return kunReadingsArr;
    }

    public String[] getOnReadingsArr() {
        String[] onReadingsArr;
        if(onReadings != null) {
            onReadingsArr = onReadings.split(",");
        } else {
            onReadingsArr = new String[] {};
        }
        return onReadingsArr;
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
        dest.writeString(character);
        dest.writeString(meanings);
        dest.writeString(kunReadings);
        dest.writeString(onReadings);
        dest.writeInt(numStrokes);
        dest.writeString(jlptLevel);
        dest.writeByte((byte) (favourited ? 1 : 0));
        dest.writeFloat(complexity);
        dest.writeFloat(symmetricity);
        dest.writeFloat(diagonality);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(!(obj instanceof KanjiData)) {
            return false;
        }

        KanjiData d = (KanjiData) obj;

        return character.equals(d.character) &&
                meanings.equals(d.meanings) &&
                ((kunReadings == null && d.kunReadings == null) || kunReadings.equals(d.kunReadings)) &&
                ((onReadings == null && d.onReadings == null) || onReadings.equals(d.onReadings)) &&
                numStrokes.equals(d.numStrokes) &&
                ((jlptLevel == null && d.jlptLevel == null) || jlptLevel.equals(d.jlptLevel)) &&
                favourited.equals(d.favourited) &&
                complexity.equals(d.complexity) &&
                symmetricity.equals(d.symmetricity) &&
                diagonality.equals(d.diagonality);
    }
}
