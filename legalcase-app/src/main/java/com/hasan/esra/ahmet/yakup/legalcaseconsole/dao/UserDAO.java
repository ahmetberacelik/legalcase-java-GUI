/**
 * @file UserDAO.java
 * @brief Data Access Object for User entities in the Legal Case Tracker system
 *
 * This file contains the UserDAO class which handles database operations for User entities,
 * including CRUD operations and authentication-related queries for system users.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief Data Access Object for User entity
 * @details This class provides methods to perform CRUD operations and additional
 * queries on User entities. It encapsulates database access for system users
 * and includes authentication-related queries.
 */
public class UserDAO {
    /**
     * @brief Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * @brief ORM Lite DAO for User entity
     * @details Handles the underlying database operations
     */
    private final Dao<User, Long> userDao;

    /**
     * @brief Constructor
     * @param connectionSource Database connection source
     * @throws SQLException if DAO creation fails
     */
    public UserDAO(ConnectionSource connectionSource) throws SQLException {
        this.userDao = DaoManager.createDao(connectionSource, User.class);
    }

    /**
     * @brief Create a new user
     * @details Persists a new user to the database
     * @param user User to create
     * @return Created user with generated ID
     * @throws SQLException if creation fails
     */
    public User create(User user) throws SQLException {
        userDao.create(user);
        return user;
    }

    /**
     * @brief Get user by ID
     * @details Retrieves a user by its primary key
     * @param id User ID
     * @return Optional containing user if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<User> getById(Long id) throws SQLException {
        User user = userDao.queryForId(id);
        return Optional.ofNullable(user);
    }

    /**
     * @brief Get user by username
     * @details Retrieves a user by their unique username (used for authentication)
     * @param username Username to search for
     * @return Optional containing user if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<User> getByUsername(String username) throws SQLException {
        QueryBuilder<User, Long> queryBuilder = userDao.queryBuilder();
        queryBuilder.where().eq("username", username);
        List<User> users = queryBuilder.query();

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    /**
     * @brief Get user by email
     * @details Retrieves a user by their unique email address
     * @param email Email to search for
     * @return Optional containing user if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<User> getByEmail(String email) throws SQLException {
        QueryBuilder<User, Long> queryBuilder = userDao.queryBuilder();
        queryBuilder.where().eq("email", email);
        List<User> users = queryBuilder.query();

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    /**
     * @brief Get all users
     * @details Retrieves all user records from the database
     * @return List of all users
     * @throws SQLException if query fails
     */
    public List<User> getAll() throws SQLException {
        return userDao.queryForAll();
    }

    /**
     * @brief Get users by role
     * @details Retrieves all users with a specific role (e.g., ADMIN, LAWYER)
     * @param role UserRole to filter by
     * @return List of users with the specified role
     * @throws SQLException if query fails
     */
    public List<User> getByRole(UserRole role) throws SQLException {
        QueryBuilder<User, Long> queryBuilder = userDao.queryBuilder();
        queryBuilder.where().eq("role", role);
        return queryBuilder.query();
    }

    /**
     * @brief Search users by name or surname
     * @details Performs a partial match search on both name and surname fields
     * @param searchTerm Search term to match against name or surname
     * @return List of users matching the search criteria
     * @throws SQLException if query fails
     */
    public List<User> searchByName(String searchTerm) throws SQLException {
        QueryBuilder<User, Long> queryBuilder = userDao.queryBuilder();
        queryBuilder.where()
                .like("name", "%" + searchTerm + "%")
                .or()
                .like("surname", "%" + searchTerm + "%");
        return queryBuilder.query();
    }

    /**
     * @brief Update user
     * @details Updates an existing user in the database after setting update timestamp
     * @param user User to update
     * @return Number of rows updated (should be 1 for success)
     * @throws SQLException if update fails
     */
    public int update(User user) throws SQLException {
        user.preUpdate();
        return userDao.update(user);
    }

    /**
     * @brief Delete user
     * @details Removes a user from the database
     * @param user User to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int delete(User user) throws SQLException {
        return userDao.delete(user);
    }

    /**
     * @brief Delete user by ID
     * @details Removes a user from the database using its primary key
     * @param id ID of user to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int deleteById(Long id) throws SQLException {
        return userDao.deleteById(id);
    }
}