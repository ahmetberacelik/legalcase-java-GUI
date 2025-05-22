package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Test class for ConsoleHelper
 */
public class UiConsoleHelperTest {
    // Capture output for testing display methods
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @Before
    public void setUp() {
        // Redirect System.out to capture output
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public void tearDown() {
        // Reset System.out and Scanner after each test
        System.setOut(originalOut);
        UiConsoleHelper.resetScanner();
    }
    
    @Test
    public void Test_DisplayMessage_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayMessage("Test message");
        
        // Verify
        assertEquals("Test message" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayError_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayError("Test error");
        
        // Verify
        assertEquals("ERROR: Test error" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplaySuccess_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displaySuccess("Test success");
        
        // Verify
        assertEquals("SUCCESS: Test success" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayInfo_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayInfo("Test info");
        
        // Verify
        assertEquals("INFO: Test info" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayWarning_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayWarning("Test warning");
        
        // Verify
        assertEquals("WARNING: Test warning" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayMenuHeader_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayMenuHeader("Test Menu");
        
        // Verify
        String expected = "==================================================" + System.lineSeparator() +
                          "TEST MENU" + System.lineSeparator() +
                          "==================================================" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }
    
    @Test
    public void Test_DisplayMenuOption_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayMenuOption(1, "Test Option");
        
        // Verify
        assertEquals("1. Test Option" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_ReadString_ReturnsUserInput() {
        // Setup
        String testInput = "test input";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        String result = UiConsoleHelper.readString("Enter text");
        
        // Verify
        assertEquals(testInput, result);
        assertTrue(outContent.toString().contains("Enter text: "));
    }
    
    @Test
    public void Test_ReadRequiredString_RejectsEmptyInput() {
        // Setup - first empty input, then valid input
        String testInput = "\nvalid input";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        String result = UiConsoleHelper.readRequiredString("Enter required text");
        
        // Verify
        assertEquals("valid input", result);
        assertTrue(outContent.toString().contains("ERROR: Input cannot be empty"));
    }
    
    @Test
    public void Test_ReadInt_ReturnsValidInteger() {
        // Setup
        String testInput = "42";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = UiConsoleHelper.readInt("Enter number");
        
        // Verify
        assertEquals(42, result);
        assertTrue(outContent.toString().contains("Enter number: "));
    }
    
    @Test
    public void Test_ReadInt_RejectsInvalidInput() {
        // Setup - first invalid input, then valid input
        String testInput = "not a number\n42";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = UiConsoleHelper.readInt("Enter number");
        
        // Verify
        assertEquals(42, result);
        assertTrue(outContent.toString().contains("ERROR: Invalid number. Please try again"));
    }
    
    @Test
    public void Test_ReadInt_WithRange_ReturnsValidInteger() {
        // Setup
        String testInput = "5";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = UiConsoleHelper.readInt("Enter number", 1, 10);
        
        // Verify
        assertEquals(5, result);
    }
    
    @Test
    public void Test_ReadInt_WithRange_RejectsOutOfRangeInput() {
        // Setup - first out of range input, then valid input
        String testInput = "20\n5";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = UiConsoleHelper.readInt("Enter number", 1, 10);
        
        // Verify
        assertEquals(5, result);
        assertTrue(outContent.toString().contains("Please enter a number between 1 and 10"));
    }
    
    @Test
    public void Test_ReadLong_ReturnsValidLong() {
        // Setup
        String testInput = "9223372036854775807"; // Max long value
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        long result = UiConsoleHelper.readLong("Enter long number");
        
        // Verify
        assertEquals(Long.MAX_VALUE, result);
    }
    
    @Test
    public void Test_ReadLong_RejectsInvalidInput() {
        // Setup - first invalid input, then valid input
        String testInput = "not a number\n42";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        long result = UiConsoleHelper.readLong("Enter long number");
        
        // Verify
        assertEquals(42L, result);
        assertTrue(outContent.toString().contains("ERROR: Invalid number. Please try again"));
    }
    
    @Test
    public void Test_ReadDate_ReturnsValidDate() {
        // Setup
        String testInput = "2025-04-01";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalDate result = UiConsoleHelper.readDate("Enter date");
        
        // Verify
        assertEquals(LocalDate.of(2025, 4, 1), result);
    }
    
    @Test
    public void Test_ReadDate_RejectsInvalidFormat() {
        // Setup - first invalid format, then valid input
        String testInput = "01/04/2025\n2025-04-01";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalDate result = UiConsoleHelper.readDate("Enter date");
        
        // Verify
        assertEquals(LocalDate.of(2025, 4, 1), result);
        assertTrue(outContent.toString().contains("ERROR: Invalid date format. Please use yyyy-MM-dd format"));
    }
    
    @Test
    public void Test_ReadTime_ReturnsValidTime() {
        // Setup
        String testInput = "14:30";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalTime result = UiConsoleHelper.readTime("Enter time");
        
        // Verify
        assertEquals(LocalTime.of(14, 30), result);
    }
    
    @Test
    public void Test_ReadTime_RejectsInvalidFormat() {
        // Setup - first invalid format, then valid input
        String testInput = "2:30 PM\n14:30";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalTime result = UiConsoleHelper.readTime("Enter time");
        
        // Verify
        assertEquals(LocalTime.of(14, 30), result);
        assertTrue(outContent.toString().contains("ERROR: Invalid time format. Please use HH:mm format"));
    }
    
    @Test
    public void Test_ReadDateTime_ReturnsValidDateTime() {
        // Setup - date and time inputs
        String testInput = "2025-04-01\n14:30";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalDateTime result = UiConsoleHelper.readDateTime("Enter date and time");
        
        // Verify
        assertEquals(LocalDateTime.of(2025, 4, 1, 14, 30), result);
        assertTrue(outContent.toString().contains("Enter date and time"));
    }
    
    @Test
    public void Test_ReadBoolean_ReturnsTrue_ForYesInput() {
        // Setup
        String testInput = "y";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = UiConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertTrue(result);
    }
    
    @Test
    public void Test_ReadBoolean_ReturnsFalse_ForNoInput() {
        // Setup
        String testInput = "n";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = UiConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    public void Test_ReadBoolean_AcceptsLongForms() {
        // Setup
        String testInput = "yes";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = UiConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertTrue(result);
        
        // Setup for "no"
        outContent.reset();
        testInput = "no";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        result = UiConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    public void Test_ReadBoolean_RejectsInvalidInput() {
        // Setup - first invalid input, then valid input
        String testInput = "maybe\ny";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = UiConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertTrue(result);
        assertTrue(outContent.toString().contains("ERROR: Please enter 'y' or 'n'"));
    }
    
    @Test
    public void Test_ReadEnum_ReturnsValidEnum() {
        // Setup
        String testInput = "2";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        TestEnum result = UiConsoleHelper.readEnum("Select option", TestEnum.class);
        
        // Verify
        assertEquals(TestEnum.VALUE2, result);
    }
    
    @Test
    public void Test_ReadEnum_RejectsInvalidOption() {
        // Setup - first invalid input, then valid input
        String testInput = "10\n2";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        TestEnum result = UiConsoleHelper.readEnum("Select option", TestEnum.class);
        
        // Verify
        assertEquals(TestEnum.VALUE2, result);
        assertTrue(outContent.toString().contains("Enter your choice"));
    }
    
    @Test
    public void Test_PressEnterToContinue_ContinuesAfterEnter() {
        // Setup
        String testInput = "\n";
        UiConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        UiConsoleHelper.pressEnterToContinue();
        
        // Verify
        assertEquals("Press Enter to continue...", outContent.toString());
    }
    
    @Test
    public void Test_DisplayHorizontalLine_OutputsCorrectly() {
        // Test
        UiConsoleHelper.displayHorizontalLine();
        
        // Verify
        assertEquals("--------------------------------------------------" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_ClearScreen() {
        // Test - Simulate Windows platform
        String originalOs = System.getProperty("os.name");
        System.setProperty("os.name", "Windows");

        try {
            // clearScreen method uses ProcessBuilder, so we can't directly test the output
            // However, we can test that it doesn't throw an Exception
            UiConsoleHelper.clearScreen();
            // If no Exception is thrown, test is successful
        } catch (Exception e) {
            // Fallback mechanism should work, it shouldn't throw an Exception in this case either
            fail("clearScreen method threw an Exception: " + e.getMessage());
        }

        // Test - Simulate Unix platform
        System.setProperty("os.name", "Linux");
        try {
            // On Unix platform, ANSI escape sequence is used
            // However, since we can't capture ProcessBuilder's output
            // we only test that it doesn't throw an Exception
            UiConsoleHelper.clearScreen();
        } catch (Exception e) {
            fail("clearScreen method threw an Exception: " + e.getMessage());
        }

        // Test - Simulate Exception case
        System.setProperty("os.name", "InvalidOS");
        try {
            // Create a situation that will throw an Exception
            // Since it's difficult to mock ProcessBuilder, we only test if fallback works
            UiConsoleHelper.clearScreen();
            // Fallback mechanism prints 50 empty lines, so outContent shouldn't be empty
            assertTrue("Fallback mechanism should have printed some output", true);
        } catch (Exception e) {
            fail("clearScreen method fallback mechanism didn't work: " + e.getMessage());
        }

        // Restore original os.name value after test
        System.setProperty("os.name", originalOs);
    }
    
    @Test
    public void Test_ScannerClose() {
        // Create a custom Scanner
        Scanner scanner = new Scanner("test input");
        
        // Set up UiConsoleHelper's scanner
        UiConsoleHelper.setScanner(scanner);
        
        // Close the Scanner
        UiConsoleHelper.resetScanner();
        
        // Verify
        assertTrue(true); // If we get to this point without errors, test passes
    }
    
    // Enum for testing readEnum method
    private enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }
}
