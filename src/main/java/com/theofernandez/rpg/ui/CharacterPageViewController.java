package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // Make sure this is used in FXML if content might overflow
import javafx.scene.layout.VBox;    // Assuming VBox is the main container in ScrollPane
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CharacterPageViewController implements NavigableController {
    private NavigationService navigationService;
    private Player currentPlayer;

    // FXML Injected Fields - Ensure these fx:id s match your FXML file.
    // If you add more stats to display, ensure corresponding Labels are added here and in FXML.
    @FXML private ScrollPane scrollPane;
    @FXML private VBox statsContainer;

    // Basic Info
    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label sexLabel;
    @FXML private Label ethnicityLabel;
    @FXML private Label heightLabel;
    @FXML private Label weightLabel;
    @FXML private Label bloodTypeLabel;
    @FXML private Label bodyTypeLabel;

    // Core Attributes - Base value & Effective value
    @FXML private Label luckLabel;                // Base Only (no temp modifier defined in Player)
    @FXML private Label beautyLabel;              // Base Only (no temp modifier defined in Player)
    @FXML private Label intelligenceLabel;        // Base
    @FXML private Label effectiveIntelligenceLabel; // Effective (Base + Temp)

    // Physical Capabilities - Base & Effective
    @FXML private Label upperBodyStrengthLabel;
    @FXML private Label effectiveUpperBodyStrengthLabel;
    @FXML private Label lowerBodyStrengthLabel;
    @FXML private Label effectiveLowerBodyStrengthLabel;
    @FXML private Label enduranceLabel;           // Base Only (no temp modifier defined in Player)
    @FXML private Label agilityLabel;
    @FXML private Label effectiveAgilityLabel;
    @FXML private Label speedLabel;
    @FXML private Label effectiveSpeedLabel;
    @FXML private Label dexterityLabel;
    @FXML private Label effectiveDexterityLabel;

    // Health & Needs (Mostly direct values, not base/effective pairs)
    @FXML private Label healthLabel;
    @FXML private Label fatigueLabel;             // Fatigue percentage
    @FXML private Label hungerLabel;              // Hunger level (higher = more satisfied)
    @FXML private Label thirstLabel;              // Thirst level (higher = more satisfied)
    @FXML private Label sleepLabel;               // Restedness level (higher = more rested)
    @FXML private Label hygieneLabel;
    @FXML private Label comfortLabel;
    @FXML private Label stressLabel;              // Stress percentage

    // Status Flags & Mood
    @FXML private Label isAliveLabel;
    @FXML private Label isConsciousLabel;
    @FXML private Label isAwakeLabel;
    @FXML private Label moodLabel;

    // Inventory
    @FXML private TextFlow inventoryTextFlow;

    // TODO: Add FXML Labels for other stats if you plan to display them:
    // Organ Integrity (Cardiovascular, Respiratory, Neural, Digestive, Immune)
    // Sensory (Base Sight, Effective Sight, Base Hearing, Effective Hearing, Smell/Taste)
    // Mental/Cognitive (Base Memory, Effective Memory, Base Focus, Effective Focus, Base Willpower, Verbal, Maths, Knowledge)
    // Emotional (Happiness, Social, Boredom, Confidence)

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in CharacterPageViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("[CharacterPageVC] Initialized.");
        this.currentPlayer = GameContext.currentPlayer;

        // Ensure UI updates are on the FX application thread.
        // displayAllStats updates many labels, so good practice.
        Platform.runLater(this::displayAllStats);
    }

    private void displayAllStats() {
        if (currentPlayer == null) {
            System.err.println("[CharacterPageVC] currentPlayer is null. Cannot display stats.");
            setAllLabelsToDefault("No player data available. Please load or start a new game.");
            // Consider disabling the back button or navigating away if player is essential for this view.
            return;
        }

        // Basic Info
        nameLabel.setText(valueOrDefault(currentPlayer.getName(), "N/A"));
        ageLabel.setText(String.valueOf(currentPlayer.getAge()));
        sexLabel.setText(valueOrDefault(currentPlayer.getSex(), "N/A", Object::toString));
        ethnicityLabel.setText(valueOrDefault(currentPlayer.getEthnicity(), "N/A"));
        heightLabel.setText(String.format("%.2f m", currentPlayer.getHeight()));
        weightLabel.setText(String.format("%.1f kg", currentPlayer.getWeight()));
        bloodTypeLabel.setText(valueOrDefault(currentPlayer.getBloodType(), "N/A", Object::toString));
        bodyTypeLabel.setText(valueOrDefault(currentPlayer.getBodyType(), "N/A", Object::toString));

        // Core Attributes
        luckLabel.setText(String.valueOf(currentPlayer.getLuck()));
        beautyLabel.setText(String.valueOf(currentPlayer.getBeauty()));
        intelligenceLabel.setText("Base: " + currentPlayer.getIntelligence());
        if (effectiveIntelligenceLabel != null) effectiveIntelligenceLabel.setText("Effective: " + currentPlayer.getEffectiveIntelligence());


        // Physical Capabilities
        upperBodyStrengthLabel.setText("Base: " + currentPlayer.getUpperBodyStrength());
        if (effectiveUpperBodyStrengthLabel != null) effectiveUpperBodyStrengthLabel.setText("Effective: " + currentPlayer.getEffectiveUpperBodyStrength());
        lowerBodyStrengthLabel.setText("Base: " + currentPlayer.getLowerBodyStrength());
        if (effectiveLowerBodyStrengthLabel != null) effectiveLowerBodyStrengthLabel.setText("Effective: " + currentPlayer.getEffectiveLowerBodyStrength());
        enduranceLabel.setText("Base: " + currentPlayer.getEndurance()); // No getEffectiveEndurance in Player.java
        agilityLabel.setText("Base: " + currentPlayer.getAgility());
        if (effectiveAgilityLabel != null) effectiveAgilityLabel.setText("Effective: " + currentPlayer.getEffectiveAgility());
        speedLabel.setText("Base: " + currentPlayer.getSpeed());
        if (effectiveSpeedLabel != null) effectiveSpeedLabel.setText("Effective: " + currentPlayer.getEffectiveSpeed());
        dexterityLabel.setText("Base: " + currentPlayer.getDexterity());
        if (effectiveDexterityLabel != null) effectiveDexterityLabel.setText("Effective: " + currentPlayer.getEffectiveDexterity());


        // Health & Needs (Presented as current value out of max, or percentage)
        healthLabel.setText(String.format("Health: %d / %d", currentPlayer.getHealth(), Player.DEFAULT_PLAYER_HEALTH));
        fatigueLabel.setText(String.format("Fatigue: %d%%", currentPlayer.getFatiguePercent() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE)); // As 0-100%
        hungerLabel.setText(String.format("Satiation: %d%%", currentPlayer.getHunger() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE)); // Hunger (0-100% satiated)
        thirstLabel.setText(String.format("Hydration: %d%%", currentPlayer.getThirst() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE)); // Thirst (0-100% hydrated)
        if (sleepLabel != null) sleepLabel.setText(String.format("Restedness: %d%%", currentPlayer.getSleep() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        if (hygieneLabel != null) hygieneLabel.setText(String.format("Hygiene: %d%%", currentPlayer.getHygiene() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        if (comfortLabel != null) comfortLabel.setText(String.format("Comfort: %d%%", currentPlayer.getComfort() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        if (stressLabel != null) stressLabel.setText(String.format("Stress: %d%%", currentPlayer.getStress() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));

        // Status Flags & Mood
        isAliveLabel.setText(String.valueOf(currentPlayer.isAlive()));
        isConsciousLabel.setText(String.valueOf(currentPlayer.isConscious()));
        isAwakeLabel.setText(String.valueOf(currentPlayer.isAwake()));
        moodLabel.setText(valueOrDefault(currentPlayer.getMood(), "N/A", Object::toString));

        // Inventory
        inventoryTextFlow.getChildren().clear(); // Clear previous items
        List<String> inventory = currentPlayer.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            inventoryTextFlow.getChildren().add(new Text("Inventory is empty."));
        } else {
            Text inventoryContent = new Text(String.join(", ", inventory));
            inventoryContent.setStyle("-fx-fill: #E0E0E0;"); // Assuming styles.css defines label color
            inventoryTextFlow.getChildren().add(inventoryContent);
        }
        System.out.println("[CharacterPageVC] Player stats displayed for: " + currentPlayer.getName());
    }

    /**
     * Helper to safely get a string value or a default if the object or its string form is null/empty.
     */
    private <T> String valueOrDefault(T value, String defaultValue, java.util.function.Function<T, String> toStringFunction) {
        if (value == null) return defaultValue;
        String strValue = toStringFunction.apply(value);
        return (strValue == null || strValue.trim().isEmpty()) ? defaultValue : strValue;
    }
    private String valueOrDefault(String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }


    private void setAllLabelsToDefault(String message) {
        // Basic Info
        nameLabel.setText(message);
        ageLabel.setText("N/A"); sexLabel.setText("N/A"); ethnicityLabel.setText("N/A");
        heightLabel.setText("N/A"); weightLabel.setText("N/A"); bloodTypeLabel.setText("N/A"); bodyTypeLabel.setText("N/A");

        // Core Attributes
        luckLabel.setText("N/A"); beautyLabel.setText("N/A");
        intelligenceLabel.setText("N/A");
        if (effectiveIntelligenceLabel != null) effectiveIntelligenceLabel.setText("N/A");

        // Physical Capabilities
        upperBodyStrengthLabel.setText("N/A");
        if (effectiveUpperBodyStrengthLabel != null) effectiveUpperBodyStrengthLabel.setText("N/A");
        lowerBodyStrengthLabel.setText("N/A");
        if (effectiveLowerBodyStrengthLabel != null) effectiveLowerBodyStrengthLabel.setText("N/A");
        enduranceLabel.setText("N/A");
        agilityLabel.setText("N/A");
        if (effectiveAgilityLabel != null) effectiveAgilityLabel.setText("N/A");
        speedLabel.setText("N/A");
        if (effectiveSpeedLabel != null) effectiveSpeedLabel.setText("N/A");
        dexterityLabel.setText("N/A");
        if (effectiveDexterityLabel != null) effectiveDexterityLabel.setText("N/A");

        // Health & Needs
        healthLabel.setText("N/A"); fatigueLabel.setText("N/A"); hungerLabel.setText("N/A");
        thirstLabel.setText("N/A");
        if(sleepLabel != null) sleepLabel.setText("N/A");
        if(hygieneLabel != null) hygieneLabel.setText("N/A");
        if(comfortLabel != null) comfortLabel.setText("N/A");
        if(stressLabel != null) stressLabel.setText("N/A");

        // Status
        isAliveLabel.setText("N/A"); isConsciousLabel.setText("N/A"); isAwakeLabel.setText("N/A"); moodLabel.setText("N/A");

        // Inventory
        inventoryTextFlow.getChildren().clear();
        inventoryTextFlow.getChildren().add(new Text(message)); // Display the error/default message
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("[CharacterPageVC] Back button clicked. Navigating to Game World.");
        if (navigationService != null) {
            navigationService.navigateTo(View.GAME_WORLD);
        } else {
            String errorMessage = "NavigationService is not available. Cannot return to game world.";
            System.err.println("[CharacterPageVC] " + errorMessage);
            showAlert(Alert.AlertType.ERROR, "Navigation Error", errorMessage);
        }
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