/**
 * @file CaseDAO.java
 * @brief Data Access Object for Case entities in the Legal Case Tracker system
 *
 * This file contains the CaseDAO class which handles database operations for Case entities,
 * including CRUD operations and relationship management with Client entities through
 * the many-to-many CaseClient junction table.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.CaseClient;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief Data Access Object for Case entity
 * @details This class provides methods to perform CRUD operations and additional
 * queries on Case entities. It also manages relationships between cases and clients.
 */
public class CaseDAO {
    private static final Logger LOGGER = Logger.getLogger(CaseDAO.class.getName());
    private final Dao<Case, Long> caseDao;
    /**
     * @brief Database connection source
     * @details Used to create additional DAOs for relationship management
     */
    private final ConnectionSource connectionSource;

    /**
     * @brief Constructor
     * @param connectionSource Database connection source
     * @throws SQLException if DAO creation fails
     */
    public CaseDAO(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;
        this.caseDao = DaoManager.createDao(connectionSource, Case.class);
    }

    /**
     * @brief Create a new case
     * @param caseEntity Case to create
     * @return Created case with generated ID
     * @throws SQLException if creation fails
     */
    public Case create(Case caseEntity) throws SQLException {
        caseEntity.prePersist();
        caseDao.create(caseEntity);
        return caseEntity;
    }

    /**
     * @brief Get case by ID
     * @param id Case ID
     * @return Optional containing case if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<Case> getById(Long id) throws SQLException {
        Case caseEntity = caseDao.queryForId(id);
        return Optional.ofNullable(caseEntity);
    }

    /**
     * @brief Get case by case number
     * @param caseNumber Case number to search for
     * @return Optional containing case if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<Case> getByCaseNumber(String caseNumber) throws SQLException {
        QueryBuilder<Case, Long> queryBuilder = caseDao.queryBuilder();
        queryBuilder.where().eq("case_number", caseNumber);
        List<Case> cases = queryBuilder.query();

        if (cases.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cases.get(0));
    }

    /**
     * @brief Get all cases
     * @return List of all cases in the database
     * @throws SQLException if query fails
     */
    public List<Case> getAll() throws SQLException {
        return caseDao.queryForAll();
    }

    /**
     * @brief Get cases by status
     * @param status Status to filter by
     * @return List of cases with specified status
     * @throws SQLException if query fails
     */
    public List<Case> getByStatus(CaseStatus status) throws SQLException {
        QueryBuilder<Case, Long> queryBuilder = caseDao.queryBuilder();
        queryBuilder.where().eq("status", status);
        return queryBuilder.query();
    }

    /**
     * @brief Get cases by type
     * @param type Type to filter by
     * @return List of cases with specified type
     * @throws SQLException if query fails
     */
    public List<Case> getByType(CaseType type) throws SQLException {
        QueryBuilder<Case, Long> queryBuilder = caseDao.queryBuilder();
        queryBuilder.where().eq("type", type);
        return queryBuilder.query();
    }

    /**
     * @brief Search cases by title
     * @param title Title to search for (partial match)
     * @return List of cases with titles containing the search term
     * @throws SQLException if query fails
     */
    public List<Case> searchByTitle(String title) throws SQLException {
        QueryBuilder<Case, Long> queryBuilder = caseDao.queryBuilder();
        queryBuilder.where().like("title", "%" + title + "%");
        return queryBuilder.query();
    }

    /**
     * @brief Update case
     * @param caseEntity Case to update
     * @return Number of rows updated (should be 1 for success)
     * @throws SQLException if update fails
     */
    public int update(Case caseEntity) throws SQLException {
        caseEntity.preUpdate();
        return caseDao.update(caseEntity);
    }

    /**
     * @brief Delete case
     * @param caseEntity Case to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int delete(Case caseEntity) throws SQLException {
        return caseDao.delete(caseEntity);
    }

    /**
     * @brief Delete case by ID
     * @param id ID of case to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int deleteById(Long id) throws SQLException {
        return caseDao.deleteById(id);
    }

    /**
     * @brief Associate multiple clients with a case
     * @details Note: This implementation may need to be adjusted based on your actual implementation
     * of the many-to-many relationship between Case and Client
     * @param caseEntity Case to associate clients with
     * @param clients List of clients to associate
     * @throws SQLException if association fails
     */
    public void addClientsToCase(Case caseEntity, List<Client> clients) throws SQLException {
        // Implementation depends on how you handle the many-to-many relationship
        for (Client client : clients) {
            caseEntity.addClient(client);
        }

        // Update the case to persist the associations
        update(caseEntity);
    }

    /**
     * @brief Add a client to a case
     * @details Associates a client with a case by updating both the transient list
     * in the Case object and creating a record in the junction table
     * @param caseEntity Case to associate
     * @param client Client to associate
     * @throws SQLException if association fails
     */
    public void addClientToCase(Case caseEntity, Client client) throws SQLException {
        // Add to transient list
        caseEntity.addClient(client);

        // Add record to CaseClient junction table
        CaseClient caseClient = new CaseClient(caseEntity, client);
        Dao<CaseClient, Long> caseClientDao = DaoManager.createDao(connectionSource, CaseClient.class);
        caseClientDao.create(caseClient);

        // Update the case
        update(caseEntity);
    }

    /**
     * @brief Remove a client from a case
     * @details Disassociates a client from a case by updating both the transient list
     * in the Case object and removing the record from the junction table
     * @param caseEntity Case to disassociate
     * @param client Client to disassociate
     * @throws SQLException if disassociation fails
     */
    public void removeClientFromCase(Case caseEntity, Client client) throws SQLException {
        // Remove from transient list
        caseEntity.removeClient(client);

        // Delete record from CaseClient junction table
        Dao<CaseClient, Long> caseClientDao = DaoManager.createDao(connectionSource, CaseClient.class);
        DeleteBuilder<CaseClient, Long> deleteBuilder = caseClientDao.deleteBuilder();
        deleteBuilder.where()
                .eq("case_id", caseEntity.getId())
                .and()
                .eq("client_id", client.getId());
        deleteBuilder.delete();

        // Update the case
        update(caseEntity);
    }

    /**
     * @brief Get all clients associated with a case
     * @details Retrieves all clients linked to the specified case through the junction table
     * @param caseId Case ID
     * @return List of clients associated with the case
     * @throws SQLException if query fails
     */
    public List<Client> getClientsForCase(Long caseId) throws SQLException {
        // Create CaseClient DAO
        Dao<CaseClient, Long> caseClientDao = DaoManager.createDao(connectionSource, CaseClient.class);

        // Create query for case_id
        QueryBuilder<CaseClient, Long> queryBuilder = caseClientDao.queryBuilder();
        queryBuilder.where().eq("case_id", caseId);

        // Get related records
        List<CaseClient> caseClients = queryBuilder.query();
        List<Client> clients = new ArrayList<>();

        // Extract client information for each relationship
        for (CaseClient cc : caseClients) {
            clients.add(cc.getClient());
        }

        return clients;
    }

    /**
     * @brief Get all cases associated with a client
     * @details Retrieves all cases linked to the specified client through the junction table
     * @param clientId Client ID
     * @return List of cases associated with the client
     * @throws SQLException if query fails
     */
    public List<Case> getCasesForClient(Long clientId) throws SQLException {
        // Create CaseClient DAO
        Dao<CaseClient, Long> caseClientDao = DaoManager.createDao(connectionSource, CaseClient.class);

        // Create query for client_id
        QueryBuilder<CaseClient, Long> queryBuilder = caseClientDao.queryBuilder();
        queryBuilder.where().eq("client_id", clientId);

        // Get related records
        List<CaseClient> caseClients = queryBuilder.query();
        List<Case> cases = new ArrayList<>();

        // Extract case information for each relationship
        for (CaseClient cc : caseClients) {
            cases.add(cc.getCse());
        }

        return cases;
    }
}