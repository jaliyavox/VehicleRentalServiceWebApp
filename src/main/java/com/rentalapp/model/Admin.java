package com.rentalapp.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents an administrator in the rental system.
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
    private String permissions; // comma-separated list of permissions
    
    // Default constructor
    public Admin() {
        this.role = "ADMIN"; // Default role
    }
    
    // Parameterized constructor
    public Admin(String id, String username, String password, String fullName, 
                 String email, String role, String permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.permissions = permissions;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getPermissions() {
        return permissions;
    }
    
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
    
    // Authentication method
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    // Check if admin has a specific permission
    public boolean hasPermission(String permission) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        
        List<String> permissionList = Arrays.asList(permissions.split(","));
        return permissionList.contains(permission) || permissionList.contains("ALL");
    }
    
    @Override
    public String toString() {
        return id + "," + username + "," + password + "," + fullName + "," + 
               email + "," + role + "," + permissions;
    }
    
    // Used for CSV-like storage
    public static Admin fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid admin data format");
        }
        
        return new Admin(
            parts[0],
            parts[1],
            parts[2],
            parts[3],
            parts[4],
            parts[5],
            parts[6]
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(id, admin.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
