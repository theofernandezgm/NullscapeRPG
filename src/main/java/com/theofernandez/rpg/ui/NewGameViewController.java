package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.game.GameContext;
import com.theofernandez.rpg.game.Player;
import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;

public class NewGameViewController implements NavigableController {

    @FXML
    private TextField playerNameField;

    @FXML
    private ChoiceBox<Player.Sex> sexChoiceBox;

    @FXML
    private ChoiceBox<Player.BodyType> bodyTypeChoiceBox;

    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        System.out.println("NewGameViewController initialized.");

        sexChoiceBox.setItems(FXCollections.observableArrayList(Player.Sex.values()));
        sexChoiceBox.setValue(Player.DEFAULT_PLAYER_SEX);

        bodyTypeChoiceBox.setItems(FXCollections.observableArrayList(Player.BodyType.values()));
        bodyTypeChoiceBox.setValue(Player.DEFAULT_PLAYER_BODY_TYPE);
    }

    @FXML
    private void handleStartGameAction(ActionEvent event) {
        String playerName = playerNameField.getText().trim();
        Player.Sex selectedSex = sexChoiceBox.getValue();
        Player.BodyType selectedBodyType = bodyTypeChoiceBox.getValue();

        if (playerName.isEmpty()) {
            showAlert("Validation Error", "Player name cannot be empty!");
            return;
        }
        if (selectedSex == null) {
            showAlert("Validation Error", "Please select a sex.");
            return;
        }
        if (selectedBodyType == null) {
            showAlert("Validation Error", "Please select a body type.");
            return;
        }

        Player newPlayer = new Player(); // Assumes default constructor sets other defaults
        newPlayer.setName(playerName);
        newPlayer.setSex(selectedSex);
        newPlayer.setBodyType(selectedBodyType);

        System.out.println("Player Created: " + newPlayer.getName() +
                ", Sex: " + newPlayer.getSex() +
                ", Body Type: " + newPlayer.getBodyType());

        GameContext.currentPlayer = newPlayer;

        if (navigationService != null) {
            navigationService.navigateTo(View.GAME_WORLD);
        } else {
            System.err.println("NavigationService not set in NewGameViewController!");
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (navigationService != null) {
            navigationService.navigateTo(View.MAIN_MENU);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}