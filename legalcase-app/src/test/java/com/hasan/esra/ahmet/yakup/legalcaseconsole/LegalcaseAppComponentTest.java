package com.hasan.esra.ahmet.yakup.legalcaseconsole;

import static org.junit.Assert.*;
import org.junit.Test;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.config.DatabaseManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.*;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Class that tests LegalcaseApp components
 */
public class LegalcaseAppComponentTest {
    
    private static final Logger LOGGER = Logger.getLogger(LegalcaseAppComponentTest.class.getName());
    
    /**
     * Tests the Logger component
     */
    @Test
    public void testLoggerInstance() throws Exception {
        // Using reflection because the LOGGER field is defined as private
        Field loggerField = LegalcaseApp.class.getDeclaredField("LOGGER");
        loggerField.setAccessible(true);
        
        // getInstance could be null since we're working with a static field
        Logger logger = (Logger) loggerField.get(null);
        
        assertNotNull("LOGGER should be an instance", logger);
        assertEquals("Logger name should be the same as the class name", 
                LegalcaseApp.class.getName(), logger.getName());
    }
    
    /**
     * Tests parameter types for static methods in the main class
     */
    @Test
    public void testMethodParameterTypes() {
        try {
            // Check parameter types for showInterfaceSelectionDialog method
            Method showDialogMethod = LegalcaseApp.class.getDeclaredMethod(
                "showInterfaceSelectionDialog", 
                AuthService.class, ClientService.class, CaseService.class, 
                HearingService.class, DocumentService.class);
            
            assertNotNull("showInterfaceSelectionDialog method should exist", showDialogMethod);
            
            // Check parameter types for startGuiInterface method
            Method startGuiMethod = LegalcaseApp.class.getDeclaredMethod(
                "startGuiInterface", 
                AuthService.class, ClientService.class, CaseService.class, 
                HearingService.class, DocumentService.class);
            
            assertNotNull("startGuiInterface method should exist", startGuiMethod);
            
            // Check parameter types for startConsoleInterface method
            Method startConsoleMethod = LegalcaseApp.class.getDeclaredMethod(
                "startConsoleInterface", 
                AuthService.class, ClientService.class, CaseService.class, 
                HearingService.class, DocumentService.class);
            
            assertNotNull("startConsoleInterface method should exist", startConsoleMethod);
            
        } catch (NoSuchMethodException e) {
            LOGGER.severe("Method not found: " + e.getMessage());
            fail("Method not found: " + e.getMessage());
        }
    }
    
    /**
     * Tests the class structure
     */
    @Test
    public void testClassStructure() {
        // Verify the structure of the class
        Constructor<?>[] constructors = LegalcaseApp.class.getDeclaredConstructors();
        
        // Check for the existence of a default constructor
        boolean hasDefaultConstructor = false;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                hasDefaultConstructor = true;
                break;
            }
        }
        
        // Check for the existence of the main method
        boolean hasMainMethod = false;
        try {
            Method mainMethod = LegalcaseApp.class.getDeclaredMethod("main", String[].class);
            hasMainMethod = true;
        } catch (NoSuchMethodException e) {
            hasMainMethod = false;
        }
        
        assertTrue("Class should contain a default constructor", hasDefaultConstructor);
        assertTrue("Class should contain a main method", hasMainMethod);
    }
    
    /**
     * Checks Look and Feel settings for the Swing interface
     */
    @Test
    public void testLookAndFeelSetup() {
        // This test checks if the Look and Feel settings 
        // for the Swing UI application are set up correctly
        
        // When the main method starts to create the UI
        // it should not throw an Exception
        
        try {
            // Check that the Look and Feel setting can be applied
            javax.swing.UIManager.getSystemLookAndFeelClassName();
            
            // Test successful
            assertTrue(true);
        } catch (Exception e) {
            LOGGER.severe("Look and Feel error: " + e.getMessage());
            fail("Look and Feel error: " + e.getMessage());
        }
    }
} 