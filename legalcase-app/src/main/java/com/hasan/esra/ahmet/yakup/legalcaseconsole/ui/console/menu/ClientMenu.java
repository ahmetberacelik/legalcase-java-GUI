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
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;

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
    private final ConsoleMenuManager consoleMenuManager;
    
    /**
     * @brief Client service for client operations
     * @details Handles CRUD operations and business logic for clients
     */
    private final ClientService clientService;

    /**
     * @brief Constructor
     *
     * @param consoleMenuManager Menu manager for navigation control
     * @param clientService Client service for business operations
     */
    public ClientMenu(ConsoleMenuManager consoleMenuManager, ClientService clientService) {
        this.consoleMenuManager = consoleMenuManager;
        this.clientService = clientService;
    }

    /**
     * @brief Display the client menu
     *
     * Shows all available client management options and handles user selection.
     */
    public void display() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Client Tracking");

        UiConsoleHelper.displayMenuOption(1, "Add New Client");
        UiConsoleHelper.displayMenuOption(2, "View Client Details");
        UiConsoleHelper.displayMenuOption(3, "Update Client");
        UiConsoleHelper.displayMenuOption(4, "Delete Client");
        UiConsoleHelper.displayMenuOption(5, "Search Client");
        UiConsoleHelper.displayMenuOption(6, "List All Clients");
        UiConsoleHelper.displayMenuOption(7, "Return to Main Menu");
        UiConsoleHelper.displayHorizontalLine();

        int choice = UiConsoleHelper.readInt("Enter your choice", 1, 7);

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
                consoleMenuManager.navigateToMainMenu();
                break;
        }
    }

    /**
     * @brief Add a new client
     *
     * Collects client information from user input and creates a new client record.
     */
    public void addClient() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Add New Client");

        try {
            String name = UiConsoleHelper.readRequiredString("Enter name");
            String surname = UiConsoleHelper.readRequiredString("Enter surname");
            String email = UiConsoleHelper.readString("Enter email");
            String phone = UiConsoleHelper.readString("Enter phone number");
            String address = UiConsoleHelper.readString("Enter address");

            Client client = clientService.createClient(name, surname, email, phone, address);

            UiConsoleHelper.displaySuccess("Client successfully added! ID: " + client.getId());
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error while adding client: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View details of a specific client
     *
     * Retrieves and displays detailed information for a client by ID.
     */
    public void viewClientDetails() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("View Client Details");

        Long clientId = UiConsoleHelper.readLong("Enter client ID");

        try {
            Optional<Client> clientOpt = clientService.getClientById(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();

                UiConsoleHelper.displayHorizontalLine();
                UiConsoleHelper.displayMessage("Client ID: " + client.getId());
                UiConsoleHelper.displayMessage("Name: " + client.getFullName());
                UiConsoleHelper.displayMessage("Email: " + client.getEmail());
                UiConsoleHelper.displayMessage("Phone: " + client.getPhone());
                UiConsoleHelper.displayMessage("Address: " + client.getAddress());
                UiConsoleHelper.displayMessage("Created At: " + client.getCreatedAt());
                UiConsoleHelper.displayMessage("Last Updated: " + client.getUpdatedAt());
                UiConsoleHelper.displayHorizontalLine();
            } else {
                UiConsoleHelper.displayError("Client with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while retrieving client information: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update an existing client
     *
     * Allows modification of client details by showing current information
     * and collecting updated information from user input.
     */
    public void updateClient() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Update Client");

        Long clientId = UiConsoleHelper.readLong("Enter client ID to update");

        try {
            Optional<Client> clientOpt = clientService.getClientById(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();

                UiConsoleHelper.displayMessage("Current information:");
                UiConsoleHelper.displayMessage("Name: " + client.getName());
                UiConsoleHelper.displayMessage("Surname: " + client.getSurname());
                UiConsoleHelper.displayMessage("Email: " + client.getEmail());
                UiConsoleHelper.displayMessage("Phone: " + client.getPhone());
                UiConsoleHelper.displayMessage("Address: " + client.getAddress());
                UiConsoleHelper.displayHorizontalLine();

                String name = UiConsoleHelper.readString("Enter new name (press Enter to leave unchanged)");
                String surname = UiConsoleHelper.readString("Enter new surname (press Enter to leave unchanged)");
                String email = UiConsoleHelper.readString("Enter new email (press Enter to leave unchanged)");
                String phone = UiConsoleHelper.readString("Enter new phone number (press Enter to leave unchanged)");
                String address = UiConsoleHelper.readString("Enter new address (press Enter to leave unchanged)");

                name = name.isEmpty() ? client.getName() : name;
                surname = surname.isEmpty() ? client.getSurname() : surname;
                email = email.isEmpty() ? client.getEmail() : email;
                phone = phone.isEmpty() ? client.getPhone() : phone;
                address = address.isEmpty() ? client.getAddress() : address;

                clientService.updateClient(clientId, name, surname, email, phone, address);

                UiConsoleHelper.displaySuccess("Client successfully updated");
            } else {
                UiConsoleHelper.displayError("Client with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error while updating client: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a client
     *
     * Removes a client record after confirmation by the user.
     * Shows client details before deletion for verification.
     */
    public void deleteClient() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Delete Client");

        Long clientId = UiConsoleHelper.readLong("Enter client ID to delete");

        try {
            Optional<Client> clientOpt = clientService.getClientById(clientId);

            if (clientOpt.isPresent()) {
                Client client = clientOpt.get();

                UiConsoleHelper.displayMessage("Client information to be deleted:");
                UiConsoleHelper.displayMessage("ID: " + client.getId());
                UiConsoleHelper.displayMessage("Name: " + client.getFullName());
                UiConsoleHelper.displayMessage("Email: " + client.getEmail());
                UiConsoleHelper.displayHorizontalLine();

                boolean confirm = UiConsoleHelper.readBoolean("Are you sure you want to delete this client?");

                if (confirm) {
                    clientService.deleteClient(clientId);
                    UiConsoleHelper.displaySuccess("Client successfully deleted");
                } else {
                    UiConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                UiConsoleHelper.displayError("Client with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error while deleting client: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Search for clients
     *
     * Performs a search operation based on client name or surname
     * and displays matching results.
     */
    public void searchClients() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Search Client");

        String searchTerm = UiConsoleHelper.readRequiredString("Enter search term (name or surname)");

        try {
            List<Client> clients = clientService.searchClients(searchTerm);

            if (clients.isEmpty()) {
                UiConsoleHelper.displayMessage("No clients found matching search criteria.");
            } else {
                UiConsoleHelper.displayMessage("Search results:");
                UiConsoleHelper.displayHorizontalLine();

                for (Client client : clients) {
                    UiConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error while searching for clients: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View all clients
     *
     * Retrieves and displays a list of all clients in the system.
     */
    public void viewAllClients() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List All Clients");

        try {
            List<Client> clients = clientService.getAllClients();

            if (clients.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no registered clients in the system.");
            } else {
                UiConsoleHelper.displayMessage("Total " + clients.size() + " clients found:");
                UiConsoleHelper.displayHorizontalLine();

                for (Client client : clients) {
                    UiConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error while listing clients: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }
}