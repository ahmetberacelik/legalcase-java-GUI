package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * @brief Test class for User model
 *
 * Contains unit tests for the User class methods and constructors
 */
public class UserTest {

    /**
     * @brief Set up test environment before each test
     *
     * Creates necessary tables in the test database
     * @throws SQLException if database operation fails
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();
    }

    /**
     * @brief Clean up after each test
     *
     * Closes the test database connection
     * @throws SQLException if database operation fails
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Test User constructor with ID parameter
     *
     * Verifies that the constructor with ID parameter correctly sets all user properties
     */
    @Test
    public void Test_Constructor_WithId_SetsCorrectValues() {
        // Arrange
        Long id = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String name = "Test";
        String surname = "User";
        UserRole role = UserRole.ADMIN;

        // Act
        User user = new User(id, username, email, name, surname, role);

        // Assert
        assertEquals("ID should be set correctly", id, user.getId());
        assertEquals("Username should be set correctly", username, user.getUsername());
        assertEquals("Email should be set correctly", email, user.getEmail());
        assertEquals("Name should be set correctly", name, user.getName());
        assertEquals("Surname should be set correctly", surname, user.getSurname());
        assertEquals("Role should be set correctly", role, user.getRole());
        assertTrue("Should be enabled", user.isEnabled());
        assertNotNull("createdAt should not be null", user.getCreatedAt());
        assertNotNull("updatedAt should not be null", user.getUpdatedAt());
    }

    /**
     * @brief Test User constructor with password parameter
     *
     * Verifies that the constructor with password parameter correctly sets all user properties
     */
    @Test
    public void Test_Constructor_WithPassword_SetsCorrectValues() {
        // Arrange
        String username = "anotheruser";
        String password = "password123";
        String email = "another@example.com";
        String name = "Another";
        String surname = "User";
        UserRole role = UserRole.LAWYER;

        // Act
        User user = new User(username, password, email, name, surname, role);

        // Assert
        assertNull("ID should be null", user.getId());
        assertEquals("Username should be set correctly", username, user.getUsername());
        assertEquals("Password should be set correctly", password, user.getPassword());
        assertEquals("Email should be set correctly", email, user.getEmail());
        assertEquals("Name should be set correctly", name, user.getName());
        assertEquals("Surname should be set correctly", surname, user.getSurname());
        assertEquals("Role should be set correctly", role, user.getRole());
        assertTrue("Should be enabled", user.isEnabled());
        assertNotNull("createdAt should not be null", user.getCreatedAt());
        assertNotNull("updatedAt should not be null", user.getUpdatedAt());
    }

    /**
     * @brief Test hasRole method
     *
     * Verifies that hasRole returns true for matching roles and false otherwise
     */
    @Test
    public void Test_HasRole_ReturnsTrueForMatchingRole() {
        // Arrange
        User user = new User("roleuser", "password", "role@example.com", "Role", "User", UserRole.ADMIN);

        // Act and Assert
        assertTrue("Should return true for matching role", user.hasRole(UserRole.ADMIN));
        assertFalse("Should return false for non-matching role", user.hasRole(UserRole.LAWYER));
    }

    /**
     * @brief Test getFullName method
     *
     * Verifies that getFullName returns the correct concatenated full name
     */
    @Test
    public void Test_GetFullName_ReturnsCorrectFullName() {
        // Arrange
        User user = new User("fullnameuser", "password", "fullname@example.com", "Full", "Name", UserRole.ASSISTANT);

        // Act
        String fullName = user.getFullName();

        // Assert
        assertEquals("Full name should be correct", "Full Name", fullName);
    }

    /**
     * @brief Test prePersist method
     *
     * Verifies that prePersist updates both timestamps correctly
     */
    @Test
    public void Test_PrePersist_UpdatesTimestamps() {
        // Arrange
        User user = new User("persistuser", "password", "persist@example.com", "Persist", "User", UserRole.JUDGE);
        LocalDateTime originalCreatedAt = user.getCreatedAt();

        // Act
        // Wait a moment
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if wait is interrupted
        }
        user.prePersist();

        // Assert
        assertNotEquals("createdAt should be updated", originalCreatedAt, user.getCreatedAt());
        assertEquals("createdAt and updatedAt should be the same", user.getCreatedAt(), user.getUpdatedAt());
    }

    /**
     * @brief Test preUpdate method
     *
     * Verifies that preUpdate only updates the updatedAt timestamp
     */
    @Test
    public void Test_PreUpdate_UpdatesOnlyUpdatedAt() {
        // Arrange
        User user = new User("updateuser", "password", "update@example.com", "Update", "User", UserRole.CLIENT);
        LocalDateTime originalCreatedAt = user.getCreatedAt();

        // Act
        // Wait a moment
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if wait is interrupted
        }
        user.preUpdate();

        // Assert
        assertEquals("createdAt should not change", originalCreatedAt, user.getCreatedAt());
        assertNotEquals("updatedAt should be updated", originalCreatedAt, user.getUpdatedAt());
    }
}