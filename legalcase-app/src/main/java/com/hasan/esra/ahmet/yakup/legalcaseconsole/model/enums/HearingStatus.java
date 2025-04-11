package com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums;

/**
 * @brief Enumeration of hearing statuses
 * @details This enum defines the possible states of a court hearing in the legal case
 * management system. It tracks the lifecycle of hearings from scheduling to completion
 * or cancellation.
 */
public enum HearingStatus {
    /**
     * @brief Hearing is scheduled for a future date
     * @details Indicates that a hearing has been officially scheduled and is pending.
     * This is the initial status for all hearings when first created.
     */
    SCHEDULED,

    /**
     * @brief Hearing has been completed
     * @details Indicates that a hearing has successfully taken place on the scheduled
     * date. This status should be set after the hearing is finished and any outcomes
     * have been recorded.
     */
    COMPLETED,

    /**
     * @brief Hearing has been postponed to a later date
     * @details Indicates that a previously scheduled hearing could not take place on
     * the original date and has been rescheduled. This status requires updating the
     * hearing date to the new date.
     */
    POSTPONED,

    /**
     * @brief Hearing has been cancelled
     * @details Indicates that a scheduled hearing will not take place and has not been
     * rescheduled. This is a terminal state for a hearing that will not be held.
     */
    CANCELLED
}