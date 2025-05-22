package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * @brief Helper class for console input/output operations
 * @details This utility class provides methods for standardized console
 * input/output operations, including displaying formatted messages,
 * reading user input with validation, and formatting dates and times.
 */
public class ConsoleHelper {
    /**
     * @brief Scanner for reading user input
     * @details Can be replaced for testing purposes
     */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * @brief Formatter for date input/output
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * @brief Formatter for time input/output
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * @brief Private constructor to prevent instantiation
     * @details Enforces the utility class pattern
     */
    private ConsoleHelper() {
    }

    /**
     * @brief Set a custom scanner for testing purposes
     * @param customScanner The scanner to use for input
     */
    public static void setScanner(Scanner customScanner) {
        scanner = customScanner;
    }

    /**
     * @brief Reset scanner to System.in
     * @details This is useful after tests to restore the default behavior
     */
    public static void resetScanner() {
        scanner = new Scanner(System.in);
    }

    /**
     * @brief Display a message to the console
     * @param message Message to display
     */
    public static void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * @brief Display an error message to the console
     * @param message Error message to display
     */
    public static void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    /**
     * @brief Display a success message to the console
     * @param message Success message to display
     */
    public static void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    /**
     * @brief Display an info message to the console
     * @param message Info message to display
     */
    public static void displayInfo(String message) {
        System.out.println("INFO: " + message);
    }

    /**
     * @brief Display a warning message to the console
     * @param message Warning message to display
     */
    public static void displayWarning(String message) {
        System.out.println("WARNING: " + message);
    }

    /**
     * @brief Display a menu header
     * @details Creates a formatted header with the title centered
     * @param title Menu title
     */
    public static void displayMenuHeader(String title) {
        String line = "=".repeat(50);
        System.out.println(line);
        System.out.println(title.toUpperCase());
        System.out.println(line);
    }

    /**
     * @brief Display a menu option
     * @details Formats a menu option with number and description
     * @param option Option number
     * @param description Option description
     */
    public static void displayMenuOption(int option, String description) {
        System.out.println(option + ". " + description);
    }

    /**
     * @brief Read a string from the console
     * @details Displays a prompt and reads a line of text
     * @param prompt Prompt to display
     * @return User input as a trimmed string
     */
    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * @brief Read a non-empty string from the console
     * @details Repeatedly prompts until a non-empty string is entered
     * @param prompt Prompt to display
     * @return User input as a non-empty trimmed string
     */
    public static String readRequiredString(String prompt) {
        String input;
        do {
            input = readString(prompt);
            if (input.isEmpty()) {
                displayError("Input cannot be empty");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * @brief Read an integer from the console
     * @details Repeatedly prompts until a valid integer is entered
     * @param prompt Prompt to display
     * @return User input as integer
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                displayError("Invalid number. Please try again");
            }
        }
    }

    /**
     * @brief Read an integer from the console within a range
     * @details Repeatedly prompts until a valid integer within the specified range is entered
     * @param prompt Prompt to display
     * @param min Minimum valid value (inclusive)
     * @param max Maximum valid value (inclusive)
     * @return User input as integer within the specified range
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            displayError("Please enter a number between " + min + " and " + max);
        }
    }

    /**
     * @brief Read a long integer from the console
     * @details Repeatedly prompts until a valid long is entered
     * @param prompt Prompt to display
     * @return User input as long
     */
    public static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                displayError("Invalid number. Please try again");
            }
        }
    }

    /**
     * @brief Read a date from the console
     * @details Repeatedly prompts until a valid date in yyyy-MM-dd format is entered
     * @param prompt Prompt to display
     * @return User input as LocalDate
     */
    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (yyyy-MM-dd): ");
                String dateStr = scanner.nextLine().trim();
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                displayError("Invalid date format. Please use yyyy-MM-dd format");
            }
        }
    }

    /**
     * @brief Read a time from the console
     * @details Repeatedly prompts until a valid time in HH:mm format is entered
     * @param prompt Prompt to display
     * @return User input as LocalTime
     */
    public static LocalTime readTime(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (HH:mm): ");
                String timeStr = scanner.nextLine().trim();
                return LocalTime.parse(timeStr, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                displayError("Invalid time format. Please use HH:mm format");
            }
        }
    }

    /**
     * @brief Read a date and time from the console
     * @details Prompts for date and time separately and combines them
     * @param prompt Prompt to display
     * @return User input as LocalDateTime
     */
    public static LocalDateTime readDateTime(String prompt) {
        displayMessage(prompt);
        LocalDate date = readDate("Enter date");
        LocalTime time = readTime("Enter time");
        return LocalDateTime.of(date, time);
    }

    /**
     * @brief Read a boolean (yes/no) from the console
     * @details Repeatedly prompts until 'y', 'yes', 'n', or 'no' is entered
     * @param prompt Prompt to display
     * @return true for 'y' or 'yes', false for 'n' or 'no'
     */
    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                displayError("Please enter 'y' or 'n'");
            }
        }
    }

    /**
     * @brief Read an enum value from the console
     * @details Displays a menu of enum values and prompts for selection
     * @param <T> Enum type
     * @param prompt Prompt to display
     * @param enumClass Enum class
     * @return Selected enum value
     */
    public static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();

        while (true) {
            displayMessage(prompt);
            for (int i = 0; i < values.length; i++) {
                displayMenuOption(i + 1, values[i].name());
            }

            int selection = readInt("Enter your choice", 1, values.length);
            return values[selection - 1];
        }
    }

    /**
     * @brief Clear the console screen
     * @details Attempts to clear the screen based on the operating system,
     * with a fallback for unsupported terminals
     */
    public static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                // Windows terminal
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Unix/Linux/MacOS terminal
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback in case the above doesn't work
            // Print several empty lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * @brief Wait for the user to press Enter to continue
     * @details Displays a prompt and waits for any input
     */
    public static void pressEnterToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * @brief Display a horizontal line
     * @details Creates a visual separator in the console
     */
    public static void displayHorizontalLine() {
        System.out.println("-".repeat(50));
    }

    /**
     * @brief Close the scanner
     * @details Releases system resources associated with the scanner
     */
    public static void closeScanner() {
        scanner.close();
    }
}