/**
 * Main application class for the Legal Case Tracker system
 *
 * This class serves as the entry point to the application and coordinates
 * initialization of all components including database connection, DAOs,
 * services, and the user interface. It provides options to start either
 * the console-based or graphical user interface.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-05-07
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.config.DatabaseManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;
import com.j256.ormlite.support.ConnectionSource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for Legal Case Tracker
 * Provides options to start either console or GUI interface
 */
public class LegalcaseApp {
    private static final Logger LOGGER = Logger.getLogger(LegalcaseApp.class.getName());

    public static void main(String[] args) {
        try {
            // Initialize database connection
            DatabaseManager.initializeDatabase();
            ConnectionSource connectionSource = DatabaseManager.getConnectionSource();

            // Create DAO objects
            UserDAO userDAO = new UserDAO(connectionSource);
            ClientDAO clientDAO = new ClientDAO(connectionSource);
            CaseDAO caseDAO = new CaseDAO(connectionSource);
            HearingDAO hearingDAO = new HearingDAO(connectionSource);
            DocumentDAO documentDAO = new DocumentDAO(connectionSource);

            // Create service objects
            AuthService authService = new AuthService(userDAO);
            ClientService clientService = new ClientService(clientDAO);
            CaseService caseService = new CaseService(caseDAO, clientDAO);
            HearingService hearingService = new HearingService(hearingDAO, caseDAO);
            DocumentService documentService = new DocumentService(documentDAO, caseDAO);
            
            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            if (args != null && args.length > 0 && "auto-gui".equalsIgnoreCase(args[0])) {
                startGuiInterface(authService, clientService, caseService, hearingService, documentService);
                return;
            }
            
            // Show interface selection dialog
            showInterfaceSelectionDialog(authService, clientService, caseService, hearingService, documentService);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error: ", e);
            JOptionPane.showMessageDialog(null,
                "Database connection error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: ", e);
            JOptionPane.showMessageDialog(null,
                "Unexpected error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows a dialog for the user to select between console and GUI interfaces
     */
    private static void showInterfaceSelectionDialog(AuthService authService, ClientService clientService, 
            CaseService caseService, HearingService hearingService, DocumentService documentService) {
        
        // Create a styled dialog
        JDialog dialog = new JDialog((Frame)null, "Legal Case Tracker", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(null);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LegalTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Legal Case Tracker");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Create content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)));
            
        // GridBagConstraints for promptLabel
        GridBagConstraints gbcPrompt = new GridBagConstraints();
        gbcPrompt.gridwidth = GridBagConstraints.REMAINDER;
        gbcPrompt.fill = GridBagConstraints.HORIZONTAL;
        gbcPrompt.insets = new Insets(10, 10, 10, 10);
        
        JLabel promptLabel = new JLabel("Please select the interface you would like to use:");
        promptLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        contentPanel.add(promptLabel, gbcPrompt);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        // GUI button
        JButton guiButton = new JButton("Graphical Interface");
        guiButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        guiButton.setBackground(LegalTheme.PRIMARY_COLOR);
        guiButton.setForeground(Color.BLACK);
        guiButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        
        // Console button
        JButton consoleButton = new JButton("Console Interface");
        consoleButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        consoleButton.setBackground(LegalTheme.SECONDARY_COLOR);
        consoleButton.setForeground(Color.BLACK);
        consoleButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        
        buttonPanel.add(guiButton);
        buttonPanel.add(consoleButton);
        
        // GridBagConstraints for buttonPanel
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridwidth = GridBagConstraints.REMAINDER;
        gbcButtons.fill = GridBagConstraints.HORIZONTAL;
        gbcButtons.insets = new Insets(20, 10, 10, 10);
        contentPanel.add(buttonPanel, gbcButtons);
        
        // Replace ActionListener with MouseListener
        guiButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialog.dispose();
                startGuiInterface(authService, clientService, caseService, hearingService, documentService);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                guiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        consoleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialog.dispose();
                startConsoleInterface(authService, clientService, caseService, hearingService, documentService);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                consoleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        // Add footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel copyrightLabel = new JLabel("© 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        copyrightLabel.setForeground(new Color(255, 255, 255));
        footerPanel.add(copyrightLabel);
        
        // Assemble the dialog
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.getContentPane().add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Starts the graphical user interface
     */
    private static void startGuiInterface(AuthService authService, ClientService clientService, 
            CaseService caseService, HearingService hearingService, DocumentService documentService) {
        
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(authService, clientService, caseService, hearingService, documentService);
            mainFrame.setVisible(true);

           
        });
    }
    
    /**
     * Starts the console user interface
     */
    private static void startConsoleInterface(AuthService authService, ClientService clientService, 
            CaseService caseService, HearingService hearingService, DocumentService documentService) {
        
        // Create and start the console menu manager
        ConsoleMenuManager menuManager = new ConsoleMenuManager(authService, clientService, 
                caseService, hearingService, documentService);

        
        // Start the console interface
        menuManager.start();
    }
}