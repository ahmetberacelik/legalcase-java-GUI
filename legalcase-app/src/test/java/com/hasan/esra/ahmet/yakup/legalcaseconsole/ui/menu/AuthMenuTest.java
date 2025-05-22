package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.AuthMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class AuthMenuTest {
    private ConsoleMenuManager consoleMenuManager;
    private AuthService authService;
    private AuthMenu authMenu;
    private UserDAO userDAO;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() throws SQLException {
        // Capture console output
        System.setOut(new PrintStream(outContent));

        // Initialize test database
        TestDatabaseManager.createTables();
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Initialize auth service
        authService = new AuthService(userDAO);

        // Create a mock MenuManager (we don't need the other services for AuthMenu tests)
        consoleMenuManager = new ConsoleMenuManager(authService, null, null, null, null);

        // Create AuthMenu
        authMenu = new AuthMenu(consoleMenuManager, authService);
    }

    @After
    public void tearDown() {
        // Reset console output
        System.setOut(originalOut);

        // Reset scanner
        UiConsoleHelper.resetScanner();

        // Close database connection
        try {
            TestDatabaseManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    @Test
    public void Test_Display_SelectLogin() {
        // Arrange - Set scanner to select Login (1) and then enter valid credentials
        // Adding an extra newline for pressEnterToContinue
        UiConsoleHelper.setScanner(new Scanner("1\nadmin\nadmin\n\n"));

        // Create a test user
        authService.register("admin", "admin", "admin@test.com", "Admin", "User", UserRole.ADMIN);

        // Act
        authMenu.display();

        // Assert - Check that login was successful
        assertTrue("User should be logged in", authService.isLoggedIn());
        assertEquals("admin", authService.getCurrentUser().getUsername());
    }

    @Test(expected = IllegalStateException.class)
    public void Test_Display_SelectLoginWithInvalidCredentials() {
        // Arrange - Set scanner to select Login (1) and then enter invalid credentials
        // Adding an extra newline for pressEnterToContinue
        UiConsoleHelper.setScanner(new Scanner("1\nadmin\nwrongpassword\n\n"));

        // Create a test user
        authService.register("admin", "admin", "admin@test.com", "Admin", "User", UserRole.ADMIN);

        // Act
        authMenu.display();

        // Assert - Check that login failed
        assertFalse("User should not be logged in", authService.isLoggedIn());
        assertNull("Current user should be null", authService.getCurrentUser());
    }

    @Test
    public void Test_Display_SelectRegister() throws SQLException {
        // Arrange - Set scanner to select Register (2) and then enter registration details
        // Adding an extra newline for pressEnterToContinue
        UiConsoleHelper.setScanner(new Scanner("2\nnewuser\npassword\nuser@test.com\nNew\nUser\n1\n\n"));

        // Act
        authMenu.display();

        // Assert - Check that registration was successful
        User user = userDAO.getByUsername("newuser").orElse(null);
        assertNotNull("User should be created", user);
        assertEquals("newuser", user.getUsername());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("New", user.getName());
        assertEquals("User", user.getSurname());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    public void Test_Display_SelectRegisterWithExistingUsername() throws SQLException {
        // Arrange - Set scanner to select Register (2) and then enter registration details with existing username
        // Adding an extra newline for pressEnterToContinue
        UiConsoleHelper.setScanner(new Scanner("2\nadmin\npassword\nuser@test.com\nNew\nUser\n1\n\n3\n"));

        // Create a test user
        authService.register("admin", "admin", "admin@test.com", "Admin", "User", UserRole.ADMIN);

        // Act
        authMenu.display();

        // Assert - Check that registration failed (only one user with username "admin" should exist)
        assertTrue("Original user should still exist", userDAO.getByUsername("admin").isPresent());
        User existingUser = userDAO.getByUsername("admin").get();
        assertEquals("admin@test.com", existingUser.getEmail()); // Should still have the original email
    }

    @Test
    public void Test_Display_SelectRegisterWithExistingEmail() throws SQLException {
        // Arrange - Set scanner to select Register (2) and then enter registration details with existing email
        // Adding an extra newline for pressEnterToContinue
        UiConsoleHelper.setScanner(new Scanner("2\nnewuser\npassword\nadmin@test.com\nNew\nUser\n1\n\n3\n"));

        // Create a test user
        authService.register("admin", "admin", "admin@test.com", "Admin", "User", UserRole.ADMIN);

        // Act
        authMenu.display();

        // Assert - Check that registration failed
        assertFalse("New user should not be created", userDAO.getByUsername("newuser").isPresent());
    }

    @Test
    public void Test_Display_SelectExit() {
        // Arrange - Set scanner to select Exit (3)
        UiConsoleHelper.setScanner(new Scanner("3\n\n"));

        // Act
        authMenu.display();

        // Assert - Not much to assert here, just make sure it doesn't throw an exception
        assertTrue(true);
    }
}
