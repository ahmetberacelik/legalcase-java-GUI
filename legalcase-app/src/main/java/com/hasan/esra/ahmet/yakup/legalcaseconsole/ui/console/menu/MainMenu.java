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
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;

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
    private final ConsoleMenuManager consoleMenuManager;
    
    /**
     * @brief Authentication service for user session management
     * @details Handles user authentication state and logout operations
     */
    private final AuthService authService;

    /**
     * @brief Constructor for MainMenu
     *
     * @param consoleMenuManager Menu manager for navigation between screens
     * @param authService Authentication service for user session management
     */
    public MainMenu(ConsoleMenuManager consoleMenuManager, AuthService authService) {
        this.consoleMenuManager = consoleMenuManager;
        this.authService = authService;
    }

    /**
     * @brief Display the main menu
     *
     * Shows the main menu options and handles user input.
     * Routes to appropriate sub-menus based on selection.
     */
    public void display() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Legal Case Tracker - Main Menu");
        UiConsoleHelper.displayInfo("Logged in user: " + authService.getCurrentUser().getFullName() +
                " (" + authService.getCurrentUser().getRole() + ")");
        UiConsoleHelper.displayHorizontalLine();

        UiConsoleHelper.displayMenuOption(1, "Case Management");
        UiConsoleHelper.displayMenuOption(2, "Client Tracking");
        UiConsoleHelper.displayMenuOption(3, "Hearing Calendar");
        UiConsoleHelper.displayMenuOption(4, "Document Archive");
        UiConsoleHelper.displayMenuOption(5, "Logout");
        UiConsoleHelper.displayMenuOption(6, "Exit Application");
        UiConsoleHelper.displayHorizontalLine();

        int choice = UiConsoleHelper.readInt("Enter your choice", 1, 6);

        switch (choice) {
            case 1:
                consoleMenuManager.navigateToCaseMenu();
                break;
            case 2:
                consoleMenuManager.navigateToClientMenu();
                break;
            case 3:
                consoleMenuManager.navigateToHearingMenu();
                break;
            case 4:
                consoleMenuManager.navigateToDocumentMenu();
                break;
            case 5:
                logout();
                break;
            case 6:
                consoleMenuManager.exit();
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
        UiConsoleHelper.clearScreen();
        authService.logout();
        UiConsoleHelper.displaySuccess("Logout successful");
        UiConsoleHelper.pressEnterToContinue();
    }
}