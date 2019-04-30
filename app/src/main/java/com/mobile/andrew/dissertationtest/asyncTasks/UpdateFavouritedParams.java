package com.mobile.andrew.dissertationtest.asyncTasks;

/**
 * Wrapper data class for parameters passed to {@link UpdateFavouritedTask}.
 */
public class UpdateFavouritedParams
{
    final String character;
    final int favourited; // 0 = false, 1 = true, -1 = unknown (read from the database)

    public UpdateFavouritedParams(String character, int favourited) {
        this.character = character;
        this.favourited = favourited;
    }
}
