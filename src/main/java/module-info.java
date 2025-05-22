module com.theofernandez.rpg { // Or 'module Game' - ensure consistency with your actual module name if different

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    // Open ui package to JavaFX for FXML loading (reflection) and graphics access.
    // If you have FXML files in other packages that have controllers, those packages also need to be opened to javafx.fxml.
    opens com.theofernandez.rpg.ui to javafx.graphics, javafx.fxml;
    opens com.theofernandez.rpg.ui.navigation to javafx.fxml; // If any FXML directly uses navigation classes as controllers (unlikely here)

    // Export packages that form the public API of this module or are needed by JavaFX launch.
    exports com.theofernandez.rpg;      // For Main class if launched externally
    exports com.theofernandez.rpg.ui;   // For Game class (extends Application)
    exports com.theofernandez.rpg.game; // For Player, GameContext classes if they were to be used by other modules (though likely internal here)
    exports com.theofernandez.rpg.ui.navigation; // For View, NavigableController, NavigationService if used across modules (internal here)
    // exports com.theofernandez.rpg.engine; // Only if PlayerStatEngine needs to be accessed externally
}