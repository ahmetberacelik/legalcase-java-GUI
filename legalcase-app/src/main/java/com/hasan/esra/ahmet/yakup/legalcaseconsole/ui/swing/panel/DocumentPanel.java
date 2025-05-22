package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Optional;

/**
 * Document management panel for the legal case management system
 * 
 * This class provides a comprehensive user interface for managing legal documents
 * within the legal case management application. It enables users to perform CRUD
 * operations (Create, Read, Update, Delete) on document records with an intuitive
 * graphical interface.
 * 
 * The panel includes functionality for:
 * - Viewing a list of all legal documents in a sortable table
 * - Searching for specific documents using various criteria
 * - Adding new document records to the system
 * - Editing existing document information
 * - Deleting documents from the database
 * - Associating documents with specific legal cases
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class DocumentPanel extends JPanel {
    /**
     * Service for document data operations
     * Handles all business logic and database interactions related to legal documents
     */
    private final DocumentService documentService;
    
    /**
     * Service for case data operations
     * Used to fetch case information when associating documents with cases
     */
    private final CaseService caseService;
    
    /**
     * Reference to the main application frame
     * Used for navigation between panels and maintaining application context
     */
    private final MainFrame mainFrame;
    
    /**
     * Table component for displaying document records
     * Shows all document data in a structured tabular format
     */
    private JTable documentTable;
    
    /**
     * Table model for managing the data displayed in the document table
     * Controls the structure, content, and behavior of the table
     */
    private DefaultTableModel tableModel;
    
    /**
     * Text field for entering search queries
     * Used to filter documents based on user input
     */
    private JTextField searchField;
    
    /**
     * Button to initiate search operations
     * Triggers the filtering of document records based on search criteria
     */
    private JButton searchButton;

    /**
     * Constructs a new DocumentPanel with necessary dependencies
     * 
     * Initializes the document management panel with services and navigation dependencies,
     * sets up the user interface components, and loads the initial document data.
     *
     * @param documentService Service for document data operations and business logic
     * @param caseService Service for case data operations when linking documents to cases
     * @param mainFrame Reference to the main application frame for navigation
     */
    public DocumentPanel(DocumentService documentService, CaseService caseService, MainFrame mainFrame) {
        this.documentService = documentService;
        this.caseService = caseService;
        this.mainFrame = mainFrame;
        initializeUI();
        loadDocuments();
        
        // Apply theme to this panel
        LegalTheme.applyPanelStyle(this);
    }

    /**
     * Initializes the user interface components
     * 
     * Sets up the layout, creates and configures all UI components including
     * the document table, search panel, and action buttons. Organizes components
     * into a cohesive and user-friendly interface following the application's
     * design guidelines.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(LegalTheme.BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = LegalTheme.createHeaderPanel("Document Management");
        add(headerPanel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton addButton = new JButton("Add New Document");
        JButton editButton = new JButton("Edit Document");
        JButton deleteButton = new JButton("Delete Document");
        JButton backButton = new JButton("Back to Menu");

        // Apply styles to buttons
        LegalTheme.applyButtonStyle(addButton);
        LegalTheme.applyButtonStyle(editButton);
        LegalTheme.applySecondaryButtonStyle(deleteButton);
        LegalTheme.applyButtonStyle(backButton);

        // Add action listeners
        addButton.addActionListener(e -> showAddDocumentDialog());
        editButton.addActionListener(e -> showEditDocumentDialog());
        deleteButton.addActionListener(e -> showDeleteDocumentDialog());
        backButton.addActionListener(e -> mainFrame.showPanel("mainMenu"));

        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Create table model with column names
        tableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"ID", "Case Number", "Title", "Type", "Content Type"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        documentTable = new JTable(tableModel);
        LegalTheme.applyTableStyle(documentTable);
        
        JScrollPane scrollPane = new JScrollPane(documentTable);
        LegalTheme.applyScrollPaneStyle(scrollPane);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LegalTheme.NORMAL_FONT);
        searchLabel.setForeground(LegalTheme.TEXT_COLOR);
        
        searchField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(searchField);
        
        searchButton = new JButton("Search");
        LegalTheme.applyButtonStyle(searchButton);
        searchButton.addActionListener(e -> searchDocuments());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create a panel for the table with a title
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
            "Document List",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LegalTheme.HEADER_FONT,
            LegalTheme.PRIMARY_COLOR));
        tablePanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Add components to main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Loads all document records from the service into the table
     * 
     * Retrieves document data from the document service, clears the current table,
     * and populates it with the latest document information. Also configures the
     * column widths for optimal display of document data.
     */
    private void loadDocuments() {
        tableModel.setRowCount(0);
        List<Document> documents = documentService.getAllDocuments();
        for (Document document : documents) {
            tableModel.addRow(new Object[]{
                document.getId(),
                document.getCse() != null ? document.getCse().getCaseNumber() : "",
                document.getTitle(),
                document.getType(),
                document.getContentType()
            });
        }
        
        // Adjust column widths
        if (documentTable.getWidth() > 0) {
            // Percentage widths for ID, Case No, Title, Type, Content Type
            int[] columnWidths = {5, 20, 35, 20, 20};
            LegalTheme.setColumnWidths(documentTable, columnWidths);
        } else {
            // If the table is not yet visible, add a listener that will run when the component is visible.
            documentTable.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int[] columnWidths = {5, 20, 35, 20, 20};
                    LegalTheme.setColumnWidths(documentTable, columnWidths);
                    // Remove Listener, only run once
                    documentTable.removeComponentListener(this);
                }
            });
        }
    }

    /**
     * Displays a dialog for adding a new document
     * 
     * Creates and shows a modal dialog with form fields for entering
     * new document information, including selecting an associated case.
     * Validates input data before saving to ensure data integrity and
     * provides feedback on successful creation or errors.
     */
    private void showAddDocumentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Document", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Case selection
        JComboBox<Case> caseComboBox = new JComboBox<>();
        List<Case> cases = caseService.getAllCases();
        for (Case caseEntity : cases) {
            caseComboBox.addItem(caseEntity);
        }

        JTextField titleField = new JTextField(20);
        JComboBox<DocumentType> typeComboBox = new JComboBox<>(DocumentType.values());
        JTextField contentTypeField = new JTextField(20);
        JTextArea contentArea = new JTextArea(5, 20);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Case:"), gbc);
        gbc.gridx = 1;
        dialog.add(caseComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Content Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(contentTypeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1;
        dialog.add(contentScroll, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                Case selectedCase = (Case) caseComboBox.getSelectedItem();
                String title = titleField.getText();
                DocumentType type = (DocumentType) typeComboBox.getSelectedItem();
                String contentType = contentTypeField.getText();
                String content = contentArea.getText();

                documentService.createDocument(selectedCase.getId(), title, type, content);
                loadDocuments();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Document added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding document: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays a dialog for editing an existing document
     * 
     * Creates and shows a modal dialog with pre-populated form fields
     * for modifying the selected document's information. Validates the modified
     * data before saving and provides feedback on success or failure.
     * Shows an error message if no document is selected or the document is not found.
     */
    private void showEditDocumentDialog() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to edit",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long documentId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Document> documentOpt = documentService.getDocumentById(documentId);

        if (documentOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Document not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Document document = documentOpt.get();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Document", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField titleField = new JTextField(document.getTitle(), 20);
        JComboBox<DocumentType> typeComboBox = new JComboBox<>(DocumentType.values());
        typeComboBox.setSelectedItem(document.getType());
        JTextField contentTypeField = new JTextField(document.getContentType(), 20);
        JTextArea contentArea = new JTextArea(document.getContent(), 5, 20);
        JScrollPane contentScroll = new JScrollPane(contentArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Content Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(contentTypeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1;
        dialog.add(contentScroll, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                DocumentType type = (DocumentType) typeComboBox.getSelectedItem();
                String contentType = contentTypeField.getText();
                String content = contentArea.getText();

                documentService.updateDocument(documentId, title, type, content);
                loadDocuments();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Document updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating document: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays a confirmation dialog for deleting a document
     * 
     * Creates and shows a modal confirmation dialog for the selected document.
     * If confirmed, removes the document from the system and updates the table.
     * Shows an error message if no document is selected or the document is not found.
     */
    private void showDeleteDocumentDialog() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long documentId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Document> documentOpt = documentService.getDocumentById(documentId);

        if (documentOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Document not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Document document = documentOpt.get();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this document?\nTitle: " + document.getTitle(),
                "Delete Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                documentService.deleteDocument(documentId);
                loadDocuments();
                JOptionPane.showMessageDialog(this, "Document deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting document: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Searches for documents matching the search criteria
     * 
     * Filters the document list based on the text entered in the search field.
     * Searches across multiple document attributes (title, type, content type,
     * and associated case number) and updates the table to show only matching documents.
     * If the search field is empty, reloads all documents.
     */
    private void searchDocuments() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadDocuments();
            return;
        }

        tableModel.setRowCount(0);
        List<Document> documents = documentService.getAllDocuments();
        for (Document document : documents) {
            String title = document.getTitle() != null ? document.getTitle().toLowerCase() : "";
            String type = document.getType() != null ? document.getType().toString().toLowerCase() : "";
            String contentType = document.getContentType() != null ? document.getContentType().toLowerCase() : "";
            String caseNumber = document.getCse() != null ? document.getCse().getCaseNumber().toLowerCase() : "";
            
            if (title.contains(searchTerm) || 
                type.contains(searchTerm) || 
                contentType.contains(searchTerm) || 
                caseNumber.contains(searchTerm)) {
                tableModel.addRow(new Object[]{
                    document.getId(),
                    document.getCse() != null ? document.getCse().getCaseNumber() : "",
                    document.getTitle(),
                    document.getType(),
                    document.getContentType()
                });
            }
        }
        
        // Adjust column widths for search results too
        int[] columnWidths = {5, 20, 35, 20, 20};
        LegalTheme.setColumnWidths(documentTable, columnWidths);
    }
}