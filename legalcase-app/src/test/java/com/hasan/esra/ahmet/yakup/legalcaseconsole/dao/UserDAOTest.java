package com.hasan.esra.ahmet.yakup.legalcaseconsole.dao;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import com.j256.ormlite.support.ConnectionSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Unit tests for UserDAO class
 */
public class UserDAOTest {

    private ConnectionSource connectionSource;
    private UserDAO userDAO;

    /**
     * Sets up the test environment before each test
     * @throws SQLException if there's an error with the database connection
     */
    @Before
    public void setUp() throws SQLException {
        // Prepare test database
        TestDatabaseManager.createTables();
        connectionSource = TestDatabaseManager.getConnectionSource();
        userDAO = new UserDAO(connectionSource);
    }

    /**
     * Cleans up the test environment after each test
     * @throws SQLException if there's an error closing the database connection
     */
    @After
    public void tearDown() throws SQLException {
        // Close test database connection
        TestDatabaseManager.closeConnection();
    }

    /**
     * Tests successful user creation with valid data
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_Create_Success_ReturnsUserWithId() throws SQLException {
        // Arrange
        User user = new User("testuser", "password123", "test@example.com", "Test", "User", UserRole.ADMIN);

        // Act
        User createdUser = userDAO.create(user);

        // Assert
        assertNotNull("Created user should not be null", createdUser);
        assertNotNull("Created user should have an ID", createdUser.getId());
        assertEquals("Username should match", "testuser", createdUser.getUsername());
        assertEquals("Email should match", "test@example.com", createdUser.getEmail());
    }

    /**
     * Tests retrieving an existing user by ID
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetById_ExistingUser_ReturnsUser() throws SQLException {
        // Arrange
        User user = new User("getbyiduser", "password123", "getbyid@example.com", "GetById", "User", UserRole.LAWYER);
        userDAO.create(user);

        // Act
        Optional<User> retrievedUserOpt = userDAO.getById(user.getId());

        // Assert
        assertTrue("User should be found", retrievedUserOpt.isPresent());
        User retrievedUser = retrievedUserOpt.get();
        assertEquals("User ID should match", user.getId(), retrievedUser.getId());
        assertEquals("Username should match", "getbyiduser", retrievedUser.getUsername());
    }

    /**
     * Tests retrieving a non-existing user by ID
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetById_NonExistingUser_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<User> retrievedUserOpt = userDAO.getById(9999L);

        // Assert
        assertFalse("Should return empty Optional when user is not found", retrievedUserOpt.isPresent());
    }

    /**
     * Tests retrieving an existing user by username
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetByUsername_ExistingUser_ReturnsUser() throws SQLException {
        // Arrange
        User user = new User("findbyusername", "password123", "findusername@example.com", "FindByUsername", "User", UserRole.ASSISTANT);
        userDAO.create(user);

        // Act
        Optional<User> retrievedUserOpt = userDAO.getByUsername("findbyusername");

        // Assert
        assertTrue("User should be found", retrievedUserOpt.isPresent());
        User retrievedUser = retrievedUserOpt.get();
        assertEquals("User ID should match", user.getId(), retrievedUser.getId());
        assertEquals("Username should match", "findbyusername", retrievedUser.getUsername());
    }

    /**
     * Tests retrieving a non-existing user by username
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetByUsername_NonExistingUser_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<User> retrievedUserOpt = userDAO.getByUsername("nonexistinguser");

        // Assert
        assertFalse("Should return empty Optional when user is not found", retrievedUserOpt.isPresent());
    }

    /**
     * Tests retrieving an existing user by email
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetByEmail_ExistingUser_ReturnsUser() throws SQLException {
        // Arrange
        User user = new User("findbyemail", "password123", "findbyemail@example.com", "FindByEmail", "User", UserRole.JUDGE);
        userDAO.create(user);

        // Act
        Optional<User> retrievedUserOpt = userDAO.getByEmail("findbyemail@example.com");

        // Assert
        assertTrue("User should be found", retrievedUserOpt.isPresent());
        User retrievedUser = retrievedUserOpt.get();
        assertEquals("User ID should match", user.getId(), retrievedUser.getId());
        assertEquals("User email should match", "findbyemail@example.com", retrievedUser.getEmail());
    }

    /**
     * Tests retrieving a non-existing user by email
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetByEmail_NonExistingUser_ReturnsEmptyOptional() throws SQLException {
        // Act
        Optional<User> retrievedUserOpt = userDAO.getByEmail("nonexisting@example.com");

        // Assert
        assertFalse("Should return empty Optional when user is not found", retrievedUserOpt.isPresent());
    }

    /**
     * Tests retrieving all users from the database
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetAll_WithUsers_ReturnsList() throws SQLException {
        // Arrange
        User user1 = new User("getalluser1", "password123", "getall1@example.com", "GetAll1", "User", UserRole.ADMIN);
        User user2 = new User("getalluser2", "password123", "getall2@example.com", "GetAll2", "User", UserRole.LAWYER);
        userDAO.create(user1);
        userDAO.create(user2);

        // Act
        List<User> allUsers = userDAO.getAll();

        // Assert
        assertNotNull("User list should not be null", allUsers);
        assertTrue("User list should not be empty", allUsers.size() >= 2);

        // Created users should be in the list
        boolean user1Found = false;
        boolean user2Found = false;
        for (User user : allUsers) {
            if (user.getUsername().equals("getalluser1")) user1Found = true;
            if (user.getUsername().equals("getalluser2")) user2Found = true;
        }
        assertTrue("getalluser1 should be in the list", user1Found);
        assertTrue("getalluser2 should be in the list", user2Found);
    }

    /**
     * Tests retrieving users by role
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_GetByRole_WithMatchingUsers_ReturnsList() throws SQLException {
        // Arrange
        User lawyer1 = new User("lawyer1", "password123", "lawyer1@example.com", "Lawyer1", "User", UserRole.LAWYER);
        User lawyer2 = new User("lawyer2", "password123", "lawyer2@example.com", "Lawyer2", "User", UserRole.LAWYER);
        User admin = new User("admin1", "password123", "admin1@example.com", "Admin1", "User", UserRole.ADMIN);
        userDAO.create(lawyer1);
        userDAO.create(lawyer2);
        userDAO.create(admin);

        // Act
        List<User> lawyerUsers = userDAO.getByRole(UserRole.LAWYER);

        // Assert
        assertNotNull("Lawyer list should not be null", lawyerUsers);
        assertTrue("Lawyer list should not be empty", lawyerUsers.size() >= 2);

        // Only lawyers should be in the list, not admin
        boolean lawyer1Found = false;
        boolean lawyer2Found = false;
        boolean adminFound = false;
        for (User user : lawyerUsers) {
            if (user.getUsername().equals("lawyer1")) lawyer1Found = true;
            if (user.getUsername().equals("lawyer2")) lawyer2Found = true;
            if (user.getUsername().equals("admin1")) adminFound = true;
        }
        assertTrue("lawyer1 should be in the list", lawyer1Found);
        assertTrue("lawyer2 should be in the list", lawyer2Found);
        assertFalse("admin1 should not be in the list", adminFound);
    }

    /**
     * Tests searching users by name
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_SearchByName_WithMatchingUsers_ReturnsList() throws SQLException {
        // Arrange
        User userJohn = new User("johnsmith", "password123", "john@example.com", "John", "Smith", UserRole.ADMIN);
        User userJane = new User("janedoe", "password123", "jane@example.com", "Jane", "Doe", UserRole.LAWYER);
        User userBob = new User("bobdoe", "password123", "bob@example.com", "Bob", "Doe", UserRole.ASSISTANT);
        userDAO.create(userJohn);
        userDAO.create(userJane);
        userDAO.create(userBob);

        // Act - Search by first name
        List<User> johnUsers = userDAO.searchByName("John");
        // Act - Search by last name
        List<User> doeUsers = userDAO.searchByName("Doe");

        // Assert - First name search
        assertNotNull("John search should not be null", johnUsers);
        assertEquals("John search should return 1 result", 1, johnUsers.size());
        assertEquals("John search should find the correct user", "johnsmith", johnUsers.get(0).getUsername());

        // Assert - Last name search
        assertNotNull("Doe search should not be null", doeUsers);
        assertEquals("Doe search should return 2 results", 2, doeUsers.size());

        // Users with Doe last name should be in the list
        boolean janeFound = false;
        boolean bobFound = false;
        for (User user : doeUsers) {
            if (user.getUsername().equals("janedoe")) janeFound = true;
            if (user.getUsername().equals("bobdoe")) bobFound = true;
        }
        assertTrue("Jane Doe should be in the list", janeFound);
        assertTrue("Bob Doe should be in the list", bobFound);
    }

    /**
     * Tests updating an existing user
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_Update_ExistingUser_Success() throws SQLException {
        // Arrange
        User user = new User("updateuser", "oldpassword", "update@example.com", "Update", "User", UserRole.CLIENT);
        userDAO.create(user);

        // Update user
        user.setEmail("updated@example.com");
        user.setName("UpdatedName");
        user.setPassword("newpassword");

        // Act
        int updatedRows = userDAO.update(user);

        // Assert
        assertEquals("1 row should be updated", 1, updatedRows);

        // Check updated user from database
        Optional<User> updatedUserOpt = userDAO.getById(user.getId());
        assertTrue("Updated user should be found", updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();
        assertEquals("Email should be updated", "updated@example.com", updatedUser.getEmail());
        assertEquals("Name should be updated", "UpdatedName", updatedUser.getName());
        assertEquals("Password should be updated", "newpassword", updatedUser.getPassword());
    }

    /**
     * Tests deleting an existing user
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_Delete_ExistingUser_Success() throws SQLException {
        // Arrange
        User user = new User("deleteuser", "password123", "delete@example.com", "Delete", "User", UserRole.ADMIN);
        userDAO.create(user);

        // Check user exists
        Optional<User> beforeDeleteOpt = userDAO.getById(user.getId());
        assertTrue("User should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = userDAO.delete(user);

        // Assert
        assertEquals("1 row should be deleted", 1, deletedRows);

        // Check user was deleted
        Optional<User> afterDeleteOpt = userDAO.getById(user.getId());
        assertFalse("User should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * Tests deleting an existing user by ID
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_DeleteById_ExistingUser_Success() throws SQLException {
        // Arrange
        User user = new User("deletebyiduser", "password123", "deletebyid@example.com", "DeleteById", "User", UserRole.LAWYER);
        userDAO.create(user);

        // Check user exists
        Optional<User> beforeDeleteOpt = userDAO.getById(user.getId());
        assertTrue("User should exist before deletion", beforeDeleteOpt.isPresent());

        // Act
        int deletedRows = userDAO.deleteById(user.getId());

        // Assert
        assertEquals("1 row should be deleted", 1, deletedRows);

        // Check user was deleted
        Optional<User> afterDeleteOpt = userDAO.getById(user.getId());
        assertFalse("User should not exist after deletion", afterDeleteOpt.isPresent());
    }

    /**
     * Tests deleting a non-existing user by ID
     * @throws SQLException if there's an error during database operation
     */
    @Test
    public void Test_DeleteById_NonExistingUser_ReturnsZero() throws SQLException {
        // Act
        int deletedRows = userDAO.deleteById(9999L);

        // Assert
        assertEquals("Should return 0 when no row is found to delete", 0, deletedRows);
    }
}