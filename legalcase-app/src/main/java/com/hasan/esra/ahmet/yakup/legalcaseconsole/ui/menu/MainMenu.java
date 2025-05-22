/**
 * @file MainMenu.java
 * @brief Main menu class for the Legal Case Tracker system
 *
 * This file contains the MainMenu class which serves as the primary navigation hub
 * for the application. It displays the main menu options and routes user choices
 * to the appropriate specialized menus.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;

/**
 * @brief Main menu for the application
 *
 * This class handles the main menu functionality of the Legal Case Tracker application.
 * It displays menu options and routes user choices to appropriate handlers.
 */
public class MainMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final MenuManager menuManager;

    /**
     * @brief Authentication service for user session management
     * @details Handles user authentication state and logout operations
     */
    private final AuthService authService;

    /**
     * @brief Constructor for MainMenu
     *
     * @param menuManager Menu manager for navigation between screens
     * @param authService Authentication service for user session management
     */
    public MainMenu(MenuManager menuManager, AuthService authService) {
        this.menuManager = menuManager;
        this.authService = authService;
    }

    /**
     * @brief Display the main menu
     *
     * Shows the main menu options and handles user input.
     * Routes to appropriate sub-menus based on selection.
     */
    public void display() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Legal Case Tracker - Main Menu");
        ConsoleHelper.displayInfo("Logged in user: " + authService.getCurrentUser().getFullName() +
                " (" + authService.getCurrentUser().getRole() + ")");
        ConsoleHelper.displayHorizontalLine();

        ConsoleHelper.displayMenuOption(1, "Case Management");
        ConsoleHelper.displayMenuOption(2, "Client Tracking");
        ConsoleHelper.displayMenuOption(3, "Hearing Calendar");
        ConsoleHelper.displayMenuOption(4, "Document Archive");
        ConsoleHelper.displayMenuOption(5, "Logout");
        ConsoleHelper.displayMenuOption(6, "Exit Application");
        ConsoleHelper.displayHorizontalLine();

        int choice = ConsoleHelper.readInt("Enter your choice", 1, 6);

        switch (choice) {
            case 1:
                menuManager.navigateToCaseMenu();
                break;
            case 2:
                menuManager.navigateToClientMenu();
                break;
            case 3:
                menuManager.navigateToHearingMenu();
                break;
            case 4:
                menuManager.navigateToDocumentMenu();
                break;
            case 5:
                logout();
                break;
            case 6:
                menuManager.exit();
                break;
        }
    }

    /**
     * @brief Handle user logout process
     *
     * Clears the screen, logs out the current user via auth service,
     * displays success message, and waits for user acknowledgment.
     */
    private void logout() {
        ConsoleHelper.clearScreen();
        authService.logout();
        ConsoleHelper.displaySuccess("Logout successful");
        ConsoleHelper.pressEnterToContinue();
    }
}