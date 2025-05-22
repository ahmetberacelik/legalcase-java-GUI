package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.panel;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.MainFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test class for Auth panel written using JUnit 4 and AssertJ Swing
 */
@RunWith(GUITestRunner.class)
public class AuthPanelTest extends AssertJSwingJUnitTestCase {
    
    private FrameFixture window;
    private JFrame frame;
    private AuthPanel authPanel;
    private AuthService authService;
    private MainFrame mainFrameMock;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JTextField registerEmailField;
    private JTextField registerNameField;
    private JTextField registerSurnameField;
    private JComboBox<UserRole> roleComboBox;
    private JButton registerButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    /**
     * Set up JOptionPane before testing
     */
    @Before
    public void setUpJOptionPanes() {
        // Settings to automatically close JOptionPane dialogs
        UIManager.put("OptionPane.buttonTypeFocus", null);
        System.setProperty("java.awt.headless", "false");
        
        // Set JVM args for Java 21 to allow reflection access
        System.setProperty("illegal-access", "permit");
        System.setProperty("--add-opens", "java.base/java.util=ALL-UNNAMED");
        System.setProperty("--add-opens", "java.desktop/java.awt=ALL-UNNAMED");
        System.setProperty("--add-opens", "java.desktop/javax.swing=ALL-UNNAMED");
        System.setProperty("--add-opens", "java.base/java.lang=ALL-UNNAMED");
        System.setProperty("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED");
    }
    
    /**
     * Called before each test starts.
     * Prepares the necessary components for testing.
     */
    @Override
    protected void onSetUp() {
        // Create mocks for services and MainFrame
        authService = Mockito.mock(AuthService.class);
        mainFrameMock = Mockito.mock(MainFrame.class, Mockito.RETURNS_DEEP_STUBS);
        
        // Configure mainFrameMock to avoid NullPointerException with dialog parents
        JFrame dummyFrame = GuiActionRunner.execute(() -> {
            JFrame frame = new JFrame();
            frame.setVisible(true);
            return frame;
        });
        
        // Setup mainFrameMock to respond correctly to showPanel method
        Mockito.when(mainFrameMock.toString()).thenReturn("MockMainFrame");
        Mockito.doNothing().when(mainFrameMock).showPanel(Mockito.anyString());
        
        // Create GUI components in EDT
        frame = GuiActionRunner.execute(() -> {
            JFrame testFrame = new JFrame("Test Auth Panel");
            testFrame.setVisible(true);
            
            authPanel = new AuthPanel(authService, mainFrameMock);
            
            // Access to private fields using Reflection
            try {
                // Login panel components
                Field usernameFieldField = AuthPanel.class.getDeclaredField("usernameField");
                usernameFieldField.setAccessible(true);
                usernameField = (JTextField) usernameFieldField.get(authPanel);
                usernameField.setName("usernameField");
                
                Field passwordFieldField = AuthPanel.class.getDeclaredField("passwordField");
                passwordFieldField.setAccessible(true);
                passwordField = (JPasswordField) passwordFieldField.get(authPanel);
                passwordField.setName("passwordField");
                
                Field loginButtonField = AuthPanel.class.getDeclaredField("loginButton");
                loginButtonField.setAccessible(true);
                loginButton = (JButton) loginButtonField.get(authPanel);
                loginButton.setName("loginButton");
                
                // Register panel components
                Field registerUsernameFieldField = AuthPanel.class.getDeclaredField("registerUsernameField");
                registerUsernameFieldField.setAccessible(true);
                registerUsernameField = (JTextField) registerUsernameFieldField.get(authPanel);
                registerUsernameField.setName("registerUsernameField");
                
                Field registerPasswordFieldField = AuthPanel.class.getDeclaredField("registerPasswordField");
                registerPasswordFieldField.setAccessible(true);
                registerPasswordField = (JPasswordField) registerPasswordFieldField.get(authPanel);
                registerPasswordField.setName("registerPasswordField");
                
                Field registerEmailFieldField = AuthPanel.class.getDeclaredField("registerEmailField");
                registerEmailFieldField.setAccessible(true);
                registerEmailField = (JTextField) registerEmailFieldField.get(authPanel);
                registerEmailField.setName("registerEmailField");
                
                Field registerNameFieldField = AuthPanel.class.getDeclaredField("registerNameField");
                registerNameFieldField.setAccessible(true);
                registerNameField = (JTextField) registerNameFieldField.get(authPanel);
                registerNameField.setName("registerNameField");
                
                Field registerSurnameFieldField = AuthPanel.class.getDeclaredField("registerSurnameField");
                registerSurnameFieldField.setAccessible(true);
                registerSurnameField = (JTextField) registerSurnameFieldField.get(authPanel);
                registerSurnameField.setName("registerSurnameField");
                
                Field roleComboBoxField = AuthPanel.class.getDeclaredField("roleComboBox");
                roleComboBoxField.setAccessible(true);
                roleComboBox = (JComboBox<UserRole>) roleComboBoxField.get(authPanel);
                roleComboBox.setName("roleComboBox");
                
                Field registerButtonField = AuthPanel.class.getDeclaredField("registerButton");
                registerButtonField.setAccessible(true);
                registerButton = (JButton) registerButtonField.get(authPanel);
                registerButton.setName("registerButton");
                
                // Card layout components
                Field cardLayoutField = AuthPanel.class.getDeclaredField("cardLayout");
                cardLayoutField.setAccessible(true);
                cardLayout = (CardLayout) cardLayoutField.get(authPanel);
                
                Field cardPanelField = AuthPanel.class.getDeclaredField("cardPanel");
                cardPanelField.setAccessible(true);
                cardPanel = (JPanel) cardPanelField.get(authPanel);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            testFrame.add(authPanel);
            testFrame.pack();
            testFrame.setLocationRelativeTo(null);
            return testFrame;
        });
        
        // Create test window - MUST be done in EDT
        window = GuiActionRunner.execute(() -> new FrameFixture(robot(), frame));
        window.show(); // Show the window
    }
    
    /**
     * Test: Should validate empty fields on login
     */
    @Test
    public void shouldValidateEmptyFieldsOnLogin() {
        // Configure JOptionPane to return OK (for automatically closing the dialog)
        final AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        GuiActionRunner.execute(() -> {
            // Set empty values
            usernameField.setText("");
            passwordField.setText("");
        });
        
        // Call handleLogin method directly using reflection
        GuiActionRunner.execute(() -> {
            try {
                Method handleLoginMethod = AuthPanel.class.getDeclaredMethod("handleLogin");
                handleLoginMethod.setAccessible(true);
                handleLoginMethod.invoke(authPanel);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to call handleLogin: " + e.getMessage());
            }
        });
        
        // Verify that login service was never called due to validation error
        Mockito.verify(authService, Mockito.never()).login(Mockito.anyString(), Mockito.anyString());
    }
    
    /**
     * Test: Should call login service with valid credentials
     */
    @Test
    public void shouldCallLoginServiceWithValidCredentials() {
        // Configure mock for successful login
        Mockito.when(authService.login("testuser", "testpass")).thenReturn(true);
        
        // Set values and call handleLogin method
        GuiActionRunner.execute(() -> {
            usernameField.setText("testuser");
            passwordField.setText("testpass");
            
            try {
                Method handleLoginMethod = AuthPanel.class.getDeclaredMethod("handleLogin");
                handleLoginMethod.setAccessible(true);
                handleLoginMethod.invoke(authPanel);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to call handleLogin: " + e.getMessage());
            }
        });
        
        // Wait for any pending events
        robot().waitForIdle();
        
        // Verify that login service was called with correct credentials
        Mockito.verify(authService).login("testuser", "testpass");
        
        // Verify navigation to main menu was called (successful login)
        Mockito.verify(mainFrameMock).showPanel("mainMenu");
    }
    
    /**
     * Test: Should handle failed login attempt correctly
     */
    @Test
    public void shouldHandleFailedLoginAttempt() {
        // Configure mock for failed login
        Mockito.when(authService.login("baduser", "badpass")).thenReturn(false);
        
        // Set values and call handleLogin method
        GuiActionRunner.execute(() -> {
            usernameField.setText("baduser");
            passwordField.setText("badpass");
            
            try {
                Method handleLoginMethod = AuthPanel.class.getDeclaredMethod("handleLogin");
                handleLoginMethod.setAccessible(true);
                handleLoginMethod.invoke(authPanel);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to call handleLogin: " + e.getMessage());
            }
        });
        
        // Wait for any pending events
        robot().waitForIdle();
        
        // Verify that login service was called
        Mockito.verify(authService).login("baduser", "badpass");
        
        // Verify navigation to main menu was NOT called (failed login)
        Mockito.verify(mainFrameMock, Mockito.never()).showPanel("mainMenu");
    }
    
    /**
     * Test: Should handle login exceptions correctly
     */
    @Test
    public void shouldHandleLoginExceptions() {
        // Configure mock to throw exception during login
        Mockito.when(authService.login("erroruser", "errorpass"))
            .thenThrow(new RuntimeException("Test exception"));
        
        // Set values and call handleLogin method
        GuiActionRunner.execute(() -> {
            usernameField.setText("erroruser");
            passwordField.setText("errorpass");
            
            try {
                Method handleLoginMethod = AuthPanel.class.getDeclaredMethod("handleLogin");
                handleLoginMethod.setAccessible(true);
                handleLoginMethod.invoke(authPanel);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to call handleLogin: " + e.getMessage());
            }
        });
        
        // Wait for any pending events
        robot().waitForIdle();
        
        // Verify that login service was called
        Mockito.verify(authService).login("erroruser", "errorpass");
        
        // Verify navigation to main menu was NOT called (exception occurred)
        Mockito.verify(mainFrameMock, Mockito.never()).showPanel("mainMenu");
    }
    
    /**
     * Called after each test finishes.
     * Cleans up resources.
     */
    @Override
    protected void onTearDown() {
        // Close any open dialogs first
        GuiActionRunner.execute(() -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog) {
                    window.dispose();
                }
            }
        });
        
        // Then clean up the main window
        if (window != null) {
            window.cleanUp();
        }
    }
    
    /**
     * Helper method to avoid test errors
     */
    private void fail(String message) {
        org.junit.Assert.fail(message);
    }
} 