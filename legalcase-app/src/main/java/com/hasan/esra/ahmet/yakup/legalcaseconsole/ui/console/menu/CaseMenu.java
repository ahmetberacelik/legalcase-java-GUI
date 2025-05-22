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
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;

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
    private final ConsoleMenuManager consoleMenuManager;
    
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
     * @param consoleMenuManager The main menu navigation manager
     * @param caseService Service for case-related operations
     * @param clientService Service for client-related operations
     */
    public CaseMenu(ConsoleMenuManager consoleMenuManager, CaseService caseService, ClientService clientService) {
        this.consoleMenuManager = consoleMenuManager;
        this.caseService = caseService;
        this.clientService = clientService;
    }

    /**
     * @brief Display the case management menu
     * @details Shows all available case operations and processes user selection
     */
    public void display() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Case Management");

        UiConsoleHelper.displayMenuOption(1, "Add New Case");
        UiConsoleHelper.displayMenuOption(2, "View Case Details");
        UiConsoleHelper.displayMenuOption(3, "Update Case");
        UiConsoleHelper.displayMenuOption(4, "Delete Case");
        UiConsoleHelper.displayMenuOption(5, "Add Client to Case");
        UiConsoleHelper.displayMenuOption(6, "Remove Client from Case");
        UiConsoleHelper.displayMenuOption(7, "List Cases by Status");
        UiConsoleHelper.displayMenuOption(8, "List All Cases");
        UiConsoleHelper.displayMenuOption(9, "Return to Main Menu");
        UiConsoleHelper.displayHorizontalLine();

        int choice = UiConsoleHelper.readInt("Enter your choice", 1, 9);

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
                consoleMenuManager.navigateToMainMenu();
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
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Add New Case");

        try {
            String caseNumber = UiConsoleHelper.readRequiredString("Enter case number");
            String title = UiConsoleHelper.readRequiredString("Enter title");
            CaseType type = UiConsoleHelper.readEnum("Select case type", CaseType.class);
            String description = UiConsoleHelper.readString("Enter description");

            Case caseEntity = caseService.createCase(caseNumber, title, type, description);

            UiConsoleHelper.displaySuccess("Case successfully created! ID: " + caseEntity.getId());

            // Ask if user wants to add client to the case
            boolean addClient = UiConsoleHelper.readBoolean("Do you want to add a client to this case?");
            if (addClient) {
                addClientToCase(caseEntity.getId());
            }

        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while creating case: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View detailed information about a specific case
     * @details Retrieves and displays complete case information including
     *          associated clients and case status
     */
    public void viewCaseDetails() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("View Case Details");

        Long caseId = UiConsoleHelper.readLong("Enter case ID");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                UiConsoleHelper.displayHorizontalLine();
                UiConsoleHelper.displayMessage("Case ID: " + caseEntity.getId());
                UiConsoleHelper.displayMessage("Case Number: " + caseEntity.getCaseNumber());
                UiConsoleHelper.displayMessage("Title: " + caseEntity.getTitle());
                UiConsoleHelper.displayMessage("Type: " + caseEntity.getType());
                UiConsoleHelper.displayMessage("Status: " + caseEntity.getStatus());
                UiConsoleHelper.displayMessage("Description: " + caseEntity.getDescription());
                UiConsoleHelper.displayMessage("Created At: " + caseEntity.getCreatedAt());
                UiConsoleHelper.displayMessage("Last Updated: " + caseEntity.getUpdatedAt());

                // Show clients associated with the case
                List<Client> clients = caseService.getClientsForCase(caseId);
                if (clients.isEmpty()) {
                    UiConsoleHelper.displayMessage("There are no clients associated with this case.");
                } else {
                    UiConsoleHelper.displayMessage("\nClients associated with the case:");
                    for (Client client : clients) {
                        UiConsoleHelper.displayMessage("ID: " + client.getId() +
                                ", Name: " + client.getFullName() +
                                ", Email: " + client.getEmail());
                    }
                }

                UiConsoleHelper.displayHorizontalLine();
            } else {
                UiConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while retrieving case information: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
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
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Update Case");

        Long caseId = UiConsoleHelper.readLong("Enter case ID to update");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                UiConsoleHelper.displayMessage("Current information:");
                UiConsoleHelper.displayMessage("Case Number: " + caseEntity.getCaseNumber());
                UiConsoleHelper.displayMessage("Title: " + caseEntity.getTitle());
                UiConsoleHelper.displayMessage("Type: " + caseEntity.getType());
                UiConsoleHelper.displayMessage("Status: " + caseEntity.getStatus());
                UiConsoleHelper.displayMessage("Description: " + caseEntity.getDescription());
                UiConsoleHelper.displayHorizontalLine();

                String caseNumber = UiConsoleHelper.readString("Enter new case number (leave empty for current value)");
                String title = UiConsoleHelper.readString("Enter new title (leave empty for current value)");

                // Ask if user wants to change the case type
                boolean updateType = UiConsoleHelper.readBoolean("Do you want to change the case type?");
                CaseType type = updateType ?
                        UiConsoleHelper.readEnum("Select new case type", CaseType.class) :
                        caseEntity.getType();

                // Ask if user wants to change the case status
                boolean updateStatus = UiConsoleHelper.readBoolean("Do you want to change the case status?");
                CaseStatus status = updateStatus ?
                        UiConsoleHelper.readEnum("Select new case status", CaseStatus.class) :
                        caseEntity.getStatus();

                String description = UiConsoleHelper.readString("Enter new description (leave empty for current value)");

                caseNumber = caseNumber.isEmpty() ? caseEntity.getCaseNumber() : caseNumber;
                title = title.isEmpty() ? caseEntity.getTitle() : title;
                description = description.isEmpty() ? caseEntity.getDescription() : description;

                caseService.updateCase(caseId, caseNumber, title, type, description, status);

                UiConsoleHelper.displaySuccess("Case successfully updated");
            } else {
                UiConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while updating case: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a case from the system
     * @details Shows case information for confirmation before permanent deletion
     * @throws Exception If deletion fails
     */
    public void deleteCase() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Delete Case");

        Long caseId = UiConsoleHelper.readLong("Enter case ID to delete");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                UiConsoleHelper.displayMessage("Case information to be deleted:");
                UiConsoleHelper.displayMessage("ID: " + caseEntity.getId());
                UiConsoleHelper.displayMessage("Case Number: " + caseEntity.getCaseNumber());
                UiConsoleHelper.displayMessage("Title: " + caseEntity.getTitle());
                UiConsoleHelper.displayHorizontalLine();

                boolean confirm = UiConsoleHelper.readBoolean("Are you sure you want to delete this case?");

                if (confirm) {
                    caseService.deleteCase(caseId);
                    UiConsoleHelper.displaySuccess("Case successfully deleted");
                } else {
                    UiConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                UiConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while deleting case: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Add a client to an existing case
     * @details Prompts for case ID then calls the helper method
     */
    public void addClientToCase() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Add Client to Case");

        Long caseId = UiConsoleHelper.readLong("Enter case ID");
        addClientToCase(caseId);

        UiConsoleHelper.pressEnterToContinue();
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
                    UiConsoleHelper.displayMessage("Clients already associated with the case:");
                    for (Client client : currentClients) {
                        UiConsoleHelper.displayMessage("ID: " + client.getId() +
                                ", Name: " + client.getFullName());
                    }
                    UiConsoleHelper.displayHorizontalLine();
                }

                // List all clients and make a selection
                List<Client> allClients = clientService.getAllClients();
                if (allClients.isEmpty()) {
                    UiConsoleHelper.displayMessage("There are no registered clients in the system. You need to add a client first.");
                    return;
                }

                UiConsoleHelper.displayMessage("Available clients:");
                for (Client client : allClients) {
                    UiConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }

                Long clientId = UiConsoleHelper.readLong("Enter client ID to add");

                Optional<Client> clientOpt = clientService.getClientById(clientId);
                if (clientOpt.isPresent()) {
                    Client client = clientOpt.get();

                    // Check if client is already added to the case
                    boolean alreadyAdded = currentClients.stream()
                            .anyMatch(c -> c.getId().equals(clientId));

                    if (alreadyAdded) {
                        UiConsoleHelper.displayWarning("This client is already added to the case.");
                    } else {
                        caseService.addClientToCase(caseId, clientId);
                        UiConsoleHelper.displaySuccess("Client successfully added to the case");
                    }
                } else {
                    UiConsoleHelper.displayError("Client with specified ID not found.");
                }
            } else {
                UiConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while adding client to case: " + e.getMessage());
        }
    }

    /**
     * @brief Remove a client association from a case
     * @details Displays currently associated clients and allows selection for removal
     * @throws Exception If client disassociation fails
     */
    public void removeClientFromCase() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Remove Client from Case");

        Long caseId = UiConsoleHelper.readLong("Enter case ID");

        try {
            Optional<Case> caseOpt = caseService.getCaseById(caseId);

            if (caseOpt.isPresent()) {
                Case caseEntity = caseOpt.get();

                // Show clients associated with the case
                List<Client> clients = caseService.getClientsForCase(caseId);
                if (clients.isEmpty()) {
                    UiConsoleHelper.displayMessage("There are no clients associated with this case.");
                    UiConsoleHelper.pressEnterToContinue();
                    display();
                    return;
                }

                UiConsoleHelper.displayMessage("Clients associated with the case:");
                for (Client client : clients) {
                    UiConsoleHelper.displayMessage("ID: " + client.getId() +
                            ", Name: " + client.getFullName() +
                            ", Email: " + client.getEmail());
                }

                Long clientId = UiConsoleHelper.readLong("Enter client ID to remove");

                // Check if client is really associated with this case
                boolean isClientInCase = clients.stream()
                        .anyMatch(c -> c.getId().equals(clientId));

                if (isClientInCase) {
                    caseService.removeClientFromCase(caseId, clientId);
                    UiConsoleHelper.displaySuccess("Client successfully removed from the case");
                } else {
                    UiConsoleHelper.displayError("The specified client is not associated with this case.");
                }
            } else {
                UiConsoleHelper.displayError("Case with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while removing client from case: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief List all cases with a specific status
     * @details Displays a filtered list of cases by the selected status
     * @throws Exception If case retrieval fails
     */
    public void listCasesByStatus() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List Cases by Status");

        CaseStatus status = UiConsoleHelper.readEnum("Select case status to list", CaseStatus.class);

        try {
            List<Case> cases = caseService.getCasesByStatus(status);

            if (cases.isEmpty()) {
                UiConsoleHelper.displayMessage("No cases found with the specified status.");
            } else {
                UiConsoleHelper.displayMessage("Found a total of " + cases.size() + " cases with status " + status + ":");
                UiConsoleHelper.displayHorizontalLine();

                for (Case caseEntity : cases) {
                    UiConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                            ", Case No: " + caseEntity.getCaseNumber() +
                            ", Title: " + caseEntity.getTitle() +
                            ", Type: " + caseEntity.getType());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing cases: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Display all cases in the system
     * @details Retrieves and lists all cases regardless of status
     * @throws Exception If case retrieval fails
     */
    public void viewAllCases() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List All Cases");

        try {
            List<Case> cases = caseService.getAllCases();

            if (cases.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no registered cases in the system.");
            } else {
                UiConsoleHelper.displayMessage("Total " + cases.size() + " cases found:");
                UiConsoleHelper.displayHorizontalLine();

                for (Case caseEntity : cases) {
                    UiConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                            ", Case No: " + caseEntity.getCaseNumber() +
                            ", Title: " + caseEntity.getTitle() +
                            ", Status: " + caseEntity.getStatus() +
                            ", Type: " + caseEntity.getType());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing cases: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }
}