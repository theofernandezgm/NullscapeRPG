package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files; // For more robust file operations if needed (e.g., last modified time)
import java.nio.file.attribute.BasicFileAttributes; // For creation/modified time
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoadGameViewController implements NavigableController {

    @FXML private ListView<String> saveGamesListView;
    @FXML private Button loadSelectedButton;
    @FXML private Button deleteSelectedButton;

    private NavigationService navigationService;

    private static final String SAVE_GAME_EXTENSION = ".sav";
    private static final String SAVES_DIRECTORY_NAME = "saves"; // Renamed for clarity

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in LoadGameViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("[LoadGameVC] Initialized.");
        saveGamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        saveGamesListView.setPlaceholder(new Label("Scanning for save files...")); // Initial placeholder

        // Disable buttons until a save is selected
        loadSelectedButton.setDisable(true);
        deleteSelectedButton.setDisable(true);

        // Populate the list of save games. Run on FX thread if any complex UI updates are done inside.
        Platform.runLater(this::populateSaveGamesList);

        // Listener to enable/disable buttons based on selection
        saveGamesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean itemSelected = (newSelection != null && !newSelection.trim().isEmpty());
            loadSelectedButton.setDisable(!itemSelected);
            deleteSelectedButton.setDisable(!itemSelected);
        });
    }

    private void populateSaveGamesList() {
        File savesDir = new File(SAVES_DIRECTORY_NAME);

        if (!savesDir.exists()) {
            System.out.println("[LoadGameVC] Saves directory does not exist: " + savesDir.getAbsolutePath());
            if (!savesDir.mkdirs()) {
                System.err.println("[LoadGameVC] Error: Could not create saves directory at: " + savesDir.getAbsolutePath());
                saveGamesListView.setPlaceholder(new Label("Error: Cannot access or create saves folder."));
                saveGamesListView.setItems(FXCollections.emptyObservableList()); // Ensure list is empty
                return;
            }
            System.out.println("[LoadGameVC] Saves directory created: " + savesDir.getAbsolutePath());
        } else if (!savesDir.isDirectory()) {
            System.err.println("[LoadGameVC] Error: Expected a directory for saves, but found a file: " + savesDir.getAbsolutePath());
            saveGamesListView.setPlaceholder(new Label("Error: Invalid saves folder configuration."));
            saveGamesListView.setItems(FXCollections.emptyObservableList());
            return;
        }

        File[] saveFilesArray = savesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(SAVE_GAME_EXTENSION));
        ObservableList<String> saveFileNames = FXCollections.observableArrayList();

        if (saveFilesArray != null) {
            saveFileNames.addAll(Arrays.stream(saveFilesArray)
                    .filter(File::isFile) // Make sure it's actually a file
                    // Sort by last modified time, newest first. Requires careful handling of potential IOExceptions.
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .map(file -> file.getName().substring(0, file.getName().length() - SAVE_GAME_EXTENSION.length()))
                    .collect(Collectors.toList()));
        }

        if (saveFileNames.isEmpty()) {
            saveGamesListView.setPlaceholder(new Label("No save files found. Start a new game!"));
        }
        saveGamesListView.setItems(saveFileNames);
        System.out.println("[LoadGameVC] Found " + saveFileNames.size() + " save files.");
    }

    @FXML
    private void handleLoadSelectedAction(ActionEvent event) {
        String selectedSaveName = saveGamesListView.getSelectionModel().getSelectedItem();
        if (selectedSaveName == null || selectedSaveName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Action Required", "Please select a game to load.");
            return;
        }

        File saveFile = new File(SAVES_DIRECTORY_NAME + File.separator + selectedSaveName + SAVE_GAME_EXTENSION);
        if (!saveFile.exists() || !saveFile.isFile()) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Save file '" + selectedSaveName + "' not found or is invalid.\nIt may have been moved or deleted. Please refresh the list.");
            populateSaveGamesList(); // Refresh list as the file might be gone
            return;
        }

        try (FileInputStream fis = new FileInputStream(saveFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Player loadedPlayer = (Player) ois.readObject();
            // GameContext related data (like time) should ideally be part of the saved object or a separate GameState object.
            // If GameContext data (e.g., currentDay, currentHour) was saved after the Player object:
            // GameContext.currentDay = (int) ois.readObject();
            // GameContext.currentHour = (int) ois.readObject();
            // GameContext.currentMinute = (int) ois.readObject();
            // For now, we assume only Player is saved, so game time will reset or use its last known state.
            // For a proper load, GameContext time should also be restored. Let's reset it if not loaded.
            GameContext.resetGameTimeToDefault(); // Reset time as it's not part of Player save.
            // OR load from save if it was included.

            GameContext.setCurrentPlayer(loadedPlayer);

            System.out.println("[LoadGameVC] Game loaded successfully: " + selectedSaveName);
            showAlert(Alert.AlertType.INFORMATION, "Game Loaded", "Successfully loaded game: " + selectedSaveName);

            if (navigationService != null) {
                navigationService.navigateTo(View.GAME_WORLD);
            } else {
                handleNavigationError("handleLoadSelectedAction", "Cannot proceed to game world.");
            }

        } catch (IOException ioe) {
            System.err.println("[LoadGameVC] I/O Error loading game '" + selectedSaveName + "': " + ioe.getMessage());
            ioe.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not read the save file. It might be corrupted or locked.\nDetails: " + ioe.getMessage());
        } catch (ClassNotFoundException | ClassCastException cnfe_cce) { // Catch multiple exceptions
            System.err.println("[LoadGameVC] Error loading game data for '" + selectedSaveName + "': " + cnfe_cce.getMessage());
            cnfe_cce.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "The save file data is incompatible or from an older version of the game.\nDetails: " + cnfe_cce.getMessage());
        }
    }

    @FXML
    private void handleDeleteSelectedAction(ActionEvent event) {
        String selectedSaveName = saveGamesListView.getSelectionModel().getSelectedItem();
        if (selectedSaveName == null || selectedSaveName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Action Required", "Please select a save file to delete.");
            return;
        }

        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Delete Save File: '" + selectedSaveName + "'?");
        confirmDelete.setContentText("Are you sure you want to permanently delete this save file?\nThis action cannot be undone.");
        confirmDelete.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO); // Explicit buttons

        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            File saveFileToDelete = new File(SAVES_DIRECTORY_NAME + File.separator + selectedSaveName + SAVE_GAME_EXTENSION);
            if (saveFileToDelete.exists() && saveFileToDelete.isFile()) {
                if (saveFileToDelete.delete()) {
                    System.out.println("[LoadGameVC] Deleted save file: " + saveFileToDelete.getName());
                    showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Save file '" + selectedSaveName + "' has been deleted.");
                } else {
                    System.err.println("[LoadGameVC] Failed to delete save file: " + saveFileToDelete.getAbsolutePath());
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not delete '" + selectedSaveName + "'.\nThe file might be locked or protected.");
                }
            } else {
                System.out.println("[LoadGameVC] Save file to delete not found (already deleted?): " + saveFileToDelete.getAbsolutePath());
                showAlert(Alert.AlertType.WARNING, "Delete Error", "Save file '" + selectedSaveName + "' not found.\nIt may have already been deleted.");
            }
            populateSaveGamesList(); // Refresh the list after deletion attempt
        } else {
            System.out.println("[LoadGameVC] Delete operation cancelled by user for: " + selectedSaveName);
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("[LoadGameVC] Back button clicked. Navigating to Main Menu.");
        if (navigationService != null) {
            GameContext.clearCurrentPlayer(); // Clear player when going back to menu
            navigationService.navigateTo(View.MAIN_MENU);
        } else {
            handleNavigationError("handleBackButtonAction", "Cannot return to main menu.");
        }
    }

    private void handleNavigationError(String methodName, String specificMessage) {
        String errorMessage = "NavigationService is not available in " + methodName + ". " + specificMessage;
        System.err.println("[LoadGameVC] " + errorMessage);
        showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
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