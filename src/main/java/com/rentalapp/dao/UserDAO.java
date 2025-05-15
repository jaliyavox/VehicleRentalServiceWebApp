package com.rentalapp.dao;

import com.rentalapp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private static final String USERS_FILE_PATH = "src/main/resources/data/users.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Default constructor
     */
    public UserDAO() {
        // Initialize if needed
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    User user = parseUserFromLine(line);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading users file", e);
        }
        
        return users;
    }
    
    /**
     * Get user by ID
     */
    public User getById(String id) {
        try {
            List<User> users = getAllUsers();
            
            for (User user : users) {
                if (user.getId().equals(id)) {
                    return user;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting user by ID", e);
        }
        
        return null;
    }
    
    /**
     * Get user by ID (alias for backward compatibility)
     */
    public User getUserById(String id) {
        return getById(id);
    }
    
    /**
     * Get user by email
     */
    public User getByEmail(String email) {
        try {
            List<User> users = getAllUsers();
            
            for (User user : users) {
                if (user.getEmail().equalsIgnoreCase(email)) {
                    return user;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting user by email", e);
        }
        
        return null;
    }
    
    /**
     * Authenticate user with email and password
     */
    public User authenticate(String email, String password) {
        try {
            User user = getByEmail(email);
            
            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (Exception e) {
            logger.error("Error authenticating user", e);
        }
        
        return null;
    }
    
    /**
     * Authenticate user (alias for backward compatibility)
     */
    public User authenticateUser(String email, String password) {
        return authenticate(email, password);
    }
    
    /**
     * Check if a username is already taken
     */
    public boolean isUsernameTaken(String username) {
        try {
            List<User> users = getAllUsers();
            
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Error checking if username is taken", e);
        }
        
        return false;
    }
    
    /**
     * Add a new user
     */
    public boolean addUser(User user) {
        if (user.getId() == null || user.getId().trim().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        
        try {
            String userRecord = formatUserToLine(user);
            Path path = Paths.get(USERS_FILE_PATH);
            Files.write(path, (userRecord + System.lineSeparator()).getBytes(), 
                    Files.exists(path) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
            
            return true;
        } catch (IOException e) {
            logger.error("Error adding user", e);
            return false;
        }
    }
    
    /**
     * Update an existing user
     */
    public boolean updateUser(User user) {
        try {
            List<User> users = getAllUsers();
            List<String> lines = new ArrayList<>();
            
            for (User existingUser : users) {
                if (existingUser.getId().equals(user.getId())) {
                    lines.add(formatUserToLine(user));
                } else {
                    lines.add(formatUserToLine(existingUser));
                }
            }
            
            Files.write(Paths.get(USERS_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error updating user", e);
            return false;
        }
    }
    
    /**
     * Delete a user by ID
     */
    public boolean deleteUser(String userId) {
        try {
            List<User> users = getAllUsers();
            List<String> lines = new ArrayList<>();
            
            for (User user : users) {
                if (!user.getId().equals(userId)) {
                    lines.add(formatUserToLine(user));
                }
            }
            
            Files.write(Paths.get(USERS_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error deleting user", e);
            return false;
        }
    }
    
    /**
     * Parse a user from a line in the data file
     */
    private User parseUserFromLine(String line) {
        String[] parts = line.split("\\|");
        
        if (parts.length >= 7) {
            User user = new User();
            
            user.setId(parts[0]);
            user.setEmail(parts[1]);
            user.setPassword(parts[2]);
            user.setFirstName(parts[3]);
            user.setLastName(parts[4]);
            user.setPhone(parts[5]);
            
            try {
                if (parts[6] != null && !parts[6].isEmpty()) {
                    user.setDateOfBirth(LocalDate.parse(parts[6], DATE_FORMATTER));
                }
            } catch (Exception e) {
                logger.error("Error parsing date of birth: " + parts[6], e);
            }
            
            if (parts.length > 7) user.setAddress(parts[7]);
            if (parts.length > 8) user.setLicenseNumber(parts[8]);
            if (parts.length > 9) user.setProfilePicture(parts[9]);
            
            return user;
        }
        
        return null;
    }
    
    /**
     * Format a user as a line for the data file
     */
    private String formatUserToLine(User user) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(user.getId()).append("|")
          .append(user.getEmail()).append("|")
          .append(user.getPassword()).append("|")
          .append(user.getFirstName()).append("|")
          .append(user.getLastName()).append("|")
          .append(user.getPhone()).append("|");
        
        if (user.getDateOfBirth() != null) {
            sb.append(user.getDateOfBirth().format(DATE_FORMATTER));
        }
        
        sb.append("|").append(user.getAddress() != null ? user.getAddress() : "");
        sb.append("|").append(user.getLicenseNumber() != null ? user.getLicenseNumber() : "");
        sb.append("|").append(user.getProfilePicture() != null ? user.getProfilePicture() : "");
        
        return sb.toString();
    }
}