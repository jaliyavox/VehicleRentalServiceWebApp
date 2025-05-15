package com.rentalapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a booking in the rental system.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String userId;
    private String vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;
    private String status;  // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private LocalDate bookingDate;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Default constructor
    public Booking() {
        this.bookingDate = LocalDate.now();
        this.status = "PENDING";  // Default status
    }
    
    // Parameterized constructor
    public Booking(String id, String userId, String vehicleId, LocalDate startDate, 
                   LocalDate endDate, double totalCost, String status, LocalDate bookingDate) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = status;
        this.bookingDate = bookingDate;
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
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    // Calculate the rental duration in days
    public long getRentalDuration() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1; // inclusive of start and end days
    }
    
    // Calculate total cost based on daily rate and duration
    public void calculateTotalCost(double dailyRate) {
        this.totalCost = dailyRate * getRentalDuration();
    }
    
    @Override
    public String toString() {
        return id + "," + userId + "," + vehicleId + "," + 
               startDate.format(DATE_FORMATTER) + "," + 
               endDate.format(DATE_FORMATTER) + "," + 
               totalCost + "," + status + "," + 
               bookingDate.format(DATE_FORMATTER);
    }
    
    // Used for CSV-like storage
    public static Booking fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid booking data format");
        }
        
        return new Booking(
            parts[0],
            parts[1],
            parts[2],
            LocalDate.parse(parts[3], DATE_FORMATTER),
            LocalDate.parse(parts[4], DATE_FORMATTER),
            Double.parseDouble(parts[5]),
            parts[6],
            LocalDate.parse(parts[7], DATE_FORMATTER)
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
