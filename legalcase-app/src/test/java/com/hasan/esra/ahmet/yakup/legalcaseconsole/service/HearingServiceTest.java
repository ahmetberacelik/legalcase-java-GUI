package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.HearingDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;

/**
 * @brief Test class for HearingService
 *
 * This class tests all functionality of the HearingService class including
 * creating, retrieving, updating, and deleting hearings.
 */
public class HearingServiceTest {

    private ConnectionSource connectionSource;
    private HearingDAO hearingDAO;
    private CaseDAO caseDAO;
    private HearingService hearingService;
    private Case testCase;
    private LocalDateTime testDateTime;

    /**
     * @brief Set up the test environment before each test
     *
     * Initializes the database connection, clears existing data,
     * creates test objects and services needed for testing.
     *
     * @throws SQLException if database operations fail
     */
    @Before
    public void setup() throws SQLException {
        // Get a connection to the test database
        connectionSource = TestDatabaseManager.getConnectionSource();

        // Clear any existing data
        TestDatabaseManager.clearTables();

        // Create real DAOs with the test connection
        hearingDAO = new HearingDAO(connectionSource);
        caseDAO = new CaseDAO(connectionSource);

        // Create the service with the real DAOs
        hearingService = new HearingService(hearingDAO, caseDAO);

        // Create a test case for hearing tests
        testCase = new Case("123", "Test Case Title", CaseType.CIVIL);
        caseDAO.create(testCase);

        // Create a test date time for hearings
        testDateTime = LocalDateTime.now().plusDays(7); // A week from now
    }

    /**
     * @brief Clean up after each test
     *
     * Clears all data from test tables to ensure tests are isolated.
     *
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Clean up after each test
        TestDatabaseManager.clearTables();
    }

    /**
     * @brief Helper method to compare dates with second precision
     *
     * Compares year, month, day, hour, minute, and second components of two dates.
     *
     * @param message Error message prefix to display if assertion fails
     * @param expected Expected date value
     * @param actual Actual date value to check
     */
    private void assertDateEquals(String message, LocalDateTime expected, LocalDateTime actual) {
        // Only compare year, month, day, hour, minute and second
        assertEquals(message + " - Year", expected.getYear(), actual.getYear());
        assertEquals(message + " - Month", expected.getMonth(), actual.getMonth());
        assertEquals(message + " - Day", expected.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(message + " - Hour", expected.getHour(), actual.getHour());
        assertEquals(message + " - Minute", expected.getMinute(), actual.getMinute());
        assertEquals(message + " - Second", expected.getSecond(), actual.getSecond());
    }

    /**
     * @brief Test creating a hearing with valid data
     *
     * Tests that creating a hearing with valid parameters returns a hearing object with
     * correct values and a valid ID.
     */
    @Test
    public void Test_CreateHearing_ValidData_ReturnsHearingWithId() {
        // Test data
        String judge = "Judge Smith";
        String location = "Courtroom 101";
        String notes = "Initial hearing notes";

        // Create hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, judge, location, notes);

        // Verify the hearing was created correctly
        assertNotNull("Created hearing should not be null", hearing);
        assertNotNull("Hearing ID should not be null", hearing.getId());
        assertEquals("Judge should match", judge, hearing.getJudge());
        assertEquals("Location should match", location, hearing.getLocation());
        assertEquals("Notes should match", notes, hearing.getNotes());
        assertEquals("Status should be SCHEDULED", HearingStatus.SCHEDULED, hearing.getStatus());
        assertEquals("Case ID should match", testCase.getId(), hearing.getCse().getId());
        assertDateEquals("Hearing date should match", hearingDate, hearing.getHearingDate());

        // Verify the hearing exists in the database
        Optional<Hearing> retrievedHearing = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should exist in database", retrievedHearing.isPresent());
        assertEquals("Retrieved hearing ID should match", hearing.getId(), retrievedHearing.get().getId());
    }

    /**
     * @brief Test creating a hearing with a non-existent case ID
     *
     * Tests that attempting to create a hearing with a case ID that doesn't exist
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_CreateHearing_NonExistentCaseId_ThrowsIllegalArgumentException() {
        // Try to create a hearing with a non-existent case ID
        hearingService.createHearing(9999L, testDateTime, "Judge Smith", "Courtroom 101", "Test notes");
    }

    /**
     * @brief Test handling of SQL exceptions during hearing creation
     *
     * Tests that when an underlying SQLException occurs during creation,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_CreateHearing_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith", "Courtroom 101", "Test notes");
    }

    /**
     * @brief Test retrieving a hearing by ID
     *
     * Tests that a hearing can be correctly retrieved by its ID.
     */
    @Test
    public void Test_GetHearingById_ExistingId_ReturnsHearing() {
        // Create a hearing
        Hearing hearing = hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith",
                "Courtroom 101", "Test notes");

        // Retrieve the hearing by ID
        Optional<Hearing> retrievedHearing = hearingService.getHearingById(hearing.getId());

        // Verify
        assertTrue("Hearing should be found", retrievedHearing.isPresent());
        assertEquals("ID should match", hearing.getId(), retrievedHearing.get().getId());
        assertEquals("Judge should match", "Judge Smith", retrievedHearing.get().getJudge());
    }

    /**
     * @brief Test retrieving a non-existent hearing
     *
     * Tests that attempting to retrieve a hearing with an ID that doesn't exist
     * returns an empty Optional.
     */
    @Test
    public void Test_GetHearingById_NonExistentId_ReturnsEmptyOptional() {
        // Try to retrieve a hearing with a non-existent ID
        Optional<Hearing> retrievedHearing = hearingService.getHearingById(9999L);

        // Verify
        assertFalse("Non-existent hearing should not be found", retrievedHearing.isPresent());
    }

    /**
     * @brief Test handling of SQL exceptions during hearing retrieval
     *
     * Tests that when an underlying SQLException occurs during retrieval,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetHearingById_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.getHearingById(1L);
    }

    /**
     * @brief Test retrieving all hearings
     *
     * Tests that all hearings can be retrieved correctly.
     */
    @Test
    public void Test_GetAllHearings_MultipleHearings_ReturnsAllHearings() {
        // Create several hearings
        hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith", "Courtroom 101", "Notes 1");
        hearingService.createHearing(testCase.getId(), testDateTime.plusDays(1), "Judge Brown", "Courtroom 102", "Notes 2");
        hearingService.createHearing(testCase.getId(), testDateTime.plusDays(2), "Judge Davis", "Courtroom 103", "Notes 3");

        // Get all hearings
        List<Hearing> hearings = hearingService.getAllHearings();

        // Verify
        assertNotNull("Hearing list should not be null", hearings);
        assertEquals("Should have 3 hearings", 3, hearings.size());
    }

    /**
     * @brief Test retrieving all hearings when none exist
     *
     * Tests that retrieving all hearings when none exist returns an empty list.
     */
    @Test
    public void Test_GetAllHearings_NoHearings_ReturnsEmptyList() {
        // Get all hearings without creating any
        List<Hearing> hearings = hearingService.getAllHearings();

        // Verify
        assertNotNull("Hearing list should not be null", hearings);
        assertTrue("Hearing list should be empty", hearings.isEmpty());
    }

    /**
     * @brief Test handling of SQL exceptions during retrieval of all hearings
     *
     * Tests that when an underlying SQLException occurs during retrieval,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetAllHearings_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.getAllHearings();
    }

    /**
     * @brief Test retrieving hearings by case ID
     *
     * Tests that hearings can be correctly filtered by case ID.
     */
    @Test
    public void Test_GetHearingsByCaseId_ExistingCaseId_ReturnsHearingsForCase() {
        // Create a second case
        Case secondCase = new Case("456", "Second Case Title", CaseType.CRIMINAL);
        try {
            caseDAO.create(secondCase);
        } catch (SQLException e) {
            fail("Failed to create second test case: " + e.getMessage());
        }

        // Create hearings for both cases
        hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith", "Courtroom 101", "First Case Hearing 1");
        hearingService.createHearing(testCase.getId(), testDateTime.plusDays(1), "Judge Brown", "Courtroom 102", "First Case Hearing 2");
        hearingService.createHearing(secondCase.getId(), testDateTime.plusDays(2), "Judge Davis", "Courtroom 103", "Second Case Hearing");

        // Get hearings for first case
        List<Hearing> firstCaseHearings = hearingService.getHearingsByCaseId(testCase.getId());

        // Verify
        assertNotNull("Hearing list should not be null", firstCaseHearings);
        assertEquals("Should have 2 hearings for first case", 2, firstCaseHearings.size());

        // Get hearings for second case
        List<Hearing> secondCaseHearings = hearingService.getHearingsByCaseId(secondCase.getId());

        // Verify
        assertNotNull("Hearing list should not be null", secondCaseHearings);
        assertEquals("Should have 1 hearing for second case", 1, secondCaseHearings.size());
    }

    /**
     * @brief Test retrieving hearings for a non-existent case
     *
     * Tests that attempting to retrieve hearings for a case ID that doesn't exist
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetHearingsByCaseId_NonExistentCaseId_ThrowsIllegalArgumentException() {
        // Try to get hearings for a non-existent case
        hearingService.getHearingsByCaseId(9999L);
    }

    /**
     * @brief Test handling of SQL exceptions during retrieval by case ID
     *
     * Tests that when an underlying SQLException occurs during retrieval,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetHearingsByCaseId_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.getHearingsByCaseId(testCase.getId());
    }

    /**
     * @brief Test retrieving hearings by status
     *
     * Tests that hearings can be correctly filtered by status.
     */
    @Test
    public void Test_GetHearingsByStatus_ExistingStatus_ReturnsHearingsWithStatus() {
        // Create hearings with different statuses
        Hearing hearing1 = hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith",
                "Courtroom 101", "Scheduled Hearing 1");
        Hearing hearing2 = hearingService.createHearing(testCase.getId(), testDateTime.plusDays(1), "Judge Brown",
                "Courtroom 102", "Scheduled Hearing 2");
        Hearing hearing3 = hearingService.createHearing(testCase.getId(), testDateTime.plusDays(2), "Judge Davis",
                "Courtroom 103", "Completed Hearing");

        // Update status of third hearing to COMPLETED
        hearingService.updateHearingStatus(hearing3.getId(), HearingStatus.COMPLETED);

        // Get hearings with SCHEDULED status
        List<Hearing> scheduledHearings = hearingService.getHearingsByStatus(HearingStatus.SCHEDULED);

        // Verify
        assertNotNull("Hearing list should not be null", scheduledHearings);
        assertEquals("Should have 2 SCHEDULED hearings", 2, scheduledHearings.size());
        for (Hearing hearing : scheduledHearings) {
            assertEquals("Hearing status should be SCHEDULED", HearingStatus.SCHEDULED, hearing.getStatus());
        }

        // Get hearings with COMPLETED status
        List<Hearing> completedHearings = hearingService.getHearingsByStatus(HearingStatus.COMPLETED);

        // Verify
        assertNotNull("Hearing list should not be null", completedHearings);
        assertEquals("Should have 1 COMPLETED hearing", 1, completedHearings.size());
        assertEquals("Hearing status should be COMPLETED", HearingStatus.COMPLETED, completedHearings.get(0).getStatus());
    }

    /**
     * @brief Test retrieving hearings by status when none exist
     *
     * Tests that retrieving hearings by a status when none exist with that status
     * returns an empty list.
     */
    @Test
    public void Test_GetHearingsByStatus_NoHearingsWithStatus_ReturnsEmptyList() {
        // Create hearings with only SCHEDULED status
        hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith", "Courtroom 101", "Scheduled Hearing");

        // Get hearings with COMPLETED status
        List<Hearing> completedHearings = hearingService.getHearingsByStatus(HearingStatus.COMPLETED);

        // Verify
        assertNotNull("Hearing list should not be null", completedHearings);
        assertTrue("Hearing list should be empty", completedHearings.isEmpty());
    }

    /**
     * @brief Test handling of SQL exceptions during retrieval by status
     *
     * Tests that when an underlying SQLException occurs during retrieval,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetHearingsByStatus_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.getHearingsByStatus(HearingStatus.SCHEDULED);
    }

    /**
     * @brief Test retrieving hearings by date range
     *
     * Tests that hearings can be correctly filtered by a date range.
     */
    @Test
    public void Test_GetHearingsByDateRange_ValidDateRange_ReturnsHearingsInRange() {
        // Create hearings on different dates
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime nextWeek = today.plusDays(7);
        LocalDateTime nextMonth = today.plusMonths(1);

        hearingService.createHearing(testCase.getId(), today.plusDays(1), "Judge Smith", "Courtroom 101", "Tomorrow");
        hearingService.createHearing(testCase.getId(), today.plusDays(5), "Judge Brown", "Courtroom 102", "In 5 days");
        hearingService.createHearing(testCase.getId(), nextMonth, "Judge Davis", "Courtroom 103", "Next month");

        // Get hearings in the next week
        List<Hearing> nextWeekHearings = hearingService.getHearingsByDateRange(today, nextWeek);

        // Verify
        assertNotNull("Hearing list should not be null", nextWeekHearings);
        assertEquals("Should have 2 hearings in the next week", 2, nextWeekHearings.size());

        // Get hearings in the next month
        List<Hearing> allHearings = hearingService.getHearingsByDateRange(today, nextMonth.plusDays(1));

        // Verify
        assertNotNull("Hearing list should not be null", allHearings);
        assertEquals("Should have 3 hearings in total", 3, allHearings.size());
    }

    /**
     * @brief Test retrieving hearings by date range when none exist
     *
     * Tests that retrieving hearings by a date range when none exist in that range
     * returns an empty list.
     */
    @Test
    public void Test_GetHearingsByDateRange_NoHearingsInRange_ReturnsEmptyList() {
        // Create a hearing
        LocalDateTime today = LocalDateTime.now();
        hearingService.createHearing(testCase.getId(), today.plusMonths(2), "Judge Smith", "Courtroom 101", "In 2 months");

        // Get hearings in the next week
        List<Hearing> nextWeekHearings = hearingService.getHearingsByDateRange(today, today.plusDays(7));

        // Verify
        assertNotNull("Hearing list should not be null", nextWeekHearings);
        assertTrue("Hearing list should be empty", nextWeekHearings.isEmpty());
    }

    /**
     * @brief Test retrieving hearings with invalid date range
     *
     * Tests that attempting to retrieve hearings with a start date after end date
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetHearingsByDateRange_StartDateAfterEndDate_ThrowsIllegalArgumentException() {
        // Try to get hearings with start date after end date
        LocalDateTime today = LocalDateTime.now();
        hearingService.getHearingsByDateRange(today.plusDays(7), today);
    }

    /**
     * @brief Test retrieving hearings with null dates
     *
     * Tests that attempting to retrieve hearings with null dates
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_GetHearingsByDateRange_NullDates_ThrowsIllegalArgumentException() {
        // Try to get hearings with null dates
        hearingService.getHearingsByDateRange(null, null);
    }

    /**
     * @brief Test handling of SQL exceptions during retrieval by date range
     *
     * Tests that when an underlying SQLException occurs during retrieval,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetHearingsByDateRange_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        LocalDateTime today = LocalDateTime.now();
        hearingService.getHearingsByDateRange(today, today.plusDays(7));
    }

    /**
     * @brief Test retrieving upcoming hearings
     *
     * Tests that upcoming hearings (those with dates in the future) can be
     * correctly retrieved.
     */
    @Test
    public void Test_GetUpcomingHearings_HasUpcomingHearings_ReturnsUpcomingHearings() {
        // Create hearings
        hearingService.createHearing(testCase.getId(), LocalDateTime.now().plusDays(1), "Judge Smith", "Courtroom 101", "Tomorrow");
        hearingService.createHearing(testCase.getId(), LocalDateTime.now().plusDays(5), "Judge Brown", "Courtroom 102", "In 5 days");
        hearingService.createHearing(testCase.getId(), LocalDateTime.now().minusDays(1), "Judge Davis", "Courtroom 103", "Yesterday");

        // Get upcoming hearings
        List<Hearing> upcomingHearings = hearingService.getUpcomingHearings();

        // Verify
        assertNotNull("Hearing list should not be null", upcomingHearings);
        assertEquals("Should have 2 upcoming hearings", 2, upcomingHearings.size());
    }

    /**
     * @brief Test retrieving upcoming hearings when none exist
     *
     * Tests that retrieving upcoming hearings when none exist returns an empty list.
     */
    @Test
    public void Test_GetUpcomingHearings_NoUpcomingHearings_ReturnsEmptyList() {
        // Create a hearing in the past
        hearingService.createHearing(testCase.getId(), LocalDateTime.now().minusDays(1), "Judge Smith", "Courtroom 101", "Yesterday");

        // Get upcoming hearings
        List<Hearing> upcomingHearings = hearingService.getUpcomingHearings();

        // Verify
        assertNotNull("Hearing list should not be null", upcomingHearings);
        assertTrue("Hearing list should be empty", upcomingHearings.isEmpty());
    }

    /**
     * @brief Test handling of SQL exceptions during retrieval of upcoming hearings
     *
     * Tests that when an underlying SQLException occurs during retrieval,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_GetUpcomingHearings_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.getUpcomingHearings();
    }

    /**
     * @brief Test updating a hearing with valid data
     *
     * Tests that updating a hearing with valid parameters correctly updates all fields.
     */
    @Test
    public void Test_UpdateHearing_ValidData_ReturnsUpdatedHearing() {
        // Create a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Judge Smith",
                "Courtroom 101", "Original notes");

        // New data for update
        LocalDateTime newDate = LocalDateTime.now().plusDays(14);
        String newJudge = "Judge Johnson";
        String newLocation = "Courtroom 202";
        String newNotes = "Updated notes";
        HearingStatus newStatus = HearingStatus.POSTPONED;

        // Update the hearing
        Hearing updatedHearing = hearingService.updateHearing(
                hearing.getId(), newDate, newJudge, newLocation, newNotes, newStatus);

        // Verify the update was successful
        assertDateEquals("Date should be updated", newDate, updatedHearing.getHearingDate());
        assertEquals("Judge should be updated", newJudge, updatedHearing.getJudge());
        assertEquals("Location should be updated", newLocation, updatedHearing.getLocation());
        assertEquals("Notes should be updated", newNotes, updatedHearing.getNotes());
        assertEquals("Status should be updated", newStatus, updatedHearing.getStatus());

        // Verify the update persisted to the database
        Optional<Hearing> retrievedHearing = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should exist", retrievedHearing.isPresent());
        assertDateEquals("Updated date should be saved", newDate, retrievedHearing.get().getHearingDate());
        assertEquals("Updated judge should be saved", newJudge, retrievedHearing.get().getJudge());
    }

    /**
     * @brief Test updating a hearing with null fields
     *
     * Tests that updating a hearing with some null fields only updates the non-null fields.
     */
    @Test
    public void Test_UpdateHearing_NullFields_OnlyUpdatesNonNullFields() {
        // Create a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Judge Smith",
                "Courtroom 101", "Original notes");

        // Update only the location, leaving other fields unchanged
        Hearing updatedHearing = hearingService.updateHearing(
                hearing.getId(), null, null, "New Location", null, null);

        // Verify only the location was updated
        assertDateEquals("Date should remain unchanged", hearingDate, updatedHearing.getHearingDate());
        assertEquals("Judge should remain unchanged", "Judge Smith", updatedHearing.getJudge());
        assertEquals("Location should be updated", "New Location", updatedHearing.getLocation());
        assertEquals("Notes should remain unchanged", "Original notes", updatedHearing.getNotes());
        assertEquals("Status should remain unchanged", HearingStatus.SCHEDULED, updatedHearing.getStatus());
    }

    /**
     * @brief Test updating a non-existent hearing
     *
     * Tests that attempting to update a hearing with an ID that doesn't exist
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateHearing_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to update a hearing with a non-existent ID
        hearingService.updateHearing(9999L, testDateTime, "Judge Smith", "Courtroom 101", "Notes", HearingStatus.SCHEDULED);
    }

    /**
     * @brief Test handling of SQL exceptions during hearing update
     *
     * Tests that when an underlying SQLException occurs during update,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_UpdateHearing_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.updateHearing(1L, testDateTime, "Judge Smith", "Courtroom 101", "Notes", HearingStatus.SCHEDULED);
    }

    /**
     * @brief Test updating a hearing status
     *
     * Tests that updating just the status of a hearing works correctly.
     */
    @Test
    public void Test_UpdateHearingStatus_ValidData_ReturnsUpdatedHearing() {
        // Create a hearing
        Hearing hearing = hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith",
                "Courtroom 101", "Original notes");

        // Update the status
        Hearing updatedHearing = hearingService.updateHearingStatus(hearing.getId(), HearingStatus.COMPLETED);

        // Verify the update was successful
        assertEquals("Status should be updated", HearingStatus.COMPLETED, updatedHearing.getStatus());

        // Verify the update persisted to the database
        Optional<Hearing> retrievedHearing = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should exist", retrievedHearing.isPresent());
        assertEquals("Updated status should be saved", HearingStatus.COMPLETED, retrievedHearing.get().getStatus());
    }

    /**
     * @brief Test updating status of a non-existent hearing
     *
     * Tests that attempting to update the status of a hearing with an ID that doesn't exist
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_UpdateHearingStatus_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to update a hearing status with a non-existent ID
        hearingService.updateHearingStatus(9999L, HearingStatus.COMPLETED);
    }

    /**
     * @brief Test handling of SQL exceptions during status update
     *
     * Tests that when an underlying SQLException occurs during status update,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_UpdateHearingStatus_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.updateHearingStatus(1L, HearingStatus.COMPLETED);
    }

    /**
     * @brief Test rescheduling a hearing
     *
     * Tests that rescheduling a hearing updates the date and adds a note.
     */
    @Test
    public void Test_RescheduleHearing_ValidData_ReturnsRescheduledHearing() {
        // Create a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Judge Smith",
                "Courtroom 101", "Original notes");

        // New date for rescheduling
        LocalDateTime newDate = LocalDateTime.now().plusDays(14);

        // Reschedule the hearing
        Hearing rescheduledHearing = hearingService.rescheduleHearing(hearing.getId(), newDate);

        // Verify the reschedule was successful
        assertDateEquals("Date should be updated", newDate, rescheduledHearing.getHearingDate());
        assertEquals("Status should be SCHEDULED", HearingStatus.SCHEDULED, rescheduledHearing.getStatus());
        assertTrue("Notes should contain rescheduling information",
                rescheduledHearing.getNotes().contains("Hearing rescheduled from"));

        // Verify the reschedule persisted to the database
        Optional<Hearing> retrievedHearing = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should exist", retrievedHearing.isPresent());
        assertDateEquals("Updated date should be saved", newDate, retrievedHearing.get().getHearingDate());
    }

    /**
     * @brief Test rescheduling a hearing with existing notes
     *
     * Tests that rescheduling a hearing appends to existing notes rather than replacing them.
     */
    @Test
    public void Test_RescheduleHearing_ExistingNotes_AppendsReschedulingNote() {
        // Create a hearing with existing notes
        String existingNotes = "These are existing notes";
        Hearing hearing = hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith",
                "Courtroom 101", existingNotes);

        // New date for rescheduling
        LocalDateTime newDate = LocalDateTime.now().plusDays(14);

        // Reschedule the hearing
        Hearing rescheduledHearing = hearingService.rescheduleHearing(hearing.getId(), newDate);

        // Verify the notes were appended, not replaced
        assertTrue("Notes should contain original notes", rescheduledHearing.getNotes().contains(existingNotes));
        assertTrue("Notes should contain rescheduling information",
                rescheduledHearing.getNotes().contains("Hearing rescheduled from"));
    }

    /**
     * @brief Test rescheduling a hearing with null date
     *
     * Tests that attempting to reschedule a hearing with a null date
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_RescheduleHearing_NullDate_ThrowsIllegalArgumentException() {
        // Create a hearing
        Hearing hearing = hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith",
                "Courtroom 101", "Notes");

        // Try to reschedule with null date
        hearingService.rescheduleHearing(hearing.getId(), null);
    }

    /**
     * @brief Test rescheduling a non-existent hearing
     *
     * Tests that attempting to reschedule a hearing with an ID that doesn't exist
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_RescheduleHearing_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to reschedule a hearing with a non-existent ID
        hearingService.rescheduleHearing(9999L, testDateTime.plusDays(14));
    }

    /**
     * @brief Test handling of SQL exceptions during rescheduling
     *
     * Tests that when an underlying SQLException occurs during rescheduling,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_RescheduleHearing_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.rescheduleHearing(1L, testDateTime.plusDays(14));
    }

    /**
     * @brief Test deleting a hearing
     *
     * Tests that a hearing can be correctly deleted.
     */
    @Test
    public void Test_DeleteHearing_ExistingId_HearingIsDeleted() {
        // Create a hearing
        Hearing hearing = hearingService.createHearing(testCase.getId(), testDateTime, "Judge Smith",
                "Courtroom 101", "Notes");

        // Verify the hearing exists
        assertTrue("Hearing should exist before deletion",
                hearingService.getHearingById(hearing.getId()).isPresent());

        // Delete the hearing
        hearingService.deleteHearing(hearing.getId());

        // Verify the hearing no longer exists
        assertFalse("Hearing should not exist after deletion",
                hearingService.getHearingById(hearing.getId()).isPresent());
    }

    /**
     * @brief Test deleting a non-existent hearing
     *
     * Tests that attempting to delete a hearing with an ID that doesn't exist
     * throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void Test_DeleteHearing_NonExistentId_ThrowsIllegalArgumentException() {
        // Try to delete a hearing with a non-existent ID
        hearingService.deleteHearing(9999L);
    }

    /**
     * @brief Test handling of SQL exceptions during deletion
     *
     * Tests that when an underlying SQLException occurs during deletion,
     * it is properly wrapped in a RuntimeException.
     *
     * @throws SQLException for test setup
     */
    @Test(expected = RuntimeException.class)
    public void Test_DeleteHearing_SQLExceptionThrown_ThrowsRuntimeException() throws SQLException {
        // Setup - Use DAO that throws SQLException
        hearingService = new HearingService(new ThrowingHearingDAO(connectionSource), caseDAO);

        // Execute - This should throw RuntimeException due to underlying SQLException
        hearingService.deleteHearing(1L);
    }

    /**
     * @brief HearingDAO implementation that throws SQLException for all operations
     *
     * This mock implementation is used to test exception handling in the service layer.
     */
    private static class ThrowingHearingDAO extends HearingDAO {

        /**
         * @brief Constructor for ThrowingHearingDAO
         *
         * @param connectionSource Database connection source
         * @throws SQLException if connection fails
         */
        public ThrowingHearingDAO(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource);
        }

        @Override
        public Hearing create(Hearing hearing) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public Optional<Hearing> getById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public List<Hearing> getAll() throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public List<Hearing> getByCaseId(Long caseId) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public List<Hearing> getByStatus(HearingStatus status) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public List<Hearing> getByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public List<Hearing> getUpcomingHearings() throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public int update(Hearing hearing) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public int delete(Hearing hearing) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }

        @Override
        public int deleteById(Long id) throws SQLException {
            throw new SQLException("Forced SQLException for testing");
        }
    }
}