package com.rentalapp.dao;

import com.rentalapp.model.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for Admin operations
 */
public class AdminDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminDAO.class);
    private static final String ADMINS_FILE_PATH = "src/main/resources/data/admins.txt";
    
    /**
     * Default constructor
     */
    public AdminDAO() {
        // Initialize if needed
    }
    
    /**
     * Authenticate an admin based on username and password
     */
    public Admin authenticate(String username, String password) {
        try {
            List<Admin> admins = getAllAdmins();
            
            for (Admin admin : admins) {
                if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                    return admin;
                }
            }
        } catch (Exception e) {
            logger.error("Error authenticating admin", e);
        }
        
        return null;
    }
    
    /**
     * Get admin by ID
     */
    public Admin getById(String id) {
        try {
            List<Admin> admins = getAllAdmins();
            
            for (Admin admin : admins) {
                if (admin.getId().equals(id)) {
                    return admin;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting admin by ID", e);
        }
        
        return null;
    }
    
    /**
     * Get all admins from the data file
     */
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMINS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Admin admin = parseAdminFromLine(line);
                    if (admin != null) {
                        admins.add(admin);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading admins file", e);
        }
        
        return admins;
    }
    
    /**
     * Add a new admin
     */
    public boolean addAdmin(Admin admin) {
        if (admin.getId() == null || admin.getId().trim().isEmpty()) {
            admin.setId(UUID.randomUUID().toString());
        }
        
        try {
            String adminRecord = formatAdminToLine(admin);
            Path path = Paths.get(ADMINS_FILE_PATH);
            Files.write(path, (adminRecord + System.lineSeparator()).getBytes(), 
                    Files.exists(path) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
            
            return true;
        } catch (IOException e) {
            logger.error("Error adding admin", e);
            return false;
        }
    }
    
    /**
     * Update an existing admin
     */
    public boolean updateAdmin(Admin admin) {
        try {
            List<Admin> admins = getAllAdmins();
            List<String> lines = new ArrayList<>();
            
            for (Admin existingAdmin : admins) {
                if (existingAdmin.getId().equals(admin.getId())) {
                    lines.add(formatAdminToLine(admin));
                } else {
                    lines.add(formatAdminToLine(existingAdmin));
                }
            }
            
            Files.write(Paths.get(ADMINS_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error updating admin", e);
            return false;
        }
    }
    
    /**
     * Delete an admin by ID
     */
    public boolean deleteAdmin(String adminId) {
        try {
            List<Admin> admins = getAllAdmins();
            List<String> lines = new ArrayList<>();
            
            for (Admin admin : admins) {
                if (!admin.getId().equals(adminId)) {
                    lines.add(formatAdminToLine(admin));
                }
            }
            
            Files.write(Paths.get(ADMINS_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error deleting admin", e);
            return false;
        }
    }
    
    /**
     * Parse an admin from a line in the data file
     */
    private Admin parseAdminFromLine(String line) {
        String[] parts = line.split("\\|");
        
        if (parts.length >= 6) {
            Admin admin = new Admin();
            admin.setId(parts[0]);
            admin.setUsername(parts[1]);
            admin.setPassword(parts[2]);
            admin.setFullName(parts[3]);
            admin.setEmail(parts[4]);
            admin.setRole(parts[5]);
            
            return admin;
        }
        
        return null;
    }
    
    /**
     * Format an admin as a line for the data file
     */
    private String formatAdminToLine(Admin admin) {
        return String.join("|", 
                admin.getId(),
                admin.getUsername(),
                admin.getPassword(),
                admin.getFullName(),
                admin.getEmail(),
                admin.getRole());
    }
}