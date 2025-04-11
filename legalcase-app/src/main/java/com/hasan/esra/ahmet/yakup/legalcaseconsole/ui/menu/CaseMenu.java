/**
 * @file CaseMenu.java
 * @brief Case management menu class for the Legal Case Tracker system
 *
 * This file contains the CaseMenu class which provides the user interface
 * for managing legal cases including creation, updating, deleting, and
 * associating clients with cases.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;

import java.util.List;
import java.util.Optional;

/**
 * @brief Menu for case management operations
 * @details Provides user interface for managing legal cases including creation,
 *          updating, deleting, and associating clients with cases
 */
public class CaseMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final MenuManager menuManager;

    /**
     * @brief Case service for case operations
     * @details Handles CRUD operations and business logic for legal cases
     */
    private final CaseService caseService;

    /**
     * @brief Client service for client operations
     * @details Used to retrieve client information when associating clients with cases
     */
    private final ClientService clientService;

    /**
     * @brief Constructor for the case management menu
     * @param menuManager The main menu navigation manager
     * @param caseService Service for case-related operations
     * @param clientService Service for client-related operations
     */
    public CaseMenu(MenuManager menuManager, CaseService caseService, ClientService clientService) {
        this.menuManager = menuManager;
        this.caseService = caseService;
        this.clientService = clientService;
    }

    /**
     * @brief Display the case management menu
     * @details Shows all available case operations and processes user selection
     */
    public void display() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Case Management");

        ConsoleHelper.displayMenuOption(1, "Add New Case");
        ConsoleHelper.displayMenuOption(2, "View Case Details");
        ConsoleHelper.displayMenuOption(3, "Update Case");
        ConsoleHelper.displayMenuOption(4, "Delete Case");
        ConsoleHelper.displayMenuOption(5, "Add Client to Case");
        ConsoleHelper.displayMenuOption(6, "Remove Client from Case");
        ConsoleHelper.displayMenuOption(7, "List Cases by Status");
        ConsoleHelper.displayMenuOption(8, "List All Cases");
        ConsoleHelper.displayMenuOption(9, "Return to Main Menu");
        ConsoleHelper.displayHorizontalLine();

        int choice = ConsoleHelper.readInt("Enter your choice", 1, 9);

        switch (choice) {
            case 1:
                addCase();
                break;
            case 2:
                viewCaseDetails();
                break;
            case 3:
                updateCase();
                break;
            case 4:
                deleteCase();
                break;
            case 5:
                addClientToCase();
                break;
            case 6:
                removeClientFromCase();
                break;
            case 7:
                listCasesByStatus();
                break;
            case 8:
                viewAllCases();
                break;
            case 9:
                menuManager.navigateToMainMenu();
                break;
        }
    }

    /**
     * @brief Add a new legal case to the system
     * @details Collects case information, creates a new case record, and optionally
     *          associates a client with the newly created case
     * @throws IllegalArgumentException If input validation fails
     * @throws Exception For any other creation failures
     */
    public void addCase() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Add New Case");

        try {
            String caseNumber = ConsoleHelper.readRequiredString("Enter case number");
            String title = ConsoleHelper.readRequiredString("Enter title");
            CaseType type = ConsoleHelper.readEnum("Select case type", CaseType.class);
            String description = ConsoleHelper.readString("Enter description");

            Case caseEntity = caseService.createCase(caseNumber, title, type, description);

            ConsoleHelper.displaySuccess("Case successfully created! ID: " + caseEntity.getId());

            // Ask if user wants to add client to the case
            boolean addClient = ConsoleHelper.readBoolean("Do you want to add a client to this case?");
            if (addClient) {
                addClientToCase(caseEntity.getId());
            }

        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while creating case: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View detailed information about a specific case
     * @details Retrieves and displays complete case information including
     *          associated clients and case status
     */
    public void viewCaseDetails() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("View Case Details");

        Long caseId = ConsoleHelper.readLong("Enter case ID");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                ConsoleHelper.displayHorizontalLine();
                ConsoleHelper.displayMessage("Case ID: " + caseEntity.getId());
                ConsoleHelper.displayMessage("Case Number: " + caseEntity.getCaseNumber());
                ConsoleHelper.displayMessage("Title: " + caseEntity.getTitle());
                ConsoleHelper.displayMessage("Type: " + caseEntity.getType());
                ConsoleHelper.displayMessage("Status: " + caseEntity.getStatus());
                ConsoleHelper.displayMessage("Description: " + caseEntity.getDescription());
                ConsoleHelper.displayMessage("Created At: " + caseEntity.getCreatedAt());
                ConsoleHelper.displayMessage("Last Updated: " + caseEntity.getUpdatedAt());

                // Show clients associated with the case
                List<Client> clients = caseService.getClientsForCase(caseId);
                if (clients.isEmpty()) {
                    ConsoleHelper.displayMessage("There are no clients associated with this case.");
                } else {
                    ConsoleHelper.displayMessage("\nClients associated with the case:");
                    for (Client client : clients) {
                        ConsoleHelper.displayMessage("ID: " + client.getId() +
                                ", Name: " + client.getFullName() +
                                ", Email: " + client.getEmail());
                    }
                }

                ConsoleHelper.displayHorizontalLine();
            } else {
                ConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while retrieving case information: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update an existing case's information
     * @details Allows modification of case number, title, type, status, and description
     *          with options to keep existing values for fields
     * @throws IllegalArgumentException If input validation fails
     * @throws Exception For any other update failures
     */
    public void updateCase() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Update Case");

        Long caseId = ConsoleHelper.readLong("Enter case ID to update");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                ConsoleHelper.displayMessage("Current information:");
                ConsoleHelper.displayMessage("Case Number: " + caseEntity.getCaseNumber());
                ConsoleHelper.displayMessage("Title: " + caseEntity.getTitle());
                ConsoleHelper.displayMessage("Type: " + caseEntity.getType());
                ConsoleHelper.displayMessage("Status: " + caseEntity.getStatus());
                ConsoleHelper.displayMessage("Description: " + caseEntity.getDescription());
                ConsoleHelper.displayHorizontalLine();

                String caseNumber = ConsoleHelper.readString("Enter new case number (leave empty for current value)");
                String title = ConsoleHelper.readString("Enter new title (leave empty for current value)");

                // Ask if user wants to change the case type
                boolean updateType = ConsoleHelper.readBoolean("Do you want to change the case type?");
                CaseType type = updateType ?
                        ConsoleHelper.readEnum("Select new case type", CaseType.class) :
                        caseEntity.getType();

                // Ask if user wants to change the case status
                boolean updateStatus = ConsoleHelper.readBoolean("Do you want to change the case status?");
                CaseStatus status = updateStatus ?
                        ConsoleHelper.readEnum("Select new case status", CaseStatus.class) :
                        caseEntity.getStatus();

                String description = ConsoleHelper.readString("Enter new description (leave empty for current value)");

                caseNumber = caseNumber.isEmpty() ? caseEntity.getCaseNumber() : caseNumber;
                title = title.isEmpty() ? caseEntity.getTitle() : title;
                description = description.isEmpty() ? caseEntity.getDescription() : description;

                caseService.updateCase(caseId, caseNumber, title, type, description, status);

                ConsoleHelper.displaySuccess("Case successfully updated");
            } else {
                ConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while updating case: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a case from the system
     * @details Shows case information for confirmation before permanent deletion
     * @throws Exception If deletion fails
     */
    public void deleteCase() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Delete Case");

        Long caseId = ConsoleHelper.readLong("Enter case ID to delete");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                ConsoleHelper.displayMessage("Case information to be deleted:");
                ConsoleHelper.displayMessage("ID: " + caseEntity.getId());
                ConsoleHelper.displayMessage("Case Number: " + caseEntity.getCaseNumber());
                ConsoleHelper.displayMessage("Title: " + caseEntity.getTitle());
                ConsoleHelper.displayHorizontalLine();

                boolean confirm = ConsoleHelper.readBoolean("Are you sure you want to delete this case?");

                if (confirm) {
                    caseService.deleteCase(caseId);
                    ConsoleHelper.displaySuccess("Case successfully deleted");
                } else {
                    ConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                ConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while deleting case: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Add a client to an existing case
     * @details Prompts for case ID then calls the helper method
     */
    public void addClientToCase() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Add Client to Case");

        Long caseId = ConsoleHelper.readLong("Enter case ID");
        addClientToCase(caseId);

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Helper method to add a client to a specific case
     * @param caseId The ID of the case to associate the client with
     * @throws Exception If client association fails
     */
    public void addClientToCase(Long caseId) {
        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                // Show current clients
                List<Client> currentClients = caseService.getClientsForCase(caseId);
                if (!currentClients.isEmpty()) {
                    ConsoleHelper.displayMessage("Clients already associated with the case:");
                    for (Client client : currentClients) {
                        ConsoleHelper.displayMessage("ID: " + client.getId() +
                                ", Name: " + client.getFullName());
                    }
                    ConsoleHelper.displayHorizontalLine();
                }

                // List all clients and make a selection
                List<Client> allClients = clientService.getAllClients();
                if (allClients.isEmpty()) {
                    ConsoleHelper.displayMessage("There are no registered clients in the system. You need to add a client first.");
                    return;
                }

                ConsoleHelper.displayMessage("Available clients:");
                for (Client client : allClients) {
                    ConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }

                Long clientId = ConsoleHelper.readLong("Enter client ID to add");

                Optional<Client> clientOpt = clientService.getClientById(clientId);
                if (clientOpt.isPresent()) {
                    Client client = clientOpt.get();

                    // Check if client is already added to the case
                    boolean alreadyAdded = currentClients.stream()
                            .anyMatch(c -> c.getId().equals(clientId));

                    if (alreadyAdded) {
                        ConsoleHelper.displayWarning("This client is already added to the case.");
                    } else {
                        caseService.addClientToCase(caseId, clientId);
                        ConsoleHelper.displaySuccess("Client successfully added to the case");
                    }
                } else {
                    ConsoleHelper.displayError("Client with specified ID not found.");
                }
            } else {
                ConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while adding client to case: " + e.getMessage());
        }
    }

    /**
     * @brief Remove a client association from a case
     * @details Displays currently associated clients and allows selection for removal
     * @throws Exception If client disassociation fails
     */
    public void removeClientFromCase() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Remove Client from Case");

        Long caseId = ConsoleHelper.readLong("Enter case ID");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                // Show clients associated with the case
                List<Client> clients = caseService.getClientsForCase(caseId);
                if (clients.isEmpty()) {
                    ConsoleHelper.displayMessage("There are no clients associated with this case.");
                    ConsoleHelper.pressEnterToContinue();
                    display();
                    return;
                }

                ConsoleHelper.displayMessage("Clients associated with the case:");
                for (Client client : clients) {
                    ConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }

                Long clientId = ConsoleHelper.readLong("Enter client ID to remove");

                // Check if client is really associated with this case
                boolean isClientInCase = clients.stream()
                        .anyMatch(c -> c.getId().equals(clientId));

                if (isClientInCase) {
                    caseService.removeClientFromCase(caseId, clientId);
                    ConsoleHelper.displaySuccess("Client successfully removed from the case");
                } else {
                    ConsoleHelper.displayError("The specified client is not associated with this case.");
                }
            } else {
                ConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while removing client from case: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief List all cases with a specific status
     * @details Displays a filtered list of cases by the selected status
     * @throws Exception If case retrieval fails
     */
    public void listCasesByStatus() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List Cases by Status");

        CaseStatus status = ConsoleHelper.readEnum("Select case status to list", CaseStatus.class);

        try {
            List<Case> cases = caseService.getCasesByStatus(status);

            if (cases.isEmpty()) {
                ConsoleHelper.displayMessage("No cases found with the specified status.");
            } else {
                ConsoleHelper.displayMessage("Found a total of " + cases.size() + " cases with status " + status + ":");
                ConsoleHelper.displayHorizontalLine();

                for (Case caseEntity : cases) {
                    ConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                            ", Case No: " + caseEntity.getCaseNumber() +
                            ", Title: " + caseEntity.getTitle() +
                            ", Type: " + caseEntity.getType());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing cases: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Display all cases in the system
     * @details Retrieves and lists all cases regardless of status
     * @throws Exception If case retrieval fails
     */
    public void viewAllCases() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List All Cases");

        try {
            List<Case> cases = caseService.getAllCases();

            if (cases.isEmpty()) {
                ConsoleHelper.displayMessage("There are no registered cases in the system.");
            } else {
                ConsoleHelper.displayMessage("Total " + cases.size() + " cases found:");
                ConsoleHelper.displayHorizontalLine();

                for (Case caseEntity : cases) {
                    ConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                            ", Case No: " + caseEntity.getCaseNumber() +
                            ", Title: " + caseEntity.getTitle() +
                            ", Status: " + caseEntity.getStatus() +
                            ", Type: " + caseEntity.getType());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing cases: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }
}