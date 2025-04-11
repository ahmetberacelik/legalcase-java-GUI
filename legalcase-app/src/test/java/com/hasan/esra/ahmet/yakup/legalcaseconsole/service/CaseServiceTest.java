package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;

/**
 * @brief Test class for CaseService
 *
 * This class contains unit tests for all methods of the CaseService class,
 * verifying the functionality of case management operations including
 * creation, retrieval, update, and deletion of legal cases.
 */
public class CaseServiceTest {

    private ConnectionSource connectionSource;
    private CaseDAO caseDAO;
    private ClientDAO clientDAO;
    private CaseService caseService;

    /**
     * @brief Set up test environment before each test
     *
     * Initializes the test database connection, clears existing data,
     * and instantiates the required DAOs and service classes.
     *
     * @throws SQLException if a database error occurs
     */
    @Before
    public void setup() throws SQLException {
        // Get a connection to the test database
        connectionSource = TestDatabaseManager.getConnectionSource();

        // Clear any existing data
        TestDatabaseManager.clearTables();

        // Create real DAOs with the test connection
        caseDAO = new CaseDAO(connectionSource);
        clientDAO = new ClientDAO(connectionSource);

        // Create the service with the real DAOs
        caseService = new CaseService(caseDAO, clientDAO);
    }

    /**
     * @brief Clean up test environment after each test
     *
     * Clears all data from test tables to ensure a clean state for the next test.
     *
     * @throws SQLException if a database error occurs
     */
    @After
    public void tearDown() throws SQLException {
        // Clean up after each test
        TestDatabaseManager.clearTables();
    }

    /**
     * @brief Test case creation with valid data
     *
     * Verifies that a new case is correctly created with the provided data
     * and assigned a unique ID.
     */
    @Test
    public void Test_CreateCase_ValidData_ReturnsCaseWithId() {
        // Test data
        String caseNumber = "CASE-001";
        String title = "Test Case";
        CaseType type = CaseType.CIVIL;
        String description = "This is a test case description";

        // Create the case
        Case createdCase = caseService.createCase(caseNumber, title, type, description);

        // Verify
        assertNotNull("Created case should not be null", createdCase);
        assertNotNull("Case ID should not be null", createdCase.getId());
        assertEquals("Case number should match", caseNumber, createdCase.getCaseNumber());
        assertEquals("Title should match", title, createdCase.getTitle());
        assertEquals("Type should match", type, createdCase.getType());
        assertEquals("Description should match", description, createdCase.getDescription());
        assertEquals("Status should be NEW", CaseStatus.NEW, createdCase.getStatus());

        // Verify the case exists in the database
        Optional<Case> retrievedCase = caseService.getCaseById(createdCase.getId());
        assertTrue("Case should exist in the database", retrievedCase.isPresent());
        assertEquals("Retrieved case ID should match", createdCase.getId(), retrievedCase.get().getId());
    }

    /**
     * @brief Test case creation with duplicate case number
     *
     * Verifies that attempting to create a case with a duplicate case number
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_CreateCase_DuplicateCaseNumber_ThrowsIllegalArgumentException() {
        // Create first case
        caseService.createCase("CASE-002", "First Case", CaseType.CIVIL, "First case description");

        // Try to create another case with the same number - should throw exception
        caseService.createCase("CASE-002", "Second Case", CaseType.CRIMINAL, "Second case description");
    }

    /**
     * @brief Test retrieving a case by ID when the ID exists
     *
     * Verifies that a case can be correctly retrieved by its ID.
     */
    @Test
    public void Test_GetCaseById_ExistingId_ReturnsCase() {
        // Create a case
        Case testCase = caseService.createCase("GET-ID-001", "Get by ID Test", CaseType.CIVIL, "Test description");

        // Get the case by ID
        Optional<Case> retrievedCase = caseService.getCaseById(testCase.getId());

        // Verify
        assertTrue("Case should be found", retrievedCase.isPresent());
        assertEquals("ID should match", testCase.getId(), retrievedCase.get().getId());
        assertEquals("Case number should match", testCase.getCaseNumber(), retrievedCase.get().getCaseNumber());
        assertEquals("Title should match", testCase.getTitle(), retrievedCase.get().getTitle());
    }

    /**
     * @brief Test retrieving a case by a non-existent ID
     *
     * Verifies that attempting to retrieve a case with a non-existent ID
     * returns an empty Optional.
     */
    @Test
    public void Test_GetCaseById_NonExistentId_ReturnsEmptyOptional() {
        // Try to get a case with a non-existent ID
        Optional<Case> retrievedCase = caseService.getCaseById(9999L);

        // Verify
        assertFalse("Non-existent case should not be found", retrievedCase.isPresent());
    }

    /**
     * @brief Test retrieving a case by case number when the number exists
     *
     * Verifies that a case can be correctly retrieved by its case number.
     */
    @Test
    public void Test_GetCaseByCaseNumber_ExistingNumber_ReturnsCase() {
        // Create a case
        String caseNumber = "UNIQUE-CASE-001";
        Case testCase = caseService.createCase(caseNumber, "Unique Case", CaseType.CIVIL, "Unique description");

        // Get the case by case number
        Optional<Case> retrievedCase = caseService.getCaseByCaseNumber(caseNumber);

        // Verify
        assertTrue("Case should be found", retrievedCase.isPresent());
        assertEquals("Case number should match", caseNumber, retrievedCase.get().getCaseNumber());
        assertEquals("ID should match", testCase.getId(), retrievedCase.get().getId());
    }

    /**
     * @brief Test retrieving a case by a non-existent case number
     *
     * Verifies that attempting to retrieve a case with a non-existent case number
     * returns an empty Optional.
     */
    @Test
    public void Test_GetCaseByCaseNumber_NonExistentNumber_ReturnsEmptyOptional() {
        // Try to get a case with a non-existent case number
        Optional<Case> retrievedCase = caseService.getCaseByCaseNumber("NON-EXISTENT-CASE");

        // Verify
        assertFalse("Non-existent case should not be found", retrievedCase.isPresent());
    }

    /**
     * @brief Test retrieving all cases when multiple cases exist
     *
     * Verifies that all created cases can be correctly retrieved.
     */
    @Test
    public void Test_GetAllCases_MultipleCases_ReturnsAllCases() {
        // Create multiple cases
        caseService.createCase("ALL-001", "First Case", CaseType.CIVIL, "First description");
        caseService.createCase("ALL-002", "Second Case", CaseType.CRIMINAL, "Second description");
        caseService.createCase("ALL-003", "Third Case", CaseType.FAMILY, "Third description");

        // Get all cases
        List<Case> allCases = caseService.getAllCases();

        // Verify
        assertNotNull("Case list should not be null", allCases);
        assertEquals("Should return 3 cases", 3, allCases.size());
    }

    /**
     * @brief Test retrieving all cases when no cases exist
     *
     * Verifies that attempting to retrieve all cases when none exist
     * returns an empty list.
     */
    @Test
    public void Test_GetAllCases_NoCases_ReturnsEmptyList() {
        // Get all cases without creating any
        List<Case> allCases = caseService.getAllCases();

        // Verify
        assertNotNull("Case list should not be null", allCases);
        assertTrue("Case list should be empty", allCases.isEmpty());
    }

    /**
     * @brief Test retrieving cases by status when cases with the status exist
     *
     * Verifies that cases can be correctly filtered by status.
     */
    @Test
    public void Test_GetCasesByStatus_ExistingStatus_ReturnsCasesWithStatus() {
        // Create cases with different statuses
        Case newCase = caseService.createCase("STATUS-NEW", "New Case", CaseType.CIVIL, "New case description");

        Case activeCase = caseService.createCase("STATUS-ACTIVE", "Active Case", CaseType.CRIMINAL, "Active case description");
        caseService.updateCase(
                activeCase.getId(),
                activeCase.getCaseNumber(),
                activeCase.getTitle(),
                activeCase.getType(),
                activeCase.getDescription(),
                CaseStatus.ACTIVE
        );

        Case pendingCase = caseService.createCase("STATUS-PENDING", "Pending Case", CaseType.FAMILY, "Pending case description");
        caseService.updateCase(
                pendingCase.getId(),
                pendingCase.getCaseNumber(),
                pendingCase.getTitle(),
                pendingCase.getType(),
                pendingCase.getDescription(),
                CaseStatus.PENDING
        );

        // Get cases by status
        List<Case> newCases = caseService.getCasesByStatus(CaseStatus.NEW);
        List<Case> activeCases = caseService.getCasesByStatus(CaseStatus.ACTIVE);
        List<Case> pendingCases = caseService.getCasesByStatus(CaseStatus.PENDING);
        List<Case> closedCases = caseService.getCasesByStatus(CaseStatus.CLOSED);

        // Verify
        assertEquals("Should have 1 NEW case", 1, newCases.size());
        assertEquals("Should have 1 ACTIVE case", 1, activeCases.size());
        assertEquals("Should have 1 PENDING case", 1, pendingCases.size());
        assertEquals("Should have 0 CLOSED cases", 0, closedCases.size());

        assertEquals("NEW case should have correct number", "STATUS-NEW", newCases.get(0).getCaseNumber());
        assertEquals("ACTIVE case should have correct number", "STATUS-ACTIVE", activeCases.get(0).getCaseNumber());
        assertEquals("PENDING case should have correct number", "STATUS-PENDING", pendingCases.get(0).getCaseNumber());
    }

    /**
     * @brief Test retrieving cases by status when no cases with the status exist
     *
     * Verifies that attempting to retrieve cases with a status that none have
     * returns an empty list.
     */
    @Test
    public void Test_GetCasesByStatus_NoMatchingCases_ReturnsEmptyList() {
        // Create a case with NEW status
        caseService.createCase("STATUS-TEST", "Status Test", CaseType.CIVIL, "Status test description");

        // Get cases with CLOSED status
        List<Case> closedCases = caseService.getCasesByStatus(CaseStatus.CLOSED);

        // Verify
        assertNotNull("Case list should not be null", closedCases);
        assertTrue("Case list should be empty", closedCases.isEmpty());
    }

    /**
     * @brief Test searching cases by title when matches exist
     *
     * Verifies that cases can be correctly filtered by title search.
     */
    @Test
    public void Test_SearchCasesByTitle_MatchingTitle_ReturnsMatchingCases() {
        // Create cases with different titles
        caseService.createCase("SEARCH-001", "Contract Dispute", CaseType.CIVIL, "Contract dispute description");
        caseService.createCase("SEARCH-002", "Property Dispute", CaseType.CIVIL, "Property dispute description");
        caseService.createCase("SEARCH-003", "Criminal Investigation", CaseType.CRIMINAL, "Criminal investigation description");

        // Search for cases with "Dispute" in the title
        List<Case> searchResults = caseService.searchCasesByTitle("Dispute");

        // Verify
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("Should find 2 cases with 'Dispute' in title", 2, searchResults.size());
    }

    /**
     * @brief Test searching cases by title when no matches exist
     *
     * Verifies that searching for cases with a title that none match
     * returns an empty list.
     */
    @Test
    public void Test_SearchCasesByTitle_NoMatch_ReturnsEmptyList() {
        // Create some cases
        caseService.createCase("SEARCH-004", "Contract Case", CaseType.CIVIL, "Contract case description");
        caseService.createCase("SEARCH-005", "Property Case", CaseType.CIVIL, "Property case description");

        // Search for a non-existent title
        List<Case> searchResults = caseService.searchCasesByTitle("NonExistent");

        // Verify
        assertNotNull("Search results should not be null", searchResults);
        assertTrue("Search results should be empty", searchResults.isEmpty());
    }

    /**
     * @brief Test updating a case with valid data
     *
     * Verifies that a case can be correctly updated with new data.
     */
    @Test
    public void Test_UpdateCase_ValidData_ReturnsUpdatedCase() {
        // Create a case
        Case originalCase = caseService.createCase("UPDATE-001", "Original Title", CaseType.CIVIL, "Original description");

        // Update the case
        Case updatedCase = caseService.updateCase(
                originalCase.getId(),
                "UPDATE-001-MODIFIED",
                "Updated Title",
                CaseType.CORPORATE,
                "Updated description",
                CaseStatus.ACTIVE
        );

        // Verify the update
        assertEquals("Case number should be updated", "UPDATE-001-MODIFIED", updatedCase.getCaseNumber());
        assertEquals("Title should be updated", "Updated Title", updatedCase.getTitle());
        assertEquals("Type should be updated", CaseType.CORPORATE, updatedCase.getType());
        assertEquals("Description should be updated", "Updated description", updatedCase.getDescription());
        assertEquals("Status should be updated", CaseStatus.ACTIVE, updatedCase.getStatus());

        // Verify the update persisted to the database
        Optional<Case> retrievedCase = caseService.getCaseById(originalCase.getId());
        assertTrue("Case should exist", retrievedCase.isPresent());
        assertEquals("Updated title should be saved", "Updated Title", retrievedCase.get().getTitle());
        assertEquals("Updated status should be saved", CaseStatus.ACTIVE, retrievedCase.get().getStatus());
    }

    /**
     * @brief Test updating a case with a non-existent ID
     *
     * Verifies that attempting to update a case with a non-existent ID
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateCase_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to update a non-existent case
        caseService.updateCase(
                9999L,
                "NONEXISTENT-CASE",
                "Nonexistent Title",
                CaseType.CIVIL,
                "Nonexistent description",
                CaseStatus.ACTIVE
        );
    }

    /**
     * @brief Test updating a case with a duplicate case number
     *
     * Verifies that attempting to update a case with a case number that
     * already exists for another case throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateCase_DuplicateCaseNumber_ThrowsIllegalArgumentException() throws SQLException {
        // Create two cases
        caseService.createCase("UPDATE-CASE-A", "Case A", CaseType.CIVIL, "Case A description");
        Case caseB = caseService.createCase("UPDATE-CASE-B", "Case B", CaseType.CRIMINAL, "Case B description");

        // Try to update case B with case A's number - should throw exception
        caseService.updateCase(
                caseB.getId(),
                "UPDATE-CASE-A", // Duplicate case number
                caseB.getTitle(),
                caseB.getType(),
                caseB.getDescription(),
                caseB.getStatus()
        );
    }

    /**
     * @brief Test updating a case with the same case number
     *
     * Verifies that a case can be updated with its own case number
     * without throwing an exception.
     */
    @Test
    public void Test_UpdateCase_SameCaseNumber_ReturnsUpdatedCase() {
        // Create a case
        Case originalCase = caseService.createCase("UPDATE-SAME-001", "Original Title", CaseType.CIVIL, "Original description");

        // Update the case with the same case number but different other fields
        Case updatedCase = caseService.updateCase(
                originalCase.getId(),
                "UPDATE-SAME-001", // Same case number
                "Updated Title",
                CaseType.CORPORATE,
                "Updated description",
                CaseStatus.ACTIVE
        );

        // Verify the update
        assertEquals("Case number should remain the same", "UPDATE-SAME-001", updatedCase.getCaseNumber());
        assertEquals("Title should be updated", "Updated Title", updatedCase.getTitle());
        assertEquals("Type should be updated", CaseType.CORPORATE, updatedCase.getType());
    }

    /**
     * @brief Test deleting a case with an existing ID
     *
     * Verifies that a case can be correctly deleted by its ID.
     */
    @Test
    public void Test_DeleteCase_ExistingId_CaseIsDeleted() {
        // Create a case
        Case caseToDelete = caseService.createCase("DELETE-CASE", "Delete Me", CaseType.CIVIL, "Case to be deleted");

        // Verify the case exists
        assertTrue("Case should exist before deletion",
                caseService.getCaseById(caseToDelete.getId()).isPresent());

        // Delete the case
        caseService.deleteCase(caseToDelete.getId());

        // Verify the case no longer exists
        assertFalse("Case should not exist after deletion",
                caseService.getCaseById(caseToDelete.getId()).isPresent());
    }

    /**
     * @brief Test deleting a case with a non-existent ID
     *
     * Verifies that attempting to delete a case with a non-existent ID
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_DeleteCase_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to delete a non-existent case
        caseService.deleteCase(9999L);
    }

    /**
     * @brief Test adding a client to a case with a non-existent case ID
     *
     * Verifies that attempting to add a client to a case with a non-existent ID
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_AddClientToCase_NonExistentCaseId_ThrowsIllegalArgumentException() throws SQLException {
        // Create a client
        Client client = new Client("Test", "Client", "test.client@example.com");
        clientDAO.create(client);

        // Try to add client to a non-existent case
        caseService.addClientToCase(9999L, client.getId());
    }

    /**
     * @brief Test adding a non-existent client to a case
     *
     * Verifies that attempting to add a client with a non-existent ID to a case
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_AddClientToCase_NonExistentClientId_ThrowsIllegalArgumentException() {
        // Create a case
        Case testCase = caseService.createCase("CLIENT-CASE-2", "Case with Client", CaseType.CIVIL, "Test description");

        // Try to add a non-existent client to the case
        caseService.addClientToCase(testCase.getId(), 9999L);
    }

    /**
     * @brief Test removing a client from a case with valid IDs
     *
     * Verifies that a client can be correctly removed from a case.
     */
    @Test
    public void Test_RemoveClientFromCase_ValidIds_ClientRemovedFromCase() throws SQLException {
        // Create a case and a client
        Case testCase = caseService.createCase("REMOVE-CLIENT", "Remove Client Case", CaseType.CIVIL, "Test description");
        Client client = new Client("Removable", "Client", "removable@example.com");
        clientDAO.create(client);

        // Add client to case
        caseService.addClientToCase(testCase.getId(), client.getId());

        // Verify client was added
        List<Client> clientsBefore = caseService.getClientsForCase(testCase.getId());
        assertEquals("Should have 1 client before removal", 1, clientsBefore.size());

        // Remove client from case
        caseService.removeClientFromCase(testCase.getId(), client.getId());

        // Verify client was removed
        List<Client> clientsAfter = caseService.getClientsForCase(testCase.getId());
        assertEquals("Should have 0 clients after removal", 0, clientsAfter.size());
    }

    /**
     * @brief Test removing a client from a case with a non-existent case ID
     *
     * Verifies that attempting to remove a client from a case with a non-existent ID
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_RemoveClientFromCase_NonExistentCaseId_ThrowsIllegalArgumentException() throws SQLException {
        // Create a client
        Client client = new Client("Test", "Client", "test.client@example.com");
        clientDAO.create(client);

        // Try to remove client from a non-existent case
        caseService.removeClientFromCase(9999L, client.getId());
    }

    /**
     * @brief Test removing a non-existent client from a case
     *
     * Verifies that attempting to remove a client with a non-existent ID from a case
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_RemoveClientFromCase_NonExistentClientId_ThrowsIllegalArgumentException() {
        // Create a case
        Case testCase = caseService.createCase("REMOVE-CLIENT-2", "Remove Client Case", CaseType.CIVIL, "Test description");

        // Try to remove a non-existent client from the case
        caseService.removeClientFromCase(testCase.getId(), 9999L);
    }

    /**
     * @brief Test getting clients for a case when clients exist
     *
     * Verifies that all clients associated with a case can be correctly retrieved.
     */
    @Test
    public void Test_GetClientsForCase_ExistingCaseWithClients_ReturnsClientList() throws SQLException {
        // Create a case and multiple clients
        Case testCase = caseService.createCase("CLIENTS-FOR-CASE", "Get Clients Case", CaseType.CIVIL, "Test description");

        Client client1 = new Client("First", "Client", "first@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Second", "Client", "second@example.com");
        clientDAO.create(client2);

        // Add clients to case
        caseService.addClientToCase(testCase.getId(), client1.getId());
        caseService.addClientToCase(testCase.getId(), client2.getId());

        // Get clients for the case
        List<Client> clients = caseService.getClientsForCase(testCase.getId());

        // Verify
        assertNotNull("Client list should not be null", clients);
        assertEquals("Should have 2 clients", 2, clients.size());
    }

    /**
     * @brief Test getting clients for a case when no clients exist
     *
     * Verifies that attempting to retrieve clients for a case with no clients
     * returns an empty list.
     */
    @Test
    public void Test_GetClientsForCase_ExistingCaseWithNoClients_ReturnsEmptyList() {
        // Create a case with no clients
        Case testCase = caseService.createCase("NO-CLIENTS-CASE", "No Clients Case", CaseType.CIVIL, "Test description");

        // Get clients for the case
        List<Client> clients = caseService.getClientsForCase(testCase.getId());

        // Verify
        assertNotNull("Client list should not be null", clients);
        assertTrue("Client list should be empty", clients.isEmpty());
    }

    /**
     * @brief Test getting clients for a case with a non-existent case ID
     *
     * Verifies that attempting to retrieve clients for a case with a non-existent ID
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetClientsForCase_NonExistentCaseId_ThrowsIllegalArgumentException() {
        // Try to get clients for a non-existent case
        caseService.getClientsForCase(9999L);
    }

    /**
     * @brief Test getting cases for a client when cases exist
     *
     * Verifies that all cases associated with a client can be correctly retrieved.
     */
    @Test
    public void Test_GetCasesForClient_ExistingClientWithCases_ReturnsCaseList() throws SQLException {
        // Create a client and multiple cases
        Client client = new Client("Client", "WithCases", "client.with.cases@example.com");
        clientDAO.create(client);

        Case case1 = caseService.createCase("CLIENT-CASE-1", "First Client Case", CaseType.CIVIL, "First case description");
        Case case2 = caseService.createCase("CLIENT-CASE-2", "Second Client Case", CaseType.CRIMINAL, "Second case description");

        // Add client to cases
        caseService.addClientToCase(case1.getId(), client.getId());
        caseService.addClientToCase(case2.getId(), client.getId());

        // Get cases for the client
        List<Case> cases = caseService.getCasesForClient(client.getId());

        // Verify
        assertNotNull("Case list should not be null", cases);
        assertEquals("Should have 2 cases", 2, cases.size());
    }

    /**
     * @brief Test getting cases for a client when no cases exist
     *
     * Verifies that attempting to retrieve cases for a client with no cases
     * returns an empty list.
     */
    @Test
    public void Test_GetCasesForClient_ExistingClientWithNoCases_ReturnsEmptyList() throws SQLException {
        // Create a client with no cases
        Client client = new Client("Client", "NoCases", "client.no.cases@example.com");
        clientDAO.create(client);

        // Get cases for the client
        List<Case> cases = caseService.getCasesForClient(client.getId());

        // Verify
        assertNotNull("Case list should not be null", cases);
        assertTrue("Case list should be empty", cases.isEmpty());
    }

    /**
     * @brief Test getting cases for a client with a non-existent client ID
     *
     * Verifies that attempting to retrieve cases for a client with a non-existent ID
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetCasesForClient_NonExistentClientId_ThrowsIllegalArgumentException() {
        // Try to get cases for a non-existent client
        caseService.getCasesForClient(9999L);
    }

    /**
     * @brief Test case creation when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during case creation.
     */
    @Test(expected = RuntimeException.class)
    public void Test_CreateCase_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.createCase("EXCEPTION-CASE", "Exception Test", CaseType.CIVIL, "Exception test description");
    }

    /**
     * @brief Test getting a case by ID when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during case retrieval by ID.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetCaseById_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.getCaseById(1L);
    }

    /**
     * @brief Test getting a case by case number when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during case retrieval by case number.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetCaseByCaseNumber_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.getCaseByCaseNumber("EXCEPTION-CASE");
    }

    /**
     * @brief Test getting all cases when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during retrieval of all cases.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetAllCases_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.getAllCases();
    }

    /**
     * @brief Test getting cases by status when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during retrieval of cases by status.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetCasesByStatus_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.getCasesByStatus(CaseStatus.ACTIVE);
    }

    /**
     * @brief Test searching cases by title when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during case search by title.
     */
    @Test(expected = RuntimeException.class)
    public void Test_SearchCasesByTitle_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.searchCasesByTitle("Exception");
    }

    /**
     * @brief Test updating a case when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during case update.
     */
    @Test(expected = RuntimeException.class)
    public void Test_UpdateCase_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.updateCase(1L, "EXCEPTION-CASE", "Exception Test", CaseType.CIVIL, "Exception description", CaseStatus.ACTIVE);
    }

    /**
     * @brief Test deleting a case when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during case deletion.
     */
    @Test(expected = RuntimeException.class)
    public void Test_DeleteCase_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.deleteCase(1L);
    }

    /**
     * @brief Test adding a client to a case when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during the process of adding a client to a case.
     */
    @Test(expected = RuntimeException.class)
    public void Test_AddClientToCase_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.addClientToCase(1L, 1L);
    }

    /**
     * @brief Test removing a client from a case when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during the process of removing a client from a case.
     */
    @Test(expected = RuntimeException.class)
    public void Test_RemoveClientFromCase_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.removeClientFromCase(1L, 1L);
    }

    /**
     * @brief Test getting clients for a case when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during retrieval of clients for a case.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetClientsForCase_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.getClientsForCase(1L);
    }

    /**
     * @brief Test getting cases for a client when a SQLException is thrown
     *
     * Verifies that a RuntimeException is thrown when a SQLException occurs
     * during retrieval of cases for a client.
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetCasesForClient_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup with DAO that throws SQLException
        caseService = new CaseService(new ThrowingSQLCaseDAO(connectionSource), clientDAO);

        // Should throw RuntimeException wrapping the SQLException
        caseService.getCasesForClient(1L);
    }

    /**
     * @brief Mock DAO class that throws SQLException for testing exception handling
     *
     * This inner class extends CaseDAO to simulate database errors by
     * throwing SQLException from all methods.
     */
    private static class ThrowingSQLCaseDAO extends CaseDAO {
        /**
         * @brief Constructor for the ThrowingSQLCaseDAO
         *
         * @param connectionSource The connection source to use for database operations
         * @throws SQLException If a database error occurs
         */
        public ThrowingSQLCaseDAO(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource);
        }

        /**
         * @brief Override of create method to throw SQLException
         *
         * @param caseEntity The case entity to create
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public Case create(Case caseEntity) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of getById method to throw SQLException
         *
         * @param id The ID of the case to retrieve
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public Optional<Case> getById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of getByCaseNumber method to throw SQLException
         *
         * @param caseNumber The case number to search for
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public Optional<Case> getByCaseNumber(String caseNumber) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of getAll method to throw SQLException
         *
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public List<Case> getAll() throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of getByStatus method to throw SQLException
         *
         * @param status The status to filter cases by
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public List<Case> getByStatus(CaseStatus status) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of searchByTitle method to throw SQLException
         *
         * @param title The title text to search for
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public List<Case> searchByTitle(String title) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of update method to throw SQLException
         *
         * @param caseEntity The case entity to update
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public int update(Case caseEntity) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of delete method to throw SQLException
         *
         * @param caseEntity The case entity to delete
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public int delete(Case caseEntity) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of deleteById method to throw SQLException
         *
         * @param id The ID of the case to delete
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public int deleteById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of addClientToCase method to throw SQLException
         *
         * @param caseEntity The case to add the client to
         * @param client The client to add to the case
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public void addClientToCase(Case caseEntity, Client client) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of removeClientFromCase method to throw SQLException
         *
         * @param caseEntity The case to remove the client from
         * @param client The client to remove from the case
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public void removeClientFromCase(Case caseEntity, Client client) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of getClientsForCase method to throw SQLException
         *
         * @param caseId The ID of the case to get clients for
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public List<Client> getClientsForCase(Long caseId) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Override of getCasesForClient method to throw SQLException
         *
         * @param clientId The ID of the client to get cases for
         * @return Never returns as SQLException is always thrown
         * @throws SQLException Always thrown to simulate database error
         */
        @Override
        public List<Case> getCasesForClient(Long clientId) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }
    }
}