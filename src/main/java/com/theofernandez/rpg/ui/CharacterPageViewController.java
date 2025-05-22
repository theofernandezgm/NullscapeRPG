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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Objects;
import java.util.function.Function; // Correct import for Function
import java.util.stream.Collectors;

public class CharacterPageViewController implements NavigableController {
    private NavigationService navigationService;
    private Player currentPlayer;

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

    // Core Attributes
    @FXML private Label luckLabel;
    @FXML private Label beautyLabel;
    @FXML private Label intelligenceLabel; // Base
    @FXML private Label effectiveIntelligenceLabel; // Effective

    // Physical Capabilities
    @FXML private Label upperBodyStrengthLabel; // Base
    @FXML private Label effectiveUpperBodyStrengthLabel; // Effective
    @FXML private Label lowerBodyStrengthLabel; // Base
    @FXML private Label effectiveLowerBodyStrengthLabel; // Effective
    @FXML private Label enduranceLabel; // Base (Player.java has no getEffectiveEndurance())
    @FXML private Label agilityLabel; // Base
    @FXML private Label effectiveAgilityLabel; // Effective
    @FXML private Label speedLabel; // Base
    @FXML private Label effectiveSpeedLabel; // Effective
    @FXML private Label dexterityLabel; // Base
    @FXML private Label effectiveDexterityLabel; // Effective

    // Health & Needs
    @FXML private Label healthLabel;
    @FXML private Label fatigueLabel;
    @FXML private Label hungerLabel;
    @FXML private Label thirstLabel;
    @FXML private Label sleepLabel;       // For Restedness
    @FXML private Label hygieneLabel;
    @FXML private Label comfortLabel;
    @FXML private Label stressLabel;

    // Status
    @FXML private Label isAliveLabel;
    @FXML private Label isConsciousLabel;
    @FXML private Label isAwakeLabel;
    @FXML private Label moodLabel;

    // Inventory
    @FXML private TextFlow inventoryTextFlow;

    // Ensure all other FXML @FXML injections for labels you might add (e.g., focus, memory) are declared here.

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = Objects.requireNonNull(navigationService, "NavigationService cannot be null in CharacterPageViewController.");
    }

    @FXML
    public void initialize() {
        System.out.println("[CharacterPageVC] Initialized.");
        this.currentPlayer = GameContext.currentPlayer;
        Platform.runLater(this::displayAllStats);
    }

    private void displayAllStats() {
        if (currentPlayer == null) {
            System.err.println("[CharacterPageVC] currentPlayer is null. Cannot display stats.");
            setAllLabelsToDefault("No player data available.");
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
        intelligenceLabel.setText(String.valueOf(currentPlayer.getIntelligence()));
        if (effectiveIntelligenceLabel != null) effectiveIntelligenceLabel.setText(String.valueOf(currentPlayer.getEffectiveIntelligence()));

        // Physical Capabilities
        upperBodyStrengthLabel.setText(String.valueOf(currentPlayer.getUpperBodyStrength()));
        if (effectiveUpperBodyStrengthLabel != null) effectiveUpperBodyStrengthLabel.setText(String.valueOf(currentPlayer.getEffectiveUpperBodyStrength()));
        lowerBodyStrengthLabel.setText(String.valueOf(currentPlayer.getLowerBodyStrength()));
        if (effectiveLowerBodyStrengthLabel != null) effectiveLowerBodyStrengthLabel.setText(String.valueOf(currentPlayer.getEffectiveLowerBodyStrength()));
        enduranceLabel.setText(String.valueOf(currentPlayer.getEndurance())); // Player.java has no getEffectiveEndurance
        agilityLabel.setText(String.valueOf(currentPlayer.getAgility()));
        if (effectiveAgilityLabel != null) effectiveAgilityLabel.setText(String.valueOf(currentPlayer.getEffectiveAgility()));
        speedLabel.setText(String.valueOf(currentPlayer.getSpeed()));
        if (effectiveSpeedLabel != null) effectiveSpeedLabel.setText(String.valueOf(currentPlayer.getEffectiveSpeed()));
        dexterityLabel.setText(String.valueOf(currentPlayer.getDexterity()));
        if (effectiveDexterityLabel != null) effectiveDexterityLabel.setText(String.valueOf(currentPlayer.getEffectiveDexterity()));

        // Health & Needs (Displaying as 0-100% for clarity)
        healthLabel.setText(String.format("%d / %d", currentPlayer.getHealth(), Player.DEFAULT_PLAYER_HEALTH));
        fatigueLabel.setText(String.format("%d%%", currentPlayer.getFatiguePercent() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        hungerLabel.setText(String.format("%d%%", currentPlayer.getHunger() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE)); // Satiation
        thirstLabel.setText(String.format("%d%%", currentPlayer.getThirst() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE)); // Hydration

        // Populate newly added FXML labels for needs
        if (sleepLabel != null) sleepLabel.setText(String.format("%d%%", currentPlayer.getSleep() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        if (hygieneLabel != null) hygieneLabel.setText(String.format("%d%%", currentPlayer.getHygiene() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        if (comfortLabel != null) comfortLabel.setText(String.format("%d%%", currentPlayer.getComfort() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));
        if (stressLabel != null) stressLabel.setText(String.format("%d%%", currentPlayer.getStress() * 100 / Player.MAX_STAT_VALUE_PERCENTAGE));


        // Status
        isAliveLabel.setText(String.valueOf(currentPlayer.isAlive()));
        isConsciousLabel.setText(String.valueOf(currentPlayer.isConscious()));
        isAwakeLabel.setText(String.valueOf(currentPlayer.isAwake()));
        moodLabel.setText(valueOrDefault(currentPlayer.getMood(), "N/A", Object::toString));

        // Inventory
        inventoryTextFlow.getChildren().clear();
        List<String> inventory = currentPlayer.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            inventoryTextFlow.getChildren().add(new Text("Empty"));
        } else {
            Text invText = new Text(String.join(", ", inventory));
            // Style can be set here or rely on CSS (.character-page-inventory-text-flow .text)
            // invText.setStyle("-fx-fill: #C0C0C0; -fx-font-size: 13px;");
            inventoryTextFlow.getChildren().add(invText);
        }
        System.out.println("[CharacterPageVC] Player stats displayed.");
    }

    private <T> String valueOrDefault(T value, String defaultValue, Function<T, String> toStringFunction) {
        if (value == null) return defaultValue;
        String strValue = toStringFunction.apply(value);
        return (strValue == null || strValue.trim().isEmpty()) ? defaultValue : strValue;
    }

    private String valueOrDefault(String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    private void setAllLabelsToDefault(String message) {
        // Basic Info
        nameLabel.setText(message); ageLabel.setText("N/A"); sexLabel.setText("N/A"); ethnicityLabel.setText("N/A");
        heightLabel.setText("N/A"); weightLabel.setText("N/A"); bloodTypeLabel.setText("N/A"); bodyTypeLabel.setText("N/A");
        // Core Attributes
        luckLabel.setText("N/A"); beautyLabel.setText("N/A"); intelligenceLabel.setText("N/A");
        if (effectiveIntelligenceLabel != null) effectiveIntelligenceLabel.setText("N/A");
        // Physical Capabilities
        upperBodyStrengthLabel.setText("N/A"); if(effectiveUpperBodyStrengthLabel != null) effectiveUpperBodyStrengthLabel.setText("N/A");
        lowerBodyStrengthLabel.setText("N/A"); if(effectiveLowerBodyStrengthLabel != null) effectiveLowerBodyStrengthLabel.setText("N/A");
        enduranceLabel.setText("N/A"); agilityLabel.setText("N/A"); if(effectiveAgilityLabel != null) effectiveAgilityLabel.setText("N/A");
        speedLabel.setText("N/A"); if(effectiveSpeedLabel != null) effectiveSpeedLabel.setText("N/A");
        dexterityLabel.setText("N/A"); if(effectiveDexterityLabel != null) effectiveDexterityLabel.setText("N/A");
        // Health & Needs
        healthLabel.setText("N/A"); fatigueLabel.setText("N/A"); hungerLabel.setText("N/A"); thirstLabel.setText("N/A");
        if(sleepLabel != null) sleepLabel.setText("N/A"); if(hygieneLabel != null) hygieneLabel.setText("N/A");
        if(comfortLabel != null) comfortLabel.setText("N/A"); if(stressLabel != null) stressLabel.setText("N/A");
        // Status
        isAliveLabel.setText("N/A"); isConsciousLabel.setText("N/A"); isAwakeLabel.setText("N/A"); moodLabel.setText("N/A");
        // Inventory
        inventoryTextFlow.getChildren().clear(); inventoryTextFlow.getChildren().add(new Text(message));
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (navigationService != null) {
            navigationService.navigateTo(View.GAME_WORLD);
        } else {
            String errorMessage = "NavigationService is not available in CharacterPageViewController.";
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