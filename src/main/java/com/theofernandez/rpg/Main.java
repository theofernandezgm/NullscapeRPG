package com.theofernandez.rpg;

import com.theofernandez.rpg.ui.Game; // Ensure correct import for your Game class
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("[Main] Application launching via com.theofernandez.rpg.Main bootstrap...");
        System.out.println("[Main] Handing off to JavaFX Application thread by calling Application.launch(Game.class, args).");

        // This is the standard way to launch a JavaFX application.
        // It will initialize the JavaFX toolkit and call the start() method of the Game class
        // on the JavaFX Application Thread.
        try {
            Application.launch(Game.class, args);
        } catch (Exception e) {
            System.err.println("[Main] CRITICAL: Exception during Application.launch(). This is highly unusual.");
            e.printStackTrace();
            // At this stage, a UI alert might not be possible if FX toolkit failed to init.
            // Exiting to prevent hung process.
            System.exit(1);
        }
    }
}