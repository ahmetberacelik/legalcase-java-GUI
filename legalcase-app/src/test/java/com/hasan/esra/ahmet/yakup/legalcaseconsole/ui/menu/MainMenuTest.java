package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class MainMenuTest {

    private MenuManager menuManager;
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

        // Konsol çıktısını yakalama
        System.setOut(new PrintStream(outContent));

        // Test veritabanını kurma
        TestDatabaseManager.createTables();

        // DAO nesnelerini oluşturma
        caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Service nesnelerini oluşturma
        caseService = new CaseService(caseDAO, clientDAO);
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);
        documentService = new DocumentService(null, caseDAO); // Belge DAO'su null olabilir, bizim testlerimiz için gerekli değil
        hearingService = new HearingService(null, caseDAO); // Duruşma DAO'su null olabilir, bizim testlerimiz için gerekli değil

        // Test kullanıcısı oluşturup giriş yapalım
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // MenuManager'ı test versiyonuyla oluşturma
        menuManager = new MenuManager(authService, clientService, caseService, hearingService, documentService);

        // CaseMenu nesnesi oluşturma
        caseMenu = new CaseMenu(menuManager, caseService, clientService);
    }

    //Craete test cases for MainMenu display method
    @Test
    public void Test_Display_EnterEveryCase_ShouldExit() {
        //Test case for display method
        authService.register("admin", "admin", "admin", "admin", "admin", UserRole.ADMIN);
        ConsoleHelper.setScanner(new Scanner("1\n9\n2\n7\n3\n10\n4\n9\n5\n\n1\nadmin\nadmin\n\n6\n"));
        MainMenu mainMenu = new MainMenu(menuManager, authService);
        mainMenu.display();
        assertTrue(true);
    }
}
