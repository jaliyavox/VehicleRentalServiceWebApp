package com.rentalapp.dao;

import com.rentalapp.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Access Object for Vehicle operations
 */
public class VehicleDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(VehicleDAO.class);
    private static final String VEHICLES_FILE_PATH = "src/main/resources/data/vehicles.txt";
    
    /**
     * Default constructor
     */
    public VehicleDAO() {
        // Initialize if needed
    }
    
    /**
     * Get all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(VEHICLES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Vehicle vehicle = parseVehicleFromLine(line);
                    if (vehicle != null) {
                        vehicles.add(vehicle);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading vehicles file", e);
        }
        
        return vehicles;
    }
    
    /**
     * Get vehicle by ID
     */
    public Vehicle getById(String id) {
        try {
            List<Vehicle> vehicles = getAllVehicles();
            
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getId().equals(id)) {
                    return vehicle;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting vehicle by ID", e);
        }
        
        return null;
    }
    
    /**
     * Get vehicle by ID (alias for getById for backward compatibility)
     */
    public Vehicle getVehicleById(String id) {
        return getById(id);
    }
    
    /**
     * Get available vehicles
     */
    public List<Vehicle> getAvailableVehicles() {
        return getAllVehicles().stream()
                .filter(v -> "available".equalsIgnoreCase(v.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get vehicles by type
     */
    public List<Vehicle> getVehiclesByType(String type) {
        return getAllVehicles().stream()
                .filter(v -> type.equalsIgnoreCase(v.getType()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get vehicles sorted by price (ascending)
     */
    public List<Vehicle> getVehiclesSortedByPrice() {
        List<Vehicle> vehicles = getAllVehicles();
        selectionSortByPrice(vehicles);
        return vehicles;
    }
    
    /**
     * Get vehicles sorted by availability then price
     */
    public List<Vehicle> getVehiclesSortedByAvailabilityAndPrice() {
        List<Vehicle> vehicles = getAllVehicles();
        selectionSortByAvailabilityAndPrice(vehicles);
        return vehicles;
    }
    
    /**
     * Sort vehicles by daily rate
     */
    public List<Vehicle> sortByDailyRate(List<Vehicle> vehicles, boolean ascending) {
        List<Vehicle> sortedVehicles = new ArrayList<>(vehicles);
        
        if (ascending) {
            selectionSortByPrice(sortedVehicles);
        } else {
            // Sort by price descending
            selectionSortByPrice(sortedVehicles);
            java.util.Collections.reverse(sortedVehicles);
        }
        
        return sortedVehicles;
    }
    
    /**
     * Sort vehicles by availability
     */
    public List<Vehicle> sortByAvailability(List<Vehicle> vehicles) {
        List<Vehicle> sortedVehicles = new ArrayList<>(vehicles);
        selectionSortByAvailabilityAndPrice(sortedVehicles);
        return sortedVehicles;
    }
    
    /**
     * Search vehicles by criteria
     */
    public List<Vehicle> searchVehicles(String type, String make, String priceRange, boolean availableOnly) {
        List<Vehicle> allVehicles = getAllVehicles();
        List<Vehicle> filteredVehicles = new ArrayList<>();
        
        for (Vehicle vehicle : allVehicles) {
            boolean typeMatch = type == null || type.isEmpty() || vehicle.getType().equalsIgnoreCase(type);
            boolean makeMatch = make == null || make.isEmpty() || vehicle.getMake().equalsIgnoreCase(make);
            boolean availabilityMatch = !availableOnly || "available".equalsIgnoreCase(vehicle.getStatus());
            
            // Parse price range if provided (format: "min-max" or "min-" or "-max")
            boolean priceMatch = true;
            if (priceRange != null && !priceRange.isEmpty()) {
                String[] parts = priceRange.split("-");
                try {
                    if (parts.length == 2) {
                        if (!parts[0].isEmpty()) {
                            BigDecimal minPrice = new BigDecimal(parts[0]);
                            if (vehicle.getDailyRate().compareTo(minPrice) < 0) {
                                priceMatch = false;
                            }
                        }
                        
                        if (!parts[1].isEmpty()) {
                            BigDecimal maxPrice = new BigDecimal(parts[1]);
                            if (vehicle.getDailyRate().compareTo(maxPrice) > 0) {
                                priceMatch = false;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.error("Error parsing price range: " + priceRange, e);
                }
            }
            
            if (typeMatch && makeMatch && availabilityMatch && priceMatch) {
                filteredVehicles.add(vehicle);
            }
        }
        
        return filteredVehicles;
    }
    
    /**
     * Get all vehicle types
     */
    public List<String> getVehicleTypes() {
        List<String> types = new ArrayList<>();
        
        for (Vehicle vehicle : getAllVehicles()) {
            if (!types.contains(vehicle.getType())) {
                types.add(vehicle.getType());
            }
        }
        
        java.util.Collections.sort(types);
        return types;
    }
    
    /**
     * Get all vehicle makes
     */
    public List<String> getVehicleMakes() {
        List<String> makes = new ArrayList<>();
        
        for (Vehicle vehicle : getAllVehicles()) {
            if (!makes.contains(vehicle.getMake())) {
                makes.add(vehicle.getMake());
            }
        }
        
        java.util.Collections.sort(makes);
        return makes;
    }
    
    /**
     * Add a new vehicle
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().trim().isEmpty()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        
        try {
            String vehicleRecord = formatVehicleToLine(vehicle);
            Path path = Paths.get(VEHICLES_FILE_PATH);
            Files.write(path, (vehicleRecord + System.lineSeparator()).getBytes(), 
                    Files.exists(path) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
            
            return true;
        } catch (IOException e) {
            logger.error("Error adding vehicle", e);
            return false;
        }
    }
    
    /**
     * Update an existing vehicle
     */
    public boolean updateVehicle(Vehicle vehicle) {
        try {
            List<Vehicle> vehicles = getAllVehicles();
            List<String> lines = new ArrayList<>();
            
            for (Vehicle existingVehicle : vehicles) {
                if (existingVehicle.getId().equals(vehicle.getId())) {
                    lines.add(formatVehicleToLine(vehicle));
                } else {
                    lines.add(formatVehicleToLine(existingVehicle));
                }
            }
            
            Files.write(Paths.get(VEHICLES_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error updating vehicle", e);
            return false;
        }
    }
    
    /**
     * Delete a vehicle by ID
     */
    public boolean deleteVehicle(String vehicleId) {
        try {
            List<Vehicle> vehicles = getAllVehicles();
            List<String> lines = new ArrayList<>();
            
            for (Vehicle vehicle : vehicles) {
                if (!vehicle.getId().equals(vehicleId)) {
                    lines.add(formatVehicleToLine(vehicle));
                }
            }
            
            Files.write(Paths.get(VEHICLES_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error deleting vehicle", e);
            return false;
        }
    }
    
    /**
     * Selection sort implementation for sorting vehicles by price
     */
    private void selectionSortByPrice(List<Vehicle> vehicles) {
        int n = vehicles.size();
        
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            
            for (int j = i + 1; j < n; j++) {
                if (vehicles.get(j).getDailyRate().compareTo(vehicles.get(minIdx).getDailyRate()) < 0) {
                    minIdx = j;
                }
            }
            
            // Swap the found minimum element with the first element
            Vehicle temp = vehicles.get(minIdx);
            vehicles.set(minIdx, vehicles.get(i));
            vehicles.set(i, temp);
        }
    }
    
    /**
     * Selection sort implementation for sorting vehicles by availability and then price
     */
    private void selectionSortByAvailabilityAndPrice(List<Vehicle> vehicles) {
        int n = vehicles.size();
        
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            
            for (int j = i + 1; j < n; j++) {
                // First, compare by availability (available vehicles first)
                boolean jIsAvailable = "available".equalsIgnoreCase(vehicles.get(j).getStatus());
                boolean minIdxIsAvailable = "available".equalsIgnoreCase(vehicles.get(minIdx).getStatus());
                
                if (jIsAvailable && !minIdxIsAvailable) {
                    minIdx = j;
                } else if (jIsAvailable == minIdxIsAvailable) {
                    // If availability is the same, compare by price
                    if (vehicles.get(j).getDailyRate().compareTo(vehicles.get(minIdx).getDailyRate()) < 0) {
                        minIdx = j;
                    }
                }
            }
            
            // Swap the found minimum element with the first element
            Vehicle temp = vehicles.get(minIdx);
            vehicles.set(minIdx, vehicles.get(i));
            vehicles.set(i, temp);
        }
    }
    
    /**
     * Parse a vehicle from a line in the data file
     */
    private Vehicle parseVehicleFromLine(String line) {
        String[] parts = line.split("\\|");
        
        if (parts.length >= 8) {
            Vehicle vehicle = new Vehicle();
            
            try {
                vehicle.setId(parts[0]);
                vehicle.setName(parts[1]);
                vehicle.setType(parts[2]);
                vehicle.setMake(parts[3]);
                vehicle.setModel(parts[4]);
                vehicle.setYear(Integer.parseInt(parts[5]));
                vehicle.setDailyRate(new BigDecimal(parts[6]));
                vehicle.setStatus(parts[7]);
                
                if (parts.length > 8) vehicle.setColor(parts[8]);
                if (parts.length > 9) vehicle.setLicensePlate(parts[9]);
                if (parts.length > 10) vehicle.setImageUrl(parts[10]);
                if (parts.length > 11) vehicle.setDescription(parts[11]);
                if (parts.length > 12 && !parts[12].isEmpty()) {
                    List<String> features = Arrays.asList(parts[12].split(";"));
                    vehicle.setFeatures(features);
                }
                if (parts.length > 13) vehicle.setSeatingCapacity(Integer.parseInt(parts[13]));
                if (parts.length > 14) vehicle.setFuelType(parts[14]);
                if (parts.length > 15) vehicle.setTransmission(parts[15]);
                if (parts.length > 16) vehicle.setAvgRating(Double.parseDouble(parts[16]));
                if (parts.length > 17) vehicle.setReviewCount(Integer.parseInt(parts[17]));
                
                return vehicle;
            } catch (NumberFormatException e) {
                logger.error("Error parsing vehicle data: " + line, e);
            }
        }
        
        return null;
    }
    
    /**
     * Format a vehicle as a line for the data file
     */
    private String formatVehicleToLine(Vehicle vehicle) {
        StringBuilder sb = new StringBuilder();
        
        // Essential fields
        sb.append(vehicle.getId()).append("|")
          .append(vehicle.getName()).append("|")
          .append(vehicle.getType()).append("|")
          .append(vehicle.getMake()).append("|")
          .append(vehicle.getModel()).append("|")
          .append(vehicle.getYear()).append("|")
          .append(vehicle.getDailyRate()).append("|")
          .append(vehicle.getStatus());
        
        // Optional fields
        sb.append("|").append(vehicle.getColor() != null ? vehicle.getColor() : "");
        sb.append("|").append(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "");
        sb.append("|").append(vehicle.getImageUrl() != null ? vehicle.getImageUrl() : "");
        sb.append("|").append(vehicle.getDescription() != null ? vehicle.getDescription() : "");
        
        // Features
        sb.append("|");
        if (vehicle.getFeatures() != null && !vehicle.getFeatures().isEmpty()) {
            sb.append(String.join(";", vehicle.getFeatures()));
        }
        
        // More optional fields
        sb.append("|").append(vehicle.getSeatingCapacity());
        sb.append("|").append(vehicle.getFuelType() != null ? vehicle.getFuelType() : "");
        sb.append("|").append(vehicle.getTransmission() != null ? vehicle.getTransmission() : "");
        sb.append("|").append(vehicle.getAvgRating());
        sb.append("|").append(vehicle.getReviewCount());
        
        return sb.toString();
    }
}