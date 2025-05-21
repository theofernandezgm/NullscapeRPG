package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text; // For TextFlow
import javafx.scene.text.TextFlow; // For Inventory

import java.util.List;
import java.util.stream.Collectors;

public class CharacterPageViewController implements NavigableController {
    private NavigationService navigationService;
    private Player currentPlayer;

    @FXML private ScrollPane scrollPane; // To ensure content fits
    @FXML private VBox statsContainer; // The main container within the ScrollPane

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
    @FXML private Label intelligenceLabel;

    // Physical Capabilities
    @FXML private Label upperBodyStrengthLabel;
    @FXML private Label lowerBodyStrengthLabel;
    @FXML private Label enduranceLabel;
    @FXML private Label agilityLabel;
    @FXML private Label speedLabel;
    @FXML private Label dexterityLabel;

    // Health & Needs
    @FXML private Label healthLabel;
    @FXML private Label fatigueLabel;
    @FXML private Label hungerLabel;
    @FXML private Label thirstLabel;
    // (Add more FXML Labels for other needs if you put them in FXML: sleep, hygiene etc.)

    // Status
    @FXML private Label isAliveLabel;
    @FXML private Label isConsciousLabel;
    @FXML private Label isAwakeLabel;
    @FXML private Label moodLabel;


    // Inventory
    @FXML private TextFlow inventoryTextFlow;


    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        System.out.println("CharacterPageViewController initialized.");
        this.currentPlayer = GameContext.currentPlayer;
        displayAllStats();
    }

    private void displayAllStats() {
        if (currentPlayer == null) {
            // Optionally set all labels to "N/A" or show a message
            nameLabel.setText("No Player Data");
            return;
        }

        // Basic Info
        nameLabel.setText(currentPlayer.getName());
        ageLabel.setText(String.valueOf(currentPlayer.getAge()));
        sexLabel.setText(currentPlayer.getSex() != null ? currentPlayer.getSex().toString() : "N/A");
        ethnicityLabel.setText(currentPlayer.getEthnicity());
        heightLabel.setText(String.format("%.2f m", currentPlayer.getHeight()));
        weightLabel.setText(String.format("%.1f kg", currentPlayer.getWeight()));
        bloodTypeLabel.setText(currentPlayer.getBloodType() != null ? currentPlayer.getBloodType().toString() : "N/A");
        bodyTypeLabel.setText(currentPlayer.getBodyType() != null ? currentPlayer.getBodyType().toString() : "N/A");

        // Core Attributes
        luckLabel.setText(String.valueOf(currentPlayer.getLuck()));
        beautyLabel.setText(String.valueOf(currentPlayer.getBeauty()));
        intelligenceLabel.setText(String.valueOf(currentPlayer.getIntelligence()));

        // Physical Capabilities
        upperBodyStrengthLabel.setText(String.valueOf(currentPlayer.getUpperBodyStrength()));
        lowerBodyStrengthLabel.setText(String.valueOf(currentPlayer.getLowerBodyStrength()));
        enduranceLabel.setText(String.valueOf(currentPlayer.getEndurance()));
        agilityLabel.setText(String.valueOf(currentPlayer.getAgility()));
        speedLabel.setText(String.valueOf(currentPlayer.getSpeed()));
        dexterityLabel.setText(String.valueOf(currentPlayer.getDexterity()));

        // Health & Needs (Format: current / max_percentage_constant for needs)
        healthLabel.setText(currentPlayer.getHealth() + " / " + Player.DEFAULT_PLAYER_HEALTH); // Assuming DEFAULT_PLAYER_HEALTH is the max
        fatigueLabel.setText(currentPlayer.getFatiguePercent() + " / " + Player.MAX_STAT_VALUE_PERCENTAGE);
        hungerLabel.setText(currentPlayer.getHunger() + " / " + Player.MAX_STAT_VALUE_PERCENTAGE);
        thirstLabel.setText(currentPlayer.getThirst() + " / " + Player.MAX_STAT_VALUE_PERCENTAGE);
        // (Populate other need labels here if added to FXML)

        // Status
        isAliveLabel.setText(String.valueOf(currentPlayer.isAlive()));
        isConsciousLabel.setText(String.valueOf(currentPlayer.isConscious()));
        isAwakeLabel.setText(String.valueOf(currentPlayer.isAwake()));
        moodLabel.setText(currentPlayer.getMood() != null ? currentPlayer.getMood().toString() : "N/A");


        // Inventory
        inventoryTextFlow.getChildren().clear();
        List<String> inventory = currentPlayer.getInventory();
        if (inventory.isEmpty()) {
            inventoryTextFlow.getChildren().add(new Text("Empty"));
        } else {
            Text invText = new Text(inventory.stream().collect(Collectors.joining(", ")));
            invText.setStyle("-fx-fill: #E0E0E0;"); // Match label text fill
            inventoryTextFlow.getChildren().add(invText);
        }

        // TODO: Populate Organ Integrity, Sensory, Mental, Emotional attributes
        // You would add more GridPanes in FXML and corresponding Labels/@FXML fields here
        // then populate them similarly.
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (navigationService != null) {
            navigationService.navigateTo(View.GAME_WORLD);
        }
    }
}