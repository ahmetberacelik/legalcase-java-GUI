package com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums;

/**
 * @brief Enumeration of case statuses
 * @details This enum defines the possible states of a legal case in the system.
 * It represents the lifecycle of cases from creation to completion and archival.
 */
public enum CaseStatus {
    /**
     * @brief Newly created case
     * @details Indicates a case that has been recently created in the system
     * but has not yet entered active processing. Initial information may still
     * be incomplete at this stage.
     */
    NEW,

    /**
     * @brief Active case being worked on
     * @details Indicates a case that is currently under active management.
     * Legal work is ongoing, and the case is being processed through the
     * legal system.
     */
    ACTIVE,

    /**
     * @brief Case pending action or decision
     * @details Indicates a case that is temporarily paused while awaiting
     * external action, response, or decision. This could be waiting for
     * court decisions, client input, or documentation.
     */
    PENDING,

    /**
     * @brief Closed case (completed)
     * @details Indicates a case where all legal work has been completed.
     * The case has reached its conclusion through settlement, judgment,
     * dismissal, or other final resolution.
     */
    CLOSED,

    /**
     * @brief Archived case (no longer active but preserved for records)
     * @details Indicates a case that has been closed for some time and
     * moved to long-term storage. These cases are generally not accessed
     * for day-to-day operations but are preserved for record-keeping,
     * compliance, and historical reference.
     */
    ARCHIVED
}