package com.theofernandez.rpg.ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects; // For Objects.requireNonNull

public class NavigationService {
    private final Stage primaryStage;
    // CSS path should be absolute from the classpath root (e.g., resources folder)
    private static final String CSS_PATH = "/css/styles.css";

    public NavigationService(Stage primaryStage) {
        this.primaryStage = Objects.requireNonNull(primaryStage, "Primary stage cannot be null in NavigationService constructor.");
    }

    public void navigateTo(View view) {
        Objects.requireNonNull(view, "View to navigate to cannot be null.");

        try {
            System.out.println("[NavigationService] Attempting to navigate to: " + view.name() + " (" + view.getFxmlPath() + ")");

            // getClass().getResource() resolves paths relative to the class's package unless absolute (starts with /)
            URL fxmlUrl = getClass().getResource(view.getFxmlPath());
            if (fxmlUrl == null) {
                System.err.println("[NavigationService] CRITICAL ERROR: FXML file not found for view: " + view.name() + " at path: " + view.getFxmlPath());
                // Optionally, show an error dialog to the user here or throw a custom exception
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent viewRoot = loader.load(); // This is where FXML LoadException can occur

            // Inject navigation service into the controller if it's Navigable
            Object controller = loader.getController();
            if (controller instanceof NavigableController) {
                ((NavigableController) controller).setNavigationService(this);
                System.out.println("[NavigationService] NavigationService injected into controller for " + view.name());
            } else if (controller == null) {
                System.out.println("[NavigationService] Warning: No controller found for FXML: " + view.getFxmlPath());
            } else {
                System.out.println("[NavigationService] Warning: Controller for " + view.name() + " does not implement NavigableController: " + controller.getClass().getName());
            }

            Scene currentScene = primaryStage.getScene();
            double width = (currentScene != null && currentScene.getWidth() > 0) ? currentScene.getWidth() : 800; // Default width
            double height = (currentScene != null && currentScene.getHeight() > 0) ? currentScene.getHeight() : 600; // Default height

            Scene newScene = new Scene(viewRoot, width, height);
            applyCSS(newScene); // Apply CSS to the new scene

            primaryStage.setScene(newScene);
            primaryStage.setTitle(view.getTitle());
            System.out.println("[NavigationService] Successfully navigated to: " + view.name());

        } catch (IOException e) { // Catches FXML LoadException (which is an IOException)
            System.err.println("[NavigationService] Failed to load FXML view: " + view.getFxmlPath());
            System.err.println("[NavigationService] Error details: " + e.getMessage());
            e.printStackTrace(); // Prints the full stack trace for debugging FXML issues
            // Consider showing a user-friendly error dialog here.
        } catch (IllegalStateException e) { // Can occur if FXML path is malformed or other FXML issues
            System.err.println("[NavigationService] IllegalStateException while loading FXML view: " + view.getFxmlPath());
            System.err.println("[NavigationService] Error details: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Catch-all for other unexpected errors during navigation
            System.err.println("[NavigationService] An unexpected error occurred during navigation to " + view.name());
            e.printStackTrace();
        }
    }

    private void applyCSS(Scene scene) {
        Objects.requireNonNull(scene, "Scene cannot be null for applying CSS.");
        try {
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("[NavigationService] CSS applied to scene: " + CSS_PATH);
            } else {
                System.err.println("[NavigationService] CRITICAL ERROR: CSS file not found at classpath location: " + CSS_PATH);
                // This usually indicates a build or packaging problem if styles.css is not in resources/css/
            }
        } catch (Exception e) { // Catch any exception during CSS loading/application
            System.err.println("[NavigationService] Error loading or applying CSS from " + CSS_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}