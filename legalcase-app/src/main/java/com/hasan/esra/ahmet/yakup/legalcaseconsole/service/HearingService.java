/**
 * @file HearingService.java
 * @brief Hearing service class for the Legal Case Tracker system
 *
 * This file contains the service class that handles business logic for court hearings,
 * including creating, retrieving, updating, and deleting hearings associated with cases.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.HearingDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for hearing related operations
 */
public class HearingService {
    private static final Logger LOGGER = Logger.getLogger(HearingService.class.getName());
    private final HearingDAO hearingDAO;
    private final CaseDAO caseDAO;

    /**
     * Constructor
     * @param hearingDAO Hearing DAO
     * @param caseDAO Case DAO
     */
    public HearingService(HearingDAO hearingDAO, CaseDAO caseDAO) {
        this.hearingDAO = hearingDAO;
        this.caseDAO = caseDAO;
    }

    /**
     * Create a new hearing
     * @param caseId Case ID
     * @param hearingDate Hearing date
     * @param judge Judge name
     * @param location Hearing location
     * @param notes Notes about the hearing
     * @return The created hearing
     * @throws IllegalArgumentException if case not found
     */
    public Hearing createHearing(Long caseId, LocalDateTime hearingDate, String judge, String location, String notes) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            Case caseEntity = caseOpt.get();

            // Create hearing
            Hearing hearing = new Hearing(caseEntity, hearingDate, judge, location);
            hearing.setNotes(notes);
            hearing.setStatus(HearingStatus.SCHEDULED);

            return hearingDAO.create(hearing);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating hearing", e);
            throw new RuntimeException("Could not create hearing", e);
        }
    }

    /**
     * Get hearing by ID
     * @param id Hearing ID
     * @return Optional containing hearing if found
     */
    public Optional<Hearing> getHearingById(Long id) {
        try {
            return hearingDAO.getById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting hearing by ID", e);
            throw new RuntimeException("Could not retrieve hearing", e);
        }
    }

    /**
     * Get all hearings
     * @return List of all hearings
     */
    public List<Hearing> getAllHearings() {
        try {
            return hearingDAO.getAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all hearings", e);
            throw new RuntimeException("Could not retrieve hearings", e);
        }
    }

    /**
     * Get hearings by case ID
     * @param caseId Case ID
     * @return List of hearings for the specified case
     */
    public List<Hearing> getHearingsByCaseId(Long caseId) {
        try {
            // Check if case exists
            Optional<Case> caseOpt = caseDAO.getById(caseId);
            if (!caseOpt.isPresent()) {
                throw new IllegalArgumentException("Case not found");
            }

            return hearingDAO.getByCaseId(caseId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting hearings by case ID", e);
            throw new RuntimeException("Could not retrieve hearings", e);
        }
    }

    /**
     * Get hearings by status
     * @param status Status to filter by
     * @return List of hearings with specified status
     */
    public List<Hearing> getHearingsByStatus(HearingStatus status) {
        try {
            return hearingDAO.getByStatus(status);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting hearings by status", e);
            throw new RuntimeException("Could not retrieve hearings", e);
        }
    }

    /**
     * Get hearings in date range
     * @param start Start date
     * @param end End date
     * @return List of hearings within the specified date range
     */
    public List<Hearing> getHearingsByDateRange(LocalDateTime start, LocalDateTime end) {
        try {
            if (start == null || end == null) {
                throw new IllegalArgumentException("Start and end dates are required");
            }

            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }

            return hearingDAO.getByDateRange(start, end);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting hearings by date range", e);
            throw new RuntimeException("Could not retrieve hearings", e);
        }
    }

    /**
     * Get upcoming hearings
     * @return List of upcoming hearings
     */
    public List<Hearing> getUpcomingHearings() {
        try {
            return hearingDAO.getUpcomingHearings();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting upcoming hearings", e);
            throw new RuntimeException("Could not retrieve hearings", e);
        }
    }

    /**
     * Update hearing
     * @param id Hearing ID
     * @param hearingDate Hearing date
     * @param judge Judge name
     * @param location Hearing location
     * @param notes Notes about the hearing
     * @param status Hearing status
     * @return Updated hearing
     * @throws IllegalArgumentException if hearing not found
     */
    public Hearing updateHearing(Long id, LocalDateTime hearingDate, String judge, String location, String notes, HearingStatus status) {
        try {
            // Check if hearing exists
            Optional<Hearing> hearingOpt = hearingDAO.getById(id);
            if (!hearingOpt.isPresent()) {
                throw new IllegalArgumentException("Hearing not found");
            }

            Hearing hearing = hearingOpt.get();

            // Update fields
            if (hearingDate != null) {
                hearing.setHearingDate(hearingDate);
            }

            if (judge != null && !judge.isEmpty()) {
                hearing.setJudge(judge);
            }

            if (location != null) {
                hearing.setLocation(location);
            }

            if (notes != null) {
                hearing.setNotes(notes);
            }

            if (status != null) {
                hearing.setStatus(status);
            }

            hearingDAO.update(hearing);
            return hearing;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating hearing", e);
            throw new RuntimeException("Could not update hearing", e);
        }
    }

    /**
     * Update hearing status
     * @param id Hearing ID
     * @param status New status
     * @return Updated hearing
     * @throws IllegalArgumentException if hearing not found
     */
    public Hearing updateHearingStatus(Long id, HearingStatus status) {
        try {
            // Check if hearing exists
            Optional<Hearing> hearingOpt = hearingDAO.getById(id);
            if (!hearingOpt.isPresent()) {
                throw new IllegalArgumentException("Hearing not found");
            }

            Hearing hearing = hearingOpt.get();
            hearing.setStatus(status);

            hearingDAO.update(hearing);
            return hearing;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating hearing status", e);
            throw new RuntimeException("Could not update hearing status", e);
        }
    }

    /**
     * Reschedule hearing
     * @param id Hearing ID
     * @param newDate New hearing date
     * @return Rescheduled hearing
     * @throws IllegalArgumentException if hearing not found
     */
    public Hearing rescheduleHearing(Long id, LocalDateTime newDate) {
        try {
            if (newDate == null) {
                throw new IllegalArgumentException("New hearing date is required");
            }

            // Check if hearing exists
            Optional<Hearing> hearingOpt = hearingDAO.getById(id);
            if (!hearingOpt.isPresent()) {
                throw new IllegalArgumentException("Hearing not found");
            }

            Hearing hearing = hearingOpt.get();

            // Keep track of the old date for the notes
            LocalDateTime oldDate = hearing.getHearingDate();

            // Update the hearing date and set status to SCHEDULED
            hearing.setHearingDate(newDate);
            hearing.setStatus(HearingStatus.SCHEDULED);

            // Add a note about rescheduling
            String existingNotes = hearing.getNotes();
            String rescheduleNote = "Hearing rescheduled from: " + oldDate + " to: " + newDate;

            if (existingNotes != null && !existingNotes.isEmpty()) {
                hearing.setNotes(existingNotes + "\n" + rescheduleNote);
            } else {
                hearing.setNotes(rescheduleNote);
            }

            hearingDAO.update(hearing);
            return hearing;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error rescheduling hearing", e);
            throw new RuntimeException("Could not reschedule hearing", e);
        }
    }

    /**
     * Delete hearing
     * @param id Hearing ID
     * @throws IllegalArgumentException if hearing not found
     */
    public void deleteHearing(Long id) {
        try {
            // Check if hearing exists
            Optional<Hearing> hearingOpt = hearingDAO.getById(id);
            if (!hearingOpt.isPresent()) {
                throw new IllegalArgumentException("Hearing not found");
            }

            hearingDAO.deleteById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting hearing", e);
            throw new RuntimeException("Could not delete hearing", e);
        }
    }
}