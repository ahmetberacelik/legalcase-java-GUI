/**
 * @file Hearing.java
 * @brief Hearing entity class for the Legal Case Tracker system
 *
 * This file defines the Hearing entity class which represents court hearings
 * in the legal system. Hearings are scheduled events associated with cases and
 * contain information about the time, location, judge, and status.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */

package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.*;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @brief Hearing entity representing court hearings
 * @details This class represents a court hearing in the legal system. Hearings are
 * scheduled events associated with cases and contain information about the time,
 * location, judge, and status.
 */
@DatabaseTable(tableName = "hearings")
@Getter
@Setter
@NoArgsConstructor
public class Hearing extends BaseEntity {

    /**
     * @brief Reference to the associated case
     * @details Foreign key relationship to the Case entity
     */
    @DatabaseField(foreign = true, columnName = "case_id", canBeNull = false)
    private Case cse;

    /**
     * @brief Timestamp of the hearing date and time
     * @details Stored as epoch seconds for database persistence
     */
    @DatabaseField(columnName = "hearing_date", canBeNull = false)
    private long hearingDateTimestamp;

    /**
     * @brief Date and time of the hearing
     * @details Transient field not stored directly in the database
     */
    private transient LocalDateTime hearingDate;

    /**
     * @brief Name of the presiding judge
     * @details The judge assigned to this hearing
     */
    @DatabaseField(canBeNull = false)
    private String judge;

    /**
     * @brief Current status of the hearing
     * @details Indicates whether the hearing is scheduled, completed, postponed, etc.
     */
    @DatabaseField(canBeNull = false)
    private HearingStatus status;

    /**
     * @brief Physical location of the hearing
     * @details Courtroom, building, or address where the hearing will take place
     */
    @DatabaseField
    private String location;

    /**
     * @brief Additional notes about the hearing
     * @details Any relevant information or observations about the hearing
     */
    @DatabaseField(columnDefinition = "TEXT")
    private String notes;

    /**
     * @brief Constructor with id
     * @param id The unique identifier for this hearing
     * @param cse The case this hearing is associated with
     * @param hearingDate The date and time of the hearing
     * @param judge The name of the presiding judge
     */
    public Hearing(Long id, Case cse, LocalDateTime hearingDate, String judge) {
        super(id);
        this.cse = cse;
        setHearingDate(hearingDate); // Use setter to remove nanosecond precision
        this.judge = judge;
        this.status = HearingStatus.SCHEDULED;
    }

    /**
     * @brief Constructor without id
     * @param cse The case this hearing is associated with
     * @param hearingDate The date and time of the hearing
     * @param judge The name of the presiding judge
     * @param location The location where the hearing will take place
     */
    public Hearing(Case cse, LocalDateTime hearingDate, String judge, String location) {
        this.cse = cse;
        setHearingDate(hearingDate); // Use setter to remove nanosecond precision
        this.judge = judge;
        this.location = location;
        this.status = HearingStatus.SCHEDULED;
    }

    /**
     * @brief String representation of the Hearing
     * @details Returns a string containing the hearing's key information
     * @return Formatted string with hearing details
     */
    @Override
    public String toString() {
        return "Hearing{id=" + getId() + ", case=" + cse.getCaseNumber() +
                ", date=" + getHearingDate() + ", judge='" + judge +
                "', location='" + location + "', status=" + status + "}";
    }

    /**
     * @brief Gets the hearing date and time
     * @details Lazy-loads the LocalDateTime from the stored timestamp if necessary
     * @return The date and time of the hearing
     */
    public LocalDateTime getHearingDate() {
        if (hearingDate == null && hearingDateTimestamp > 0) {
            // Create LocalDateTime with second precision only (no nanoseconds)
            hearingDate = LocalDateTime.ofEpochSecond(hearingDateTimestamp, 0, ZoneOffset.UTC);
        }
        return hearingDate;
    }

    /**
     * @brief Sets the hearing date and time and updates the timestamp
     * @details Removes nanosecond precision to ensure consistent storage
     * @param hearingDate The new date and time of the hearing
     */
    public void setHearingDate(LocalDateTime hearingDate) {
        if (hearingDate != null) {
            // Remove nanosecond precision
            this.hearingDate = LocalDateTime.of(
                    hearingDate.getYear(),
                    hearingDate.getMonth(),
                    hearingDate.getDayOfMonth(),
                    hearingDate.getHour(),
                    hearingDate.getMinute(),
                    hearingDate.getSecond()
            );
            this.hearingDateTimestamp = this.hearingDate.toEpochSecond(ZoneOffset.UTC);
        } else {
            this.hearingDate = null;
        }
    }

    /**
     * @brief Gets the raw timestamp value
     * @details Used for database persistence
     * @return The epoch second timestamp of the hearing date
     */
    public long getHearingDateTimestamp() {
        return hearingDateTimestamp;
    }

    /**
     * @brief Sets the raw timestamp value
     * @details Used when loading from database
     * @param hearingDateTimestamp The epoch second timestamp to set
     */
    public void setHearingDateTimestamp(long hearingDateTimestamp) {
        this.hearingDateTimestamp = hearingDateTimestamp;
        this.hearingDate = null; // Reset transient field to be regenerated on next getHearingDate call
    }
}