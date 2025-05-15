package com.rentalapp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a payment in the rental system.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String bookingId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;  // CASH, CREDIT_CARD, BANK_TRANSFER, etc.
    private String status;  // PENDING, APPROVED, REJECTED
    private String transactionId;
    private String paymentSlipPath;  // Path to the uploaded payment slip file
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Default constructor
    public Payment() {
        this.paymentDate = LocalDate.now();
        this.status = "PENDING";  // Default status
        this.amount = BigDecimal.ZERO;
    }
    
    // Parameterized constructor
    public Payment(String id, String bookingId, BigDecimal amount, LocalDate paymentDate, 
                  String paymentMethod, String status, String transactionId, String paymentSlipPath) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.paymentSlipPath = paymentSlipPath;
    }
    
    // Legacy constructor for backward compatibility
    public Payment(String id, String bookingId, double amount, LocalDate paymentDate, 
                  String paymentMethod, String status, String transactionId, String paymentSlipPath) {
        this(id, bookingId, new BigDecimal(String.valueOf(amount)), paymentDate, 
             paymentMethod, status, transactionId, paymentSlipPath);
    }
    
    // Getters and setters
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    // Legacy method for backward compatibility
    public void setAmount(double amount) {
        this.amount = new BigDecimal(String.valueOf(amount));
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentSlipPath() {
        return paymentSlipPath;
    }
    
    public void setPaymentSlipPath(String paymentSlipPath) {
        this.paymentSlipPath = paymentSlipPath;
    }
    
    // Check if payment is approved
    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }
    
    // Check if payment is rejected
    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(status);
    }
    
    // Check if payment is pending
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return id + "," + bookingId + "," + amount + "," + 
               paymentDate.format(DATE_FORMATTER) + "," + 
               paymentMethod + "," + status + "," + 
               transactionId + "," + paymentSlipPath;
    }
    
    // Used for CSV-like storage
    public static Payment fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid payment data format");
        }
        
        return new Payment(
            parts[0],
            parts[1],
            new BigDecimal(parts[2]),
            LocalDate.parse(parts[3], DATE_FORMATTER),
            parts[4],
            parts[5],
            parts[6],
            parts[7]
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
