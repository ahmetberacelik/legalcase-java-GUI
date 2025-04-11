/**
 * @file DocumentDAO.java
 * @brief Data Access Object for Document entities in the Legal Case Tracker system
 *
 * This file contains the DocumentDAO class which handles database operations for Document entities,
 * including CRUD operations and queries for retrieving and managing legal documents.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief Data Access Object for Document entity
 * @details This class provides methods to perform CRUD operations and additional
 * queries on Document entities. It encapsulates database access for legal documents.
 */
public class DocumentDAO {
    /**
     * @brief Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());

    /**
     * @brief ORM Lite DAO for Document entity
     * @details Handles the underlying database operations
     */
    private final Dao<Document, Long> documentDao;

    /**
     * @brief Constructor
     * @param connectionSource Database connection source
     * @throws SQLException if DAO creation fails
     */
    public DocumentDAO(ConnectionSource connectionSource) throws SQLException {
        this.documentDao = DaoManager.createDao(connectionSource, Document.class);
    }

    /**
     * @brief Create a new document
     * @details Persists a new document to the database after setting creation timestamps
     * @param document Document to create
     * @return Created document with generated ID
     * @throws SQLException if creation fails
     */
    public Document create(Document document) throws SQLException {
        document.prePersist();
        documentDao.create(document);
        return document;
    }

    /**
     * @brief Get document by ID
     * @details Retrieves a document by its primary key
     * @param id Document ID
     * @return Optional containing document if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<Document> getById(Long id) throws SQLException {
        Document document = documentDao.queryForId(id);
        return Optional.ofNullable(document);
    }

    /**
     * @brief Get all documents
     * @details Retrieves all document records from the database
     * @return List of all documents
     * @throws SQLException if query fails
     */
    public List<Document> getAll() throws SQLException {
        return documentDao.queryForAll();
    }

    /**
     * @brief Get documents by case ID
     * @details Retrieves all documents associated with a specific case
     * @param caseId Case ID to filter by
     * @return List of documents associated with the specified case
     * @throws SQLException if query fails
     */
    public List<Document> getByCaseId(Long caseId) throws SQLException {
        QueryBuilder<Document, Long> queryBuilder = documentDao.queryBuilder();
        queryBuilder.where().eq("case_id", caseId);
        return queryBuilder.query();
    }

    /**
     * @brief Get documents by type
     * @details Retrieves all documents of a specific document type
     * @param type DocumentType to filter by
     * @return List of documents of the specified type
     * @throws SQLException if query fails
     */
    public List<Document> getByType(DocumentType type) throws SQLException {
        QueryBuilder<Document, Long> queryBuilder = documentDao.queryBuilder();
        queryBuilder.where().eq("type", type);
        return queryBuilder.query();
    }

    /**
     * @brief Search documents by title
     * @details Performs a partial match search on document titles
     * @param title Title to search for (partial match)
     * @return List of documents with titles containing the search term
     * @throws SQLException if query fails
     */
    public List<Document> searchByTitle(String title) throws SQLException {
        QueryBuilder<Document, Long> queryBuilder = documentDao.queryBuilder();
        queryBuilder.where().like("title", "%" + title + "%");
        return queryBuilder.query();
    }

    /**
     * @brief Update document
     * @details Updates an existing document in the database after setting update timestamp
     * @param document Document to update
     * @return Number of rows updated (should be 1 for success)
     * @throws SQLException if update fails
     */
    public int update(Document document) throws SQLException {
        document.preUpdate();
        return documentDao.update(document);
    }

    /**
     * @brief Delete document
     * @details Removes a document from the database
     * @param document Document to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int delete(Document document) throws SQLException {
        return documentDao.delete(document);
    }

    /**
     * @brief Delete document by ID
     * @details Removes a document from the database using its primary key
     * @param id ID of document to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int deleteById(Long id) throws SQLException {
        return documentDao.deleteById(id);
    }
}