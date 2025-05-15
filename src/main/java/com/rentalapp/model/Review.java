package com.rentalapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a review in the rental system.
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String userId;
    private String vehicleId;
    private int rating;  // 1-5 stars
    private String comment;
    private LocalDate reviewDate;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Default constructor
    public Review() {
        this.reviewDate = LocalDate.now();
    }
    
    // Parameterized constructor
    public Review(String id, String userId, String vehicleId, int rating, 
                 String comment, LocalDate reviewDate) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        // Validate rating is between 1 and 5
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDate getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    @Override
    public String toString() {
        return id + "," + userId + "," + vehicleId + "," + 
               rating + "," + comment + "," + 
               reviewDate.format(DATE_FORMATTER);
    }
    
    // Used for CSV-like storage
    public static Review fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid review data format");
        }
        
        return new Review(
            parts[0],
            parts[1],
            parts[2],
            Integer.parseInt(parts[3]),
            parts[4],
            LocalDate.parse(parts[5], DATE_FORMATTER)
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
