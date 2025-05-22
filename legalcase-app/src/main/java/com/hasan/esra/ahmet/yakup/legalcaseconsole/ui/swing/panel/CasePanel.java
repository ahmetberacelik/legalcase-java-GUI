package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;

/**
 * Case management panel compatible with Eclipse WindowBuilder and AssertJ
 * 
 * This class provides a user interface for managing legal cases. It allows users to view, 
 * add, edit, and delete case records. The panel is designed to work seamlessly with Eclipse 
 * WindowBuilder for GUI design and AssertJ for UI testing.
 */
public class CasePanel extends JPanel {
    // SerialVersionUID for serialization compatibility
    private static final long serialVersionUID = 1L;
    
    // Services
    private CaseService caseService;
    private MainFrame mainFrame;
    
    // UI Components - all made protected for testing with AssertJ
    protected JTable caseTable;
    protected DefaultTableModel tableModel;
    protected JTextField searchField;
    protected JButton searchButton;
    protected JButton addButton;
    protected JButton editButton;
    protected JButton deleteButton;
    protected JButton backButton;
    protected JScrollPane scrollPane;
    protected JPanel headerPanel;
    protected JPanel tablePanel;
    protected JPanel searchPanel;
    protected JPanel buttonPanel;
    
    /**
     * Default constructor required for WindowBuilder
     * 
     * This constructor initializes UI components without services.
     * It's primarily used by WindowBuilder for design-time rendering.
     */
    public CasePanel() {
        initComponents();
    }
    
    /**
     * Constructor with service and main frame references
     * 
     * @param caseService The service to handle case operations
     * @param mainFrame The main application frame
     */
    public CasePanel(CaseService caseService, MainFrame mainFrame) {
        this.caseService = caseService;
        this.mainFrame = mainFrame;
        
        initComponents();
        
        // Load cases when service is injected
        if (caseService != null) {
            loadCases();
        }
    }
    
    /**
     * Initialize UI components
     * 
     * This method sets up the entire UI structure including panels,
     * tables, buttons, and other controls while maintaining compatibility
     * with WindowBuilder.
     */
    private void initComponents() {
        // Main panel configuration
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(LegalTheme.BACKGROUND_COLOR);
        
        // Create header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Case Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create table model
        String[] columnNames = new String[]{"ID", "Case No", "Title", "Type", "Status", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Long.class;
                }
                return String.class;
            }
        };
        
        // Create table
        caseTable = new JTable(tableModel);
        caseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseTable.setRowHeight(25);
        caseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        caseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Scroll pane
        scrollPane = new JScrollPane(caseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Table panel
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
            "Case List",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            LegalTheme.PRIMARY_COLOR));
        tablePanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        // Search panel
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        searchField = new JTextField(20);
        searchField.setName("searchField");
        
        searchButton = new JButton("Search");
        searchButton.setName("searchButton");
        searchButton.addActionListener(e -> searchCases());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        addButton = new JButton("Add Case");
        addButton.setName("addButton");
        addButton.addActionListener(e -> showAddCaseDialog());
        
        editButton = new JButton("Edit Case");
        editButton.setName("editButton");
        editButton.addActionListener(e -> showEditCaseDialog());
        
        deleteButton = new JButton("Delete Case");
        deleteButton.setName("deleteButton");
        deleteButton.addActionListener(e -> showDeleteCaseDialog());
        
        backButton = new JButton("Return to Menu");
        backButton.setName("backButton");
        backButton.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.showPanel("mainMenu");
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Apply theme
        applyTheme();
    }
    
    /**
     * Apply theme to UI components
     * 
     * This method applies consistent styling to all UI elements
     * according to the application theme.
     */
    private void applyTheme() {
        // Apply style to buttons
        JButton[] buttons = {searchButton, addButton, editButton, backButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(242, 242, 242)); // Light gray background
            button.setForeground(Color.BLACK); // Black text
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        }
        
        // Different style for delete button
        deleteButton.setBackground(new Color(255, 235, 235)); // Light red background
        deleteButton.setForeground(new Color(200, 30, 30)); // Dark red text
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        
        // Search field style
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    /**
     * Load all cases into table
     * 
     * This method retrieves all cases from the service and
     * populates the table with the data.
     */
    public void loadCases() {
        if (caseService == null) {
            return;
        }
        
        tableModel.setRowCount(0);
        List<Case> cases = caseService.getAllCases();
        
        for (Case legalCase : cases) {
            tableModel.addRow(new Object[]{
                legalCase.getId(),
                legalCase.getCaseNumber(),
                legalCase.getTitle(),
                legalCase.getType().toString(),
                legalCase.getStatus().toString(),
                legalCase.getDescription()
            });
        }
        
        // Adjust column widths
        if (caseTable.getColumnModel().getColumnCount() > 0) {
            caseTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
            caseTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Case No
            caseTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Title
            caseTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Type
            caseTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
            caseTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Description
        }
    }
    
    /**
     * Show dialog for adding a new case
     * 
     * This method displays a dialog with input fields for creating
     * a new case record in the system.
     */
    protected void showAddCaseDialog() {
        if (caseService == null || mainFrame == null) {
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Case", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(LegalTheme.PANEL_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Case Number field
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Case Number:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField caseNumberField = new JTextField(20);
        caseNumberField.setName("caseNumberField");
        dialog.add(caseNumberField, gbc);
        
        // Title field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Title:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField titleField = new JTextField(20);
        titleField.setName("titleField");
        dialog.add(titleField, gbc);
        
        // Type dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<CaseType> typeComboBox = new JComboBox<>(CaseType.values());
        typeComboBox.setName("typeComboBox");
        dialog.add(typeComboBox, gbc);
        
        // Description area
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setName("descriptionArea");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        dialog.add(descriptionScrollPane, gbc);
        
        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        JButton saveButton = new JButton("Save");
        saveButton.setName("saveButton");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setName("cancelButton");
        
        // Apply theme to buttons
        LegalTheme.applyButtonStyle(saveButton);
        LegalTheme.applyButtonStyle(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        // Action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate case number - must be digits only
                String caseNumber = caseNumberField.getText().trim();
                if (caseNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Case number is required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!caseNumber.matches("\\d+")) {
                    JOptionPane.showMessageDialog(dialog,
                        "Case number must contain only digits.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate title
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Title is required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the case
                Case newCase = caseService.createCase(
                    caseNumber,
                    title,
                    (CaseType) typeComboBox.getSelectedItem(),
                    descriptionArea.getText()
                );
                
                // Refresh the table and show success message
                loadCases();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    "Case added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error adding case: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Set dialog properties
        dialog.pack();
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog for editing an existing case
     * 
     * This method displays a dialog with pre-filled input fields
     * for updating an existing case record.
     */
    protected void showEditCaseDialog() {
        if (caseService == null || mainFrame == null) {
            return;
        }
        
        int selectedRow = caseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to edit",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Long caseId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Case> caseOpt = caseService.getCaseById(caseId);
        
        if (caseOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Case not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Case caseToEdit = caseOpt.get();
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Case", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(LegalTheme.PANEL_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Case Number field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel caseNumberLabel = new JLabel("Case Number:");
        dialog.add(caseNumberLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField caseNumberField = new JTextField(caseToEdit.getCaseNumber(), 20);
        caseNumberField.setName("caseNumberField");
        dialog.add(caseNumberField, gbc);
        
        // Title field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Title:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField titleField = new JTextField(caseToEdit.getTitle(), 20);
        titleField.setName("titleField");
        dialog.add(titleField, gbc);
        
        // Type dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<CaseType> typeComboBox = new JComboBox<>(CaseType.values());
        typeComboBox.setSelectedItem(caseToEdit.getType());
        typeComboBox.setName("typeComboBox");
        dialog.add(typeComboBox, gbc);
        
        // Status dropdown
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<CaseStatus> statusComboBox = new JComboBox<>(CaseStatus.values());
        statusComboBox.setSelectedItem(caseToEdit.getStatus());
        statusComboBox.setName("statusComboBox");
        dialog.add(statusComboBox, gbc);
        
        // Description area
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea descriptionArea = new JTextArea(caseToEdit.getDescription(), 5, 20);
        descriptionArea.setName("descriptionArea");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        dialog.add(descriptionScrollPane, gbc);
        
        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        JButton saveButton = new JButton("Save");
        saveButton.setName("saveEditButton");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setName("cancelEditButton");
        
        // Apply theme to buttons
        LegalTheme.applyButtonStyle(saveButton);
        LegalTheme.applyButtonStyle(cancelButton);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        
        // Action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate case number - must be digits only
                String caseNumber = caseNumberField.getText().trim();
                if (caseNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Case number is required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!caseNumber.matches("\\d+")) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Case number must contain only digits.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validate title
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Title is required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update the case
                Case updatedCase = caseService.updateCase(
                    caseId,
                    caseNumber,
                    title,
                    (CaseType) typeComboBox.getSelectedItem(),
                    descriptionArea.getText(),
                    (CaseStatus) statusComboBox.getSelectedItem()
                );
                
                // Refresh the table and show success message
                loadCases();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    "Case updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error updating case: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Set dialog properties
        dialog.pack();
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Show confirmation dialog for deleting a case
     * 
     * This method displays a confirmation dialog and handles
     * the process of deleting a case record if confirmed.
     */
    protected void showDeleteCaseDialog() {
        if (caseService == null || mainFrame == null) {
            return;
        }
        
        int selectedRow = caseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Long caseId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Case> caseOpt = caseService.getCaseById(caseId);
        
        if (caseOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Case not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Case caseToDelete = caseOpt.get();
        
        // Delete confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this case?\n" +
            "Case No: " + caseToDelete.getCaseNumber() + "\n" +
            "Title: " + caseToDelete.getTitle(),
            "Delete Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                caseService.deleteCase(caseId);
                loadCases();
                JOptionPane.showMessageDialog(this, "Case deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting case: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Search for cases matching the search criteria
     * 
     * This method filters the case list based on the search term entered
     * by the user and updates the table with matching results.
     */
    protected void searchCases() {
        if (caseService == null) {
            return;
        }
        
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadCases();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Case> cases = caseService.getAllCases();
        
        for (Case legalCase : cases) {
            if (legalCase.getCaseNumber().toLowerCase().contains(searchTerm) ||
                legalCase.getTitle().toLowerCase().contains(searchTerm) ||
                legalCase.getDescription().toLowerCase().contains(searchTerm) ||
                legalCase.getType().toString().toLowerCase().contains(searchTerm) ||
                legalCase.getStatus().toString().toLowerCase().contains(searchTerm)) {
                
                tableModel.addRow(new Object[]{
                    legalCase.getId(),
                    legalCase.getCaseNumber(),
                    legalCase.getTitle(),
                    legalCase.getType().toString(),
                    legalCase.getStatus().toString(),
                    legalCase.getDescription()
                });
            }
        }
    }
    
    // Getter methods for AssertJ tests
    
    /**
     * @return The case table component
     */
    public JTable getCaseTable() {
        return caseTable;
    }
    
    /**
     * @return The table model for the case table
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    
    /**
     * @return The search field component
     */
    public JTextField getSearchField() {
        return searchField;
    }
    
    /**
     * @return The search button component
     */
    public JButton getSearchButton() {
        return searchButton;
    }
    
    /**
     * @return The add case button component
     */
    public JButton getAddButton() {
        return addButton;
    }
    
    /**
     * @return The edit case button component
     */
    public JButton getEditButton() {
        return editButton;
    }
    
    /**
     * @return The delete case button component
     */
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    /**
     * @return The back to menu button component
     */
    public JButton getBackButton() {
        return backButton;
    }
    
    /**
     * Sets the case service for this panel
     * 
     * @param caseService The case service to use
     */
    public void setCaseService(CaseService caseService) {
        this.caseService = caseService;
    }
    
    /**
     * Sets the main frame for this panel
     * 
     * @param mainFrame The main frame reference
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
} 