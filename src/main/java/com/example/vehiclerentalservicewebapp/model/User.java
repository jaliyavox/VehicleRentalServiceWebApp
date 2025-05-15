package com.example.vehiclerentalservicewebapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * User class represents a user in the vehicle rental system.
 * It stores personal information and credentials for authentication.
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDateTime registrationDate;
    private boolean isAdmin;

    // Default constructor
    public User() {
        this.registrationDate = LocalDateTime.now();
        this.isAdmin = false;
    }

    /**
     * Constructor with all fields
     *
     * @param id Unique identifier for the user
     * @param username Username chosen by the user
     * @param password Hashed password for security
     * @param email User's email address
     * @param fullName User's full name
     * @param phoneNumber User's contact number
     * @param registrationDate Date when the user registered
     * @param isAdmin Flag indicating if user has administrative privileges
     */
    public User(String id, String username, String password, String email, String fullName,
                String phoneNumber, LocalDateTime registrationDate, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.isAdmin = isAdmin;
    }

    /**
     * Constructor with essential fields, generates current timestamp for registrationDate
     *
     * @param id Unique identifier for the user
     * @param username Username chosen by the user
     * @param password Hashed password for security
     * @param email User's email address
     * @param fullName User's full name
     * @param phoneNumber User's contact number
     */
    public User(String id, String username, String password, String email, String fullName, String phoneNumber) {
        this(id, username, password, email, fullName, phoneNumber, LocalDateTime.now(), false);
    }

    // Getters and Setters

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Convert the user object to a string representation for storage in a text file
     * Uses pipe character as delimiter for easy parsing
     *
     * @return String representation of the user
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.join("|",
                id,
                username,
                password,
                email,
                fullName,
                phoneNumber,
                registrationDate.format(formatter),
                String.valueOf(isAdmin)
        );
    }

    /**
     * Create a User object from a string representation (read from file)
     *
     * @param fileString The string representation of a user from the text file
     * @return A new User object
     */
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid user data format");
        }

        User user = new User();
        user.setId(parts[0]);
        user.setUsername(parts[1]);
        user.setPassword(parts[2]);
        user.setEmail(parts[3]);
        user.setFullName(parts[4]);
        user.setPhoneNumber(parts[5]);
        user.setRegistrationDate(LocalDateTime.parse(parts[6], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        user.setAdmin(Boolean.parseBoolean(parts[7]));

        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationDate=" + registrationDate +
                ", isAdmin=" + isAdmin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isAdmin == user.isAdmin &&
                Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(fullName, user.fullName) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(registrationDate, user.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, fullName, phoneNumber, registrationDate, isAdmin);
    }
}