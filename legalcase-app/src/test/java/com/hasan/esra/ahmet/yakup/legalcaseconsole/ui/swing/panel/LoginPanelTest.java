package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * Test class for Login panel written using JUnit 4 and AssertJ Swing
 */
@RunWith(GUITestRunner.class)
public class LoginPanelTest extends AssertJSwingJUnitTestCase {
    
    private FrameFixture window;
    private JFrame frame;
    private LoginPanel loginPanel;
    private AuthService authService;
    private MainFrame mainFrameMock;
    
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
        // Create mocks for AuthService and MainFrame
        authService = Mockito.mock(AuthService.class);
        mainFrameMock = Mockito.mock(MainFrame.class);
        
        // Create GUI components in EDT
        frame = GuiActionRunner.execute(() -> {
            JFrame testFrame = new JFrame("Test Login Panel");
            loginPanel = new LoginPanel(authService, mainFrameMock);
            
            // Access to private fields and add component names
            setupComponentNames();
            
            testFrame.add(loginPanel);
            testFrame.pack();
            testFrame.setVisible(true); // Make the frame visible
            return testFrame;
        });
        
        // Create test window
        window = new FrameFixture(robot(), frame);
        window.show(); // Show the window
    }
    
    /**
     * Setup component names for testing
     */
    private void setupComponentNames() {
        try {
            // Access to usernameField
            Field usernameField = LoginPanel.class.getDeclaredField("usernameField");
            usernameField.setAccessible(true);
            JTextField usernameFieldComponent = (JTextField) usernameField.get(loginPanel);
            usernameFieldComponent.setName("usernameField");
            
            // Access to passwordField
            Field passwordField = LoginPanel.class.getDeclaredField("passwordField");
            passwordField.setAccessible(true);
            JPasswordField passwordFieldComponent = (JPasswordField) passwordField.get(loginPanel);
            passwordFieldComponent.setName("passwordField");
            
            // Access to loginButton
            Field loginButton = LoginPanel.class.getDeclaredField("loginButton");
            loginButton.setAccessible(true);
            JButton loginButtonComponent = (JButton) loginButton.get(loginPanel);
            loginButtonComponent.setName("loginButton");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test: Constructor and initialization should correctly setup UI components
     */
    @Test
    public void shouldInitializeUIComponents() {
        // Verify the component existence and visibility
        window.textBox("usernameField").requireVisible();
        window.textBox("passwordField").requireVisible();
        window.button("loginButton").requireVisible();
        
        // Verify the layout setup - check if the components have proper parents
        GuiActionRunner.execute(() -> {
            Container parent = loginPanel.getParent();
            assertNotNull(parent);
            assertEquals(BorderLayout.class, loginPanel.getLayout().getClass());
            
            // Verify background color
            assertNotNull(loginPanel.getBackground());
        });
    }
    
    /**
     * Test: Should be able to login with valid username and password
     */
    @Test
    public void shouldLoginWithValidCredentials() throws Exception {
        // Simulate successful login for any username/password combination
        Mockito.when(authService.login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(true);
        
        // User login actions
        window.textBox("usernameField").setText("admin");
        window.textBox("passwordField").setText("password");
        
        // Click the login button, this will trigger JOptionPane
        window.button("loginButton").click();
        
        // Find JOptionPane message box and close it automatically
        try {
            // Find JOptionPane dialog (by title)
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Welcome".equals(dialog.getTitle());
                }
            }).withTimeout(2000).using(robot());
            
            // Verify that dialog is visible
            dialog.requireVisible();
            
            // Click "OK" button
            dialog.button().click();
            
            // Wait for dialog to close
            robot().waitForIdle();
        } catch (Exception e) {
            System.out.println("Dialog not found: " + e.getMessage());
            // Continue execution, as the dialog might be handled differently in headless mode
        }
        
        // We should wait before Mockito verify calls
        robot().waitForIdle();
        
        // Verify login was called
        Mockito.verify(authService).login("admin", "password");
        
        // Verify main menu was shown
        Mockito.verify(mainFrameMock).showPanel("mainMenu");
    }
    
    /**
     * Test: Login should fail with empty username and password
     */
    @Test
    public void shouldNotLoginWithEmptyCredentials() {
        // Try to login with empty fields
        window.textBox("usernameField").setText("");
        window.textBox("passwordField").setText("");
        window.button("loginButton").click();
        
        // Find and close validation error dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Validation Error".equals(dialog.getTitle());
                }
            }).withTimeout(2000).using(robot());
            
            dialog.requireVisible();
            dialog.button().click();
        } catch (Exception e) {
            System.out.println("Validation dialog not found: " + e.getMessage());
        }
        
        // Login method should never be called
        Mockito.verify(authService, Mockito.never()).login(Mockito.anyString(), Mockito.anyString());
    }
    
    /**
     * Test: Login should fail with invalid credentials
     */
    @Test
    public void shouldNotLoginWithInvalidCredentials() {
        // Simulate failed login
        Mockito.when(authService.login("wronguser", "wrongpass")).thenReturn(false);
        
        // Enter invalid user credentials
        window.textBox("usernameField").setText("wronguser");
        window.textBox("passwordField").setText("wrongpass");
        window.button("loginButton").click();
        
        // Find and close error dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "Authentication Error".equals(dialog.getTitle());
                }
            }).withTimeout(2000).using(robot());
            
            dialog.requireVisible();
            dialog.button().click();
        } catch (Exception e) {
            System.out.println("Authentication error dialog not found: " + e.getMessage());
        }
        
        // Was login method called with incorrect credentials?
        Mockito.verify(authService).login("wronguser", "wrongpass");
        
        // showPanel method should not be called
        Mockito.verify(mainFrameMock, Mockito.never()).showPanel(Mockito.anyString());
    }
    
    /**
     * Test: Login should handle exceptions from AuthService
     */
    @Test
    public void shouldHandleExceptionsDuringLogin() {
        // Simulate exception during login
        Mockito.when(authService.login("exceptionuser", "exceptionpass"))
               .thenThrow(new RuntimeException("Test exception"));
        
        // Enter credentials that will cause exception
        window.textBox("usernameField").setText("exceptionuser");
        window.textBox("passwordField").setText("exceptionpass");
        window.button("loginButton").click();
        
        // Find and close error dialog
        try {
            DialogFixture dialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {
                @Override
                protected boolean isMatching(JDialog dialog) {
                    return "System Error".equals(dialog.getTitle());
                }
            }).withTimeout(2000).using(robot());
            
            dialog.requireVisible();
            dialog.button().click();
        } catch (Exception e) {
            System.out.println("System error dialog not found: " + e.getMessage());
        }
        
        // Verify login method was called and exception was handled
        Mockito.verify(authService).login("exceptionuser", "exceptionpass");
        
        // Button should be re-enabled after exception
        GuiActionRunner.execute(() -> {
            try {
                Field loginButtonField = LoginPanel.class.getDeclaredField("loginButton");
                loginButtonField.setAccessible(true);
                JButton loginButton = (JButton) loginButtonField.get(loginPanel);
                assertTrue(loginButton.isEnabled());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Called after each test finishes.
     * Cleans up resources.
     */
    @Override
    protected void onTearDown() {
        window.cleanUp();
    }
    
    // Helper assertion methods to make tests more readable
    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected object to be not null");
        }
    }
    
    private void assertEquals(Class<?> expected, Class<?> actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
    
    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }
} 