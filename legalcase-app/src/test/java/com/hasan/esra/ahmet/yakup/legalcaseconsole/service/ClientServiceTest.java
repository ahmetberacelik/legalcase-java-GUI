package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;

/**
 * @brief Test class for ClientService functionality
 *
 * This class tests all the CRUD operations and business logic of the ClientService class
 * using a test database connection.
 */
public class ClientServiceTest {

    private ConnectionSource connectionSource;
    private ClientDAO clientDAO;
    private ClientService clientService;

    /**
     * @brief Sets up the test environment before each test
     *
     * Establishes a connection to the test database, clears existing data,
     * and initializes the ClientDAO and ClientService objects.
     *
     * @throws SQLException If database connection fails
     */
    @Before
    public void setup() throws SQLException {
        // Get a connection to the test database
        connectionSource = TestDatabaseManager.getConnectionSource();

        // Clear any existing data
        TestDatabaseManager.clearTables();

        // Create a real DAO with the test connection
        clientDAO = new ClientDAO(connectionSource);

        // Create the service with the real DAO
        clientService = new ClientService(clientDAO);
    }

    /**
     * @brief Cleans up the test environment after each test
     *
     * Clears all data from test tables to ensure test isolation.
     *
     * @throws SQLException If database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Clean up after each test
        TestDatabaseManager.clearTables();
    }

    /**
     * @brief Tests client creation with valid data
     *
     * Verifies that a client is properly created with an ID and
     * that all fields match the provided input values.
     */
    @Test
    public void Test_CreateClient_ValidData_ReturnsClientWithId() {
        // Test data
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@example.com";
        String phone = "555-1234";
        String address = "123 Test St";

        // Create client
        Client client = clientService.createClient(name, surname, email, phone, address);

        // Verify the client was created correctly
        assertNotNull("Created client should not be null", client);
        assertNotNull("Client ID should not be null", client.getId());
        assertEquals("Name should match", name, client.getName());
        assertEquals("Surname should match", surname, client.getSurname());
        assertEquals("Email should match", email, client.getEmail());
        assertEquals("Phone should match", phone, client.getPhone());
        assertEquals("Address should match", address, client.getAddress());

        // Verify the client exists in the database
        Optional<Client> retrievedClient = clientService.getClientById(client.getId());
        assertTrue("Client should exist in database", retrievedClient.isPresent());
        assertEquals("Retrieved client ID should match", client.getId(), retrievedClient.get().getId());
    }

    /**
     * @brief Tests that creating a client with a duplicate email throws an exception
     *
     * Verifies that the system enforces email uniqueness by throwing an
     * IllegalArgumentException when a duplicate email is used.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_CreateClient_DuplicateEmail_ThrowsIllegalArgumentException() {
        // Create first client
        clientService.createClient("John", "Doe", "duplicate@example.com", "555-1234", "123 Test St");

        // Try to create another client with same email - should throw IllegalArgumentException
        clientService.createClient("Jane", "Smith", "duplicate@example.com", "555-5678", "456 Test Ave");
    }

    /**
     * @brief Tests client creation with a null email
     *
     * Verifies that a client can be created with a null email field
     * and that the null value is properly stored.
     */
    @Test
    public void Test_CreateClient_NullEmail_ReturnsValidClient() {
        // Test with null email
        Client client = clientService.createClient("Alice", "Johnson", null, "555-7890", "789 Test Blvd");

        assertNotNull("Created client should not be null", client);
        assertNotNull("Client ID should not be null", client.getId());
        assertEquals("Name should match", "Alice", client.getName());
        assertNull("Email should be null", client.getEmail());
    }

    /**
     * @brief Tests retrieving a client by ID when the client exists
     *
     * Verifies that the system can correctly retrieve a client when given
     * a valid ID that exists in the database.
     */
    @Test
    public void Test_GetClientById_ExistingId_ReturnsClient() {
        // Create a client
        Client client = clientService.createClient("Test", "User", "test@example.com", "555-TEST", "Test Address");

        // Retrieve the client by ID
        Optional<Client> retrievedClient = clientService.getClientById(client.getId());

        // Verify
        assertTrue("Client should be found", retrievedClient.isPresent());
        assertEquals("ID should match", client.getId(), retrievedClient.get().getId());
        assertEquals("Name should match", "Test", retrievedClient.get().getName());
    }

    /**
     * @brief Tests retrieving a client by ID when the ID doesn't exist
     *
     * Verifies that the system returns an empty Optional when trying
     * to retrieve a client with a non-existent ID.
     */
    @Test
    public void Test_GetClientById_NonExistentId_ReturnsEmptyOptional() {
        // Try to retrieve a client with a non-existent ID
        Optional<Client> retrievedClient = clientService.getClientById(9999L);

        // Verify
        assertFalse("Non-existent client should not be found", retrievedClient.isPresent());
    }

    /**
     * @brief Tests retrieving a client by email when the email exists
     *
     * Verifies that the system can correctly retrieve a client when given
     * a valid email that exists in the database.
     */
    @Test
    public void Test_GetClientByEmail_ExistingEmail_ReturnsClient() {
        // Create a client
        String email = "unique.email@example.com";
        Client client = clientService.createClient("Email", "Test", email, "555-EMAIL", "Email Address");

        // Retrieve the client by email
        Optional<Client> retrievedClient = clientService.getClientByEmail(email);

        // Verify
        assertTrue("Client should be found", retrievedClient.isPresent());
        assertEquals("Email should match", email, retrievedClient.get().getEmail());
        assertEquals("Name should match", "Email", retrievedClient.get().getName());
    }

    /**
     * @brief Tests retrieving a client by email when the email doesn't exist
     *
     * Verifies that the system returns an empty Optional when trying
     * to retrieve a client with a non-existent email.
     */
    @Test
    public void Test_GetClientByEmail_NonExistentEmail_ReturnsEmptyOptional() {
        // Try to retrieve a client with a non-existent email
        Optional<Client> retrievedClient = clientService.getClientByEmail("nonexistent@example.com");

        // Verify
        assertFalse("Non-existent client should not be found", retrievedClient.isPresent());
    }

    /**
     * @brief Tests updating a client with valid data
     *
     * Verifies that a client's information can be successfully updated
     * and that the changes are correctly persisted to the database.
     */
    @Test
    public void Test_UpdateClient_ValidData_ReturnsUpdatedClient() {
        // Create a client
        Client client = clientService.createClient("Original", "User", "original@example.com", "555-ORIG", "Original Address");

        // Update the client
        Client updatedClient = clientService.updateClient(
                client.getId(), "Updated", "Name", "updated@example.com", "555-UPDT", "Updated Address");

        // Verify the update was successful
        assertEquals("Name should be updated", "Updated", updatedClient.getName());
        assertEquals("Surname should be updated", "Name", updatedClient.getSurname());
        assertEquals("Email should be updated", "updated@example.com", updatedClient.getEmail());

        // Verify the update persisted to the database
        Optional<Client> retrievedClient = clientService.getClientById(client.getId());
        assertTrue("Client should exist", retrievedClient.isPresent());
        assertEquals("Updated name should be saved", "Updated", retrievedClient.get().getName());
    }

    /**
     * @brief Tests updating a client with an email that already exists for another client
     *
     * Verifies that the system enforces email uniqueness when updating a client
     * by throwing an IllegalArgumentException when attempting to use a duplicate email.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateClient_DuplicateEmail_ThrowsIllegalArgumentException() throws SQLException {
        // Create two clients
        Client client1 = clientService.createClient("First", "Client", "first@example.com", "555-1111", "First Address");
        Client client2 = clientService.createClient("Second", "Client", "second@example.com", "555-2222", "Second Address");

        // Try to update client2 with client1's email - should throw IllegalArgumentException
        clientService.updateClient(client2.getId(), client2.getName(), client2.getSurname(), "first@example.com", client2.getPhone(), client2.getAddress());
    }

    /**
     * @brief Tests updating a client that doesn't exist
     *
     * Verifies that the system throws an IllegalArgumentException when
     * attempting to update a client with a non-existent ID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateClient_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to update a client with a non-existent ID
        clientService.updateClient(9999L, "NonExistent", "Client", "nonexistent@example.com", "555-9999", "NonExistent Address");
    }

    /**
     * @brief Tests deleting a client that exists
     *
     * Verifies that a client can be successfully deleted from the database
     * and that it can no longer be retrieved after deletion.
     */
    @Test
    public void Test_DeleteClient_ExistingId_ClientIsDeleted() {
        // Create a client
        Client client = clientService.createClient("Delete", "Me", "delete@example.com", "555-DEL", "Delete Address");

        // Verify the client exists
        assertTrue("Client should exist before deletion",
                clientService.getClientById(client.getId()).isPresent());

        // Delete the client
        clientService.deleteClient(client.getId());

        // Verify the client no longer exists
        assertFalse("Client should not exist after deletion",
                clientService.getClientById(client.getId()).isPresent());
    }

    /**
     * @brief Tests deleting a client that doesn't exist
     *
     * Verifies that the system throws an IllegalArgumentException when
     * attempting to delete a client with a non-existent ID.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_DeleteClient_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to delete a client with a non-existent ID
        clientService.deleteClient(9999L);
    }

    /**
     * @brief Tests retrieving all clients when multiple clients exist
     *
     * Verifies that the system correctly returns all clients in the database
     * and that the count matches the expected number.
     */
    @Test
    public void Test_GetAllClients_MultipleClients_ReturnsAllClients() {
        // Create several clients
        clientService.createClient("User", "One", "user1@example.com", "555-1111", "Address 1");
        clientService.createClient("User", "Two", "user2@example.com", "555-2222", "Address 2");
        clientService.createClient("User", "Three", "user3@example.com", "555-3333", "Address 3");

        // Get all clients
        List<Client> clients = clientService.getAllClients();

        // Verify
        assertNotNull("Client list should not be null", clients);
        assertEquals("Should have 3 clients", 3, clients.size());
    }

    /**
     * @brief Tests retrieving all clients when the database is empty
     *
     * Verifies that the system returns an empty list when there are no clients
     * in the database, rather than null or throwing an exception.
     */
    @Test
    public void Test_GetAllClients_NoClients_ReturnsEmptyList() {
        // Get all clients without creating any
        List<Client> clients = clientService.getAllClients();

        // Verify
        assertNotNull("Client list should not be null", clients);
        assertTrue("Client list should be empty", clients.isEmpty());
    }

    /**
     * @brief Tests searching for clients by name with matches
     *
     * Verifies that the system correctly finds and returns clients
     * that match the search term in their name or surname.
     */
    @Test
    public void Test_SearchClients_MatchingName_ReturnsMatchingClients() {
        // Create clients with different names
        clientService.createClient("John", "Smith", "john@example.com", "555-JOHN", "John's Address");
        clientService.createClient("Jane", "Smith", "jane@example.com", "555-JANE", "Jane's Address");
        clientService.createClient("Bob", "Jones", "bob@example.com", "555-BOB", "Bob's Address");

        // Search for Smiths
        List<Client> smiths = clientService.searchClients("Smith");

        // Verify
        assertNotNull("Search results should not be null", smiths);
        assertEquals("Should find 2 Smiths", 2, smiths.size());

        // Search for Bob
        List<Client> bobs = clientService.searchClients("Bob");

        // Verify
        assertNotNull("Search results should not be null", bobs);
        assertEquals("Should find 1 Bob", 1, bobs.size());
        assertEquals("Should find Bob Jones", "Jones", bobs.get(0).getSurname());
    }

    /**
     * @brief Tests searching for clients by name with no matches
     *
     * Verifies that the system returns an empty list when no clients
     * match the search term, rather than null or throwing an exception.
     */
    @Test
    public void Test_SearchClients_NoMatch_ReturnsEmptyList() {
        // Create some clients
        clientService.createClient("John", "Smith", "john@example.com", "555-JOHN", "John's Address");
        clientService.createClient("Jane", "Smith", "jane@example.com", "555-JANE", "Jane's Address");

        // Search for a non-existent name
        List<Client> results = clientService.searchClients("Nonexistent");

        // Verify
        assertNotNull("Search results should not be null", results);
        assertTrue("Should find no clients", results.isEmpty());
    }

    /**
     * @brief Tests that a SQLException during client creation is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_CreateClient_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Test data
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@example.com";
        String phone = "555-1234";
        String address = "123 Test St";

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.createClient(name, surname, email, phone, address);
    }

    /**
     * @brief Tests that a SQLException during client retrieval by ID is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetClientById_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.getClientById(1L);
    }

    /**
     * @brief Tests that a SQLException during client retrieval by email is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetClientByEmail_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.getClientByEmail("test@example.com");
    }

    /**
     * @brief Tests that a SQLException during retrieval of all clients is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetAllClients_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.getAllClients();
    }

    /**
     * @brief Tests that a SQLException during client search is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_SearchClients_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.searchClients("Smith");
    }

    /**
     * @brief Tests that a SQLException during client update is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_UpdateClient_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.updateClient(1L, "Updated", "Name", "updated@example.com", "555-UPDT", "Updated Address");
    }

    /**
     * @brief Tests that a SQLException during client deletion is properly handled
     *
     * Verifies that the system wraps SQL exceptions in RuntimeExceptions
     * to maintain the service layer's exception contract.
     */
    @Test(expected = RuntimeException.class)
    public void Test_DeleteClient_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        clientService = new ClientService(new ThrowingClientDAO(connectionSource));

        // Execute - This should throw RuntimeException due to underlying SQLException
        clientService.deleteClient(1L);
    }

    /**
     * @brief Tests updating a client's email to null
     *
     * Verifies that a client's email can be successfully updated to null
     * and that the change is correctly persisted to the database.
     */
    @Test
    public void Test_UpdateClient_NullEmail_SuccessfullyUpdatesClient() {
        // Create a client with an email
        Client client = clientService.createClient("Original", "User", "original@example.com", "555-ORIG", "Original Address");

        // Update the client with a null email
        Client updatedClient = clientService.updateClient(
                client.getId(), "Updated", "Name", null, "555-UPDT", "Updated Address");

        // Verify the update was successful
        assertEquals("Name should be updated", "Updated", updatedClient.getName());
        assertEquals("Surname should be updated", "Name", updatedClient.getSurname());
        assertNull("Email should be null", updatedClient.getEmail());

        // Verify the update persisted to the database
        Optional<Client> retrievedClient = clientService.getClientById(client.getId());
        assertTrue("Client should exist", retrievedClient.isPresent());
        assertNull("Email should be null in database", retrievedClient.get().getEmail());
    }

    /**
     * @brief Tests updating a client with the same email
     *
     * Verifies that a client can be updated with its own email address
     * without triggering the email uniqueness check.
     */
    @Test
    public void Test_UpdateClient_SameEmail_SkipsEmailCheck() {
        // Create a client
        String originalEmail = "same.email@example.com";
        Client client = clientService.createClient("Original", "User", originalEmail, "555-ORIG", "Original Address");

        // Create another client to ensure email clash would occur if checked
        clientService.createClient("Other", "User", "other.email@example.com", "555-OTHER", "Other Address");

        // Update the first client without changing email - this should not trigger email uniqueness check
        Client updatedClient = clientService.updateClient(
                client.getId(), "Updated", "Name", originalEmail, "555-UPDT", "Updated Address");

        // Verify the update was successful
        assertEquals("Name should be updated", "Updated", updatedClient.getName());
        assertEquals("Email should remain the same", originalEmail, updatedClient.getEmail());
    }

    /**
     * @brief A mock ClientDAO that throws SQLException for all operations
     *
     * This inner class is used to test exception handling in the ClientService
     * by forcing SQLExceptions to be thrown from the DAO layer.
     */
    private static class ThrowingClientDAO extends ClientDAO {

        /**
         * @brief Constructor that initializes the DAO with a connection source
         *
         * @param connectionSource The database connection source
         * @throws SQLException If initialization fails
         */
        public ThrowingClientDAO(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource);
        }

        /**
         * @brief Override of the create method to throw SQLException
         *
         * @param client The client to create
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public Client create(Client client) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the getById method to throw SQLException
         *
         * @param id The ID to look up
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public Optional<Client> getById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the getByEmail method to throw SQLException
         *
         * @param email The email to look up
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public Optional<Client> getByEmail(String email) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the getAll method to throw SQLException
         *
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public List<Client> getAll() throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the searchByName method to throw SQLException
         *
         * @param searchTerm The term to search for
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public List<Client> searchByName(String searchTerm) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the update method to throw SQLException
         *
         * @param client The client to update
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public int update(Client client) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the delete method to throw SQLException
         *
         * @param client The client to delete
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public int delete(Client client) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of the deleteById method to throw SQLException
         *
         * @param id The ID of the client to delete
         * @return Never returns due to exception
         * @throws SQLException Always throws this exception
         */
        @Override
        public int deleteById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }
    }
}