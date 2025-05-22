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
     * @brief Shared database connection source
     * @details Singleton instance that provides database connectivity to DAOs
     */
    private static ConnectionSource connectionSource = null;

    /**
     * @brief JDBC URL for the SQLite database
     */
    private static String DATABASE_URL = null;

    /**
     * @brief Initialize the database connection and create tables if they don't exist
     * @details Creates the database file if it doesn't exist, establishes a connection,
     * and ensures all required tables are available in the schema
     * @throws RuntimeException if database initialization fails
     */
    public static void initializeDatabase() throws Exception {
        // Set ORMLite logging level to WARNING to suppress INFO messages
        Logger.getLogger("com.j256.ormlite").setLevel(Level.WARNING);
    
        // Get the absolute path of the database file
        File dbFile = new File("legalcase.db");
        DATABASE_URL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    
        // Check if database file exists, if not, create it
        if (!dbFile.exists()) { if (!dbFile.createNewFile()) { throw new RuntimeException("Could not create database file");
            }
        }
    
        // Create a connection source to our database
        connectionSource = new JdbcConnectionSource(DATABASE_URL);
    
        // Create tables if they don't already exist
        createTables();
    
        LOGGER.info("Database initialized successfully");
    }
    

    /**
     * @brief Create database tables for all model classes
     * @details Uses ORM Lite's TableUtils to create tables for all entity classes
     * if they don't already exist in the database
     * @throws SQLException if table creation fails
     */
    private static void createTables() throws SQLException {
        if (connectionSource == null) { throw new IllegalStateException("Connection source is null"); }
        
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Client.class);
        TableUtils.createTableIfNotExists(connectionSource, Case.class);
        TableUtils.createTableIfNotExists(connectionSource, Hearing.class);
        TableUtils.createTableIfNotExists(connectionSource, Document.class);
        TableUtils.createTableIfNotExists(connectionSource, CaseClient.class);
    }

    /**
     * @brief Get the database connection source
     * @details Returns the existing connection source or initializes a new one if needed
     * @return ConnectionSource object for database access
     */
    public static ConnectionSource getConnectionSource() {
        if (connectionSource == null) { throw new IllegalStateException("Database not initialized. Call initializeDatabase() first.");}
        return connectionSource;
    }

}