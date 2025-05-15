package com.rentalapp.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a vehicle in the rental system
 */
public class Vehicle {
    private String id;
    private String name;
    private String type; // car, motorcycle, bicycle, etc.
    private String make;
    private String model;
    private int year;
    private String color;
    private String licensePlate;
    private BigDecimal dailyRate;
    private String status; // available, rented, maintenance
    private String imageUrl;
    private String description;
    private List<String> features;
    private int seatingCapacity;
    private String fuelType;
    private String transmission;
    private double avgRating;
    private int reviewCount;
    
    /**
     * Default constructor
     */
    public Vehicle() {
        this.features = new ArrayList<>();
    }
    
    /**
     * Constructor with essential fields
     */
    public Vehicle(String id, String name, String type, String make, String model, int year, 
                  BigDecimal dailyRate, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.make = make;
        this.model = model;
        this.year = year;
        this.dailyRate = dailyRate;
        this.status = status;
        this.features = new ArrayList<>();
    }
    
    /**
     * Full constructor
     */
    public Vehicle(String id, String name, String type, String make, String model, int year,
                  String color, String licensePlate, BigDecimal dailyRate, String status,
                  String imageUrl, String description, List<String> features, int seatingCapacity,
                  String fuelType, String transmission) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.licensePlate = licensePlate;
        this.dailyRate = dailyRate;
        this.status = status;
        this.imageUrl = imageUrl;
        this.description = description;
        this.features = features != null ? features : new ArrayList<>();
        this.seatingCapacity = seatingCapacity;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.avgRating = 0.0;
        this.reviewCount = 0;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public BigDecimal getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    public void addFeature(String feature) {
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        this.features.add(feature);
    }
    
    public int getSeatingCapacity() {
        return seatingCapacity;
    }
    
    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public String getTransmission() {
        return transmission;
    }
    
    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }
    
    public double getAvgRating() {
        return avgRating;
    }
    
    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
    
    public int getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
    
    /**
     * Updates the average rating when a new review is added
     */
    public void updateRating(int newRating) {
        double totalRating = (avgRating * reviewCount) + newRating;
        reviewCount++;
        avgRating = totalRating / reviewCount;
    }
    
    /**
     * Set vehicle availability status
     * @param available if true, set status to "available"
     */
    public void setAvailable(boolean available) {
        this.status = available ? "available" : "rented";
    }
    
    /**
     * Check if the vehicle is available
     * @return true if status is "available"
     */
    public boolean isAvailable() {
        return "available".equalsIgnoreCase(this.status);
    }
    
    /**
     * Set registration number (alias for license plate for backward compatibility)
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.licensePlate = registrationNumber;
    }
    
    /**
     * Get registration number (alias for license plate for backward compatibility)
     */
    public String getRegistrationNumber() {
        return this.licensePlate;
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", dailyRate=" + dailyRate +
                ", status='" + status + '\'' +
                '}';
    }
}