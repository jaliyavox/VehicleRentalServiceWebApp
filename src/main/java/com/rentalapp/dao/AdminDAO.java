package com.rentalapp.dao;

import com.rentalapp.model.Admin;
import com.rentalapp.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Admin entity.
 */
public class AdminDAO {
    private static final Logger LOGGER = Logger.getLogger(AdminDAO.class.getName());
    private static final String ADMINS_FILE = "admins.txt";
    
    /**
     * Retrieves all admins from the data store.
     * 
     * @return a list of all admins
     */
    public List<Admin> getAllAdmins() {
        List<String> lines = FileUtil.readAllLines(ADMINS_FILE);
        List<Admin> admins = new ArrayList<>();
        
        for (String line : lines) {
            try {
                admins.add(Admin.fromString(line));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing admin line: " + line, e);
            }
        }
        
        return admins;
    }
    
    /**
     * Retrieves an admin by ID.
     * 
     * @param id the ID of the admin to retrieve
     * @return the admin with the specified ID, or null if not found
     */
    public Admin getAdminById(String id) {
        List<Admin> admins = getAllAdmins();
        
        for (Admin admin : admins) {
            if (admin.getId().equals(id)) {
                return admin;
            }
        }
        
        return null;
    }
    
    /**
     * Retrieves an admin by username.
     * 
     * @param username the username of the admin to retrieve
     * @return the admin with the specified username, or null if not found
     */
    public Admin getAdminByUsername(String username) {
        List<Admin> admins = getAllAdmins();
        
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new admin to the data store.
     * 
     * @param admin the admin to add
     * @return true if successful, false otherwise
     */
    public boolean addAdmin(Admin admin) {
        if (admin == null || admin.getId() == null || admin.getId().isEmpty()) {
            return false;
        }
        
        // Check if admin already exists
        if (getAdminById(admin.getId()) != null || getAdminByUsername(admin.getUsername()) != null) {
            return false;
        }
        
        return FileUtil.appendLine(ADMINS_FILE, admin.toString());
    }
    
    /**
     * Updates an existing admin in the data store.
     * 
     * @param admin the admin to update
     * @return true if successful, false otherwise
     */
    public boolean updateAdmin(Admin admin) {
        if (admin == null || admin.getId() == null || admin.getId().isEmpty()) {
            return false;
        }
        
        List<Admin> admins = getAllAdmins();
        boolean found = false;
        
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getId().equals(admin.getId())) {
                // Check if trying to change username to one that already exists for another admin
                if (!admins.get(i).getUsername().equals(admin.getUsername())) {
                    Admin existingAdmin = getAdminByUsername(admin.getUsername());
                    if (existingAdmin != null && !existingAdmin.getId().equals(admin.getId())) {
                        return false;
                    }
                }
                
                admins.set(i, admin);
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        List<String> lines = admins.stream()
                .map(Admin::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(ADMINS_FILE, lines);
    }
    
    /**
     * Deletes an admin from the data store.
     * 
     * @param id the ID of the admin to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteAdmin(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        
        List<Admin> admins = getAllAdmins();
        boolean removed = admins.removeIf(a -> a.getId().equals(id));
        
        if (!removed) {
            return false;
        }
        
        List<String> lines = admins.stream()
                .map(Admin::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(ADMINS_FILE, lines);
    }
    
    /**
     * Authenticates an admin.
     * 
     * @param username the username of the admin to authenticate
     * @param password the password to check
     * @return the authenticated admin, or null if authentication failed
     */
    public Admin authenticateAdmin(String username, String password) {
        Admin admin = getAdminByUsername(username);
        
        if (admin != null && admin.authenticate(password)) {
            return admin;
        }
        
        return null;
    }
    
    /**
     * Checks if an admin has a specific permission.
     * 
     * @param adminId the ID of the admin to check
     * @param permission the permission to check for
     * @return true if the admin has the permission, false otherwise
     */
    public boolean hasPermission(String adminId, String permission) {
        Admin admin = getAdminById(adminId);
        
        if (admin == null) {
            return false;
        }
        
        return admin.hasPermission(permission);
    }
    
    /**
     * Creates a default admin if no admins exist.
     * 
     * @return true if a default admin was created, false otherwise
     */
    public boolean createDefaultAdminIfNeeded() {
        List<Admin> admins = getAllAdmins();
        
        if (admins.isEmpty()) {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setId(FileUtil.generateUniqueId());
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword("admin123"); // Should be changed after first login in a real system
            defaultAdmin.setFullName("System Administrator");
            defaultAdmin.setEmail("admin@example.com");
            defaultAdmin.setRole("ADMIN");
            defaultAdmin.setPermissions("ALL");
            
            return addAdmin(defaultAdmin);
        }
        
        return false;
    }
}
