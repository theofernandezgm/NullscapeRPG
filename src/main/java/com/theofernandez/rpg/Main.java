package com.theofernandez.rpg;

import com.theofernandez.rpg.ui.Game;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("Application launching via com.theofernandez.rpg.Main...");

        Application.launch(Game.class, args);
    }
}