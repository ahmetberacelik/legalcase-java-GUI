/**
 * @file HearingMenu.java
 * @brief Hearing management menu class for the Legal Case Tracker system
 *
 * This file contains the HearingMenu class which provides the user interface
 * for managing court hearings including scheduling, viewing, updating, and
 * deleting hearing records.
 *
 * @author Hasan, Esra, Ahmet, Yakup
 * @date 2025-04-11
 */
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.UiConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.console.ConsoleMenuManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * @class HearingMenu
 * @brief Menu for hearing management operations
 *
 * This class provides a user interface for managing court hearings including
 * scheduling, viewing, updating, and deleting hearing records.
 */
public class HearingMenu {
    /**
     * @brief Menu manager reference for navigation control
     * @details Used to navigate between different menus in the application
     */
    private final ConsoleMenuManager consoleMenuManager;
    
    /**
     * @brief Hearing service for hearing operations
     * @details Handles CRUD operations and business logic for court hearings
     */
    private final HearingService hearingService;
    
    /**
     * @brief Case service for case operations
     * @details Used to retrieve case information when associating hearings with cases
     */
    private final CaseService caseService;

    /**
     * @brief Constructor for HearingMenu
     *
     * @param consoleMenuManager Menu manager instance for navigation
     * @param hearingService Service for hearing operations
     * @param caseService Service for case operations
     */
    public HearingMenu(ConsoleMenuManager consoleMenuManager, HearingService hearingService, CaseService caseService) {
        this.consoleMenuManager = consoleMenuManager;
        this.hearingService = hearingService;
        this.caseService = caseService;
    }

    /**
     * @brief Display the hearing menu and handle user selection
     *
     * Presents all available hearing management options to the user
     * and processes their selection.
     */
    public void display() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Hearing Calendar");

        UiConsoleHelper.displayMenuOption(1, "Add New Hearing");
        UiConsoleHelper.displayMenuOption(2, "View Hearing Details");
        UiConsoleHelper.displayMenuOption(3, "Update Hearing");
        UiConsoleHelper.displayMenuOption(4, "Reschedule Hearing");
        UiConsoleHelper.displayMenuOption(5, "Change Hearing Status");
        UiConsoleHelper.displayMenuOption(6, "Delete Hearing");
        UiConsoleHelper.displayMenuOption(7, "List Upcoming Hearings");
        UiConsoleHelper.displayMenuOption(8, "List Hearings by Case");
        UiConsoleHelper.displayMenuOption(9, "List Hearings by Date Range");
        UiConsoleHelper.displayMenuOption(10, "Return to Main Menu");
        UiConsoleHelper.displayHorizontalLine();

        int choice = UiConsoleHelper.readInt("Enter your choice", 1, 10);

        switch (choice) {
            case 1:
                scheduleHearing();
                break;
            case 2:
                viewHearingDetails();
                break;
            case 3:
                updateHearing();
                break;
            case 4:
                rescheduleHearing();
                break;
            case 5:
                updateHearingStatus();
                break;
            case 6:
                deleteHearing();
                break;
            case 7:
                viewUpcomingHearings();
                break;
            case 8:
                viewHearingsByCase();
                break;
            case 9:
                viewHearingsByDateRange();
                break;
            case 10:
                consoleMenuManager.navigateToMainMenu();
                break;
        }
    }

    /**
     * @brief Schedule a new hearing
     *
     * Guides the user through the process of creating a new hearing record
     * by selecting a case and entering hearing details.
     */
    private void scheduleHearing() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Add New Hearing");

        try {
            // First select the case to associate with
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                UiConsoleHelper.displayError("You must create a case first to add a hearing");
                UiConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            UiConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                UiConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            UiConsoleHelper.displayHorizontalLine();

            Long caseId = UiConsoleHelper.readLong("Enter case ID to add hearing");

            Optional<Case> caseOpt = caseService.getCaseById(caseId);
            if (!caseOpt.isPresent()) {
                UiConsoleHelper.displayError("Case with specified ID not found.");
                UiConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            // Get hearing details
            LocalDateTime hearingDate = UiConsoleHelper.readDateTime("Enter hearing date and time");
            String judge = UiConsoleHelper.readRequiredString("Enter judge name");
            String location = UiConsoleHelper.readRequiredString("Enter hearing location");
            String notes = UiConsoleHelper.readString("Enter notes");

            Hearing hearing = hearingService.createHearing(caseId, hearingDate, judge, location, notes);

            UiConsoleHelper.displaySuccess("Hearing successfully scheduled! ID: " + hearing.getId());
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while scheduling hearing: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View details of a specific hearing
     *
     * Retrieves and displays comprehensive information about a hearing
     * based on the ID provided by the user.
     */
    private void viewHearingDetails() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("View Hearing Details");

        Long hearingId = UiConsoleHelper.readLong("Enter hearing ID");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                UiConsoleHelper.displayHorizontalLine();
                UiConsoleHelper.displayMessage("Hearing ID: " + hearing.getId());
                UiConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                UiConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                UiConsoleHelper.displayMessage("Judge: " + hearing.getJudge());
                UiConsoleHelper.displayMessage("Location: " + hearing.getLocation());
                UiConsoleHelper.displayMessage("Status: " + hearing.getStatus());
                if (hearing.getNotes() != null && !hearing.getNotes().isEmpty()) {
                    UiConsoleHelper.displayMessage("Notes: " + hearing.getNotes());
                }
                UiConsoleHelper.displayMessage("Created At: " + hearing.getCreatedAt());
                UiConsoleHelper.displayMessage("Last Updated: " + hearing.getUpdatedAt());
                UiConsoleHelper.displayHorizontalLine();
            } else {
                UiConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while retrieving hearing information: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update an existing hearing
     *
     * Allows the user to modify hearing information including judge,
     * location, notes, and status.
     */
    private void updateHearing() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Update Hearing");

        Long hearingId = UiConsoleHelper.readLong("Enter hearing ID to update");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                UiConsoleHelper.displayMessage("Current information:");
                UiConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                UiConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                UiConsoleHelper.displayMessage("Judge: " + hearing.getJudge());
                UiConsoleHelper.displayMessage("Location: " + hearing.getLocation());
                UiConsoleHelper.displayMessage("Status: " + hearing.getStatus());
                UiConsoleHelper.displayMessage("Notes: " + hearing.getNotes());
                UiConsoleHelper.displayHorizontalLine();

                // Date changes should be done with rescheduleHearing
                boolean updateJudge = UiConsoleHelper.readBoolean("Do you want to update the judge information?");
                String judge = updateJudge ? UiConsoleHelper.readRequiredString("Enter new judge name") : hearing.getJudge();

                boolean updateLocation = UiConsoleHelper.readBoolean("Do you want to update the hearing location?");
                String location = updateLocation ? UiConsoleHelper.readRequiredString("Enter new hearing location") : hearing.getLocation();

                boolean updateNotes = UiConsoleHelper.readBoolean("Do you want to update the notes?");
                String notes = updateNotes ? UiConsoleHelper.readString("Enter new notes") : hearing.getNotes();

                boolean updateStatus = UiConsoleHelper.readBoolean("Do you want to change the hearing status?");
                HearingStatus status = updateStatus ?
                        UiConsoleHelper.readEnum("Select new hearing status", HearingStatus.class) :
                        hearing.getStatus();

                hearingService.updateHearing(hearingId, hearing.getHearingDate(), judge, location, notes, status);

                UiConsoleHelper.displaySuccess("Hearing successfully updated");
            } else {
                UiConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while updating hearing: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Reschedule a hearing
     *
     * Allows the user to change the date and time of an existing hearing.
     */
    private void rescheduleHearing() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Reschedule Hearing");

        Long hearingId = UiConsoleHelper.readLong("Enter hearing ID to reschedule");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                UiConsoleHelper.displayMessage("Current information:");
                UiConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                UiConsoleHelper.displayMessage("Current Date and Time: " + hearing.getHearingDate());
                UiConsoleHelper.displayMessage("Status: " + hearing.getStatus());
                UiConsoleHelper.displayHorizontalLine();

                LocalDateTime newDate = UiConsoleHelper.readDateTime("Enter new hearing date and time");

                hearingService.rescheduleHearing(hearingId, newDate);

                UiConsoleHelper.displaySuccess("Hearing successfully rescheduled");
            } else {
                UiConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while rescheduling hearing: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update hearing status
     *
     * Allows the user to change the status of an existing hearing.
     */
    private void updateHearingStatus() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Change Hearing Status");

        Long hearingId = UiConsoleHelper.readLong("Enter hearing ID");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                UiConsoleHelper.displayMessage("Current information:");
                UiConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                UiConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                UiConsoleHelper.displayMessage("Current Status: " + hearing.getStatus());
                UiConsoleHelper.displayHorizontalLine();

                HearingStatus newStatus = UiConsoleHelper.readEnum("Select new hearing status", HearingStatus.class);

                hearingService.updateHearingStatus(hearingId, newStatus);

                UiConsoleHelper.displaySuccess("Hearing status successfully updated");
            } else {
                UiConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while updating hearing status: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a hearing
     *
     * Allows the user to permanently remove a hearing record from the system
     * after confirmation.
     */
    private void deleteHearing() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("Delete Hearing");

        Long hearingId = UiConsoleHelper.readLong("Enter hearing ID to delete");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                UiConsoleHelper.displayMessage("Hearing information to be deleted:");
                UiConsoleHelper.displayMessage("ID: " + hearing.getId());
                UiConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                UiConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                UiConsoleHelper.displayMessage("Judge: " + hearing.getJudge());
                UiConsoleHelper.displayHorizontalLine();

                boolean confirm = UiConsoleHelper.readBoolean("Are you sure you want to delete this hearing?");

                if (confirm) {
                    hearingService.deleteHearing(hearingId);
                    UiConsoleHelper.displaySuccess("Hearing successfully deleted");
                } else {
                    UiConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                UiConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while deleting hearing: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View upcoming hearings
     *
     * Displays a list of all upcoming hearings scheduled in the system.
     */
    private void viewUpcomingHearings() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List Upcoming Hearings");

        try {
            List<Hearing> hearings = hearingService.getUpcomingHearings();

            if (hearings.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no upcoming hearings.");
            } else {
                UiConsoleHelper.displayMessage("Found a total of " + hearings.size() + " upcoming hearings:");
                UiConsoleHelper.displayHorizontalLine();

                for (Hearing hearing : hearings) {
                    UiConsoleHelper.displayMessage("ID: " + hearing.getId() +
                            ", Case: " + hearing.getCse().getCaseNumber() +
                            ", Date: " + hearing.getHearingDate() +
                            ", Location: " + hearing.getLocation() +
                            ", Status: " + hearing.getStatus());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing hearings: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View hearings by case
     *
     * Retrieves and displays all hearings associated with a specific case.
     */
    private void viewHearingsByCase() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List Hearings by Case");

        try {
            // First list all cases
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no registered cases in the system.");
                UiConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            UiConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                UiConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            UiConsoleHelper.displayHorizontalLine();

            Long caseId = UiConsoleHelper.readLong("Enter case ID to list hearings");

            List<Hearing> hearings = hearingService.getHearingsByCaseId(caseId);

            if (hearings.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no hearings for this case.");
            } else {
                UiConsoleHelper.displayMessage("Found a total of " + hearings.size() + " hearings:");
                UiConsoleHelper.displayHorizontalLine();

                for (Hearing hearing : hearings) {
                    UiConsoleHelper.displayMessage("ID: " + hearing.getId() +
                            ", Date: " + hearing.getHearingDate() +
                            ", Judge: " + hearing.getJudge() +
                            ", Location: " + hearing.getLocation() +
                            ", Status: " + hearing.getStatus());
                }
            }
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing hearings: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View hearings by date range
     *
     * Retrieves and displays all hearings scheduled within a specified date range.
     */
    private void viewHearingsByDateRange() {
        UiConsoleHelper.clearScreen();
        UiConsoleHelper.displayMenuHeader("List Hearings by Date Range");

        try {
            LocalDate startDate = UiConsoleHelper.readDate("Enter start date");
            LocalDate endDate = UiConsoleHelper.readDate("Enter end date");

            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);

            List<Hearing> hearings = hearingService.getHearingsByDateRange(start, end);

            if (hearings.isEmpty()) {
                UiConsoleHelper.displayMessage("There are no hearings in the specified date range.");
            } else {
                UiConsoleHelper.displayMessage("Found a total of " + hearings.size() + " hearings:");
                UiConsoleHelper.displayHorizontalLine();

                for (Hearing hearing : hearings) {
                    UiConsoleHelper.displayMessage("ID: " + hearing.getId() +
                            ", Case: " + hearing.getCse().getCaseNumber() +
                            ", Date: " + hearing.getHearingDate() +
                            ", Judge: " + hearing.getJudge() +
                            ", Location: " + hearing.getLocation() +
                            ", Status: " + hearing.getStatus());
                }
            }
        } catch (IllegalArgumentException e) {
            UiConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            UiConsoleHelper.displayError("Error occurred while listing hearings: " + e.getMessage());
        }

        UiConsoleHelper.pressEnterToContinue();
        display();
    }
}