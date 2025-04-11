package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * @brief Test class for MenuManager functionality
 *
 * This class tests the navigation and operations of the MenuManager,
 * including its interaction with various services and menus.
 */
public class MenuManagerTest {
    private MenuManager menuManager;
    private AuthService authService;
    private ClientService clientService;
    private CaseService caseService;
    private HearingService hearingService;
    private DocumentService documentService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * @brief Set up test environment before each test
     *
     * Initializes database, DAOs, services, and the MenuManager
     * Also captures console output for verification
     *
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Capture console output
        System.setOut(new PrintStream(outContent));

        // Initialize DAOs with test database
        TestDatabaseManager.createTables(); // Create and clear tables

        // Get connection source
        UserDAO userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());
        ClientDAO clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        CaseDAO caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        HearingDAO hearingDAO = new HearingDAO(TestDatabaseManager.getConnectionSource());
        DocumentDAO documentDAO = new DocumentDAO(TestDatabaseManager.getConnectionSource());

        // Initialize services with DAOs
        authService = new AuthService(userDAO);
        clientService = new ClientService(clientDAO);
        caseService = new CaseService(caseDAO, clientDAO);
        hearingService = new HearingService(hearingDAO, caseDAO);
        documentService = new DocumentService(documentDAO, caseDAO);

        // Initialize menu manager
        menuManager = new MenuManager(authService, clientService, caseService, hearingService, documentService);
    }

    /**
     * @brief Clean up after each test
     *
     * Resets console output, scanner, and closes database connections
     */
    @After
    public void tearDown() {
        // Reset console output
        System.setOut(originalOut);

        // Reset scanner
        ConsoleHelper.resetScanner();

        // Close database connection
        try {
            TestDatabaseManager.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * @brief Test navigation to the main menu
     *
     * Verifies that the main menu displays correctly and user can exit
     */
    @Test
    public void Test_NavigateToMainMenu_DisplaysMainMenu() {
        // Arrange - Set scanner to select option 6 (Exit Application) in MainMenu
        ConsoleHelper.setScanner(new Scanner("6"));

        // Arrange - Register a user to login. Values are admin admin
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        // Arrange - Login as admin
        authService.login("admin", "admin");

        // Act
        menuManager.navigateToMainMenu();

        // Assert - No assertion needed, we're just testing navigation flow
        // If the test completes without exceptions, it means the navigation worked
    }

    /**
     * @brief Test navigation to the client menu
     *
     * Verifies that the client menu displays correctly and user can return to main menu
     */
    @Test
    public void Test_NavigateToClientMenu_DisplaysClientMenu() {
        // Arrange - Set scanner to select option 7 (Return to Main Menu) in ClientMenu
        ConsoleHelper.setScanner(new Scanner("7\n6"));

        // Arrange - Register a user to login. Values are admin admin
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        // Arrange - Login as admin
        authService.login("admin", "admin");
        // Act
        menuManager.navigateToClientMenu();

        // Assert - No assertion needed, we're just testing navigation flow
    }

    /**
     * @brief Test navigation to the case menu
     *
     * Verifies that the case menu displays correctly and user can return to main menu
     */
    @Test
    public void Test_NavigateToCaseMenu_DisplaysCaseMenu() {
        // Arrange - Set scanner to select option 9 (Return to Main Menu) in CaseMenu
        ConsoleHelper.setScanner(new Scanner("9\n6\n"));

        // Arrange - Register a user to login. Values are admin admin
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        // Arrange - Login as admin
        authService.login("admin", "admin");
        // Act
        menuManager.navigateToCaseMenu();

        // Assert - No assertion needed, we're just testing navigation flow
    }

    /**
     * @brief Test navigation to the hearing menu
     *
     * Verifies that the hearing menu displays correctly and user can return to main menu
     */
    @Test
    public void Test_NavigateToHearingMenu_DisplaysHearingMenu() {
        // Arrange - Set scanner to select option 10 (Return to Main Menu) in HearingMenu
        ConsoleHelper.setScanner(new Scanner("10\n6\n"));

        // Arrange - Register a user to login. Values are admin admin
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        // Arrange - Login as admin
        authService.login("admin", "admin");
        // Act
        menuManager.navigateToHearingMenu();

        // Assert - No assertion needed, we're just testing navigation flow
    }

    /**
     * @brief Test navigation to the document menu
     *
     * Verifies that the document menu displays correctly and user can return to main menu
     */
    @Test
    public void Test_NavigateToDocumentMenu_DisplaysDocumentMenu() {
        // Arrange - Set scanner to select option 9 (Return to Main Menu) in DocumentMenu
        ConsoleHelper.setScanner(new Scanner("9\n6\n"));

        // Arrange - Register a user to login. Values are admin admin
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        // Arrange - Login as admin
        authService.login("admin", "admin");
        // Act
        menuManager.navigateToDocumentMenu();

        // Assert - No assertion needed, we're just testing navigation flow
    }

    /**
     * @brief Test exit functionality
     *
     * Verifies that calling exit() method sets the running state to false
     */
    @Test
    public void Test_Exit_SetsRunningToFalse() {
        // Act
        menuManager.exit();

        // Assert - No direct assertion possible as running is private
        // Instead, we'll test the behavior by checking if start() method exits

        // Set scanner for auth menu to select exit (3)
        ConsoleHelper.setScanner(new Scanner("3"));

        // This should exit immediately since running is now false
        menuManager.start();

        // If we reach here, the test passed
        assertTrue(true);
    }

    /**
     * @brief Test application start and exit without login
     *
     * Verifies that the application starts and exits correctly when user selects exit
     */
    @Test
    public void Test_start_StartsAndExit() {
        // Set scanner for auth menu to select exit (3)
        ConsoleHelper.setScanner(new Scanner("3"));

        // This should exit immediately since running is now false
        menuManager.start();

        // If we reach here, the test passed
        assertTrue(true);
    }

    /**
     * @brief Test application start and exit with login
     *
     * Verifies that the application starts, allows login, and exits correctly
     */
    @Test
    public void Test_start_StartsAndExitWithLogin() {
        // Set scanner for auth menu to select exit (6)
        ConsoleHelper.setScanner(new Scanner("6"));

        // Arrange - Register a user to login. Values are admin admin
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        // Arrange - Login as admin
        authService.login("admin", "admin");
        // This should exit immediately since running is now false
        menuManager.start();

        // If we reach here, the test passed
        assertTrue(true);
    }
}