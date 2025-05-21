package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; // For confirmation dialog
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.io.File;
import java.io.FileInputStream;
// Removed unused import: java.io.FilenameFilter; // No longer used directly, using lambda
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Optional; // For ButtonType confirmation
import java.util.stream.Collectors;

public class LoadGameViewController implements NavigableController {

    @FXML
    private ListView<String> saveGamesListView;

    @FXML
    private Button loadSelectedButton;

    @FXML
    private Button deleteSelectedButton; // Added delete button

    private NavigationService navigationService;

    private static final String SAVE_GAME_EXTENSION = ".sav";
    private static final String SAVES_DIRECTORY = "saves";

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        System.out.println("LoadGameViewController initialized.");
        saveGamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Disable buttons initially
        loadSelectedButton.setDisable(true);
        deleteSelectedButton.setDisable(true);

        populateSaveGamesList();

        // Enable/Disable load and delete buttons based on selection
        saveGamesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean itemSelected = (newSelection != null && !newSelection.trim().isEmpty());
            loadSelectedButton.setDisable(!itemSelected);
            deleteSelectedButton.setDisable(!itemSelected);
        });
    }

    private void populateSaveGamesList() {
        File savesDir = new File(SAVES_DIRECTORY);
        if (!savesDir.exists() || !savesDir.isDirectory()) {
            System.out.println("Saves directory does not exist or is not a directory: " + SAVES_DIRECTORY);
            saveGamesListView.setPlaceholder(new javafx.scene.control.Label("No save files folder found."));
            saveGamesListView.setItems(FXCollections.observableArrayList());
            return;
        }

        File[] saveFiles = savesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(SAVE_GAME_EXTENSION));

        ObservableList<String> saveFileNames = FXCollections.observableArrayList();
        if (saveFiles != null && saveFiles.length > 0) {
            saveFileNames.addAll(Arrays.stream(saveFiles)
                    .map(file -> file.getName().substring(0, file.getName().length() - SAVE_GAME_EXTENSION.length()))
                    .sorted() // Optional: sort the save names
                    .collect(Collectors.toList()));
        }

        if (saveFileNames.isEmpty()) {
            saveGamesListView.setPlaceholder(new javafx.scene.control.Label("No save files found."));
        }
        saveGamesListView.setItems(saveFileNames);
    }

    @FXML
    private void handleLoadSelectedAction(ActionEvent event) {
        String selectedSaveName = saveGamesListView.getSelectionModel().getSelectedItem();
        if (selectedSaveName == null || selectedSaveName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Save Selected", "Please select a game to load.");
            return;
        }

        File saveFile = new File(SAVES_DIRECTORY + File.separator + selectedSaveName + SAVE_GAME_EXTENSION);
        if (!saveFile.exists()) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Save file not found: " + selectedSaveName + "\nPlease refresh or check the saves directory.");
            populateSaveGamesList(); // Refresh list
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            Player loadedPlayer = (Player) ois.readObject();
            GameContext.currentPlayer = loadedPlayer;

            System.out.println("Game loaded successfully: " + selectedSaveName);
            showAlert(Alert.AlertType.INFORMATION, "Game Loaded", "Successfully loaded: " + selectedSaveName);

            if (navigationService != null) {
                navigationService.navigateTo(View.GAME_WORLD);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load the selected game.\nThe file might be corrupted or incompatible.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteSelectedAction(ActionEvent event) {
        String selectedSaveName = saveGamesListView.getSelectionModel().getSelectedItem();
        if (selectedSaveName == null || selectedSaveName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Save Selected", "Please select a save file to delete.");
            return;
        }

        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Delete");
        confirmDelete.setHeaderText("Delete '" + selectedSaveName + "'?");
        confirmDelete.setContentText("Are you sure you want to permanently delete this save file?");

        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            File saveFileToDelete = new File(SAVES_DIRECTORY + File.separator + selectedSaveName + SAVE_GAME_EXTENSION);
            if (saveFileToDelete.exists()) {
                if (saveFileToDelete.delete()) {
                    showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "'" + selectedSaveName + "' has been deleted.");
                    System.out.println("Deleted save file: " + saveFileToDelete.getName());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not delete '" + selectedSaveName + "'.");
                    System.err.println("Failed to delete save file: " + saveFileToDelete.getName());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Error", "'" + selectedSaveName + "' not found. It might have already been deleted.");
            }
            populateSaveGamesList(); // Refresh the list of save games
        } else {
            System.out.println("Delete cancelled by user for: " + selectedSaveName);
        }
    }


    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button in LoadGameView clicked!");
        if (navigationService != null) {
            navigationService.navigateTo(View.MAIN_MENU);
        } else {
            System.err.println("NavigationService not set in LoadGameViewController!");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}