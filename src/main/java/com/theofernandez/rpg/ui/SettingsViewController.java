package com.theofernandez.rpg.ui;

// Removed unused import: com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Platform; // For ensuring UI updates are on the FX thread
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button; // Keep if used, though not explicitly in provided FXML fields
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Properties;

public class SettingsViewController implements NavigableController {

    @FXML private CheckBox soundCheckBox;
    @FXML private Slider musicVolumeSlider;
    @FXML private ChoiceBox<String> difficultyChoiceBox;
    // @FXML private Button saveSettingsButton; // This FXML field was in the original but not used in methods directly by this name. Assuming it triggers handleSaveSettingsAction.

    private NavigationService navigationService;

    // Configuration file constants
    private static final String CONFIG_DIR_NAME = ".NullscapeRPG"; // Game-specific hidden folder
    private static final String CONFIG_FILE_NAME = "settings.properties"; // More descriptive name

    // Property keys
    private static final String KEY_SOUND_ENABLED = "audio.sound.enabled";
    private static final String KEY_MUSIC_VOLUME = "audio.music.volume";
    private static final String KEY_GAME_DIFFICULTY = "game.difficulty";

    // Default values
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final double DEFAULT_MUSIC_VOLUME = 75.0; // Slider typically 0-100
    private static final String DEFAULT_GAME_DIFFICULTY = "Normal";
    private static final String[] DIFFICULTY_LEVELS = {"Easy", "Normal", "Hard", "Nightmare"};


    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in SettingsViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("SettingsViewController initialized.");

        // Populate ChoiceBox with difficulty options
        difficultyChoiceBox.setItems(FXCollections.observableArrayList(DIFFICULTY_LEVELS));

        // Load settings when the view is initialized
        // Ensure this happens on the FX thread if it involves UI updates directly after loading,
        // though loadSettings primarily sets control values which is fine in initialize.
        loadSettings();

        // Add listeners for immediate feedback or debugging (optional, good for development)
        soundCheckBox.selectedProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("[Settings] Sound Enabled (UI) Changed To: " + newVal)
        );
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("[Settings] Music Volume (UI) Changed To: " + String.format("%.0f", newVal.doubleValue()))
        );
        difficultyChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("[Settings] Difficulty Selected (UI) Changed To: " + newVal)
        );
    }

    private File getConfigFile() {
        String homeDir = System.getProperty("user.home");
        File configDir = new File(homeDir, CONFIG_DIR_NAME);

        if (!configDir.exists()) {
            if (configDir.mkdirs()) {
                System.out.println("[Settings] Config directory created: " + configDir.getAbsolutePath());
            } else {
                System.err.println("[Settings] Error: Could not create config directory: " + configDir.getAbsolutePath());
                // Fallback to current working directory if user home is not writable or accessible.
                // This is less ideal as it's not user-specific.
                File fallbackConfigFile = new File(CONFIG_FILE_NAME);
                System.out.println("[Settings] Warning: Using fallback config file location: " + fallbackConfigFile.getAbsolutePath());
                return fallbackConfigFile;
            }
        }
        return new File(configDir, CONFIG_FILE_NAME);
    }

    private void loadSettings() {
        Properties props = new Properties();
        File configFile = getConfigFile();

        if (configFile.exists() && configFile.isFile()) {
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
                System.out.println("[Settings] Settings loaded from: " + configFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("[Settings] Error loading settings from " + configFile.getAbsolutePath() + ": " + e.getMessage());
                // Proceed with defaults if loading fails, show alert to user.
                showAlert(Alert.AlertType.WARNING, "Load Settings Failed",
                        "Could not load settings from file. Using default values.\nError: " + e.getMessage());
            }
        } else {
            System.out.println("[Settings] Config file not found at " + configFile.getAbsolutePath() + ". Using default settings.");
            // Optionally, inform user that defaults are being used if no file exists yet.
            // showAlert(Alert.AlertType.INFORMATION, "Using Defaults", "No settings file found. Default settings will be used and saved.");
        }

        // Apply loaded settings or defaults to UI controls
        soundCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(KEY_SOUND_ENABLED, String.valueOf(DEFAULT_SOUND_ENABLED))));
        musicVolumeSlider.setValue(Double.parseDouble(props.getProperty(KEY_MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME))));

        String loadedDifficulty = props.getProperty(KEY_GAME_DIFFICULTY, DEFAULT_GAME_DIFFICULTY);
        // Ensure the loaded difficulty is valid, otherwise use default
        boolean isValidDifficulty = false;
        for (String level : DIFFICULTY_LEVELS) {
            if (level.equals(loadedDifficulty)) {
                isValidDifficulty = true;
                break;
            }
        }
        if (isValidDifficulty) {
            difficultyChoiceBox.setValue(loadedDifficulty);
        } else {
            System.err.println("[Settings] Loaded difficulty '" + loadedDifficulty + "' is not a valid option. Resetting to default: " + DEFAULT_GAME_DIFFICULTY);
            difficultyChoiceBox.setValue(DEFAULT_GAME_DIFFICULTY);
        }
    }

    @FXML
    private void handleSaveSettingsAction(ActionEvent event) {
        Properties props = new Properties();

        // Get current values from UI controls
        props.setProperty(KEY_SOUND_ENABLED, String.valueOf(soundCheckBox.isSelected()));
        props.setProperty(KEY_MUSIC_VOLUME, String.valueOf(musicVolumeSlider.getValue())); // Store precise double value
        if (difficultyChoiceBox.getValue() != null) {
            props.setProperty(KEY_GAME_DIFFICULTY, difficultyChoiceBox.getValue());
        } else {
            props.setProperty(KEY_GAME_DIFFICULTY, DEFAULT_GAME_DIFFICULTY); // Fallback if somehow null
        }


        File configFile = getConfigFile();
        try (OutputStream output = new FileOutputStream(configFile)) {
            props.store(output, "Nullscape RPG Game Settings"); // Add a comment to the properties file
            System.out.println("[Settings] Settings saved to: " + configFile.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Settings Saved", "Your settings have been saved successfully.");
        } catch (IOException e) {
            System.err.println("[Settings] Error saving settings to " + configFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace(); // Log full error for debugging
            showAlert(Alert.AlertType.ERROR, "Save Error", "Could not save settings to file.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("[Settings] Back button clicked. Navigating to Main Menu.");
        if (navigationService != null) {
            navigationService.navigateTo(View.MAIN_MENU);
        } else {
            String errorMessage = "NavigationService is not available. Cannot return to main menu.";
            System.err.println("[Settings] " + errorMessage);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Ensure alerts are shown on the JavaFX Application Thread, especially if called from non-FX event handlers
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null); // Keep header null for simpler dialogs
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}