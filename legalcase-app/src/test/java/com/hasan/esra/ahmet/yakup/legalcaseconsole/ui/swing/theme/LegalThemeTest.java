package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Unit tests for LegalTheme class
 */
public class LegalThemeTest {
    
    private JButton button;
    private Color testColor;
    
    @Before
    public void setUp() {
        button = new JButton("Test Button");
        testColor = new Color(50, 100, 150);
    }
    
    /**
     * Tests the LegalTheme constructor
     */
    @Test
    public void testConstructor() {
        LegalTheme theme = new LegalTheme();
        assertNotNull("LegalTheme instance should be created", theme);
    }
    
    /**
     * Tests the applyColoredButtonStyle method
     */
    @Test
    public void testApplyColoredButtonStyle() {
        // Call test method
        LegalTheme.applyColoredButtonStyle(button, testColor);
        
        // Validations
        assertEquals("Background color should be set to test color", testColor, button.getBackground());
        assertEquals("Foreground color should be black", Color.BLACK, button.getForeground());
        assertFalse("Focus should not be painted", button.isFocusPainted());
        
        // Check if MouseListener was added
        assertTrue("Button should have at least one MouseListener", button.getMouseListeners().length > 0);
    }
    /**
     * Tests the createMenuButton method
     */
    @Test
    public void testCreateMenuButton() {
        JButton menuButton = LegalTheme.createMenuButton("Test Menu", testColor);
        
        assertNotNull("Menu button should be created", menuButton);
        assertEquals("Button text should be set", "Test Menu", menuButton.getText());
        assertEquals("Background should be white", Color.WHITE, menuButton.getBackground());
        assertEquals("Foreground should be black", Color.BLACK, menuButton.getForeground());
        assertFalse("Focus should not be painted", menuButton.isFocusPainted());
    }
    
    /**
     * Tests the createFooterPanel method
     */
    @Test
    public void testCreateFooterPanel() {
        JPanel footerPanel = LegalTheme.createFooterPanel();
        
        assertNotNull("Footer panel should be created", footerPanel);
        assertEquals("Background should be primary color", LegalTheme.PRIMARY_COLOR, footerPanel.getBackground());
        assertEquals("Layout should be FlowLayout", FlowLayout.class, footerPanel.getLayout().getClass());
        
        // Check components in the panel
        Component[] components = footerPanel.getComponents();
        assertEquals("Footer should contain one component", 1, components.length);
        assertTrue("Component should be a JLabel", components[0] instanceof JLabel);
        
        JLabel label = (JLabel) components[0];
        assertTrue("Label should contain copyright text", label.getText().contains("Legal Case Tracker"));
        assertEquals("Label should have white text", Color.WHITE, label.getForeground());
    }
    
    /**
     * Checks if one color is brighter than the other
     */
    private boolean isColorBrighter(Color color1, Color color2) {
        int brightness1 = (color1.getRed() + color1.getGreen() + color1.getBlue()) / 3;
        int brightness2 = (color2.getRed() + color2.getGreen() + color2.getBlue()) / 3;
        return brightness1 > brightness2;
    }
} 