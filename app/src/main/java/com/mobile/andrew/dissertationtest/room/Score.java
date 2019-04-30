package com.mobile.andrew.dissertationtest.room;

import androidx.room.ColumnInfo;

/**
 * Data entity class for scores stored in the database. The data in these objects are a subset of
 * the data in {@link KanjiData} objects.
 */
public class Score
{
    /**
     * The complexity value for this score.
     */
    @ColumnInfo(name = "Complexity")
    public Float complexity;
    /**
     * The symmetricity value for this score.
     */
    @ColumnInfo(name = "Symmetricity")
    public Float symmetricity;
    /**
     * The diagonality value for this score.
     */
    @ColumnInfo(name = "Diagonality")
    public Float diagonality;

    Score(Float complexity, Float symmetricity, Float diagonality) {
        this.complexity = complexity;
        this.symmetricity = symmetricity;
        this.diagonality = diagonality;
    }

    /**
     * Converts the individual values of this score into a single float array.
     *
     * @return  A float array of this score's values.
     */
    public float[] toFloatArray() {
        return new float[] { complexity, symmetricity, diagonality };
    }
}
