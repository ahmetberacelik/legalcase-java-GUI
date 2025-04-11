/**
 * @file Case.java
 * @brief Case entity class for the Legal Case Tracker system
 *
 * This file defines the Case entity class which represents legal cases in the system.
 * It contains all relevant information about cases including their identifying information,
 * status, associated clients, hearings, and documents.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */

package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @brief Case entity representing legal cases
 * @details This class represents a legal case in the system and contains all relevant
 * information about the case, including its identifying information, status,
 * associated clients, hearings, and documents.
 */
@DatabaseTable(tableName = "cases")
@Getter
@Setter
@NoArgsConstructor
public class Case extends BaseEntity {

    /**
     * @brief Unique case number identifier
     * @details Official unique identifier for the legal case
     */
    @DatabaseField(columnName = "case_number", unique = true)
    private String caseNumber;

    /**
     * @brief Title of the case
     * @details Short descriptive title for the case
     */
    @DatabaseField(canBeNull = false)
    private String title;

    /**
     * @brief Type of the legal case
     * @details Indicates the category or type of legal case
     */
    @DatabaseField(canBeNull = false)
    private CaseType type;

    /**
     * @brief Detailed description of the case
     * @details Provides comprehensive information about the case
     */
    @DatabaseField(columnDefinition = "TEXT")
    private String description;

    /**
     * @brief Current status of the case
     * @details Indicates the current stage in the case lifecycle
     */
    @DatabaseField(canBeNull = false)
    private CaseStatus status;

    /**
     * @brief List of clients associated with this case
     * @details Transient field that is not persisted to the database
     * Used only for in-memory operations
     */
    private transient List<Client> clients = new ArrayList<>();

    /**
     * @brief Collection of hearings associated with this case
     * @details Eagerly loaded collection of hearing records
     */
    @ForeignCollectionField(eager = true)
    private Collection<Hearing> hearings;

    /**
     * @brief Collection of documents associated with this case
     * @details Eagerly loaded collection of document records
     */
    @ForeignCollectionField(eager = true)
    private Collection<Document> documents;

    /**
     * @brief Constructor with id
     * @param id The unique identifier for this case
     * @param caseNumber The official case number
     * @param title The title of the case
     * @param type The type of legal case
     */
    public Case(Long id, String caseNumber, String title, CaseType type) {
        super(id);
        this.caseNumber = caseNumber;
        this.title = title;
        this.type = type;
        this.status = CaseStatus.NEW;
    }

    /**
     * @brief Constructor without id
     * @param caseNumber The official case number
     * @param title The title of the case
     * @param type The type of legal case
     */
    public Case(String caseNumber, String title, CaseType type) {
        this.caseNumber = caseNumber;
        this.title = title;
        this.type = type;
        this.status = CaseStatus.NEW;
    }

    /**
     * @brief Add a client to the case
     * @details This method only updates the transient list, actual persistence is handled by CaseDAO
     * @param client Client to add to the case
     */
    public void addClient(Client client) {
        if (this.clients == null) {
            this.clients = new ArrayList<>();
        }

        if (!this.clients.contains(client)) {
            this.clients.add(client);
        }
    }

    /**
     * @brief Remove a client from the case
     * @details This method only updates the transient list, actual persistence is handled by CaseDAO
     * @param client Client to remove from the case
     */
    public void removeClient(Client client) {
        if (this.clients != null) {
            this.clients.remove(client);
        }
    }

    /**
     * @brief Get all clients associated with this case
     * @details This is a helper method that returns the transient list
     * Actual data should be retrieved through CaseDAO
     * @return List of clients associated with this case
     */
    public List<Client> getClients() {
        if (this.clients == null) {
            this.clients = new ArrayList<>();
        }
        return this.clients;
    }

    /**
     * @brief Set the clients list
     * @details This is a helper method that updates the transient list
     * @param clients List of clients to associate with this case
     */
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    /**
     * @brief Add a hearing to the case
     * @details Associates the hearing with this case and adds it to the collection
     * @param hearing Hearing to add to the case
     */
    public void addHearing(Hearing hearing) {
        if (this.hearings == null) {
            this.hearings = new ArrayList<>();
        }

        hearing.setCse(this);
        this.hearings.add(hearing);
    }

    /**
     * @brief Add a document to the case
     * @details Associates the document with this case and adds it to the collection
     * @param document Document to add to the case
     */
    public void addDocument(Document document) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }

        document.setCse(this);
        this.documents.add(document);
    }

    /**
     * @brief String representation of the Case
     * @details Returns a string containing the case's key information
     * @return Formatted string with case details
     */
    @Override
    public String toString() {
        return "Case{id=" + getId() + ", caseNumber='" + caseNumber + "', title='" + title +
                "', type=" + type + ", status=" + status + "}";
    }
}