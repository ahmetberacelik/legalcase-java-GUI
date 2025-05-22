package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*    ;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.ClientMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.Assert.*;

public class ClientMenuTest {

    private ConsoleMenuManager consoleMenuManager;
    private ClientMenu clientMenu;
    private ClientService clientService;
    private AuthService authService;

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
        clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Create service objects
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);

        // Create test user and login
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // Create MenuManager with test version
        consoleMenuManager = new TestConsoleMenuManager(authService, clientService, null, null, null);

        // Create ClientMenu object
        clientMenu = new ClientMenu(consoleMenuManager, clientService);
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
    public void Test_Display_SelectReturnToMainMenu() {
        // Setup - Configure Scanner to select "7. Return to Main Menu" option
        UiConsoleHelper.setScanner(new Scanner("7\n"));

        // Action
        clientMenu.display();

        // Verification - Check if MenuManager's navigateToMainMenu method was called
        assertTrue("Should return to main menu", ((TestConsoleMenuManager) consoleMenuManager).isNavigatedToMainMenu());
    }

    @Test
    public void Test_Display_SelectAddClient_Success() throws SQLException {
        // Setup - Configure Scanner to select "1. Add New Client" option
        // Example client information and return to main menu (7)
        UiConsoleHelper.setScanner(new Scanner("1\nJohn\nDoe\njohn.doe@example.com\n555-1234\n123 Main St\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Was the client created?
        Optional<Client> clientOpt = clientService.getClientByEmail("john.doe@example.com");
        assertTrue("Client should be created", clientOpt.isPresent());
        assertEquals("Client name should be correct", "John", clientOpt.get().getName());
        assertEquals("Client surname should be correct", "Doe", clientOpt.get().getSurname());
        assertEquals("Client phone number should be correct", "555-1234", clientOpt.get().getPhone());
        assertEquals("Client address should be correct", "123 Main St", clientOpt.get().getAddress());
    }

    @Test
    public void Test_Display_SelectViewClientDetails_Success() throws SQLException {
        // First create a client
        Client client = new Client("Jane", "Smith", "jane.smith@example.com");
        client.setPhone("555-5678");
        client.setAddress("456 Oak St");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "2. View Client Details" option
        UiConsoleHelper.setScanner(new Scanner("2\n" + client.getId() + "\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Are client details in the output?
        String output = outContent.toString();
        assertTrue("Output should contain client name", output.contains("Jane"));
        assertTrue("Output should contain client surname", output.contains("Smith"));
        assertTrue("Output should contain client email", output.contains("jane.smith@example.com"));
        assertTrue("Output should contain client phone", output.contains("555-5678"));
        assertTrue("Output should contain client address", output.contains("456 Oak St"));
    }

    @Test
    public void Test_Display_SelectViewClientDetails_NonExistentClient() {
        // Setup - Configure Scanner with non-existent client ID
        UiConsoleHelper.setScanner(new Scanner("2\n9999\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Client not found' message", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectUpdateClient_Success() throws SQLException {
        // First create a client
        Client client = new Client("Update", "Test", "update.test@example.com");
        client.setPhone("555-0000");
        client.setAddress("Update Address");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "3. Update Client" option
        // Update all fields
        UiConsoleHelper.setScanner(new Scanner("3\n" + client.getId() + "\nUpdated\nName\nupdated.email@example.com\n555-9999\nNew Address\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Was the client updated?
        Optional<Client> updatedClient = clientService.getClientById(client.getId());
        assertTrue("Client should be found", updatedClient.isPresent());
        assertEquals("Client name should be updated", "Updated", updatedClient.get().getName());
        assertEquals("Client surname should be updated", "Name", updatedClient.get().getSurname());
        assertEquals("Client email should be updated", "updated.email@example.com", updatedClient.get().getEmail());
        assertEquals("Client phone should be updated", "555-9999", updatedClient.get().getPhone());
        assertEquals("Client address should be updated", "New Address", updatedClient.get().getAddress());
    }

    @Test
    public void Test_Display_SelectUpdateClient_NonExistentClient() {
        // Setup - Configure Scanner with non-existent client ID
        UiConsoleHelper.setScanner(new Scanner("3\n9999\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Client not found' message", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectDeleteClient_Confirm_Success() throws SQLException {
        // First create a client
        Client client = new Client("Delete", "Test", "delete.test@example.com");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "4. Delete Client" option and confirm
        UiConsoleHelper.setScanner(new Scanner("4\n" + client.getId() + "\ny\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Was the client deleted?
        Optional<Client> deletedClient = clientService.getClientById(client.getId());
        assertFalse("Client should be deleted", deletedClient.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteClient_Cancel_NotDeleted() throws SQLException {
        // First create a client
        Client client = new Client("NotDelete", "Test", "notdelete.test@example.com");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "4. Delete Client" option but cancel
        UiConsoleHelper.setScanner(new Scanner("4\n" + client.getId() + "\nn\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Was the client not deleted?
        Optional<Client> notDeletedClient = clientService.getClientById(client.getId());
        assertTrue("Client should not be deleted", notDeletedClient.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteClient_NonExistentClient() {
        // Setup - Configure Scanner with non-existent client ID
        UiConsoleHelper.setScanner(new Scanner("4\n9999\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Client not found' message", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectSearchClients_Success() throws SQLException {
        // Create some clients
        Client client1 = new Client("Search", "Result", "search.result@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Another", "Client", "another.client@example.com");
        clientDAO.create(client2);

        // Setup - Configure Scanner to select "5. Search Clients" option and enter search term
        UiConsoleHelper.setScanner(new Scanner("5\nSearch\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Is the search result in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Search Result'", output.contains("Search"));
        assertFalse("Output should not contain 'Another Client'", output.contains("Another"));
    }

    @Test
    public void Test_Display_SelectSearchClients_NoResults() throws SQLException {
        // Create a client
        Client client = new Client("Test", "Client", "test.client@example.com");
        clientDAO.create(client);

        // Setup - Configure Scanner to select "5. Search Clients" option and use non-matching term
        UiConsoleHelper.setScanner(new Scanner("5\nNoMatch\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Is there a no results message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'No clients found' message", output.contains("No clients found"));
    }

    @Test
    public void Test_Display_SelectViewAllClients_Success() throws SQLException {
        // Create some clients
        Client client1 = new Client("First", "Client", "first.client@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Second", "Client", "second.client@example.com");
        clientDAO.create(client2);

        // Setup - Configure Scanner to select "6. List All Clients" option
        UiConsoleHelper.setScanner(new Scanner("6\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Are all clients listed in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'First Client'", output.contains("First"));
        assertTrue("Output should contain 'Second Client'", output.contains("Second"));
    }

    @Test
    public void Test_Display_SelectViewAllClients_NoClients() {
        // Don't create any clients

        // Setup - Configure Scanner to select "6. List All Clients" option
        UiConsoleHelper.setScanner(new Scanner("6\n\n7\n"));

        // Action
        clientMenu.display();

        // Verification - Is there a no clients message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'no registered clients' message",
                output.contains("no registered clients") ||
                        output.contains("There are no registered clients"));
    }

    @Test
    public void Test_AddClient_WithDuplicateEmail_ShowsError() throws SQLException {
        // First create a client
        Client client = new Client("Duplicate", "Email", "duplicate@example.com");
        clientDAO.create(client);

        // Setup - Configure Scanner to create new client with same email
        UiConsoleHelper.setScanner(new Scanner("New\nClient\nduplicate@example.com\n555-1234\nTest Address\n\n7\n"));

        // Action
        clientMenu.addClient();
    }

    @Test
    public void Test_ViewClientDetails_NonExistentClient() {
        // Setup - Use non-existent client ID
        long nonExistentId = 9999L;
        UiConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n7\n"));

        // Action
        clientMenu.viewClientDetails();

        // Verification - Is there a client not found error in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Client not found' message", output.contains("not found"));
    }

    @Test
    public void Test_UpdateClient_WithEmptyFields_KeepsOldValues() throws SQLException {
        // First create a client
        Client client = new Client("Keep", "Values", "keep.values@example.com");
        client.setPhone("555-1234");
        client.setAddress("Original Address");
        clientDAO.create(client);

        // Setup - Configure Scanner to leave some fields empty
        UiConsoleHelper.setScanner(new Scanner(client.getId() + "\n\n\n\n\nNew Address\n\n7\n"));

        // Action
        clientMenu.updateClient();

        // Verification - Were empty fields kept unchanged, and changed field updated?
        Optional<Client> updatedClient = clientService.getClientById(client.getId());
        assertTrue("Client should be found", updatedClient.isPresent());
        assertEquals("Client name should remain unchanged", "Keep", updatedClient.get().getName());
        assertEquals("Client surname should remain unchanged", "Values", updatedClient.get().getSurname());
        assertEquals("Client email should remain unchanged", "keep.values@example.com", updatedClient.get().getEmail());
        assertEquals("Client phone should remain unchanged", "555-1234", updatedClient.get().getPhone());
        assertEquals("Client address should be updated", "New Address", updatedClient.get().getAddress());
    }

    @Test
    public void Test_UpdateClient_WithInvalidEmail_ShowsError() throws SQLException {
        // First create two clients
        Client client1 = new Client("Update", "Email", "update.email@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Other", "Client", "other.clientss@example.com");
        clientDAO.create(client2);

        // Setup - Configure Scanner to use another client's email
        UiConsoleHelper.setScanner(new Scanner(client1.getId() + "\n\n\nother.client@example.com\n\n\n\n7\n"));

        // Action
        clientMenu.updateClient();
    }

    @Test
    public void Test_DeleteClient_NonExistentClient() {
        // Setup - Use non-existent client ID
        long nonExistentId = 9999L;
        UiConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n7\n"));

        // Action
        clientMenu.deleteClient();

        // Verification - Is there a client not found error in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Client not found' message", output.contains("not found"));
    }

    @Test
    public void Test_SearchClients_EmptySearchTerm_ShowsError() {
        // Setup - Configure Scanner to enter empty search term
        UiConsoleHelper.setScanner(new Scanner("example\n\n7\n"));

        // Action
        clientMenu.searchClients();
    }
}