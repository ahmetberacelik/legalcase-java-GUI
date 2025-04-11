package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.DocumentDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;

/**
 * @brief Test suite for the DocumentService class
 *
 * This class tests all functionality of the DocumentService including:
 * - Creating documents
 * - Retrieving documents by ID, case, or type
 * - Updating documents
 * - Deleting documents
 * - Searching for documents
 * - Error handling for various scenarios
 */
public class DocumentServiceTest {

    private ConnectionSource connectionSource;
    private DocumentDAO documentDAO;
    private CaseDAO caseDAO;
    private DocumentService documentService;
    private Case testCase;

    /**
     * @brief Setup method run before each test
     *
     * Initializes database connection, DAOs, services, and test data
     */
    @Before
    public void setup() throws SQLException {
        // Get a connection to the test database
        connectionSource = TestDatabaseManager.getConnectionSource();

        // Clear any existing data
        TestDatabaseManager.clearTables();

        // Create real DAOs with the test connection
        documentDAO = new DocumentDAO(connectionSource);
        caseDAO = new CaseDAO(connectionSource);

        // Create the service with the real DAOs
        documentService = new DocumentService(documentDAO, caseDAO);

        // Create a test case for document tests
        testCase = new Case("123", "Test_Case123", CaseType.CIVIL);
        caseDAO.create(testCase);
    }

    /**
     * @brief Cleanup method run after each test
     *
     * Clears test data from the database
     */
    @After
    public void tearDown() throws SQLException {
        // Clean up after each test
        TestDatabaseManager.clearTables();
    }

    /**
     * @brief Test creating a document with valid data
     *
     * Verifies that a document is created correctly and stored in the database
     */
    @Test
    public void Test_CreateDocument_ValidData_ReturnsDocumentWithId() {
        // Test data
        String title = "Test Document";
        DocumentType type = DocumentType.CONTRACT;
        String content = "This is a test document content";

        // Create document
        Document document = documentService.createDocument(testCase.getId(), title, type, content);

        // Verify the document was created correctly
        assertNotNull("Created document should not be null", document);
        assertNotNull("Document ID should not be null", document.getId());
        assertEquals("Title should match", title, document.getTitle());
        assertEquals("Type should match", type, document.getType());
        assertEquals("Content should match", content, document.getContent());
        assertEquals("Case ID should match", testCase.getId(), document.getCse().getId());

        // Verify the document exists in the database
        Optional<Document> retrievedDocument = documentService.getDocumentById(document.getId());
        assertTrue("Document should exist in database", retrievedDocument.isPresent());
        assertEquals("Retrieved document ID should match", document.getId(), retrievedDocument.get().getId());
    }

    /**
     * @brief Test creating a document with a non-existent case ID
     *
     * Verifies that an IllegalArgumentException is thrown when attempting to create
     * a document with a case ID that doesn't exist
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_CreateDocument_NonExistentCaseId_ThrowsIllegalArgumentException() {
        // Try to create a document with a non-existent case ID
        documentService.createDocument(9999L, "Invalid Case Document", DocumentType.EVIDENCE, "Test content");
    }

    /**
     * @brief Test creating a document when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_CreateDocument_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.createDocument(testCase.getId(), "Test Document", DocumentType.CONTRACT, "Test content");
    }

    /**
     * @brief Test retrieving a document by ID when the document exists
     *
     * Verifies that the correct document is returned when a valid ID is provided
     */
    @Test
    public void Test_GetDocumentById_ExistingId_ReturnsDocument() {
        // Create a document
        Document document = documentService.createDocument(testCase.getId(), "Test Document",
                DocumentType.CONTRACT, "Test content");

        // Retrieve the document by ID
        Optional<Document> retrievedDocument = documentService.getDocumentById(document.getId());

        // Verify
        assertTrue("Document should be found", retrievedDocument.isPresent());
        assertEquals("ID should match", document.getId(), retrievedDocument.get().getId());
        assertEquals("Title should match", "Test Document", retrievedDocument.get().getTitle());
    }

    /**
     * @brief Test retrieving a document by ID when the document doesn't exist
     *
     * Verifies that an empty Optional is returned when an invalid ID is provided
     */
    @Test
    public void Test_GetDocumentById_NonExistentId_ReturnsEmptyOptional() {
        // Try to retrieve a document with a non-existent ID
        Optional<Document> retrievedDocument = documentService.getDocumentById(9999L);

        // Verify
        assertFalse("Non-existent document should not be found", retrievedDocument.isPresent());
    }

    /**
     * @brief Test retrieving a document by ID when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetDocumentById_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.getDocumentById(1L);
    }

    /**
     * @brief Test retrieving document content by ID when the document exists
     *
     * Verifies that the correct content is returned for a valid document ID
     */
    @Test
    public void Test_GetDocumentContent_ExistingId_ReturnsContent() {
        // Create a document
        String content = "This is the document content to retrieve";
        Document document = documentService.createDocument(testCase.getId(), "Content Test",
                DocumentType.EVIDENCE, content);

        // Get the document content
        String retrievedContent = documentService.getDocumentContent(document.getId());

        // Verify
        assertEquals("Content should match", content, retrievedContent);
    }

    /**
     * @brief Test retrieving document content by ID when the document doesn't exist
     *
     * Verifies that an IllegalArgumentException is thrown when an invalid ID is provided
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetDocumentContent_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to get content of a document with a non-existent ID
        documentService.getDocumentContent(9999L);
    }

    /**
     * @brief Test retrieving document content when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetDocumentContent_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.getDocumentContent(1L);
    }

    /**
     * @brief Test retrieving all documents when multiple documents exist
     *
     * Verifies that all documents are returned correctly
     */
    @Test
    public void Test_GetAllDocuments_MultipleDocuments_ReturnsAllDocuments() {
        // Create several documents
        documentService.createDocument(testCase.getId(), "Document One", DocumentType.CONTRACT, "Content 1");
        documentService.createDocument(testCase.getId(), "Document Two", DocumentType.EVIDENCE, "Content 2");
        documentService.createDocument(testCase.getId(), "Document Three", DocumentType.PETITION, "Content 3");

        // Get all documents
        List<Document> documents = documentService.getAllDocuments();

        // Verify
        assertNotNull("Document list should not be null", documents);
        assertEquals("Should have 3 documents", 3, documents.size());
    }

    /**
     * @brief Test retrieving all documents when no documents exist
     *
     * Verifies that an empty list is returned when no documents exist in the database
     */
    @Test
    public void Test_GetAllDocuments_NoDocuments_ReturnsEmptyList() {
        // Get all documents without creating any
        List<Document> documents = documentService.getAllDocuments();

        // Verify
        assertNotNull("Document list should not be null", documents);
        assertTrue("Document list should be empty", documents.isEmpty());
    }

    /**
     * @brief Test retrieving all documents when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetAllDocuments_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.getAllDocuments();
    }

    /**
     * @brief Test retrieving documents by case ID when documents exist for the case
     *
     * Verifies that only documents associated with the specified case are returned
     */
    @Test
    public void Test_GetDocumentsByCaseId_ExistingCaseId_ReturnsDocumentsForCase() {
        // Create a second case
        Case secondCase = new Case("456", "Second case title", CaseType.CRIMINAL);
        try {
            caseDAO.create(secondCase);
        } catch (SQLException e) {
            fail("Failed to create second test case: " + e.getMessage());
        }

        // Create documents for both cases
        documentService.createDocument(testCase.getId(), "First Case Doc 1", DocumentType.CONTRACT, "Content 1");
        documentService.createDocument(testCase.getId(), "First Case Doc 2", DocumentType.EVIDENCE, "Content 2");
        documentService.createDocument(secondCase.getId(), "Second Case Doc", DocumentType.PETITION, "Content 3");

        // Get documents for first case
        List<Document> firstCaseDocs = documentService.getDocumentsByCaseId(testCase.getId());

        // Verify
        assertNotNull("Document list should not be null", firstCaseDocs);
        assertEquals("Should have 2 documents for first case", 2, firstCaseDocs.size());

        // Get documents for second case
        List<Document> secondCaseDocs = documentService.getDocumentsByCaseId(secondCase.getId());

        // Verify
        assertNotNull("Document list should not be null", secondCaseDocs);
        assertEquals("Should have 1 document for second case", 1, secondCaseDocs.size());
    }

    /**
     * @brief Test retrieving documents by case ID when the case doesn't exist
     *
     * Verifies that an IllegalArgumentException is thrown when an invalid case ID is provided
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetDocumentsByCaseId_NonExistentCaseId_ThrowsIllegalArgumentException() {
        // Try to get documents for a non-existent case
        documentService.getDocumentsByCaseId(9999L);
    }

    /**
     * @brief Test retrieving documents by case ID when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetDocumentsByCaseId_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.getDocumentsByCaseId(testCase.getId());
    }

    /**
     * @brief Test retrieving documents by type when documents of that type exist
     *
     * Verifies that only documents of the specified type are returned
     */
    @Test
    public void Test_GetDocumentsByType_ExistingType_ReturnsDocumentsOfType() {
        // Create documents of different types
        documentService.createDocument(testCase.getId(), "Contract Doc 1", DocumentType.CONTRACT, "Contract 1");
        documentService.createDocument(testCase.getId(), "Contract Doc 2", DocumentType.CONTRACT, "Contract 2");
        documentService.createDocument(testCase.getId(), "Evidence Doc", DocumentType.EVIDENCE, "Evidence");
        documentService.createDocument(testCase.getId(), "Petition Doc", DocumentType.PETITION, "Petition");

        // Get documents of CONTRACT type
        List<Document> contractDocs = documentService.getDocumentsByType(DocumentType.CONTRACT);

        // Verify
        assertNotNull("Document list should not be null", contractDocs);
        assertEquals("Should have 2 CONTRACT documents", 2, contractDocs.size());
        for (Document doc : contractDocs) {
            assertEquals("Document type should be CONTRACT", DocumentType.CONTRACT, doc.getType());
        }

        // Get documents of EVIDENCE type
        List<Document> evidenceDocs = documentService.getDocumentsByType(DocumentType.EVIDENCE);

        // Verify
        assertNotNull("Document list should not be null", evidenceDocs);
        assertEquals("Should have 1 EVIDENCE document", 1, evidenceDocs.size());
        assertEquals("Document type should be EVIDENCE", DocumentType.EVIDENCE, evidenceDocs.get(0).getType());
    }

    /**
     * @brief Test retrieving documents by type when no documents of that type exist
     *
     * Verifies that an empty list is returned when no documents of the specified type exist
     */
    @Test
    public void Test_GetDocumentsByType_NoDocumentsOfType_ReturnsEmptyList() {
        // Create documents of only one type
        documentService.createDocument(testCase.getId(), "Contract Doc", DocumentType.CONTRACT, "Contract");

        // Get documents of a type that doesn't exist in the database
        List<Document> courtOrderDocs = documentService.getDocumentsByType(DocumentType.COURT_ORDER);

        // Verify
        assertNotNull("Document list should not be null", courtOrderDocs);
        assertTrue("Document list should be empty", courtOrderDocs.isEmpty());
    }

    /**
     * @brief Test retrieving documents by type when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetDocumentsByType_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.getDocumentsByType(DocumentType.CONTRACT);
    }

    /**
     * @brief Test searching for documents by title when matching documents exist
     *
     * Verifies that only documents with matching titles are returned
     */
    @Test
    public void Test_SearchDocumentsByTitle_MatchingTitle_ReturnsMatchingDocuments() {
        // Create documents with different titles
        documentService.createDocument(testCase.getId(), "Contract Agreement", DocumentType.CONTRACT, "Contract content");
        documentService.createDocument(testCase.getId(), "Evidence Report", DocumentType.EVIDENCE, "Evidence content");
        documentService.createDocument(testCase.getId(), "Petition for Damages", DocumentType.PETITION, "Petition content");
        documentService.createDocument(testCase.getId(), "Contract Amendment", DocumentType.CONTRACT, "Amendment content");

        // Search for documents with "Contract" in the title
        List<Document> contractDocs = documentService.searchDocumentsByTitle("Contract");

        // Verify
        assertNotNull("Search results should not be null", contractDocs);
        assertEquals("Should find 2 documents with 'Contract' in title", 2, contractDocs.size());

        // Search for documents with "Evidence" in the title
        List<Document> evidenceDocs = documentService.searchDocumentsByTitle("Evidence");

        // Verify
        assertNotNull("Search results should not be null", evidenceDocs);
        assertEquals("Should find 1 document with 'Evidence' in title", 1, evidenceDocs.size());
        assertTrue("Document title should contain 'Evidence'",
                evidenceDocs.get(0).getTitle().contains("Evidence"));
    }

    /**
     * @brief Test searching for documents by title when no matching documents exist
     *
     * Verifies that an empty list is returned when no documents match the search criteria
     */
    @Test
    public void Test_SearchDocumentsByTitle_NoMatch_ReturnsEmptyList() {
        // Create some documents
        documentService.createDocument(testCase.getId(), "Contract Agreement", DocumentType.CONTRACT, "Contract content");
        documentService.createDocument(testCase.getId(), "Evidence Report", DocumentType.EVIDENCE, "Evidence content");

        // Search for a non-existent title
        List<Document> results = documentService.searchDocumentsByTitle("Nonexistent");

        // Verify
        assertNotNull("Search results should not be null", results);
        assertTrue("Should find no documents", results.isEmpty());
    }

    /**
     * @brief Test searching for documents by title when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_SearchDocumentsByTitle_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.searchDocumentsByTitle("Test");
    }

    /**
     * @brief Test updating a document with valid data
     *
     * Verifies that a document is updated correctly and changes are persisted to the database
     */
    @Test
    public void Test_UpdateDocument_ValidData_ReturnsUpdatedDocument() {
        // Create a document
        Document document = documentService.createDocument(testCase.getId(), "Original Title",
                DocumentType.CONTRACT, "Original content");

        // Update the document
        Document updatedDocument = documentService.updateDocument(
                document.getId(), "Updated Title", DocumentType.EVIDENCE, "Updated content");

        // Verify the update was successful
        assertEquals("Title should be updated", "Updated Title", updatedDocument.getTitle());
        assertEquals("Type should be updated", DocumentType.EVIDENCE, updatedDocument.getType());
        assertEquals("Content should be updated", "Updated content", updatedDocument.getContent());

        // Verify the update persisted to the database
        Optional<Document> retrievedDocument = documentService.getDocumentById(document.getId());
        assertTrue("Document should exist", retrievedDocument.isPresent());
        assertEquals("Updated title should be saved", "Updated Title", retrievedDocument.get().getTitle());
        assertEquals("Updated type should be saved", DocumentType.EVIDENCE, retrievedDocument.get().getType());
        assertEquals("Updated content should be saved", "Updated content", retrievedDocument.get().getContent());
    }

    /**
     * @brief Test updating a document with null fields
     *
     * Verifies that only non-null fields are updated while null fields remain unchanged
     */
    @Test
    public void Test_UpdateDocument_NullFields_OnlyUpdatesNonNullFields() {
        // Create a document
        Document document = documentService.createDocument(testCase.getId(), "Original Title",
                DocumentType.CONTRACT, "Original content");

        // Update only the title, leaving other fields unchanged
        Document updatedDocument = documentService.updateDocument(
                document.getId(), "Updated Title", null, null);

        // Verify only the title was updated
        assertEquals("Title should be updated", "Updated Title", updatedDocument.getTitle());
        assertEquals("Type should remain unchanged", DocumentType.CONTRACT, updatedDocument.getType());
        assertEquals("Content should remain unchanged", "Original content", updatedDocument.getContent());

        // Update only the type, leaving other fields unchanged
        updatedDocument = documentService.updateDocument(
                document.getId(), null, DocumentType.EVIDENCE, null);

        // Verify only the type was updated
        assertEquals("Title should remain unchanged", "Updated Title", updatedDocument.getTitle());
        assertEquals("Type should be updated", DocumentType.EVIDENCE, updatedDocument.getType());
        assertEquals("Content should remain unchanged", "Original content", updatedDocument.getContent());

        // Update only the content, leaving other fields unchanged
        updatedDocument = documentService.updateDocument(
                document.getId(), null, null, "Updated content");

        // Verify only the content was updated
        assertEquals("Title should remain unchanged", "Updated Title", updatedDocument.getTitle());
        assertEquals("Type should remain unchanged", DocumentType.EVIDENCE, updatedDocument.getType());
        assertEquals("Content should be updated", "Updated content", updatedDocument.getContent());
    }

    /**
     * @brief Test updating a document with a non-existent ID
     *
     * Verifies that an IllegalArgumentException is thrown when attempting to update
     * a document with an ID that doesn't exist
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateDocument_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to update a document with a non-existent ID
        documentService.updateDocument(9999L, "Nonexistent Document", DocumentType.OTHER, "Content");
    }

    /**
     * @brief Test updating a document when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_UpdateDocument_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.updateDocument(1L, "Updated Title", DocumentType.EVIDENCE, "Updated content");
    }

    /**
     * @brief Test deleting a document with a valid ID
     *
     * Verifies that a document is correctly deleted from the database
     */
    @Test
    public void Test_DeleteDocument_ExistingId_DocumentIsDeleted() {
        // Create a document
        Document document = documentService.createDocument(testCase.getId(), "Document to Delete",
                DocumentType.CONTRACT, "Content to delete");

        // Verify the document exists
        assertTrue("Document should exist before deletion",
                documentService.getDocumentById(document.getId()).isPresent());

        // Delete the document
        documentService.deleteDocument(document.getId());

        // Verify the document no longer exists
        assertFalse("Document should not exist after deletion",
                documentService.getDocumentById(document.getId()).isPresent());
    }

    /**
     * @brief Test deleting a document with a non-existent ID
     *
     * Verifies that an IllegalArgumentException is thrown when attempting to delete
     * a document with an ID that doesn't exist
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_DeleteDocument_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to delete a document with a non-existent ID
        documentService.deleteDocument(9999L);
    }

    /**
     * @brief Test deleting a document when an SQL exception occurs
     *
     * Verifies that a RuntimeException is thrown when the underlying DAO throws an SQLException
     */
    @Test(expected = RuntimeException.class)
    public void Test_DeleteDocument_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        documentService = new DocumentService(new ThrowingDocumentDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        documentService.deleteDocument(1L);
    }

    /**
     * @brief Helper class for testing SQL exception handling
     *
     * A DocumentDAO implementation that throws SQLException for all operations
     * to test the exception handling logic in DocumentService
     */
    private static class ThrowingDocumentDAO extends DocumentDAO {

        /**
         * @brief Constructor
         *
         * @param connectionSource Database connection source
         * @throws SQLException If there's an issue establishing the connection
         */
        public ThrowingDocumentDAO(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource);
        }

        /**
         * @brief Overridden create method that always throws SQLException
         *
         * @param document The document to create
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public Document create(Document document) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden getById method that always throws SQLException
         *
         * @param id The document ID to retrieve
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public Optional<Document> getById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden getAll method that always throws SQLException
         *
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public List<Document> getAll() throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden getByCaseId method that always throws SQLException
         *
         * @param caseId The case ID to search for
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public List<Document> getByCaseId(Long caseId) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden getByType method that always throws SQLException
         *
         * @param type The document type to search for
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public List<Document> getByType(DocumentType type) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden searchByTitle method that always throws SQLException
         *
         * @param title The title to search for
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public List<Document> searchByTitle(String title) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden update method that always throws SQLException
         *
         * @param document The document to update
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public int update(Document document) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden delete method that always throws SQLException
         *
         * @param document The document to delete
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public int delete(Document document) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        /**
         * @brief Overridden deleteById method that always throws SQLException
         *
         * @param id The document ID to delete
         * @return Never returns due to exception
         * @throws SQLException Always thrown for testing
         */
        @Override
        public int deleteById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }
    }
}