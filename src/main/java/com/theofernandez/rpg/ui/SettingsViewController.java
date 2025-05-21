package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.Player; // For Player.Sex/BodyType if still used for defaults, and constants
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class SettingsViewController implements NavigableController {

    @FXML private CheckBox soundCheckBox;
    @FXML private Slider musicVolumeSlider;
    @FXML private ChoiceBox<String> difficultyChoiceBox;
    @FXML private Button saveSettingsButton;

    private NavigationService navigationService;

    private static final String CONFIG_DIR_NAME = ".MyRPGGame"; // Hidden folder in user home
    private static final String CONFIG_FILE_NAME = "config.properties";

    // Define property keys
    private static final String KEY_SOUND_ENABLED = "sound.enabled";
    private static final String KEY_MUSIC_VOLUME = "music.volume";
    private static final String KEY_GAME_DIFFICULTY = "game.difficulty";

    // Define default values
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final double DEFAULT_MUSIC_VOLUME = 75.0;
    private static final String DEFAULT_GAME_DIFFICULTY = "Normal";


    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        System.out.println("SettingsViewController initialized.");

        // Populate ChoiceBox with difficulty options
        difficultyChoiceBox.setItems(FXCollections.observableArrayList(
                "Easy", "Normal", "Hard", "Nightmare"
        ));

        loadSettings(); // Load settings when the view is initialized

        // Optional: Add listeners to react to changes (for immediate feedback or debugging)
        soundCheckBox.selectedProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("UI Sound Enabled Changed To: " + newVal)
        );
        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("UI Music Volume Changed To: " + String.format("%.0f", newVal))
        );
        difficultyChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("UI Difficulty Selected Changed To: " + newVal)
        );
    }

    private File getConfigFile() {
        String homeDir = System.getProperty("user.home");
        File configDir = new File(homeDir, CONFIG_DIR_NAME);
        if (!configDir.exists()) {
            if (configDir.mkdirs()) {
                System.out.println("Config directory created: " + configDir.getAbsolutePath());
            } else {
                System.err.println("Could not create config directory: " + configDir.getAbsolutePath());
                // Fallback to current directory if user home is not writable, though less ideal
                return new File(CONFIG_FILE_NAME);
            }
        }
        return new File(configDir, CONFIG_FILE_NAME);
    }

    private void loadSettings() {
        Properties props = new Properties();
        File configFile = getConfigFile();

        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
                System.out.println("Settings loaded from: " + configFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error loading settings from " + configFile.getAbsolutePath() + ": " + e.getMessage());
                // Proceed with defaults if loading fails
            }
        } else {
            System.out.println("Config file not found. Using default settings.");
        }

        // Apply loaded settings or defaults to UI controls
        soundCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(KEY_SOUND_ENABLED, String.valueOf(DEFAULT_SOUND_ENABLED))));
        musicVolumeSlider.setValue(Double.parseDouble(props.getProperty(KEY_MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME))));
        difficultyChoiceBox.setValue(props.getProperty(KEY_GAME_DIFFICULTY, DEFAULT_GAME_DIFFICULTY));

        // Ensure the selected difficulty is actually in the ChoiceBox list, otherwise set default
        if (difficultyChoiceBox.getItems().contains(difficultyChoiceBox.getValue())) {
            // Value is valid and present
        } else {
            System.err.println("Loaded difficulty '" + difficultyChoiceBox.getValue() + "' not in ChoiceBox items. Resetting to default.");
            difficultyChoiceBox.setValue(DEFAULT_GAME_DIFFICULTY);
        }
    }

    @FXML
    private void handleSaveSettingsAction(ActionEvent event) {
        Properties props = new Properties();

        // Get current values from UI controls
        props.setProperty(KEY_SOUND_ENABLED, String.valueOf(soundCheckBox.isSelected()));
        props.setProperty(KEY_MUSIC_VOLUME, String.valueOf(musicVolumeSlider.getValue()));
        props.setProperty(KEY_GAME_DIFFICULTY, difficultyChoiceBox.getValue());

        File configFile = getConfigFile();
        try (OutputStream output = new FileOutputStream(configFile)) {
            props.store(output, "RPGame Settings");
            System.out.println("Settings saved to: " + configFile.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Settings Saved", "Your settings have been saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving settings to " + configFile.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Could not save settings.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button in SettingsView clicked!");
        if (navigationService != null) {
            navigationService.navigateTo(View.MAIN_MENU);
        } else {
            System.err.println("NavigationService not set for SettingsViewController!");
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