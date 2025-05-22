package com.theofernandez.rpg.ui.navigation;

public enum View {
    // Ensure FXML paths are correct, starting from a root recognized by getResource()
    // Typically, this is from the 'resources' directory.
    // Example: if MainMenuView.fxml is in 'src/main/resources/com/theofernandez/rpg/ui/MainMenuView.fxml'
    // then the path "/com/theofernandez/rpg/ui/MainMenuView.fxml" is correct.

    MAIN_MENU("/com/theofernandez/rpg/ui/MainMenuView.fxml", "Main Menu"),
    NEW_GAME("/com/theofernandez/rpg/ui/NewGameView.fxml", "New Game"),
    LOAD_GAME("/com/theofernandez/rpg/ui/LoadGameView.fxml", "Load Game"),
    SETTINGS("/com/theofernandez/rpg/ui/SettingsView.fxml", "Settings"),
    GAME_WORLD("/com/theofernandez/rpg/ui/GameWorldView.fxml", "Nullscape"), // Changed title for flavor
    CHARACTER_PAGE("/com/theofernandez/rpg/ui/CharacterPageView.fxml", "Character Details"),
    SOCIALS_PAGE("/com/theofernandez/rpg/ui/SocialsView.fxml", "Socials"); // Placeholder for future

    private final String fxmlPath;
    private final String title;

    View(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }
}