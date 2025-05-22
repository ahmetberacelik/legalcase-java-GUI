package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main menu panel for the legal case management system
 * 
 * This class provides the primary navigation hub for the application,
 * containing buttons that allow users to access the different modules
 * of the legal case management system. It serves as the central dashboard
 * after successful login.
 * 
 * The panel features:
 * - A visually distinct header with application branding
 * - Navigation buttons for each major functional area
 * - Consistent styling and layout according to the application theme
 * - Logout functionality to return to the authentication screen
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class MainMenuPanel extends JPanel {
    
    /**
     * Reference to the main application frame
     * Used for navigation between different panels
     */
    private final MainFrame mainFrame;
    
    /**
     * UI Components for the main menu interface
     */
    private JPanel headerPanel;
    private JPanel menuPanel;
    private JPanel footerPanel;
    private JButton clientButton;
    private JButton caseButton;
    private JButton hearingButton;
    private JButton documentButton;
    private JButton logoutButton;
    
    /**
     * Constructs a new MainMenuPanel with necessary dependencies
     * 
     * Initializes the main menu panel with the main frame reference
     * and sets up the user interface components.
     *
     * @param mainFrame Reference to the main application frame for navigation
     */
    public MainMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setName("mainMenuPanel");
        initializeUI();
    }
    
    /**
     * Initializes and arranges all UI components
     * 
     * Sets up the main layout structure, including header, menu buttons,
     * and footer sections. Configures the panel with appropriate styling
     * and dimensions according to the application theme.
     */
    private void initializeUI() {
        // Main panel configuration
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setBackground(LegalTheme.BACKGROUND_COLOR);
        
        // Set minimum size for better appearance in WindowBuilder
        setPreferredSize(new Dimension(800, 600));
        
        // Header panel
        createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Menu panel
        createMenuPanel();
        add(menuPanel, BorderLayout.CENTER);
        
        // Footer panel
        createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the application header with logo and title
     * 
     * Sets up a visually distinct header panel that includes the application
     * logo and title for consistent branding. The header uses the primary theme
     * color with appropriate spacing and font styling.
     */
    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setName("headerPanel");
        headerPanel.setLayout(new BorderLayout(0, 0));
        headerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Container for logo and title
        JPanel titleContainer = new JPanel();
        titleContainer.setName("titleContainer");
        titleContainer.setLayout(new BorderLayout(15, 0));
        titleContainer.setBackground(LegalTheme.PRIMARY_COLOR);
        
        // Logo
        JLabel logoLabel = new JLabel("\u2696");
        logoLabel.setName("logoLabel");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setPreferredSize(new Dimension(80, 80));
        titleContainer.add(logoLabel, BorderLayout.WEST);
        
        // Title section
        JPanel textPanel = new JPanel();
        textPanel.setName("textPanel");
        textPanel.setLayout(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        
        // Main title
        JLabel titleLabel = new JLabel("LEGAL CASE TRACKER");
        titleLabel.setName("titleLabel");
        titleLabel.setFont(LegalTheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Efficient Legal Case Management System");
        subtitleLabel.setName("subtitleLabel");
        subtitleLabel.setFont(LegalTheme.SUBTITLE_FONT);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(subtitleLabel);
        
        titleContainer.add(textPanel, BorderLayout.CENTER);
        headerPanel.add(titleContainer, BorderLayout.CENTER);
    }
    
    /**
     * Creates the main navigation menu with function buttons
     * 
     * Sets up a panel containing buttons for accessing each major
     * functional area of the application. Each button is styled
     * distinctively and configured with appropriate action listeners
     * for navigation.
     */
    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setName("menuPanel");
        menuPanel.setLayout(new GridLayout(5, 1, 0, 25));
        menuPanel.setBackground(LegalTheme.BACKGROUND_COLOR);
        menuPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        // Client management button
        clientButton = new JButton("\u2630 Client Management");
        clientButton.setName("clientButton");
        clientButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        clientButton.setBackground(Color.WHITE);
        clientButton.setForeground(Color.BLACK);
        clientButton.setFocusPainted(false);
        clientButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.ACCENT_COLOR, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        clientButton.setHorizontalAlignment(SwingConstants.LEFT);
        clientButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        clientButton.setIconTextGap(10);
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.showClientPanel();
                }
            }
        });
        menuPanel.add(clientButton);
        
        // Case management button
        caseButton = new JButton("\u2630 Case Management");
        caseButton.setName("caseButton");
        caseButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        caseButton.setBackground(Color.WHITE);
        caseButton.setForeground(Color.BLACK);
        caseButton.setFocusPainted(false);
        caseButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.SUCCESS_COLOR, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        caseButton.setHorizontalAlignment(SwingConstants.LEFT);
        caseButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        caseButton.setIconTextGap(10);
        caseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.showCasePanel();
                }
            }
        });
        menuPanel.add(caseButton);
        
        // Hearing management button
        hearingButton = new JButton("\u2630 Hearing Management");
        hearingButton.setName("hearingButton");
        hearingButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        hearingButton.setBackground(Color.WHITE);
        hearingButton.setForeground(Color.BLACK);
        hearingButton.setFocusPainted(false);
        hearingButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(142, 68, 173), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        hearingButton.setHorizontalAlignment(SwingConstants.LEFT);
        hearingButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        hearingButton.setIconTextGap(10);
        hearingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.showHearingPanel();
                }
            }
        });
        menuPanel.add(hearingButton);
        
        // Document management button
        documentButton = new JButton("\u2630 Document Management");
        documentButton.setName("documentButton");
        documentButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        documentButton.setBackground(Color.WHITE);
        documentButton.setForeground(Color.BLACK);
        documentButton.setFocusPainted(false);
        documentButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(211, 84, 0), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        documentButton.setHorizontalAlignment(SwingConstants.LEFT);
        documentButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        documentButton.setIconTextGap(10);
        documentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.showDocumentPanel();
                }
            }
        });
        menuPanel.add(documentButton);
        
        // Logout button
        logoutButton = new JButton("\u2630 Logout");
        logoutButton.setName("logoutButton");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LegalTheme.DANGER_COLOR, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        logoutButton.setIconTextGap(10);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.showAuthPanel();
                }
            }
        });
        menuPanel.add(logoutButton);
        
        Dimension buttonSize = new Dimension(500, 70);
        clientButton.setPreferredSize(buttonSize);
        caseButton.setPreferredSize(buttonSize);
        hearingButton.setPreferredSize(buttonSize);
        documentButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);
    }
    
    /**
     * Creates the footer section with copyright information
     * 
     * Sets up a panel at the bottom of the screen that displays
     * copyright information and other footer content. Uses the
     * application's primary theme color for visual consistency.
     */
    private void createFooterPanel() {
        footerPanel = new JPanel();
        footerPanel.setName("footerPanel");
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel copyrightLabel = new JLabel("© 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setName("copyrightLabel");
        copyrightLabel.setFont(LegalTheme.SMALL_FONT);
        copyrightLabel.setForeground(Color.WHITE);
        footerPanel.add(copyrightLabel);
    }
} 