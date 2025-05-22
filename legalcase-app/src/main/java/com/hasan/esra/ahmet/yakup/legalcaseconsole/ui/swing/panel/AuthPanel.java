package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme.LegalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @class AuthPanel
 * @brief Authentication panel for the Legal Case Tracker application.
 * 
 * This panel provides two interfaces:
 * 1. Login interface for existing users
 * 2. Registration interface for new users
 * 
 * It uses a CardLayout to switch between these interfaces and handles
 * all authentication-related operations through the AuthService.
 * 
 * @author Hasan Basri Taskin
 * @version 1.0
 * @date 2023
 */
public class AuthPanel extends JPanel {
    /** @brief Logger for this class */
    private static final Logger LOGGER = Logger.getLogger(AuthPanel.class.getName());
    
    /** @brief Service handling authentication operations */
    private final AuthService authService;
    
    /** @brief Reference to the main application frame */
    private final MainFrame mainFrame;
    
    /** @brief Card layout for switching between login and register screens */
    private final CardLayout cardLayout;
    
    /** @brief Panel containing the login and register cards */
    private final JPanel cardPanel;
    
    /** @brief Text field for username on the login screen */
    private JTextField usernameField;
    
    /** @brief Password field on the login screen */
    private JPasswordField passwordField;
    
    /** @brief Text field for username on the registration screen */
    private JTextField registerUsernameField;
    
    /** @brief Password field on the registration screen */
    private JPasswordField registerPasswordField;
    
    /** @brief Text field for email on the registration screen */
    private JTextField registerEmailField;
    
    /** @brief Text field for first name on the registration screen */
    private JTextField registerNameField;
    
    /** @brief Text field for surname on the registration screen */
    private JTextField registerSurnameField;
    
    /** @brief Combo box for selecting user role during registration */
    private JComboBox<UserRole> roleComboBox;

    /**
     * @brief Constructs an AuthPanel with the necessary services and parent frame.
     * 
     * Initializes the authentication panel with login and registration interfaces.
     * Applies the application's visual theme to all components.
     * 
     * @param authService The service responsible for authentication operations
     * @param mainFrame The parent frame that contains this panel
     */
    public AuthPanel(AuthService authService, MainFrame mainFrame) {
        this.authService = authService;
        this.mainFrame = mainFrame;
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        initializeUI();
        
        // Apply theme to this panel
        setBackground(LegalTheme.BACKGROUND_COLOR);
    }

    /**
     * @brief Initializes the UI components and layout.
     * 
     * Sets up the panel layout, creates the login and registration panels,
     * and adds them to the card panel with appropriate identifiers.
     * Shows the login panel by default.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Style the card panel
        cardPanel.setBackground(LegalTheme.BACKGROUND_COLOR);
        add(cardPanel, BorderLayout.CENTER);

        // Create login panel
        JPanel loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "login");

        // Create register panel
        JPanel registerPanel = createRegisterPanel();
        cardPanel.add(registerPanel, "register");

        // Show login panel by default
        cardLayout.show(cardPanel, "login");
    }

    /**
     * @brief Creates and returns the login panel with all its components.
     * 
     * Sets up a login form with username and password fields, and buttons for
     * login, registration navigation, and application exit.
     * 
     * @return JPanel The configured login panel
     */
    private JPanel createLoginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LegalTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel with title
        JPanel headerPanel = LegalTheme.createHeaderPanel("Legal Case Tracker");
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
                "User Login",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                LegalTheme.HEADER_FONT,
                LegalTheme.PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)));
        
        // Username label
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.gridwidth = 1;
        gbcUsernameLabel.gridy = 0;
        gbcUsernameLabel.gridx = 0;
        gbcUsernameLabel.anchor = GridBagConstraints.EAST;
        gbcUsernameLabel.insets = new Insets(10, 10, 10, 10);
        gbcUsernameLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(LegalTheme.NORMAL_FONT);
        usernameLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(usernameLabel, gbcUsernameLabel);
        
        // Username field
        GridBagConstraints gbcUsernameField = new GridBagConstraints();
        gbcUsernameField.gridwidth = 1;
        gbcUsernameField.gridy = 0;
        gbcUsernameField.gridx = 1;
        gbcUsernameField.anchor = GridBagConstraints.WEST;
        gbcUsernameField.insets = new Insets(10, 10, 10, 10);
        gbcUsernameField.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(usernameField);
        formPanel.add(usernameField, gbcUsernameField);

        // Password label
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.gridwidth = 1;
        gbcPasswordLabel.gridy = 1;
        gbcPasswordLabel.gridx = 0;
        gbcPasswordLabel.anchor = GridBagConstraints.EAST;
        gbcPasswordLabel.insets = new Insets(10, 10, 10, 10);
        gbcPasswordLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LegalTheme.NORMAL_FONT);
        passwordLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(passwordLabel, gbcPasswordLabel);
        
        // Password field
        GridBagConstraints gbcPasswordField = new GridBagConstraints();
        gbcPasswordField.gridwidth = 1;
        gbcPasswordField.gridy = 1;
        gbcPasswordField.gridx = 1;
        gbcPasswordField.anchor = GridBagConstraints.WEST;
        gbcPasswordField.insets = new Insets(10, 10, 10, 10);
        gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField(20);
        LegalTheme.applyTextFieldStyle(passwordField);
        formPanel.add(passwordField, gbcPasswordField);

        // Button panel
        GridBagConstraints gbcButtonPanel = new GridBagConstraints();
        gbcButtonPanel.gridy = 2;
        gbcButtonPanel.gridx = 0;
        gbcButtonPanel.gridwidth = 2;
        gbcButtonPanel.anchor = GridBagConstraints.CENTER;
        gbcButtonPanel.insets = new Insets(20, 10, 10, 10);
        gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");
        
        LegalTheme.applyButtonStyle(loginButton);
        LegalTheme.applyButtonStyle(registerButton);
        LegalTheme.applySecondaryButtonStyle(exitButton);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> cardLayout.show(cardPanel, "register"));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);
        formPanel.add(buttonPanel, gbcButtonPanel);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add footer with copyright
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel copyrightLabel = new JLabel("© 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        copyrightLabel.setForeground(Color.BLACK);
        footerPanel.add(copyrightLabel);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * @brief Creates and returns the registration panel with all its components.
     * 
     * Sets up a registration form with fields for username, password, email,
     * first name, last name, and user role. Includes buttons for registration
     * submission and navigation back to the login screen.
     * 
     * @return JPanel The configured registration panel
     */
    private JPanel createRegisterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(LegalTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel with title
        JPanel headerPanel = LegalTheme.createHeaderPanel("Legal Case Tracker");
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR),
                "User Registration",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                LegalTheme.HEADER_FONT,
                LegalTheme.PRIMARY_COLOR),
            new EmptyBorder(20, 20, 20, 20)));
        
        Insets defaultInsets = new Insets(8, 10, 8, 10);
        
        // Username label
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.gridwidth = 1;
        gbcUsernameLabel.gridy = 0;
        gbcUsernameLabel.gridx = 0;
        gbcUsernameLabel.anchor = GridBagConstraints.EAST;
        gbcUsernameLabel.insets = defaultInsets;
        gbcUsernameLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(LegalTheme.NORMAL_FONT);
        usernameLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(usernameLabel, gbcUsernameLabel);
        
        // Username field
        GridBagConstraints gbcUsernameField = new GridBagConstraints();
        gbcUsernameField.gridwidth = 1;
        gbcUsernameField.gridy = 0;
        gbcUsernameField.gridx = 1;
        gbcUsernameField.anchor = GridBagConstraints.WEST;
        gbcUsernameField.insets = defaultInsets;
        gbcUsernameField.fill = GridBagConstraints.HORIZONTAL;
        registerUsernameField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(registerUsernameField);
        formPanel.add(registerUsernameField, gbcUsernameField);

        // Password label
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.gridwidth = 1;
        gbcPasswordLabel.gridy = 1;
        gbcPasswordLabel.gridx = 0;
        gbcPasswordLabel.anchor = GridBagConstraints.EAST;
        gbcPasswordLabel.insets = defaultInsets;
        gbcPasswordLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LegalTheme.NORMAL_FONT);
        passwordLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(passwordLabel, gbcPasswordLabel);
        
        // Password field
        GridBagConstraints gbcPasswordField = new GridBagConstraints();
        gbcPasswordField.gridwidth = 1;
        gbcPasswordField.gridy = 1;
        gbcPasswordField.gridx = 1;
        gbcPasswordField.anchor = GridBagConstraints.WEST;
        gbcPasswordField.insets = defaultInsets;
        gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
        registerPasswordField = new JPasswordField(20);
        LegalTheme.applyTextFieldStyle(registerPasswordField);
        formPanel.add(registerPasswordField, gbcPasswordField);

        // Email label
        GridBagConstraints gbcEmailLabel = new GridBagConstraints();
        gbcEmailLabel.gridwidth = 1;
        gbcEmailLabel.gridy = 2;
        gbcEmailLabel.gridx = 0;
        gbcEmailLabel.anchor = GridBagConstraints.EAST;
        gbcEmailLabel.insets = defaultInsets;
        gbcEmailLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LegalTheme.NORMAL_FONT);
        emailLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(emailLabel, gbcEmailLabel);
        
        // Email field
        GridBagConstraints gbcEmailField = new GridBagConstraints();
        gbcEmailField.gridwidth = 1;
        gbcEmailField.gridy = 2;
        gbcEmailField.gridx = 1;
        gbcEmailField.anchor = GridBagConstraints.WEST;
        gbcEmailField.insets = defaultInsets;
        gbcEmailField.fill = GridBagConstraints.HORIZONTAL;
        registerEmailField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(registerEmailField);
        formPanel.add(registerEmailField, gbcEmailField);

        // Name label
        GridBagConstraints gbcNameLabel = new GridBagConstraints();
        gbcNameLabel.gridwidth = 1;
        gbcNameLabel.gridy = 3;
        gbcNameLabel.gridx = 0;
        gbcNameLabel.anchor = GridBagConstraints.EAST;
        gbcNameLabel.insets = defaultInsets;
        gbcNameLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel nameLabel = new JLabel("First Name:");
        nameLabel.setFont(LegalTheme.NORMAL_FONT);
        nameLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(nameLabel, gbcNameLabel);
        
        // Name field
        GridBagConstraints gbcNameField = new GridBagConstraints();
        gbcNameField.gridwidth = 1;
        gbcNameField.gridy = 3;
        gbcNameField.gridx = 1;
        gbcNameField.anchor = GridBagConstraints.WEST;
        gbcNameField.insets = defaultInsets;
        gbcNameField.fill = GridBagConstraints.HORIZONTAL;
        registerNameField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(registerNameField);
        formPanel.add(registerNameField, gbcNameField);

        // Surname label
        GridBagConstraints gbcSurnameLabel = new GridBagConstraints();
        gbcSurnameLabel.gridwidth = 1;
        gbcSurnameLabel.gridy = 4;
        gbcSurnameLabel.gridx = 0;
        gbcSurnameLabel.anchor = GridBagConstraints.EAST;
        gbcSurnameLabel.insets = defaultInsets;
        gbcSurnameLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel surnameLabel = new JLabel("Last Name:");
        surnameLabel.setFont(LegalTheme.NORMAL_FONT);
        surnameLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(surnameLabel, gbcSurnameLabel);
        
        // Surname field
        GridBagConstraints gbcSurnameField = new GridBagConstraints();
        gbcSurnameField.gridwidth = 1;
        gbcSurnameField.gridy = 4;
        gbcSurnameField.gridx = 1;
        gbcSurnameField.anchor = GridBagConstraints.WEST;
        gbcSurnameField.insets = defaultInsets;
        gbcSurnameField.fill = GridBagConstraints.HORIZONTAL;
        registerSurnameField = new JTextField(20);
        LegalTheme.applyTextFieldStyle(registerSurnameField);
        formPanel.add(registerSurnameField, gbcSurnameField);

        // Role label
        GridBagConstraints gbcRoleLabel = new GridBagConstraints();
        gbcRoleLabel.gridwidth = 1;
        gbcRoleLabel.gridy = 5;
        gbcRoleLabel.gridx = 0;
        gbcRoleLabel.anchor = GridBagConstraints.EAST;
        gbcRoleLabel.insets = defaultInsets;
        gbcRoleLabel.fill = GridBagConstraints.HORIZONTAL;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(LegalTheme.NORMAL_FONT);
        roleLabel.setForeground(LegalTheme.TEXT_COLOR);
        formPanel.add(roleLabel, gbcRoleLabel);
        
        // Role field
        GridBagConstraints gbcRoleField = new GridBagConstraints();
        gbcRoleField.gridwidth = 1;
        gbcRoleField.gridy = 5;
        gbcRoleField.gridx = 1;
        gbcRoleField.anchor = GridBagConstraints.WEST;
        gbcRoleField.insets = defaultInsets;
        gbcRoleField.fill = GridBagConstraints.HORIZONTAL;
        roleComboBox = new JComboBox<>(UserRole.values());
        roleComboBox.setFont(LegalTheme.NORMAL_FONT);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createLineBorder(LegalTheme.PRIMARY_COLOR, 1));
        formPanel.add(roleComboBox, gbcRoleField);

        // Button panel
        GridBagConstraints gbcButtonPanel = new GridBagConstraints();
        gbcButtonPanel.gridy = 6;
        gbcButtonPanel.gridx = 0;
        gbcButtonPanel.gridwidth = 2;
        gbcButtonPanel.anchor = GridBagConstraints.CENTER;
        gbcButtonPanel.insets = new Insets(20, 10, 10, 10);
        gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(LegalTheme.PANEL_BACKGROUND);
        
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");
        
        LegalTheme.applyButtonStyle(registerButton);
        LegalTheme.applySecondaryButtonStyle(backButton);

        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "login"));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        formPanel.add(buttonPanel, gbcButtonPanel);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add footer with copyright
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(LegalTheme.PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel copyrightLabel = new JLabel("© 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        copyrightLabel.setForeground(Color.BLACK);
        footerPanel.add(copyrightLabel);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * @brief Handles the login button action.
     * 
     * Validates the login form input, attempts authentication via the AuthService,
     * and handles success/failure cases appropriately. On success, navigates to the
     * main menu panel of the application.
     * 
     * Error handling includes:
     * - Form validation
     * - Authentication failures
     * - System exceptions during login
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password",
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            if (authService.login(username, password)) {
                JOptionPane.showMessageDialog(this,
                    "Login successful!",
                    "Welcome", 
                    JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel("mainMenu");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Authentication Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            JOptionPane.showMessageDialog(this,
                "An error occurred during login: " + e.getMessage(),
                "System Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * @brief Handles the registration button action.
     * 
     * Validates the registration form input, attempts user registration via
     * the AuthService, and handles success/failure cases appropriately.
     * On success, clears the form and returns to the login panel.
     * 
     * Error handling includes:
     * - Form validation
     * - System exceptions during registration
     */
    private void handleRegister() {
        String username = registerUsernameField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String email = registerEmailField.getText().trim();
        String name = registerNameField.getText().trim();
        String surname = registerSurnameField.getText().trim();
        UserRole role = (UserRole) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            authService.register(username, password, email, name, surname, role);
            
            JOptionPane.showMessageDialog(this,
                "Registration successful! You can now login.",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Clear fields
            registerUsernameField.setText("");
            registerPasswordField.setText("");
            registerEmailField.setText("");
            registerNameField.setText("");
            registerSurnameField.setText("");
            
            // Return to login panel
            cardLayout.show(cardPanel, "login");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during registration", e);
            JOptionPane.showMessageDialog(this,
                "An error occurred during registration: " + e.getMessage(),
                "System Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
} 