package com.rentalapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class representing a payment in the vehicle rental system.
 */
public class Payment {
    
    private String id;
    private String bookingId;
    private String userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String slipImagePath;
    private LocalDateTime paymentDate;
    private String status; // pending, approved, rejected
    private String notes;
    private String adminId; // ID of the admin who processed the payment
    private LocalDateTime processedDate;
    
    /**
     * Default constructor
     */
    public Payment() {
        // Default constructor needed for object creation
    }
    
    /**
     * Constructor with essential fields
     */
    public Payment(String id, String bookingId, String userId, BigDecimal amount, 
                  String paymentMethod, String slipImagePath, LocalDateTime paymentDate, 
                  String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.slipImagePath = slipImagePath;
        this.paymentDate = paymentDate;
        this.status = status;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getSlipImagePath() {
        return slipImagePath;
    }
    
    public void setSlipImagePath(String slipImagePath) {
        this.slipImagePath = slipImagePath;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getAdminId() {
        return adminId;
    }
    
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    
    public LocalDateTime getProcessedDate() {
        return processedDate;
    }
    
    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }
    
    /**
     * Check if the payment is pending
     */
    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the payment is approved
     */
    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the payment is rejected
     */
    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}