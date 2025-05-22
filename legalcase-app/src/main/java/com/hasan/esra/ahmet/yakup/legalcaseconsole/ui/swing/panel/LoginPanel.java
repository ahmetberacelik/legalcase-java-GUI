package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Authentication panel for the legal case management system
 * 
 * This class provides a user-friendly login interface that allows users
 * to authenticate themselves before accessing the main functionalities
 * of the legal case management application. It implements a secure login
 * flow with proper validation and error handling.
 * 
 * The panel features:
 * - Username and password input fields with appropriate validation
 * - Secure password handling with masked input
 * - Responsive UI with theme integration
 * - Error messages for invalid credentials
 * - Seamless integration with the application's authentication service
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class LoginPanel extends JPanel {
    /**
     * Text field for entering username
     * Used for capturing user identity during authentication
     */
    private JTextField usernameField;
    
    /**
     * Password field for secure password entry
     * Provides masked input to protect password visibility
     */
    private JPasswordField passwordField;
    
    /**
     * Button to initiate the login process
     * Triggers user authentication when clicked
     */
    private JButton loginButton;
    
    /**
     * Service for authentication operations
     * Handles verification of user credentials against the system
     */
    private AuthService authService;
    
    /**
     * Reference to the main application frame
     * Used for navigation after successful authentication
     */
    private MainFrame mainFrame;

    /**
     * Constructs a new LoginPanel with necessary dependencies
     * 
     * Initializes the login panel with authentication service and
     * main frame references, then sets up the user interface components.
     *
     * @param authService Service for authentication operations
     * @param mainFrame Reference to the main application frame for navigation
     */
    public LoginPanel(AuthService authService, MainFrame mainFrame) {
        this.authService = authService;
        this.mainFrame = mainFrame;
        initializeUI();
        
        // Apply theme to this panel
        LegalTheme.applyPanelStyle(this);
    }

    /**
     * Initializes the user interface components
     * 
     * Sets up the login form with username and password fields,
     * login button, and decorative elements (header and footer).
     * Arranges components using appropriate layout managers and
     * applies consistent styling according to the application theme.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(LegalTheme.BACKGROUND_COLOR);
        
        // Create header panel with logo and title
        JPanel headerPanel = LegalTheme.createHeaderPanel("Legal Case Tracker - Login");
        add(headerPanel, BorderLayout.NORTH);
        
        // Create login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
                "User Authentication",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                LegalTheme.HEADER_FONT,
                LegalTheme.PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)));
            
        // Common insets value
        Insets defaultInsets = new Insets(10, 10, 10, 10);
        
        // Username label
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.gridx = 0;
        gbcUsernameLabel.gridy = 0;
        gbcUsernameLabel.anchor = GridBagConstraints.EAST;
        gbcUsernameLabel.insets = defaultInsets;
        gbcUsernameLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(LegalTheme.NORMAL_FONT);
        usernameLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(usernameLabel, gbcUsernameLabel);

        // Username field
        GridBagConstraints gbcUsernameField = new GridBagConstraints();
        gbcUsernameField.gridx = 1;
        gbcUsernameField.gridy = 0;
        gbcUsernameField.anchor = GridBagConstraints.WEST;
        gbcUsernameField.insets = defaultInsets;
        gbcUsernameField.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(usernameField);
        formPanel.add(usernameField, gbcUsernameField);

        // Password label
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.gridx = 0;
        gbcPasswordLabel.gridy = 1;
        gbcPasswordLabel.anchor = GridBagConstraints.EAST;
        gbcPasswordLabel.insets = defaultInsets;
        gbcPasswordLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LegalTheme.NORMAL_FONT);
        passwordLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(passwordLabel, gbcPasswordLabel);

        // Password field
        GridBagConstraints gbcPasswordField = new GridBagConstraints();
        gbcPasswordField.gridx = 1;
        gbcPasswordField.gridy = 1;
        gbcPasswordField.anchor = GridBagConstraints.WEST;
        gbcPasswordField.insets = defaultInsets;
        gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        LegalTheme.applyTextFieldStyle(passwordField);
        formPanel.add(passwordField, gbcPasswordField);

        // Login button
        loginButton = new JButton("Login");
        LegalTheme.applyButtonStyle(loginButton);
        loginButton.addActionListener(e -> handleLogin());
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        buttonPanel.add(loginButton);
        
        // Button panel constraints
        GridBagConstraints gbcButtonPanel = new GridBagConstraints();
        gbcButtonPanel.gridx = 0;
        gbcButtonPanel.gridy = 3;
        gbcButtonPanel.gridwidth = 2;
        gbcButtonPanel.anchor = GridBagConstraints.CENTER;
        gbcButtonPanel.insets = new Insets(20, 10, 10, 10);
        gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbcButtonPanel);
        
        // Add form panel to center
        add(formPanel, BorderLayout.CENTER);
        
        // Add footer with copyright
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel copyrightLabel = new JLabel(" 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        copyrightLabel.setForeground(Color.BLACK);
        footerPanel.add(copyrightLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Processes user login attempt
     * 
     * Validates user input, authenticates credentials against the 
     * authentication service, and provides appropriate feedback.
     * Shows a success message and navigates to the main menu on successful
     * authentication, or displays error messages for invalid input or
     * failed authentication. Also manages cursor state during the
     * authentication process to provide visual feedback to the user.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            loginButton.setEnabled(false);
            
            if (authService.login(username, password)) {
                JOptionPane.showMessageDialog(this, 
                    "Login successful!", 
                    "Welcome", 
                    JOptionPane.INFORMATION_MESSAGE);
                // Redirect to main menu
                mainFrame.showPanel("mainMenu");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password!", 
                    "Authentication Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred during login: " + e.getMessage(),
                "System Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
            loginButton.setEnabled(true);
        }
    }
}