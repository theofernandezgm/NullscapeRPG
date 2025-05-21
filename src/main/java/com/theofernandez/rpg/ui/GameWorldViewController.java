package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.engine.PlayerStatEngine; // Import the engine
import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GameWorldViewController implements NavigableController {

    // --- FXML Fields ---
    @FXML private Label welcomeLabel;
    @FXML private Label timeLabel;
    @FXML private Label playerNameLabel;
    @FXML private Label playerAgeLabel;
    @FXML private Label playerSexLabel;
    @FXML private Label playerHealthLabel;
    @FXML private Label playerIntelligenceLabel;
    @FXML private Label playerEnduranceLabel;
    @FXML private Label hungerLabel;
    @FXML private Label thirstLabel;
    @FXML private Label fatigueLabel;
    @FXML private ListView<String> inventoryListView;
    @FXML private TextArea eventLogArea;
    // @FXML private VBox actionChoicesBox; // Only if direct manipulation needed

    private NavigationService navigationService;
    private Player currentPlayer;
    private Random random = new Random();
    private PlayerStatEngine statEngine; // Instance of the engine

    private static final String SAVE_GAME_EXTENSION = ".sav";
    private static final String SAVES_DIRECTORY = "saves";

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        System.out.println("GameWorldViewController initialized.");
        this.currentPlayer = GameContext.currentPlayer;
        this.statEngine = new PlayerStatEngine(); // Initialize the engine

        if (this.currentPlayer != null) {
            welcomeLabel.setText("Welcome, " + currentPlayer.getName() + "!");
            logEvent("You are in " + generateLocationDescription() + ".");
            // Process initial state interdependencies
            statEngine.processPlayerStateChanges(currentPlayer);
        } else {
            welcomeLabel.setText("Welcome! (No player data loaded)");
            logEvent("Error: Could not load player data.");
        }
        updateUIDisplays(); // Update UI with potentially modified stats

        File savesDir = new File(SAVES_DIRECTORY);
        if (!savesDir.exists()) {
            if (savesDir.mkdirs()) { System.out.println("Saves directory created."); }
            else { System.err.println("Failed to create saves directory."); }
        }
    }

    private void updateUIDisplays() {
        updatePlayerInfo();
        updateTimeDisplay();
        updateInventoryDisplay();
        // Potentially update mood display if you add a label for it
    }

    private void updatePlayerInfo() {
        if (this.currentPlayer == null) {
            playerNameLabel.setText("N/A"); playerAgeLabel.setText("N/A"); playerSexLabel.setText("N/A");
            playerHealthLabel.setText("N/A");
            if (playerIntelligenceLabel != null) playerIntelligenceLabel.setText("N/A");
            if (playerEnduranceLabel != null) playerEnduranceLabel.setText("N/A");
            hungerLabel.setText("N/A"); thirstLabel.setText("N/A"); fatigueLabel.setText("N/A");
            return;
        }

        playerNameLabel.setText(currentPlayer.getName());
        playerAgeLabel.setText(String.valueOf(currentPlayer.getAge()));
        playerSexLabel.setText(currentPlayer.getSex() != null ? currentPlayer.getSex().toString() : "N/A");
        playerHealthLabel.setText(currentPlayer.getHealth() + " / " + Player.DEFAULT_PLAYER_HEALTH);
        if (playerIntelligenceLabel != null) playerIntelligenceLabel.setText(String.valueOf(currentPlayer.getIntelligence()));
        if (playerEnduranceLabel != null) playerEnduranceLabel.setText(String.valueOf(currentPlayer.getEndurance()));
        hungerLabel.setText(currentPlayer.getHunger() + " / " + Player.MAX_STAT_VALUE_PERCENTAGE);
        thirstLabel.setText(currentPlayer.getThirst() + " / " + Player.MAX_STAT_VALUE_PERCENTAGE);
        fatigueLabel.setText(currentPlayer.getFatiguePercent() + " / " + Player.MAX_STAT_VALUE_PERCENTAGE);
    }

    private void updateInventoryDisplay() {
        if (currentPlayer != null && inventoryListView != null) {
            inventoryListView.setItems(FXCollections.observableArrayList(currentPlayer.getInventory()));
            inventoryListView.setPlaceholder(new Label(currentPlayer.getInventory().isEmpty() ? "Inventory is Empty" : ""));
        } else if (inventoryListView != null) {
            inventoryListView.setItems(FXCollections.observableArrayList());
            inventoryListView.setPlaceholder(new Label("Inventory N/A"));
        }
    }

    private void updateTimeDisplay() {
        if (timeLabel != null) {
            timeLabel.setText(GameContext.getFormattedTime());
        }
    }

    private String generateLocationDescription() {
        String[] locations = {"a quiet place", "an open field", "the edge of a dark wood"};
        return locations[random.nextInt(locations.length)];
    }

    private void logEvent(String message) {
        if (eventLogArea != null) {
            Platform.runLater(() -> {
                eventLogArea.appendText(message + "\n\n");
                eventLogArea.setScrollTop(Double.MAX_VALUE);
            });
        }
    }

    @FXML
    private void handleExploreAction(ActionEvent event) {
        if (currentPlayer == null) {
            logEvent("Cannot explore without a player.");
            return;
        }

        int timePassedMinutes = 30 + random.nextInt(30); // Explore takes 30-59 minutes

        // 1. Log action and advance game time
        logEvent("You explore the surroundings for roughly " + timePassedMinutes + " minutes.");
        GameContext.advanceTime(timePassedMinutes);

        // 2. Apply direct consequences of the action
        currentPlayer.setFatiguePercent(currentPlayer.getFatiguePercent() + (5 + random.nextInt(6))); // Fatigue from the action of exploring

        // 3. Update player's basic needs due to time passed
        currentPlayer.updateNeeds(timePassedMinutes);

        // 4. Process cascading effects and interdependencies based on the new state
        statEngine.processPlayerStateChanges(currentPlayer);

        // 5. Log a simple outcome of exploration (can be expanded)
        String[] exploreMessages = {
                "You find nothing of particular note, but the air is fresh.",
                "The area seems calm and undisturbed.",
                "You take a moment to observe the details of your environment."
        };
        logEvent(exploreMessages[random.nextInt(exploreMessages.length)]);

        // 6. Refresh UI to show all changes
        updateUIDisplays();
    }

    // ... (handleCharacterPageAction, handleSocialsAction as before) ...
    @FXML
    private void handleCharacterPageAction(ActionEvent event) {
        System.out.println("Character Page button clicked.");
        if(navigationService != null) navigationService.navigateTo(View.CHARACTER_PAGE);
    }

    @FXML
    private void handleSocialsAction(ActionEvent event) {
        System.out.println("Socials button clicked.");
        if(navigationService != null) navigationService.navigateTo(View.SOCIALS_PAGE);
    }

    @FXML
    private void handleSaveAndExitAction(ActionEvent event) {
        // ... (existing save and exit logic from previous step, no changes needed here) ...
        System.out.println("Save & Exit button clicked from GameWorldView.");
        if (currentPlayer == null) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "No player data to save.");
            navigateToMainMenu();
            return;
        }
        String defaultSaveName = currentPlayer.getName().replaceAll("[^a-zA-Z0-9_.-]", "_");
        TextInputDialog dialog = new TextInputDialog(defaultSaveName);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your save game.\n(Using an existing name will offer to overwrite)");
        dialog.setContentText("Save Name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String saveNameInput = result.get().trim();
            String sanitizedSaveName = saveNameInput.replaceAll("[^a-zA-Z0-9_.-]", "_");
            File saveFile = new File(SAVES_DIRECTORY + File.separator + sanitizedSaveName + SAVE_GAME_EXTENSION);
            if (saveFile.exists()) {
                Alert confirmOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
                confirmOverwrite.setTitle("Confirm Overwrite");
                confirmOverwrite.setHeaderText("Save file '" + sanitizedSaveName + SAVE_GAME_EXTENSION + "' already exists.");
                confirmOverwrite.setContentText("Do you want to overwrite it?");
                Optional<ButtonType> confirmResult = confirmOverwrite.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    saveGame(sanitizedSaveName);
                } else {
                    System.out.println("Overwrite cancelled. Game not saved as '" + sanitizedSaveName + "'.");
                }
            } else {
                saveGame(sanitizedSaveName);
            }
        } else {
            System.out.println("Save dialog cancelled or empty name provided. Game not saved.");
        }
        navigateToMainMenu();
    }

    private void saveGame(String fileName) {
        // ... (existing saveGame logic) ...
        if (currentPlayer == null) { return; }
        File saveFile = new File(SAVES_DIRECTORY + File.separator + fileName + SAVE_GAME_EXTENSION);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            oos.writeObject(currentPlayer);
            System.out.println("Game saved successfully to: " + saveFile.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Game Saved", "Game saved as:\n" + fileName + SAVE_GAME_EXTENSION);
        } catch (IOException e) {
            System.err.println("Error saving game to " + saveFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Could not save the game.\n" + e.getMessage());
        }
    }

    private void navigateToMainMenu() {
        if (navigationService != null) {
            navigationService.navigateTo(View.MAIN_MENU);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // ... (existing showAlert logic) ...
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}