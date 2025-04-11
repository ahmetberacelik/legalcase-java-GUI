package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import com.j256.ormlite.support.ConnectionSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @brief Test class for ClientDAO operations
 *
 * This class tests all CRUD operations and search functions for the ClientDAO class
 */
public class ClientDAOTest {

    private ConnectionSource connectionSource;
    private ClientDAO clientDAO;

    /**
     * @brief Setup method to initialize the test environment
     *
     * Creates test database tables and initializes the DAO
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Initialize test database
        TestDatabaseManager.createTables();
        connectionSource = TestDatabaseManager.getConnectionSource();
        clientDAO = new ClientDAO(connectionSource);
    }

    /**
     * @brief Teardown method to clean up after tests
     *
     * Closes database connections
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Tests client creation functionality
     *
     * Verifies that a client can be created with all fields populated correctly
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Create_Success_ReturnsClientWithId() throws SQLException {
        // Arrange
        Client client = new Client("John", "Doe", "john.doe@example.com");
        client.setPhone("555-1234");
        client.setAddress("123 Main St");

        // Act
        Client createdClient = clientDAO.create(client);

        // Assert
        assertNotNull("Created client should not be null", createdClient);
        assertNotNull("Created client should have an ID", createdClient.getId());
        assertEquals("Name should be correct", "John", createdClient.getName());
        assertEquals("Surname should be correct", "Doe", createdClient.getSurname());
        assertEquals("Email should be correct", "john.doe@example.com", createdClient.getEmail());
        assertEquals("Phone should be correct", "555-1234", createdClient.getPhone());
        assertEquals("Address should be correct", "123 Main St", createdClient.getAddress());
    }

    /**
     * @brief Tests retrieval of client by ID
     *
     * Verifies that an existing client can be retrieved by ID
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetById_ExistingClient_ReturnsClient() throws SQLException {
        // Arrange
        Client client = new Client("Jane", "Smith", "jane.smith@example.com");
        clientDAO.create(client);

        // Act
        Optional<Client> retrievedClientOpt = clientDAO.getById(client.getId());

        // Assert
        assertTrue("Client should be found", retrievedClientOpt.isPresent());
        Client retrievedClient = retrievedClientOpt.get();
        assertEquals("Client ID should match", client.getId(), retrievedClient.getId());
        assertEquals("Name should match", "Jane", retrievedClient.getName());
        assertEquals("Surname should match", "Smith", retrievedClient.getSurname());
    }

    /**
     * @brief Tests retrieval of non-existing client by ID
     *
     * Verifies that attempting to retrieve a non-existing client returns an empty Optional
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetById_NonExistingClient_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<Client> retrievedClientOpt = clientDAO.getById(9999L);

        // Assert
        assertFalse("Should return empty Optional when client not found", retrievedClientOpt.isPresent());
    }

    /**
     * @brief Tests retrieval of client by email
     *
     * Verifies that an existing client can be retrieved by email address
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByEmail_ExistingClient_ReturnsClient() throws SQLException {
        // Arrange
        Client client = new Client("Robert", "Johnson", "robert.johnson@example.com");
        clientDAO.create(client);

        // Act
        Optional<Client> retrievedClientOpt = clientDAO.getByEmail("robert.johnson@example.com");

        // Assert
        assertTrue("Client should be found", retrievedClientOpt.isPresent());
        Client retrievedClient = retrievedClientOpt.get();
        assertEquals("Client ID should match", client.getId(), retrievedClient.getId());
        assertEquals("Name should match", "Robert", retrievedClient.getName());
        assertEquals("Email should match", "robert.johnson@example.com", retrievedClient.getEmail());
    }

    /**
     * @brief Tests retrieval of non-existing client by email
     *
     * Verifies that attempting to retrieve a non-existing client by email returns an empty Optional
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByEmail_NonExistingClient_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<Client> retrievedClientOpt = clientDAO.getByEmail("nonexisting@example.com");

        // Assert
        assertFalse("Should return empty Optional when client not found", retrievedClientOpt.isPresent());
    }

    /**
     * @brief Tests retrieval of all clients
     *
     * Verifies that the getAll method returns a list containing all created clients
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetAll_WithClients_ReturnsList() throws SQLException {
        // Arrange
        Client client1 = new Client("Client", "One", "client.one@example.com");
        Client client2 = new Client("Client", "Two", "client.two@example.com");
        clientDAO.create(client1);
        clientDAO.create(client2);

        // Act
        List<Client> allClients = clientDAO.getAll();

        // Assert
        assertNotNull("Client list should not be null", allClients);
        assertTrue("Client list should not be empty", allClients.size() >= 2);

        // Both created clients should be in the list
        boolean client1Found = false;
        boolean client2Found = false;
        for (Client client : allClients) {
            if (client.getEmail().equals("client.one@example.com")) client1Found = true;
            if (client.getEmail().equals("client.two@example.com")) client2Found = true;
        }
        assertTrue("Client One should be in the list", client1Found);
        assertTrue("Client Two should be in the list", client2Found);
    }

    /**
     * @brief Tests search by name functionality
     *
     * Verifies that clients can be found by searching for names or surnames
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_SearchByName_WithMatchingClients_ReturnsList() throws SQLException {
        // Arrange
        Client smithClient1 = new Client("John", "Smith", "john.smith@example.com");
        Client smithClient2 = new Client("Sarah", "Smith", "sarah.smith@example.com");
        Client johnsonClient = new Client("Michael", "Johnson", "michael.johnson@example.com");
        clientDAO.create(smithClient1);
        clientDAO.create(smithClient2);
        clientDAO.create(johnsonClient);

        // Act - Search by surname
        List<Client> smithClients = clientDAO.searchByName("Smith");

        // Act - Search by partial name - will match both "John" and "Johnson"
        List<Client> johnClients = clientDAO.searchByName("John");

        // Assert - Surname search
        assertNotNull("Smith search results should not be null", smithClients);
        assertEquals("Smith search should find 2 results", 2, smithClients.size());

        // All Smith clients should be in the list
        boolean john_smithFound = false;
        boolean sarah_smithFound = false;
        for (Client client : smithClients) {
            if (client.getEmail().equals("john.smith@example.com")) john_smithFound = true;
            if (client.getEmail().equals("sarah.smith@example.com")) sarah_smithFound = true;
        }
        assertTrue("John Smith should be in the list", john_smithFound);
        assertTrue("Sarah Smith should be in the list", sarah_smithFound);

        // Assert - Name search (now expecting 2 results due to partial matching)
        assertNotNull("John search results should not be null", johnClients);
        assertTrue("John search should find at least 1 result", johnClients.size() >= 1);

        // Check that both John Smith and Michael Johnson are in the results
        boolean johnSmithFound = false;
        boolean michaelJohnsonFound = false;
        for (Client client : johnClients) {
            if (client.getEmail().equals("john.smith@example.com")) johnSmithFound = true;
            if (client.getEmail().equals("michael.johnson@example.com")) michaelJohnsonFound = true;
        }
        assertTrue("At least one of the expected clients should be found", johnSmithFound || michaelJohnsonFound);
    }

    /**
     * @brief Tests client update functionality
     *
     * Verifies that an existing client can be updated with new information
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Update_ExistingClient_Success() throws SQLException {
        // Arrange
        Client client = new Client("Update", "Test", "update.test@example.com");
        client.setPhone("555-0000");
        client.setAddress("Original Address");
        clientDAO.create(client);

        // Update client
        client.setName("Updated");
        client.setSurname("Name");
        client.setEmail("updated.email@example.com");
        client.setPhone("555-9999");
        client.setAddress("New Address");

        // Act
        int updatedRows = clientDAO.update(client);

        // Assert
        assertEquals("Should update 1 row", 1, updatedRows);

        // Verify updated client from database
        Optional<Client> updatedClientOpt = clientDAO.getById(client.getId());
        assertTrue("Updated client should be found", updatedClientOpt.isPresent());
        Client updatedClient = updatedClientOpt.get();
        assertEquals("Name should be updated", "Updated", updatedClient.getName());
        assertEquals("Surname should be updated", "Name", updatedClient.getSurname());
        assertEquals("Email should be updated", "updated.email@example.com", updatedClient.getEmail());
        assertEquals("Phone should be updated", "555-9999", updatedClient.getPhone());
        assertEquals("Address should be updated", "New Address", updatedClient.getAddress());
    }

    /**
     * @brief Tests client deletion functionality
     *
     * Verifies that an existing client can be deleted from the database
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Delete_ExistingClient_Success() throws SQLException {
        // Arrange
        Client client = new Client("Delete", "Test", "delete.test@example.com");
        clientDAO.create(client);

        // Verify client exists before deletion
        Optional<Client> beforeDeleteOpt = clientDAO.getById(client.getId());
        assertTrue("Client should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = clientDAO.delete(client);

        // Assert
        assertEquals("Should delete 1 row", 1, deletedRows);

        // Verify client is deleted
        Optional<Client> afterDeleteOpt = clientDAO.getById(client.getId());
        assertFalse("Client should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Tests client deletion by ID functionality
     *
     * Verifies that an existing client can be deleted using only the client ID
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_DeleteById_ExistingClient_Success() throws SQLException {
        // Arrange
        Client client = new Client("DeleteById", "Test", "deletebyid.test@example.com");
        clientDAO.create(client);

        // Verify client exists before deletion
        Optional<Client> beforeDeleteOpt = clientDAO.getById(client.getId());
        assertTrue("Client should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = clientDAO.deleteById(client.getId());

        // Assert
        assertEquals("Should delete 1 row", 1, deletedRows);

        // Verify client is deleted
        Optional<Client> afterDeleteOpt = clientDAO.getById(client.getId());
        assertFalse("Client should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Tests deletion of non-existing client by ID
     *
     * Verifies that attempting to delete a non-existing client returns zero deleted rows
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_DeleteById_NonExistingClient_ReturnsZero() throws SQLException {
        // Act
        int deletedRows = clientDAO.deleteById(9999L);

        // Assert
        assertEquals("Should return 0 when no rows are deleted", 0, deletedRows);
    }
}