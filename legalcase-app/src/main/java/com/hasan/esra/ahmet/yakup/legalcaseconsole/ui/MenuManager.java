package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu.*;

/**
 * @brief Manager class for coordinating menu navigation
 * @details This class orchestrates the navigation between different menus
 * in the application and manages the overall application lifecycle.
 * It serves as a controller for the user interface flow.
 */
public class MenuManager {
    /**
     * @brief Authentication service for user login/logout
     */
    private final AuthService authService;

    /**
     * @brief Service for client-related operations
     */
    private final ClientService clientService;

    /**
     * @brief Service for case-related operations
     */
    private final CaseService caseService;

    /**
     * @brief Service for hearing-related operations
     */
    private final HearingService hearingService;

    /**
     * @brief Service for document-related operations
     */
    private final DocumentService documentService;

    /**
     * @brief Menu for authentication operations
     */
    private AuthMenu authMenu;

    /**
     * @brief Main application menu
     */
    private MainMenu mainMenu;

    /**
     * @brief Menu for client management
     */
    private ClientMenu clientMenu;

    /**
     * @brief Menu for case management
     */
    private CaseMenu caseMenu;

    /**
     * @brief Menu for hearing management
     */
    private HearingMenu hearingMenu;

    /**
     * @brief Menu for document management
     */
    private DocumentMenu documentMenu;

    /**
     * @brief Flag indicating if the application is running
     */
    private boolean running = true;

    /**
     * @brief Constructor
     * @details Initializes the menu manager with all required services
     * and creates the menu objects
     * @param authService Authentication service
     * @param clientService Client service
     * @param caseService Case service
     * @param hearingService Hearing service
     * @param documentService Document service
     */
    public MenuManager(AuthService authService, ClientService clientService, CaseService caseService,
                       HearingService hearingService, DocumentService documentService) {
        this.authService = authService;
        this.clientService = clientService;
        this.caseService = caseService;
        this.hearingService = hearingService;
        this.documentService = documentService;

        initializeMenus();
    }

    /**
     * @brief Initialize all menu objects
     * @details Creates instances of all menu classes and injects necessary dependencies
     */
    private void initializeMenus() {
        authMenu = new AuthMenu(this, authService);
        mainMenu = new MainMenu(this, authService);
        clientMenu = new ClientMenu(this, clientService);
        caseMenu = new CaseMenu(this, caseService, clientService);
        hearingMenu = new HearingMenu(this, hearingService, caseService);
        documentMenu = new DocumentMenu(this, documentService, caseService);
    }

    /**
     * @brief Start the application menu system
     * @details Begins the main application loop, displaying the appropriate menu
     * based on authentication state until the application is exited
     */
    public void start() {
        while (running) {
            ConsoleHelper.clearScreen();
            if (!authService.isLoggedIn()) {
                authMenu.display();
            } else {
                mainMenu.display();
            }
        }

        ConsoleHelper.displayMessage("Thank you for using the Legal Case Tracker. Goodbye!");
        ConsoleHelper.closeScanner();
    }

    /**
     * @brief Display the main menu
     * @details Navigation method to show the main application menu
     */
    public void navigateToMainMenu() {
        mainMenu.display();
    }

    /**
     * @brief Display the client menu
     * @details Navigation method to show the client management menu
     */
    public void navigateToClientMenu() {
        clientMenu.display();
    }

    /**
     * @brief Display the case menu
     * @details Navigation method to show the case management menu
     */
    public void navigateToCaseMenu() {
        caseMenu.display();
    }

    /**
     * @brief Display the hearing menu
     * @details Navigation method to show the hearing management menu
     */
    public void navigateToHearingMenu() {
        hearingMenu.display();
    }

    /**
     * @brief Display the document menu
     * @details Navigation method to show the document management menu
     */
    public void navigateToDocumentMenu() {
        documentMenu.display();
    }

    /**
     * @brief Exit the application
     * @details Sets the running flag to false, which will terminate the main loop
     */
    public void exit() {
        running = false;
    }
}