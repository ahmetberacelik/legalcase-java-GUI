/**
 * @file CaseClient.java
 * @brief Junction entity class for the Legal Case Tracker system
 *
 * This file defines the CaseClient entity class which implements the many-to-many
 * relationship between Case and Client entities. Each record in this table
 * represents an association between one case and one client.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @brief Junction table entity for many-to-many relationship between Case and Client
 * @details This entity represents the association between a legal case and a client,
 * implementing the many-to-many relationship between these entities. Each record
 * links one case to one client.
 */
@DatabaseTable(tableName = "case_client")
public class CaseClient {

    /**
     * @brief Explicit no-arg constructor required by ORMLite
     */
    public CaseClient() {
        super();
    }
    
    /**
     * @brief Unique identifier for the case-client relationship
     * @details Auto-generated primary key for the junction table
     */
    @DatabaseField(generatedId = true)
    private Long id;

    /**
     * @brief Reference to the associated case
     * @details Foreign key relationship to the Case entity
     */
    @DatabaseField(foreign = true, columnName = "case_id", canBeNull = false)
    private Case cse;

    /**
     * @brief Reference to the associated client
     * @details Foreign key relationship to the Client entity
     */
    @DatabaseField(foreign = true, columnName = "client_id", canBeNull = false)
    private Client client;

    /**
     * @brief Constructor with case and client
     * @details Creates a new case-client relationship with the specified entities
     * @param cse The Case entity to associate
     * @param client The Client entity to associate
     */
    public CaseClient(Case cse, Client client) {
        this.cse = cse;
        this.client = client;
    }
    
    /**
     * @brief Get the ID of the case-client relationship
     * @return The unique identifier
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @brief Set the ID of the case-client relationship
     * @param id The ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @brief Get the associated case
     * @return The Case entity
     */
    public Case getCse() {
        return cse;
    }
    
    /**
     * @brief Set the associated case
     * @param cse The Case entity to set
     */
    public void setCse(Case cse) {
        this.cse = cse;
    }
    
    /**
     * @brief Get the associated client
     * @return The Client entity
     */
    public Client getClient() {
        return client;
    }
    
    /**
     * @brief Set the associated client
     * @param client The Client entity to set
     */
    public void setClient(Client client) {
        this.client = client;
    }
}