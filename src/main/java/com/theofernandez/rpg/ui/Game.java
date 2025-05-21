package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class Game extends Application {

    private NavigationService navigationService;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Realistic Text-Based RPG Sandbox (JavaFX)"); // Initial title

        this.navigationService = new NavigationService(primaryStage);
        this.navigationService.navigateTo(View.MAIN_MENU); // Navigate to the initial view

        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(450);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}