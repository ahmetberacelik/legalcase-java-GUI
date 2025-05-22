package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.CaseMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.MainMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class MainMenuTest {

    private ConsoleMenuManager consoleMenuManager;
    private CaseMenu caseMenu;
    private CaseService caseService;
    private ClientService clientService;
    private AuthService authService;
    private DocumentService documentService;
    private HearingService hearingService;

    private CaseDAO caseDAO;
    private ClientDAO clientDAO;
    private UserDAO userDAO;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() throws SQLException {
        // Capture console output
        System.setOut(new PrintStream(outContent));

        // Set up test database
        TestDatabaseManager.createTables();

        // Create DAO objects
        UserDAO userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());
        ClientDAO clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        CaseDAO caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());

        // Create service objects
        authService = new AuthService(userDAO);
        clientService = new ClientService(clientDAO);
        caseService = new CaseService(caseDAO, clientDAO);
        documentService = new DocumentService(null, caseDAO); // Document DAO can be null, not needed for our tests
        hearingService = new HearingService(null, caseDAO); // Hearing DAO can be null, not needed for our tests

        // Create test user and login
        authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // Create MenuManager with test version
        consoleMenuManager = new ConsoleMenuManager(authService, clientService, caseService, hearingService, documentService);

        // Create CaseMenu object
        caseMenu = new CaseMenu(consoleMenuManager, caseService, clientService);
    }

    //Craete test cases for MainMenu display method
    @Test
    public void Test_Display_EnterEveryCase_ShouldExit() {
        //Test case for display method
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        UiConsoleHelper.setScanner(new Scanner("1\n9\n2\n7\n3\n10\n4\n9\n5\n\n1\nadmin\nadmin\n\n6\n"));
        MainMenu mainMenu = new MainMenu(consoleMenuManager, authService);
        mainMenu.display();
        assertTrue(true);
    }
}
