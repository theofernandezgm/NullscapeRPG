module com.theofernandez.rpg { // Or 'module Game' - ensure consistency

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    // Open ui package to JavaFX for FXML loading and graphics access
    opens com.theofernandez.rpg.ui to javafx.graphics, javafx.fxml;

    // Export packages that form the public API of this module
    exports com.theofernandez.rpg;      // For com.theofernandez.rpg.Main
    exports com.theofernandez.rpg.ui;   // If Game class needs to be accessed, or UI elements
    exports com.theofernandez.rpg.game; // For Player class etc.
    exports com.theofernandez.rpg.ui.navigation; // Export the new navigation package
}