package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
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
 * @brief Test class for the Client entity
 *
 * Contains unit tests that verify the functionality of the Client class methods
 * and constructors.
 */
public class ClientTest {

    /**
     * @brief Setup method that runs before each test
     *
     * Prepares the test database by creating necessary tables
     *
     * @throws SQLException if there's an error creating the database tables
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare the test database
        TestDatabaseManager.createTables();
    }

    /**
     * @brief Cleanup method that runs after each test
     *
     * Closes the test database connection
     *
     * @throws SQLException if there's an error closing the database connection
     */
    @After
    public void tearDown() throws SQLException {
        // Close the test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Test the constructor with ID parameter
     *
     * Verifies that the constructor with ID parameter sets all values correctly
     */
    @Test
    public void Test_Constructor_WithId_SetsCorrectValues() {
        // Action
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@example.com";
        Client client = new Client(id, name, surname, email);

        // Verification
        assertEquals("ID should be set correctly", id, client.getId());
        assertEquals("Name should be set correctly", name, client.getName());
        assertEquals("Surname should be set correctly", surname, client.getSurname());
        assertEquals("Email should be set correctly", email, client.getEmail());
        assertNotNull("createdAt should not be null", client.getCreatedAt());
        assertNotNull("updatedAt should not be null", client.getUpdatedAt());
    }

    /**
     * @brief Test the constructor without ID parameter
     *
     * Verifies that the constructor without ID parameter sets all values correctly
     */
    @Test
    public void Test_Constructor_WithoutId_SetsCorrectValues() {
        // Action
        String name = "Jane";
        String surname = "Smith";
        String email = "jane.smith@example.com";
        Client client = new Client(name, surname, email);

        // Verification
        assertNull("ID should be null", client.getId());
        assertEquals("Name should be set correctly", name, client.getName());
        assertEquals("Surname should be set correctly", surname, client.getSurname());
        assertEquals("Email should be set correctly", email, client.getEmail());
        assertNotNull("createdAt should not be null", client.getCreatedAt());
        assertNotNull("updatedAt should not be null", client.getUpdatedAt());
    }

    /**
     * @brief Test getFullName method
     *
     * Verifies that getFullName returns the correct full name
     */
    @Test
    public void Test_GetFullName_ReturnsCorrectFullName() {
        // Arrange
        Client client = new Client("Alice", "Johnson", "alice@example.com");

        // Action
        String fullName = client.getFullName();

        // Verification
        assertEquals("Full name should be correct", "Alice Johnson", fullName);
    }

    /**
     * @brief Test addCase method
     *
     * Verifies that addCase adds a case to the list
     */
    @Test
    public void Test_AddCase_AddsCaseToList() {
        // Arrange
        Client client = new Client("Bob", "Brown", "bob@example.com");
        Case caseEntity = new Case("CASE1", "Test Case", CaseType.CIVIL);

        // Action
        client.addCase(caseEntity);

        // Verification
        assertTrue("Client's case list should contain the case", client.getCases().contains(caseEntity));
        assertTrue("Case's client list should contain the client", caseEntity.getClients().contains(client));
    }

    /**
     * @brief Test addCase method with null cases list
     *
     * Verifies that addCase initializes the cases list if it's null
     */
    @Test
    public void Test_AddCase_WithNullCases_InitializesList() {
        // Arrange
        Client client = new Client("Charlie", "Clark", "charlie@example.com");
        client.setCases(null); // Manually set to null
        Case caseEntity = new Case("CASE2", "Another Test Case", CaseType.CRIMINAL);

        // Action
        client.addCase(caseEntity);

        // Verification
        assertNotNull("Cases list should not be null", client.getCases());
        assertTrue("Client's case list should contain the case", client.getCases().contains(caseEntity));
    }

    /**
     * @brief Test addCase method with already added case
     *
     * Verifies that addCase doesn't add duplicates
     */
    @Test
    public void Test_AddCase_AlreadyAdded_DoesNotAddDuplicate() {
        // Arrange
        Client client = new Client("Dave", "Davis", "dave@example.com");
        Case caseEntity = new Case("CASE3", "Yet Another Test Case", CaseType.FAMILY);
        client.addCase(caseEntity); // First addition

        // Action
        client.addCase(caseEntity); // Second addition

        // Verification
        assertEquals("Cases list should have only one item", 1, client.getCases().size());
    }

    /**
     * @brief Test removeCase method
     *
     * Verifies that removeCase removes the case from the list
     */
    @Test
    public void Test_RemoveCase_RemovesCaseFromList() {
        // Arrange
        Client client = new Client("Eve", "Evans", "eve@example.com");
        Case caseEntity = new Case("CASE4", "Remove Test Case", CaseType.CORPORATE);
        client.addCase(caseEntity);

        // Action
        client.removeCase(caseEntity);

        // Verification
        assertFalse("Client's case list should not contain the case", client.getCases().contains(caseEntity));
        assertFalse("Case's client list should not contain the client", caseEntity.getClients().contains(client));
    }

    /**
     * @brief Test removeCase method with null cases list
     *
     * Verifies that removeCase doesn't throw an exception when cases list is null
     */
    @Test
    public void Test_RemoveCase_WithNullCases_DoesNotThrowException() {
        // Arrange
        Client client = new Client("Frank", "Foster", "frank@example.com");
        client.setCases(null); // Manually set to null
        Case caseEntity = new Case("CASE5", "Null Test Case", CaseType.OTHER);

        // Action and Verification (should not throw exception)
        try {
            client.removeCase(caseEntity);
            // If it gets here, no exception was thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("No exception should have been thrown: " + e.getMessage());
        }
    }

    /**
     * @brief Test getCases method with null cases list
     *
     * Verifies that getCases returns an empty list when cases is null
     */
    @Test
    public void Test_GetCases_WithNullCases_ReturnsEmptyList() {
        // Arrange
        Client client = new Client("Grace", "Green", "grace@example.com");
        client.setCases(null); // Manually set to null

        // Action
        List<Case> cases = client.getCases();

        // Verification
        assertNotNull("Cases list should not be null", cases);
        assertTrue("Cases list should be empty", cases.isEmpty());
    }

    /**
     * @brief Test setCases method
     *
     * Verifies that setCases updates the cases list
     */
    @Test
    public void Test_SetCases_UpdatesCasesList() {
        // Arrange
        Client client = new Client("Harry", "Harrison", "harry@example.com");
        List<Case> caseList = new ArrayList<>();
        Case case1 = new Case("CASE6", "Set Test Case 1", CaseType.CIVIL);
        Case case2 = new Case("CASE7", "Set Test Case 2", CaseType.CRIMINAL);
        caseList.add(case1);
        caseList.add(case2);

        // Action
        client.setCases(caseList);

        // Verification
        assertEquals("Cases list should be set correctly", caseList, client.getCases());
        assertEquals("Cases list should have 2 items", 2, client.getCases().size());
    }

    /**
     * @brief Test prePersist method
     *
     * Verifies that prePersist updates the timestamps
     */
    @Test
    public void Test_PrePersist_UpdatesTimestamps() {
        // Arrange
        Client client = new Client("Ian", "Ivanov", "ian@example.com");
        LocalDateTime originalCreatedAt = client.getCreatedAt();

        // Action
        // Wait a bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if waiting is interrupted
        }
        client.prePersist();

        // Verification
        assertNotEquals("createdAt should be updated", originalCreatedAt, client.getCreatedAt());
        assertEquals("createdAt and updatedAt should be the same", client.getCreatedAt(), client.getUpdatedAt());
    }

    /**
     * @brief Test preUpdate method
     *
     * Verifies that preUpdate only updates the updatedAt timestamp
     */
    @Test
    public void Test_PreUpdate_UpdatesOnlyUpdatedAt() {
        // Arrange
        Client client = new Client("Jack", "Johnson", "jack@example.com");
        LocalDateTime originalCreatedAt = client.getCreatedAt();

        // Action
        // Wait a bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if waiting is interrupted
        }
        client.preUpdate();

        // Verification
        assertEquals("createdAt should not change", originalCreatedAt, client.getCreatedAt());
        assertNotEquals("updatedAt should be updated", originalCreatedAt, client.getUpdatedAt());
    }

    /**
     * @brief Test toString method
     *
     * Verifies that toString contains all relevant information
     */
    @Test
    public void Test_ToString_ContainsRelevantInfo() {
        // Arrange
        Long id = 123L;
        String name = "Karen";
        String surname = "King";
        String email = "karen@example.com";
        Client client = new Client(id, name, surname, email);

        // Action
        String toString = client.toString();

        // Verification
        assertTrue("toString should contain id", toString.contains(id.toString()));
        assertTrue("toString should contain name", toString.contains(name));
        assertTrue("toString should contain surname", toString.contains(surname));
        assertTrue("toString should contain email", toString.contains(email));
    }
}