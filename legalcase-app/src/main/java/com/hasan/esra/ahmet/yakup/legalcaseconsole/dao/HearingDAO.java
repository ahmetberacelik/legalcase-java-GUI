/**
 * @file HearingDAO.java
 * @brief Data Access Object for Hearing entities in the Legal Case Tracker system
 *
 * This file contains the HearingDAO class which handles database operations for Hearing entities,
 * including CRUD operations and specialized queries for date ranges and upcoming hearings.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief Data Access Object for Hearing entity
 * @details This class provides methods to perform CRUD operations and additional
 * queries on Hearing entities. It encapsulates database access for court hearings
 * and provides specialized queries for date ranges and upcoming hearings.
 */
public class HearingDAO {
    /**
     * @brief Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(HearingDAO.class.getName());

    /**
     * @brief ORM Lite DAO for Hearing entity
     * @details Handles the underlying database operations
     */
    private final Dao<Hearing, Long> hearingDao;

    /**
     * @brief Constructor
     * @param connectionSource Database connection source
     * @throws SQLException if DAO creation fails
     */
    public HearingDAO(ConnectionSource connectionSource) throws SQLException {
        this.hearingDao = DaoManager.createDao(connectionSource, Hearing.class);
    }

    /**
     * @brief Create a new hearing
     * @details Persists a new hearing to the database after setting creation timestamps
     * @param hearing Hearing to create
     * @return Created hearing with generated ID
     * @throws SQLException if creation fails
     */
    public Hearing create(Hearing hearing) throws SQLException {
        hearing.prePersist();
        hearingDao.create(hearing);
        return hearing;
    }

    /**
     * @brief Get hearing by ID
     * @details Retrieves a hearing by its primary key
     * @param id Hearing ID
     * @return Optional containing hearing if found, empty Optional otherwise
     * @throws SQLException if query fails
     */
    public Optional<Hearing> getById(Long id) throws SQLException {
        Hearing hearing = hearingDao.queryForId(id);
        return Optional.ofNullable(hearing);
    }

    /**
     * @brief Get all hearings
     * @details Retrieves all hearing records from the database
     * @return List of all hearings
     * @throws SQLException if query fails
     */
    public List<Hearing> getAll() throws SQLException {
        return hearingDao.queryForAll();
    }

    /**
     * @brief Get hearings by case ID
     * @details Retrieves all hearings associated with a specific case
     * @param caseId Case ID to filter by
     * @return List of hearings for the specified case
     * @throws SQLException if query fails
     */
    public List<Hearing> getByCaseId(Long caseId) throws SQLException {
        QueryBuilder<Hearing, Long> queryBuilder = hearingDao.queryBuilder();
        queryBuilder.where().eq("case_id", caseId);
        return queryBuilder.query();
    }

    /**
     * @brief Get hearings by status
     * @details Retrieves all hearings with a specific status
     * @param status HearingStatus to filter by
     * @return List of hearings with the specified status
     * @throws SQLException if query fails
     */
    public List<Hearing> getByStatus(HearingStatus status) throws SQLException {
        QueryBuilder<Hearing, Long> queryBuilder = hearingDao.queryBuilder();
        queryBuilder.where().eq("status", status);
        return queryBuilder.query();
    }

    /**
     * @brief Get hearings in date range
     * @details Retrieves hearings scheduled between the specified start and end dates
     * @param start Start date (inclusive)
     * @param end End date (inclusive)
     * @return List of hearings within the specified date range
     * @throws SQLException if query fails
     */
    public List<Hearing> getByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {
        QueryBuilder<Hearing, Long> queryBuilder = hearingDao.queryBuilder();
        queryBuilder.where()
                .ge("hearing_date", start.toEpochSecond(java.time.ZoneOffset.UTC))
                .and()
                .le("hearing_date", end.toEpochSecond(java.time.ZoneOffset.UTC));
        return queryBuilder.query();
    }

    /**
     * @brief Get upcoming hearings
     * @details Retrieves all future hearings that have not been cancelled,
     * sorted by date in ascending order (earliest first)
     * @return List of upcoming hearings
     * @throws SQLException if query fails
     */
    public List<Hearing> getUpcomingHearings() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        long nowTimestamp = now.toEpochSecond(java.time.ZoneOffset.UTC);

        QueryBuilder<Hearing, Long> queryBuilder = hearingDao.queryBuilder();
        queryBuilder.where()
                .gt("hearing_date", nowTimestamp)
                .and()
                .ne("status", HearingStatus.CANCELLED);

        queryBuilder.orderBy("hearing_date", true);
        return queryBuilder.query();
    }

    /**
     * @brief Update hearing
     * @details Updates an existing hearing in the database after setting update timestamp
     * @param hearing Hearing to update
     * @return Number of rows updated (should be 1 for success)
     * @throws SQLException if update fails
     */
    public int update(Hearing hearing) throws SQLException {
        hearing.preUpdate();
        return hearingDao.update(hearing);
    }

    /**
     * @brief Delete hearing
     * @details Removes a hearing from the database
     * @param hearing Hearing to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int delete(Hearing hearing) throws SQLException {
        return hearingDao.delete(hearing);
    }

    /**
     * @brief Delete hearing by ID
     * @details Removes a hearing from the database using its primary key
     * @param id ID of hearing to delete
     * @return Number of rows deleted (should be 1 for success)
     * @throws SQLException if deletion fails
     */
    public int deleteById(Long id) throws SQLException {
        return hearingDao.deleteById(id);
    }
}