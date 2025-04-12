/**
 * @file LegalcaseAppTest.java
 * @brief This file contains the test cases for the LegalcaseApp class.
 * @details This file includes test methods to validate the functionality of the LegalcaseApp class. It uses JUnit for unit testing.
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole;

import static org.junit.Assert.*;

import java.util.Scanner;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import org.junit.Test;

/**
 * @brief Test class for LegalcaseApp
 */
public class LegalcaseAppTest {

    /**
     * @brief Tests the main method with exit option input
     * @details This test provides input "3" (presumably an exit option)
     *          to the application and verifies it executes without exceptions
     */
    @Test
    public void testMainSuccess() {
        ConsoleHelper.setScanner(new Scanner("3\n"));
        LegalCaseApp app = new LegalCaseApp();
        app.main(new String[] {});
        assertTrue(true);
    }
}