/**
 * @file AuthMenu.java
 * @brief Authentication menu class for the Legal Case Tracker system
 *
 * This file contains the AuthMenu class which provides the user interface
 * for authentication operations including login and registration.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;

/**
 * @brief Authentication menu for login and registration
 * @details Provides user interface for authentication operations including login and registration
 */
public class AuthMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final ConsoleMenuManager consoleMenuManager;
    
    /**
     * @brief Authentication service for user operations
     * @details Handles login, registration, and other authentication operations
     */
    private final AuthService authService;

    /**
     * @brief Constructor for AuthMenu
     * @param consoleMenuManager Menu manager instance to handle navigation
     * @param authService Authentication service for user validation
     */
    public AuthMenu(ConsoleMenuManager consoleMenuManager, AuthService authService) {
        this.consoleMenuManager = consoleMenuManager;
        this.authService = authService;
    }

    /**
     * @brief Display the authentication menu
     * @details Shows main authentication options and processes user choice
     */
    public void display() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Legal Case Tracker - Authentication");
        UiConsoleHelper.displayMenuOption(1, "Login");
        UiConsoleHelper.displayMenuOption(2, "Register");
        UiConsoleHelper.displayMenuOption(3, "Exit");
        UiConsoleHelper.displayHorizontalLine();

        int choice = UiConsoleHelper.readInt("Enter your choice", 1, 3);

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                consoleMenuManager.exit();
                break;
        }
    }

    /**
     * @brief Handle user login
     * @details Prompts for credentials and attempts to authenticate the user
     */
    private void login() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Login");

        String username = UiConsoleHelper.readRequiredString("Enter username");
        String password = UiConsoleHelper.readRequiredString("Enter password");

        if (authService.login(username, password)) {
            UiConsoleHelper.displaySuccess("Login successful! Welcome, " + authService.getCurrentUser().getFullName());
            UiConsoleHelper.pressEnterToContinue();
        } else {
            UiConsoleHelper.displayError("Login failed. Invalid username or password");
            UiConsoleHelper.pressEnterToContinue();
        }
    }

    /**
     * @brief Handle user registration
     * @details Collects new user information and creates an account
     * @throws IllegalArgumentException If input validation fails
     * @throws Exception For any other registration failures
     */
    private void register() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("New User Registration");

        try {
            String username = UiConsoleHelper.readRequiredString("Enter username");
            String password = UiConsoleHelper.readRequiredString("Enter password");
            String email = UiConsoleHelper.readRequiredString("Enter email");
            String firstName = UiConsoleHelper.readRequiredString("Enter first name");
            String lastName = UiConsoleHelper.readRequiredString("Enter last name");

            // Display available roles and let the user select one
            UserRole role = UiConsoleHelper.readEnum("Select role", UserRole.class);

            authService.register(username, password, email, firstName, lastName, role);

            UiConsoleHelper.displaySuccess("Registration successful! You can now login with your user credentials");
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Registration failed: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
    }
}