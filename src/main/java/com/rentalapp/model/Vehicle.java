package com.rentalapp.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a vehicle in the rental system.
 */
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String type;
    private String make;
    private String model;
    private int year;
    private String registrationNumber;
    private double dailyRate;
    private boolean available;
    private String imageUrl;
    private String description;
    
    // Default constructor
    public Vehicle() {
    }
    
    // Parameterized constructor
    public Vehicle(String id, String type, String make, String model, int year, 
                   String registrationNumber, double dailyRate, boolean available, 
                   String imageUrl, String description) {
        this.id = id;
        this.type = type;
        this.make = make;
        this.model = model;
        this.year = year;
        this.registrationNumber = registrationNumber;
        this.dailyRate = dailyRate;
        this.available = available;
        this.imageUrl = imageUrl;
        this.description = description;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public double getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return id + "," + type + "," + make + "," + model + "," + year + "," + 
               registrationNumber + "," + dailyRate + "," + available + "," + 
               imageUrl + "," + description;
    }
    
    // Used for CSV-like storage
    public static Vehicle fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid vehicle data format");
        }
        
        return new Vehicle(
            parts[0],
            parts[1],
            parts[2],
            parts[3],
            Integer.parseInt(parts[4]),
            parts[5],
            Double.parseDouble(parts[6]),
            Boolean.parseBoolean(parts[7]),
            parts[8],
            parts[9]
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
