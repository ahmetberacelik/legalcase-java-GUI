/**
 * @file User.java
 * @brief User entity class for the Legal Case Tracker system
 *
 * This file defines the User entity class which represents system users.
 * It contains user identity information, authentication details, and role-based
 * access control attributes.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.BaseEntity;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @brief User entity representing system users
 * @details This class represents a user in the legal case management system.
 * It contains user identity information, authentication details, and role-based
 * access control attributes.
 */
@DatabaseTable(tableName = "users")
public class User extends BaseEntity {

    /**
     * @brief Default constructor required by ORMLite
     */
    public User() {
        super();
    }

    /**
     * @brief Unique login name for the user
     * @details Username used for authentication and identification
     */
    @DatabaseField(unique = true, canBeNull = false)
    private String username;

    /**
     * @brief User's encrypted password
     * @details Password hash used for authentication
     */
    @DatabaseField(canBeNull = false)
    private String password;

    /**
     * @brief User's email address
     * @details Unique email identifier for user communications
     */
    @DatabaseField(unique = true, canBeNull = false)
    private String email;

    /**
     * @brief User's first name
     * @details Given name of the user
     */
    @DatabaseField(canBeNull = false)
    private String name;

    /**
     * @brief User's last name
     * @details Family name of the user
     */
    @DatabaseField(canBeNull = false)
    private String surname;

    /**
     * @brief User's system role
     * @details Determines user's permissions and access levels
     */
    @DatabaseField(canBeNull = false)
    private UserRole role;

    /**
     * @brief User account status
     * @details Indicates whether the user account is active or disabled
     */
    @DatabaseField(canBeNull = false, defaultValue = "true")
    private boolean enabled = true;

    /**
     * @brief Constructor with id
     * @param id The unique identifier for this user
     * @param username The login username
     * @param email The user's email address
     * @param name The user's first name
     * @param surname The user's last name
     * @param role The user's system role
     */
    public User(Long id, String username, String email, String name, String surname, UserRole role) {
        super(id);
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.enabled = true;
    }

    /**
     * @brief Constructor without id
     * @param username The login username
     * @param password The user's password
     * @param email The user's email address
     * @param name The user's first name
     * @param surname The user's last name
     * @param role The user's system role
     */
    public User(String username, String password, String email, String name, String surname, UserRole role) {
        this.username = username;
        this.password = hashPassword(password);
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.enabled = true;
    }

    /**
     * @brief Get the username
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Set the username
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @brief Get the password hash
     * @return The user's password hash
     */
    public String getPassword() {
        return password;
    }

    /**
     * @brief Set the password hash
     * @param password The password hash to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @brief Get the email address
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Set the email address
     * @param email The email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Get the first name
     * @return The user's first name
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Set the first name
     * @param name The first name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Get the last name
     * @return The user's last name
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @brief Set the last name
     * @param surname The last name to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @brief Get the user role
     * @return The user's system role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * @brief Set the user role
     * @param role The role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * @brief Check if the user account is enabled
     * @return True if the account is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @brief Set the account enabled status
     * @param enabled The enabled status to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing error", e);
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(hashPassword(password));
    }

    /**
     * @brief Check if user has a specific role
     * @param role The role to check against
     * @return True if the user has the specified role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    /**
     * @brief Get full name of user
     * @details Combines first and last name into a complete name
     * @return String containing the user's full name
     */
    public String getFullName() {
        return name + " " + surname;
    }
}