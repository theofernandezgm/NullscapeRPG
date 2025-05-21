package com.theofernandez.rpg.ui;

import com.theofernandez.rpg.ui.navigation.NavigableController;
import com.theofernandez.rpg.ui.navigation.NavigationService;
import com.theofernandez.rpg.ui.navigation.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SocialsViewController implements NavigableController {
    private NavigationService navigationService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() { System.out.println("SocialsViewController initialized."); }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (navigationService != null) navigationService.navigateTo(View.GAME_WORLD);
    }
}