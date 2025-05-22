package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert; // For showing alerts
import java.util.Objects;

public class SocialsViewController implements NavigableController {
    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in SocialsViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("SocialsViewController initialized.");
        // Currently no specific initialization needed for this placeholder view.
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (navigationService != null) {
            // Assuming GAME_WORLD is the correct view to return to from Socials.
            // If Socials can be accessed from multiple places, this might need more complex logic
            // (e.g., remembering the previous view). For now, GAME_WORLD is a common default.
            System.out.println("Navigating back to Game World from Socials screen...");
            navigationService.navigateTo(View.GAME_WORLD);
        } else {
            String errorMessage = "NavigationService is not available in handleBackButtonAction. Cannot navigate.";
            System.err.println(errorMessage);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Application Error");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }
    }

    // Placeholder for any social media link handling, e.g.:
    /*
    @FXML
    private void handleOpenTwitterLink(ActionEvent event) {
        // Requires host services if opening external links
        // Application.getHostServices().showDocument("https://twitter.com/yourgame");
        System.out.println("Open Twitter link action (not implemented).");
         showAlert(Alert.AlertType.INFORMATION, "Not Implemented", "This social link is not yet active.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    */
}