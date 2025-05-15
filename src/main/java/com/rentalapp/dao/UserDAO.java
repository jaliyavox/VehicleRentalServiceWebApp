package com.rentalapp.dao;

import com.rentalapp.model.User;
import com.rentalapp.util.FileUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for User entity.
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final String USERS_FILE = "users.txt";
    
    /**
     * Retrieves all users from the data store.
     * 
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        List<String> lines = FileUtil.readAllLines(USERS_FILE);
        List<User> users = new ArrayList<>();
        
        for (String line : lines) {
            try {
                users.add(User.fromString(line));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing user line: " + line, e);
            }
        }
        
        return users;
    }
    
    /**
     * Retrieves a user by ID.
     * 
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID, or null if not found
     */
    public User getUserById(String id) {
        List<User> users = getAllUsers();
        
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Retrieves a user by username.
     * 
     * @param username the username of the user to retrieve
     * @return the user with the specified username, or null if not found
     */
    public User getUserByUsername(String username) {
        List<User> users = getAllUsers();
        
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new user to the data store.
     * 
     * @param user the user to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            return false;
        }
        
        // Check if user with same ID or username already exists
        if (getUserById(user.getId()) != null || getUserByUsername(user.getUsername()) != null) {
            return false;
        }
        
        // Set registration date if not already set
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDate.now());
        }
        
        return FileUtil.appendLine(USERS_FILE, user.toString());
    }
    
    /**
     * Updates an existing user in the data store.
     * 
     * @param user the user to update
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            return false;
        }
        
        List<User> users = getAllUsers();
        boolean found = false;
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                // Check if trying to change username to one that already exists for another user
                if (!users.get(i).getUsername().equals(user.getUsername())) {
                    User existingUser = getUserByUsername(user.getUsername());
                    if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                        return false;
                    }
                }
                
                users.set(i, user);
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        List<String> lines = users.stream()
                .map(User::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(USERS_FILE, lines);
    }
    
    /**
     * Deletes a user from the data store.
     * 
     * @param id the ID of the user to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        
        List<User> users = getAllUsers();
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        
        if (!removed) {
            return false;
        }
        
        List<String> lines = users.stream()
                .map(User::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(USERS_FILE, lines);
    }
    
    /**
     * Authenticates a user.
     * 
     * @param username the username of the user to authenticate
     * @param password the password to check
     * @return the authenticated user, or null if authentication failed
     */
    public User authenticateUser(String username, String password) {
        User user = getUserByUsername(username);
        
        if (user != null && user.authenticate(password)) {
            return user;
        }
        
        return null;
    }
    
    /**
     * Checks if a username is already taken.
     * 
     * @param username the username to check
     * @return true if the username is taken, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return getUserByUsername(username) != null;
    }
    
    /**
     * Gets a list of users by role.
     * 
     * @param role the role to filter by
     * @return a list of users with the specified role
     */
    public List<User> getUsersByRole(String role) {
        List<User> users = getAllUsers();
        
        return users.stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }
}
