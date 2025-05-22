package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @brief Test class for AuthService
 *
 * This class contains unit tests for the authentication service functionality
 * including user registration, login, logout, and role verification.
 */
public class AuthServiceTest {
    /** Authentication service instance being tested */
    private AuthService authService;

    /** Data Access Object for User operations */
    private UserDAO userDAO;

    // Test data
    /** Username used for testing */
    private final String testUsername = "testuser";

    /** Password used for testing */
    private final String testPassword = "password123";

    /** Email used for testing */
    private final String testEmail = "test@example.com";

    /** First name used for testing */
    private final String testName = "Test";

    /** Last name used for testing */
    private final String testSurname = "User";

    /** User role used for testing */
    private final UserRole testRole = UserRole.LAWYER;

    /**
     * @brief Set up test environment before each test
     *
     * Creates test database tables and initializes DAOs and services
     *
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Set up test database
        TestDatabaseManager.createTables();

        // Create DAO and service
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());
        authService = new AuthService(userDAO);
    }

    /**
     * @brief Clean up test environment after each test
     *
     * Clears database tables and logs out any active user
     *
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Clean up database after each test
        TestDatabaseManager.clearTables();

        // Make sure user is logged out
        authService.logout();
    }

    /**
     * @brief Test user registration with valid data
     *
     * Verifies that registration with valid data returns a user object with an ID
     */
    @Test
    public void Test_Register_ValidData_ReturnsUserWithId() {
        // Register a new user
        User user = authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Verify
        assertNotNull("User should not be null", user);
        assertNotNull("User ID should not be null", user.getId());
        assertEquals("Username should match", testUsername, user.getUsername());
        assertEquals("Email should match", testEmail, user.getEmail());
        assertEquals("Name should match", testName, user.getName());
        assertEquals("Surname should match", testSurname, user.getSurname());
        assertEquals("Role should match", testRole, user.getRole());
        assertTrue("User should be enabled by default", user.isEnabled());
    }

    /**
     * @brief Test registration with duplicate username
     *
     * Verifies that attempting to register with an existing username throws an exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_Register_DuplicateUsername_ThrowsException() {
        // Register a user
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Try to register another user with the same username but different email
        authService.register(testUsername, "anotherpassword", "another@example.com", "Another", "User", UserRole.ASSISTANT);
    }

    /**
     * @brief Test registration with duplicate email
     *
     * Verifies that attempting to register with an existing email throws an exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_Register_DuplicateEmail_ThrowsException() {
        // Register a user
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Try to register another user with the same email but different username
        authService.register("anotheruser", "anotherpassword", testEmail, "Another", "User", UserRole.ASSISTANT);
    }

    /**
     * @brief Test login with valid credentials
     *
     * Verifies that login succeeds with valid username and password
     */
    @Test
    public void Test_Login_ValidCredentials_ReturnsTrue() {
        // Register a user
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Login with valid credentials
        boolean result = authService.login(testUsername, testPassword);

        // Verify
        assertTrue("Login should succeed with valid credentials", result);
        assertTrue("User should be logged in", authService.isLoggedIn());

        // Verify current user
        User currentUser = authService.getCurrentUser();
        assertEquals("Current user should match registered user", testUsername, currentUser.getUsername());
    }

    /**
     * @brief Test login with invalid username
     *
     * Verifies that login fails when using a non-existent username
     */
    @Test
    public void Test_Login_InvalidUsername_ReturnsFalse() {
        // Register a user
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Login with invalid username
        boolean result = authService.login("wrongusername", testPassword);

        // Verify
        assertFalse("Login should fail with invalid username", result);
        assertFalse("User should not be logged in", authService.isLoggedIn());
    }

    /**
     * @brief Test login with invalid password
     *
     * Verifies that login fails when using an incorrect password
     */
    @Test
    public void Test_Login_InvalidPassword_ReturnsFalse() {
        // Register a user
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Login with invalid password
        boolean result = authService.login(testUsername, "wrongpassword");

        // Verify
        assertFalse("Login should fail with invalid password", result);
        assertFalse("User should not be logged in", authService.isLoggedIn());
    }

    /**
     * @brief Test login with disabled account
     *
     * Verifies that login fails when the account is disabled
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Login_DisabledAccount_ReturnsFalse() throws SQLException {
        // Register a user
        User user = authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);

        // Disable the account
        user.setEnabled(false);
        userDAO.update(user);

        // Login with valid credentials but disabled account
        boolean result = authService.login(testUsername, testPassword);

        // Verify
        assertFalse("Login should fail with disabled account", result);
        assertFalse("User should not be logged in", authService.isLoggedIn());
    }

    /**
     * @brief Test logout functionality for logged-in user
     *
     * Verifies that logout clears the current user when called
     */
    @Test
    public void Test_Logout_LoggedInUser_ClearsCurrentUser() {
        // Register and login
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);
        authService.login(testUsername, testPassword);

        // Verify user is logged in
        assertTrue("User should be logged in", authService.isLoggedIn());

        // Logout
        authService.logout();

        // Verify
        assertFalse("User should be logged out", authService.isLoggedIn());
    }

    /**
     * @brief Test logout functionality when no user is logged in
     *
     * Verifies that logout has no effect when no user is logged in
     */
    @Test
    public void Test_Logout_NoLoggedInUser_DoesNothing() {
        // Ensure no user is logged in
        assertFalse("No user should be logged in initially", authService.isLoggedIn());

        // Logout (should do nothing)
        authService.logout();

        // Verify still no user logged in
        assertFalse("Still no user should be logged in", authService.isLoggedIn());
    }

    /**
     * @brief Test isLoggedIn method when a user is logged in
     *
     * Verifies that isLoggedIn returns true when a user is authenticated
     */
    @Test
    public void Test_IsLoggedIn_UserLoggedIn_ReturnsTrue() {
        // Register and login
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);
        authService.login(testUsername, testPassword);

        // Verify
        assertTrue("isLoggedIn should return true when user is logged in", authService.isLoggedIn());
    }

    /**
     * @brief Test isLoggedIn method when no user is logged in
     *
     * Verifies that isLoggedIn returns false when no user is authenticated
     */
    @Test
    public void Test_IsLoggedIn_NoUserLoggedIn_ReturnsFalse() {
        // Verify
        assertFalse("isLoggedIn should return false when no user is logged in", authService.isLoggedIn());
    }

    /**
     * @brief Test getCurrentUser method when a user is logged in
     *
     * Verifies that getCurrentUser returns the correct user object
     */
    @Test
    public void Test_GetCurrentUser_UserLoggedIn_ReturnsUser() {
        // Register and login
        User registeredUser = authService.register(testUsername, testPassword, testEmail, testName, testSurname, testRole);
        authService.login(testUsername, testPassword);

        // Get current user
        User currentUser = authService.getCurrentUser();

        // Verify
        assertNotNull("Current user should not be null", currentUser);
        assertEquals("Current user ID should match registered user", registeredUser.getId(), currentUser.getId());
        assertEquals("Current user username should match", testUsername, currentUser.getUsername());
    }

    /**
     * @brief Test getCurrentUser method when no user is logged in
     *
     * Verifies that getCurrentUser throws an exception when no user is authenticated
     */
    @Test(expected = IllegalStateException.class)
    public void Test_GetCurrentUser_NoUserLoggedIn_ThrowsException() {
        // Try to get current user when no one is logged in
        authService.getCurrentUser();
    }

    /**
     * @brief Test hasRole method when user has the specified role
     *
     * Verifies that hasRole returns true when the logged-in user has the specified role
     */
    @Test
    public void Test_HasRole_UserHasRole_ReturnsTrue() {
        // Register and login with LAWYER role
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, UserRole.LAWYER);
        authService.login(testUsername, testPassword);

        // Check if user has LAWYER role
        boolean result = authService.hasRole(UserRole.LAWYER);

        // Verify
        assertTrue("hasRole should return true when user has the role", result);
    }

    /**
     * @brief Test hasRole method when user doesn't have the specified role
     *
     * Verifies that hasRole returns false when the logged-in user doesn't have the specified role
     */
    @Test
    public void Test_HasRole_UserDoesNotHaveRole_ReturnsFalse() {
        // Register and login with LAWYER role
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, UserRole.LAWYER);
        authService.login(testUsername, testPassword);

        // Check if user has ADMIN role
        boolean result = authService.hasRole(UserRole.ADMIN);

        // Verify
        assertFalse("hasRole should return false when user doesn't have the role", result);
    }

    /**
     * @brief Test hasRole method when no user is logged in
     *
     * Verifies that hasRole throws an exception when no user is authenticated
     */
    @Test(expected = IllegalStateException.class)
    public void Test_HasRole_NoUserLoggedIn_ThrowsException() {
        // Try to check role when no one is logged in
        authService.hasRole(UserRole.ADMIN);
    }

    /**
     * @brief Test isAdmin method when an admin is logged in
     *
     * Verifies that isAdmin returns true when an admin user is authenticated
     */
    @Test
    public void Test_IsAdmin_AdminLoggedIn_ReturnsTrue() {
        // Register and login with ADMIN role
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, UserRole.ADMIN);
        authService.login(testUsername, testPassword);

        // Check if user is admin
        boolean result = authService.isAdmin();

        // Verify
        assertTrue("isAdmin should return true when admin is logged in", result);
    }

    /**
     * @brief Test isAdmin method when a non-admin is logged in
     *
     * Verifies that isAdmin returns false when a non-admin user is authenticated
     */
    @Test
    public void Test_IsAdmin_NonAdminLoggedIn_ReturnsFalse() {
        // Register and login with LAWYER role
        authService.register(testUsername, testPassword, testEmail, testName, testSurname, UserRole.LAWYER);
        authService.login(testUsername, testPassword);

        // Check if user is admin
        boolean result = authService.isAdmin();

        // Verify
        assertFalse("isAdmin should return false when non-admin is logged in", result);
    }

    /**
     * @brief Test isAdmin method when no user is logged in
     *
     * Verifies that isAdmin returns false when no user is authenticated
     */
    @Test
    public void Test_IsAdmin_NoUserLoggedIn_ReturnsFalse() {
        // Check if user is admin when no one is logged in
        boolean result = authService.isAdmin();

        // Verify
        assertFalse("isAdmin should return false when no user is logged in", result);
    }
}