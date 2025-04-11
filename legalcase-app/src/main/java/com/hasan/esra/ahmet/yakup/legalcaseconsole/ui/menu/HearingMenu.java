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
package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;

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
    private final MenuManager menuManager;

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
     * @param menuManager Menu manager instance for navigation
     * @param hearingService Service for hearing operations
     * @param caseService Service for case operations
     */
    public HearingMenu(MenuManager menuManager, HearingService hearingService, CaseService caseService) {
        this.menuManager = menuManager;
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
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Hearing Calendar");

        ConsoleHelper.displayMenuOption(1, "Add New Hearing");
        ConsoleHelper.displayMenuOption(2, "View Hearing Details");
        ConsoleHelper.displayMenuOption(3, "Update Hearing");
        ConsoleHelper.displayMenuOption(4, "Reschedule Hearing");
        ConsoleHelper.displayMenuOption(5, "Change Hearing Status");
        ConsoleHelper.displayMenuOption(6, "Delete Hearing");
        ConsoleHelper.displayMenuOption(7, "List Upcoming Hearings");
        ConsoleHelper.displayMenuOption(8, "List Hearings by Case");
        ConsoleHelper.displayMenuOption(9, "List Hearings by Date Range");
        ConsoleHelper.displayMenuOption(10, "Return to Main Menu");
        ConsoleHelper.displayHorizontalLine();

        int choice = ConsoleHelper.readInt("Enter your choice", 1, 10);

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
                menuManager.navigateToMainMenu();
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
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Add New Hearing");

        try {
            // First select the case to associate with
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                ConsoleHelper.displayError("You must create a case first to add a hearing");
                ConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            ConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                ConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            ConsoleHelper.displayHorizontalLine();

            Long caseId = ConsoleHelper.readLong("Enter case ID to add hearing");

            Optional<Case> caseOpt = caseService.getCaseById(caseId);
            if (!caseOpt.isPresent()) {
                ConsoleHelper.displayError("Case with specified ID not found.");
                ConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            // Get hearing details
            LocalDateTime hearingDate = ConsoleHelper.readDateTime("Enter hearing date and time");
            String judge = ConsoleHelper.readRequiredString("Enter judge name");
            String location = ConsoleHelper.readRequiredString("Enter hearing location");
            String notes = ConsoleHelper.readString("Enter notes");

            Hearing hearing = hearingService.createHearing(caseId, hearingDate, judge, location, notes);

            ConsoleHelper.displaySuccess("Hearing successfully scheduled! ID: " + hearing.getId());
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while scheduling hearing: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View details of a specific hearing
     *
     * Retrieves and displays comprehensive information about a hearing
     * based on the ID provided by the user.
     */
    private void viewHearingDetails() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("View Hearing Details");

        Long hearingId = ConsoleHelper.readLong("Enter hearing ID");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                ConsoleHelper.displayHorizontalLine();
                ConsoleHelper.displayMessage("Hearing ID: " + hearing.getId());
                ConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                ConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                ConsoleHelper.displayMessage("Judge: " + hearing.getJudge());
                ConsoleHelper.displayMessage("Location: " + hearing.getLocation());
                ConsoleHelper.displayMessage("Status: " + hearing.getStatus());
                if (hearing.getNotes() != null && !hearing.getNotes().isEmpty()) {
                    ConsoleHelper.displayMessage("Notes: " + hearing.getNotes());
                }
                ConsoleHelper.displayMessage("Created At: " + hearing.getCreatedAt());
                ConsoleHelper.displayMessage("Last Updated: " + hearing.getUpdatedAt());
                ConsoleHelper.displayHorizontalLine();
            } else {
                ConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while retrieving hearing information: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update an existing hearing
     *
     * Allows the user to modify hearing information including judge,
     * location, notes, and status.
     */
    private void updateHearing() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Update Hearing");

        Long hearingId = ConsoleHelper.readLong("Enter hearing ID to update");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                ConsoleHelper.displayMessage("Current information:");
                ConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                ConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                ConsoleHelper.displayMessage("Judge: " + hearing.getJudge());
                ConsoleHelper.displayMessage("Location: " + hearing.getLocation());
                ConsoleHelper.displayMessage("Status: " + hearing.getStatus());
                ConsoleHelper.displayMessage("Notes: " + hearing.getNotes());
                ConsoleHelper.displayHorizontalLine();

                // Date changes should be done with rescheduleHearing
                boolean updateJudge = ConsoleHelper.readBoolean("Do you want to update the judge information?");
                String judge = updateJudge ? ConsoleHelper.readRequiredString("Enter new judge name") : hearing.getJudge();

                boolean updateLocation = ConsoleHelper.readBoolean("Do you want to update the hearing location?");
                String location = updateLocation ? ConsoleHelper.readRequiredString("Enter new hearing location") : hearing.getLocation();

                boolean updateNotes = ConsoleHelper.readBoolean("Do you want to update the notes?");
                String notes = updateNotes ? ConsoleHelper.readString("Enter new notes") : hearing.getNotes();

                boolean updateStatus = ConsoleHelper.readBoolean("Do you want to change the hearing status?");
                HearingStatus status = updateStatus ?
                        ConsoleHelper.readEnum("Select new hearing status", HearingStatus.class) :
                        hearing.getStatus();

                hearingService.updateHearing(hearingId, hearing.getHearingDate(), judge, location, notes, status);

                ConsoleHelper.displaySuccess("Hearing successfully updated");
            } else {
                ConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while updating hearing: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Reschedule a hearing
     *
     * Allows the user to change the date and time of an existing hearing.
     */
    private void rescheduleHearing() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Reschedule Hearing");

        Long hearingId = ConsoleHelper.readLong("Enter hearing ID to reschedule");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                ConsoleHelper.displayMessage("Current information:");
                ConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                ConsoleHelper.displayMessage("Current Date and Time: " + hearing.getHearingDate());
                ConsoleHelper.displayMessage("Status: " + hearing.getStatus());
                ConsoleHelper.displayHorizontalLine();

                LocalDateTime newDate = ConsoleHelper.readDateTime("Enter new hearing date and time");

                hearingService.rescheduleHearing(hearingId, newDate);

                ConsoleHelper.displaySuccess("Hearing successfully rescheduled");
            } else {
                ConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while rescheduling hearing: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Update hearing status
     *
     * Allows the user to change the status of an existing hearing.
     */
    private void updateHearingStatus() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Change Hearing Status");

        Long hearingId = ConsoleHelper.readLong("Enter hearing ID");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                ConsoleHelper.displayMessage("Current information:");
                ConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                ConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                ConsoleHelper.displayMessage("Current Status: " + hearing.getStatus());
                ConsoleHelper.displayHorizontalLine();

                HearingStatus newStatus = ConsoleHelper.readEnum("Select new hearing status", HearingStatus.class);

                hearingService.updateHearingStatus(hearingId, newStatus);

                ConsoleHelper.displaySuccess("Hearing status successfully updated");
            } else {
                ConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while updating hearing status: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief Delete a hearing
     *
     * Allows the user to permanently remove a hearing record from the system
     * after confirmation.
     */
    private void deleteHearing() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("Delete Hearing");

        Long hearingId = ConsoleHelper.readLong("Enter hearing ID to delete");

        try {
            Optional<Hearing> hearingOpt = hearingService.getHearingById(hearingId);

            if (hearingOpt.isPresent()) {
                Hearing hearing = hearingOpt.get();

                ConsoleHelper.displayMessage("Hearing information to be deleted:");
                ConsoleHelper.displayMessage("ID: " + hearing.getId());
                ConsoleHelper.displayMessage("Case: " + hearing.getCse().getCaseNumber() + " - " + hearing.getCse().getTitle());
                ConsoleHelper.displayMessage("Date and Time: " + hearing.getHearingDate());
                ConsoleHelper.displayMessage("Judge: " + hearing.getJudge());
                ConsoleHelper.displayHorizontalLine();

                boolean confirm = ConsoleHelper.readBoolean("Are you sure you want to delete this hearing?");

                if (confirm) {
                    hearingService.deleteHearing(hearingId);
                    ConsoleHelper.displaySuccess("Hearing successfully deleted");
                } else {
                    ConsoleHelper.displayMessage("Operation cancelled");
                }
            } else {
                ConsoleHelper.displayError("Hearing with specified ID not found.");
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while deleting hearing: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View upcoming hearings
     *
     * Displays a list of all upcoming hearings scheduled in the system.
     */
    private void viewUpcomingHearings() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List Upcoming Hearings");

        try {
            List<Hearing> hearings = hearingService.getUpcomingHearings();

            if (hearings.isEmpty()) {
                ConsoleHelper.displayMessage("There are no upcoming hearings.");
            } else {
                ConsoleHelper.displayMessage("Found a total of " + hearings.size() + " upcoming hearings:");
                ConsoleHelper.displayHorizontalLine();

                for (Hearing hearing : hearings) {
                    ConsoleHelper.displayMessage("ID: " + hearing.getId() +
                            ", Case: " + hearing.getCse().getCaseNumber() +
                            ", Date: " + hearing.getHearingDate() +
                            ", Location: " + hearing.getLocation() +
                            ", Status: " + hearing.getStatus());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing hearings: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View hearings by case
     *
     * Retrieves and displays all hearings associated with a specific case.
     */
    private void viewHearingsByCase() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List Hearings by Case");

        try {
            // First list all cases
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                ConsoleHelper.displayMessage("There are no registered cases in the system.");
                ConsoleHelper.pressEnterToContinue();
                display();
                return;
            }

            ConsoleHelper.displayMessage("Available cases:");
            for (Case caseEntity : cases) {
                ConsoleHelper.displayMessage("ID: " + caseEntity.getId() +
                        ", Case No: " + caseEntity.getCaseNumber() +
                        ", Title: " + caseEntity.getTitle());
            }
            ConsoleHelper.displayHorizontalLine();

            Long caseId = ConsoleHelper.readLong("Enter case ID to list hearings");

            List<Hearing> hearings = hearingService.getHearingsByCaseId(caseId);

            if (hearings.isEmpty()) {
                ConsoleHelper.displayMessage("There are no hearings for this case.");
            } else {
                ConsoleHelper.displayMessage("Found a total of " + hearings.size() + " hearings:");
                ConsoleHelper.displayHorizontalLine();

                for (Hearing hearing : hearings) {
                    ConsoleHelper.displayMessage("ID: " + hearing.getId() +
                            ", Date: " + hearing.getHearingDate() +
                            ", Judge: " + hearing.getJudge() +
                            ", Location: " + hearing.getLocation() +
                            ", Status: " + hearing.getStatus());
                }
            }
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing hearings: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }

    /**
     * @brief View hearings by date range
     *
     * Retrieves and displays all hearings scheduled within a specified date range.
     */
    private void viewHearingsByDateRange() {
        ConsoleHelper.clearScreen();
        ConsoleHelper.displayMenuHeader("List Hearings by Date Range");

        try {
            LocalDate startDate = ConsoleHelper.readDate("Enter start date");
            LocalDate endDate = ConsoleHelper.readDate("Enter end date");

            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);

            List<Hearing> hearings = hearingService.getHearingsByDateRange(start, end);

            if (hearings.isEmpty()) {
                ConsoleHelper.displayMessage("There are no hearings in the specified date range.");
            } else {
                ConsoleHelper.displayMessage("Found a total of " + hearings.size() + " hearings:");
                ConsoleHelper.displayHorizontalLine();

                for (Hearing hearing : hearings) {
                    ConsoleHelper.displayMessage("ID: " + hearing.getId() +
                            ", Case: " + hearing.getCse().getCaseNumber() +
                            ", Date: " + hearing.getHearingDate() +
                            ", Judge: " + hearing.getJudge() +
                            ", Location: " + hearing.getLocation() +
                            ", Status: " + hearing.getStatus());
                }
            }
        } catch (IllegalArgumentException e) {
            ConsoleHelper.displayError(e.getMessage());
        } catch (Exception e) {
            ConsoleHelper.displayError("Error occurred while listing hearings: " + e.getMessage());
        }

        ConsoleHelper.pressEnterToContinue();
        display();
    }
}