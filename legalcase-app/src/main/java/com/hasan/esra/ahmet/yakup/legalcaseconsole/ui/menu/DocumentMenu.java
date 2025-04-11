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
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;

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
    private final MenuManager menuManager;

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
     * @param menuManager Menu manager for navigation control
     * @param documentService Document service for document CRUD operations
     * @param caseService Case service for retrieving case information
     */
    public DocumentMenu(MenuManager menuManager, DocumentService documentService, CaseService caseService) {
        this.menuManager = menuManager;
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
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Document Archive");

        ConsoleHelper.displayMenuOption(1, "Add New Document");
        ConsoleHelper.displayMenuOption(2, "View Document Details");
        ConsoleHelper.displayMenuOption(3, "Update Document");
        ConsoleHelper.displayMenuOption(4, "Delete Document");
        ConsoleHelper.displayMenuOption(5, "Search Documents by Title");
        ConsoleHelper.displayMenuOption(6, "List Documents by Type");
        ConsoleHelper.displayMenuOption(7, "List Documents by Case");
        ConsoleHelper.displayMenuOption(8, "List All Documents");
        ConsoleHelper.displayMenuOption(9, "Return to Main Menu");
        ConsoleHelper.displayHorizontalLine();

        int choice = ConsoleHelper.readInt("Enter your choice", 1, 9);

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
                menuManager.navigateToMainMenu();
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
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Add New Document");

        try {
            // First select the case to associate with
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                ConsoleHelper.displayError("You must create a case first to add a document");
                ConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            ConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                ConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            ConsoleHelper.displayHorizontalLine();

            Long caseId = ConsoleHelper.readLong("Enter case ID to add document");

            Optional<Case> caseOpt = caseService.getCaseById(caseId);
            if (!caseOpt.isPresent()) {
                ConsoleHelper.displayError("Case with specified ID not found.");
                ConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            // Get document details
            String title = ConsoleHelper.readRequiredString("Enter document title");
            DocumentType type = ConsoleHelper.readEnum("Select document type", DocumentType.class);

            // Get document content (instead of actual file upload, text content)
            ConsoleHelper.displayMessage("Enter document content (can be multiple lines, enter an empty line when finished):");
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while (!(line = ConsoleHelper.readString("")).isEmpty()) {
                contentBuilder.append(line).append("\n");
            }
            String content = contentBuilder.toString().trim();

            Document document = documentService.createDocument(caseId, title, type, content);

            ConsoleHelper.displaySuccess("Document successfully added! ID: " + document.getId());
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while adding document: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View details of a specific document
     *
     * Retrieves and displays comprehensive information about a document,
     * including its metadata and complete content.
     */
    public void viewDocumentDetails() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("View Document Details");

        Long documentId = ConsoleHelper.readLong("Enter document ID");
        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);

            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();

                ConsoleHelper.displayHorizontalLine();
                ConsoleHelper.displayMessage("Document ID: " + document.getId());
                ConsoleHelper.displayMessage("Title: " + document.getTitle());
                ConsoleHelper.displayMessage("Type: " + document.getType());
                ConsoleHelper.displayMessage("Case: " + document.getCse().getCaseNumber() + " - " + document.getCse().getTitle());
                ConsoleHelper.displayMessage("Created At: " + document.getCreatedAt());
                ConsoleHelper.displayMessage("Last Updated: " + document.getUpdatedAt());

                // Show document content
                ConsoleHelper.displayHorizontalLine();
                ConsoleHelper.displayMessage("Document Content:");
                ConsoleHelper.displayMessage(document.getContent());
                ConsoleHelper.displayHorizontalLine();
            } else {
                ConsoleHelper.displayError("Document with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while retrieving document information: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
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
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Update Document");

        Long documentId = ConsoleHelper.readLong("Enter document ID to update");

        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);

            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();

                ConsoleHelper.displayMessage("Current information:");
                ConsoleHelper.displayMessage("Title: " + document.getTitle());
                ConsoleHelper.displayMessage("Type: " + document.getType());
                ConsoleHelper.displayMessage("Case: " + document.getCse().getCaseNumber() + " - " + document.getCse().getTitle());

                // Show a short content preview
                String contentPreview = document.getContent();
                if (contentPreview.length() > 100) {
                    contentPreview = contentPreview.substring(0, 100) + "...";
                }
                ConsoleHelper.displayMessage("Content Preview: " + contentPreview);
                ConsoleHelper.displayHorizontalLine();

                String title = ConsoleHelper.readString("Enter new title (leave empty for current value)");

                boolean updateType = ConsoleHelper.readBoolean("Do you want to change the document type?");
                DocumentType type = updateType ?
                        ConsoleHelper.readEnum("Select new document type", DocumentType.class) :
                        document.getType();

                boolean updateContent = ConsoleHelper.readBoolean("Do you want to update the document content?");
                String content = document.getContent();

                if (updateContent) {
                    ConsoleHelper.displayMessage("Enter new document content (can be multiple lines, enter an empty line when finished):");
                    StringBuilder contentBuilder = new StringBuilder();
                    String line;
                    while (!(line = ConsoleHelper.readString("")).isEmpty()) {
                        contentBuilder.append(line).append("\n");
                    }
                    content = contentBuilder.toString().trim();
                }

                title = title.isEmpty() ? document.getTitle() : title;

                documentService.updateDocument(documentId, title, type, content);

                ConsoleHelper.displaySuccess("Document successfully updated");
            } else {
                ConsoleHelper.displayError("Document with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while updating document: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a document
     *
     * Removes a document record after confirmation by the user.
     * Shows document details before deletion for verification.
     */
    public void deleteDocument() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Delete Document");

        Long documentId = ConsoleHelper.readLong("Enter document ID to delete");

        try {
            Optional<Document> documentOpt = documentService.getDocumentById(documentId);

            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();

                ConsoleHelper.displayMessage("Document information to be deleted:");
                ConsoleHelper.displayMessage("ID: " + document.getId());
                ConsoleHelper.displayMessage("Title: " + document.getTitle());
                ConsoleHelper.displayMessage("Type: " + document.getType());
                ConsoleHelper.displayMessage("Case: " + document.getCse().getCaseNumber() + " - " + document.getCse().getTitle());
                ConsoleHelper.displayHorizontalLine();

                boolean confirm = ConsoleHelper.readBoolean("Are you sure you want to delete this document?");

                if (confirm) {
                    documentService.deleteDocument(documentId);
                    ConsoleHelper.displaySuccess("Document successfully deleted");
                } else {
                    ConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                ConsoleHelper.displayError("Document with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while deleting document: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Search documents by title
     *
     * Performs a search operation based on document title keywords
     * and displays matching results with basic metadata.
     */
    public void searchDocumentsByTitle() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Search Documents by Title");

        String title = ConsoleHelper.readRequiredString("Enter title term to search");

        try {
            List<Document> documents = documentService.searchDocumentsByTitle(title);

            if (documents.isEmpty()) {
                ConsoleHelper.displayMessage("No documents found matching search criteria.");
            } else {
                ConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                ConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    ConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Type: " + document.getType() +
                            ", Case: " + document.getCse().getCaseNumber());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while searching documents: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief List documents by type
     *
     * Retrieves and displays a filtered list of documents based on
     * the selected document type (e.g., contract, court order, evidence).
     */
    public void listDocumentsByType() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List Documents by Type");

        DocumentType type = ConsoleHelper.readEnum("Select document type to list", DocumentType.class);

        try {
            List<Document> documents = documentService.getDocumentsByType(type);

            if (documents.isEmpty()) {
                ConsoleHelper.displayMessage("No documents found of the specified type.");
            } else {
                ConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                ConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    ConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Case: " + document.getCse().getCaseNumber());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing documents: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
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
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List Documents by Case");

        try {
            // First list all cases
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                ConsoleHelper.displayMessage("There are no registered cases in the system.");
                ConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            ConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                ConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            ConsoleHelper.displayHorizontalLine();

            Long caseId = ConsoleHelper.readLong("Enter case ID to list documents");

            List<Document> documents = documentService.getDocumentsByCaseId(caseId);

            if (documents.isEmpty()) {
                ConsoleHelper.displayMessage("There are no documents for this case.");
            } else {
                ConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                ConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    ConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Type: " + document.getType() +
                            ", Created At: " + document.getCreatedAt());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing documents: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View all documents
     *
     * Retrieves and displays a comprehensive list of all documents in the system,
     * including basic metadata for each document (ID, title, type, and associated case).
     */
    public void viewAllDocuments() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List All Documents");

        try {
            List<Document> documents = documentService.getAllDocuments();

            if (documents.isEmpty()) {
                ConsoleHelper.displayMessage("There are no registered documents in the system.");
            } else {
                ConsoleHelper.displayMessage("Total " + documents.size() + " documents found:");
                ConsoleHelper.displayHorizontalLine();

                for (Document document : documents) {
                    ConsoleHelper.displayMessage("ID: " + document.getId() +
                            ", Title: " + document.getTitle() +
                            ", Type: " + document.getType() +
                            ", Case: " + document.getCse().getCaseNumber());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing documents: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }
}