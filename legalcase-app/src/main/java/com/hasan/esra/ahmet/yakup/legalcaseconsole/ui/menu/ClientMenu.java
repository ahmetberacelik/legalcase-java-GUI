/**
 * @file ClientMenu.java
 * @brief Client management menu class for the Legal Case Tracker system
 *
 * This file contains the ClientMenu class which provides the user interface
 * for managing clients including adding, viewing, updating, deleting, 
 * searching, and listing clients.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;

import java.util.List;
import java.util.Optional;

/**
 * @brief Menu for client management
 *
 * This class handles all client-related user interface operations, including
 * adding, viewing, updating, deleting, searching, and listing clients.
 */
public class ClientMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final MenuManager menuManager;

    /**
     * @brief Client service for client operations
     * @details Handles CRUD operations and business logic for clients
     */
    private final ClientService clientService;

    /**
     * @brief Constructor
     *
     * @param menuManager Menu manager for navigation control
     * @param clientService Client service for business operations
     */
    public ClientMenu(MenuManager menuManager, ClientService clientService) {
        this.menuManager = menuManager;
        this.clientService = clientService;
    }

    /**
     * @brief Display the client menu
     *
     * Shows all available client management options and handles user selection.
     */
    public void display() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Client Tracking");

        ConsoleHelper.displayMenuOption(1, "Add New Client");
        ConsoleHelper.displayMenuOption(2, "View Client Details");
        ConsoleHelper.displayMenuOption(3, "Update Client");
        ConsoleHelper.displayMenuOption(4, "Delete Client");
        ConsoleHelper.displayMenuOption(5, "Search Client");
        ConsoleHelper.displayMenuOption(6, "List All Clients");
        ConsoleHelper.displayMenuOption(7, "Return to Main Menu");
        ConsoleHelper.displayHorizontalLine();

        int choice = ConsoleHelper.readInt("Enter your choice", 1, 7);

        switch (choice) {
            case 1:
                addClient();
                break;
            case 2:
                viewClientDetails();
                break;
            case 3:
                updateClient();
                break;
            case 4:
                deleteClient();
                break;
            case 5:
                searchClients();
                break;
            case 6:
                viewAllClients();
                break;
            case 7:
                menuManager.navigateToMainMenu();
                break;
        }
    }

    /**
     * @brief Add a new client
     *
     * Collects client information from user input and creates a new client record.
     */
    public void addClient() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Add New Client");

        try {
            String name = ConsoleHelper.readRequiredString("Enter name");
            String surname = ConsoleHelper.readRequiredString("Enter surname");
            String email = ConsoleHelper.readString("Enter email");
            String phone = ConsoleHelper.readString("Enter phone number");
            String address = ConsoleHelper.readString("Enter address");

            Client client = clientService.createClient(name, surname, email, phone, address);

            ConsoleHelper.displaySuccess("Client successfully added! ID: " + client.getId());
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error while adding client: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View details of a specific client
     *
     * Retrieves and displays detailed information for a client by ID.
     */
    public void viewClientDetails() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("View Client Details");

        Long clientId = ConsoleHelper.readLong("Enter client ID");

        try {
            Optional<Client> clientOpt = clientService.getClientById(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();

                ConsoleHelper.displayHorizontalLine();
                ConsoleHelper.displayMessage("Client ID: " + client.getId());
                ConsoleHelper.displayMessage("Name: " + client.getFullName());
                ConsoleHelper.displayMessage("Email: " + client.getEmail());
                ConsoleHelper.displayMessage("Phone: " + client.getPhone());
                ConsoleHelper.displayMessage("Address: " + client.getAddress());
                ConsoleHelper.displayMessage("Created At: " + client.getCreatedAt());
                ConsoleHelper.displayMessage("Last Updated: " + client.getUpdatedAt());
                ConsoleHelper.displayHorizontalLine();
            } else {
                ConsoleHelper.displayError("Client with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while retrieving client information: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update an existing client
     *
     * Allows modification of client details by showing current information
     * and collecting updated information from user input.
     */
    public void updateClient() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Update Client");

        Long clientId = ConsoleHelper.readLong("Enter client ID to update");

        try {
            Optional<Client> clientOpt = clientService.getClientById(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();

                ConsoleHelper.displayMessage("Current information:");
                ConsoleHelper.displayMessage("Name: " + client.getName());
                ConsoleHelper.displayMessage("Surname: " + client.getSurname());
                ConsoleHelper.displayMessage("Email: " + client.getEmail());
                ConsoleHelper.displayMessage("Phone: " + client.getPhone());
                ConsoleHelper.displayMessage("Address: " + client.getAddress());
                ConsoleHelper.displayHorizontalLine();

                String name = ConsoleHelper.readString("Enter new name (press Enter to leave unchanged)");
                String surname = ConsoleHelper.readString("Enter new surname (press Enter to leave unchanged)");
                String email = ConsoleHelper.readString("Enter new email (press Enter to leave unchanged)");
                String phone = ConsoleHelper.readString("Enter new phone number (press Enter to leave unchanged)");
                String address = ConsoleHelper.readString("Enter new address (press Enter to leave unchanged)");

                name = name.isEmpty() ? client.getName() : name;
                surname = surname.isEmpty() ? client.getSurname() : surname;
                email = email.isEmpty() ? client.getEmail() : email;
                phone = phone.isEmpty() ? client.getPhone() : phone;
                address = address.isEmpty() ? client.getAddress() : address;

                clientService.updateClient(clientId, name, surname, email, phone, address);

                ConsoleHelper.displaySuccess("Client successfully updated");
            } else {
                ConsoleHelper.displayError("Client with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error while updating client: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a client
     *
     * Removes a client record after confirmation by the user.
     * Shows client details before deletion for verification.
     */
    public void deleteClient() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Delete Client");

        Long clientId = ConsoleHelper.readLong("Enter client ID to delete");

        try {
            Optional<Client> clientOpt = clientService.getClientById(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();

                ConsoleHelper.displayMessage("Client information to be deleted:");
                ConsoleHelper.displayMessage("ID: " + client.getId());
                ConsoleHelper.displayMessage("Name: " + client.getFullName());
                ConsoleHelper.displayMessage("Email: " + client.getEmail());
                ConsoleHelper.displayHorizontalLine();

                boolean confirm = ConsoleHelper.readBoolean("Are you sure you want to delete this client?");

                if (confirm) {
                    clientService.deleteClient(clientId);
                    ConsoleHelper.displaySuccess("Client successfully deleted");
                } else {
                    ConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                ConsoleHelper.displayError("Client with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error while deleting client: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Search for clients
     *
     * Performs a search operation based on client name or surname
     * and displays matching results.
     */
    public void searchClients() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Search Client");

        String searchTerm = ConsoleHelper.readRequiredString("Enter search term (name or surname)");

        try {
            List<Client> clients = clientService.searchClients(searchTerm);

            if (clients.isEmpty()) {
                ConsoleHelper.displayMessage("No clients found matching search criteria.");
            } else {
                ConsoleHelper.displayMessage("Search results:");
                ConsoleHelper.displayHorizontalLine();

                for (Client client : clients) {
                    ConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error while searching for clients: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View all clients
     *
     * Retrieves and displays a list of all clients in the system.
     */
    public void viewAllClients() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List All Clients");

        try {
            List<Client> clients = clientService.getAllClients();

            if (clients.isEmpty()) {
                ConsoleHelper.displayMessage("There are no registered clients in the system.");
            } else {
                ConsoleHelper.displayMessage("Total " + clients.size() + " clients found:");
                ConsoleHelper.displayHorizontalLine();

                for (Client client : clients) {
                    ConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error while listing clients: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }
}