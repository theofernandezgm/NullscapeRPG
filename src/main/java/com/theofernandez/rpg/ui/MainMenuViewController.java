package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View; // Ensure View enum is correctly imported
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert; // For showing alerts

import java.util.Objects;

public class MainMenuViewController implements NavigableController {

    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in MainMenuViewController.");
    }

    @FXML
    public void initialize() {
        // Platform.startup is not needed here; this method is called after FXML loading.
        System.out.println("MainMenuViewController initialized.");
        // No complex initialization needed for main menu typically.
    }

    @FXML
    private void handleNewGameAction(ActionEvent event) {
        if (navigationService != null) {
            System.out.println("Navigating to New Game screen...");
            navigationService.navigateTo(View.NEW_GAME);
        } else {
            handleNavigationServiceError("handleNewGameAction");
        }
    }

    @FXML
    private void handleLoadGameAction(ActionEvent event) {
        if (navigationService != null) {
            System.out.println("Navigating to Load Game screen...");
            navigationService.navigateTo(View.LOAD_GAME);
        } else {
            handleNavigationServiceError("handleLoadGameAction");
        }
    }

    @FXML
    private void handleSettingsAction(ActionEvent event) {
        if (navigationService != null) {
            System.out.println("Navigating to Settings screen...");
            navigationService.navigateTo(View.SETTINGS);
        } else {
            handleNavigationServiceError("handleSettingsAction");
        }
    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        System.out.println("Exit button clicked. Closing application.");
        // Consider adding a confirmation dialog before exiting
        // Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
        // confirmExit.setTitle("Confirm Exit");
        // confirmExit.setHeaderText(null);
        // Optional<ButtonType> result = confirmExit.showAndWait();
        // if (result.isPresent() && result.get() == ButtonType.YES) {
        // Platform.exit();
        // System.exit(0); // Ensures JVM termination if Platform.exit() doesn't suffice
        // }
        Platform.exit();
        System.exit(0); // Added for good measure if Platform.exit() has issues in some environments
    }

    private void handleNavigationServiceError(String methodName) {
        String errorMessage = "NavigationService is not available in " + methodName + ". Cannot navigate.";
        System.err.println(errorMessage);
        // Show an alert to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText("Critical Application Error");
        alert.setContentText(errorMessage + "\nPlease restart the application.");
        alert.showAndWait();
    }
}