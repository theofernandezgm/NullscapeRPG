package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Application;
import javafx.stage.Stage;
// Consider adding an import for javafx.scene.image.Image for an application icon
// import javafx.scene.image.Image;

public class Game extends Application {

    private NavigationService navigationService; // Keep as instance variable if needed later, though only used in start() here.

    private static final double DEFAULT_MIN_WIDTH = 700;  // Slightly increased for better layout
    private static final double DEFAULT_MIN_HEIGHT = 550; // Slightly increased
    private static final double INITIAL_WIDTH = 1024;     // Default initial size
    private static final double INITIAL_HEIGHT = 768;      // Default initial size

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("[Game] Application starting...");

            // Initialize the NavigationService with the primary stage
            this.navigationService = new NavigationService(primaryStage);

            // Set the initial title for the application window
            primaryStage.setTitle("Nullscape RPG"); // Consistent game title

            // Optional: Set an application icon
            // try {
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app_icon.png")));
            // System.out.println("[Game] Application icon set.");
            // } catch (Exception e) {
            // System.err.println("[Game] Warning: Could not load application icon. " + e.getMessage());
            // }

            // Set minimum dimensions for the window
            primaryStage.setMinWidth(DEFAULT_MIN_WIDTH);
            primaryStage.setMinHeight(DEFAULT_MIN_HEIGHT);

            // Set initial size of the window
            primaryStage.setWidth(INITIAL_WIDTH);
            primaryStage.setHeight(INITIAL_HEIGHT);

            // Center the stage on screen initially (optional, but good UX)
            primaryStage.centerOnScreen();

            // Navigate to the initial view (Main Menu)
            // This will also set the scene and apply CSS via NavigationService
            this.navigationService.navigateTo(View.MAIN_MENU);

            // Show the primary stage
            primaryStage.show();
            System.out.println("[Game] Primary stage shown. Application is running.");

        } catch (Exception e) {
            // Catch any unexpected errors during application startup
            System.err.println("[Game] CRITICAL ERROR during application startup: " + e.getMessage());
            e.printStackTrace();
            // Optionally, show a fatal error dialog to the user before exiting
            // Alert fatalErrorAlert = new Alert(Alert.AlertType.ERROR);
            // fatalErrorAlert.setTitle("Fatal Application Error");
            // fatalErrorAlert.setHeaderText("Could not start the application due to an unexpected error.");
            // fatalErrorAlert.setContentText("Details: " + e.getMessage() + "\n\nThe application will now close.");
            // fatalErrorAlert.showAndWait();
            // Platform.exit(); // Attempt graceful FX exit
            // System.exit(1);  // Force exit if critical error
        }
    }

    public static void main(String[] args) {
        System.out.println("[Game] Launching JavaFX application via Game.main()...");
        launch(args); // Calls the start() method
    }
}