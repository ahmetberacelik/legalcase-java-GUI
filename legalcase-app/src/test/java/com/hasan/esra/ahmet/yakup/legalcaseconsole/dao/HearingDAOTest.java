package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import com.j256.ormlite.support.ConnectionSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @brief Test class for HearingDAO operations
 *
 * This class tests all CRUD operations and specialized queries for the Hearing entity
 */
public class HearingDAOTest {

    private ConnectionSource connectionSource;
    private HearingDAO hearingDAO;
    private CaseDAO caseDAO;

    /**
     * @brief Setup method run before each test
     *
     * Initializes the test database and DAOs
     *
     * @throws SQLException if a database error occurs
     */
    @Before
    public void setUp() throws SQLException {
        // Initialize test database
        TestDatabaseManager.createTables();
        connectionSource = TestDatabaseManager.getConnectionSource();
        hearingDAO = new HearingDAO(connectionSource);
        caseDAO = new CaseDAO(connectionSource);
    }

    /**
     * @brief Teardown method run after each test
     *
     * Closes the test database connection
     *
     * @throws SQLException if a database error occurs
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Tests successful creation of a Hearing
     *
     * Verifies that a hearing can be created and returns with a valid ID
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_Create_Success_ReturnsHearingWithId() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-001", "Test Case for Hearing", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        LocalDateTime hearingDate = LocalDateTime.of(2023, 10, 15, 10, 0);
        String judge = "Judge Smith";
        String location = "Courtroom 101";
        String notes = "Test hearing notes";

        Hearing hearing = new Hearing(caseEntity, hearingDate, judge, location);
        hearing.setNotes(notes);

        // Act
        Hearing createdHearing = hearingDAO.create(hearing);

        // Assert
        assertNotNull("Created hearing should not be null", createdHearing);
        assertNotNull("Created hearing should have an ID", createdHearing.getId());
        assertEquals("Case should be correct", caseEntity.getId(), createdHearing.getCse().getId());
        assertEquals("Hearing date should be correct", hearingDate.withNano(0), createdHearing.getHearingDate());
        assertEquals("Judge should be correct", judge, createdHearing.getJudge());
        assertEquals("Location should be correct", location, createdHearing.getLocation());
        assertEquals("Status should be SCHEDULED", HearingStatus.SCHEDULED, createdHearing.getStatus());
        assertEquals("Notes should be correct", notes, createdHearing.getNotes());
    }

    /**
     * @brief Tests retrieving an existing Hearing by ID
     *
     * Verifies that a hearing can be retrieved by its ID
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetById_ExistingHearing_ReturnsHearing() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-002", "Another Test Case", CaseType.CRIMINAL);
        caseDAO.create(caseEntity);

        LocalDateTime hearingDate = LocalDateTime.of(2023, 11, 20, 14, 30);
        Hearing hearing = new Hearing(caseEntity, hearingDate, "Judge Johnson", "Courtroom 202");
        hearingDAO.create(hearing);

        // Act
        Optional<Hearing> retrievedHearingOpt = hearingDAO.getById(hearing.getId());

        // Assert
        assertTrue("Hearing should be found", retrievedHearingOpt.isPresent());
        Hearing retrievedHearing = retrievedHearingOpt.get();
        assertEquals("Hearing ID should match", hearing.getId(), retrievedHearing.getId());
        assertEquals("Case should match", caseEntity.getId(), retrievedHearing.getCse().getId());
        assertEquals("Hearing date should match", hearingDate.withNano(0), retrievedHearing.getHearingDate());
        assertEquals("Judge should match", "Judge Johnson", retrievedHearing.getJudge());
    }

    /**
     * @brief Tests retrieving a non-existing Hearing by ID
     *
     * Verifies that an empty Optional is returned when the hearing ID doesn't exist
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetById_NonExistingHearing_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<Hearing> retrievedHearingOpt = hearingDAO.getById(9999L);

        // Assert
        assertFalse("Should return empty Optional when hearing not found", retrievedHearingOpt.isPresent());
    }

    /**
     * @brief Tests retrieving all Hearings
     *
     * Verifies that all hearings can be retrieved from the database
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetAll_WithHearings_ReturnsList() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-003", "Test Case for Multiple Hearings", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        Hearing hearing1 = new Hearing(caseEntity, LocalDateTime.of(2023, 12, 1, 9, 0), "Judge Brown", "Courtroom 301");
        Hearing hearing2 = new Hearing(caseEntity, LocalDateTime.of(2023, 12, 15, 10, 30), "Judge Brown", "Courtroom 301");
        hearingDAO.create(hearing1);
        hearingDAO.create(hearing2);

        // Act
        List<Hearing> allHearings = hearingDAO.getAll();

        // Assert
        assertNotNull("Hearing list should not be null", allHearings);
        assertTrue("Hearing list should not be empty", allHearings.size() >= 2);

        // Both created hearings should be in the list
        boolean hearing1Found = false;
        boolean hearing2Found = false;
        for (Hearing hearing : allHearings) {
            LocalDateTime hearingDate = hearing.getHearingDate();
            if (hearingDate != null) {
                if (hearingDate.equals(LocalDateTime.of(2023, 12, 1, 9, 0))) hearing1Found = true;
                if (hearingDate.equals(LocalDateTime.of(2023, 12, 15, 10, 30))) hearing2Found = true;
            }
        }
        assertTrue("First hearing should be in the list", hearing1Found);
        assertTrue("Second hearing should be in the list", hearing2Found);
    }

    /**
     * @brief Tests retrieving Hearings by Case ID
     *
     * Verifies that hearings can be retrieved by their associated case ID
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetByCaseId_WithMatchingHearings_ReturnsList() throws SQLException {
        // Arrange
        Case case1 = new Case("HEAR2023-004", "Case One", CaseType.CORPORATE);
        Case case2 = new Case("HEAR2023-005", "Case Two", CaseType.OTHER);
        caseDAO.create(case1);
        caseDAO.create(case2);

        Hearing case1Hearing1 = new Hearing(case1, LocalDateTime.of(2024, 1, 10, 9, 0), "Judge Davis", "Courtroom 401");
        Hearing case1Hearing2 = new Hearing(case1, LocalDateTime.of(2024, 1, 20, 14, 0), "Judge Davis", "Courtroom 401");
        Hearing case2Hearing = new Hearing(case2, LocalDateTime.of(2024, 1, 15, 11, 0), "Judge Wilson", "Courtroom 501");
        hearingDAO.create(case1Hearing1);
        hearingDAO.create(case1Hearing2);
        hearingDAO.create(case2Hearing);

        // Act
        List<Hearing> case1Hearings = hearingDAO.getByCaseId(case1.getId());

        // Assert
        assertNotNull("Case One hearing list should not be null", case1Hearings);
        assertEquals("Case One should have 2 hearings", 2, case1Hearings.size());

        // Only Case One hearings should be in the list
        boolean hearing1Found = false;
        boolean hearing2Found = false;
        boolean case2HearingFound = false;
        for (Hearing hearing : case1Hearings) {
            LocalDateTime hearingDate = hearing.getHearingDate();
            if (hearingDate != null) {
                if (hearing.getCse().getId().equals(case1.getId())) {
                    if (hearingDate.equals(LocalDateTime.of(2024, 1, 10, 9, 0))) hearing1Found = true;
                    if (hearingDate.equals(LocalDateTime.of(2024, 1, 20, 14, 0))) hearing2Found = true;
                }
                if (hearing.getCse().getId().equals(case2.getId())) {
                    case2HearingFound = true;
                }
            }
        }
        assertTrue("First Case One hearing should be in the list", hearing1Found);
        assertTrue("Second Case One hearing should be in the list", hearing2Found);
        assertFalse("Case Two hearing should not be in the list", case2HearingFound);
    }

    /**
     * @brief Tests retrieving Hearings by Status
     *
     * Verifies that hearings can be retrieved by their status
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetByStatus_WithMatchingHearings_ReturnsList() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-006", "Status Test Case", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Hearing scheduledHearing = new Hearing(caseEntity, LocalDateTime.of(2024, 2, 1, 10, 0), "Judge Miller", "Courtroom 101");
        scheduledHearing.setStatus(HearingStatus.SCHEDULED);

        Hearing completedHearing = new Hearing(caseEntity, LocalDateTime.of(2024, 2, 10, 9, 0), "Judge Miller", "Courtroom 101");
        completedHearing.setStatus(HearingStatus.COMPLETED);

        Hearing postponedHearing = new Hearing(caseEntity, LocalDateTime.of(2024, 2, 15, 14, 0), "Judge Miller", "Courtroom 101");
        postponedHearing.setStatus(HearingStatus.POSTPONED);

        hearingDAO.create(scheduledHearing);
        hearingDAO.create(completedHearing);
        hearingDAO.create(postponedHearing);

        // Act
        List<Hearing> scheduledHearings = hearingDAO.getByStatus(HearingStatus.SCHEDULED);

        // Assert
        assertNotNull("Scheduled hearings list should not be null", scheduledHearings);
        assertTrue("Scheduled hearings list should not be empty", scheduledHearings.size() >= 1);

        // Only SCHEDULED hearings should be in the list
        boolean scheduledFound = false;
        boolean completedFound = false;
        boolean postponedFound = false;
        for (Hearing hearing : scheduledHearings) {
            if (hearing.getId().equals(scheduledHearing.getId())) scheduledFound = true;
            if (hearing.getId().equals(completedHearing.getId())) completedFound = true;
            if (hearing.getId().equals(postponedHearing.getId())) postponedFound = true;
        }
        assertTrue("Scheduled hearing should be in the list", scheduledFound);
        assertFalse("Completed hearing should not be in the list", completedFound);
        assertFalse("Postponed hearing should not be in the list", postponedFound);
    }

    /**
     * @brief Tests retrieving Hearings within a date range
     *
     * Verifies that hearings can be retrieved when they fall within a specified date range
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetByDateRange_WithinRange_ReturnsMatchingHearings() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-007", "Date Range Test Case", CaseType.CRIMINAL);
        caseDAO.create(caseEntity);

        Hearing hearing1 = new Hearing(caseEntity, LocalDateTime.of(2024, 3, 1, 9, 0), "Judge Taylor", "Courtroom 201");
        Hearing hearing2 = new Hearing(caseEntity, LocalDateTime.of(2024, 3, 15, 10, 30), "Judge Taylor", "Courtroom 201");
        Hearing hearing3 = new Hearing(caseEntity, LocalDateTime.of(2024, 3, 30, 14, 0), "Judge Taylor", "Courtroom 201");
        hearingDAO.create(hearing1);
        hearingDAO.create(hearing2);
        hearingDAO.create(hearing3);

        // Act - Define a date range that should include only hearing1 and hearing2
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 25, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 20, 23, 59);
        List<Hearing> rangeHearings = hearingDAO.getByDateRange(startDate, endDate);

        // Assert
        assertNotNull("Range hearings list should not be null", rangeHearings);
        assertEquals("Range should include 2 hearings", 2, rangeHearings.size());

        // Only hearings within the range should be included
        boolean hearing1Found = false;
        boolean hearing2Found = false;
        boolean hearing3Found = false;
        for (Hearing hearing : rangeHearings) {
            LocalDateTime hearingDate = hearing.getHearingDate();
            if (hearingDate != null) {
                if (hearingDate.equals(LocalDateTime.of(2024, 3, 1, 9, 0))) hearing1Found = true;
                if (hearingDate.equals(LocalDateTime.of(2024, 3, 15, 10, 30))) hearing2Found = true;
                if (hearingDate.equals(LocalDateTime.of(2024, 3, 30, 14, 0))) hearing3Found = true;
            }
        }
        assertTrue("First hearing should be in the range", hearing1Found);
        assertTrue("Second hearing should be in the range", hearing2Found);
        assertFalse("Third hearing should not be in the range", hearing3Found);
    }

    /**
     * @brief Tests retrieving upcoming Hearings
     *
     * Verifies that only future hearings that aren't cancelled are included
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_GetUpcomingHearings_ReturnsOnlyFutureNotCancelledHearings() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-008", "Upcoming Test Case", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        // Past hearing
        LocalDateTime pastDate = LocalDateTime.now().minusDays(7);
        Hearing pastHearing = new Hearing(caseEntity, pastDate, "Judge Roberts", "Courtroom 301");

        // Future scheduled hearing
        LocalDateTime futureDate1 = LocalDateTime.now().plusDays(7);
        Hearing futureScheduledHearing = new Hearing(caseEntity, futureDate1, "Judge Roberts", "Courtroom 301");

        // Future cancelled hearing
        LocalDateTime futureDate2 = LocalDateTime.now().plusDays(14);
        Hearing futureCancelledHearing = new Hearing(caseEntity, futureDate2, "Judge Roberts", "Courtroom 301");
        futureCancelledHearing.setStatus(HearingStatus.CANCELLED);

        hearingDAO.create(pastHearing);
        hearingDAO.create(futureScheduledHearing);
        hearingDAO.create(futureCancelledHearing);

        // Act
        List<Hearing> upcomingHearings = hearingDAO.getUpcomingHearings();

        // Assert
        assertNotNull("Upcoming hearings list should not be null", upcomingHearings);
        assertTrue("Upcoming hearings list should not be empty", upcomingHearings.size() >= 1);

        // Only future non-cancelled hearings should be included
        boolean pastFound = false;
        boolean futureScheduledFound = false;
        boolean futureCancelledFound = false;
        for (Hearing hearing : upcomingHearings) {
            if (hearing.getId().equals(pastHearing.getId())) pastFound = true;
            if (hearing.getId().equals(futureScheduledHearing.getId())) futureScheduledFound = true;
            if (hearing.getId().equals(futureCancelledHearing.getId())) futureCancelledFound = true;
        }
        assertFalse("Past hearing should not be in upcoming hearings", pastFound);
        assertTrue("Future scheduled hearing should be in upcoming hearings", futureScheduledFound);
        assertFalse("Future cancelled hearing should not be in upcoming hearings", futureCancelledFound);
    }

    /**
     * @brief Tests updating an existing Hearing
     *
     * Verifies that a hearing can be updated with new values
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_Update_ExistingHearing_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-009", "Update Test Case", CaseType.CORPORATE);
        caseDAO.create(caseEntity);

        LocalDateTime originalDate = LocalDateTime.of(2024, 4, 15, 10, 0);
        Hearing hearing = new Hearing(caseEntity, originalDate, "Original Judge", "Original Courtroom");
        hearing.setNotes("Original notes");
        hearing.setStatus(HearingStatus.SCHEDULED);
        hearingDAO.create(hearing);

        // Update hearing
        LocalDateTime newDate = LocalDateTime.of(2024, 4, 20, 14, 30);
        hearing.setHearingDate(newDate);
        hearing.setJudge("Updated Judge");
        hearing.setLocation("Updated Courtroom");
        hearing.setNotes("Updated notes");
        hearing.setStatus(HearingStatus.POSTPONED);

        // Act
        int updatedRows = hearingDAO.update(hearing);

        // Assert
        assertEquals("Should update 1 row", 1, updatedRows);

        // Verify updated hearing from database
        Optional<Hearing> updatedHearingOpt = hearingDAO.getById(hearing.getId());
        assertTrue("Updated hearing should be found", updatedHearingOpt.isPresent());
        Hearing updatedHearing = updatedHearingOpt.get();
        assertEquals("Hearing date should be updated", newDate.withNano(0), updatedHearing.getHearingDate());
        assertEquals("Judge should be updated", "Updated Judge", updatedHearing.getJudge());
        assertEquals("Location should be updated", "Updated Courtroom", updatedHearing.getLocation());
        assertEquals("Notes should be updated", "Updated notes", updatedHearing.getNotes());
        assertEquals("Status should be updated", HearingStatus.POSTPONED, updatedHearing.getStatus());
    }

    /**
     * @brief Tests deleting an existing Hearing
     *
     * Verifies that a hearing can be deleted using the entity
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_Delete_ExistingHearing_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-010", "Delete Test Case", CaseType.OTHER);
        caseDAO.create(caseEntity);

        Hearing hearing = new Hearing(caseEntity, LocalDateTime.of(2024, 5, 1, 9, 0), "Judge Moore", "Courtroom 401");
        hearingDAO.create(hearing);

        // Verify hearing exists before deletion
        Optional<Hearing> beforeDeleteOpt = hearingDAO.getById(hearing.getId());
        assertTrue("Hearing should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = hearingDAO.delete(hearing);

        // Assert
        assertEquals("Should delete 1 row", 1, deletedRows);

        // Verify hearing is deleted
        Optional<Hearing> afterDeleteOpt = hearingDAO.getById(hearing.getId());
        assertFalse("Hearing should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Tests deleting an existing Hearing by ID
     *
     * Verifies that a hearing can be deleted using just its ID
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_DeleteById_ExistingHearing_Success() throws SQLException {
        // Arrange
        Case caseEntity = new Case("HEAR2023-011", "DeleteById Test Case", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Hearing hearing = new Hearing(caseEntity, LocalDateTime.of(2024, 5, 15, 10, 30), "Judge Adams", "Courtroom 501");
        hearingDAO.create(hearing);

        // Verify hearing exists before deletion
        Optional<Hearing> beforeDeleteOpt = hearingDAO.getById(hearing.getId());
        assertTrue("Hearing should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = hearingDAO.deleteById(hearing.getId());

        // Assert
        assertEquals("Should delete 1 row", 1, deletedRows);

        // Verify hearing is deleted
        Optional<Hearing> afterDeleteOpt = hearingDAO.getById(hearing.getId());
        assertFalse("Hearing should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * @brief Tests deleting a non-existing Hearing by ID
     *
     * Verifies that attempting to delete a non-existing hearing returns zero affected rows
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    public void Test_DeleteById_NonExistingHearing_ReturnsZero() throws SQLException {
        // Act
        int deletedRows = hearingDAO.deleteById(9999L);

        // Assert
        assertEquals("Should return 0 when no rows are deleted", 0, deletedRows);
    }
}