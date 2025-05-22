package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.DocumentMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class DocumentMenuTest {

    private ConsoleMenuManager consoleMenuManager;
    private DocumentMenu documentMenu;
    private DocumentService documentService;
    private CaseService caseService;
    private AuthService authService;
    private ClientService clientService;
    private HearingService hearingService;

    private DocumentDAO documentDAO;
    private CaseDAO caseDAO;
    private UserDAO userDAO;
    private ClientDAO clientDAO;

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Inner class that extends MenuManager for testing
    private class TestConsoleMenuManager extends ConsoleMenuManager {
        private boolean navigatedToMainMenu = false;

        public TestConsoleMenuManager(AuthService authService, ClientService clientService,
                                      CaseService caseService, HearingService hearingService,
                                      DocumentService documentService) {
            super(authService, clientService, caseService, hearingService, documentService);
        }

        @Override
        public void navigateToMainMenu() {
            navigatedToMainMenu = true;
            // Will be marked as successful without doing anything for testing
        }

        public boolean isNavigatedToMainMenu() {
            return navigatedToMainMenu;
        }
    }

    @Before
    public void setUp() throws SQLException {
        // Capture console output
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Set up test database
        TestDatabaseManager.createTables();

        // Create DAO objects
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());
        clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        documentDAO = new DocumentDAO(TestDatabaseManager.getConnectionSource());

        // Create service objects
        caseService = new CaseService(caseDAO, null); // ClientDAO can be null, not needed for our tests
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);
        documentService = new DocumentService(documentDAO, caseDAO);

        // Create test user and login
        authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // Create MenuManager with test version
        consoleMenuManager = new TestConsoleMenuManager(authService, clientService, caseService, hearingService, documentService);

        // Create DocumentMenu object
        documentMenu = new DocumentMenu(consoleMenuManager, documentService, caseService);
    }

    @After
    public void tearDown() throws SQLException {
        // Restore console output
        System.setOut(originalOut);

        // Reset Scanner
        UiConsoleHelper.resetScanner();

        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    @Test
    public void Test_Display_SelectReturnToMainMenu_Success() {
        // Setup - Configure Scanner to select "9. Return to Main Menu" option
        UiConsoleHelper.setScanner(new Scanner("9\n"));

        // Action
        documentMenu.display();

        // Verification - Check if MenuManager's navigateToMainMenu method was called
        assertTrue("Should return to main menu", ((TestConsoleMenuManager) consoleMenuManager).isNavigatedToMainMenu());
    }

    @Test
    public void Test_Display_SelectAddDocument_Success() throws SQLException {
        // First create a case
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");

        // Setup - Configure Scanner to select "1. Add New Document" option
        // Example document information and return to main menu (9)
        UiConsoleHelper.setScanner(new Scanner("1\n" + testCase.getId() + "\nTest Document\n1\nThis is a test document.\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Was the document created?
        List<Document> documents = documentService.getDocumentsByCaseId(testCase.getId());
        assertFalse("Document list should not be empty", documents.isEmpty());
        assertEquals("Document title should be correct", "Test Document", documents.get(0).getTitle());
        assertEquals("Document type should be correct", DocumentType.CONTRACT, documents.get(0).getType());
        assertEquals("Document content should be correct", "This is a test document.", documents.get(0).getContent());
    }

    @Test
    public void Test_Display_SelectAddDocument_CaseNotFound_Error() {
        // Setup - Configure Scanner with non-existent case ID
        UiConsoleHelper.setScanner(new Scanner("1\n9999\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Case not found' message", output.contains("Case with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectViewDocumentDetails_Success() throws SQLException {
        // First create a case and document
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document document = documentService.createDocument(testCase.getId(), "Test Document", DocumentType.CONTRACT, "This is a test document.");

        // Setup - Configure Scanner to select "2. View Document Details" option
        UiConsoleHelper.setScanner(new Scanner("2\n" + document.getId() + "\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Are document details in the output?
        String output = outContent.toString();
        assertTrue("Output should contain document ID", output.contains("Document ID: " + document.getId()));
        assertTrue("Output should contain document title", output.contains("Title: Test Document"));
        assertTrue("Output should contain document type", output.contains("Type: CONTRACT"));
        assertTrue("Output should contain document content", output.contains("Content: This is a test document."));
    }

    @Test
    public void Test_Display_SelectViewDocumentDetails_NonExistentDocument_NotFound() {
        // Setup - Configure Scanner with non-existent document ID
        UiConsoleHelper.setScanner(new Scanner("2\n9999\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Document not found' message", output.contains("Document with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectUpdateDocument_Success() throws SQLException {
        // First create a case and document
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document document = documentService.createDocument(testCase.getId(), "Old Title", DocumentType.CONTRACT, "Old content");

        // Setup - Configure Scanner to select "3. Update Document" option
        // Update all fields
        UiConsoleHelper.setScanner(new Scanner("3\n" + document.getId() + "\ny\nNew Title\ny\n2\ny\nNew content\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Was the document updated?
        Document updatedDocument = documentService.getDocumentById(document.getId()).orElse(null);
        assertNotNull("Document should be found", updatedDocument);
        assertEquals("Document title should be updated", "New Title", updatedDocument.getTitle());
        assertEquals("Document type should be updated", DocumentType.COURT_ORDER, updatedDocument.getType());
        assertEquals("Document content should be updated", "New content", updatedDocument.getContent());
    }

    @Test
    public void Test_Display_SelectUpdateDocument_NonExistentDocument_NotFound() {
        // Setup - Configure Scanner with non-existent document ID
        UiConsoleHelper.setScanner(new Scanner("3\n9999\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Document not found' message", output.contains("Document with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectDeleteDocument_Success() throws SQLException {
        // First create a case and document
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document document = documentService.createDocument(testCase.getId(), "Test Document", DocumentType.CONTRACT, "This is a test document.");

        // Setup - Configure Scanner to select "4. Delete Document" option
        UiConsoleHelper.setScanner(new Scanner("4\n" + document.getId() + "\ny\n\n9\n")); // Confirm deletion

        // Action
        documentMenu.display();

        // Verification - Was the document deleted?
        assertTrue("Document should be deleted", documentService.getDocumentById(document.getId()).isEmpty());
    }

    @Test
    public void Test_Display_SelectDeleteDocument_Cancelled_NotDeleted() throws SQLException {
        // First create a case and document
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document document = documentService.createDocument(testCase.getId(), "Test Document", DocumentType.CONTRACT, "This is a test document.");

        // Setup - Configure Scanner to select "4. Delete Document" option
        UiConsoleHelper.setScanner(new Scanner("4\n" + document.getId() + "\nn\n\n9\n")); // Cancel deletion

        // Action
        documentMenu.display();

        // Verification - Was the document not deleted?
        assertTrue("Document should not be deleted", documentService.getDocumentById(document.getId()).isPresent());

        // Is there a cancellation message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Operation cancelled' message", output.contains("Operation cancelled"));
    }

    @Test
    public void Test_Display_SelectViewDocumentsByCase_Success() throws SQLException {
        // First create a case and documents
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document doc1 = documentService.createDocument(testCase.getId(), "Document 1", DocumentType.CONTRACT, "Content 1");
        Document doc2 = documentService.createDocument(testCase.getId(), "Document 2", DocumentType.COURT_ORDER, "Content 2");

        // Setup - Configure Scanner to select "5. List Documents by Case" option
        UiConsoleHelper.setScanner(new Scanner("5\n" + testCase.getId() + "\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Are documents in the output?
        String output = outContent.toString();
        assertTrue("Output should contain document 1 ID", output.contains("ID: " + doc1.getId()));
        assertTrue("Output should contain document 2 ID", output.contains("ID: " + doc2.getId()));
    }

    @Test
    public void Test_Display_SelectViewDocumentsByType_Success() throws SQLException {
        // First create a case and documents of different types
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document contract = documentService.createDocument(testCase.getId(), "Contract Doc", DocumentType.CONTRACT, "Contract content");
        Document order = documentService.createDocument(testCase.getId(), "Order Doc", DocumentType.COURT_ORDER, "Order content");

        // Setup - Configure Scanner to select "6. List Documents by Type" option
        UiConsoleHelper.setScanner(new Scanner("6\n1\n\n9\n")); // 1 = CONTRACT type

        // Action
        documentMenu.display();

        // Verification - Are only CONTRACT type documents in the output?
        String output = outContent.toString();
        assertTrue("Output should contain contract document ID", output.contains("ID: " + contract.getId()));
        assertFalse("Output should not contain order document ID", output.contains("ID: " + order.getId()));
    }

    @Test
    public void Test_Display_SelectSearchDocuments_Success() throws SQLException {
        // First create a case and documents with specific keywords
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document doc1 = documentService.createDocument(testCase.getId(), "Important Contract", DocumentType.CONTRACT, "This is an important contract");
        Document doc2 = documentService.createDocument(testCase.getId(), "Regular Order", DocumentType.COURT_ORDER, "This is a regular order");

        // Setup - Configure Scanner to select "7. Search Documents" option
        UiConsoleHelper.setScanner(new Scanner("7\nimportant\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Are only documents with "important" keyword in the output?
        String output = outContent.toString();
        assertTrue("Output should contain document with 'important' keyword", output.contains("ID: " + doc1.getId()));
        assertFalse("Output should not contain document without 'important' keyword", output.contains("ID: " + doc2.getId()));
    }

    @Test
    public void Test_Display_SelectListDocumentsByCase_Success() throws SQLException {
        // First create two cases with different documents
        String caseNumber1 = "DOC2023-001";
        String caseTitle1 = "Test Case 1";
        Case testCase1 = caseService.createCase(caseNumber1, caseTitle1, CaseType.CIVIL, "Test case 1 description");
        Document doc1 = documentService.createDocument(testCase1.getId(), "Case 1 Document", DocumentType.CONTRACT, "This is a document for case 1");

        String caseNumber2 = "DOC2023-002";
        String caseTitle2 = "Test Case 2";
        Case testCase2 = caseService.createCase(caseNumber2, caseTitle2, CaseType.CRIMINAL, "Test case 2 description");
        Document doc2 = documentService.createDocument(testCase2.getId(), "Case 2 Document", DocumentType.EVIDENCE, "This is a document for case 2");

        // Setup - Configure Scanner to select "7. List Documents by Case" option
        UiConsoleHelper.setScanner(new Scanner("7\n" + testCase1.getId() + "\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Are only documents for selected case in the output?
        String output = outContent.toString();
        assertTrue("Output should contain document for case 1", output.contains("ID: " + doc1.getId()));
        assertTrue("Output should contain document title for case 1", output.contains("Title: Case 1 Document"));
        assertTrue("Output should contain document type for case 1", output.contains("Type: CONTRACT"));
        assertFalse("Output should not contain document for case 2", output.contains("ID: " + doc2.getId()));
        assertFalse("Output should not contain document title for case 2", output.contains("Title: Case 2 Document"));
    }

    @Test
    public void Test_Display_SelectListDocumentsByCase_EmptyCase_NoDocuments() throws SQLException {
        // Create a case without documents
        String caseNumber = "DOC2023-003";
        String caseTitle = "Empty Case";
        Case emptyCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Empty case description");

        // Setup - Configure Scanner to select "7. List Documents by Case" option
        UiConsoleHelper.setScanner(new Scanner("7\n" + emptyCase.getId() + "\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Is the appropriate message displayed?
        String output = outContent.toString();
        assertTrue("Output should indicate no documents for case", output.contains("There are no documents for this case"));
    }

    @Test
    public void Test_Display_SelectListDocumentsByCase_InvalidCase_Error() {
        // Setup - Configure Scanner with non-existent case ID
        UiConsoleHelper.setScanner(new Scanner("7\n9999\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain error message", output.contains("Error occurred while listing documents"));
    }

    @Test
    public void Test_Display_SelectExportDocument_Success() throws SQLException {
        // First create a case and document
        String caseNumber = "DOC2023-001";
        String caseTitle = "Test Case";
        Case testCase = caseService.createCase(caseNumber, caseTitle, CaseType.CIVIL, "Test case description");
        Document document = documentService.createDocument(testCase.getId(), "Test Document", DocumentType.CONTRACT, "This is a test document.");

        // Setup - Configure Scanner to select "8. Export Document" option
        UiConsoleHelper.setScanner(new Scanner("8\n" + document.getId() + "\n\n9\n"));

        // Action
        documentMenu.display();

        // Verification - Is there a success message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain export success message", output.contains("Document exported successfully"));
    }
}