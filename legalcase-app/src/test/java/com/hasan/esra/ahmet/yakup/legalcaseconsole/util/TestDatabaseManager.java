package com.hasan.esra.ahmet.yakup.legalcaseconsole.util;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.*;

import java.sql.SQLException;

/**
 * @brief Manages the test database connections and tables
 *
 * This class handles creation and management of in-memory SQLite database
 * for testing purposes. It provides methods to initialize connection,
 * create/clear tables, and close connections.
 */
public class TestDatabaseManager {
    /** In-memory database URL for tests */
    private static final String TEST_DB_URL = "jdbc:sqlite:memory:testdb";
    /** Shared connection source instance */
    private static ConnectionSource connectionSource;

    /**
     * @brief Gets or creates a connection to the test database
     *
     * @return A connection source to the test database
     * @throws SQLException If a database access error occurs
     */
    public static ConnectionSource getConnectionSource() throws SQLException {
        if (connectionSource == null) {
            connectionSource = new JdbcConnectionSource(TEST_DB_URL);
            createTables();
        }
        return connectionSource;
    }

    /**
     * @brief Creates all required database tables if they don't exist
     *
     * This method attempts to clear existing tables first, then creates
     * all necessary tables for the application models.
     *
     * @throws SQLException If a database access error occurs
     */
    public static void createTables() throws SQLException {
        ConnectionSource cs = getConnectionSource();
        try {
            clearTables();
        } catch (Exception e) {
            // Exception silently caught
        }

        TableUtils.createTableIfNotExists(cs, BaseEntity.class);
        TableUtils.createTableIfNotExists(cs, Client.class);
        TableUtils.createTableIfNotExists(cs, Case.class);
        TableUtils.createTableIfNotExists(cs, CaseClient.class);

        TableUtils.dropTable(cs, Hearing.class, true);
        TableUtils.createTableIfNotExists(cs, Hearing.class);

        TableUtils.createTableIfNotExists(cs, Document.class);
        TableUtils.createTableIfNotExists(cs, User.class);
    }

    /**
     * @brief Clears all data from database tables
     *
     * This method removes all rows from all tables while
     * preserving the table structure.
     *
     * @throws SQLException If a database access error occurs
     */
    public static void clearTables() throws SQLException {
        ConnectionSource cs = getConnectionSource();
        TableUtils.clearTable(cs, CaseClient.class);
        TableUtils.clearTable(cs, Document.class);
        TableUtils.clearTable(cs, Hearing.class);
        TableUtils.clearTable(cs, Case.class);
        TableUtils.clearTable(cs, Client.class);
        TableUtils.clearTable(cs, User.class);
    }

    /**
     * @brief Closes the database connection
     *
     * This method safely closes the connection to the database and
     * sets the connection source to null.
     *
     * @throws SQLException If a database access error occurs
     */
    public static void closeConnection() throws SQLException {
        if (connectionSource != null) {
            try {
                connectionSource.close();
                connectionSource = null;
            } catch (Exception e) {
                // Exception silently caught
            }
        }
    }
}