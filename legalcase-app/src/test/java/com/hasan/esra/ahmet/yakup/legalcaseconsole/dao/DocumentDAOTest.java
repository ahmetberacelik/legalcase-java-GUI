package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
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
 * @brief Test class for DocumentDAO operations
 *
 * This class tests the CRUD operations and query methods of the DocumentDAO class
 * using a test database connection.
 */
public class DocumentDAOTest {

    private ConnectionSource connectionSource;
    private DocumentDAO documentDAO;
    private CaseDAO caseDAO;

    /**
     * @brief Set up the test environment before each test
     *
     * Creates test database tables and initializes DAO objects
     *
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Initialize test database
        TestDatabaseManager.createTables();
        connectionSource = TestDatabaseManager.getConnectionSource();
        documentDAO = new DocumentDAO(connectionSource);
        caseDAO = new CaseDAO(connectionSource);
    }

    /**
     * @brief Clean up the test environment after each test
     *
     * Closes the test database connection
     *
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Test document creation operation
     *
     * Verifies that a document is correctly created with all properties
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Create_Success_ReturnsDocumentWithId() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-001", "Test Case for Document", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Test Document", DocumentType.CONTRACT, caseEntity, "This is a test document content.");

        // Act
        Document createdDocument = documentDAO.create(document);

        // Assert
        assertNotNull("Created document should not be null", createdDocument);
        assertNotNull("Created document should have an ID", createdDocument.getId());
        assertEquals("Title should be correct", "Test Document", createdDocument.getTitle());
        assertEquals("Type should be correct", DocumentType.CONTRACT, createdDocument.getType());
        assertEquals("Case should be correct", caseEntity.getId(), createdDocument.getCse().getId());
        assertEquals("Content should be correct", "This is a test document content.", createdDocument.getContent());
        assertEquals("Content type should be text/plain", "text/plain", createdDocument.getContentType());
    }

    /**
     * @brief Test retrieving a document by ID
     *
     * Verifies that an existing document can be retrieved by its ID
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetById_ExistingDocument_ReturnsDocument() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-002", "Another Test Case", CaseType.CRIMINAL);
        caseDAO.create(caseEntity);

        Document document = new Document("Get By ID Test", DocumentType.EVIDENCE, caseEntity, "Content for get by ID test");
        documentDAO.create(document);

        // Act
        Optional<Document> retrievedDocOpt = documentDAO.getById(document.getId());

        // Assert
        assertTrue("Document should be found", retrievedDocOpt.isPresent());
        Document retrievedDoc = retrievedDocOpt.get();
        assertEquals("Document ID should match", document.getId(), retrievedDoc.getId());
        assertEquals("Title should match", "Get By ID Test", retrievedDoc.getTitle());
        assertEquals("Type should match", DocumentType.EVIDENCE, retrievedDoc.getType());
    }

    /**
     * @brief Test retrieving a non-existent document by ID
     *
     * Verifies that attempting to retrieve a non-existent document returns an empty Optional
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetById_NonExistingDocument_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<Document> retrievedDocOpt = documentDAO.getById(9999L);

        // Assert
        assertFalse("Should return empty Optional when document not found", retrievedDocOpt.isPresent());
    }

    /**
     * @brief Test retrieving all documents
     *
     * Verifies that all documents can be retrieved from the database
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetAll_WithDocuments_ReturnsList() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-003", "Test Case for Multiple Docs", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        Document doc1 = new Document("Document One", DocumentType.CONTRACT, caseEntity, "Content for document one");
        Document doc2 = new Document("Document Two", DocumentType.PETITION, caseEntity, "Content for document two");
        documentDAO.create(doc1);
        documentDAO.create(doc2);

        // Act
        List<Document> allDocuments = documentDAO.getAll();

        // Assert
        assertNotNull("Document list should not be null", allDocuments);
        assertTrue("Document list should not be empty", allDocuments.size() >= 2);

        // Both created documents should be in the list
        boolean doc1Found = false;
        boolean doc2Found = false;
        for (Document doc : allDocuments) {
            if (doc.getTitle().equals("Document One")) doc1Found = true;
            if (doc.getTitle().equals("Document Two")) doc2Found = true;
        }
        assertTrue("Document One should be in the list", doc1Found);
        assertTrue("Document Two should be in the list", doc2Found);
    }

    /**
     * @brief Test retrieving documents by case ID
     *
     * Verifies that documents can be filtered by their associated case ID
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByCaseId_WithMatchingDocuments_ReturnsList() throws SQLException {
        // Arrange
        Case case1 = new Case("DOC2023-004", "Case One", CaseType.CORPORATE);
        Case case2 = new Case("DOC2023-005", "Case Two", CaseType.OTHER);
        caseDAO.create(case1);
        caseDAO.create(case2);

        Document doc1 = new Document("Case One Doc", DocumentType.CONTRACT, case1, "Content for case one");
        Document doc2 = new Document("Another Case One Doc", DocumentType.EVIDENCE, case1, "More content for case one");
        Document doc3 = new Document("Case Two Doc", DocumentType.COURT_ORDER, case2, "Content for case two");
        documentDAO.create(doc1);
        documentDAO.create(doc2);
        documentDAO.create(doc3);

        // Act
        List<Document> case1Documents = documentDAO.getByCaseId(case1.getId());

        // Assert
        assertNotNull("Case One document list should not be null", case1Documents);
        assertEquals("Case One should have 2 documents", 2, case1Documents.size());

        // Only Case One documents should be in the list
        boolean doc1Found = false;
        boolean doc2Found = false;
        boolean doc3Found = false;
        for (Document doc : case1Documents) {
            if (doc.getTitle().equals("Case One Doc")) doc1Found = true;
            if (doc.getTitle().equals("Another Case One Doc")) doc2Found = true;
            if (doc.getTitle().equals("Case Two Doc")) doc3Found = true;
        }
        assertTrue("Case One Doc should be in the list", doc1Found);
        assertTrue("Another Case One Doc should be in the list", doc2Found);
        assertFalse("Case Two Doc should not be in the list", doc3Found);
    }

    /**
     * @brief Test retrieving documents by type
     *
     * Verifies that documents can be filtered by their document type
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_GetByType_WithMatchingDocuments_ReturnsList() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-006", "Test Case for Doc Types", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document contract1 = new Document("Contract One", DocumentType.CONTRACT, caseEntity, "Content for contract one");
        Document contract2 = new Document("Contract Two", DocumentType.CONTRACT, caseEntity, "Content for contract two");
        Document evidence = new Document("Evidence Doc", DocumentType.EVIDENCE, caseEntity, "Content for evidence");
        documentDAO.create(contract1);
        documentDAO.create(contract2);
        documentDAO.create(evidence);

        // Act
        List<Document> contractDocuments = documentDAO.getByType(DocumentType.CONTRACT);

        // Assert
        assertNotNull("Contract document list should not be null", contractDocuments);
        assertTrue("Contract document list should have at least 2 documents", contractDocuments.size() >= 2);

        // Only CONTRACT documents should be in the list
        boolean contract1Found = false;
        boolean contract2Found = false;
        boolean evidenceFound = false;
        for (Document doc : contractDocuments) {
            if (doc.getTitle().equals("Contract One")) contract1Found = true;
            if (doc.getTitle().equals("Contract Two")) contract2Found = true;
            if (doc.getTitle().equals("Evidence Doc")) evidenceFound = true;
        }
        assertTrue("Contract One should be in the list", contract1Found);
        assertTrue("Contract Two should be in the list", contract2Found);
        assertFalse("Evidence Doc should not be in the list", evidenceFound);
    }

    /**
     * @brief Test searching documents by title
     *
     * Verifies that documents can be found by searching for text in their titles
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_SearchByTitle_WithMatchingDocuments_ReturnsList() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-007", "Test Case for Search", CaseType.CRIMINAL);
        caseDAO.create(caseEntity);

        Document doc1 = new Document("Search Test Document", DocumentType.CONTRACT, caseEntity, "Content for search test");
        Document doc2 = new Document("Another Document with Search", DocumentType.PETITION, caseEntity, "More content with search");
        Document doc3 = new Document("No Match Document", DocumentType.OTHER, caseEntity, "Content with no match");
        documentDAO.create(doc1);
        documentDAO.create(doc2);
        documentDAO.create(doc3);

        // Act
        List<Document> searchResults = documentDAO.searchByTitle("Search");

        // Assert
        assertNotNull("Search results should not be null", searchResults);
        assertTrue("Search should find at least 1 document", searchResults.size() >= 1);

        // Only documents with "Search" in title should be in the list
        boolean search1Found = false;
        boolean search2Found = false;
        boolean noMatchFound = false;
        for (Document doc : searchResults) {
            if (doc.getTitle().equals("Search Test Document")) search1Found = true;
            if (doc.getTitle().equals("Another Document with Search")) search2Found = true;
            if (doc.getTitle().equals("No Match Document")) noMatchFound = true;
        }
        assertTrue("At least one search document should be found", search1Found || search2Found);
        assertFalse("No Match Document should not be in the list", noMatchFound);
    }

    /**
     * @brief Test updating an existing document
     *
     * Verifies that a document can be successfully updated in the database
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Update_ExistingDocument_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-008", "Test Case for Update", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        Document document = new Document("Original Title", DocumentType.CONTRACT, caseEntity, "Original content");
        documentDAO.create(document);

        // Update document
        document.setTitle("Updated Title");
        document.setType(DocumentType.EVIDENCE);
        document.setContent("Updated content");
        document.setContentType("text/markdown");

        // Act
        int updatedRows = documentDAO.update(document);

        // Assert
        assertEquals("Should update 1 row", 1, updatedRows);

        // Verify updated document from database
        Optional<Document> updatedDocOpt = documentDAO.getById(document.getId());
        assertTrue("Updated document should be found", updatedDocOpt.isPresent());
        Document updatedDoc = updatedDocOpt.get();
        assertEquals("Title should be updated", "Updated Title", updatedDoc.getTitle());
        assertEquals("Type should be updated", DocumentType.EVIDENCE, updatedDoc.getType());
        assertEquals("Content should be updated", "Updated content", updatedDoc.getContent());
        assertEquals("Content type should be updated", "text/markdown", updatedDoc.getContentType());
    }

    /**
     * @brief Test deleting an existing document
     *
     * Verifies that a document can be successfully deleted from the database
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_Delete_ExistingDocument_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-009", "Test Case for Delete", CaseType.CORPORATE);
        caseDAO.create(caseEntity);

        Document document = new Document("Delete Test Doc", DocumentType.CONTRACT, caseEntity, "Content to be deleted");
        documentDAO.create(document);

        // Verify document exists before deletion
        Optional<Document> beforeDeleteOpt = documentDAO.getById(document.getId());
        assertTrue("Document should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = documentDAO.delete(document);

        // Assert
        assertEquals("Should delete 1 row", 1, deletedRows);

        // Verify document is deleted
        Optional<Document> afterDeleteOpt = documentDAO.getById(document.getId());
        assertFalse("Document should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Test deleting a document by ID
     *
     * Verifies that a document can be successfully deleted using only its ID
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_DeleteById_ExistingDocument_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("DOC2023-010", "Test Case for DeleteById", CaseType.OTHER);
        caseDAO.create(caseEntity);

        Document document = new Document("DeleteById Test Doc", DocumentType.PETITION, caseEntity, "Content to be deleted by ID");
        documentDAO.create(document);

        // Verify document exists before deletion
        Optional<Document> beforeDeleteOpt = documentDAO.getById(document.getId());
        assertTrue("Document should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = documentDAO.deleteById(document.getId());

        // Assert
        assertEquals("Should delete 1 row", 1, deletedRows);

        // Verify document is deleted
        Optional<Document> afterDeleteOpt = documentDAO.getById(document.getId());
        assertFalse("Document should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Test deleting a non-existent document by ID
     *
     * Verifies that attempting to delete a non-existent document returns zero rows affected
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void Test_DeleteById_NonExistingDocument_ReturnsZero() throws SQLException {
        // Act
        int deletedRows = documentDAO.deleteById(9999L);

        // Assert
        assertEquals("Should return 0 when no rows are deleted", 0, deletedRows);
    }
}