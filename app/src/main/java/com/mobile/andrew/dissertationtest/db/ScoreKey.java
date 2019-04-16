package com.mobile.andrew.dissertationtest.db;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Immutable container used as the keys in the kanji dictionary Map.
 *
 * Since kanji characters are identified by their attribute scores, this immutable class is
 * required to ensure that mappings are preserved in the kanji dictionary.
 */
final class ScoreKey implements Serializable
{
    private final float complexity, symmetricity, strokeCurviness;

    ScoreKey(Float[] scores) {
        complexity = scores[0];
        symmetricity = scores[1];
        strokeCurviness = scores[2];
    }

    ScoreKey(ScoreKey old, Float[] adjust) {
        if(old.complexity + adjust[0] > 1) {
            complexity = 1;
        } else if(old.complexity + adjust[0] < 0) {
            complexity = 0;
        } else {
            complexity = old.complexity + adjust[0];
        }

        if(old.symmetricity + adjust[1] > 1) {
            symmetricity = 1;
        } else if(old.symmetricity + adjust[1] < 0) {
            symmetricity = 0;
        } else {
            symmetricity = old.symmetricity + adjust[1];
        }

        if(old.strokeCurviness + adjust[2] > 1) {
            strokeCurviness = 1;
        } else if(old.strokeCurviness + adjust[2] < 0) {
            strokeCurviness = 0;
        } else {
            strokeCurviness = old.strokeCurviness + adjust[2];
        }
    }

    /**
     * Convenience method allowing ScoreKey objects to be treated similarly to lists. Retrieves the
     * attribute score corresponding to the submitted index.
     *
     * @param i     The index of the attribute score being retrieved.
     * @return      The attribute score at the given index.
     */
    float get(int i) {
        switch(i) {
            case 0:
                return complexity;
            case 1:
                return symmetricity;
            case 2:
                return strokeCurviness;
            default:
                throw new ArrayIndexOutOfBoundsException("Tried to access a kanji attribute that doesn't exist");
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new float[] {complexity, symmetricity, strokeCurviness});
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ScoreKey) &&
                ((ScoreKey) obj).complexity == complexity &&
                ((ScoreKey) obj).symmetricity == symmetricity &&
                ((ScoreKey) obj).strokeCurviness == strokeCurviness;
    }

    @Override
    public String toString() {
        return "[" + complexity + ", " + symmetricity + ", " + strokeCurviness + "]";
    }
}
