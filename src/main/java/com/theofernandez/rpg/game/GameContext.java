package com.theofernandez.rpg.game;

public class GameContext {
    public static Player currentPlayer;

    // Basic Timekeeping
    public static int currentDay = 1;
    public static int currentHour = 8; // Game starts at 8 AM
    public static int currentMinute = 0;

    public static void advanceTime(int minutes) {
        currentMinute += minutes;
        while (currentMinute >= 60) {
            currentMinute -= 60;
            currentHour++;
        }
        while (currentHour >= 24) {
            currentHour -= 24;
            currentDay++;
        }
        // TODO: Trigger time-based events, needs updates, etc.
        System.out.println("Time advanced to: Day " + currentDay + ", " + String.format("%02d:%02d", currentHour, currentMinute));
    }

    public static String getFormattedTime() {
        return "Day " + currentDay + " - " + String.format("%02d:%02d", currentHour, currentMinute);
    }
}