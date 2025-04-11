/**
 * @file LegalcaseApp.java
 * @brief Main application class for the Legal Case Tracker system
 *
 * This class serves as the entry point to the application and coordinates
 * initialization of all components including database connection, DAOs,
 * services, and the user interface.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.config.DatabaseManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @file LegalcaseApp.java
 * @brief Main application class for the Legal Case Tracker system
 *
 * This class serves as the entry point to the application and coordinates
 * initialization of all components including database connection, DAOs,
 * services, and the user interface.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-02
 */
public class LegalcaseApp {
    /** Logger for this class */
    private static final Logger LOGGER = Logger.getLogger(LegalcaseApp.class.getName());

    /**
     * @brief Main method - application entry point
     *
     * Initializes the database connection, creates data access objects,
     * service objects, and starts the menu manager for user interaction.
     * Handles exceptions and ensures proper resource cleanup.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Start message
            ConsoleHelper.displayMenuHeader("LEGAL CASE TRACKER APPLICATION");
            ConsoleHelper.displayInfo("Starting application...");

            // Initialize database
            DatabaseManager.initializeDatabase();
            ConnectionSource connectionSource = DatabaseManager.getConnectionSource();

            // Create DAO objects
            UserDAO userDAO = new UserDAO(connectionSource);
            ClientDAO clientDAO = new ClientDAO(connectionSource);
            CaseDAO caseDAO = new CaseDAO(connectionSource);
            HearingDAO hearingDAO = new HearingDAO(connectionSource);
            DocumentDAO documentDAO = new DocumentDAO(connectionSource);

            // Create service objects
            AuthService authService = new AuthService(userDAO);
            ClientService clientService = new ClientService(clientDAO);
            CaseService caseService = new CaseService(caseDAO, clientDAO);
            HearingService hearingService = new HearingService(hearingDAO, caseDAO);
            DocumentService documentService = new DocumentService(documentDAO, caseDAO);

            // Create menu manager and start application
            MenuManager menuManager = new MenuManager(authService, clientService, caseService, hearingService, documentService);
            menuManager.start();

            // Close connection on application exit
            DatabaseManager.closeConnection();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error: ", e);
            ConsoleHelper.displayError("An error occurred during database connection: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: ", e);
            ConsoleHelper.displayError("An unexpected error occurred during application: " + e.getMessage());
        } finally {
            // Close scanner in all cases
            try {
                ConsoleHelper.closeScanner();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing scanner: ", e);
            }
        }
    }
}