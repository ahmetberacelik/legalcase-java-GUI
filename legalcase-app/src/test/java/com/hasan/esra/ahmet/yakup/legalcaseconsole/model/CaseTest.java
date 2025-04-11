package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @brief Test class for Case entity
 *
 * This class contains unit tests for the Case class functionality
 * including constructors, client management, hearing and document addition,
 * and lifecycle callbacks.
 */
public class CaseTest {

    /**
     * @brief Set up test environment before each test
     *
     * Prepares the test database for testing
     *
     * @throws SQLException if database initialization fails
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();
    }

    /**
     * @brief Clean up after each test
     *
     * Closes the test database connection
     *
     * @throws SQLException if connection closing fails
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Test constructor with ID parameter
     *
     * Verifies that the constructor sets all fields correctly when ID is provided
     */
    @Test
    public void Test_Constructor_WithId_SetsCorrectValues() {
        // Action
        Long id = 1L;
        String caseNumber = "CASE2023-001";
        String title = "Test Case Title";
        CaseType type = CaseType.CIVIL;
        Case caseEntity = new Case(id, caseNumber, title, type);

        // Verification
        assertEquals("ID should be set correctly", id, caseEntity.getId());
        assertEquals("Case number should be set correctly", caseNumber, caseEntity.getCaseNumber());
        assertEquals("Title should be set correctly", title, caseEntity.getTitle());
        assertEquals("Type should be set correctly", type, caseEntity.getType());
        assertEquals("Status should be set to NEW", CaseStatus.NEW, caseEntity.getStatus());
        assertNotNull("createdAt should not be null", caseEntity.getCreatedAt());
        assertNotNull("updatedAt should not be null", caseEntity.getUpdatedAt());
    }

    /**
     * @brief Test constructor without ID parameter
     *
     * Verifies that the constructor sets all fields correctly when ID is not provided
     */
    @Test
    public void Test_Constructor_WithoutId_SetsCorrectValues() {
        // Action
        String caseNumber = "CASE2023-002";
        String title = "Another Test Case";
        CaseType type = CaseType.CRIMINAL;
        Case caseEntity = new Case(caseNumber, title, type);

        // Verification
        assertNull("ID should be null", caseEntity.getId());
        assertEquals("Case number should be set correctly", caseNumber, caseEntity.getCaseNumber());
        assertEquals("Title should be set correctly", title, caseEntity.getTitle());
        assertEquals("Type should be set correctly", type, caseEntity.getType());
        assertEquals("Status should be set to NEW", CaseStatus.NEW, caseEntity.getStatus());
        assertNotNull("createdAt should not be null", caseEntity.getCreatedAt());
        assertNotNull("updatedAt should not be null", caseEntity.getUpdatedAt());
    }

    /**
     * @brief Test adding a client to a case
     *
     * Verifies that a client is properly added to the case's client list
     */
    @Test
    public void Test_AddClient_AddsClientToList() {
        // Setup
        Case caseEntity = new Case("CASE2023-003", "Client Test Case", CaseType.FAMILY);
        Client client = new Client("Alice", "Johnson", "alice@example.com");

        // Action
        caseEntity.addClient(client);

        // Verification
        assertTrue("Case client list should contain the client", caseEntity.getClients().contains(client));
    }

    /**
     * @brief Test adding a client when clients list is null
     *
     * Verifies that the clients list is initialized when a client is added to a case with null clients list
     */
    @Test
    public void Test_AddClient_WithNullClients_InitializesList() {
        // Setup
        Case caseEntity = new Case("CASE2023-004", "Null Clients Test", CaseType.CORPORATE);
        caseEntity.setClients(null); // Manually set to null
        Client client = new Client("Bob", "Brown", "bob@example.com");

        // Action
        caseEntity.addClient(client);

        // Verification
        assertNotNull("Clients list should not be null", caseEntity.getClients());
        assertTrue("Case client list should contain the client", caseEntity.getClients().contains(client));
    }

    /**
     * @brief Test adding a duplicate client
     *
     * Verifies that adding the same client twice does not create duplicates
     */
    @Test
    public void Test_AddClient_AlreadyAdded_DoesNotAddDuplicate() {
        // Setup
        Case caseEntity = new Case("CASE2023-005", "Duplicate Client Test", CaseType.OTHER);
        Client client = new Client("Charlie", "Clark", "charlie@example.com");
        caseEntity.addClient(client); // First addition

        // Action
        caseEntity.addClient(client); // Second addition

        // Verification
        assertEquals("Clients list should have only one item", 1, caseEntity.getClients().size());
    }

    /**
     * @brief Test removing a client
     *
     * Verifies that a client is properly removed from the case's client list
     */
    @Test
    public void Test_RemoveClient_RemovesClientFromList() {
        // Setup
        Case caseEntity = new Case("CASE2023-006", "Remove Client Test", CaseType.CIVIL);
        Client client = new Client("Dave", "Davis", "dave@example.com");
        caseEntity.addClient(client);

        // Action
        caseEntity.removeClient(client);

        // Verification
        assertFalse("Case client list should not contain the client", caseEntity.getClients().contains(client));
    }

    /**
     * @brief Test removing a client when clients list is null
     *
     * Verifies that removing a client from a case with null clients list does not throw an exception
     */
    @Test
    public void Test_RemoveClient_WithNullClients_DoesNotThrowException() {
        // Setup
        Case caseEntity = new Case("CASE2023-007", "Null Clients Remove Test", CaseType.CRIMINAL);
        caseEntity.setClients(null); // Manually set to null
        Client client = new Client("Eve", "Evans", "eve@example.com");

        // Action and Verification (should not throw exception)
        try {
            caseEntity.removeClient(client);
            // If we get here, no exception was thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    /**
     * @brief Test getting clients when clients list is null
     *
     * Verifies that an empty list is returned when getting clients from a case with null clients list
     */
    @Test
    public void Test_GetClients_WithNullClients_ReturnsEmptyList() {
        // Setup
        Case caseEntity = new Case("CASE2023-008", "Null Clients Get Test", CaseType.FAMILY);
        caseEntity.setClients(null); // Manually set to null

        // Action
        List<Client> clients = caseEntity.getClients();

        // Verification
        assertNotNull("Clients list should not be null", clients);
        assertTrue("Clients list should be empty", clients.isEmpty());
    }

    /**
     * @brief Test setting clients list
     *
     * Verifies that setting the clients list updates it correctly
     */
    @Test
    public void Test_SetClients_UpdatesClientsList() {
        // Setup
        Case caseEntity = new Case("CASE2023-009", "Set Clients Test", CaseType.CORPORATE);
        List<Client> clientList = new ArrayList<>();
        Client client1 = new Client("Frank", "Foster", "frank@example.com");
        Client client2 = new Client("Grace", "Green", "grace@example.com");
        clientList.add(client1);
        clientList.add(client2);

        // Action
        caseEntity.setClients(clientList);

        // Verification
        assertEquals("Clients list should be set correctly", clientList, caseEntity.getClients());
        assertEquals("Clients list should have 2 items", 2, caseEntity.getClients().size());
    }

    /**
     * @brief Test adding a hearing to a case
     *
     * Verifies that a hearing is properly added to the case and that the case reference is set in the hearing
     */
    @Test
    public void Test_AddHearing_AddsHearingAndSetsCase() {
        // Setup
        Case caseEntity = new Case("CASE2023-010", "Hearing Test Case", CaseType.CIVIL);
        Hearing hearing = new Hearing(caseEntity, LocalDateTime.now(), "Judge Smith", "Courtroom 101");

        // Action
        caseEntity.addHearing(hearing);

        // Verification
        assertTrue("Hearings list should contain the hearing", caseEntity.getHearings().contains(hearing));
        assertEquals("Hearing's case should be set correctly", caseEntity, hearing.getCse());
    }

    /**
     * @brief Test adding a document to a case
     *
     * Verifies that a document is properly added to the case and that the case reference is set in the document
     */
    @Test
    public void Test_AddDocument_AddsDocumentAndSetsCase() {
        // Setup
        Case caseEntity = new Case("CASE2023-011", "Document Test Case", CaseType.CRIMINAL);
        Document document = new Document("Test Document", DocumentType.CONTRACT, caseEntity, "Document content");

        // Action
        caseEntity.addDocument(document);

        // Verification
        assertTrue("Documents list should contain the document", caseEntity.getDocuments().contains(document));
        assertEquals("Document's case should be set correctly", caseEntity, document.getCse());
    }

    /**
     * @brief Test prePersist lifecycle callback
     *
     * Verifies that prePersist updates both createdAt and updatedAt timestamps
     */
    @Test
    public void Test_PrePersist_UpdatesTimestamps() {
        // Setup
        Case caseEntity = new Case("CASE2023-012", "Timestamp Test", CaseType.FAMILY);
        LocalDateTime originalCreatedAt = caseEntity.getCreatedAt();

        // Action
        // Wait a bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if interrupted
        }
        caseEntity.prePersist();

        // Verification
        assertNotEquals("createdAt should be updated", originalCreatedAt, caseEntity.getCreatedAt());
        assertEquals("createdAt and updatedAt should be the same", caseEntity.getCreatedAt(), caseEntity.getUpdatedAt());
    }

    /**
     * @brief Test preUpdate lifecycle callback
     *
     * Verifies that preUpdate updates only the updatedAt timestamp
     */
    @Test
    public void Test_PreUpdate_UpdatesOnlyUpdatedAt() {
        // Setup
        Case caseEntity = new Case("CASE2023-013", "Update Timestamp Test", CaseType.CORPORATE);
        LocalDateTime originalCreatedAt = caseEntity.getCreatedAt();

        // Action
        // Wait a bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if interrupted
        }
        caseEntity.preUpdate();

        // Verification
        assertEquals("createdAt should not change", originalCreatedAt, caseEntity.getCreatedAt());
        assertNotEquals("updatedAt should be updated", originalCreatedAt, caseEntity.getUpdatedAt());
    }

    /**
     * @brief Test toString method
     *
     * Verifies that toString contains all relevant case information
     */
    @Test
    public void Test_ToString_ContainsRelevantInfo() {
        // Setup
        Long id = 123L;
        String caseNumber = "CASE2023-014";
        String title = "ToString Test";
        CaseType type = CaseType.OTHER;
        CaseStatus status = CaseStatus.ACTIVE;

        Case caseEntity = new Case(id, caseNumber, title, type);
        caseEntity.setStatus(status);

        // Action
        String toString = caseEntity.toString();

        // Verification
        assertTrue("toString should contain id", toString.contains(id.toString()));
        assertTrue("toString should contain case number", toString.contains(caseNumber));
        assertTrue("toString should contain title", toString.contains(title));
        assertTrue("toString should contain type", toString.contains(type.toString()));
        assertTrue("toString should contain status", toString.contains(status.toString()));
    }
}