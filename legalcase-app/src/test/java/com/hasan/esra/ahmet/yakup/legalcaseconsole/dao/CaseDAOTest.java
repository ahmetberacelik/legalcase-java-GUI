package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
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
 * @brief Test class for CaseDAO operations
 *
 * This class tests all CRUD operations and relationship management
 * methods of the CaseDAO class using a test database
 */
public class CaseDAOTest {

    private ConnectionSource connectionSource;
    private CaseDAO caseDAO;
    private ClientDAO clientDAO;

    /**
     * @brief Setup method that runs before each test
     *
     * Creates test database tables and initializes DAO instances
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();
        connectionSource = TestDatabaseManager.getConnectionSource();
        caseDAO = new CaseDAO(connectionSource);
        clientDAO = new ClientDAO(connectionSource);
    }

    /**
     * @brief Cleanup method that runs after each test
     *
     * Closes test database connection
     * @throws SQLException if closing connection fails
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Tests case creation
     *
     * Verifies that a case is properly created with all attributes
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Create_Success_ReturnsCaseWithId() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-001", "Test Case", CaseType.CIVIL);
        caseEntity.setDescription("This is a test case description.");

        // Act
        Case createdCase = caseDAO.create(caseEntity);

        // Assert
        assertNotNull("Created case should not be null", createdCase);
        assertNotNull("Created case should have an ID", createdCase.getId());
        assertEquals("Case number should match", "CASE2023-001", createdCase.getCaseNumber());
        assertEquals("Case title should match", "Test Case", createdCase.getTitle());
        assertEquals("Case description should match", "This is a test case description.", createdCase.getDescription());
        assertEquals("Case type should match", CaseType.CIVIL, createdCase.getType());
        assertEquals("Case status should be NEW", CaseStatus.NEW, createdCase.getStatus());
    }

    /**
     * @brief Tests retrieval of a case by ID
     *
     * Verifies that an existing case can be properly retrieved by its ID
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetById_ExistingCase_ReturnsCase() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-002", "Get By ID Test", CaseType.CRIMINAL);
        caseDAO.create(caseEntity);

        // Act
        Optional<Case> retrievedCaseOpt = caseDAO.getById(caseEntity.getId());

        // Assert
        assertTrue("Case should be found", retrievedCaseOpt.isPresent());
        Case retrievedCase = retrievedCaseOpt.get();
        assertEquals("Case ID should match", caseEntity.getId(), retrievedCase.getId());
        assertEquals("Case number should match", "CASE2023-002", retrievedCase.getCaseNumber());
    }

    /**
     * @brief Tests retrieval of a non-existing case by ID
     *
     * Verifies that an empty Optional is returned when case ID doesn't exist
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetById_NonExistingCase_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<Case> retrievedCaseOpt = caseDAO.getById(9999L);

        // Assert
        assertFalse("Should return empty Optional when case is not found", retrievedCaseOpt.isPresent());
    }

    /**
     * @brief Tests retrieval of a case by case number
     *
     * Verifies that an existing case can be properly retrieved by its case number
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByCaseNumber_ExistingCase_ReturnsCase() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-003", "Get By Number Test", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        // Act
        Optional<Case> retrievedCaseOpt = caseDAO.getByCaseNumber("CASE2023-003");

        // Assert
        assertTrue("Case should be found", retrievedCaseOpt.isPresent());
        Case retrievedCase = retrievedCaseOpt.get();
        assertEquals("Case ID should match", caseEntity.getId(), retrievedCase.getId());
        assertEquals("Case title should match", "Get By Number Test", retrievedCase.getTitle());
    }

    /**
     * @brief Tests retrieval of a non-existing case by case number
     *
     * Verifies that an empty Optional is returned when case number doesn't exist
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByCaseNumber_NonExistingCase_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<Case> retrievedCaseOpt = caseDAO.getByCaseNumber("NONEXISTINGCASE");

        // Assert
        assertFalse("Should return empty Optional when case is not found", retrievedCaseOpt.isPresent());
    }

    /**
     * @brief Tests retrieval of all cases
     *
     * Verifies that all cases can be properly retrieved from the database
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetAll_WithCases_ReturnsList() throws SQLException {
        // Arrange
        Case case1 = new Case("CASE2023-004", "Get All Test 1", CaseType.CORPORATE);
        Case case2 = new Case("CASE2023-005", "Get All Test 2", CaseType.OTHER);
        caseDAO.create(case1);
        caseDAO.create(case2);

        // Act
        List<Case> allCases = caseDAO.getAll();

        // Assert
        assertNotNull("Case list should not be null", allCases);
        assertTrue("Case list should not be empty", allCases.size() >= 2);

        // Created cases should be in the list
        boolean case1Found = false;
        boolean case2Found = false;
        for (Case caseEntity : allCases) {
            if (caseEntity.getCaseNumber().equals("CASE2023-004")) case1Found = true;
            if (caseEntity.getCaseNumber().equals("CASE2023-005")) case2Found = true;
        }
        assertTrue("CASE2023-004 should be in the list", case1Found);
        assertTrue("CASE2023-005 should be in the list", case2Found);
    }

    /**
     * @brief Tests retrieval of cases by status
     *
     * Verifies that cases can be properly filtered by their status
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByStatus_WithMatchingCases_ReturnsList() throws SQLException {
        // Arrange
        Case activeCase1 = new Case("CASE2023-006", "Active Case 1", CaseType.CIVIL);
        activeCase1.setStatus(CaseStatus.ACTIVE);
        Case activeCase2 = new Case("CASE2023-007", "Active Case 2", CaseType.CRIMINAL);
        activeCase2.setStatus(CaseStatus.ACTIVE);
        Case closedCase = new Case("CASE2023-008", "Closed Case", CaseType.FAMILY);
        closedCase.setStatus(CaseStatus.CLOSED);

        caseDAO.create(activeCase1);
        caseDAO.create(activeCase2);
        caseDAO.create(closedCase);

        // Act
        List<Case> activeCases = caseDAO.getByStatus(CaseStatus.ACTIVE);

        // Assert
        assertNotNull("Active case list should not be null", activeCases);
        assertTrue("Active case list should not be empty", activeCases.size() >= 2);

        // Only active cases should be in the list, closed case should not
        boolean active1Found = false;
        boolean active2Found = false;
        boolean closedFound = false;
        for (Case caseEntity : activeCases) {
            if (caseEntity.getCaseNumber().equals("CASE2023-006")) active1Found = true;
            if (caseEntity.getCaseNumber().equals("CASE2023-007")) active2Found = true;
            if (caseEntity.getCaseNumber().equals("CASE2023-008")) closedFound = true;
        }
        assertTrue("Active Case 1 should be in the list", active1Found);
        assertTrue("Active Case 2 should be in the list", active2Found);
        assertFalse("Closed Case should not be in the list", closedFound);
    }

    /**
     * @brief Tests retrieval of cases by type
     *
     * Verifies that cases can be properly filtered by their type
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByType_WithMatchingCases_ReturnsList() throws SQLException {
        // Arrange
        Case civilCase1 = new Case("CASE2023-009", "Civil Case 1", CaseType.CIVIL);
        Case civilCase2 = new Case("CASE2023-010", "Civil Case 2", CaseType.CIVIL);
        Case criminalCase = new Case("CASE2023-011", "Criminal Case", CaseType.CRIMINAL);

        caseDAO.create(civilCase1);
        caseDAO.create(civilCase2);
        caseDAO.create(criminalCase);

        // Act
        List<Case> civilCases = caseDAO.getByType(CaseType.CIVIL);

        // Assert
        assertNotNull("Civil case list should not be null", civilCases);
        assertTrue("Civil case list should not be empty", civilCases.size() >= 2);

        // Only civil cases should be in the list, criminal case should not
        boolean civil1Found = false;
        boolean civil2Found = false;
        boolean criminalFound = false;
        for (Case caseEntity : civilCases) {
            if (caseEntity.getCaseNumber().equals("CASE2023-009")) civil1Found = true;
            if (caseEntity.getCaseNumber().equals("CASE2023-010")) civil2Found = true;
            if (caseEntity.getCaseNumber().equals("CASE2023-011")) criminalFound = true;
        }
        assertTrue("Civil Case 1 should be in the list", civil1Found);
        assertTrue("Civil Case 2 should be in the list", civil2Found);
        assertFalse("Criminal Case should not be in the list", criminalFound);
    }

    /**
     * @brief Tests case search by title
     *
     * Verifies that cases can be properly searched by title substring
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_SearchByTitle_WithMatchingCases_ReturnsList() throws SQLException {
        // Arrange
        Case searchCase1 = new Case("CASE2023-012", "Search Test Case", CaseType.CORPORATE);
        Case searchCase2 = new Case("CASE2023-013", "Another Search Case", CaseType.OTHER);
        Case noMatchCase = new Case("CASE2023-014", "No Match Here", CaseType.CIVIL);

        caseDAO.create(searchCase1);
        caseDAO.create(searchCase2);
        caseDAO.create(noMatchCase);

        // Act
        List<Case> searchResults = caseDAO.searchByTitle("Search");

        // Assert
        assertNotNull("Search results should not be null", searchResults);
        assertTrue("Search results should not be empty", searchResults.size() >= 1);
    }

    /**
     * @brief Tests case update
     *
     * Verifies that a case can be properly updated in the database
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Update_ExistingCase_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-015", "Update Test", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        // Update the case
        caseEntity.setTitle("Updated Title");
        caseEntity.setStatus(CaseStatus.ACTIVE);
        caseEntity.setDescription("Updated description");

        // Act
        caseDAO.update(caseEntity);

        // Assert - We can only check if the update was applied correctly

        // Check updated case from database
        Optional<Case> updatedCaseOpt = caseDAO.getById(caseEntity.getId());
        assertTrue("Updated case should be found", updatedCaseOpt.isPresent());
        Case updatedCase = updatedCaseOpt.get();
        assertEquals("Title should be updated", "Updated Title", updatedCase.getTitle());
        assertEquals("Status should be updated", CaseStatus.ACTIVE, updatedCase.getStatus());
        assertEquals("Description should be updated", "Updated description", updatedCase.getDescription());
    }

    /**
     * @brief Tests case deletion
     *
     * Verifies that a case can be properly deleted from the database
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Delete_ExistingCase_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-016", "Delete Test", CaseType.CORPORATE);
        caseDAO.create(caseEntity);

        // Check that case exists
        Optional<Case> beforeDeleteOpt = caseDAO.getById(caseEntity.getId());
        assertTrue("Case should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        caseDAO.delete(caseEntity);

        // Assert - We can only check if the deletion was successful

        // Check that case was deleted
        Optional<Case> afterDeleteOpt = caseDAO.getById(caseEntity.getId());
        assertFalse("Case should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Tests case deletion by ID
     *
     * Verifies that a case can be properly deleted by its ID
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_DeleteById_ExistingCase_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-017", "Delete By ID Test", CaseType.OTHER);
        caseDAO.create(caseEntity);

        // Check that case exists
        Optional<Case> beforeDeleteOpt = caseDAO.getById(caseEntity.getId());
        assertTrue("Case should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        caseDAO.deleteById(caseEntity.getId());

        // Assert - We can only check if the deletion was successful

        // Check that case was deleted
        Optional<Case> afterDeleteOpt = caseDAO.getById(caseEntity.getId());
        assertFalse("Case should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Tests adding a client to a case
     *
     * Verifies that a client can be properly associated with a case
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_AddClientToCase_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-018", "Client Association Test", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("John", "Doe", "john.doe@example.com");
        clientDAO.create(client);

        // Act
        caseDAO.addClientToCase(caseEntity, client);

        // Assert
        List<Client> associatedClients = caseDAO.getClientsForCase(caseEntity.getId());
        assertNotNull("Associated clients list should not be null", associatedClients);
        assertEquals("Associated clients list should have 1 item", 1, associatedClients.size());
        assertEquals("Associated client should match", client.getId(), associatedClients.get(0).getId());
    }

    /**
     * @brief Tests removing a client from a case
     *
     * Verifies that a client can be properly disassociated from a case
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_RemoveClientFromCase_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-019", "Client Disassociation Test", CaseType.CRIMINAL);
        caseDAO.create(caseEntity);

        Client client = new Client("Jane", "Smith", "jane.smith@example.com");
        clientDAO.create(client);

        // First add client to case
        caseDAO.addClientToCase(caseEntity, client);

        // Check that client was added
        List<Client> beforeRemove = caseDAO.getClientsForCase(caseEntity.getId());
        assertEquals("Should have 1 client before removal", 1, beforeRemove.size());

        // Act
        caseDAO.removeClientFromCase(caseEntity, client);

        // Assert
        List<Client> afterRemove = caseDAO.getClientsForCase(caseEntity.getId());
        assertTrue("Client list should be empty after removal", afterRemove.isEmpty());
    }

    /**
     * @brief Tests retrieving clients for a case
     *
     * Verifies that all clients associated with a case can be retrieved
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetClientsForCase_WithMultipleClients_ReturnsList() throws SQLException {
        // Arrange
        Case caseEntity = new Case("CASE2023-020", "Multiple Clients Test", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        Client client1 = new Client("Client", "One", "client.one@example.com");
        Client client2 = new Client("Client", "Two", "client.two@example.com");
        clientDAO.create(client1);
        clientDAO.create(client2);

        // Add clients to case
        caseDAO.addClientToCase(caseEntity, client1);
        caseDAO.addClientToCase(caseEntity, client2);

        // Act
        List<Client> clients = caseDAO.getClientsForCase(caseEntity.getId());

        // Assert
        assertNotNull("Clients list should not be null", clients);
        assertEquals("Clients list should have 2 items", 2, clients.size());

        // Both clients should be in the list
        boolean client1Found = false;
        boolean client2Found = false;
        for (Client client : clients) {
            if (client.getId().equals(client1.getId())) client1Found = true;
            if (client.getId().equals(client2.getId())) client2Found = true;
        }
        assertTrue("Client One should be in the list", client1Found);
        assertTrue("Client Two should be in the list", client2Found);
    }

    /**
     * @brief Tests retrieving cases for a client
     *
     * Verifies that all cases associated with a client can be retrieved
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetCasesForClient_WithMultipleCases_ReturnsList() throws SQLException {
        // Arrange
        Client client = new Client("Multi", "Case", "multi.case@example.com");
        clientDAO.create(client);

        Case case1 = new Case("CASE2023-021", "Client's Case 1", CaseType.CORPORATE);
        Case case2 = new Case("CASE2023-022", "Client's Case 2", CaseType.OTHER);
        caseDAO.create(case1);
        caseDAO.create(case2);

        // Add cases to client
        caseDAO.addClientToCase(case1, client);
        caseDAO.addClientToCase(case2, client);

        // Act
        List<Case> cases = caseDAO.getCasesForClient(client.getId());

        // Assert
        assertNotNull("Cases list should not be null", cases);
        assertEquals("Cases list should have 2 items", 2, cases.size());

        // Both cases should be in the list
        boolean case1Found = false;
        boolean case2Found = false;
        for (Case caseEntity : cases) {
            if (caseEntity.getId().equals(case1.getId())) case1Found = true;
            if (caseEntity.getId().equals(case2.getId())) case2Found = true;
        }
        assertTrue("Client's Case 1 should be in the list", case1Found);
        assertTrue("Client's Case 2 should be in the list", case2Found);
    }
}