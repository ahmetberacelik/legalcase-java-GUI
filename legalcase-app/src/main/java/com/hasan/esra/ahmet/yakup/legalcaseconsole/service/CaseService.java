/**
 * @file CaseService.java
 * @brief Service class for case-related operations in the Legal Case Tracker system
 *
 * This file contains the service class that handles business logic for legal cases,
 * including creating, retrieving, updating, and deleting cases, as well as managing
 * relationships between cases and clients.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for case related operations
 */
public class CaseService {
    private static final Logger LOGGER = Logger.getLogger(CaseService.class.getName());
    private final CaseDAO caseDAO;
    private final ClientDAO clientDAO;

    /**
     * Constructor
     * @param caseDAO Case DAO
     * @param clientDAO Client DAO
     */
    public CaseService(CaseDAO caseDAO, ClientDAO clientDAO) {
        this.caseDAO = caseDAO;
        this.clientDAO = clientDAO;
    }

    /**
     * Create a new case
     * @param caseNumber Case number
     * @param title Case title
     * @param type Case type
     * @param description Case description
     * @return The created case
     * @throws IllegalArgumentException if case number is already in use
     */
    public Case createCase(String caseNumber, String title, CaseType type, String description) {
        try {
            // Check if case number is already in use
            if (caseNumber != null && !caseNumber.isEmpty()) {
                Optional<Case> existingCase = caseDAO.getByCaseNumber(caseNumber);
                if (existingCase.isPresent()) {
                    throw new IllegalArgumentException("This case number is already in use");
                }
            }

            // Create and save the case
            Case caseEntity = new Case(caseNumber, title, type);
            caseEntity.setDescription(description);
            caseEntity.setStatus(CaseStatus.NEW);

            return caseDAO.create(caseEntity);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating case", e);
            throw new RuntimeException("Could not create case", e);
        }
    }

    /**
     * Get case by ID
     * @param id Case ID
     * @return Optional containing case if found
     */
    public Optional<Case> getCaseById(Long id) {
        try {
            return caseDAO.getById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting case by ID", e);
            throw new RuntimeException("Could not retrieve case", e);
        }
    }

    /**
     * Get case by case number
     * @param caseNumber Case number
     * @return Optional containing case if found
     */
    public Optional<Case> getCaseByCaseNumber(String caseNumber) {
        try {
            return caseDAO.getByCaseNumber(caseNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting case by case number", e);
            throw new RuntimeException("Could not retrieve case", e);
        }
    }

    /**
     * Get all cases
     * @return List of all cases
     */
    public List<Case> getAllCases() {
        try {
            return caseDAO.getAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all cases", e);
            throw new RuntimeException("Could not retrieve cases", e);
        }
    }

    /**
     * Get cases by status
     * @param status Status to filter by
     * @return List of cases with specified status
     */
    public List<Case> getCasesByStatus(CaseStatus status) {
        try {
            return caseDAO.getByStatus(status);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cases by status", e);
            throw new RuntimeException("Could not retrieve cases", e);
        }
    }

    /**
     * Search cases by title
     * @param title Title to search for
     * @return List of matching cases
     */
    public List<Case> searchCasesByTitle(String title) {
        try {
            return caseDAO.searchByTitle(title);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching cases", e);
            throw new RuntimeException("Could not search cases", e);
        }
    }

    /**
     * Update case
     * @param id Case ID
     * @param caseNumber Case number
     * @param title Case title
     * @param type Case type
     * @param description Case description
     * @param status Case status
     * @return Updated case
     * @throws IllegalArgumentException if case not found or case number is already in use by another case
     */
    public Case updateCase(Long id, String caseNumber, String title, CaseType type, String description, CaseStatus status) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(id);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            Case caseEntity = caseOpt.get();

            // Check if case number is already used by another case
            if (caseNumber != null && !caseNumber.isEmpty() && !caseNumber.equals(caseEntity.getCaseNumber())) {
                Optional<Case> existingCase = caseDAO.getByCaseNumber(caseNumber);
                if (existingCase.isPresent() && !existingCase.get().getId().equals(id)) {
                    throw new IllegalArgumentException("This case number is already used by another case");
                }
            }

            // Update fields
            caseEntity.setCaseNumber(caseNumber);
            caseEntity.setTitle(title);
            caseEntity.setType(type);
            caseEntity.setDescription(description);
            caseEntity.setStatus(status);

            caseDAO.update(caseEntity);
            return caseEntity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating case", e);
            throw new RuntimeException("Could not update case", e);
        }
    }

    /**
     * Delete case
     * @param id Case ID
     * @throws IllegalArgumentException if case not found
     */
    public void deleteCase(Long id) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(id);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            caseDAO.deleteById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting case", e);
            throw new RuntimeException("Could not delete case", e);
        }
    }

    /**
     * Associate a client with a case
     * @param caseId Case ID
     * @param clientId Client ID
     * @throws IllegalArgumentException if case or client not found
     */
    public void addClientToCase(Long caseId, Long clientId) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            // Check if client exists
            Optional<Client> clientOpt = clientDAO.getById(clientId);
            if (!clientOpt.isPresent()) {
                throw new IllegalArgumentException("Client not found");
            }

            caseDAO.addClientToCase(caseOpt.get(), clientOpt.get());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding client to case", e);
            throw new RuntimeException("Could not add client to case", e);
        }
    }

    /**
     * Remove client association from a case
     * @param caseId Case ID
     * @param clientId Client ID
     * @throws IllegalArgumentException if case or client not found
     */
    public void removeClientFromCase(Long caseId, Long clientId) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            // Check if client exists
            Optional<Client> clientOpt = clientDAO.getById(clientId);
            if (!clientOpt.isPresent()) {
                throw new IllegalArgumentException("Client not found");
            }

            caseDAO.removeClientFromCase(caseOpt.get(), clientOpt.get());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing client from case", e);
            throw new RuntimeException("Could not remove client from case", e);
        }
    }

    /**
     * Get all clients associated with a case
     * @param caseId Case ID
     * @return List of clients associated with the case
     * @throws IllegalArgumentException if case not found
     */
    public List<Client> getClientsForCase(Long caseId) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            return caseDAO.getClientsForCase(caseId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting clients for case", e);
            throw new RuntimeException("Could not retrieve clients for case", e);
        }
    }

    /**
     * Get all cases associated with a client
     * @param clientId Client ID
     * @return List of cases associated with the client
     * @throws IllegalArgumentException if client not found
     */
    public List<Case> getCasesForClient(Long clientId) {
        try {
            // Check if client exists
            Optional<Client> clientOpt = clientDAO.getById(clientId);
            if (!clientOpt.isPresent()) {
                throw new IllegalArgumentException("Client not found");
            }

            return caseDAO.getCasesForClient(clientId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting cases for client", e);
            throw new RuntimeException("Could not retrieve cases for client", e);
        }
    }
}