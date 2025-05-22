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
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;

/**
 * @brief Authentication menu for login and registration
 * @details Provides user interface for authentication operations including login and registration
 */
public class AuthMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final MenuManager menuManager;

    /**
     * @brief Authentication service for user operations
     * @details Handles login, registration, and other authentication operations
     */
    private final AuthService authService;

    /**
     * @brief Constructor for AuthMenu
     * @param menuManager Menu manager instance to handle navigation
     * @param authService Authentication service for user validation
     */
    public AuthMenu(MenuManager menuManager, AuthService authService) {
        this.menuManager = menuManager;
        this.authService = authService;
    }

    /**
     * @brief Display the authentication menu
     * @details Shows main authentication options and processes user choice
     */
    public void display() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Legal Case Tracker - Authentication");
        ConsoleHelper.displayMenuOption(1, "Login");
        ConsoleHelper.displayMenuOption(2, "Register");
        ConsoleHelper.displayMenuOption(3, "Exit");
        ConsoleHelper.displayHorizontalLine();

        int choice = ConsoleHelper.readInt("Enter your choice", 1, 3);

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                menuManager.exit();
                break;
        }
    }

    /**
     * @brief Handle user login
     * @details Prompts for credentials and attempts to authenticate the user
     */
    private void login() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Login");

        String username = ConsoleHelper.readRequiredString("Enter username");
        String password = ConsoleHelper.readRequiredString("Enter password");

        if (authService.login(username, password)) {
            ConsoleHelper.displaySuccess("Login successful! Welcome, " + authService.getCurrentUser().getFullName());
            ConsoleHelper.pressEnterToContinue();
        } else {
            ConsoleHelper.displayError("Login failed. Invalid username or password");
            ConsoleHelper.pressEnterToContinue();
        }
    }

    /**
     * @brief Handle user registration
     * @details Collects new user information and creates an account
     * @throws IllegalArgumentException If input validation fails
     * @throws Exception For any other registration failures
     */
    private void register() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("New User Registration");

        try {
            String username = ConsoleHelper.readRequiredString("Enter username");
            String password = ConsoleHelper.readRequiredString("Enter password");
            String email = ConsoleHelper.readRequiredString("Enter email");
            String firstName = ConsoleHelper.readRequiredString("Enter first name");
            String lastName = ConsoleHelper.readRequiredString("Enter last name");

            // Display available roles and let the user select one
            UserRole role = ConsoleHelper.readEnum("Select role", UserRole.class);

            authService.register(username, password, email, firstName, lastName, role);

            ConsoleHelper.displaySuccess("Registration successful! You can now login with your user credentials");
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Registration failed: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
    }
}