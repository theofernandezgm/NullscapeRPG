package com.theofernandez.rpg.game;

public class GameContext {

    public static Player currentPlayer; // Central access to the player

    // --- Timekeeping ---
    public static int currentDay = 1;
    public static int currentHour = 8; // Default game start time: 8 AM
    public static int currentMinute = 0;

    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_START_HOUR = 8;
    private static final int DEFAULT_START_MINUTE = 0;

    /**
     * Advances the game time by the specified number of minutes.
     * Handles rollovers for hours and days.
     *
     * @param minutesToAdvance The number of minutes to advance time by. Must be non-negative.
     */
    public static void advanceTime(int minutesToAdvance) {
        if (minutesToAdvance < 0) {
            System.err.println("[GameContext] Error: Cannot advance time by negative minutes (" + minutesToAdvance + ").");
            return;
        }
        if (minutesToAdvance == 0) {
            return; // No time passed, no change needed.
        }

        currentMinute += minutesToAdvance;

        // Handle minute overflow into hours
        while (currentMinute >= MINUTES_IN_HOUR) {
            currentMinute -= MINUTES_IN_HOUR;
            currentHour++;

            // Handle hour overflow into days
            if (currentHour >= HOURS_IN_DAY) {
                currentHour -= HOURS_IN_DAY;
                currentDay++;
                // Optional: Log new day or trigger daily events
                // System.out.println("[GameContext] A new day has dawned: Day " + currentDay);
            }
        }
        // System.out.println("[GameContext] Time advanced by " + minutesToAdvance + " min. New time: " + getFormattedTime());
    }

    /**
     * @return A string representation of the current game time.
     */
    public static String getFormattedTime() {
        return String.format("Day %d - %02d:%02d", currentDay, currentHour, currentMinute);
    }

    /**
     * Resets the game time to the default starting time (Day 1, 08:00 AM).
     * Typically used when starting a new game.
     */
    public static void resetGameTimeToDefault() {
        currentDay = DEFAULT_START_DAY;
        currentHour = DEFAULT_START_HOUR;
        currentMinute = DEFAULT_START_MINUTE;
        System.out.println("[GameContext] Game time has been reset to default start: " + getFormattedTime());
    }

    /**
     * Sets the current player for the game session.
     * @param player The player to set as current.
     */
    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    /**
     * Clears the current player, typically when returning to the main menu or ending a session.
     */
    public static void clearCurrentPlayer() {
        currentPlayer = null;
    }
}