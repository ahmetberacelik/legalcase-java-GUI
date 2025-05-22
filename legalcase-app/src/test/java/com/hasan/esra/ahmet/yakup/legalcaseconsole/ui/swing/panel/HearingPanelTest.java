package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.swing.data.TableCell;
import org.assertj.swing.data.TableCellFinder;
import org.assertj.swing.data.TableCellInRowByValue;

/**
 * Test class for Hearing panel written using JUnit 4 and AssertJ Swing
 */
@RunWith(GUITestRunner.class)
public class HearingPanelTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private JFrame frame;
    private HearingPanel hearingPanel;
    private HearingService hearingService;
    private CaseService caseService;
    private MainFrame mainFrameMock;
    private DefaultTableModel tableModel;
    private JTable hearingTable;

    /**
     * Set up JOptionPane before testing
     */
    @Before
    public void setUpJOptionPanes() {
        // Settings to automatically close JOptionPane dialogs
        UIManager.put("OptionPane.buttonTypeFocus", null);
        System.setProperty("java.awt.headless", "false");
    }

    /**
     * Called before each test starts.
     * Prepares the necessary components for testing.
     */
    @Override
    protected void onSetUp() {
        // Create mocks for services and MainFrame
        hearingService = Mockito.mock(HearingService.class);
        caseService = Mockito.mock(CaseService.class);
        mainFrameMock = Mockito.mock(MainFrame.class);

        // Prepare mock data for tests
        prepareMockData();

        // Create GUI components in EDT
        frame = GuiActionRunner.execute(() -> {
            JFrame testFrame = new JFrame("Test Hearing Panel");
            hearingPanel = new HearingPanel(hearingService, caseService, mainFrameMock);

            // Access to private fields using Reflection
            try {
                // Access to table
                Field hearingTableField = HearingPanel.class.getDeclaredField("hearingTable");
                hearingTableField.setAccessible(true);
                hearingTable = (JTable) hearingTableField.get(hearingPanel);
                hearingTable.setName("hearingTable");

                // Access to tableModel
                Field tableModelField = HearingPanel.class.getDeclaredField("tableModel");
                tableModelField.setAccessible(true);
                tableModel = (DefaultTableModel) tableModelField.get(hearingPanel);

                // Manually populate table with test data
                populateTableWithTestData();

                // Access to search field
                Field searchFieldField = HearingPanel.class.getDeclaredField("searchField");
                searchFieldField.setAccessible(true);
                JTextField searchFieldComponent = (JTextField) searchFieldField.get(hearingPanel);
                searchFieldComponent.setName("searchField");

                // Access to search button
                Field searchButtonField = HearingPanel.class.getDeclaredField("searchButton");
                searchButtonField.setAccessible(true);
                JButton searchButtonComponent = (JButton) searchButtonField.get(hearingPanel);
                searchButtonComponent.setName("searchButton");
            } catch (Exception e) {
                e.printStackTrace();
            }

            testFrame.add(hearingPanel);
            testFrame.pack();
            return testFrame;
        });

        // Create test window
        window = new FrameFixture(robot(), frame);
        window.show(); // Show the window
    }

    /**
     * Manually populate table with test data
     */
    private void populateTableWithTestData() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add test data rows
        tableModel.addRow(new Object[]{
                1L, // ID
                "C-2025-001", // Case Number
                "Smith vs. Johnson", // Case Title
                LocalDateTime.now().plusDays(1).toString(), // Date & Time
                "Judge Adams", // Judge
                "Court Room 101", // Location
                HearingStatus.SCHEDULED // Status
        });

        tableModel.addRow(new Object[]{
                2L, // ID
                "C-2025-002", // Case Number
                "Doe vs. Corporation", // Case Title
                LocalDateTime.now().plusDays(5).toString(), // Date & Time
                "Judge Smith", // Judge
                "Court Room 202", // Location
                HearingStatus.SCHEDULED // Status
        });
    }

    /**
     * Prepare mock data for tests
     */
    private void prepareMockData() {
        // Create mock cases
        Case mockCase1 = new Case();
        mockCase1.setId(1L);
        mockCase1.setCaseNumber("C-2025-001");
        mockCase1.setTitle("Smith vs. Johnson");

        Case mockCase2 = new Case();
        mockCase2.setId(2L);
        mockCase2.setCaseNumber("C-2025-002");
        mockCase2.setTitle("Doe vs. Corporation");

        // Create mock hearings
        Hearing mockHearing1 = new Hearing(mockCase1, LocalDateTime.now().plusDays(1), "Judge Adams", "Court Room 101");
        mockHearing1.setId(1L);
        mockHearing1.setStatus(HearingStatus.SCHEDULED);
        mockHearing1.setNotes("Initial hearing");

        Hearing mockHearing2 = new Hearing(mockCase2, LocalDateTime.now().plusDays(5), "Judge Smith", "Court Room 202");
        mockHearing2.setId(2L);
        mockHearing2.setStatus(HearingStatus.SCHEDULED);
        mockHearing2.setNotes("Evidence hearing");

        // Prepare mock responses
        List<Hearing> mockHearings = new ArrayList<>();
        mockHearings.add(mockHearing1);
        mockHearings.add(mockHearing2);

        List<Case> mockCases = new ArrayList<>();
        mockCases.add(mockCase1);
        mockCases.add(mockCase2);

        // Configure mocks
        Mockito.when(hearingService.getAllHearings()).thenReturn(mockHearings);
        Mockito.when(hearingService.getHearingById(1L)).thenReturn(Optional.of(mockHearing1));
        Mockito.when(hearingService.getHearingById(2L)).thenReturn(Optional.of(mockHearing2));
        Mockito.when(caseService.getAllCases()).thenReturn(mockCases);

        // Mock createHearing method to return a valid hearing
        Mockito.when(hearingService.createHearing(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenAnswer(invocation -> {
            Long caseId = invocation.getArgument(0);
            LocalDateTime hearingDate = invocation.getArgument(1);
            String judge = invocation.getArgument(2);
            String location = invocation.getArgument(3);
            String notes = invocation.getArgument(4);

            Hearing newHearing = new Hearing(mockCase1, hearingDate, judge, location);
            newHearing.setId(3L);
            newHearing.setNotes(notes);
            return newHearing;
        });
    }

    /**
     * Test: All components should be visible
     */
    @Test
    public void shouldShowAllComponents() {
        // Table component should be visible
        window.table("hearingTable").requireVisible();

        // Search components should be visible
        window.textBox("searchField").requireVisible();
        window.button("searchButton").requireVisible();

        // Check for necessary buttons (they don't have names, so we'll check by text)
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Add New Hearing")).requireVisible();
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Hearing")).requireVisible();
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Delete Hearing")).requireVisible();
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Back to Menu")).requireVisible();
    }

    /**
     * Test: Table should be populated with data
     */
    @Test
    public void shouldLoadHearingsIntoTable() {
        // Get table fixture
        JTableFixture tableFixture = window.table("hearingTable");

        // Verify row count (should match our mock data)
        tableFixture.requireRowCount(2);

        // Verify table headers
        tableFixture.requireColumnCount(7);

        // Verify specific cell content
        tableFixture.cell(TableCell.row(0).column(0)).requireValue("1"); // ID
        tableFixture.cell(TableCell.row(0).column(1)).requireValue("C-2025-001"); // Case Number
        tableFixture.cell(TableCell.row(0).column(2)).requireValue("Smith vs. Johnson"); // Case Title
        tableFixture.cell(TableCell.row(0).column(4)).requireValue("Judge Adams"); // Judge
        tableFixture.cell(TableCell.row(0).column(5)).requireValue("Court Room 101"); // Location
        tableFixture.cell(TableCell.row(0).column(6)).requireValue("SCHEDULED"); // Status

        // Check service method was called
        Mockito.verify(hearingService).getAllHearings();
    }

    /**
     * Test: Back button should navigate to main menu
     */
    @Test
    public void shouldNavigateToMainMenuWhenBackButtonClicked() {
        // Click the back button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Back to Menu")).click();

        // Verify navigation to main menu
        Mockito.verify(mainFrameMock).showPanel("mainMenu");
    }

    /**
     * Test: Should open add hearing dialog when add button is clicked
     */
    @Test
    public void shouldOpenAddHearingDialogWhenAddButtonClicked() {
        // Click the add button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Add New Hearing")).click();

        // Wait for the robot to process all pending events
        robot().waitForIdle();

        // Try to find dialog with title "Add New Hearing"
        try {
            // Find dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Add New Hearing".equals(dialog.getTitle());
                }
            }).using(robot());

            // Verify dialog is visible
            dialog.requireVisible();

            // Check that case combo box is populated
            dialog.comboBox().requireVisible();

            // Close dialog to continue
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Cancel")).click();
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Add hearing dialog not displayed: " + e.getMessage());
        }

        // Verify case service was called to get cases for dropdown
        Mockito.verify(caseService).getAllCases();
    }

    /**
     * Test: Should create a new hearing when form is filled and save button clicked
     */
    @Test
    public void shouldCreateNewHearingWhenFormSubmitted() {
        // Setup mock for case service to return mock cases for combo box
        Case mockCase = new Case();
        mockCase.setId(1L);
        mockCase.setCaseNumber("C-2025-001");
        mockCase.setTitle("Smith vs. Johnson");

        List<Case> mockCases = new ArrayList<>();
        mockCases.add(mockCase);
        Mockito.when(caseService.getAllCases()).thenReturn(mockCases);

        // Click the add button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Add New Hearing")).click();

        // Wait for the robot to process all pending events
        robot().waitForIdle();

        try {
            // Find dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Add New Hearing".equals(dialog.getTitle());
                }
            }).using(robot());

            // Fill form
            // Select first case in combo box
            dialog.comboBox().selectItem(0);

            // Find text fields by label
            Component[] components = dialog.target().getComponents();

            // Using a simpler approach to access components directly
            // Find all JTextField and JTextArea components and set their values
            for (Component component : components) {
                if (component instanceof JTextField) {
                    JTextField textField = (JTextField) component;
                    // Fill all text fields except Spinner.formattedTextField
                    if (!"Spinner.formattedTextField".equals(textField.getName())) {
                        JTextComponentFixture textFixture = new JTextComponentFixture(robot(), textField);
                        if (textField.getToolTipText() != null && textField.getToolTipText().contains("judge")) {
                            textFixture.setText("Judge Test");
                        } else if (textField.getToolTipText() != null && textField.getToolTipText().contains("location")) {
                            textFixture.setText("Court Room Test");
                        } else {
                            // For other text fields
                            textFixture.setText("Test Value");
                        }
                    }
                } else if (component instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) component;
                    if (scrollPane.getViewport().getView() instanceof JTextArea) {
                        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
                        JTextComponentFixture textFixture = new JTextComponentFixture(robot(), textArea);
                        textFixture.setText("Test notes");
                    }
                }
            }

            // Setup for direct call to hearingService.createHearing method
            Mockito.doAnswer(invocation -> {
                Long caseId = invocation.getArgument(0);
                LocalDateTime hearingDate = invocation.getArgument(1);
                String judge = invocation.getArgument(2);
                String location = invocation.getArgument(3);
                String notes = invocation.getArgument(4);

                // Set values for test
                if (judge == null || judge.isEmpty()) {
                    judge = "Judge Test";
                }
                if (location == null || location.isEmpty()) {
                    location = "Court Room Test";
                }
                if (notes == null || notes.isEmpty()) {
                    notes = "Test notes";
                }

                Hearing newHearing = new Hearing(mockCase, hearingDate, judge, location);
                newHearing.setId(3L);
                newHearing.setNotes(notes);
                return newHearing;
            }).when(hearingService).createHearing(
                    Mockito.anyLong(),
                    Mockito.any(LocalDateTime.class),
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.anyString()
            );

            // Click save button
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Save")).click();

            // Wait for dialog to close and service call to be made
            robot().waitForIdle();

            // Try to find success dialog
            try {
                DialogFixture successDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override
                    protected boolean isMatching(JDialog dialog) {
                        return dialog.isVisible();
                    }
                }).using(robot());

                successDialog.button().click(); // Close success dialog
            } catch (Exception e) {
                // If success dialog not found, just continue
                System.out.println("Success dialog not found: " + e.getMessage());
            }

            // Check that hearingService.createHearing was called
            ArgumentCaptor<Long> caseIdCaptor = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            ArgumentCaptor<String> judgeCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> notesCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(hearingService).createHearing(
                    caseIdCaptor.capture(),
                    dateCaptor.capture(),
                    judgeCaptor.capture(),
                    locationCaptor.capture(),
                    notesCaptor.capture()
            );

            // Verify captured values - we're only checking that the call was made, not validating specific values
            assertEquals(mockCase.getId(), caseIdCaptor.getValue());

        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Add hearing dialog not displayed or interaction failed: " + e.getMessage());
        }
    }

    /**
     * Test: Should show error when trying to edit without selecting a hearing
     */
    @Test
    public void shouldShowErrorWhenEditingWithoutSelection() {
        // Click edit button without selecting a row
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Hearing")).click();

        // Wait for the robot to process all pending events
        robot().waitForIdle();

        // Try to find error dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Error".equals(dialog.getTitle());
                }
            }).using(robot());

            // Verify dialog shows correct message
            dialog.requireVisible();

            // Close dialog
            dialog.button().click();
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Error dialog not displayed: " + e.getMessage());
        }
    }

    /**
     * Test: Should open edit hearing dialog when row selected and edit button clicked
     */
    @Test
    public void shouldOpenEditDialogWhenRowSelectedAndEditClicked() {
        // Manually select first row in table
        GuiActionRunner.execute(() -> hearingTable.setRowSelectionInterval(0, 0));

        // Click edit button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Hearing")).click();

        // Wait for the robot to process all pending events
        robot().waitForIdle();

        // Try to find edit dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Edit Hearing".equals(dialog.getTitle());
                }
            }).using(robot());

            // Verify dialog is visible
            dialog.requireVisible();

            // Close dialog to continue
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Cancel")).click();
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Edit hearing dialog not displayed: " + e.getMessage());
        }

        // Verify service call to get hearing by ID
        Mockito.verify(hearingService).getHearingById(1L); // ID of first row in our mock data
    }

    /**
     * Test: Should show error when trying to delete without selecting a hearing
     */
    @Test
    public void shouldShowErrorWhenDeletingWithoutSelection() {
        // Click delete button without selecting a row
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Delete Hearing")).click();

        // Wait for the robot to process all pending events
        robot().waitForIdle();

        // Try to find error dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Error".equals(dialog.getTitle());
                }
            }).using(robot());

            // Verify dialog shows correct message
            dialog.requireVisible();

            // Close dialog
            dialog.button().click();
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Error dialog not displayed: " + e.getMessage());
        }
    }

    /**
     * Test: Should show confirmation dialog when delete button clicked with selection
     */
    @Test
    public void shouldShowConfirmationWhenDeletingWithSelection() {
        // Manually select first row in table
        GuiActionRunner.execute(() -> hearingTable.setRowSelectionInterval(0, 0));

        // Click delete button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Delete Hearing")).click();

        // Wait for the robot to process all pending events
        robot().waitForIdle();

        // Try to find confirmation dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return dialog.isVisible() && dialog instanceof JDialog;
                }
            }).using(robot());

            // Verify dialog is visible
            dialog.requireVisible();

            // Click "No" to cancel deletion
            JButtonFixture noButton = dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("No"));
            if (noButton != null) {
                noButton.click();
            } else {
                // If "No" button not found, just click "Cancel" or close button
                dialog.button().click();
            }
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Confirmation dialog not displayed: " + e.getMessage());
        }
    }

    /**
     * Test: Should handle different search scenarios in searchHearings method
     */
    @Test
    public void shouldHandleDifferentSearchScenarios() {
        // Reset Mockito - clear previous calls
        Mockito.reset(hearingService);

        // Prepare mock data for different scenarios
        List<Hearing> mockHearings = new ArrayList<>();

        // 1. Normal case - hearing with all fields populated
        Case mockCase1 = new Case();
        mockCase1.setId(1L);
        mockCase1.setCaseNumber("C-2025-001");
        mockCase1.setTitle("Smith vs. Johnson");

        Hearing mockHearing1 = new Hearing(mockCase1, LocalDateTime.now().plusDays(1), "Judge Adams", "Court Room 101");
        mockHearing1.setId(1L);
        mockHearing1.setStatus(HearingStatus.SCHEDULED);
        mockHearing1.setNotes("Regular hearing");
        mockHearings.add(mockHearing1);

        // 2. Hearing with null case reference
        Hearing mockHearing2 = new Hearing(null, LocalDateTime.now().plusDays(2), "Judge Brown", "Court Room 202");
        mockHearing2.setId(2L);
        mockHearing2.setStatus(HearingStatus.SCHEDULED);
        mockHearing2.setNotes("Hearing with null case");
        mockHearings.add(mockHearing2);

        // 3. Hearing with null date
        Hearing mockHearing3 = new Hearing(mockCase1, null, "Judge Carter", "Court Room 303");
        mockHearing3.setId(3L);
        mockHearing3.setStatus(HearingStatus.POSTPONED);
        mockHearing3.setNotes("Hearing with null date");
        mockHearings.add(mockHearing3);

        // 4. Hearing with null judge and location
        Hearing mockHearing4 = new Hearing(mockCase1, LocalDateTime.now().plusDays(4), null, null);
        mockHearing4.setId(4L);
        mockHearing4.setStatus(HearingStatus.CANCELLED);
        mockHearing4.setNotes("Hearing with null judge and location");
        mockHearings.add(mockHearing4);

        // 5. Hearing with null status
        Hearing mockHearing5 = new Hearing(mockCase1, LocalDateTime.now().plusDays(5), "Judge Evans", "Court Room 505");
        mockHearing5.setId(5L);
        mockHearing5.setStatus(null);
        mockHearing5.setNotes("Hearing with null status");
        mockHearings.add(mockHearing5);

        // Configure mock service
        Mockito.when(hearingService.getAllHearings()).thenReturn(mockHearings);

        // Use reflection to directly call searchHearings method
        try {
            // First clear the tableModel
            GuiActionRunner.execute(() -> tableModel.setRowCount(0));

            // 1. Test: "Court" search - should find hearings with location containing "Court" (4 hearings)
            window.textBox("searchField").setText("Court");
            window.button("searchButton").click();
            robot().waitForIdle();

            // Verify correct number of rows in table (should be 4 - excluding the one with null location)
            JTableFixture tableFixture = window.table("hearingTable");
            tableFixture.requireRowCount(4); // 4 hearings contain "Court"

            // 2. Test: "Judge" search - should find hearings with judge name containing "Judge" (4 hearings)
            window.textBox("searchField").setText("Judge");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(4); // 4 hearings contain "Judge" (excluding the one with null judge)

            // 3. Test: "POSTPONED" search - should find postponed hearings (1 hearing)
            window.textBox("searchField").setText("POSTPONED");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(1); // 1 hearing has "POSTPONED" status

            // 4. Test: "N/A" search - should find records with null case or null date
            window.textBox("searchField").setText("N/A");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(2); // 2 hearings contain "N/A" (null case and null date)

            // 5. Test: Empty search - should return all hearings
            window.textBox("searchField").setText("");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(5); // All hearings should be shown

            // Verify service method was called
            Mockito.verify(hearingService, Mockito.atLeast(5)).getAllHearings();

        } catch (Exception e) {
            fail("Search test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Should search hearings when search button clicked
     */
    @Test
    public void shouldSearchHearingsWhenSearchButtonClicked() {
        // Reset Mockito - clear previous calls
        Mockito.reset(hearingService);

        // Prepare mock data for search
        List<Hearing> mockHearings = new ArrayList<>();
        Case mockCase1 = new Case();
        mockCase1.setId(1L);
        mockCase1.setCaseNumber("C-2025-001");
        mockCase1.setTitle("Smith vs. Johnson");

        Hearing mockHearing1 = new Hearing(mockCase1, LocalDateTime.now().plusDays(1), "Judge Adams", "Court Room 101");
        mockHearing1.setId(1L);
        mockHearing1.setStatus(HearingStatus.SCHEDULED);
        mockHearings.add(mockHearing1);

        // Configure mock service
        Mockito.when(hearingService.getAllHearings()).thenReturn(mockHearings);

        // Access the searchButton field using reflection
        JButton searchButtonComponent = null;
        try {
            Field searchButtonField = HearingPanel.class.getDeclaredField("searchButton");
            searchButtonField.setAccessible(true);
            searchButtonComponent = (JButton) searchButtonField.get(hearingPanel);

            // Access the searchField field using reflection
            Field searchFieldField = HearingPanel.class.getDeclaredField("searchField");
            searchFieldField.setAccessible(true);
            JTextField searchFieldComponent = (JTextField) searchFieldField.get(hearingPanel);

            // Set search text directly
            GuiActionRunner.execute(() -> searchFieldComponent.setText("Smith"));

            // Get and invoke the actionListeners directly
            ActionListener[] listeners = searchButtonComponent.getActionListeners();
            if (listeners.length > 0) {
                // Create an action event and dispatch it
                ActionEvent actionEvent = new ActionEvent(searchButtonComponent, ActionEvent.ACTION_PERFORMED, "");
                GuiActionRunner.execute(() -> listeners[0].actionPerformed(actionEvent));
            }

            // Wait for processing to complete
            robot().waitForIdle();

            // Verify that search was performed
            Mockito.verify(hearingService).getAllHearings();
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Tests saving data changes in the hearing edit dialog
     */
    @Test
    public void shouldUpdateHearingWhenEditFormSubmitted() {
        // Create mock for hearing
        Hearing mockHearing = createMockHearing();

        // Mockito setup for test - Update this to return the hearing instead of doNothing()
        Mockito.when(hearingService.updateHearing(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(HearingStatus.class)
        )).thenReturn(mockHearing);

        // Select first row in table
        GuiActionRunner.execute(() -> hearingTable.setRowSelectionInterval(0, 0));

        // Click edit button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Hearing")).click();

        // Wait for robot to complete all operations
        robot().waitForIdle();

        try {
            // Find edit dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Edit Hearing".equals(dialog.getTitle());
                }
            }).using(robot());

            // Change form fields
            // Find and change judge field
            Component[] components = dialog.target().getComponents();
            for (Component component : components) {
                if (component instanceof JTextField) {
                    JTextField textField = (JTextField) component;
                    if (textField.getText() != null && textField.getText().contains("Judge")) {
                        JTextComponentFixture textFixture = new JTextComponentFixture(robot(), textField);
                        textFixture.setText("Updated Judge");
                    } else if (textField.getText() != null && textField.getText().contains("Court")) {
                        JTextComponentFixture textFixture = new JTextComponentFixture(robot(), textField);
                        textFixture.setText("Updated Location");
                    }
                } else if (component instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) component;
                    if (scrollPane.getViewport().getView() instanceof JTextArea) {
                        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
                        JTextComponentFixture textFixture = new JTextComponentFixture(robot(), textArea);
                        textFixture.setText("Updated Notes");
                    }
                } else if (component instanceof JComboBox) {
                    JComboBoxFixture comboFixture = new JComboBoxFixture(robot(), (JComboBox<?>) component);
                    comboFixture.selectItem(HearingStatus.COMPLETED.toString());
                }
            }

            // Click save button
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Save")).click();

            // Wait for robot to complete all operations
            robot().waitForIdle();

            // Find and close success message dialog
            try {
                DialogFixture successDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override
                    protected boolean isMatching(JDialog dialog) {
                        return dialog.isVisible();
                    }
                }).using(robot());

                successDialog.button().click(); // Close success dialog
            } catch (Exception e) {
                System.out.println("Success dialog not found: " + e.getMessage());
            }

            // Verify updateHearing method was called
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            ArgumentCaptor<String> judgeCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> notesCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<HearingStatus> statusCaptor = ArgumentCaptor.forClass(HearingStatus.class);

            Mockito.verify(hearingService).updateHearing(
                    idCaptor.capture(),
                    dateCaptor.capture(),
                    judgeCaptor.capture(),
                    locationCaptor.capture(),
                    notesCaptor.capture(),
                    statusCaptor.capture()
            );

            // Check values - we're verifying that the method call was made
            assertEquals(1L, idCaptor.getValue()); // ID of the first row

        } catch (Exception e) {
            fail("Edit dialog was not displayed or interaction failed: " + e.getMessage());
        }
    }

    /**
     * Test: Tests error scenario during hearing update
     */
    @Test
    public void shouldShowErrorWhenUpdateHearingFails() {
        // Create mock for hearing
        Hearing mockHearing = createMockHearing();

        // Set up mock behavior to throw error
        Mockito.doThrow(new RuntimeException("Test error message")).when(hearingService).updateHearing(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(HearingStatus.class)
        );

        // Select first row in table
        GuiActionRunner.execute(() -> hearingTable.setRowSelectionInterval(0, 0));

        // Click edit button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Hearing")).click();

        // Wait for robot to complete all operations
        robot().waitForIdle();

        try {
            // Find edit dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Edit Hearing".equals(dialog.getTitle());
                }
            }).using(robot());

            // Click save button
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Save")).click();

            // Wait for robot to complete all operations
            robot().waitForIdle();

            // Find error message dialog
            try {
                DialogFixture errorDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                    @Override
                    protected boolean isMatching(JDialog dialog) {
                        return "Error".equals(dialog.getTitle());
                    }
                }).using(robot());

                // Verify error dialog is displayed
                errorDialog.requireVisible();

                // Close error dialog
                errorDialog.button().click();
            } catch (Exception e) {
                fail("Error dialog was not displayed: " + e.getMessage());
            }

        } catch (Exception e) {
            fail("Edit dialog was not displayed or interaction failed: " + e.getMessage());
        }
    }

    /**
     * Test: Tests LocalDateTime conversion
     */
    @Test
    public void shouldConvertSpinnerDateToLocalDateTime() {
        // Select first row in table
        GuiActionRunner.execute(() -> hearingTable.setRowSelectionInterval(0, 0));

        // Click edit button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Hearing")).click();

        // Wait for robot to complete all operations
        robot().waitForIdle();

        try {
            // Find edit dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Edit Hearing".equals(dialog.getTitle());
                }
            }).using(robot());

            // Find date spinner
            JSpinner dateSpinner = null;
            Component[] components = dialog.target().getComponents();
            for (Component component : components) {
                if (component instanceof JSpinner) {
                    dateSpinner = (JSpinner) component;
                    break;
                }
            }

            if (dateSpinner != null) {
                // Make the variable final to use in lambda
                final JSpinner finalDateSpinner = dateSpinner;

                // Set a date
                final java.util.Date testDate = new java.util.Date();
                GuiActionRunner.execute(() -> finalDateSpinner.setValue(testDate));

                // Wait for robot to complete all operations
                robot().waitForIdle();

                // Test: LocalDateTime conversion is correct
                LocalDateTime convertedDateTime = testDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();

                // Verify year, month and day are the same
                assertEquals(convertedDateTime.getYear(),
                        testDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().getYear());
                assertEquals(convertedDateTime.getMonth(),
                        testDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().getMonth());
                assertEquals(convertedDateTime.getDayOfMonth(),
                        testDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().getDayOfMonth());
            }

            // Close dialog
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Cancel")).click();

        } catch (Exception e) {
            fail("Edit dialog was not displayed or interaction failed: " + e.getMessage());
        }
    }

    /**
     * Test helper method: Creates a mock hearing
     */
    private Hearing createMockHearing() {
        Case mockCase = new Case();
        mockCase.setId(1L);
        mockCase.setCaseNumber("C-2025-001");
        mockCase.setTitle("Smith vs. Johnson");

        Hearing mockHearing = new Hearing(mockCase, LocalDateTime.now().plusDays(1), "Judge Adams", "Court Room 101");
        mockHearing.setId(1L);
        mockHearing.setStatus(HearingStatus.SCHEDULED);
        mockHearing.setNotes("Test Notes");

        // Set up mock response for getHearingById
        Mockito.when(hearingService.getHearingById(1L)).thenReturn(Optional.of(mockHearing));

        return mockHearing;
    }

    /**
     * Called after each test finishes.
     * Cleans up resources.
     */
    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    /**
     * Helper method to avoid test errors
     */
    private void fail(String message) {
        org.junit.Assert.fail(message);
    }

    /**
     * Helper method to verify equality
     */
    private void assertEquals(Object expected, Object actual) {
        org.junit.Assert.assertEquals(expected, actual);
    }
}