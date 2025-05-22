package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.swing.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Theme provider class for consistent visual styling across the application
 * 
 * This class provides a collection of constants, utility methods, and styling
 * functions to ensure a consistent visual appearance throughout the legal case
 * management application. It centralizes all theme-related functionality and
 * can be easily modified to update the entire application's look and feel.
 * 
 * Features include:
 * - Predefined color scheme for the legal case management system
 * - Standard font definitions for various UI elements
 * - Helper methods for applying consistent styles to UI components
 * - Factory methods for creating themed UI elements
 * 
 * The theme has been simplified to work effectively with WindowBuilder.
 * 
 * @author Legal Case Management System Team
 * @version 1.0
 */
public class LegalTheme {
    // Color definitions
    /** Primary color - Dark blue, used for main elements and header backgrounds */
    public static final Color PRIMARY_COLOR = new Color(25, 55, 109);
    
    /** Secondary color - Burgundy, used for secondary elements and accents */
    public static final Color SECONDARY_COLOR = new Color(120, 47, 64);
    
    /** Accent color - Orange, used for highlighting important elements */
    public static final Color ACCENT_COLOR = new Color(230, 126, 34);
    
    /** Success color - Green, used for positive actions and confirmations */
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    
    /** Danger color - Red, used for warnings and destructive actions */
    public static final Color DANGER_COLOR = new Color(192, 57, 43);
    
    /** Background color - Light gray-blue, used for application background */
    public static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    
    /** Panel background - White, used for content panels */
    public static final Color PANEL_BACKGROUND = new Color(255, 255, 255);
    
    /** Text color - Dark gray, used for standard text */
    public static final Color TEXT_COLOR = new Color(33, 33, 33);
    
    // Font definitions
    /** Title font - Large bold serif font for main headings */
    public static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 24);
    
    /** Subtitle font - Medium italic serif font for subheadings */
    public static final Font SUBTITLE_FONT = new Font("Serif", Font.ITALIC, 18);
    
    /** Header font - Medium bold serif font for section headers */
    public static final Font HEADER_FONT = new Font("Serif", Font.BOLD, 16);
    
    /** Button font - Bold sans-serif font for buttons */
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    
    /** Normal font - Regular sans-serif font for general text */
    public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    
    /** Small font - Smaller sans-serif font for less important text */
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    
    /**
     * Creates a standardized border for panels
     * 
     * @return A compound border with primary color outline and padding
     */
    public static Border createPanelBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(10, 10, 10, 10)
        );
    }
    
    /**
     * Creates a standardized border for buttons
     * 
     * @return A compound border with rounded corners and padding
     */
    public static Border createButtonBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1, true),
            new EmptyBorder(8, 15, 8, 15)
        );
    }
    
    /**
     * Applies theme styles to a panel
     * 
     * @param panel The panel to style with theme colors and borders
     */
    public static void applyPanelStyle(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(createPanelBorder());
    }
    
    /**
     * Creates a header panel with title
     * 
     * @param title The title text to display in the header
     * @return A styled header panel with the specified title
     */
    public static JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel();
        headerPanel.setName("headerPanel");
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setName("titleLabel");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Applies primary button styling to a button
     * 
     * Configures the button with the primary theme color, standard font,
     * and hover effects for better user interaction feedback.
     * 
     * @param button The button to style
     */
    public static void applyButtonStyle(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.BLACK);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(createButtonBorder());
        button.setFocusPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(35, 65, 119));  // Lighter blue
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }
    
    /**
     * Applies secondary button styling to a button
     * 
     * Configures the button with the secondary theme color, standard font,
     * and hover effects for better user interaction feedback.
     * 
     * @param button The button to style
     */
    public static void applySecondaryButtonStyle(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.BLACK);
        button.setBackground(SECONDARY_COLOR);
        button.setBorder(createButtonBorder());
        button.setFocusPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(130, 57, 74));  // Lighter burgundy
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });
    }
    
    /**
     * Applies custom colored styling to a button
     * 
     * Allows for buttons with custom colors while maintaining consistent
     * styling with the rest of the application's theme.
     * 
     * @param button The button to style
     * @param baseColor The custom base color to use for the button
     */
    public static void applyColoredButtonStyle(JButton button, final Color baseColor) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.BLACK);
        button.setBackground(baseColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(baseColor.darker(), 1, true),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }
    
    /**
     * Applies theme styling to a text field
     * 
     * @param textField The text field to style with theme colors and fonts
     */
    public static void applyTextFieldStyle(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
    }
    
    /**
     * Applies theme styling to a table
     * 
     * Configures the table with consistent colors, fonts, and layout settings
     * to match the application theme. Includes special styling for headers
     * and centers cell content for better readability.
     * 
     * @param table The table to style
     */
    public static void applyTableStyle(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(210, 220, 240));
        table.setSelectionForeground(TEXT_COLOR);
        
        // Header styles
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(HEADER_FONT);
        
        // Center cell contents
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    /**
     * Sets column widths for a table based on percentage values
     * 
     * Configures the column widths of a table according to specified percentage
     * values, ensuring proportional column sizing. Also applies consistent
     * header styling to match the application theme.
     * 
     * @param table The table to configure
     * @param percentages An array of percentage values for each column width
     */
    public static void setColumnWidths(JTable table, int[] percentages) {
        int tableWidth = table.getWidth();
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        for (int i = 0; i < percentages.length && i < table.getColumnCount(); i++) {
            int width = (tableWidth * percentages[i]) / 100;
            table.getColumnModel().getColumn(i).setPreferredWidth(width);
            table.getColumnModel().getColumn(i).setMinWidth(width/2);
        }
        
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setFont(HEADER_FONT);
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });
    }
    
    /**
     * Applies theme styling to a scroll pane
     * 
     * @param scrollPane The scroll pane to style with theme colors and borders
     */
    public static void applyScrollPaneStyle(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
    }
    
    /**
     * Creates a styled menu button with specified text and border color
     * 
     * @param text The button text
     * @param borderColor The color for the button border
     * @return A styled menu button with the specified text and border color
     */
    public static JButton createMenuButton(String text, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 2), 
            new EmptyBorder(15, 25, 15, 25)
        ));
        
        return button;
    }
    
    /**
     * Creates a standardized footer panel with copyright information
     * 
     * @return A styled footer panel with copyright text
     */
    public static JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel copyrightLabel = new JLabel("© 2025 Legal Case Tracker - All Rights Reserved");
        copyrightLabel.setFont(SMALL_FONT);
        copyrightLabel.setForeground(Color.WHITE);
        footerPanel.add(copyrightLabel);
        
        return footerPanel;
    }
}
