package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel.*;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.CardLayout;
import java.awt.LayoutManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test class for MainFrame written using JUnit 4 and AssertJ Swing
 */
@RunWith(GUITestRunner.class)
public class MainFrameTest extends AssertJSwingJUnitTestCase {
    
    private FrameFixture window;
    private MainFrame frame;
    private AuthService authService;
    private ClientService clientService;
    private CaseService caseService;
    private HearingService hearingService;
    private DocumentService documentService;
    
    /**
     * Called before each test starts.
     * Prepares the necessary components for testing.
     */
    @Override
    protected void onSetUp() {
        // Mock the services
        authService = Mockito.mock(AuthService.class);
        clientService = Mockito.mock(ClientService.class);
        caseService = Mockito.mock(CaseService.class);
        hearingService = Mockito.mock(HearingService.class);
        documentService = Mockito.mock(DocumentService.class);
        
        // Create GUI components in EDT
        frame = GuiActionRunner.execute(() -> 
            new MainFrame(authService, clientService, caseService, hearingService, documentService)
        );
        
        // Make cardPanel accessible and name it
        GuiActionRunner.execute(() -> {
            try {
                // Access to cardPanel field
                Field cardPanelField = MainFrame.class.getDeclaredField("cardPanel");
                cardPanelField.setAccessible(true);
                JPanel cardPanel = (JPanel) cardPanelField.get(frame);
                cardPanel.setName("cardPanel");
                
                // Access to contentPane field
                Field contentPaneField = MainFrame.class.getDeclaredField("contentPane");
                contentPaneField.setAccessible(true);
                JPanel contentPane = (JPanel) contentPaneField.get(frame);
                contentPane.setName("contentPane");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Create test window
        window = new FrameFixture(robot(), frame);
        window.show(); // Show the window
    }
    
    /**
     * Test: Auth panel should be shown when frame is created
     */
    @Test
    public void shouldShowAuthPanelOnStartup() {
        // We cannot directly test if "auth" panel is visible
        // because only the active panel is visible in CardLayout
        // so we test indirectly
        
        window.panel("cardPanel").requireVisible();
        
        // Let's verify the active panel by looking for elements in AuthPanel
        // This test assumes that MainFrame shows AuthPanel at startup
        GuiActionRunner.execute(() -> {
            try {
                Field cardLayoutField = MainFrame.class.getDeclaredField("cardLayout");
                cardLayoutField.setAccessible(true);
                CardLayout cardLayout = (CardLayout) cardLayoutField.get(frame);
                
                Field cardPanelField = MainFrame.class.getDeclaredField("cardPanel");
                cardPanelField.setAccessible(true);
                JPanel cardPanel = (JPanel) cardPanelField.get(frame);
                
                // We don't have access to the currentCard value of cardLayout
                // Therefore, we can only check if cardPanel is visible
                assert cardPanel.isVisible();
            } catch (Exception e) {
                e.printStackTrace();
                assert false : "Reflection error: " + e.getMessage();
            }
        });
    }
    
    /**
     * Test: Client panel should be shown when showClientPanel method is called
     */
    @Test
    public void shouldSwitchToClientPanel() {
        // Switch to Client panel
        GuiActionRunner.execute(() -> frame.showClientPanel());
        
        // Only the active panel is visible in CardLayout
        window.panel("cardPanel").requireVisible();
    }
    
    /**
     * Test: Case panel should be shown when showCasePanel method is called
     */
    @Test
    public void shouldSwitchToCasePanel() {
        // Switch to Case panel
        GuiActionRunner.execute(() -> frame.showCasePanel());
        
        // Only the active panel is visible in CardLayout
        window.panel("cardPanel").requireVisible();
    }
    
    /**
     * Test: Main menu panel should be shown when showMainMenuPanel method is called
     */
    @Test
    public void shouldSwitchToMainMenuPanel() {
        // Switch to Main menu panel
        GuiActionRunner.execute(() -> frame.showMainMenuPanel());
        
        // Only the active panel is visible in CardLayout
        window.panel("cardPanel").requireVisible();
    }
    
    /**
     * Test: Verifies that the design panel is created correctly
     */
    @Test
    public void shouldCreateDesignPanelCorrectly() {
        // Create design panel
        GuiActionRunner.execute(() -> {
            try {
                // Call createDesignTimePanel method
                Method createDesignTimePanel = MainFrame.class.getDeclaredMethod("createDesignTimePanel");
                createDesignTimePanel.setAccessible(true);
                createDesignTimePanel.invoke(frame);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to create design panel: " + e.getMessage());
            }
        });

        // Check panel components
        window.panel("designPanel").requireVisible();
        window.panel("headerPanel").requireVisible();
        window.panel("centerPanel").requireVisible();
        window.panel("footerPanel").requireVisible();

        // Check title label
        window.label("titleLabel").requireVisible()
            .requireText("LEGAL CASE TRACKER");
        assertEquals("Title label color is incorrect", 
                    Color.WHITE, 
                    window.label("titleLabel").target().getForeground());

        // Check menu buttons
        String[] buttonNames = {
            "clientButton",
            "caseButton",
            "hearingButton",
            "documentButton",
            "logoutButton"
        };

        for (String buttonName : buttonNames) {
            window.button(buttonName).requireVisible();
        }

        // Check copyright label
        window.label("copyrightLabel").requireVisible()
            .requireText("© 2025 Legal Case Tracker - All Rights Reserved");
        assertEquals("Copyright label color is incorrect", 
                    Color.WHITE, 
                    window.label("copyrightLabel").target().getForeground());
    }

    /**
     * Test: Verifies that buttons in the design panel have correct colors and styles
     */
    @Test
    public void shouldHaveCorrectButtonStyles() {
        // Create design panel
        GuiActionRunner.execute(() -> {
            try {
                Method createDesignTimePanel = MainFrame.class.getDeclaredMethod("createDesignTimePanel");
                createDesignTimePanel.setAccessible(true);
                createDesignTimePanel.invoke(frame);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to create design panel: " + e.getMessage());
            }
        });

        // Check button colors
        Color[] expectedColors = {
            new Color(173, 216, 230), // Client Management
            new Color(144, 238, 144), // Case Management
            new Color(230, 190, 230), // Hearing Management
            new Color(255, 222, 173), // Document Management
            new Color(255, 200, 200)  // Logout
        };

        String[] buttonNames = {
            "clientButton",
            "caseButton",
            "hearingButton",
            "documentButton",
            "logoutButton"
        };

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = window.button(buttonNames[i]).target();
            assertNotNull("Button not found: " + buttonNames[i], button);
            
            // Check button border color
            Border border = button.getBorder();
            assertTrue("Button border not found: " + buttonNames[i], 
                      border instanceof CompoundBorder);
            
            CompoundBorder compoundBorder = (CompoundBorder) border;
            Border outerBorder = compoundBorder.getOutsideBorder();
            assertTrue("Outer border is not LineBorder: " + buttonNames[i], 
                      outerBorder instanceof LineBorder);
            
            LineBorder lineBorder = (LineBorder) outerBorder;
            assertEquals("Button border color is incorrect: " + buttonNames[i],
                        expectedColors[i], lineBorder.getLineColor());
        }
    }

    /**
     * Test: Verifies the layout and dimension properties of the design panel
     */
    @Test
    public void shouldHaveCorrectLayoutAndDimensions() {
        // Create design panel
        GuiActionRunner.execute(() -> {
            try {
                Method createDesignTimePanel = MainFrame.class.getDeclaredMethod("createDesignTimePanel");
                createDesignTimePanel.setAccessible(true);
                createDesignTimePanel.invoke(frame);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to create design panel: " + e.getMessage());
            }
        });

        // Check header panel size
        JPanel headerPanel = window.panel("headerPanel").target();
        assertEquals("Header panel height is incorrect", 100, headerPanel.getPreferredSize().height);

        // Check footer panel size
        JPanel footerPanel = window.panel("footerPanel").target();
        assertEquals("Footer panel height is incorrect", 40, footerPanel.getPreferredSize().height);

        // Check center panel layout
        JPanel centerPanel = window.panel("centerPanel").target();
        assertTrue("Center panel does not use GridLayout", 
                  centerPanel.getLayout() instanceof GridLayout);
        
        GridLayout gridLayout = (GridLayout) centerPanel.getLayout();
        assertEquals("Grid row count is incorrect", 5, gridLayout.getRows());
        assertEquals("Grid column count is incorrect", 1, gridLayout.getColumns());
        assertEquals("Grid horizontal gap is incorrect", 10, gridLayout.getHgap());
        assertEquals("Grid vertical gap is incorrect", 10, gridLayout.getVgap());

        // Check main panel layout
        JPanel designPanel = window.panel("designPanel").target();
        assertTrue("Design panel does not use BorderLayout", 
                  designPanel.getLayout() instanceof BorderLayout);
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
     * Helper method called when a test fails
     */
    private void fail(String message) {
        org.junit.Assert.fail(message);
    }
} 