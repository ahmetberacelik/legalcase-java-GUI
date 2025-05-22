package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Court hearing management panel for the legal case management system
 * 
 * This class provides a comprehensive user interface for managing court hearings
 * within the legal case management application. It enables users to perform CRUD
 * operations (Create, Read, Update, Delete) on hearing records with an intuitive
 * graphical interface.
 * 
 * The panel includes functionality for:
 * - Viewing a list of all scheduled court hearings in a sortable table
 * - Searching for specific hearings using various criteria
 * - Adding new hearing records to the system
 * - Editing existing hearing information including status updates
 * - Deleting hearings from the database
 * - Associating hearings with specific legal cases
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class HearingPanel extends JPanel {
    /**
     * Service for hearing data operations
     * Handles all business logic and database interactions related to court hearings
     */
    private final HearingService hearingService;
    
    /**
     * Service for case data operations
     * Used to fetch case information when associating hearings with cases
     */
    private final CaseService caseService;
    
    /**
     * Reference to the main application frame
     * Used for navigation between panels and maintaining application context
     */
    private final MainFrame mainFrame;
    
    /**
     * Table component for displaying hearing records
     * Shows all hearing data in a structured tabular format
     */
    private JTable hearingTable;
    
    /**
     * Table model for managing the data displayed in the hearing table
     * Controls the structure, content, and behavior of the table
     */
    private DefaultTableModel tableModel;
    
    /**
     * Text field for entering search queries
     * Used to filter hearings based on user input
     */
    private JTextField searchField;
    
    /**
     * Button to initiate search operations
     * Triggers the filtering of hearing records based on search criteria
     */
    private JButton searchButton;

    /**
     * Constructs a new HearingPanel with necessary dependencies
     * 
     * Initializes the hearing management panel with services and navigation dependencies,
     * sets up the user interface components, and loads the initial hearing data.
     *
     * @param hearingService Service for hearing data operations and business logic
     * @param caseService Service for case data operations when linking hearings to cases
     * @param mainFrame Reference to the main application frame for navigation
     */
    public HearingPanel(HearingService hearingService, CaseService caseService, MainFrame mainFrame) {
        this.hearingService = hearingService;
        this.caseService = caseService;
        this.mainFrame = mainFrame;
        initializeUI();
        loadHearings();
        
        // Apply theme to this panel
        LegalTheme.applyPanelStyle(this);
    }

    /**
     * Initializes the user interface components
     * 
     * Sets up the layout, creates and configures all UI components including
     * the hearing table, search panel, and action buttons. Organizes components
     * into a cohesive and user-friendly interface following the application's
     * design guidelines.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(LegalTheme.BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = LegalTheme.createHeaderPanel("Hearing Management");
        add(headerPanel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton addButton = new JButton("Add New Hearing");
        JButton editButton = new JButton("Edit Hearing");
        JButton deleteButton = new JButton("Delete Hearing");
        JButton backButton = new JButton("Back to Menu");

        // Apply styles to buttons
        LegalTheme.applyButtonStyle(addButton);
        LegalTheme.applyButtonStyle(editButton);
        LegalTheme.applySecondaryButtonStyle(deleteButton);
        LegalTheme.applyButtonStyle(backButton);

        // Add action listeners
        addButton.addActionListener(e -> showAddHearingDialog());
        editButton.addActionListener(e -> showEditHearingDialog());
        deleteButton.addActionListener(e -> showDeleteHearingDialog());
        backButton.addActionListener(e -> mainFrame.showPanel("mainMenu"));

        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Create table model with column names
        tableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"ID", "Case Number", "Case Title", "Date & Time", "Judge", "Location", "Status"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table and add to scroll pane
        hearingTable = new JTable(tableModel);
        LegalTheme.applyTableStyle(hearingTable);
        
        JScrollPane scrollPane = new JScrollPane(hearingTable);
        LegalTheme.applyScrollPaneStyle(scrollPane);
        
        // Create a panel for the table with a title
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
            "Hearing Schedule",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LegalTheme.HEADER_FONT,
            LegalTheme.PRIMARY_COLOR));
        tablePanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
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
        searchButton.addActionListener(e -> searchHearings());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        
        // Add components to main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Loads all hearing records from the service into the table
     * 
     * Retrieves hearing data from the hearing service, clears the current table,
     * and populates it with the latest hearing information. Handles null values
     * appropriately and formats date information. Also configures the column widths
     * for optimal display of hearing data.
     */
    private void loadHearings() {
        tableModel.setRowCount(0);
        List<Hearing> hearings = hearingService.getAllHearings();
        for (Hearing hearing : hearings) {
            // Handle null case reference
            String caseNumber = "N/A";
            String caseTitle = "N/A";
            if (hearing.getCse() != null) {
                caseNumber = hearing.getCse().getCaseNumber();
                caseTitle = hearing.getCse().getTitle();
            }
            
            // Handle null hearing date
            String formattedDate = "N/A";
            if (hearing.getHearingDate() != null) {
                formattedDate = hearing.getHearingDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }
            
            tableModel.addRow(new Object[]{
                hearing.getId(),
                caseNumber,
                caseTitle,
                formattedDate,
                hearing.getJudge(),
                hearing.getLocation(),
                hearing.getStatus()
            });
        }
        
        // Adjust column widths
        if (hearingTable.getWidth() > 0) {
            // Percentile widths for ID, Case No, Case Title, Date, Judge, Place, Status
            int[] columnWidths = {5, 10, 20, 20, 15, 15, 15};
            LegalTheme.setColumnWidths(hearingTable, columnWidths);
        } else {
            // If the table is not yet visible, add a listener that will run when the component is visible.
            hearingTable.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int[] columnWidths = {5, 10, 20, 20, 15, 15, 15};
                    LegalTheme.setColumnWidths(hearingTable, columnWidths);
                    // Remove Listener, only run once
                    hearingTable.removeComponentListener(this);
                }
            });
        }
    }

    /**
     * Displays a dialog for adding a new hearing
     * 
     * Creates and shows a modal dialog with form fields for entering
     * new hearing information, including selecting an associated case,
     * date and time, and other relevant details. Validates input data
     * before saving and provides feedback on successful creation or errors.
     */
    private void showAddHearingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Hearing", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Case selection
        JComboBox<Case> caseComboBox = new JComboBox<>();
        List<Case> cases = caseService.getAllCases();
        for (Case caseEntity : cases) {
            caseComboBox.addItem(caseEntity);
        }

        // Date and time selection
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy HH:mm");
        dateSpinner.setEditor(dateEditor);

        JTextField judgeField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextArea notesArea = new JTextArea(5, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Case:"), gbc);
        gbc.gridx = 1;
        dialog.add(caseComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Date and Time:"), gbc);
        gbc.gridx = 1;
        dialog.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Judge:"), gbc);
        gbc.gridx = 1;
        dialog.add(judgeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        dialog.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        dialog.add(notesScroll, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                Case selectedCase = (Case) caseComboBox.getSelectedItem();
                if (selectedCase == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a case");
                    return;
                }

                LocalDateTime hearingDate = ((java.util.Date) dateSpinner.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

                hearingService.createHearing(
                    selectedCase.getId(),
                    hearingDate,
                    judgeField.getText(),
                    locationField.getText(),
                    notesArea.getText()
                );
                loadHearings();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Hearing added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding hearing: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays a dialog for editing an existing hearing
     * 
     * Creates and shows a modal dialog with pre-populated form fields
     * for modifying the selected hearing's information, including date,
     * time, judge, location, and status. Validates the modified data 
     * before saving and provides feedback on success or failure.
     * Shows an error message if no hearing is selected or the hearing is not found.
     */
    private void showEditHearingDialog() {
        int selectedRow = hearingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a hearing to edit",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long hearingId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

        if (hearingOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hearing not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hearing hearing = hearingOpt.get();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Hearing", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Date and time selection
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy HH:mm");
        dateSpinner.setEditor(dateEditor);
        if (hearing.getHearingDate() != null) {
            dateSpinner.setValue(java.util.Date.from(
                hearing.getHearingDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }

        JTextField judgeField = new JTextField(hearing.getJudge(), 20);
        JTextField locationField = new JTextField(hearing.getLocation(), 20);
        JTextArea notesArea = new JTextArea(hearing.getNotes(), 5, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        // Status selection
        JComboBox<HearingStatus> statusComboBox = new JComboBox<>(HearingStatus.values());
        statusComboBox.setSelectedItem(hearing.getStatus());

        // Case info (read-only)
        JLabel caseInfoLabel = new JLabel("Case: " + (hearing.getCse() != null ? 
            hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle() : "N/A"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(caseInfoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        dialog.add(new JLabel("Date and Time:"), gbc);
        gbc.gridx = 1;
        dialog.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Judge:"), gbc);
        gbc.gridx = 1;
        dialog.add(judgeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        dialog.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(statusComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        dialog.add(notesScroll, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                LocalDateTime newDate = ((java.util.Date) dateSpinner.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

                hearingService.updateHearing(
                    hearingId,
                    newDate,
                    judgeField.getText(),
                    locationField.getText(),
                    notesArea.getText(),
                    (HearingStatus) statusComboBox.getSelectedItem()
                );
                loadHearings();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Hearing updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating hearing: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Searches for hearings matching the search criteria
     * 
     * Filters the hearing list based on the text entered in the search field.
     * Searches across multiple hearing attributes (case number, title, date,
     * judge, location, and status) and updates the table to show only matching hearings.
     * If the search field is empty, reloads all hearings.
     */
    private void searchHearings() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadHearings();
            return;
        }

        tableModel.setRowCount(0);
        List<Hearing> hearings = hearingService.getAllHearings();
        for (Hearing hearing : hearings) {
            // Handle null case reference
            String caseNumber = "N/A";
            String caseTitle = "N/A";
            if (hearing.getCse() != null) {
                caseNumber = hearing.getCse().getCaseNumber();
                caseTitle = hearing.getCse().getTitle();
            }
            
            // Handle null hearing date
            String formattedDate = "N/A";
            if (hearing.getHearingDate() != null) {
                formattedDate = hearing.getHearingDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }
            
            // Handle null fields
            String judge = hearing.getJudge() != null ? hearing.getJudge() : "";
            String location = hearing.getLocation() != null ? hearing.getLocation() : "";
            String status = hearing.getStatus() != null ? hearing.getStatus().toString() : "";
            
            // Filter by search criteria
            if (caseNumber.toLowerCase().contains(searchTerm) ||
                caseTitle.toLowerCase().contains(searchTerm) ||
                formattedDate.toLowerCase().contains(searchTerm) ||
                judge.toLowerCase().contains(searchTerm) ||
                location.toLowerCase().contains(searchTerm) ||
                status.toLowerCase().contains(searchTerm)) {
                
                tableModel.addRow(new Object[]{
                    hearing.getId(),
                    caseNumber,
                    caseTitle,
                    formattedDate,
                    judge,
                    location,
                    hearing.getStatus()
                });
            }
        }
        
        // Adjust column widths for search results too
        int[] columnWidths = {5, 10, 20, 20, 15, 15, 15};
        LegalTheme.setColumnWidths(hearingTable, columnWidths);
    }

    /**
     * Displays a confirmation dialog for deleting a hearing
     * 
     * Creates and shows a modal confirmation dialog for the selected hearing.
     * If confirmed, removes the hearing from the system and updates the table.
     * Shows an error message if no hearing is selected or the hearing is not found.
     * Handles null values appropriately when displaying information.
     */
    private void showDeleteHearingDialog() {
        int selectedRow = hearingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a hearing to delete",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long hearingId = (Long) tableModel.getValueAt(selectedRow, 0);
        Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

        if (hearingOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hearing not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hearing hearing = hearingOpt.get();
        
        // Build confirmation message with null check for case
        String caseInfo = "Unknown";
        if (hearing.getCse() != null) {
            caseInfo = hearing.getCse().getCaseNumber();
        }
        
        String dateInfo = "Unknown";
        if (hearing.getHearingDate() != null) {
            dateInfo = hearing.getHearingDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this hearing?\nCase: " + caseInfo +
                "\nDate: " + dateInfo,
            "Delete Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                hearingService.deleteHearing(hearingId);
                loadHearings();
                JOptionPane.showMessageDialog(this, "Hearing deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting hearing: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
