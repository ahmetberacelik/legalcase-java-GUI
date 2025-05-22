package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu.HearingMenu;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.Assert.*;

public class HearingMenuTest {

    private ConsoleMenuManager consoleMenuManager;
    private HearingMenu hearingMenu;
    private HearingService hearingService;
    private CaseService caseService;
    private AuthService authService;
    private DocumentService documentService;
    private ClientService clientService;

    private HearingDAO hearingDAO;
    private CaseDAO caseDAO;
    private UserDAO userDAO;

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Case to be used for testing
    private Case testCase;

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
        // Case to be used for testing
        testCase = new Case("TEST-2024-001", "Test Case", CaseType.CIVIL);

        // Capture console output
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Set up test database
        TestDatabaseManager.createTables();

        // Create DAO objects
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());
        caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        hearingDAO = new HearingDAO(TestDatabaseManager.getConnectionSource());
        ClientDAO clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        DocumentDAO documentDAO = new DocumentDAO(TestDatabaseManager.getConnectionSource());

        // Create service objects
        caseService = new CaseService(caseDAO, null); // ClientDAO can be null, not needed for our tests
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);
        hearingService = new HearingService(hearingDAO, caseDAO);
        documentService = new DocumentService(documentDAO, caseDAO);

        // Create test user and login
        authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // Create test case
        testCase = caseService.createCase(testCase.getCaseNumber(), testCase.getTitle(), testCase.getType(), "Test description");

        // Create MenuManager with test version
        consoleMenuManager = new TestConsoleMenuManager(authService, clientService, caseService, hearingService, documentService);

        // Create HearingMenu object
        hearingMenu = new HearingMenu(consoleMenuManager, hearingService, caseService);
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
    public void Test_Display_SelectAddHearing_Success() throws SQLException {
        // Setup - Configure Scanner to select "1. Add New Hearing" option
        // Example hearing information and return to main menu (10)
        LocalDate hearingDate = LocalDate.now().plusDays(7);
        LocalTime hearingTime = LocalTime.of(10, 0);
        
        UiConsoleHelper.setScanner(new Scanner("1\n" + testCase.getId() + "\n" + hearingDate.toString() + "\n" +
                                            hearingTime.toString() + "\nJudge Name\nCourt Building\nTest notes\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Was the hearing created?
        List<Hearing> hearings = hearingService.getHearingsByCaseId(testCase.getId());
        assertFalse("Hearing list should not be empty", hearings.isEmpty());
        assertEquals("Hearing judge should be correct", "Judge Name", hearings.get(0).getJudge());
        assertEquals("Hearing location should be correct", "Court Building", hearings.get(0).getLocation());
        assertEquals("Hearing notes should be correct", "Test notes", hearings.get(0).getNotes());
        assertEquals("Hearing status should be SCHEDULED", HearingStatus.SCHEDULED, hearings.get(0).getStatus());
    }

    @Test
    public void Test_Display_SelectAddHearing_CaseNotFound_Error() {
        // Setup - Configure Scanner with non-existent case ID
        UiConsoleHelper.setScanner(new Scanner("1\n9999\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Case not found' message", output.contains("Case with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectViewHearingDetails_Success() throws SQLException {
        // First add a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Judge", "Test Location", "Test Notes");

        // Setup - Configure Scanner to select "2. View Hearing Details" option
        UiConsoleHelper.setScanner(new Scanner("2\n" + hearing.getId() + "\n\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Are hearing details in the output?
        String output = outContent.toString();
        assertTrue("Output should contain hearing ID", output.contains("Hearing ID: " + hearing.getId()));
    }

    @Test
    public void Test_Display_SelectViewHearingDetails_NonExistentHearing_NotFound() {
        // Setup - Configure Scanner with non-existent hearing ID
        UiConsoleHelper.setScanner(new Scanner("2\n9999\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Hearing not found' message", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectUpdateHearing_Success() throws SQLException {
        // First add a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Old Judge", "Old Location", "Old Notes");

        // Setup - Configure Scanner to select "3. Update Hearing" option
        // Update all fields
        UiConsoleHelper.setScanner(new Scanner("3\n" + hearing.getId() + "\ny\nNew Judge\ny\nNew Location\ny\nNew Notes\ny\n1\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Was the hearing updated?
        Optional<Hearing> updatedHearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should be found", updatedHearingOpt.isPresent());
        Hearing updatedHearing = updatedHearingOpt.get();
        assertEquals("Hearing judge should be updated", "New Judge", updatedHearing.getJudge());
        assertEquals("Hearing location should be updated", "New Location", updatedHearing.getLocation());
        assertEquals("Hearing notes should be updated", "New Notes", updatedHearing.getNotes());
        assertEquals("Hearing status should be updated", HearingStatus.SCHEDULED, updatedHearing.getStatus());
    }

    @Test
    public void Test_Display_SelectUpdateHearing_NonExistentHearing_NotFound() {
        // Setup - Configure Scanner with non-existent hearing ID
        UiConsoleHelper.setScanner(new Scanner("3\n9999\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Hearing not found' message", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectRescheduleHearing_Success() throws SQLException {
        // First add a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Judge", "Test Location", "Test Notes");

        // Setup - Configure Scanner to select "4. Reschedule Hearing" option
        LocalDate newDate = LocalDate.now().plusDays(14);
        LocalTime newTime = LocalTime.of(14, 30);
        UiConsoleHelper.setScanner(new Scanner("4\n" + hearing.getId() + "\n" + newDate.toString() + "\n" + newTime.toString() + "\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Was the hearing rescheduled?
        Optional<Hearing> rescheduledHearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should be found", rescheduledHearingOpt.isPresent());
        Hearing rescheduledHearing = rescheduledHearingOpt.get();
        
        // Date and time check - Year, month, day, hour and minute check
        LocalDateTime expectedDateTime = LocalDateTime.of(newDate, newTime);
        assertEquals("Hearing date should be updated - Year", expectedDateTime.getYear(), rescheduledHearing.getHearingDate().getYear());
        assertEquals("Hearing date should be updated - Month", expectedDateTime.getMonth(), rescheduledHearing.getHearingDate().getMonth());
        assertEquals("Hearing date should be updated - Day", expectedDateTime.getDayOfMonth(), rescheduledHearing.getHearingDate().getDayOfMonth());
        assertEquals("Hearing time should be updated - Hour", expectedDateTime.getHour(), rescheduledHearing.getHearingDate().getHour());
        assertEquals("Hearing time should be updated - Minute", expectedDateTime.getMinute(), rescheduledHearing.getHearingDate().getMinute());
        
        // Hearing status check
        assertEquals("Hearing status should be SCHEDULED", HearingStatus.SCHEDULED, rescheduledHearing.getStatus());
        
        // Notes check - Rescheduling note should be added
        assertTrue("Hearing notes should contain rescheduling information", rescheduledHearing.getNotes().contains("Hearing rescheduled from"));
    }

    @Test
    public void Test_Display_SelectRescheduleHearing_NonExistentHearing_NotFound() {
        // Setup - Configure Scanner with non-existent hearing ID
        UiConsoleHelper.setScanner(new Scanner("4\n9999\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Hearing not found' message", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectUpdateHearingStatus_Success() throws SQLException {
        // First add a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Judge", "Test Location", "Test Notes");

        // Setup - Configure Scanner to select "5. Update Hearing Status" option
        UiConsoleHelper.setScanner(new Scanner("5\n" + hearing.getId() + "\n2\n\n10\n")); // 2 = COMPLETED status

        // Action
        hearingMenu.display();

        // Verification - Was the hearing status updated?
        Optional<Hearing> updatedHearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should be found", updatedHearingOpt.isPresent());
        assertEquals("Hearing status should be COMPLETED", HearingStatus.COMPLETED, updatedHearingOpt.get().getStatus());
    }

    @Test
    public void Test_Display_SelectUpdateHearingStatus_NonExistentHearing_NotFound() {
        // Setup - Configure Scanner with non-existent hearing ID
        UiConsoleHelper.setScanner(new Scanner("5\n9999\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Is there an error message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Hearing not found' message", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectDeleteHearing_Success() throws SQLException {
        // First add a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Judge", "Test Location", "Test Notes");

        // Setup - Configure Scanner to select "6. Delete Hearing" option
        UiConsoleHelper.setScanner(new Scanner("6\n" + hearing.getId() + "\ny\n\n10\n")); // Confirm deletion

        // Action
        hearingMenu.display();

        // Verification - Was the hearing deleted?
        Optional<Hearing> deletedHearingOpt = hearingService.getHearingById(hearing.getId());
        assertFalse("Hearing should be deleted", deletedHearingOpt.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteHearing_Cancelled_NotDeleted() throws SQLException {
        // First add a hearing
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Judge", "Test Location", "Test Notes");

        // Setup - Configure Scanner to select "6. Delete Hearing" option
        UiConsoleHelper.setScanner(new Scanner("6\n" + hearing.getId() + "\nn\n\n10\n")); // Cancel deletion

        // Action
        hearingMenu.display();

        // Verification - Was the hearing not deleted?
        Optional<Hearing> hearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Hearing should not be deleted", hearingOpt.isPresent());
        
        // Is there a cancellation message in the output?
        String output = outContent.toString();
        assertTrue("Output should contain 'Operation cancelled' message", output.contains("Operation cancelled"));
    }

    @Test
    public void Test_Display_SelectViewUpcomingHearings_Success() throws SQLException {
        // Add several hearings
        LocalDateTime pastDate = LocalDateTime.now().minusDays(7);
        LocalDateTime futureDate1 = LocalDateTime.now().plusDays(3);
        LocalDateTime futureDate2 = LocalDateTime.now().plusDays(7);
        
        // Past hearing
        hearingService.createHearing(testCase.getId(), pastDate, "Past Judge", "Past Location", "Past Notes");
        
        // Future hearings
        Hearing future1 = hearingService.createHearing(testCase.getId(), futureDate1, "Future Judge 1", "Future Location 1", "Future Notes 1");
        Hearing future2 = hearingService.createHearing(testCase.getId(), futureDate2, "Future Judge 2", "Future Location 2", "Future Notes 2");

        // Setup - Configure Scanner to select "7. List Upcoming Hearings" option
        UiConsoleHelper.setScanner(new Scanner("7\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Are future hearings in the output?
        String output = outContent.toString();
        assertTrue("Output should contain future hearing 1 ID", output.contains("ID: " + future1.getId()));
        assertTrue("Output should contain future hearing 2 ID", output.contains("ID: " + future2.getId()));
    }

    @Test
    public void Test_Display_SelectViewHearingsByCase_Success() throws SQLException {
        // Add several hearings
        LocalDateTime date1 = LocalDateTime.now().plusDays(3);
        LocalDateTime date2 = LocalDateTime.now().plusDays(7);
        
        Hearing hearing1 = hearingService.createHearing(testCase.getId(), date1, "Judge 1", "Location 1", "Notes 1");
        Hearing hearing2 = hearingService.createHearing(testCase.getId(), date2, "Judge 2", "Location 2", "Notes 2");

        // Setup - Configure Scanner to select "8. List Hearings by Case" option
        UiConsoleHelper.setScanner(new Scanner("8\n" + testCase.getId() + "\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Are hearings in the output?
        String output = outContent.toString();
        assertTrue("Output should contain hearing 1 ID", output.contains("ID: " + hearing1.getId()));
        assertTrue("Output should contain hearing 2 ID", output.contains("ID: " + hearing2.getId()));
    }

    @Test
    public void Test_Display_SelectViewHearingsByDateRange_Success() throws SQLException {
        // Add several hearings
        LocalDateTime date1 = LocalDateTime.now().plusDays(3); // Within date range
        LocalDateTime date2 = LocalDateTime.now().plusDays(7); // Within date range
        LocalDateTime date3 = LocalDateTime.now().plusDays(15); // Outside date range
        
        Hearing hearing1 = hearingService.createHearing(testCase.getId(), date1, "Judge 1", "Location 1", "Notes 1");
        Hearing hearing2 = hearingService.createHearing(testCase.getId(), date2, "Judge 2", "Location 2", "Notes 2");
        Hearing hearing3 = hearingService.createHearing(testCase.getId(), date3, "Judge 3", "Location 3", "Notes 3");

        // Setup - Configure Scanner to select "9. List Hearings by Date Range" option
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        UiConsoleHelper.setScanner(new Scanner("9\n" + startDate.toString() + "\n" + endDate.toString() + "\n\n10\n"));

        // Action
        hearingMenu.display();

        // Verification - Are hearings within date range in the output?
        String output = outContent.toString();
        assertTrue("Output should contain hearing 1 ID", output.contains("ID: " + hearing1.getId()));
        assertTrue("Output should contain hearing 2 ID", output.contains("ID: " + hearing2.getId()));
        assertFalse("Output should not contain hearing 3 ID", output.contains("ID: " + hearing3.getId()));
    }

    @Test
    public void Test_Display_SelectReturnToMainMenu_Success() {
        // Setup - Configure Scanner to select "10. Return to Main Menu" option
        UiConsoleHelper.setScanner(new Scanner("10\n"));

        // Action
        hearingMenu.display();

        // Verification - Was redirected to main menu?
        TestConsoleMenuManager testMenuManager = (TestConsoleMenuManager) consoleMenuManager;
        assertTrue("Should be redirected to main menu", testMenuManager.isNavigatedToMainMenu());
    }
}
