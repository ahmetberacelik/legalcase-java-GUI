package com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums;

/**
 * @brief Enumeration of user roles
 * @details This enum defines the different roles users can have in the legal case
 * management system. Each role has different permission levels and access rights.
 */
public enum UserRole {
    /**
     * @brief Administrator role with full system access
     * @details Administrators can manage users, system settings, and have
     * unrestricted access to all features of the application.
     */
    ADMIN,

    /**
     * @brief Lawyer role with case management capabilities
     * @details Lawyers can create and manage cases, documents, hearings,
     * and interact with clients.
     */
    LAWYER,

    /**
     * @brief Assistant role with limited permissions
     * @details Assistants can help with case management tasks but have
     * restricted access to sensitive operations.
     */
    ASSISTANT,

    /**
     * @brief Judge role with special permissions
     * @details Judges have specialized access related to hearings, rulings,
     * and case review functionality.
     */
    JUDGE,

    /**
     * @brief Client role with limited access
     * @details Clients can only view their own cases and related documents,
     * with minimal system interaction permissions.
     */
    CLIENT
}