package com.theofernandez.rpg.ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class NavigationService {
    private final Stage primaryStage;
    private final String CSS_PATH = "/css/styles.css";

    public NavigationService(Stage primaryStage) {
        if (primaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null.");
        }
        this.primaryStage = primaryStage;
    }

    public void navigateTo(View view) {
        if (view == null) {
            System.err.println("Navigation Error: View cannot be null.");
            return;
        }
        try {
            // This is where GameWorldView.fxml is loaded
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            Parent viewRoot = loader.load(); // The error occurs here if FXML is malformed

            Object controller = loader.getController();
            if (controller instanceof NavigableController) {
                ((NavigableController) controller).setNavigationService(this);
            }

            Scene currentScene = primaryStage.getScene();
            double width = (currentScene != null) ? currentScene.getWidth() : 800;
            double height = (currentScene != null) ? currentScene.getHeight() : 600;

            Scene newScene = new Scene(viewRoot, width, height);
            applyCSS(newScene);

            primaryStage.setScene(newScene);
            primaryStage.setTitle(view.getTitle());

        } catch (IOException e) { // This catches FXML LoadException
            System.err.println("Failed to load FXML view: " + view.getFxmlPath());
            e.printStackTrace(); // This will print the detailed FXML parsing error
        } catch (NullPointerException e) {
            System.err.println("Error: FXML file not found for view: " + view + " at path: " + view.getFxmlPath());
            e.printStackTrace();
        }
    }

    private void applyCSS(Scene scene) {
        try {
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("CRITICAL ERROR: CSS file not found at " + CSS_PATH + "!");
            }
        } catch (Exception e) {
            System.err.println("Error loading CSS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}