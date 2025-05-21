package com.theofernandez.rpg.ui.navigation;

public enum View {
    MAIN_MENU("/com/theofernandez/rpg/ui/MainMenuView.fxml", "Main Menu"),
    NEW_GAME("/com/theofernandez/rpg/ui/NewGameView.fxml", "New Game"),
    LOAD_GAME("/com/theofernandez/rpg/ui/LoadGameView.fxml", "Load Game"),
    SETTINGS("/com/theofernandez/rpg/ui/SettingsView.fxml", "Settings"),
    GAME_WORLD("/com/theofernandez/rpg/ui/GameWorldView.fxml", "Game World"),
    CHARACTER_PAGE("/com/theofernandez/rpg/ui/CharacterPageView.fxml", "Character Details"), // <<< VERIFY THIS LINE
    SOCIALS_PAGE("/com/theofernandez/rpg/ui/SocialsView.fxml", "Socials");

    // ... (rest of the enum code)
    private final String fxmlPath;
    private final String title;

    View(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() { return fxmlPath; }
    public String getTitle() { return title; }
}