package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View; // Import View enum
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuViewController implements NavigableController {

    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        System.out.println("MainMenuViewController initialized.");
    }

    @FXML
    private void handleNewGameAction(ActionEvent event) {
        if (navigationService != null) navigationService.navigateTo(View.NEW_GAME);
    }

    @FXML
    private void handleLoadGameAction(ActionEvent event) {
        if (navigationService != null) navigationService.navigateTo(View.LOAD_GAME);
    }

    @FXML
    private void handleSettingsAction(ActionEvent event) {
        if (navigationService != null) navigationService.navigateTo(View.SETTINGS);
    }

    @FXML
    private void handleExitAction(ActionEvent event) {
        System.out.println("Exit button clicked from Main Menu!");
        Platform.exit();
    }
}