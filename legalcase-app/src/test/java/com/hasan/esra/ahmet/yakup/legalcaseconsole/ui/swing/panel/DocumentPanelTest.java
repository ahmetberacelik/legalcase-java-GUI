package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.data.TableCell;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for Document panel written using JUnit 4 and AssertJ Swing
 */
@RunWith(GUITestRunner.class)
public class DocumentPanelTest extends AssertJSwingJUnitTestCase {
    
    private FrameFixture window;
    private JFrame frame;
    private DocumentPanel documentPanel;
    private DocumentService documentService;
    private CaseService caseService;
    private MainFrame mainFrameMock;
    private DefaultTableModel tableModel;
    private JTable documentTable;
    
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
        documentService = Mockito.mock(DocumentService.class);
        caseService = Mockito.mock(CaseService.class);
        mainFrameMock = Mockito.mock(MainFrame.class);
        
        // Prepare mock data for tests
        prepareMockData();
        
        // Create GUI components in EDT
        frame = GuiActionRunner.execute(() -> {
            JFrame testFrame = new JFrame("Test Document Panel");
            documentPanel = new DocumentPanel(documentService, caseService, mainFrameMock);
            
            // Access to private fields using Reflection
            try {
                // Access to table
                Field documentTableField = DocumentPanel.class.getDeclaredField("documentTable");
                documentTableField.setAccessible(true);
                documentTable = (JTable) documentTableField.get(documentPanel);
                documentTable.setName("documentTable");
                
                // Access to tableModel
                Field tableModelField = DocumentPanel.class.getDeclaredField("tableModel");
                tableModelField.setAccessible(true);
                tableModel = (DefaultTableModel) tableModelField.get(documentPanel);
                
                // Manually populate table with test data
                populateTableWithTestData();
                
                // Access to search field
                Field searchFieldField = DocumentPanel.class.getDeclaredField("searchField");
                searchFieldField.setAccessible(true);
                JTextField searchFieldComponent = (JTextField) searchFieldField.get(documentPanel);
                searchFieldComponent.setName("searchField");
                
                // Access to search button
                Field searchButtonField = DocumentPanel.class.getDeclaredField("searchButton");
                searchButtonField.setAccessible(true);
                JButton searchButtonComponent = (JButton) searchButtonField.get(documentPanel);
                searchButtonComponent.setName("searchButton");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            testFrame.add(documentPanel);
            testFrame.pack();
            return testFrame;
        });
        
        // Create test window
        window = new FrameFixture(robot(), frame);
        window.show(); // Show the window
    }
    
    /**
     * Traverses the component tree to find all components of a specific type
     * 
     * @param container The container to search in
     * @param type The component type to search for
     * @param result List to add found components to
     */
    private <T extends Component> void findAllComponentsOfType(Container container, Class<T> type, List<T> result) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (type.isInstance(component)) {
                result.add(type.cast(component));
            }
            if (component instanceof Container) {
                findAllComponentsOfType((Container) component, type, result);
            }
        }
    }
    
    /**
     * Finds components in a dialog by type and returns them as a map with their names or text as keys
     * 
     * @param dialog The dialog to search in
     * @return Map of components found in the dialog
     */
    private Map<String, Component> findDialogComponents(JDialog dialog) {
        Map<String, Component> components = new HashMap<>();
        
        // Find all text fields
        List<JTextField> textFields = new ArrayList<>();
        findAllComponentsOfType(dialog, JTextField.class, textFields);
        for (JTextField textField : textFields) {
            // Try to identify text fields by the label next to them
            Container parent = textField.getParent();
            if (parent != null) {
                Component[] siblings = parent.getComponents();
                for (Component sibling : siblings) {
                    if (sibling instanceof JLabel) {
                        JLabel label = (JLabel) sibling;
                        components.put(label.getText().replace(":", ""), textField);
                        break;
                    }
                }
            }
        }
        
        // Find all combo boxes
        List<JComboBox> comboBoxes = new ArrayList<>();
        findAllComponentsOfType(dialog, JComboBox.class, comboBoxes);
        for (JComboBox comboBox : comboBoxes) {
            // Try to identify combo boxes by the label next to them
            Container parent = comboBox.getParent();
            if (parent != null) {
                Component[] siblings = parent.getComponents();
                for (Component sibling : siblings) {
                    if (sibling instanceof JLabel) {
                        JLabel label = (JLabel) sibling;
                        components.put(label.getText().replace(":", ""), comboBox);
                        break;
                    }
                }
            }
        }
        
        // Find all text areas
        List<JTextArea> textAreas = new ArrayList<>();
        findAllComponentsOfType(dialog, JTextArea.class, textAreas);
        for (JTextArea textArea : textAreas) {
            // Try to identify text areas by the label next to them
            Container parent = textArea.getParent();
            if (parent instanceof JViewport) {
                parent = parent.getParent(); // Get the scroll pane
                if (parent != null) {
                    parent = parent.getParent(); // Get the parent of the scroll pane
                }
            }
            
            if (parent != null) {
                Component[] siblings = parent.getComponents();
                for (Component sibling : siblings) {
                    if (sibling instanceof JLabel) {
                        JLabel label = (JLabel) sibling;
                        components.put(label.getText().replace(":", ""), textArea);
                        break;
                    }
                }
            }
        }
        
        // Find all buttons
        List<JButton> buttons = new ArrayList<>();
        findAllComponentsOfType(dialog, JButton.class, buttons);
        for (JButton button : buttons) {
            components.put(button.getText(), button);
        }
        
        return components;
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
            "Contract Agreement", // Document Title
            DocumentType.CONTRACT, // Document Type
            "text/plain" // Content Type
        });
        
        tableModel.addRow(new Object[]{
            2L, // ID
            "C-2025-002", // Case Number
            "Evidence Report", // Document Title
            DocumentType.EVIDENCE, // Document Type
            "text/plain" // Content Type
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
        
        // Create mock documents
        Document mockDocument1 = new Document("Contract Agreement", DocumentType.CONTRACT, mockCase1, "Contract content");
        mockDocument1.setId(1L);
        mockDocument1.setContentType("text/plain");
        
        Document mockDocument2 = new Document("Evidence Report", DocumentType.EVIDENCE, mockCase2, "Evidence content");
        mockDocument2.setId(2L);
        mockDocument2.setContentType("text/plain");
        
        // Prepare mock responses
        List<Document> mockDocuments = new ArrayList<>();
        mockDocuments.add(mockDocument1);
        mockDocuments.add(mockDocument2);
        
        List<Case> mockCases = new ArrayList<>();
        mockCases.add(mockCase1);
        mockCases.add(mockCase2);
        
        // Configure mocks
        Mockito.when(documentService.getAllDocuments()).thenReturn(mockDocuments);
        Mockito.when(documentService.getDocumentById(1L)).thenReturn(Optional.of(mockDocument1));
        Mockito.when(documentService.getDocumentById(2L)).thenReturn(Optional.of(mockDocument2));
        Mockito.when(caseService.getAllCases()).thenReturn(mockCases);
        
        // Mock createDocument method to return a valid document
        Mockito.when(documentService.createDocument(
            Mockito.anyLong(), 
            Mockito.anyString(), 
            Mockito.any(DocumentType.class), 
            Mockito.anyString()
        )).thenAnswer(invocation -> {
            Long caseId = invocation.getArgument(0);
            String title = invocation.getArgument(1);
            DocumentType type = invocation.getArgument(2);
            String content = invocation.getArgument(3);
            
            Document newDocument = new Document(title, type, mockCase1, content);
            newDocument.setId(3L);
            newDocument.setContentType("text/plain");
            return newDocument;
        });
    }
    
    /**
     * Test: All components should be visible
     */
    @Test
    public void shouldShowAllComponents() {
        // Table component should be visible
        window.table("documentTable").requireVisible();
        
        // Search components should be visible
        window.textBox("searchField").requireVisible();
        window.button("searchButton").requireVisible();
        
        // Check for necessary buttons (they don't have names, so we'll check by text)
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Add New Document")).requireVisible();
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Document")).requireVisible();
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Delete Document")).requireVisible();
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Back to Menu")).requireVisible();
    }
    
    /**
     * Test: Table should be populated with data
     */
    @Test
    public void shouldLoadDocumentsIntoTable() {
        // Get table fixture
        JTableFixture tableFixture = window.table("documentTable");
        
        // Verify row count (should match our mock data)
        tableFixture.requireRowCount(2);
        
        // Verify table headers
        tableFixture.requireColumnCount(5);
        
        // Verify specific cell content
        tableFixture.cell(TableCell.row(0).column(0)).requireValue("1"); // ID
        tableFixture.cell(TableCell.row(0).column(1)).requireValue("C-2025-001"); // Case Number
        tableFixture.cell(TableCell.row(0).column(2)).requireValue("Contract Agreement"); // Document Title
        tableFixture.cell(TableCell.row(0).column(3)).requireValue("CONTRACT"); // Document Type
        tableFixture.cell(TableCell.row(0).column(4)).requireValue("text/plain"); // Content Type
        
        // Check service method was called
        Mockito.verify(documentService).getAllDocuments();
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
     * Test: Should open add document dialog when add button is clicked
     */
    @Test
    public void shouldOpenAddDocumentDialogWhenAddButtonClicked() {
        // Click the add button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Add New Document")).click();
        
        // Wait for the robot to process all pending events
        robot().waitForIdle();
        
        // Try to find dialog with title "Add New Document"
        try {
            // Find dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Add New Document".equals(dialog.getTitle());
                }
            }).using(robot());
            
            // Verify dialog is visible
            dialog.requireVisible();
            
            // Find all JComboBox components
            List<JComboBox> comboBoxes = new ArrayList<>();
            findAllComponentsOfType(dialog.target(), JComboBox.class, comboBoxes);
            
            // There should be at least one JComboBox
            assertTrue("Dialog should contain at least one combo box", comboBoxes.size() > 0);
            
            // Close dialog to continue
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Cancel")).click();
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Add document dialog not displayed: " + e.getMessage());
        }
        
        // Verify case service was called to get cases for dropdown
        Mockito.verify(caseService).getAllCases();
    }
    
    /**
     * Test: Should show error when trying to edit without selecting a document
     */
    @Test
    public void shouldShowErrorWhenEditingWithoutSelection() {
        // Click edit button without selecting a row
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Document")).click();
        
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
     * Test: Should open edit document dialog when row selected and edit button clicked
     */
    @Test
    public void shouldOpenEditDialogWhenRowSelectedAndEditClicked() {
        // Manually select first row in table
        GuiActionRunner.execute(() -> documentTable.setRowSelectionInterval(0, 0));
        
        // Click edit button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Edit Document")).click();
        
        // Wait for the robot to process all pending events
        robot().waitForIdle();
        
        // Try to find edit dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Edit Document".equals(dialog.getTitle());
                }
            }).using(robot());
            
            // Verify dialog is visible
            dialog.requireVisible();
            
            // Close dialog to continue
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Cancel")).click();
        } catch (Exception e) {
            // If dialog not found, fail test
            fail("Edit document dialog not displayed: " + e.getMessage());
        }
        
        // Verify service call to get document by ID
        Mockito.verify(documentService).getDocumentById(1L); // ID of first row in our mock data
    }
    
    /**
     * Test: Should show error when trying to delete without selecting a document
     */
    @Test
    public void shouldShowErrorWhenDeletingWithoutSelection() {
        // Click delete button without selecting a row
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Delete Document")).click();
        
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
        GuiActionRunner.execute(() -> documentTable.setRowSelectionInterval(0, 0));
        
        // Click delete button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Delete Document")).click();
        
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
     * Test: Should search documents when search button clicked
     */
    @Test
    public void shouldSearchDocumentsWhenSearchButtonClicked() {
        // Reset Mockito - clear previous calls
        Mockito.reset(documentService);
        
        // Prepare mock data for search
        List<Document> mockDocuments = new ArrayList<>();
        Case mockCase1 = new Case();
        mockCase1.setId(1L);
        mockCase1.setCaseNumber("C-2025-001");
        mockCase1.setTitle("Smith vs. Johnson");
        
        Document mockDocument1 = new Document("Contract Agreement", DocumentType.CONTRACT, mockCase1, "Contract content");
        mockDocument1.setId(1L);
        mockDocument1.setContentType("text/plain");
        mockDocuments.add(mockDocument1);
        
        // Configure mock service
        Mockito.when(documentService.getAllDocuments()).thenReturn(mockDocuments);
        
        // Access the searchButton field using reflection
        JButton searchButtonComponent = null;
        try {
            Field searchButtonField = DocumentPanel.class.getDeclaredField("searchButton");
            searchButtonField.setAccessible(true);
            searchButtonComponent = (JButton) searchButtonField.get(documentPanel);
            
            // Access the searchField field using reflection
            Field searchFieldField = DocumentPanel.class.getDeclaredField("searchField");
            searchFieldField.setAccessible(true);
            JTextField searchFieldComponent = (JTextField) searchFieldField.get(documentPanel);
            
            // Set search text directly
            GuiActionRunner.execute(() -> searchFieldComponent.setText("Contract"));
            
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
            Mockito.verify(documentService).getAllDocuments();
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Should handle different search scenarios in searchDocuments method
     */
    @Test
    public void shouldHandleDifferentSearchScenarios() {
        // Reset Mockito - clear previous calls
        Mockito.reset(documentService);
        
        // Prepare mock data for different scenarios
        List<Document> mockDocuments = new ArrayList<>();
        
        // 1. Normal case - document with all fields populated
        Case mockCase1 = new Case();
        mockCase1.setId(1L);
        mockCase1.setCaseNumber("C-2025-001");
        mockCase1.setTitle("Smith vs. Johnson");
        
        Document mockDocument1 = new Document("Contract Agreement", DocumentType.CONTRACT, mockCase1, "Contract content");
        mockDocument1.setId(1L);
        mockDocument1.setContentType("text/plain");
        mockDocuments.add(mockDocument1);
        
        // 2. Document with null case reference
        Document mockDocument2 = new Document("Evidence Report", DocumentType.EVIDENCE, null, "Evidence content");
        mockDocument2.setId(2L);
        mockDocument2.setContentType("application/pdf");
        mockDocuments.add(mockDocument2);
        
        // 3. Document with null title
        Document mockDocument3 = new Document(null, DocumentType.PETITION, mockCase1, "Brief content");
        mockDocument3.setId(3L);
        mockDocument3.setContentType("text/plain");
        mockDocuments.add(mockDocument3);
        
        // 4. Document with null type
        Document mockDocument4 = new Document("Court Order", null, mockCase1, "Order content");
        mockDocument4.setId(4L);
        mockDocument4.setContentType("text/plain");
        mockDocuments.add(mockDocument4);
        
        // 5. Document with null content type
        Document mockDocument5 = new Document("Witness Statement", DocumentType.OTHER, mockCase1, "Statement content");
        mockDocument5.setId(5L);
        mockDocument5.setContentType(null);
        mockDocuments.add(mockDocument5);
        
        // Configure mock service
        Mockito.when(documentService.getAllDocuments()).thenReturn(mockDocuments);
        
        // Use reflection to directly call searchDocuments method
        try {
            // First clear the tableModel
            GuiActionRunner.execute(() -> tableModel.setRowCount(0));
            
            // 1. Test: "text" search - should find documents with "text/plain" content type (3 documents)
            window.textBox("searchField").setText("text");
            window.button("searchButton").click();
            robot().waitForIdle();
            
            // Verify correct number of rows in table (should be 3)
            JTableFixture tableFixture = window.table("documentTable");
            tableFixture.requireRowCount(3); // 3 documents contain "text/plain"
            
            // 2. Test: "evidence" search - should find documents with title or type containing "evidence" (1 document)
            window.textBox("searchField").setText("evidence");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(1); // 1 document contains "Evidence"
            
            // 3. Test: Empty search - should return all documents
            window.textBox("searchField").setText("");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(5); // All documents should be shown
            
            // 4. Test: Case number search
            window.textBox("searchField").setText("c-2025");
            window.button("searchButton").click();
            robot().waitForIdle();
            tableFixture.requireRowCount(4); // 4 documents with non-null case should be shown
            
            // Verify service method was called
            Mockito.verify(documentService, Mockito.atLeast(4)).getAllDocuments();
            
        } catch (Exception e) {
            fail("Search test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test: Should create a new document when form is filled and save button clicked
     */
    @Test
    public void shouldCreateNewDocumentWhenFormSubmitted() {
        // Setup mock for case service to return mock cases for combo box
        Case mockCase = new Case();
        mockCase.setId(1L);
        mockCase.setCaseNumber("C-2025-001");
        mockCase.setTitle("Smith vs. Johnson");
        
        List<Case> mockCases = new ArrayList<>();
        mockCases.add(mockCase);
        Mockito.when(caseService.getAllCases()).thenReturn(mockCases);
        
        // Click the add button
        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Add New Document")).click();
        
        // Wait for the robot to process all pending events
        robot().waitForIdle();
        
        try {
            // Find dialog
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Add New Document".equals(dialog.getTitle());
                }
            }).using(robot());
            
            // Use a simpler approach to fill form fields
            // Find and identify components by their labels
            Component[] components = dialog.target().getComponents();
            
            // Find and select first item in the case combo box
            for (Component component : components) {
                if (component instanceof JComboBox) {
                    JComboBox comboBox = (JComboBox) component;
                    GuiActionRunner.execute(() -> comboBox.setSelectedIndex(0));
                    break;
                }
            }
            
            // Find and fill all text fields
            List<JTextField> textFields = new ArrayList<>();
            findAllComponentsOfType(dialog.target(), JTextField.class, textFields);
            
            // Map to track which fields have been filled
            Map<String, Boolean> filledFields = new HashMap<>();
            
            for (JTextField textField : textFields) {
                if (textField.getName() == null || !textField.getName().equals("Spinner.formattedTextField")) {
                    Container parent = textField.getParent();
                    if (parent != null) {
                        for (Component sibling : parent.getComponents()) {
                            if (sibling instanceof JLabel) {
                                JLabel label = (JLabel) sibling;
                                String labelText = label.getText();
                                
                                if (labelText.contains("Title")) {
                                    new JTextComponentFixture(robot(), textField).setText("Test Document Title");
                                    filledFields.put("Title", true);
                                } else if (labelText.contains("Content Type")) {
                                    new JTextComponentFixture(robot(), textField).setText("text/plain");
                                    filledFields.put("Content Type", true);
                                }
                            }
                        }
                    }
                }
            }
            
            // If we couldn't find the title field by label, try to find all text fields and fill them
            if (!filledFields.containsKey("Title")) {
                for (JTextField textField : textFields) {
                    if (textField.getName() == null || !textField.getName().equals("Spinner.formattedTextField")) {
                        // First text field is likely the title field
                        if (!filledFields.containsKey("Title")) {
                            new JTextComponentFixture(robot(), textField).setText("Test Document Title");
                            filledFields.put("Title", true);
                        } 
                        // Second text field is likely the content type field
                        else if (!filledFields.containsKey("Content Type")) {
                            new JTextComponentFixture(robot(), textField).setText("text/plain");
                            filledFields.put("Content Type", true);
                        }
                    }
                }
            }
            
            // Find and fill all text areas
            List<JTextArea> textAreas = new ArrayList<>();
            findAllComponentsOfType(dialog.target(), JTextArea.class, textAreas);
            
            for (JTextArea textArea : textAreas) {
                new JTextComponentFixture(robot(), textArea).setText("Test document content");
            }
            
            // Find and select first item in the document type combo box
            List<JComboBox> comboBoxes = new ArrayList<>();
            findAllComponentsOfType(dialog.target(), JComboBox.class, comboBoxes);
            
            for (JComboBox comboBox : comboBoxes) {
                Container parent = comboBox.getParent();
                if (parent != null) {
                    for (Component sibling : parent.getComponents()) {
                        if (sibling instanceof JLabel) {
                            JLabel label = (JLabel) sibling;
                            if (label.getText().contains("Type")) {
                                GuiActionRunner.execute(() -> comboBox.setSelectedIndex(0));
                            }
                        }
                    }
                }
            }
            
            // Configure mock service
            Mockito.reset(documentService);
            
            // Create a mock document to return
            Document newDocument = new Document("Test Document Title", DocumentType.CONTRACT, mockCase, "Test document content");
            newDocument.setId(3L);
            newDocument.setContentType("text/plain");
            
            // Configure mock to return the document when createDocument is called
            Mockito.when(documentService.createDocument(
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.any(DocumentType.class),
                Mockito.anyString()
            )).thenReturn(newDocument);
            
            // Click save button
            dialog.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("Save")).click();
            
            // Wait for processing to complete
            robot().waitForIdle();
            
            // Verify service call with any parameters (since we can't control exact field order)
            Mockito.verify(documentService).createDocument(
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.any(DocumentType.class),
                Mockito.anyString()
            );
            
        } catch (Exception e) {
            fail("Failed to create document: " + e.getMessage());
        }
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
    
    /**
     * Helper method to verify truth value
     */
    private void assertTrue(String message, boolean condition) {
        org.junit.Assert.assertTrue(message, condition);
    }
} 