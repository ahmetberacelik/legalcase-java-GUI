/**
 * @file ClientDAO.java
 * @brief Data Access Object for Client entities in the Legal Case Tracker system
 *
 * This file contains the ClientDAO class which handles database operations for Client entities,
 * including CRUD operations and queries for retrieving client information.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @brief Data Access Object for Client entity
 * @details This class provides methods to perform CRUD operations and additional
 * queries on Client entities. It encapsulates database access for Client objects.
 */
public class ClientDAO {
    /**
     * @brief Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    /**
     * @brief ORM Lite DAO for Client entity
     * @details Handles the underlying database operations
     */
    private final Dao<Client, Long> clientDao;

    /**
     * @brief Constructor
     * @param connectionSource Database connection source
     * @throws SQLException if DAO creation fails
     */
    public ClientDAO(ConnectionSource connectionSource) throws SQLException {
        this.clientDao = DaoManager.createDao(connectionSource, Client.class);
    }

    /**
     * @brief Create a new client
     * @details Persists a new client to the database after setting creation timestamps
     * @param client Client to create
     * @return Created client with generated ID
     * @throws SQLException if creation fails
     */
    public Client create(Client client) throws SQLException {
        client.prePersist();
        clientDao.create(client);
        return client;
    }

    /**
     * @brief Get client by ID
     * @details Retrieves a client by its primary key
     * @param id Client ID
     * @return Optional containing client if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<Client> getById(Long id) throws SQLException {
        Client client = clientDao.queryForId(id);
        return Optional.ofNullable(client);
    }

    /**
     * @brief Get client by email
     * @details Retrieves a client using their unique email address
     * @param email Email to search for
     * @return Optional containing client if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<Client> getByEmail(String email) throws SQLException {
        QueryBuilder<Client, Long> queryBuilder = clientDao.queryBuilder();
        queryBuilder.where().eq("email", email);
        List<Client> clients = queryBuilder.query();

        if (clients.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(clients.get(0));
    }

    /**
     * @brief Get all clients
     * @details Retrieves all client records from the database
     * @return List of all clients
     * @throws SQLException if query fails
     */
    public List<Client> getAll() throws SQLException {
        return clientDao.queryForAll();
    }

    /**
     * @brief Search clients by name or surname
     * @details Performs a partial match search on both name and surname fields
     * @param searchTerm Search term to match against name or surname
     * @return List of clients matching the search criteria
     * @throws SQLException if query fails
     */
    public List<Client> searchByName(String searchTerm) throws SQLException {
        QueryBuilder<Client, Long> queryBuilder = clientDao.queryBuilder();
        queryBuilder.where()
                .like("name", "%" + searchTerm + "%")
                .or()
                .like("surname", "%" + searchTerm + "%");
        return queryBuilder.query();
    }

    /**
     * @brief Update client
     * @details Updates an existing client in the database after setting update timestamp
     * @param client Client to update
     * @return Number of rows updated (should be 1 for success)
     * @throws SQLException if update fails
     */
    public int update(Client client) throws SQLException {
        client.preUpdate();
        return clientDao.update(client);
    }

    /**
     * @brief Delete client
     * @details Removes a client from the database
     * @param client Client to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int delete(Client client) throws SQLException {
        return clientDao.delete(client);
    }

    /**
     * @brief Delete client by ID
     * @details Removes a client from the database using its primary key
     * @param id ID of client to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int deleteById(Long id) throws SQLException {
        return clientDao.deleteById(id);
    }
}