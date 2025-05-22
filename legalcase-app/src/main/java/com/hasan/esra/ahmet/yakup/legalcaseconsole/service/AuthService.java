/**
 * @file AuthService.java
 * @brief Authentication service class for the Legal Case Tracker system
 *
 * This file contains the service class that handles user authentication,
 * including login, logout, and current user management.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for authentication related operations
 */
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO;
    private User currentUser;

    /**
     * Constructor
     * @param userDAO User DAO
     */
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.currentUser = null;
    }

    /**
     * Register a new user
     * @param username Username
     * @param password Password
     * @param email Email
     * @param name Name
     * @param surname Surname
     * @param role User role
     * @return The created user
     */
    public User register(String username, String password, String email, String name, String surname, UserRole role) {
        try {
            // Check if username is already taken
            Optional<User> existingUser = userDAO.getByUsername(username);
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }

            // Check if email is already in use
            Optional<User> existingEmail = userDAO.getByEmail(email);
            if (existingEmail.isPresent()) {
                throw new IllegalArgumentException("Email address is already in use");
            }

            // Create the new user
            User user = new User(username, password, email, name, surname, role);
            return userDAO.create(user);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during user registration", e);
            throw new RuntimeException("Could not register user", e);
        }
    }

    /**
     * Login user
     * @param username Username
     * @param password Password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        try {
            Optional<User> userOpt = userDAO.getByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Check if account is enabled
                if (!user.isEnabled()) {
                    LOGGER.warning("Login attempt for disabled account: " + username);
                    return false;
                }

                // Check if password matches
                if (password.equals(user.getPassword())) {
                    this.currentUser = user;
                    LOGGER.info("User logged in: " + username);
                    return true;
                }
            }

            LOGGER.warning("Failed login attempt: " + username);
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            return false;
        }
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (this.currentUser != null) {
            LOGGER.info("User logged out: " + this.currentUser.getUsername());
            this.currentUser = null;
        }
    }

    /**
     * Check if user is logged in
     * @return true if a user is logged in
     */
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    /**
     * Get current logged in user
     * @return Current user
     * @throws IllegalStateException if no user is logged in
     */
    public User getCurrentUser() {
        if (this.currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return this.currentUser;
    }

    /**
     * Check if current user has a specific role
     * @param role Role to check
     * @return true if current user has the role
     * @throws IllegalStateException if no user is logged in
     */
    public boolean hasRole(UserRole role) {
        if (this.currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return this.currentUser.getRole() == role;
    }

    /**
     * Check if current user is an admin
     * @return true if current user is an admin
     */
    public boolean isAdmin() {
        return isLoggedIn() && hasRole(UserRole.ADMIN);
    }
}