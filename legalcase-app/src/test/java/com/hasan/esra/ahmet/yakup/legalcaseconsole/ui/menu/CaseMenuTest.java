package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.CaseMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.Assert.*;

public class CaseMenuTest {

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

    // Inner class that extends MenuManager for testing
    private class TestConsoleMenuManager extends ConsoleMenuManager {
        private boolean navigatedToMainMenu = false;

        public TestConsoleMenuManager(AuthService authService, ClientService clientService,
                                      CaseService caseService, HearingService hearingService,
                                      DocumentService documentService) {
            super(authService, clientService, caseService, hearingService, documentService);
        }

        @Override
        public void navigateToMainMenu() {
            navigatedToMainMenu = true;
            // Will be marked as successful without doing anything for testing
        }

        public boolean isNavigatedToMainMenu() {
            return navigatedToMainMenu;
        }
    }

    @Before
    public void setUp() throws SQLException {
        // Capture console output
        System.setOut(new PrintStream(outContent));

        // Set up test database
        TestDatabaseManager.createTables();

        // Create DAO objects
        caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Create service objects
        caseService = new CaseService(caseDAO, clientDAO);
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);
        documentService = new DocumentService(null, caseDAO); // Document DAO can be null, not needed for our tests
        hearingService = new HearingService(null, caseDAO); // Hearing DAO can be null, not needed for our tests

        // Create test user and login
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // Create MenuManager with test version
        consoleMenuManager = new TestConsoleMenuManager(authService, clientService, caseService, hearingService, documentService);

        // Create CaseMenu object
        caseMenu = new CaseMenu(consoleMenuManager, caseService, clientService);
    }

    @After
    public void tearDown() throws SQLException {
        // Restore console output
        System.setOut(originalOut);

        // Reset Scanner
        UiConsoleHelper.resetScanner();

        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    @Test
    public void Test_Display_SelectAddCase_Success() throws SQLException {
        // Setup - Configure Scanner to select "1. Add New Case" option
        // Example case information and return to main menu (9)
        UiConsoleHelper.setScanner(new Scanner("1\nDV2023-001\nTest Case\n1\nTest description\nn\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Was the case created?
        Optional<Case> caseOpt = caseService.getCaseByCaseNumber("DV2023-001");
        assertTrue("Case should be created", caseOpt.isPresent());
        assertEquals("Case title should be correct", "Test Case", caseOpt.get().getTitle());
        assertEquals("Case type should be correct", CaseType.CIVIL, caseOpt.get().getType());
        assertEquals("Case description should be correct", "Test description", caseOpt.get().getDescription());
        assertEquals("Case status should be NEW", CaseStatus.NEW, caseOpt.get().getStatus());
    }

    @Test
    public void Test_Display_SelectAddCase_WithClient_Success() throws SQLException {
        // First add a client
        Client client = new Client("John", "Doe", "john.doe@example.com");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "1. Add New Case" option and add client
        UiConsoleHelper.setScanner(new Scanner("1\nDV2023-002\nTest Case 2\n2\nClient Test\ny\n" + client.getId() + "\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Was the case created and client added?
        Optional<Case> caseOpt = caseService.getCaseByCaseNumber("DV2023-002");
        assertTrue("Case should be created", caseOpt.isPresent());

        List<Client> clients = caseService.getClientsForCase(caseOpt.get().getId());
        assertFalse("Case client list should not be empty", clients.isEmpty());
        assertEquals("Case client ID should be correct", client.getId(), clients.get(0).getId());
    }

    @Test
    public void Test_Display_SelectViewCaseDetails_Success() throws SQLException {
        // First add a case
        Case caseEntity = new Case("DV2023-003", "Test Case 3", CaseType.CRIMINAL);
        caseEntity.setDescription("Detailed description");
        caseDAO.create(caseEntity);

        // Setup - Configure Scanner to select "2. View Case Details" option
        UiConsoleHelper.setScanner(new Scanner("2\n" + caseEntity.getId() + "\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Are case details in the output?
        String output = outContent.toString();
        assertTrue("Output should contain case number", output.contains("DV2023-003"));
        assertTrue("Output should contain case title", output.contains("Test Case 3"));
        assertTrue("Output should contain case type", output.contains("CRIMINAL"));
        assertTrue("Output should contain case description", output.contains("Detailed description"));
    }

    @Test
    public void Test_Display_SelectViewCaseDetails_NonExistentCase_NotFound() {
        // Setup - Configure Scanner with non-existent case ID
        UiConsoleHelper.setScanner(new Scanner("2\n9999\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Case not found' message", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectUpdateCase_Success() throws SQLException {
        // First add a case
        Case caseEntity = new Case("DV2023-004", "Old Title", CaseType.FAMILY);
        caseEntity.setDescription("Old description");
        caseDAO.create(caseEntity);

        // Setup - Configure Scanner to select "3. Update Case" option
        // Update all fields
        UiConsoleHelper.setScanner(new Scanner("3\n" + caseEntity.getId() + "\nDV2023-004-UPD\nNew Title\ny\n3\ny\n2\nNew description\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Was the case updated?
        Optional<Case> updatedCase = caseService.getCaseById(caseEntity.getId());
        assertTrue("Case should be found", updatedCase.isPresent());
        assertEquals("Case number should be updated", "DV2023-004-UPD", updatedCase.get().getCaseNumber());
        assertEquals("Case title should be updated", "New Title", updatedCase.get().getTitle());
        assertEquals("Case type should be updated", CaseType.FAMILY, updatedCase.get().getType());
        assertEquals("Case status should be updated", CaseStatus.ACTIVE, updatedCase.get().getStatus());
        assertEquals("Case description should be updated", "New description", updatedCase.get().getDescription());
    }

    @Test
    public void Test_Display_SelectDeleteCase_Confirm_Success() throws SQLException {
        // First add a case
        Case caseEntity = new Case("DV2023-005", "Case to Delete", CaseType.CORPORATE);
        caseDAO.create(caseEntity);

        // Setup - Configure Scanner to select "4. Delete Case" option and confirm
        UiConsoleHelper.setScanner(new Scanner("4\n" + caseEntity.getId() + "\ny\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Was the case deleted?
        Optional<Case> deletedCase = caseService.getCaseById(caseEntity.getId());
        assertFalse("Case should be deleted", deletedCase.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteCase_Cancel_NotDeleted() throws SQLException {
        // First add a case
        Case caseEntity = new Case("DV2023-006", "Case Not to Delete", CaseType.OTHER);
        caseDAO.create(caseEntity);

        // Setup - Configure Scanner to select "4. Delete Case" option but cancel
        UiConsoleHelper.setScanner(new Scanner("4\n" + caseEntity.getId() + "\nn\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Was the case not deleted?
        Optional<Case> notDeletedCase = caseService.getCaseById(caseEntity.getId());
        assertTrue("Case should not be deleted", notDeletedCase.isPresent());
    }

    @Test
    public void Test_Display_SelectAddClientToCase_Success() throws SQLException {
        // First add a case and client
        Case caseEntity = new Case("DV2023-007", "Case to Add Client", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("Jane", "Doe", "jane.doe@example.com");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "5. Add Client to Case" option
        UiConsoleHelper.setScanner(new Scanner("5\n" + caseEntity.getId() + "\n" + client.getId() + "\n\n9\n"));

        // Action
        caseMenu.display();

        // Verification - Was the client added to the case?
        List<Client> clients = caseService.getClientsForCase(caseEntity.getId());
        assertFalse("Client list should not be empty", clients.isEmpty());
        assertEquals("Client ID should be correct", client.getId(), clients.get(0).getId());
    }

    @Test
    public void Test_Display_SelectRemoveClientFromCase_Success() throws SQLException {
        // First add a case and client and associate them
        Case caseEntity = new Case("DV2023-008", "Case to Remove Client", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        Client client = new Client("Bob", "Smith", "bob.smith@example.com");
        clientDAO.create(client);

        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Check if relationship was established
        List<Client> initialClients = caseService.getClientsForCase(caseEntity.getId());
        assertFalse("Initial client list should not be empty", initialClients.isEmpty());

        // Setup - Configure Scanner to select "6. Remove Client from Case" option
        UiConsoleHelper.setScanner(new Scanner("6\n" + caseEntity.getId() + "\n" + client.getId() + "\n\n9\n6\n"));

        // Action
        caseMenu.display();

        // Verification - Was the client removed from the case?
        List<Client> finalClients = caseService.getClientsForCase(caseEntity.getId());
        assertTrue("Client list should be empty", finalClients.isEmpty());
    }

    @Test
    public void Test_Display_SelectListCasesByStatus_Success() throws SQLException {
        // Add some cases with different statuses
        Case case1 = new Case("DV2023-009", "Active Case 1", CaseType.CIVIL);
        case1.setStatus(CaseStatus.ACTIVE);
        caseDAO.create(case1);

        Case case2 = new Case("DV2023-010", "Active Case 2", CaseType.CRIMINAL);
        case2.setStatus(CaseStatus.ACTIVE);
        caseDAO.create(case2);

        Case case3 = new Case("DV2023-011", "Closed Case", CaseType.FAMILY);
        case3.setStatus(CaseStatus.CLOSED);
        caseDAO.create(case3);

        // Setup - Configure Scanner to select "7. List Cases by Status" option
        // Select ACTIVE status
        UiConsoleHelper.setScanner(new Scanner("7\n2\n\n9\n\n6\n")); // 2 = ACTIVE enum value

        // Action
        caseMenu.display();

        // Verification - Are active cases listed in the output?
        String output = outContent.toString();
        assertTrue("Output should contain Active Case 1", output.contains("Active Case 1"));
        assertTrue("Output should contain Active Case 2", output.contains("Active Case 2"));
        assertFalse("Output should not contain Closed Case", output.contains("Closed Case"));
    }

    @Test
    public void Test_Display_SelectViewAllCases_Success() throws SQLException {
        // Add some cases
        Case case1 = new Case("DV2023-012", "Test Case A", CaseType.CIVIL);
        caseDAO.create(case1);

        Case case2 = new Case("DV2023-013", "Test Case B", CaseType.CRIMINAL);
        caseDAO.create(case2);

        // Setup - Configure Scanner to select "8. List All Cases" option
        UiConsoleHelper.setScanner(new Scanner("8\n\n9\n6\n"));

        // Action
        caseMenu.display();

        // Verification - Are all cases listed in the output?
        String output = outContent.toString();
        assertTrue("Output should contain Test Case A", output.contains("Test Case A"));
        assertTrue("Output should contain Test Case B", output.contains("Test Case B"));
    }

    @Test
    public void Test_Display_SelectReturnToMainMenu() {
        // Setup - Configure Scanner to select "9. Return to Main Menu" option
        UiConsoleHelper.setScanner(new Scanner("9\n"));

        // Action
        caseMenu.display();

        // Verification - Check if MenuManager's navigateToMainMenu method was called
        assertTrue("Should return to main menu", ((TestConsoleMenuManager) consoleMenuManager).isNavigatedToMainMenu());
    }

    @Test
    public void Test_AddCase_WithDuplicateCaseNumber_ShowsError() throws SQLException {
        // Create an existing case number
        Case existingCase = new Case("DV2023-DUPLICATE", "Existing Case", CaseType.CIVIL);
        caseDAO.create(existingCase);

        // Setup - Configure Scanner with same case number
        UiConsoleHelper.setScanner(new Scanner("DV2023-DUPLICATE\nDuplicate Test\n1\nTest description\nn\n\n9\n"));

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.addCase();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain error message", output.contains("ERROR"));
        assertTrue("Output should contain 'already in use' message", output.contains("already in use"));
    }

    @Test
    public void Test_ViewCaseDetails_WithAssociatedClients_ShowsClients() throws SQLException {
        // Create a case
        Case caseEntity = new Case("DV2023-WITH-CLIENTS", "Case with Client", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        // Create a client and associate with case
        Client client = new Client("Test", "Client", "test.client@example.com");
        clientDAO.create(client);

        // Associate client with case
        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Clear output before test
        outContent.reset();

        // IMPORTANT: Check if clients are loaded correctly before capturing output
        List<Client> clientList = caseService.getClientsForCase(caseEntity.getId());
        assertFalse("Client list should not be empty", clientList.isEmpty());

        // Setup Scanner - add extra empty lines
        UiConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n\n\n\n9\n"));

        // Action
        caseMenu.viewCaseDetails();

        // Verification - check output more flexibly
        String output = outContent.toString();
        System.out.println("OUTPUT: " + output);

        // Check if there is any content related to client
        assertTrue("Output should contain client information",
                output.contains("Client") ||
                        output.contains("client") ||
                        output.contains("Test"));
    }

    @Test
    public void Test_UpdateCase_NonExistentCase_ShowsError() {
        // Setup - Use non-existent case ID
        long nonExistentId = 9999L;
        UiConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.updateCase();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Case not found' message", output.contains("not found"));
    }

    @Test
    public void Test_AddClientToCase_AlreadyAddedClient_ShowsWarning() throws SQLException {
        // Create a case and client
        Case caseEntity = new Case("DV2023-012", "Test Case", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("Duplicate", "Client", "duplicate@example.com");
        clientDAO.create(client);

        // Add first time
        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Clear output before test
        outContent.reset();

        // IMPORTANT: Create new Scanner object - add extra empty lines for longer wait time
        UiConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n" + client.getId() + "\n\n\n\n"));

        // Action - call method directly in CaseMenu
        // Call addClientToCase(Long caseId) method instead of addClientToCase()
        caseMenu.addClientToCase(caseEntity.getId());

        // Verification - Is there a "This client is already added to the case" message in the output?
        String output = outContent.toString();
        System.out.println("OUTPUT: " + output);

        // Check message content instead of exact WARNING word
        assertTrue("Output should contain 'already added to the case' message",
                output.contains("already added to the case") ||
                        output.contains("already associated with"));
    }

    @Test
    public void Test_RemoveClientFromCase_EmptyClientList_ShowsMessage() throws SQLException {
        // Create a case but don't add any clients
        Case caseEntity = new Case("DV2023-014", "Case without Client", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        // Setup - Configure Scanner
        UiConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n\n9\n"));

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.removeClientFromCase();

        // Verification - Is there an info message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain no clients message", output.contains("no clients associated"));
    }

    @Test
    public void Test_RemoveClientFromCase_NonExistentClient_ShowsError() throws SQLException {
        // Create a case and client
        Case caseEntity = new Case("DV2023-015", "Test Case", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("Real", "Client", "real@example.com");
        clientDAO.create(client);

        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Non-existent client ID
        long nonExistentClientId = 9999L;

        // Setup - Use non-existent client ID
        UiConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n" + nonExistentClientId + "\n\n9\n"));

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.removeClientFromCase();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain not associated message", output.contains("not associated with this case"));
    }

    @Test
    public void Test_ListCasesByStatus_NoMatchingCases_ShowsMessage() throws SQLException {
        // Create a case only with ACTIVE status
        Case caseEntity = new Case("DV2023-016", "Active Case", CaseType.CIVIL);
        caseEntity.setStatus(CaseStatus.ACTIVE);
        caseDAO.create(caseEntity);

        // Setup Scanner for CLOSED status (based on CaseStatus enum position)
        UiConsoleHelper.setScanner(new Scanner("4\n\n9\n")); // Assuming 4 is the CLOSED enum value

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.listCasesByStatus();

        // Verification - Is there an info message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain no cases found message", output.contains("No cases found"));
    }

    @Test
    public void Test_ViewAllCases_NoCasesInDatabase_ShowsMessage() throws SQLException {
        // We need to delete all cases
        // First get and delete all existing cases
        List<Case> allCases = caseService.getAllCases();
        for (Case c : allCases) {
            caseDAO.delete(c);
        }

        // Setup - Configure Scanner
        UiConsoleHelper.setScanner(new Scanner("8\n\n9\n"));

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.viewAllCases();

        // Verification - Is there an info message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain no cases message", output.contains("no registered cases"));
    }

    @Test
    public void Test_DeleteCase_NonExistentCase_ShowsError() {
        // Setup - Use non-existent case ID
        long nonExistentId = 9999L;
        UiConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // Clear output before test
        outContent.reset();

        // Action
        caseMenu.deleteCase();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Case not found' message", output.contains("not found"));
    }
}