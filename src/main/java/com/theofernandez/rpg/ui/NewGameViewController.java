package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Platform; // For UI updates from non-FX threads if needed (showAlert is safe)
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField; // Correct import for TextField

import java.util.Objects;
import java.util.regex.Pattern; // For more complex name validation if desired

public class NewGameViewController implements NavigableController {

    @FXML private TextField playerNameField;
    @FXML private ChoiceBox<Player.Sex> sexChoiceBox;
    @FXML private ChoiceBox<Player.BodyType> bodyTypeChoiceBox;

    private NavigationService navigationService;

    // Optional: Pattern for player name validation (e.g., alphanumeric, spaces, some special chars)
    private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 _'-]{1,25}$");


    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in NewGameViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("[NewGameVC] Initialized.");

        // Populate ChoiceBoxes with enum values and set defaults
        sexChoiceBox.setItems(FXCollections.observableArrayList(Player.Sex.values()));
        sexChoiceBox.setValue(Player.DEFAULT_PLAYER_SEX);

        bodyTypeChoiceBox.setItems(FXCollections.observableArrayList(Player.BodyType.values()));
        bodyTypeChoiceBox.setValue(Player.DEFAULT_PLAYER_BODY_TYPE);

        playerNameField.setPromptText("Enter player name (1-25 chars)");
    }

    @FXML
    private void handleStartGameAction(ActionEvent event) {
        String playerNameInput = playerNameField.getText().trim();
        Player.Sex selectedSex = sexChoiceBox.getValue();
        Player.BodyType selectedBodyType = bodyTypeChoiceBox.getValue();

        // --- Input Validation ---
        if (playerNameInput.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Player name cannot be empty.");
            playerNameField.requestFocus();
            return;
        }
        // Using a regex pattern for more specific validation (optional but good)
        if (!PLAYER_NAME_PATTERN.matcher(playerNameInput).matches()) {
            showAlert(Alert.AlertType.WARNING, "Input Error",
                    "Player name can only contain letters, numbers, spaces, underscores, hyphens, apostrophes and be 1-25 characters long.");
            playerNameField.requestFocus();
            return;
        }
        if (selectedSex == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a sex for your character.");
            sexChoiceBox.requestFocus();
            return;
        }
        if (selectedBodyType == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a body type for your character.");
            bodyTypeChoiceBox.requestFocus();
            return;
        }

        // --- Player and Game Context Setup ---
        Player newPlayer = new Player(); // Initializes with hardcoded defaults from Player constructor
        newPlayer.resetToDefaults();    // Crucially, reset all stats to their defined defaults (from Player constants)

        // Set user-chosen attributes
        newPlayer.setName(playerNameInput);
        newPlayer.setSex(selectedSex);
        newPlayer.setBodyType(selectedBodyType);
        // Other attributes like age, ethnicity etc., will remain as their defaults from resetToDefaults()
        // unless you add UI elements to customize them here.

        GameContext.setCurrentPlayer(newPlayer); // Set the newly created player as current
        GameContext.resetGameTimeToDefault();    // Reset game time for a new session

        System.out.println("[NewGameVC] New player created: " + newPlayer.getName() +
                ", Sex: " + newPlayer.getSex() +
                ", Body Type: " + newPlayer.getBodyType());
        System.out.println("[NewGameVC] Game context reset. Day: " + GameContext.currentDay + " Time: " + GameContext.currentHour + ":" + GameContext.currentMinute);


        // --- Navigation ---
        if (navigationService != null) {
            System.out.println("[NewGameVC] Navigating to Game World...");
            navigationService.navigateTo(View.GAME_WORLD);
        } else {
            String errorMessage = "NavigationService is not available. Cannot start game.";
            System.err.println("[NewGameVC] " + errorMessage);
            showAlert(Alert.AlertType.ERROR, "Critical Error", errorMessage + "\nPlease contact support or restart.");
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("[NewGameVC] Back button clicked. Navigating to Main Menu.");
        if (navigationService != null) {
            navigationService.navigateTo(View.MAIN_MENU);
        } else {
            String errorMessage = "NavigationService is not available. Cannot return to main menu.";
            System.err.println("[NewGameVC] " + errorMessage);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Platform.runLater is good practice if this could ever be called from a non-FX thread,
        // but for direct event handlers, it's usually not strictly necessary.
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null); // For simpler dialogs
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}