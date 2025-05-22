package com.theofernandez.rpg.ui.navigation;

/**
 * Interface for controllers that require navigation services.
 * Allows the NavigationService to be injected into controllers.
 */
public interface NavigableController {
    /**
     * Sets the navigation service for this controller.
     * @param navigationService The navigation service instance.
     */
    void setNavigationService(NavigationService navigationService);
}