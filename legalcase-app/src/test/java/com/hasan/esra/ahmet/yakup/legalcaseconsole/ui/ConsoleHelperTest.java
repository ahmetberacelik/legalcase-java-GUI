package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Test class for ConsoleHelper
 */
public class ConsoleHelperTest {
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
        ConsoleHelper.resetScanner();
    }
    
    @Test
    public void Test_DisplayMessage_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayMessage("Test message");
        
        // Verify
        assertEquals("Test message" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayError_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayError("Test error");
        
        // Verify
        assertEquals("ERROR: Test error" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplaySuccess_OutputsCorrectly() {
        // Test
        ConsoleHelper.displaySuccess("Test success");
        
        // Verify
        assertEquals("SUCCESS: Test success" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayInfo_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayInfo("Test info");
        
        // Verify
        assertEquals("INFO: Test info" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayWarning_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayWarning("Test warning");
        
        // Verify
        assertEquals("WARNING: Test warning" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_DisplayMenuHeader_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayMenuHeader("Test Menu");
        
        // Verify
        String expected = "==================================================" + System.lineSeparator() +
                          "TEST MENU" + System.lineSeparator() +
                          "==================================================" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }
    
    @Test
    public void Test_DisplayMenuOption_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayMenuOption(1, "Test Option");
        
        // Verify
        assertEquals("1. Test Option" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_ReadString_ReturnsUserInput() {
        // Setup
        String testInput = "test input";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        String result = ConsoleHelper.readString("Enter text");
        
        // Verify
        assertEquals(testInput, result);
        assertTrue(outContent.toString().contains("Enter text: "));
    }
    
    @Test
    public void Test_ReadRequiredString_RejectsEmptyInput() {
        // Setup - first empty input, then valid input
        String testInput = "\nvalid input";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        String result = ConsoleHelper.readRequiredString("Enter required text");
        
        // Verify
        assertEquals("valid input", result);
        assertTrue(outContent.toString().contains("ERROR: Input cannot be empty"));
    }
    
    @Test
    public void Test_ReadInt_ReturnsValidInteger() {
        // Setup
        String testInput = "42";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = ConsoleHelper.readInt("Enter number");
        
        // Verify
        assertEquals(42, result);
        assertTrue(outContent.toString().contains("Enter number: "));
    }
    
    @Test
    public void Test_ReadInt_RejectsInvalidInput() {
        // Setup - first invalid input, then valid input
        String testInput = "not a number\n42";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = ConsoleHelper.readInt("Enter number");
        
        // Verify
        assertEquals(42, result);
        assertTrue(outContent.toString().contains("ERROR: Invalid number. Please try again"));
    }
    
    @Test
    public void Test_ReadInt_WithRange_ReturnsValidInteger() {
        // Setup
        String testInput = "5";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = ConsoleHelper.readInt("Enter number", 1, 10);
        
        // Verify
        assertEquals(5, result);
    }
    
    @Test
    public void Test_ReadInt_WithRange_RejectsOutOfRangeInput() {
        // Setup - first out of range input, then valid input
        String testInput = "20\n5";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        int result = ConsoleHelper.readInt("Enter number", 1, 10);
        
        // Verify
        assertEquals(5, result);
        assertTrue(outContent.toString().contains("Please enter a number between 1 and 10"));
    }
    
    @Test
    public void Test_ReadLong_ReturnsValidLong() {
        // Setup
        String testInput = "9223372036854775807"; // Max long value
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        long result = ConsoleHelper.readLong("Enter long number");
        
        // Verify
        assertEquals(Long.MAX_VALUE, result);
    }
    
    @Test
    public void Test_ReadLong_RejectsInvalidInput() {
        // Setup - first invalid input, then valid input
        String testInput = "not a number\n42";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        long result = ConsoleHelper.readLong("Enter long number");
        
        // Verify
        assertEquals(42L, result);
        assertTrue(outContent.toString().contains("ERROR: Invalid number. Please try again"));
    }
    
    @Test
    public void Test_ReadDate_ReturnsValidDate() {
        // Setup
        String testInput = "2025-04-01";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalDate result = ConsoleHelper.readDate("Enter date");
        
        // Verify
        assertEquals(LocalDate.of(2025, 4, 1), result);
    }
    
    @Test
    public void Test_ReadDate_RejectsInvalidFormat() {
        // Setup - first invalid format, then valid input
        String testInput = "01/04/2025\n2025-04-01";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalDate result = ConsoleHelper.readDate("Enter date");
        
        // Verify
        assertEquals(LocalDate.of(2025, 4, 1), result);
        assertTrue(outContent.toString().contains("ERROR: Invalid date format. Please use yyyy-MM-dd format"));
    }
    
    @Test
    public void Test_ReadTime_ReturnsValidTime() {
        // Setup
        String testInput = "14:30";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalTime result = ConsoleHelper.readTime("Enter time");
        
        // Verify
        assertEquals(LocalTime.of(14, 30), result);
    }
    
    @Test
    public void Test_ReadTime_RejectsInvalidFormat() {
        // Setup - first invalid format, then valid input
        String testInput = "2:30 PM\n14:30";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalTime result = ConsoleHelper.readTime("Enter time");
        
        // Verify
        assertEquals(LocalTime.of(14, 30), result);
        assertTrue(outContent.toString().contains("ERROR: Invalid time format. Please use HH:mm format"));
    }
    
    @Test
    public void Test_ReadDateTime_ReturnsValidDateTime() {
        // Setup - date and time inputs
        String testInput = "2025-04-01\n14:30";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        LocalDateTime result = ConsoleHelper.readDateTime("Enter date and time");
        
        // Verify
        assertEquals(LocalDateTime.of(2025, 4, 1, 14, 30), result);
        assertTrue(outContent.toString().contains("Enter date and time"));
    }
    
    @Test
    public void Test_ReadBoolean_ReturnsTrue_ForYesInput() {
        // Setup
        String testInput = "y";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = ConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertTrue(result);
    }
    
    @Test
    public void Test_ReadBoolean_ReturnsFalse_ForNoInput() {
        // Setup
        String testInput = "n";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = ConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    public void Test_ReadBoolean_AcceptsLongForms() {
        // Setup
        String testInput = "yes";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = ConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertTrue(result);
        
        // Setup for "no"
        outContent.reset();
        testInput = "no";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        result = ConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertFalse(result);
    }
    
    @Test
    public void Test_ReadBoolean_RejectsInvalidInput() {
        // Setup - first invalid input, then valid input
        String testInput = "maybe\ny";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        boolean result = ConsoleHelper.readBoolean("Confirm");
        
        // Verify
        assertTrue(result);
        assertTrue(outContent.toString().contains("ERROR: Please enter 'y' or 'n'"));
    }
    
    @Test
    public void Test_ReadEnum_ReturnsValidEnum() {
        // Setup
        String testInput = "2";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        TestEnum result = ConsoleHelper.readEnum("Select option", TestEnum.class);
        
        // Verify
        assertEquals(TestEnum.VALUE2, result);
    }
    
    @Test
    public void Test_ReadEnum_RejectsInvalidOption() {
        // Setup - first invalid input, then valid input
        String testInput = "10\n2";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        TestEnum result = ConsoleHelper.readEnum("Select option", TestEnum.class);
        
        // Verify
        assertEquals(TestEnum.VALUE2, result);
        assertTrue(outContent.toString().contains("Enter your choice"));
    }
    
    @Test
    public void Test_PressEnterToContinue_ContinuesAfterEnter() {
        // Setup
        String testInput = "\n";
        ConsoleHelper.setScanner(new Scanner(testInput));
        
        // Test
        ConsoleHelper.pressEnterToContinue();
        
        // Verify
        assertEquals("Press Enter to continue...", outContent.toString());
    }
    
    @Test
    public void Test_DisplayHorizontalLine_OutputsCorrectly() {
        // Test
        ConsoleHelper.displayHorizontalLine();
        
        // Verify
        assertEquals("--------------------------------------------------" + System.lineSeparator(), outContent.toString());
    }
    
    @Test
    public void Test_ClearScreen_OutputsCorrectSequence() {
        // Test - Windows platformunda olduğumuzu simüle edelim
        System.setProperty("os.name", "Windows 10");
        
        try {
            // clearScreen metodu ProcessBuilder kullanır, bu yüzden doğrudan çıktıyı test edemeyiz
            // Ancak Exception fırlatmamasını test edebiliriz
            ConsoleHelper.clearScreen();
            // Exception fırlatmadıysa test başarılı
            assertTrue(true);
        } catch (Exception e) {
            // Fallback mekanizması çalışmalı, bu durumda da Exception fırlatmamalı
            fail("clearScreen metodu Exception fırlattı: " + e.getMessage());
        }
        
        // Test - Unix platformunda olduğumuzu simüle edelim
        System.setProperty("os.name", "Linux");
        outContent.reset();
        
        try {
            ConsoleHelper.clearScreen();
            // Unix platformunda ANSI escape sequence kullanılır
            // Ancak ProcessBuilder'ın çıktısını yakalayamadığımız için
            // sadece Exception fırlatmamasını test ediyoruz
            assertTrue(true);
        } catch (Exception e) {
            fail("clearScreen metodu Exception fırlattı: " + e.getMessage());
        }
        
        // Test - Exception durumunu simüle edelim
        outContent.reset();
        
        try {
            // Exception fırlatacak bir durum oluşturalım
            // ProcessBuilder'ı mock etmek zor olduğu için sadece fallback'in çalışıp çalışmadığını test edelim
            ConsoleHelper.clearScreen();
            // Fallback mekanizması 50 boş satır yazdırır, bu yüzden outContent boş olmamalı
            assertFalse(outContent.toString().isEmpty());
        } catch (Exception e) {
            fail("clearScreen metodu fallback mekanizması çalışmadı: " + e.getMessage());
        }
        
        // Test sonrası orijinal os.name değerini geri yükleyelim
        System.setProperty("os.name", System.getProperty("os.name"));
    }
    
    @Test
    public void Test_CloseScanner_ClosesScanner() {
        // Özel bir Scanner oluşturalım
        Scanner testScanner = new Scanner("test");
        ConsoleHelper.setScanner(testScanner);
        
        // Scanner'ı kapatalım
        ConsoleHelper.closeScanner();
        
        // Scanner kapatıldıktan sonra kullanmaya çalışırsak NoSuchElementException fırlatmalı
        try {
            testScanner.nextLine();
            fail("Scanner kapatılmadı, NoSuchElementException fırlatılmadı");
        } catch (NoSuchElementException e) {
            // Scanner kapatıldığında NoSuchElementException fırlatılır, bu beklenen davranış
            assertTrue(true);
        } catch (IllegalStateException e) {
            // Scanner kapatıldığında bazen IllegalStateException da fırlatılabilir, bu da kabul edilebilir
            assertTrue(true);
        }
        
        // Test sonrası Scanner'ı sıfırlayalım
        ConsoleHelper.resetScanner();
    }
    
    // Enum for testing readEnum method
    private enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }
}
