/**
 * @file BaseEntity.java
 * @brief Base entity class for the Legal Case Tracker system
 *
 * This file contains the abstract base entity class that provides common fields
 * and functionality for all entity classes in the system, including ID and
 * timestamp tracking.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */

package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @brief Base entity class that provides common fields for all entities
 * @details This abstract class implements common properties and methods
 * that all entity classes in the system inherit.
 */
@Getter
@Setter
public abstract class BaseEntity {
    /**
     * @brief Unique identifier for the entity
     * @details Auto-generated primary key field
     */
    @DatabaseField(generatedId = true)
    private Long id;

    /**
     * @brief Creation timestamp of the entity
     * @details Stores when the entity was first created in the database
     */
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "created_at", canBeNull = false)
    private LocalDateTime createdAt;

    /**
     * @brief Last update timestamp of the entity
     * @details Stores when the entity was last modified
     */
    @DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * @brief Default constructor
     * @details Initializes creation and update timestamps to current time
     */
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * @brief Constructor with ID
     * @details Initializes the entity with a specific ID and current timestamps
     * @param id The unique identifier to assign to this entity
     */
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    /**
     * @brief Pre-persist lifecycle callback
     * @details Method called before entity is persisted to the database
     * Sets the creation and update timestamps to current time
     */
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * @brief Pre-update lifecycle callback
     * @details Method called before entity is updated in the database
     * Updates only the update timestamp to current time
     */
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}