/**
 * @file DocumentService.java
 * @brief Document service class for the Legal Case Tracker system
 *
 * This file contains the service class that handles business logic for legal documents,
 * including creating, retrieving, updating, and deleting documents associated with cases.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.DocumentDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for document related operations
 */
public class DocumentService {
    private static final Logger LOGGER = Logger.getLogger(DocumentService.class.getName());
    private final DocumentDAO documentDAO;
    private final CaseDAO caseDAO;

    /**
     * Constructor
     * @param documentDAO Document DAO
     * @param caseDAO Case DAO
     */
    public DocumentService(DocumentDAO documentDAO, CaseDAO caseDAO) {
        this.documentDAO = documentDAO;
        this.caseDAO = caseDAO;
    }

    /**
     * Create a new document
     * @param caseId Case ID
     * @param title Document title
     * @param type Document type
     * @param content Document content
     * @return The created document
     * @throws IllegalArgumentException if case not found
     */
    public Document createDocument(Long caseId, String title, DocumentType type, String content) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            Case caseEntity = caseOpt.get();

            // Create document
            Document document = new Document(title, type, caseEntity, content);

            return documentDAO.create(document);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating document", e);
            throw new RuntimeException("Could not create document", e);
        }
    }

    /**
     * Get document by ID
     * @param id Document ID
     * @return Optional containing document if found
     */
    public Optional<Document> getDocumentById(Long id) {
        try {
            return documentDAO.getById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting document by ID", e);
            throw new RuntimeException("Could not retrieve document", e);
        }
    }

    /**
     * Get document content by ID
     * @param id Document ID
     * @return Document content
     * @throws IllegalArgumentException if document not found
     */
    public String getDocumentContent(Long id) {
        try {
            // Check if document exists
            Optional<Document> documentOpt = documentDAO.getById(id);
            if (!documentOpt.isPresent()) {
                throw new IllegalArgumentException("Document not found");
            }

            Document document = documentOpt.get();
            return document.getContent();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting document content", e);
            throw new RuntimeException("Could not retrieve document content", e);
        }
    }

    /**
     * Get all documents
     * @return List of all documents
     */
    public List<Document> getAllDocuments() {
        try {
            return documentDAO.getAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all documents", e);
            throw new RuntimeException("Could not retrieve documents", e);
        }
    }

    /**
     * Get documents by case ID
     * @param caseId Case ID
     * @return List of documents for the specified case
     */
    public List<Document> getDocumentsByCaseId(Long caseId) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            return documentDAO.getByCaseId(caseId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting documents by case ID", e);
            throw new RuntimeException("Could not retrieve documents", e);
        }
    }

    /**
     * Get documents by type
     * @param type Type to filter by
     * @return List of documents with specified type
     */
    public List<Document> getDocumentsByType(DocumentType type) {
        try {
            return documentDAO.getByType(type);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting documents by type", e);
            throw new RuntimeException("Could not retrieve documents", e);
        }
    }

    /**
     * Search documents by title
     * @param title Title to search for
     * @return List of matching documents
     */
    public List<Document> searchDocumentsByTitle(String title) {
        try {
            return documentDAO.searchByTitle(title);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching documents", e);
            throw new RuntimeException("Could not search documents", e);
        }
    }

    /**
     * Update document
     * @param id Document ID
     * @param title Document title
     * @param type Document type
     * @param content Document content
     * @return Updated document
     * @throws IllegalArgumentException if document not found
     */
    public Document updateDocument(Long id, String title, DocumentType type, String content) {
        try {
            // Check if document exists
            Optional<Document> documentOpt = documentDAO.getById(id);
            if (!documentOpt.isPresent()) {
                throw new IllegalArgumentException("Document not found");
            }

            Document document = documentOpt.get();

            // Update fields
            if (title != null && !title.isEmpty()) {
                document.setTitle(title);
            }

            if (type != null) {
                document.setType(type);
            }

            if (content != null) {
                document.setContent(content);
            }

            documentDAO.update(document);
            return document;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating document", e);
            throw new RuntimeException("Could not update document", e);
        }
    }

    /**
     * Delete document
     * @param id Document ID
     * @throws IllegalArgumentException if document not found
     */
    public void deleteDocument(Long id) {
        try {
            // Check if document exists
            Optional<Document> documentOpt = documentDAO.getById(id);
            if (!documentOpt.isPresent()) {
                throw new IllegalArgumentException("Document not found");
            }

            documentDAO.deleteById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting document", e);
            throw new RuntimeException("Could not delete document", e);
        }
    }
}