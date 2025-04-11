/**
 * @file DatabaseManager.java
 * @brief Database management class for the Legal Case Tracker system
 *
 * This file contains the DatabaseManager class which is responsible for
 * database connection and initialization. It handles the SQLite database
 * connection lifecycle and ensures that all required tables are created.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.config;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.*;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief Database manager class responsible for database connection and initialization
 * @details This utility class handles the SQLite database connection lifecycle and
 * ensures that all required tables are created. It follows the singleton pattern
 * for database connection management.
 */
public class DatabaseManager {
    /**
     * @brief Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    /**
     * @brief JDBC URL for the SQLite database
     */
    private static final String DATABASE_URL = "jdbc:sqlite:legalcase.db";

    /**
     * @brief Shared database connection source
     * @details Singleton instance that provides database connectivity to DAOs
     */
    private static ConnectionSource connectionSource = null;

    /**
     * @brief Private constructor to prevent instantiation
     * @details Enforces the singleton pattern for this utility class
     */
    private DatabaseManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * @brief Initialize the database connection and create tables if they don't exist
     * @details Creates the database file if it doesn't exist, establishes a connection,
     * and ensures all required tables are available in the schema
     * @throws RuntimeException if database initialization fails
     */
    public static void initializeDatabase() {
        try {
            // Check if database file exists, if not, create it
            File dbFile = new File("legalcase.db");
            if (!dbFile.exists()) {
                LOGGER.info("Database file not found. Creating new database.");
            }

            // Create a connection source to our database
            connectionSource = new JdbcConnectionSource(DATABASE_URL);

            // Create tables if they don't already exist
            createTables();

            LOGGER.info("Database initialized successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * @brief Create database tables for all model classes
     * @details Uses ORM Lite's TableUtils to create tables for all entity classes
     * if they don't already exist in the database
     * @throws SQLException if table creation fails
     */
    private static void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Client.class);
        TableUtils.createTableIfNotExists(connectionSource, Case.class);
        TableUtils.createTableIfNotExists(connectionSource, Hearing.class);
        TableUtils.createTableIfNotExists(connectionSource, Document.class);
        TableUtils.createTableIfNotExists(connectionSource, CaseClient.class);

        LOGGER.info("Database tables created successfully.");
    }

    /**
     * @brief Get the database connection source
     * @details Returns the existing connection source or initializes a new one if needed
     * @return ConnectionSource object for database access
     */
    public static ConnectionSource getConnectionSource() {
        if (connectionSource == null) {
            initializeDatabase();
        }
        return connectionSource;
    }

    /**
     * @brief Close the database connection
     * @details Safely releases database resources and logs the outcome
     */
    public static void closeConnection() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
                LOGGER.info("Database connection closed successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
}