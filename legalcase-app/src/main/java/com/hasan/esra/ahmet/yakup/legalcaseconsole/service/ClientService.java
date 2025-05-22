/**
 * @file ClientService.java
 * @brief Client service class for the Legal Case Tracker system
 *
 * This file contains the service class that handles business logic for clients,
 * including creating, retrieving, updating, and deleting client records.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.service;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for client related operations
 */
public class ClientService {
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    private final ClientDAO clientDAO;

    /**
     * Constructor
     * @param clientDAO Client DAO
     */
    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    /**
     * Create a new client
     * @param name Client name
     * @param surname Client surname
     * @param email Client email
     * @param phone Client phone number
     * @param address Client address
     * @return The created client
     * @throws IllegalArgumentException if client with email already exists
     */
    public Client createClient(String name, String surname, String email, String phone, String address) {
        try {
            // Check if email is already in use
            if (email != null && !email.isEmpty()) {
                Optional<Client> existingClient = clientDAO.getByEmail(email);
                if (existingClient.isPresent()) {
                    throw new IllegalArgumentException("Email address is already in use");
                }
            }

            // Create and save the client
            Client client = new Client(name, surname, email);
            client.setPhone(phone);
            client.setAddress(address);

            return clientDAO.create(client);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while creating client", e);
            throw new RuntimeException("Could not create client", e);
        }
    }

    /**
     * Get client by ID
     * @param id Client ID
     * @return Optional containing client if found
     */
    public Optional<Client> getClientById(Long id) {
        try {
            return clientDAO.getById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting client by ID", e);
            throw new RuntimeException("Could not retrieve client", e);
        }
    }

    /**
     * Get client by email
     * @param email Client email
     * @return Optional containing client if found
     */
    public Optional<Client> getClientByEmail(String email) {
        try {
            return clientDAO.getByEmail(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting client by email", e);
            throw new RuntimeException("Could not retrieve client", e);
        }
    }

    /**
     * Get all clients
     * @return List of all clients
     */
    public List<Client> getAllClients() {
        try {
            return clientDAO.getAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while getting all clients", e);
            throw new RuntimeException("Could not retrieve clients", e);
        }
    }

    /**
     * Search clients by name or surname
     * @param searchTerm Search term
     * @return List of matching clients
     */
    public List<Client> searchClients(String searchTerm) {
        try {
            return clientDAO.searchByName(searchTerm);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while searching clients", e);
            throw new RuntimeException("Could not search clients", e);
        }
    }

    /**
     * Update client
     * @param id Client ID
     * @param name Client name
     * @param surname Client surname
     * @param email Client email
     * @param phone Client phone number
     * @param address Client address
     * @return Updated client
     * @throws IllegalArgumentException if client not found or email is already in use by another client
     */
    public Client updateClient(Long id, String name, String surname, String email, String phone, String address) {
        try {
            // Check if client exists
            Optional<Client> clientOpt = clientDAO.getById(id);
            if (!clientOpt.isPresent()) {
                throw new IllegalArgumentException("Client not found");
            }

            Client client = clientOpt.get();

            // Check if email is already used by another client
            if (email != null && !email.isEmpty() && !email.equals(client.getEmail())) {
                Optional<Client> existingClient = clientDAO.getByEmail(email);
                if (existingClient.isPresent() && !existingClient.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Email address is already used by another client");
                }
            }

            // Update fields
            client.setName(name);
            client.setSurname(surname);
            client.setEmail(email);
            client.setPhone(phone);
            client.setAddress(address);

            clientDAO.update(client);
            return client;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while updating client", e);
            throw new RuntimeException("Could not update client", e);
        }
    }

    /**
     * Delete client
     * @param id Client ID
     * @throws IllegalArgumentException if client not found
     */
    public void deleteClient(Long id) {
        try {
            // Check if client exists
            Optional<Client> clientOpt = clientDAO.getById(id);
            if (!clientOpt.isPresent()) {
                throw new IllegalArgumentException("Client not found");
            }

            clientDAO.deleteById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while deleting client", e);
            throw new RuntimeException("Could not delete client", e);
        }
    }
}