package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * @brief Test class for the CaseClient model
 *
 * This class contains unit tests for the CaseClient class functionality,
 * including constructors and getter/setter methods.
 */
public class CaseClientTest {

    private Case testCase;
    private Client testClient;

    /**
     * @brief Sets up the test environment before each test
     *
     * Prepares the test database and creates test Case and Client objects
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();

        // Create Case and Client objects for testing
        testCase = new Case("CC-TEST", "CaseClient Test Case", CaseType.CIVIL);
        testClient = new Client("CaseClient", "Test", "caselient@example.com");
    }

    /**
     * @brief Cleans up the test environment after each test
     *
     * Closes the test database connection
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Tests that the default constructor creates an empty object
     */
    @Test
    public void Test_Constructor_Default_CreatesEmptyObject() {
        // Action
        CaseClient caseClient = new CaseClient();

        // Verification
        assertNull("ID should be null", caseClient.getId());
        assertNull("Case should be null", caseClient.getCse());
        assertNull("Client should be null", caseClient.getClient());
    }

    /**
     * @brief Tests that the constructor with parameters sets values correctly
     */
    @Test
    public void Test_Constructor_WithCaseAndClient_SetsCorrectValues() {
        // Action
        CaseClient caseClient = new CaseClient(testCase, testClient);

        // Verification
        assertNull("ID should be null", caseClient.getId());
        assertEquals("Case should be set correctly", testCase, caseClient.getCse());
        assertEquals("Client should be set correctly", testClient, caseClient.getClient());
    }

    /**
     * @brief Tests that getters and setters work correctly
     */
    @Test
    public void Test_SettersAndGetters_WorkCorrectly() {
        // Setup
        CaseClient caseClient = new CaseClient();
        Long id = 123L;

        // Action
        caseClient.setId(id);
        caseClient.setCse(testCase);
        caseClient.setClient(testClient);

        // Verification
        assertEquals("ID should be set correctly", id, caseClient.getId());
        assertEquals("Case should be set correctly", testCase, caseClient.getCse());
        assertEquals("Client should be set correctly", testClient, caseClient.getClient());
    }
}