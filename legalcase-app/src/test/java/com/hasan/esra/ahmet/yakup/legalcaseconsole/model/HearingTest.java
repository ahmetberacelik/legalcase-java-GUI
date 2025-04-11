package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

/**
 * @brief Test class for the Hearing model
 *
 * This class contains unit tests for validating the functionality of the Hearing class.
 */
public class HearingTest {

    /**
     * @brief Sets up the test environment before each test
     *
     * Creates the required test database tables
     *
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();
    }

    /**
     * @brief Cleans up the test environment after each test
     *
     * Closes the test database connection
     *
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Test for constructor with ID parameter
     *
     * Verifies that the constructor with ID sets all values correctly
     */
    @Test
    public void Test_Constructor_WithId_SetsCorrectValues() {
        // Arrange
        Long id = 1L;
        Case caseEntity = new Case("HEAR001", "Hearing Test Case", CaseType.CIVIL);
        LocalDateTime hearingDate = LocalDateTime.now();
        String judge = "Judge Smith";

        // Act
        Hearing hearing = new Hearing(id, caseEntity, hearingDate, judge);

        // Assert
        assertEquals("ID should be set correctly", id, hearing.getId());
        assertEquals("Case should be set correctly", caseEntity, hearing.getCse());
        assertEquals("Hearing date should be set correctly", hearingDate.withNano(0), hearing.getHearingDate());
        assertEquals("Judge should be set correctly", judge, hearing.getJudge());
        assertEquals("Status should be set to SCHEDULED", HearingStatus.SCHEDULED, hearing.getStatus());
        assertNotNull("createdAt should not be null", hearing.getCreatedAt());
        assertNotNull("updatedAt should not be null", hearing.getUpdatedAt());
    }

    /**
     * @brief Test for constructor with location parameter
     *
     * Verifies that the constructor with location sets all values correctly
     */
    @Test
    public void Test_Constructor_WithLocation_SetsCorrectValues() {
        // Arrange
        Case caseEntity = new Case("HEAR002", "Another Hearing Test", CaseType.CRIMINAL);
        LocalDateTime hearingDate = LocalDateTime.now();
        String judge = "Judge Johnson";
        String location = "Courtroom 101";

        // Act
        Hearing hearing = new Hearing(caseEntity, hearingDate, judge, location);

        // Assert
        assertNull("ID should be null", hearing.getId());
        assertEquals("Case should be set correctly", caseEntity, hearing.getCse());
        assertEquals("Hearing date should be set correctly", hearingDate.withNano(0), hearing.getHearingDate());
        assertEquals("Judge should be set correctly", judge, hearing.getJudge());
        assertEquals("Location should be set correctly", location, hearing.getLocation());
        assertEquals("Status should be set to SCHEDULED", HearingStatus.SCHEDULED, hearing.getStatus());
        assertNotNull("createdAt should not be null", hearing.getCreatedAt());
        assertNotNull("updatedAt should not be null", hearing.getUpdatedAt());
    }

    /**
     * @brief Test for setHearingDate method
     *
     * Verifies that the method updates timestamp and removes nanoseconds
     */
    @Test
    public void Test_SetHearingDate_UpdatesTimestampAndRemovesNanoseconds() {
        // Arrange
        Hearing hearing = new Hearing();
        LocalDateTime dateWithNanos = LocalDateTime.of(2023, 10, 15, 14, 30, 45, 123456789);

        // Act
        hearing.setHearingDate(dateWithNanos);

        // Assert
        LocalDateTime expectedDate = LocalDateTime.of(2023, 10, 15, 14, 30, 45);
        assertEquals("Hearing date should be set without nanoseconds", expectedDate, hearing.getHearingDate());
        assertEquals("Timestamp should be set correctly", expectedDate.toEpochSecond(ZoneOffset.UTC), hearing.getHearingDateTimestamp());
    }

    /**
     * @brief Test for setHearingDate method with null value
     *
     * Verifies that the method doesn't throw an exception when passed null
     */
    @Test
    public void Test_SetHearingDate_WithNull_DoesNotThrowException() {
        // Arrange
        Hearing hearing = new Hearing();

        // Act and Assert - should not throw exception
        try {
            hearing.setHearingDate(null);
            assertNull("Hearing date should be null", hearing.getHearingDate());
        } catch (Exception e) {
            fail("No exception should be thrown: " + e.getMessage());
        }
    }

    /**
     * @brief Test for getHearingDate method with null transient field
     *
     * Verifies that the method correctly converts from timestamp when the transient field is null
     */
    @Test
    public void Test_GetHearingDate_WithNullTransientField_ConvertsFromTimestamp() {
        // Arrange
        Hearing hearing = new Hearing();
        LocalDateTime testDate = LocalDateTime.of(2023, 11, 20, 10, 0, 0);
        long timestamp = testDate.toEpochSecond(ZoneOffset.UTC);
        hearing.setHearingDateTimestamp(timestamp);

        // Act
        LocalDateTime retrievedDate = hearing.getHearingDate();

        // Assert
        assertEquals("Hearing date should be correctly converted from timestamp", testDate, retrievedDate);
    }

    /**
     * @brief Test for getHearingDate method with zero timestamp
     *
     * Verifies that the method returns null when timestamp is zero
     */
    @Test
    public void Test_GetHearingDate_WithZeroTimestamp_ReturnsNull() {
        // Arrange
        Hearing hearing = new Hearing();
        hearing.setHearingDateTimestamp(0);

        // Act
        LocalDateTime retrievedDate = hearing.getHearingDate();

        // Assert
        assertNull("Should return null for 0 timestamp", retrievedDate);
    }

    /**
     * @brief Test for setHearingDateTimestamp method
     *
     * Verifies that the method resets the transient field
     */
    @Test
    public void Test_SetHearingDateTimestamp_ResetsTransientField() {
        // Arrange
        Hearing hearing = new Hearing();
        LocalDateTime initialDate = LocalDateTime.of(2023, 12, 25, 12, 0, 0);
        hearing.setHearingDate(initialDate);

        // Verify that the transient field is set
        assertNotNull("Transient field should be set first", hearing.getHearingDate());

        // Act
        long newTimestamp = LocalDateTime.of(2024, 1, 1, 9, 0, 0).toEpochSecond(ZoneOffset.UTC);
        hearing.setHearingDateTimestamp(newTimestamp);

        // Verify that when getHearingDate is called, the transient field is reset and recreated from the timestamp
        LocalDateTime newDate = LocalDateTime.of(2024, 1, 1, 9, 0, 0);
        assertEquals("Hearing date should be set according to new timestamp", newDate, hearing.getHearingDate());
    }

    /**
     * @brief Test for prePersist method
     *
     * Verifies that the method updates timestamps correctly
     */
    @Test
    public void Test_PrePersist_UpdatesTimestamps() {
        // Arrange
        Hearing hearing = new Hearing();
        LocalDateTime originalCreatedAt = hearing.getCreatedAt();

        // Act
        // Wait for a short time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if sleep is interrupted
        }
        hearing.prePersist();

        // Assert
        assertNotEquals("createdAt should be updated", originalCreatedAt, hearing.getCreatedAt());
        assertEquals("createdAt and updatedAt should be the same", hearing.getCreatedAt(), hearing.getUpdatedAt());
    }

    /**
     * @brief Test for preUpdate method
     *
     * Verifies that the method updates only the updatedAt timestamp
     */
    @Test
    public void Test_PreUpdate_UpdatesOnlyUpdatedAt() {
        // Arrange
        Hearing hearing = new Hearing();
        LocalDateTime originalCreatedAt = hearing.getCreatedAt();

        // Act
        // Wait for a short time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if sleep is interrupted
        }
        hearing.preUpdate();

        // Assert
        assertEquals("createdAt should not change", originalCreatedAt, hearing.getCreatedAt());
        assertNotEquals("updatedAt should be updated", originalCreatedAt, hearing.getUpdatedAt());
    }

    /**
     * @brief Test for toString method
     *
     * Verifies that the toString method contains all relevant information
     */
    @Test
    public void Test_ToString_ContainsRelevantInfo() {
        // Arrange
        Long id = 123L;
        Case caseEntity = new Case(456L, "CASE-HST", "ToString Test Case", CaseType.CIVIL);
        LocalDateTime hearingDate = LocalDateTime.of(2023, 10, 20, 9, 30);
        String judge = "Judge toString";
        String location = "ToString Courtroom";
        HearingStatus status = HearingStatus.SCHEDULED;

        Hearing hearing = new Hearing(id, caseEntity, hearingDate, judge);
        hearing.setLocation(location);
        hearing.setStatus(status);

        // Act
        String toString = hearing.toString();

        // Assert
        assertTrue("toString should contain id", toString.contains(id.toString()));
        assertTrue("toString should contain case number", toString.contains(caseEntity.getCaseNumber()));
        assertTrue("toString should contain date", toString.contains(hearingDate.toString()));
        assertTrue("toString should contain judge", toString.contains(judge));
        assertTrue("toString should contain location", toString.contains(location));
        assertTrue("toString should contain status", toString.contains(status.toString()));
    }
}