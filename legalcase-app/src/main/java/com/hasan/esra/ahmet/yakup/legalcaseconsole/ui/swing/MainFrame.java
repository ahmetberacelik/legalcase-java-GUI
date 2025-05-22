package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;

/**
 * Main JFrame class for the Legal Case Tracker application
 * 
 * This class serves as the container for all panels in the application and
 * manages navigation between them using a card layout. It maintains references
 * to all service components and provides methods to switch between different
 * functional areas of the application.
 * 
 * The class is designed to work seamlessly with WindowBuilder and includes
 * special handling for design-time vs runtime behavior to ensure proper
 * visualization in the IDE's design view.
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class MainFrame extends JFrame {
    
    // Services
    /**
     * Authentication service for user login functionality
     */
    private AuthService authService;
    
    /**
     * Client service for managing client records
     */
    private ClientService clientService;
    
    /**
     * Case service for managing legal case records
     */
    private CaseService caseService;
    
    /**
     * Hearing service for managing case hearing records
     */
    private HearingService hearingService;
    
    /**
     * Document service for managing case-related documents
     */
    private DocumentService documentService;
    
    // UI Components
    /**
     * Main content pane that contains all UI elements
     */
    private JPanel contentPane;
    
    /**
     * Card layout for switching between different panels
     */
    private CardLayout cardLayout;
    
    /**
     * Panel that holds all the application's panels with card layout
     */
    private JPanel cardPanel;
    
    /**
     * No-argument constructor for WindowBuilder design mode
     * 
     * This constructor is called during design time in WindowBuilder.
     * It initializes the UI components without requiring service dependencies.
     */
    public MainFrame() {
        // Services will be null in design mode
        initialize();
    }
    
    /**
     * Runtime constructor with all required service dependencies
     * 
     * Initializes the frame with all necessary services for full application
     * functionality. Creates all UI components and loads all application panels.
     * 
     * @param authService Authentication service for user management
     * @param clientService Client management service
     * @param caseService Case management service
     * @param hearingService Hearing management service
     * @param documentService Document management service
     */
    public MainFrame(AuthService authService, ClientService clientService, 
                    CaseService caseService, HearingService hearingService, 
                    DocumentService documentService) {
        this.authService = authService;
        this.clientService = clientService;
        this.caseService = caseService;
        this.hearingService = hearingService;
        this.documentService = documentService;
        
        initialize();
        loadPanels();
    }
    
    /**
     * Creates and configures all UI components
     * 
     * Sets up the frame properties, content pane, and card layout for panel
     * navigation. Handles both design-time and runtime initialization with
     * appropriate error handling.
     */
    private void initialize() {
        setTitle("Legal Case Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 700);
        setLocationRelativeTo(null);
        
        // Main content panel
        contentPane = new JPanel();
        contentPane.setName("contentPane");
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Card layout for panel switching
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setName("cardPanel");
        contentPane.add(cardPanel, BorderLayout.CENTER);
        
        // In WindowBuilder design mode, we add a dummy panel to avoid errors
        // when trying to create dynamic instances
        try {
            // Create normal MainMenuPanel at runtime
            if (isDesignTime()) {
                // Show a simple design panel in design time
                createDesignTimePanel();
            } else {
                // Use the actual MainMenuPanel at runtime
                MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
                cardPanel.add(mainMenuPanel, "mainMenu");
                cardLayout.show(cardPanel, "mainMenu");
            }
        } catch (Exception e) {
            // If an error occurs in WindowBuilder design time, show a simple panel
            createDesignTimePanel();
        }
    }
    
    /**
     * Creates a simplified panel for WindowBuilder design mode
     * 
     * This method generates a basic panel with sample UI components that
     * represents the application's appearance without requiring actual
     * service dependencies or complex component initialization.
     */
    private void createDesignTimePanel() {
        JPanel designPanel = new JPanel();
        designPanel.setName("designPanel");
        designPanel.setLayout(new BorderLayout(0, 0));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setName("headerPanel");
        headerPanel.setBackground(new Color(25, 55, 109));
        headerPanel.setPreferredSize(new Dimension(1000, 100));
        headerPanel.setLayout(new BorderLayout(0, 0));
        
        JLabel titleLabel = new JLabel("LEGAL CASE TRACKER");
        titleLabel.setName("titleLabel");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Center panel (for menu buttons)
        JPanel centerPanel = new JPanel();
        centerPanel.setName("centerPanel");
        centerPanel.setBackground(new Color(240, 240, 245));
        centerPanel.setLayout(new GridLayout(5, 1, 10, 10));
        centerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Menu buttons
        String[] buttonTexts = {
            "Client Management",
            "Case Management", 
            "Hearing Management",
            "Document Management",
            "Logout"
        };
        
        Color[] buttonColors = {
            new Color(173, 216, 230),
            new Color(144, 238, 144),
            new Color(230, 190, 230),
            new Color(255, 222, 173),
            new Color(255, 200, 200)
        };
        
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton("\u2630 " + buttonTexts[i]);
            button.setName(buttonTexts[i].split(" ")[0].toLowerCase() + "Button");
            button.setFont(new Font("SansSerif", Font.BOLD, 16));
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColors[i], 3),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            centerPanel.add(button);
        }
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setName("footerPanel");
        footerPanel.setBackground(new Color(25, 55, 109));
        footerPanel.setPreferredSize(new Dimension(1000, 40));
        
        JLabel copyrightLabel = new JLabel("© 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setName("copyrightLabel");
        copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.WHITE);
        footerPanel.add(copyrightLabel);
        
        // Add panels to main panel
        designPanel.add(headerPanel, BorderLayout.NORTH);
        designPanel.add(centerPanel, BorderLayout.CENTER);
        designPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add to card panel
        cardPanel.add(designPanel, "designPanel");
        cardLayout.show(cardPanel, "designPanel");
    }
    
    /**
     * Checks if the application is running in WindowBuilder design mode
     * 
     * @return true if running in design time, false otherwise
     */
    private boolean isDesignTime() {
        return Beans.isDesignTime() || System.getProperty("com.google.gdt.eclipse.designer") != null;
    }
    
    /**
     * Loads all application panels at runtime
     * 
     * Creates instances of all functional panels with appropriate service
     * dependencies and adds them to the card layout. This method is only
     * executed when running the actual application, not in design mode.
     */
    private void loadPanels() {
        if (authService != null) {
            // Load real MainMenuPanel
            MainMenuPanel mainMenuPanel = new MainMenuPanel(this);
            cardPanel.add(mainMenuPanel, "mainMenu");
            
            // Create all other panels
            AuthPanel authPanel = new AuthPanel(authService, this);
            ClientPanel clientPanel = new ClientPanel(clientService, this);
            CasePanel casePanel = new CasePanel(caseService, this);
            HearingPanel hearingPanel = new HearingPanel(hearingService, caseService, this);
            DocumentPanel documentPanel = new DocumentPanel(documentService, caseService, this);
            
            // Add to card panel
            cardPanel.add(authPanel, "auth");
            cardPanel.add(clientPanel, "client");
            cardPanel.add(casePanel, "case");
            cardPanel.add(hearingPanel, "hearing");
            cardPanel.add(documentPanel, "document");
            
            // Show login panel at application startup
            cardLayout.show(cardPanel, "auth");
        }
    }
    
    /**
     * Helper method to switch between panels
     * 
     * @param panelName The name identifier of the panel to display
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
    
    // Convenience methods for each panel
    
    /**
     * Shows the authentication panel
     */
    public void showAuthPanel() {
        cardLayout.show(cardPanel, "auth");
    }
    
    /**
     * Shows the main menu panel
     */
    public void showMainMenuPanel() {
        cardLayout.show(cardPanel, "mainMenu");
    }
    
    /**
     * Shows the client management panel
     */
    public void showClientPanel() {
        cardLayout.show(cardPanel, "client");
    }
    
    /**
     * Shows the case management panel
     */
    public void showCasePanel() {
        cardLayout.show(cardPanel, "case");
    }
    
    /**
     * Shows the hearing management panel
     */
    public void showHearingPanel() {
        cardLayout.show(cardPanel, "hearing");
    }
    
    /**
     * Shows the document management panel
     */
    public void showDocumentPanel() {
        cardLayout.show(cardPanel, "document");
    }
} 