package com.mobile.andrew.dissertationtest.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * The primary data entity class for the app's database, containing info on a kanji character
 * as well as its attribute scores.
 */
@Entity(tableName = "Data")
public class KanjiData implements Parcelable
{
    /**
     * The String kanji character.
     */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Character")
    public final String character;

    /**
     * A comma-separated String representation of this character's English meanings.
     */
    @ColumnInfo(name = "Meanings")
    @NonNull
    public final String meanings;

    /**
     * A comma-separated String representation of the kun readings of this character, if it has any.
     */
    @ColumnInfo(name = "KunReadings")
    public final String kunReadings;

    /**
     * A comma-separated String representation of the on readings of this character, if it has any.
     */
    @ColumnInfo(name = "OnReadings")
    public final String onReadings;

    /**
     * The integer number of strokes required to draw this character.
     */
    @ColumnInfo(name = "NumStrokes")
    @NonNull
    public final Integer numStrokes;

    /**
     * The (unofficial) JLPT level of this character, if it has one.
     */
    @ColumnInfo(name = "JlptLevel")
    public final String jlptLevel;

    /**
     * Whether or not this character has been favourited by the user.
     */
    @ColumnInfo(name = "Favourited")
    @NonNull
    public Boolean favourited;

    /**
     * The complexity score of this kanji.
     */
    @ColumnInfo(name = "Complexity")
    @NonNull
    public Float complexity;

    /**
     * The symmetricity score of this kanji.
     */
    @ColumnInfo(name = "Symmetricity")
    @NonNull
    public Float symmetricity;

    /**
     * The diagonality score of this kanji.
     */
    @ColumnInfo(name = "Diagonality")
    @NonNull
    public Float diagonality;

    public KanjiData(@NonNull String character, @NonNull String meanings, String kunReadings,
                     String onReadings, int numStrokes, String jlptLevel, boolean favourited,
                     float complexity, float symmetricity, float diagonality) {
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

    /**
     * Constructs a {@link KanjiData} object from a {@link Parcel} object. Used to pass kanji data
     * between activities in intents.
     *
     * @param in    A parcelled {@link KanjiData} object being opened.
     */
    KanjiData(Parcel in) {
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

    /**
     * Converts the comma-separated, String representation of this kanji's English meanings into
     * a String array.
     *
     * @return  A String array containing each English meaning for this kanji.
     */
    public String[] getMeaningsArr() {
        return meanings.split(", ");
    }

    /**
     * Converts the comma-separated, String representation of this kanji's kun readings into a
     * String array. Returns an empty array if this kanji doesn't have any kun readings.
     *
     * @return  A String array containing each kun reading for this kanji.
     */
    public String[] getKunReadingsArr() {
        String[] kunReadingsArr;
        if(kunReadings != null) {
            kunReadingsArr = kunReadings.split(",");
        } else {
            kunReadingsArr = new String[] {};
        }
        return kunReadingsArr;
    }

    /**
     * Converts the comma-separated, String representation of this kanji's kun readings into a
     * String array. Returns an empty array if this kanji doesn't have any on readings.
     *
     * @return  A String array containing each on reading for this kanji.
     */
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
        // If the given object has the same reference, it is equal
        if(obj == this) {
            return true;
        }

        // If the given object is not a KanjiData object, it cannot be equal
        if(!(obj instanceof KanjiData)) {
            return false;
        }

        KanjiData d = (KanjiData) obj;

        // All fields must be the same for this and the given object to be equal
        return character.equals(d.character) &&
                meanings.equals(d.meanings) &&
                ((kunReadings == null && d.kunReadings == null) || Objects.requireNonNull(kunReadings).equals(d.kunReadings)) &&
                ((onReadings == null && d.onReadings == null) || Objects.requireNonNull(onReadings).equals(d.onReadings)) &&
                numStrokes.equals(d.numStrokes) &&
                ((jlptLevel == null && d.jlptLevel == null) || Objects.requireNonNull(jlptLevel).equals(d.jlptLevel)) &&
                favourited.equals(d.favourited) &&
                complexity.equals(d.complexity) &&
                symmetricity.equals(d.symmetricity) &&
                diagonality.equals(d.diagonality);
    }
}
