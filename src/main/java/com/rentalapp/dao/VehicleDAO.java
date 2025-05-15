package com.rentalapp.dao;

import com.rentalapp.model.Vehicle;
import com.rentalapp.util.FileUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Vehicle entity.
 */
public class VehicleDAO {
    private static final Logger LOGGER = Logger.getLogger(VehicleDAO.class.getName());
    private static final String VEHICLES_FILE = "vehicles.txt";
    
    /**
     * Retrieves all vehicles from the data store.
     * 
     * @return a list of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        List<String> lines = FileUtil.readAllLines(VEHICLES_FILE);
        List<Vehicle> vehicles = new ArrayList<>();
        
        for (String line : lines) {
            try {
                vehicles.add(Vehicle.fromString(line));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing vehicle line: " + line, e);
            }
        }
        
        return vehicles;
    }
    
    /**
     * Retrieves a vehicle by its ID.
     * 
     * @param id the ID of the vehicle to retrieve
     * @return the vehicle with the specified ID, or null if not found
     */
    public Vehicle getVehicleById(String id) {
        List<Vehicle> vehicles = getAllVehicles();
        
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id)) {
                return vehicle;
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new vehicle to the data store.
     * 
     * @param vehicle the vehicle to add
     * @return true if successful, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null || vehicle.getId().isEmpty()) {
            return false;
        }
        
        // Check if vehicle already exists
        if (getVehicleById(vehicle.getId()) != null) {
            return false;
        }
        
        return FileUtil.appendLine(VEHICLES_FILE, vehicle.toString());
    }
    
    /**
     * Updates an existing vehicle in the data store.
     * 
     * @param vehicle the vehicle to update
     * @return true if successful, false otherwise
     */
    public boolean updateVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null || vehicle.getId().isEmpty()) {
            return false;
        }
        
        List<Vehicle> vehicles = getAllVehicles();
        boolean found = false;
        
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(vehicle.getId())) {
                vehicles.set(i, vehicle);
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        List<String> lines = vehicles.stream()
                .map(Vehicle::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(VEHICLES_FILE, lines);
    }
    
    /**
     * Deletes a vehicle from the data store.
     * 
     * @param id the ID of the vehicle to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteVehicle(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        
        List<Vehicle> vehicles = getAllVehicles();
        boolean removed = vehicles.removeIf(v -> v.getId().equals(id));
        
        if (!removed) {
            return false;
        }
        
        List<String> lines = vehicles.stream()
                .map(Vehicle::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(VEHICLES_FILE, lines);
    }
    
    /**
     * Searches for vehicles matching the specified criteria.
     * 
     * @param type the type of vehicle to search for (or null for any type)
     * @param make the make of vehicle to search for (or null for any make)
     * @param model the model of vehicle to search for (or null for any model)
     * @param availableOnly whether to include only available vehicles
     * @return a list of vehicles matching the criteria
     */
    public List<Vehicle> searchVehicles(String type, String make, String model, boolean availableOnly) {
        List<Vehicle> vehicles = getAllVehicles();
        
        return vehicles.stream()
                .filter(v -> type == null || type.isEmpty() || v.getType().equalsIgnoreCase(type))
                .filter(v -> make == null || make.isEmpty() || v.getMake().equalsIgnoreCase(make))
                .filter(v -> model == null || model.isEmpty() || v.getModel().equalsIgnoreCase(model))
                .filter(v -> !availableOnly || v.isAvailable())
                .collect(Collectors.toList());
    }
    
    /**
     * Gets a list of all available vehicles.
     * 
     * @return a list of all available vehicles
     */
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = getAllVehicles();
        
        return vehicles.stream()
                .filter(Vehicle::isAvailable)
                .collect(Collectors.toList());
    }
    
    /**
     * Sorts vehicles by availability (available first, then unavailable).
     * 
     * @param vehicles the list of vehicles to sort
     * @return the sorted list of vehicles
     */
    public List<Vehicle> sortByAvailability(List<Vehicle> vehicles) {
        List<Vehicle> sortedVehicles = new ArrayList<>(vehicles);
        
        // Using selection sort as per requirement
        for (int i = 0; i < sortedVehicles.size() - 1; i++) {
            int maxIdx = i;
            
            for (int j = i + 1; j < sortedVehicles.size(); j++) {
                // Compare availability (available = true comes before available = false)
                if (sortedVehicles.get(j).isAvailable() && !sortedVehicles.get(maxIdx).isAvailable()) {
                    maxIdx = j;
                }
            }
            
            // Swap elements
            if (maxIdx != i) {
                Vehicle temp = sortedVehicles.get(i);
                sortedVehicles.set(i, sortedVehicles.get(maxIdx));
                sortedVehicles.set(maxIdx, temp);
            }
        }
        
        return sortedVehicles;
    }
    
    /**
     * Sorts vehicles by daily rate (ascending or descending).
     * 
     * @param vehicles the list of vehicles to sort
     * @param ascending true for ascending order, false for descending
     * @return the sorted list of vehicles
     */
    public List<Vehicle> sortByDailyRate(List<Vehicle> vehicles, boolean ascending) {
        List<Vehicle> sortedVehicles = new ArrayList<>(vehicles);
        
        // Using selection sort as per requirement
        for (int i = 0; i < sortedVehicles.size() - 1; i++) {
            int extremeIdx = i;
            
            for (int j = i + 1; j < sortedVehicles.size(); j++) {
                // Compare daily rates
                boolean shouldSwap = ascending ? 
                    sortedVehicles.get(j).getDailyRate() < sortedVehicles.get(extremeIdx).getDailyRate() :
                    sortedVehicles.get(j).getDailyRate() > sortedVehicles.get(extremeIdx).getDailyRate();
                
                if (shouldSwap) {
                    extremeIdx = j;
                }
            }
            
            // Swap elements
            if (extremeIdx != i) {
                Vehicle temp = sortedVehicles.get(i);
                sortedVehicles.set(i, sortedVehicles.get(extremeIdx));
                sortedVehicles.set(extremeIdx, temp);
            }
        }
        
        return sortedVehicles;
    }
    
    /**
     * Gets a list of unique vehicle types.
     * 
     * @return a list of unique vehicle types
     */
    public List<String> getVehicleTypes() {
        List<Vehicle> vehicles = getAllVehicles();
        
        return vehicles.stream()
                .map(Vehicle::getType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Gets a list of unique vehicle makes.
     * 
     * @return a list of unique vehicle makes
     */
    public List<String> getVehicleMakes() {
        List<Vehicle> vehicles = getAllVehicles();
        
        return vehicles.stream()
                .map(Vehicle::getMake)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
