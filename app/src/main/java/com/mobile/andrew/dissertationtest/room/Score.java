package com.mobile.andrew.dissertationtest.room;

import androidx.room.ColumnInfo;

public class Score
{
    @ColumnInfo(name = "Complexity")
    public Float complexity;
    @ColumnInfo(name = "Symmetricity")
    public Float symmetricity;
    @ColumnInfo(name = "Diagonality")
    public Float diagonality;

    public Score(Float complexity, Float symmetricity, Float diagonality) {
        this.complexity = complexity;
        this.symmetricity = symmetricity;
        this.diagonality = diagonality;
    }

    public float[] toFloatArray() {
        return new float[] { complexity, symmetricity, diagonality };
    }
}
