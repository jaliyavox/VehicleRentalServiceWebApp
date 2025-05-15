package com.rentalapp.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for validation.
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[0-9]{10,15}$");
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Validates an email address.
     * 
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates a phone number.
     * 
     * @param phone the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validates a date string.
     * 
     * @param dateStr the date string to validate
     * @return true if the date is valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validates a number string.
     * 
     * @param numStr the number string to validate
     * @return true if the string is a valid number, false otherwise
     */
    public static boolean isValidNumber(String numStr) {
        if (numStr == null || numStr.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(numStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates a date range.
     * 
     * @param startDateStr the start date string
     * @param endDateStr the end date string
     * @return true if the date range is valid, false otherwise
     */
    public static boolean isValidDateRange(String startDateStr, String endDateStr) {
        if (!isValidDate(startDateStr) || !isValidDate(endDateStr)) {
            return false;
        }
        
        LocalDate startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
        LocalDate endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
        
        // End date must be after or equal to start date
        return !endDate.isBefore(startDate);
    }
    
    /**
     * Validates that a date is in the future.
     * 
     * @param dateStr the date string to validate
     * @return true if the date is in the future, false otherwise
     */
    public static boolean isFutureDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return false;
        }
        
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        return date.isAfter(LocalDate.now());
    }
    
    /**
     * Sanitizes a string to prevent XSS.
     * 
     * @param input the input string to sanitize
     * @return the sanitized string
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        
        // Replace HTML special characters with their entity equivalents
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * Validates a username.
     * 
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        // Username should be alphanumeric and between 4 and 20 characters
        return username.matches("^[a-zA-Z0-9]{4,20}$");
    }
    
    /**
     * Validates a password.
     * 
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Password should be at least 8 characters long and contain at least one letter and one number
        return password.length() >= 8 && 
               password.matches(".*[a-zA-Z].*") && 
               password.matches(".*[0-9].*");
    }
    
    /**
     * Validates a rating.
     * 
     * @param rating the rating to validate
     * @return true if the rating is valid, false otherwise
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}
