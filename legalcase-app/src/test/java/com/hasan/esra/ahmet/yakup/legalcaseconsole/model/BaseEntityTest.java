package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * @brief Test class for BaseEntity
 * @details Contains tests for constructors, getters, setters and lifecycle methods
 */
public class BaseEntityTest {

    /**
     * @brief Test implementation of BaseEntity
     * @details Created for testing since BaseEntity is abstract
     */
    private static class TestEntity extends BaseEntity {
        /**
         * @brief Default constructor
         */
        public TestEntity() {
            super();
        }

        /**
         * @brief Constructor with ID parameter
         * @param id The entity identifier
         */
        public TestEntity(Long id) {
            super(id);
        }
    }

    /**
     * @brief Setup method executed before each test
     * @throws SQLException If database setup fails
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();
    }

    /**
     * @brief Cleanup method executed after each test
     * @throws SQLException If database connection closing fails
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * @brief Tests that default constructor initializes timestamps
     */
    @Test
    public void Test_Constructor_Default_InitializesTimestamps() {
        // Action
        TestEntity entity = new TestEntity();

        // Verification
        assertNull("ID should be null", entity.getId());
        assertNotNull("createdAt should not be null", entity.getCreatedAt());
        assertNotNull("updatedAt should not be null", entity.getUpdatedAt());
    }

    /**
     * @brief Tests that constructor with ID sets ID and initializes timestamps
     */
    @Test
    public void Test_Constructor_WithId_SetsIdAndTimestamps() {
        // Action
        Long id = 123L;
        TestEntity entity = new TestEntity(id);

        // Verification
        assertEquals("ID should be set correctly", id, entity.getId());
        assertNotNull("createdAt should not be null", entity.getCreatedAt());
        assertNotNull("updatedAt should not be null", entity.getUpdatedAt());
    }

    /**
     * @brief Tests that setters and getters work correctly
     */
    @Test
    public void Test_SettersAndGetters_WorkCorrectly() {
        // Arrangement
        TestEntity entity = new TestEntity();
        Long id = 456L;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);

        // Action
        entity.setId(id);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        // Verification
        assertEquals("ID should be set correctly", id, entity.getId());
        assertEquals("createdAt should be set correctly", createdAt, entity.getCreatedAt());
        assertEquals("updatedAt should be set correctly", updatedAt, entity.getUpdatedAt());
    }

    /**
     * @brief Tests that prePersist method updates timestamps
     */
    @Test
    public void Test_PrePersist_UpdatesTimestamps() {
        // Arrangement
        TestEntity entity = new TestEntity();
        LocalDateTime originalCreatedAt = entity.getCreatedAt();

        // Action
        // Wait a moment
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if sleep is interrupted
        }
        entity.prePersist();

        // Verification
        assertNotEquals("createdAt should be updated", originalCreatedAt, entity.getCreatedAt());
        assertEquals("createdAt and updatedAt should be the same", entity.getCreatedAt(), entity.getUpdatedAt());
    }

    /**
     * @brief Tests that preUpdate method only updates updatedAt
     */
    @Test
    public void Test_PreUpdate_UpdatesOnlyUpdatedAt() {
        // Arrangement
        TestEntity entity = new TestEntity();
        LocalDateTime originalCreatedAt = entity.getCreatedAt();

        // Action
        // Wait a moment
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore if sleep is interrupted
        }
        entity.preUpdate();

        // Verification
        assertEquals("createdAt should not change", originalCreatedAt, entity.getCreatedAt());
        assertNotEquals("updatedAt should be updated", originalCreatedAt, entity.getUpdatedAt());
    }
}