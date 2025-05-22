/**
 * @file DocumentMenu.java
 * @brief Document management menu class for the Legal Case Tracker system
 *
 * This file contains the DocumentMenu class which provides the user interface
 * for managing legal documents, including adding, viewing, updating, deleting,
 * and searching documents. Documents are categorized by type and associated with
 * specific legal cases.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;

import java.util.List;
import java.util.Optional;

/**
 * @brief Menu for document management
 *
 * This class provides a user interface for managing legal documents, including
 * adding, viewing, updating, deleting, and searching documents. Documents are
 * categorized by type and associated with specific legal cases.
 */
public class DocumentMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final ConsoleMenuManager consoleMenuManager;
    
    /**
     * @brief Document service for document operations
     * @details Handles CRUD operations and business logic for legal documents
     */
    private final DocumentService documentService;
    
    /**
     * @brief Case service for case operations
     * @details Used to retrieve case information when associating documents with cases
     */
    private final CaseService caseService;

    /**
     * @brief Constructor
     *
     * @param consoleMenuManager Menu manager for navigation control
     * @param documentService Document service for document CRUD operations
     * @param caseService Case service for retrieving case information
     */
    public DocumentMenu(ConsoleMenuManager consoleMenuManager, DocumentService documentService, CaseService caseService) {
        this.consoleMenuManager = consoleMenuManager;
        this.documentService = documentService;
        this.caseService = caseService;
    }

    /**
     * @brief Display the document menu
     *
     * Shows all available document management options and handles user selection.
     * The menu includes options for adding, viewing, updating, deleting, and
     * searching documents, as well as filtering by various criteria.
     */
    public void display() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Document Archive");

        UiConsoleHelper.displayMenuOption(1, "Add New Document");
        UiConsoleHelper.displayMenuOption(2, "View Document Details");
        UiConsoleHelper.displayMenuOption(3, "Update Document");
        UiConsoleHelper.displayMenuOption(4, "Delete Document");
        UiConsoleHelper.displayMenuOption(5, "Search Documents by Title");
        UiConsoleHelper.displayMenuOption(6, "List Documents by Type");
        UiConsoleHelper.displayMenuOption(7, "List Documents by Case");
        UiConsoleHelper.displayMenuOption(8, "List All Documents");
        UiConsoleHelper.displayMenuOption(9, "Return to Main Menu");
        UiConsoleHelper.displayHorizontalLine();

        int choice = UiConsoleHelper.readInt("Enter your choice", 1, 9);

        switch (choice) {
            case 1:
                addDocument();
                break;
            case 2:
                viewDocumentDetails();
                break;
            case 3:
                updateDocument();
                break;
            case 4:
                deleteDocument();
                break;
            case 5:
                searchDocumentsByTitle();
                break;
            case 6:
                listDocumentsByType();
                break;
            case 7:
                listDocumentsByCase();
                break;
            case 8:
                viewAllDocuments();
                break;
            case 9:
                consoleMenuManager.navigateToMainMenu();
                break;
        }
    }

    /**
     * @brief Add a new document
     *
     * Collects document information including title, type, and content from user input
     * and associates it with an existing case. Verifies that at least one case
     * exists before allowing document creation.
     */
    public void addDocument() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Add New Document");

        try {
            // First select the case to associate with
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                UiConsoleHelper.displayError("You must create a case first to add a document");
                UiConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            UiConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                UiConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            UiConsoleHelper.displayHorizontalLine();

            Long caseId = UiConsoleHelper.readLong("Enter case ID to add document");

            Optional<Case> caseOpt = caseService.getCaseById(caseId);
            if (!caseOpt.isPresent()) {
                UiConsoleHelper.displayError("Case with specified ID not found.");
                UiConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            // Get document details
            String title = UiConsoleHelper.readRequiredString("Enter document title");
            DocumentType type = UiConsoleHelper.readEnum("Select document type", DocumentType.class);

            // Get document content (instead of actual file upload, text content)
            UiConsoleHelper.displayMessage("Enter document content (can be multiple lines, enter an empty line when finished):");
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while (!(line = UiConsoleHelper.readString("")).isEmpty()) {
                contentBuilder.append(line).append("\n");
            }
            String content = contentBuilder.toString().trim();

            Document document = documentService.createDocument(caseId, title, type, content);

            UiConsoleHelper.displaySuccess("Document successfully added! ID: " + document.getId());
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while adding document: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View details of a specific document
     *
     * Retrieves and displays comprehensive information about a document,
     * including its metadata and complete content.
     */
    public void viewDocumentDetails() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("View Document Details");

        Long documentId = UiConsoleHelper.readLong("Enter document ID");
        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);

            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();

                UiConsoleHelper.displayHorizontalLine();
                UiConsoleHelper.displayMessage("Document ID: " + document.getId());
                UiConsoleHelper.displayMessage("Title: " + document.getTitle());
                UiConsoleHelper.displayMessage("Type: " + document.getType());
                UiConsoleHelper.displayMessage("Case: " + document.getCse().getCaseNumber() + " - " + document.getCse().getTitle());
                UiConsoleHelper.displayMessage("Created At: " + document.getCreatedAt());
                UiConsoleHelper.displayMessage("Last Updated: " + document.getUpdatedAt());

                // Show document content
                UiConsoleHelper.displayHorizontalLine();
                UiConsoleHelper.displayMessage("Document Content:");
                UiConsoleHelper.displayMessage(document.getContent());
                UiConsoleHelper.displayHorizontalLine();
            } else {
                UiConsoleHelper.displayError("Document with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while retrieving document information: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update an existing document
     *
     * Allows modification of document details by showing current information
     * and collecting updated information from user input. Users can selectively
     * update title, type, and content while preserving unchanged fields.
     */
    public void updateDocument() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Update Document");

        Long documentId = UiConsoleHelper.readLong("Enter document ID to update");

        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);

            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();

                UiConsoleHelper.displayMessage("Current information:");
                UiConsoleHelper.displayMessage("Title: " + document.getTitle());
                UiConsoleHelper.displayMessage("Type: " + document.getType());
                UiConsoleHelper.displayMessage("Case: " + document.getCse().getCaseNumber() + " - " + document.getCse().getTitle());

                // Show a short content preview
                String contentPreview = document.getContent();
                if (contentPreview.length() > 100) {
                    contentPreview = contentPreview.substring(0, 100) + "...";
                }
                UiConsoleHelper.displayMessage("Content Preview: " + contentPreview);
                UiConsoleHelper.displayHorizontalLine();

                String title = UiConsoleHelper.readString("Enter new title (leave empty for current value)");

                boolean updateType = UiConsoleHelper.readBoolean("Do you want to change the document type?");
                DocumentType type = updateType ?
                        UiConsoleHelper.readEnum("Select new document type", DocumentType.class) :
                        document.getType();

                boolean updateContent = UiConsoleHelper.readBoolean("Do you want to update the document content?");
                String content = document.getContent();

                if (updateContent) {
                    UiConsoleHelper.displayMessage("Enter new document content (can be multiple lines, enter an empty line when finished):");
                    StringBuilder contentBuilder = new StringBuilder();
                    String line;
                    while (!(line = UiConsoleHelper.readString("")).isEmpty()) {
                        contentBuilder.append(line).append("\n");
                    }
                    content = contentBuilder.toString().trim();
                }

                title = title.isEmpty() ? document.getTitle() : title;

                documentService.updateDocument(documentId, title, type, content);

                UiConsoleHelper.displaySuccess("Document successfully updated");
            } else {
                UiConsoleHelper.displayError("Document with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while updating document: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a document
     *
     * Removes a document record after confirmation by the user.
     * Shows document details before deletion for verification.
     */
    public void deleteDocument() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Delete Document");

        Long documentId = UiConsoleHelper.readLong("Enter document ID to delete");

        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);

            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();

                UiConsoleHelper.displayMessage("Document information to be deleted:");
                UiConsoleHelper.displayMessage("ID: " + document.getId());
                UiConsoleHelper.displayMessage("Title: " + document.getTitle());
                UiConsoleHelper.displayMessage("Type: " + document.getType());
                UiConsoleHelper.displayMessage("Case: " + document.getCse().getCaseNumber() + " - " + document.getCse().getTitle());
                UiConsoleHelper.displayHorizontalLine();

                boolean confirm = UiConsoleHelper.readBoolean("Are you sure you want to delete this document?");

                if (confirm) {
                    documentService.deleteDocument(documentId);
                    UiConsoleHelper.displaySuccess("Document successfully deleted");
                } else {
                    UiConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                UiConsoleHelper.displayError("Document with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while deleting document: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Search documents by title
     *
     * Performs a search operation based on document title keywords
     * and displays matching results with basic metadata.
     */
    public void searchDocumentsByTitle() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Search Documents by Title");

        String title = UiConsoleHelper.readRequiredString("Enter title term to search");

        try {
            List<Document> documents = documentService.searchDocumentsByTitle(title);

            if (documents.isEmpty()) {
                UiConsoleHelper.displayMessage("No documents found matching search criteria.");
            } else {
                UiConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                UiConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    UiConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Type: " + document.getType() +
                            ", Case: " + document.getCse().getCaseNumber());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while searching documents: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief List documents by type
     *
     * Retrieves and displays a filtered list of documents based on
     * the selected document type (e.g., contract, court order, evidence).
     */
    public void listDocumentsByType() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List Documents by Type");

        DocumentType type = UiConsoleHelper.readEnum("Select document type to list", DocumentType.class);

        try {
            List<Document> documents = documentService.getDocumentsByType(type);

            if (documents.isEmpty()) {
                UiConsoleHelper.displayMessage("No documents found of the specified type.");
            } else {
                UiConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                UiConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    UiConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Case: " + document.getCse().getCaseNumber());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing documents: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief List documents by case
     *
     * Displays available cases and allows the user to select a specific case
     * to view all associated documents. Verifies that at least one case exists
     * in the system before proceeding.
     */
    public void listDocumentsByCase() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List Documents by Case");

        try {
            // First list all cases
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no registered cases in the system.");
                UiConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            UiConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                UiConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            UiConsoleHelper.displayHorizontalLine();

            Long caseId = UiConsoleHelper.readLong("Enter case ID to list documents");

            List<Document> documents = documentService.getDocumentsByCaseId(caseId);

            if (documents.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no documents for this case.");
            } else {
                UiConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                UiConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    UiConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Type: " + document.getType() +
                            ", Created At: " + document.getCreatedAt());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing documents: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View all documents
     *
     * Retrieves and displays a comprehensive list of all documents in the system,
     * including basic metadata for each document (ID, title, type, and associated case).
     */
    public void viewAllDocuments() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List All Documents");

        try {
            List<Document> documents = documentService.getAllDocuments();

            if (documents.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no registered documents in the system.");
            } else {
                UiConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                UiConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    UiConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Type: " + document.getType() +
                            ", Case: " + document.getCse().getCaseNumber());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing documents: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }
}