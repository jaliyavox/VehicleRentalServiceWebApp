package com.rentalapp.model;

import java.util.Date;

/**
 * Represents a vehicle review in the system
 */
public class Review {
    private String id;
    private String userId;
    private String userName;
    private String vehicleId;
    private String vehicleName;
    private String bookingId;
    private int rating;
    private String comment;
    private Date reviewDate;
    private boolean verified;
    
    /**
     * Default constructor
     */
    public Review() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Review(String id, String userId, String vehicleId, int rating, String comment, Date reviewDate) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.verified = false;
    }
    
    /**
     * Full constructor
     */
    public Review(String id, String userId, String userName, String vehicleId, String vehicleName, 
                  String bookingId, int rating, String comment, Date reviewDate, boolean verified) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.verified = verified;
    }
    
    // Getters and Setters
    
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getVehicleName() {
        return vehicleName;
    }
    
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
    
    public String getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Date getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                ", verified=" + verified +
                '}';
    }
}