package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.engine.PlayerStatEngine;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern; // For save name sanitization

public class GameWorldViewController implements NavigableController {

    // FXML Fields - ensure these match your FXML
    @FXML private Label welcomeLabel;
    @FXML private Label timeLabel;
    @FXML private Label playerNameLabel;
    @FXML private Label playerAgeLabel;
    @FXML private Label playerSexLabel;
    @FXML private Label playerHealthLabel;
    @FXML private Label playerMoodLabel; // Added in a previous review pass
    // Consider adding labels for key "effective" stats if important for quick view
    // @FXML private Label effectiveStrengthLabel;
    // @FXML private Label effectiveFocusLabel;
    @FXML private Label hungerLabel;    // Will show satiation %
    @FXML private Label thirstLabel;    // Will show hydration %
    @FXML private Label fatigueLabel;   // Will show fatigue %
    @FXML private ListView<String> inventoryListView;
    @FXML private TextArea eventLogArea;

    private NavigationService navigationService;
    private Player currentPlayer;
    private final Random random = new Random(); // final as it's initialized once
    private PlayerStatEngine statEngine;

    private static final String SAVE_GAME_EXTENSION = ".sav";
    private static final String SAVES_DIRECTORY_NAME = "saves";
    private static final Pattern SAVE_NAME_SANITIZER_PATTERN = Pattern.compile("[^a-zA-Z0-9_.-]");


    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in GameWorldViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("[GameWorldVC] Initialized.");
        this.currentPlayer = GameContext.currentPlayer;
        this.statEngine = new PlayerStatEngine(); // Initialize the stat processing engine

        if (this.currentPlayer != null) {
            welcomeLabel.setText("Welcome back, " + currentPlayer.getName() + "!"); // Or "Welcome," for new game
            logEventToUI("You are in " + generateLocationDescription() + ".");
            // Initial state processing for the player (e.g., after loading a game or starting new)
            statEngine.processPlayerStateChanges(currentPlayer);
        } else {
            // This state should ideally be prevented by proper game flow (e.g., must new/load game first)
            welcomeLabel.setText("Error: No Player Loaded!");
            logEventToUI("CRITICAL ERROR: No player data found. Please start a new game or load a valid save file.");
            // Consider disabling action buttons if currentPlayer is null
        }
        // Ensure UI updates are on the FX application thread.
        Platform.runLater(this::updateAllUIDisplays);
    }

    private void updateAllUIDisplays() {
        if (currentPlayer == null) {
            // Set UI to a default "no player" state
            playerNameLabel.setText("Player: N/A");
            playerAgeLabel.setText("Age: N/A");
            playerSexLabel.setText("Sex: N/A");
            playerHealthLabel.setText("Health: N/A");
            if (playerMoodLabel != null) playerMoodLabel.setText("Mood: N/A");
            hungerLabel.setText("Satiation: N/A");
            thirstLabel.setText("Hydration: N/A");
            fatigueLabel.setText("Fatigue: N/A");
            if (inventoryListView != null) {
                inventoryListView.setItems(FXCollections.emptyObservableList());
                inventoryListView.setPlaceholder(new Label("Inventory Empty/Unavailable"));
            }
            timeLabel.setText(GameContext.getFormattedTime()); // Time can still update
            return;
        }

        updatePlayerInfoOnUI();
        updateTimeDisplayOnUI();
        updateInventoryDisplayOnUI();
    }

    private void updatePlayerInfoOnUI() {
        if (this.currentPlayer == null) return; // Should be caught by updateAllUIDisplays

        playerNameLabel.setText("Name: " + currentPlayer.getName());
        playerAgeLabel.setText("Age: " + currentPlayer.getAge());
        playerSexLabel.setText("Sex: " + (currentPlayer.getSex() != null ? currentPlayer.getSex().toString() : "N/A"));
        playerHealthLabel.setText(String.format("Health: %d / %d", currentPlayer.getHealth(), Player.DEFAULT_PLAYER_HEALTH));

        if (playerMoodLabel != null) {
            playerMoodLabel.setText("Mood: " + (currentPlayer.getMood() != null ? currentPlayer.getMood().toString() : "UNKNOWN"));
        }

        // Display needs as user-friendly percentages (0-100%)
        hungerLabel.setText(String.format("Satiation: %d%%", currentPlayer.getHunger() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        thirstLabel.setText(String.format("Hydration: %d%%", currentPlayer.getThirst() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        fatigueLabel.setText(String.format("Fatigue: %d%%", currentPlayer.getFatiguePercent() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
    }

    private void updateInventoryDisplayOnUI() {
        if (currentPlayer != null && inventoryListView != null) {
            inventoryListView.setItems(FXCollections.observableArrayList(currentPlayer.getInventory()));
            inventoryListView.setPlaceholder(new Label(currentPlayer.getInventory().isEmpty() ? "Inventory is Empty" : "Error loading inventory."));
        } else if (inventoryListView != null) {
            inventoryListView.setItems(FXCollections.emptyObservableList());
            inventoryListView.setPlaceholder(new Label("Inventory Unavailable"));
        }
    }

    private void updateTimeDisplayOnUI() {
        if (timeLabel != null) {
            timeLabel.setText("Time: " + GameContext.getFormattedTime());
        }
    }

    private String generateLocationDescription() {
        String[] locations = {"a quiet, desolate ruin", "an open, windswept field", "the shadowy edge of a dark, ancient wood", "a crumbling urban street", "a still, murky swamp edge"};
        return locations[random.nextInt(locations.length)];
    }

    private void logEventToUI(String message) {
        if (eventLogArea != null) {
            Platform.runLater(() -> { // Ensure UI update is on the FX Application Thread
                // String timestamp = GameContext.getFormattedTime(); // Timestamp can make log verbose, optional
                // eventLogArea.appendText("[" + timestamp + "] " + message + "\n\n");
                eventLogArea.appendText(message + "\n\n"); // Simpler log entry
                eventLogArea.setScrollTop(Double.MAX_VALUE); // Auto-scroll to the bottom
            });
        } else {
            System.out.println("[GameWorld Event] " + message); // Fallback to console if TextArea not available
        }
    }

    @FXML
    private void handleExploreAction(ActionEvent event) {
        if (currentPlayer == null || !currentPlayer.isAlive()) {
            logEventToUI(currentPlayer == null ? "Cannot explore: No player data." : "You are not in a state to explore (deceased).");
            return;
        }
        if (!currentPlayer.isConscious()) {
            logEventToUI("You are unconscious and cannot explore.");
            return;
        }

        logEventToUI("You decide to explore the surroundings...");
        int timePassedMinutes = 30 + random.nextInt(31); // Explore takes 30-60 minutes

        // 1. Advance game time (affects needs)
        GameContext.advanceTime(timePassedMinutes);
        logEventToUI("About " + timePassedMinutes + " minutes pass as you search.");

        // 2. Apply direct consequences of the action (e.g., fatigue)
        int baseFatigueGain = 5; // Base fatigue for exploring
        // More fatigue if player has low endurance (using effective endurance if available, else base)
        int enduranceValue = currentPlayer.getEndurance(); // Player.java doesn't have getEffectiveEndurance
        int fatigueFromEndurance = Math.max(0, (Player.MAX_STAT_VALUE / 2 - enduranceValue) / (Player.MAX_STAT_VALUE / 20)); // Example scaling
        currentPlayer.setFatiguePercent(currentPlayer.getFatiguePercent() + baseFatigueGain + random.nextInt(3) + fatigueFromEndurance);

        // 3. Update player's general needs based on time passed
        currentPlayer.updateNeeds(timePassedMinutes);

        // 4. Process ALL cascading effects from new state (needs, fatigue, etc.) using the StatEngine
        statEngine.processPlayerStateChanges(currentPlayer);

        // 5. Determine and log outcome of exploration (can be expanded into complex events)
        String[] exploreOutcomes = {
                "You find nothing of particular note, but the air grows colder.",
                "The area seems eerily silent and undisturbed.",
                "You take a moment to observe the desolate details of your environment. A sense of unease settles in.",
                "A strange, unidentifiable sound echoes in the distance, putting you on edge.",
                "You discover a small, tarnished locket. It seems to hold no monetary value, only echoes of a forgotten past."
        };
        logEventToUI(exploreOutcomes[random.nextInt(exploreOutcomes.length)]);

        // Example: Small chance to find a common item
        if (random.nextDouble() < 0.15) { // 15% chance
            String itemFound = "Old Rag"; // Placeholder
            currentPlayer.addItem(itemFound);
            logEventToUI("Tucked away in a corner, you find an " + itemFound + ".");
        }

        // 6. Refresh UI to show all changes (health, needs, mood, inventory, time)
        updateAllUIDisplays();

        // 7. Check for game over states or critical conditions post-action
        if (!currentPlayer.isAlive()) {
            logEventToUI("Your exploration has led to your demise. Your journey ends here.");
            showAlert(Alert.AlertType.WARNING, "You Have Perished", "Your character has died.");
            // Consider disabling action buttons or navigating to a game over screen/main menu
            // For now, player can technically click buttons but actions are blocked if !isAlive.
        } else if (!currentPlayer.isConscious()) {
            logEventToUI("You have fallen unconscious!");
            showAlert(Alert.AlertType.WARNING, "Unconscious", "Your character has lost consciousness.");
        }
    }

    @FXML
    private void handleCharacterPageAction(ActionEvent event) {
        System.out.println("[GameWorldVC] Character Page button clicked.");
        if (navigationService != null) {
            navigationService.navigateTo(View.CHARACTER_PAGE);
        } else {
            handleNavigationError("handleCharacterPageAction", "Cannot open character page.");
        }
    }

    @FXML
    private void handleSocialsAction(ActionEvent event) {
        System.out.println("[GameWorldVC] Socials button clicked.");
        // if(navigationService != null) navigationService.navigateTo(View.SOCIALS_PAGE);
        showAlert(Alert.AlertType.INFORMATION, "Feature Not Available", "Social interaction features are planned for a future update.");
    }

    @FXML
    private void handleSaveAndExitAction(ActionEvent event) {
        System.out.println("[GameWorldVC] Save & Exit button clicked.");
        if (currentPlayer == null) {
            showAlert(Alert.AlertType.WARNING, "Save Error", "No active game to save. Returning to Main Menu.");
            navigateToMainMenu(true); // Force exit to menu
            return;
        }

        String defaultSaveName = SAVE_NAME_SANITIZER_PATTERN.matcher(currentPlayer.getName()).replaceAll("_") + "_save";
        TextInputDialog dialog = new TextInputDialog(defaultSaveName);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your save file.\n(Using an existing name will offer to overwrite it).");
        dialog.setContentText("Save Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String saveNameInput = result.get().trim();
            String sanitizedSaveName = SAVE_NAME_SANITIZER_PATTERN.matcher(saveNameInput).replaceAll("_");

            if (sanitizedSaveName.isEmpty() || sanitizedSaveName.equals("_")) { // Ensure name isn't just underscores
                showAlert(Alert.AlertType.ERROR, "Invalid Save Name", "Save name cannot be empty or invalid characters only. Please try again.");
                return; // Stay on GameWorldView, do not exit
            }

            File saveFile = new File(SAVES_DIRECTORY_NAME + File.separator + sanitizedSaveName + SAVE_GAME_EXTENSION);
            boolean proceedWithSave = true;

            if (saveFile.exists()) {
                Alert confirmOverwrite = new Alert(Alert.AlertType.CONFIRMATION);
                confirmOverwrite.setTitle("Confirm Overwrite");
                confirmOverwrite.setHeaderText("Save file '" + sanitizedSaveName + SAVE_GAME_EXTENSION + "' already exists.");
                confirmOverwrite.setContentText("Do you want to overwrite it?");
                confirmOverwrite.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> confirmResult = confirmOverwrite.showAndWait();
                if (confirmResult.isEmpty() || confirmResult.get() != ButtonType.YES) {
                    proceedWithSave = false;
                    logEventToUI("Save overwritten cancelled for '" + sanitizedSaveName + "'.");
                }
            }

            if (proceedWithSave) {
                saveGameData(sanitizedSaveName); // This method shows its own success/failure alert
            }
            // Regardless of save success/failure (if attempted), proceed to exit if user confirmed a name.
            navigateToMainMenu(true);

        } else {
            logEventToUI("Save dialog cancelled or empty name provided. Game not saved. Continuing current session.");
            // User cancelled the save dialog, so they likely want to continue playing. Do NOT exit.
        }
    }

    private void saveGameData(String fileName) {
        if (currentPlayer == null || fileName == null || fileName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Critical data missing. Cannot save game.");
            return;
        }

        File savesDir = new File(SAVES_DIRECTORY_NAME);
        if (!savesDir.exists() && !savesDir.mkdirs()) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Could not create saves directory: " + savesDir.getAbsolutePath());
            return;
        }

        File saveFile = new File(savesDir, fileName + SAVE_GAME_EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(saveFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(currentPlayer);
            // TODO: Future: Save GameContext (time, etc.) as well, perhaps in a wrapper GameState object.
            // oos.writeObject(GameContext.currentDay); // Example
            System.out.println("[GameWorldVC] Game saved successfully to: " + saveFile.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Game Saved", "Game saved as: " + fileName + SAVE_GAME_EXTENSION);
        } catch (IOException e) {
            System.err.println("[GameWorldVC] Error saving game to " + saveFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Could not save the game data.\n" + e.getMessage());
        }
    }

    private void navigateToMainMenu(boolean clearPlayer) {
        if (navigationService != null) {
            if (clearPlayer) {
                GameContext.clearCurrentPlayer(); // Clear current player context
            }
            navigationService.navigateTo(View.MAIN_MENU);
        } else {
            handleNavigationError("navigateToMainMenu", "Cannot return to Main Menu.");
        }
    }

    private void handleNavigationError(String methodName, String specificMessage) {
        String errorMessage = "NavigationService is not available in " + methodName + ". " + specificMessage;
        System.err.println("[GameWorldVC] " + errorMessage);
        showAlert(Alert.AlertType.ERROR, "Critical Navigation Error", errorMessage + "\nPlease restart the application if issues persist.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}