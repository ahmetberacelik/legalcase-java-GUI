package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.util.List;
import java.util.Optional;

/**
 * Client management panel for the legal case management system
 * 
 * This class provides a comprehensive user interface for managing client records
 * within the legal case management application. It enables users to perform CRUD
 * operations (Create, Read, Update, Delete) on client data through an intuitive
 * graphical interface.
 * 
 * The panel includes functionality for:
 * - Viewing a list of all clients in a sortable table
 * - Searching for specific clients using various criteria
 * - Adding new client records to the system
 * - Editing existing client information
 * - Deleting clients from the database
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class ClientPanel extends JPanel {
    /**
     * Service for client data operations
     * Handles all business logic and database interactions related to clients
     */
    private final ClientService clientService;
    
    /**
     * Reference to the main application frame
     * Used for navigation between panels and maintaining application context
     */
    private final MainFrame mainFrame;
    
    /**
     * Table component for displaying client records
     * Shows all client data in a structured tabular format
     */
    private JTable clientTable;
    
    /**
     * Table model for managing the data displayed in the client table
     * Controls the structure, content, and behavior of the table
     */
    private DefaultTableModel tableModel;
    
    /**
     * Text field for entering search queries
     * Used to filter clients based on user input
     */
    private JTextField searchField;
    
    /**
     * Button to initiate search operations
     * Triggers the filtering of client records based on search criteria
     */
    private JButton searchButton;
    
    /**
     * Button to open the add client dialog
     * Allows users to create new client records
     */
    private JButton addButton;
    
    /**
     * Button to open the edit client dialog
     * Enables modification of selected client information
     */
    private JButton editButton;
    
    /**
     * Button to delete the selected client
     * Removes client records after confirmation
     */
    private JButton deleteButton;
    
    /**
     * Button to return to the main menu
     * Navigates away from the client management panel
     */
    private JButton backButton;

    /**
     * Constructs a new ClientPanel with necessary dependencies
     * 
     * Initializes the client management panel with service and navigation dependencies,
     * sets up the user interface components, and loads the initial client data.
     *
     * @param clientService Service for client data operations and business logic
     * @param mainFrame Reference to the main application frame for navigation
     */
    public ClientPanel(ClientService clientService, MainFrame mainFrame) {
        this.clientService = clientService;
        this.mainFrame = mainFrame;
        initializeUI();
        loadClients();
        
        // Apply theme to this panel
        LegalTheme.applyPanelStyle(this);
    }

    /**
     * Initializes the user interface components
     * 
     * Sets up the layout, creates and configures all UI components including
     * the client table, search panel, and action buttons. Organizes components
     * into a cohesive and user-friendly interface following the application's
     * design guidelines.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(LegalTheme.BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = LegalTheme.createHeaderPanel("Client Management");
        add(headerPanel, BorderLayout.NORTH);

        // Create table model with column names
        tableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"ID", "Name", "Surname", "Email", "Phone", "Address"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table and add to scroll pane
        clientTable = new JTable(tableModel);
        LegalTheme.applyTableStyle(clientTable);
        
        JScrollPane scrollPane = new JScrollPane(clientTable);
        LegalTheme.applyScrollPaneStyle(scrollPane);
        
        // Create a panel for the table with a title
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
            "Client List",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LegalTheme.HEADER_FONT,
            LegalTheme.PRIMARY_COLOR));
        tablePanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LegalTheme.NORMAL_FONT);
        searchField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(searchField);
        
        searchButton = new JButton("Search");
        LegalTheme.applyButtonStyle(searchButton);
        searchButton.addActionListener(e -> searchClients());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        addButton = new JButton("Add Client");
        editButton = new JButton("Edit Client");
        deleteButton = new JButton("Delete Client");
        backButton = new JButton("Back to Menu");

        // Apply styles to buttons
        LegalTheme.applyButtonStyle(addButton);
        LegalTheme.applyButtonStyle(editButton);
        LegalTheme.applySecondaryButtonStyle(deleteButton);
        LegalTheme.applyButtonStyle(backButton);

        // Add action listeners
        addButton.addActionListener(e -> showAddClientDialog());
        editButton.addActionListener(e -> showEditClientDialog());
        deleteButton.addActionListener(e -> showDeleteClientDialog());
        backButton.addActionListener(e -> mainFrame.showPanel("mainMenu"));

        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Add button panel to main panel
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads all client records from the service into the table
     * 
     * Retrieves client data from the client service, clears the current table,
     * and populates it with the latest client information. Also configures the
     * column widths for optimal display of client data.
     */
    private void loadClients() {
        tableModel.setRowCount(0);
        List<Client> clients = clientService.getAllClients();
        for (Client client : clients) {
            tableModel.addRow(new Object[]{
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress()
            });
        }
        
        // Adjust column widths
        if (clientTable.getWidth() > 0) {
            // Percentile widths for ID, Name, Surname, Email, Phone, Address
            int[] columnWidths = {5, 15, 15, 25, 15, 25};
            LegalTheme.setColumnWidths(clientTable, columnWidths);
        } else {
            // If the table is not yet visible, add a listener that will run when the component is visible.
            clientTable.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int[] columnWidths = {5, 15, 15, 25, 15, 25};
                    LegalTheme.setColumnWidths(clientTable, columnWidths);
                    // Remove Listener, only run once
                    clientTable.removeComponentListener(this);
                }
            });
        }
    }
    
    /**
     * Validates if a string contains only numeric characters
     * 
     * Checks whether the given string consists exclusively of digits,
     * which is used for validating phone numbers.
     *
     * @param str The string to check for numeric content
     * @return true if the string contains only digits, false otherwise
     */
    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Displays a dialog for adding a new client
     * 
     * Creates and shows a modal dialog with form fields for entering
     * new client information. Validates input data before saving to ensure
     * data integrity. Provides feedback on successful creation or errors.
     */
    private void showAddClientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Client", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create form fields
        JTextField nameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        // Apply styles to text fields
        LegalTheme.applyTextFieldStyle(nameField);
        LegalTheme.applyTextFieldStyle(surnameField);
        LegalTheme.applyTextFieldStyle(emailField);
        LegalTheme.applyTextFieldStyle(phoneField);
        LegalTheme.applyTextFieldStyle(addressField);

        // Add form fields with labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Surname:"), gbc);
        gbc.gridx = 1;
        dialog.add(surnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        // Apply styles to buttons
        LegalTheme.applyButtonStyle(saveButton);
        LegalTheme.applySecondaryButtonStyle(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate phone number - must contain only digits
            String phoneNumber = phoneField.getText();
            if (!isNumeric(phoneNumber)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Phone number must contain only digits.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                clientService.createClient(
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText()
                );
                loadClients();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Client added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding client: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays a dialog for editing an existing client
     * 
     * Creates and shows a modal dialog with pre-populated form fields
     * for modifying the selected client's information. Validates the modified
     * data before saving and provides feedback on success or failure.
     * Shows an error message if no client is selected or the client is not found.
     */
    private void showEditClientDialog() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to edit",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long clientId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Client> clientOpt = clientService.getClientById(clientId);

        if (clientOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Client not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = clientOpt.get();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Client", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Client header info
        JLabel clientInfoLabel = new JLabel("Editing Client: " + client.getName() + " " + client.getSurname());
        clientInfoLabel.setFont(LegalTheme.HEADER_FONT);
        clientInfoLabel.setForeground(LegalTheme.PRIMARY_COLOR);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(clientInfoLabel, gbc);
        
        // Reset gridwidth and set anchor for form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LegalTheme.NORMAL_FONT);
        dialog.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(client.getName(), 20);
        LegalTheme.applyTextFieldStyle(nameField);
        dialog.add(nameField, gbc);

        // Surname field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel surnameLabel = new JLabel("Surname:");
        surnameLabel.setFont(LegalTheme.NORMAL_FONT);
        dialog.add(surnameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField surnameField = new JTextField(client.getSurname(), 20);
        LegalTheme.applyTextFieldStyle(surnameField);
        dialog.add(surnameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LegalTheme.NORMAL_FONT);
        dialog.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(client.getEmail(), 20);
        LegalTheme.applyTextFieldStyle(emailField);
        dialog.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(LegalTheme.NORMAL_FONT);
        dialog.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(client.getPhone(), 20);
        LegalTheme.applyTextFieldStyle(phoneField);
        dialog.add(phoneField, gbc);

        // Address field
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(LegalTheme.NORMAL_FONT);
        dialog.add(addressLabel, gbc);
        
        gbc.gridx = 1;
        JTextField addressField = new JTextField(client.getAddress(), 20);
        LegalTheme.applyTextFieldStyle(addressField);
        dialog.add(addressField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        JButton saveButton = new JButton("Save");
        LegalTheme.applyButtonStyle(saveButton);
        
        JButton cancelButton = new JButton("Cancel");
        LegalTheme.applySecondaryButtonStyle(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);

        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate phone number - must contain only digits
            String phoneNumber = phoneField.getText();
            if (!isNumeric(phoneNumber)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Phone number must contain only digits.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                clientService.updateClient(
                    clientId,
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText()
                );
                loadClients();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Client updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating client: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays a confirmation dialog for deleting a client
     * 
     * Creates and shows a modal confirmation dialog for the selected client.
     * If confirmed, removes the client from the system and updates the table.
     * Shows an error message if no client is selected or the client is not found.
     */
    private void showDeleteClientDialog() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long clientId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Client> clientOpt = clientService.getClientById(clientId);

        if (clientOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Client not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = clientOpt.get();
        
        // Create a custom confirmation dialog
        JDialog confirmDialog = new JDialog(mainFrame, "Delete Confirmation", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.getContentPane().setBackground(LegalTheme.PANEL_BACKGROUND);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(mainFrame);
        
        // Create header panel
        JPanel headerPanel = LegalTheme.createHeaderPanel("Confirm Deletion");
        confirmDialog.add(headerPanel, BorderLayout.NORTH);
        
        // Create message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        messagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("<html>Are you sure you want to delete this client?<br/><br/>" +
                "<b>Name:</b> " + client.getName() + " " + client.getSurname() + "<br/>" +
                "<b>Email:</b> " + client.getEmail() + "</html>");
        messageLabel.setFont(LegalTheme.NORMAL_FONT);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        confirmDialog.add(messagePanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JButton yesButton = new JButton("Yes, Delete");
        LegalTheme.applySecondaryButtonStyle(yesButton);
        
        JButton noButton = new JButton("Cancel");
        LegalTheme.applyButtonStyle(noButton);
        
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        yesButton.addActionListener(e -> {
            try {
                clientService.deleteClient(clientId);
                loadClients();
                confirmDialog.dispose();
                JOptionPane.showMessageDialog(this, "Client deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting client: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        noButton.addActionListener(e -> confirmDialog.dispose());
        
        confirmDialog.setVisible(true);
    }

    /**
     * Searches for clients matching the search criteria
     * 
     * Filters the client list based on the text entered in the search field.
     * Searches across multiple client attributes (name, surname, email, phone,
     * and address) and updates the table to show only matching clients.
     * If the search field is empty, reloads all clients.
     */
    private void searchClients() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadClients();
            return;
        }

        tableModel.setRowCount(0);
        List<Client> clients = clientService.getAllClients();
        for (Client client : clients) {
            if (client.getName().toLowerCase().contains(searchTerm) ||
                client.getSurname().toLowerCase().contains(searchTerm) ||
                client.getEmail().toLowerCase().contains(searchTerm) ||
                client.getPhone().contains(searchTerm) ||
                client.getAddress().toLowerCase().contains(searchTerm)) {
                tableModel.addRow(new Object[]{
                    client.getId(),
                    client.getName(),
                    client.getSurname(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getAddress()
                });
            }
        }
        
        // Adjust column widths for search results too
        int[] columnWidths = {5, 15, 15, 25, 15, 25};
        LegalTheme.setColumnWidths(clientTable, columnWidths);
    }
}
