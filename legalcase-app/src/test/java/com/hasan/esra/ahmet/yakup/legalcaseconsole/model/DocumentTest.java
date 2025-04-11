package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * @brief Test class for Document entity
 *
 * Contains unit tests for Document class methods and constructors
 */
public class DocumentTest {

    /**
     * @brief Set up test environment before each test
     *
     * Prepares the test database by creating necessary tables
     *
     * @throws SQLException If database setup fails
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
     * @throws SQLException If connection close fails
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Test constructor with ID parameter
     *
     * Verifies that the constructor with ID sets all values correctly
     */
    @Test
    public void Test_Constructor_WithId_SetsCorrectValues() {
        // Setup
        Long id = 1L;
        String title = "Test Document";
        DocumentType type = DocumentType.CONTRACT;
        Document document = new Document(id, title, type);

        // Verification
        assertEquals("ID should be set correctly", id, document.getId());
        assertEquals("Title should be set correctly", title, document.getTitle());
        assertEquals("Type should be set correctly", type, document.getType());
        assertNotNull("createdAt should not be null", document.getCreatedAt());
        assertNotNull("updatedAt should not be null", document.getUpdatedAt());
    }

    /**
     * @brief Test constructor with case and content parameters
     *
     * Verifies that the constructor with case and content sets all values correctly
     */
    @Test
    public void Test_Constructor_WithCaseAndContent_SetsCorrectValues() {
        // Setup
        String title = "Another Test Document";
        DocumentType type = DocumentType.EVIDENCE;
        Case caseEntity = new Case("DOC001", "Document Test Case", CaseType.CIVIL);
        String content = "This is a test document content";

        // Action
        Document document = new Document(title, type, caseEntity, content);

        // Verification
        assertNull("ID should be null", document.getId());
        assertEquals("Title should be set correctly", title, document.getTitle());
        assertEquals("Type should be set correctly", type, document.getType());
        assertEquals("Case should be set correctly", caseEntity, document.getCse());
        assertEquals("Content should be set correctly", content, document.getContent());
        assertEquals("Content type should be text/plain", "text/plain", document.getContentType());
        assertNotNull("createdAt should not be null", document.getCreatedAt());
        assertNotNull("updatedAt should not be null", document.getUpdatedAt());
    }

    /**
     * @brief Test prePersist method updates timestamps
     *
     * Verifies that prePersist method correctly updates both timestamps
     */
    @Test
    public void Test_PrePersist_UpdatesTimestamps() {
        // Setup
        Case caseEntity = new Case("DOC001", "Document Test Case", CaseType.CIVIL);
        Document document = new Document("Timestamp Test", DocumentType.PETITION, caseEntity, "Test content");
        LocalDateTime originalCreatedAt = document.getCreatedAt();

        // Action
        // Wait for a moment
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if waiting is interrupted
        }
        document.prePersist();

        // Verification
        assertNotEquals("createdAt should be updated", originalCreatedAt, document.getCreatedAt());
        assertEquals("createdAt and updatedAt should be the same", document.getCreatedAt(), document.getUpdatedAt());
    }

    /**
     * @brief Test preUpdate method updates only updatedAt
     *
     * Verifies that preUpdate method correctly updates only the updatedAt timestamp
     */
    @Test
    public void Test_PreUpdate_UpdatesOnlyUpdatedAt() {
        // Setup
        Case caseEntity = new Case("DOC002", "Update Document Test Case", CaseType.CIVIL);
        Document document = new Document("Update Test", DocumentType.COURT_ORDER, caseEntity, "Test content for update");
        LocalDateTime originalCreatedAt = document.getCreatedAt();

        // Action
        // Wait for a moment
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if waiting is interrupted
        }
        document.preUpdate();

        // Verification
        assertEquals("createdAt should not change", originalCreatedAt, document.getCreatedAt());
        assertNotEquals("updatedAt should be updated", originalCreatedAt, document.getUpdatedAt());
    }

    /**
     * @brief Test toString method includes relevant information
     *
     * Verifies that toString method returns a string containing all relevant object information
     */
    @Test
    public void Test_ToString_ContainsRelevantInfo() {
        // Setup
        Long id = 123L;
        String title = "ToString Test";
        DocumentType type = DocumentType.OTHER;
        Case caseEntity = new Case(456L, "CASE001", "Test Case", CaseType.CIVIL);
        Document document = new Document(id, title, type);
        document.setCse(caseEntity);

        // Action
        String toString = document.toString();

        // Verification
        assertTrue("toString should contain id", toString.contains(id.toString()));
        assertTrue("toString should contain title", toString.contains(title));
        assertTrue("toString should contain type", toString.contains(type.toString()));
        assertTrue("toString should contain case ID", toString.contains(caseEntity.getId().toString()));
    }
}