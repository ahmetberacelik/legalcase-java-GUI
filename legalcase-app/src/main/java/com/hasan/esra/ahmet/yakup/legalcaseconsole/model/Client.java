/**
 * @file Client.java
 * @brief Client entity class for the Legal Case Tracker system
 *
 * This file defines the Client entity class which represents clients in the legal system.
 * It contains personal information about clients as well as relationships to their
 * associated legal cases.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */

package com.hasan.esra.ahmet.yakup.legalcaseconsole.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Client entity representing legal clients
 * @details This class represents a client in the legal system and contains
 * personal information as well as relationships to associated cases.
 */
@DatabaseTable(tableName = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client extends BaseEntity {

    /**
     * @brief First name of the client
     * @details The given name of the client
     */
    @DatabaseField(canBeNull = false)
    private String name;

    /**
     * @brief Last name of the client
     * @details The family name of the client
     */
    @DatabaseField(canBeNull = false)
    private String surname;

    /**
     * @brief Email address of the client
     * @details Unique email identifier for the client
     */
    @DatabaseField(unique = true)
    private String email;

    /**
     * @brief Phone number of the client
     * @details Contact phone number for the client
     */
    @DatabaseField
    private String phone;

    /**
     * @brief Physical address of the client
     * @details Residential or mailing address for the client
     */
    @DatabaseField
    private String address;

    /**
     * @brief List of cases associated with this client
     * @details Transient field that is not persisted to the database
     * Used only for in-memory operations
     */
    private transient List<Case> cases = new ArrayList<>();

    /**
     * @brief Constructor with id
     * @param id The unique identifier for this client
     * @param name The first name of the client
     * @param surname The last name of the client
     * @param email The email address of the client
     */
    public Client(Long id, String name, String surname, String email) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    /**
     * @brief Constructor without id
     * @param name The first name of the client
     * @param surname The last name of the client
     * @param email The email address of the client
     */
    public Client(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    /**
     * @brief Get the full name of the client
     * @details Combines first and last name into a complete name
     * @return String containing the client's full name
     */
    public String getFullName() {
        return name + " " + surname;
    }

    /**
     * @brief Add a case to the client
     * @details This method only updates the transient list, actual persistence is handled by CaseDAO
     * Also maintains bidirectional relationship by adding this client to the case
     * @param cse Case to add to this client
     */
    public void addCase(Case cse) {
        if (this.cases == null) {
            this.cases = new ArrayList<>();
        }

        if (!this.cases.contains(cse)) {
            this.cases.add(cse);
            if (!cse.getClients().contains(this)) {
                cse.addClient(this);
            }
        }
    }

    /**
     * @brief Remove a case from the client
     * @details This method only updates the transient list, actual persistence is handled by CaseDAO
     * Also maintains bidirectional relationship by removing this client from the case
     * @param cse Case to remove from this client
     */
    public void removeCase(Case cse) {
        if (this.cases != null) {
            this.cases.remove(cse);
            if (cse.getClients().contains(this)) {
                cse.removeClient(this);
            }
        }
    }

    /**
     * @brief Get all cases associated with this client
     * @details This is a helper method that returns the transient list
     * Actual data should be retrieved through CaseDAO
     * @return List of cases associated with this client
     */
    public List<Case> getCases() {
        if (this.cases == null) {
            this.cases = new ArrayList<>();
        }
        return this.cases;
    }

    /**
     * @brief Set the cases list
     * @details This is a helper method that updates the transient list
     * @param cases List of cases to associate with this client
     */
    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    /**
     * @brief String representation of the Client
     * @details Returns a string containing the client's key information
     * @return Formatted string with client details
     */
    @Override
    public String toString() {
        return "Client{id=" + getId() + ", name='" + name + "', surname='" + surname + "', email='" + email + "'}";
    }
}