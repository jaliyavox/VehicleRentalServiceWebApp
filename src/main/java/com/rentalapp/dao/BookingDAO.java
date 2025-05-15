package com.rentalapp.dao;

import com.rentalapp.model.Booking;
import com.rentalapp.model.Vehicle;
import com.rentalapp.util.FileUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Booking entity.
 */
public class BookingDAO {
    private static final Logger LOGGER = Logger.getLogger(BookingDAO.class.getName());
    private static final String BOOKINGS_FILE = "bookings.txt";
    
    /**
     * Retrieves all bookings from the data store.
     * 
     * @return a list of all bookings
     */
    public List<Booking> getAllBookings() {
        List<String> lines = FileUtil.readAllLines(BOOKINGS_FILE);
        List<Booking> bookings = new ArrayList<>();
        
        for (String line : lines) {
            try {
                bookings.add(Booking.fromString(line));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing booking line: " + line, e);
            }
        }
        
        return bookings;
    }
    
    /**
     * Retrieves a booking by ID.
     * 
     * @param id the ID of the booking to retrieve
     * @return the booking with the specified ID, or null if not found
     */
    public Booking getBookingById(String id) {
        List<Booking> bookings = getAllBookings();
        
        for (Booking booking : bookings) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new booking to the data store.
     * 
     * @param booking the booking to add
     * @return true if successful, false otherwise
     */
    public boolean addBooking(Booking booking) {
        if (booking == null || booking.getId() == null || booking.getId().isEmpty()) {
            return false;
        }
        
        // Check if booking already exists
        if (getBookingById(booking.getId()) != null) {
            return false;
        }
        
        // Set booking date if not already set
        if (booking.getBookingDate() == null) {
            booking.setBookingDate(LocalDate.now());
        }
        
        return FileUtil.appendLine(BOOKINGS_FILE, booking.toString());
    }
    
    /**
     * Updates an existing booking in the data store.
     * 
     * @param booking the booking to update
     * @return true if successful, false otherwise
     */
    public boolean updateBooking(Booking booking) {
        if (booking == null || booking.getId() == null || booking.getId().isEmpty()) {
            return false;
        }
        
        List<Booking> bookings = getAllBookings();
        boolean found = false;
        
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId().equals(booking.getId())) {
                bookings.set(i, booking);
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        List<String> lines = bookings.stream()
                .map(Booking::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(BOOKINGS_FILE, lines);
    }
    
    /**
     * Deletes a booking from the data store.
     * 
     * @param id the ID of the booking to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBooking(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        
        List<Booking> bookings = getAllBookings();
        boolean removed = bookings.removeIf(b -> b.getId().equals(id));
        
        if (!removed) {
            return false;
        }
        
        List<String> lines = bookings.stream()
                .map(Booking::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(BOOKINGS_FILE, lines);
    }
    
    /**
     * Gets all bookings for a user.
     * 
     * @param userId the ID of the user
     * @return a list of bookings for the user
     */
    public List<Booking> getBookingsByUser(String userId) {
        List<Booking> bookings = getAllBookings();
        
        return bookings.stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all bookings for a vehicle.
     * 
     * @param vehicleId the ID of the vehicle
     * @return a list of bookings for the vehicle
     */
    public List<Booking> getBookingsByVehicle(String vehicleId) {
        List<Booking> bookings = getAllBookings();
        
        return bookings.stream()
                .filter(b -> b.getVehicleId().equals(vehicleId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all bookings with a specific status.
     * 
     * @param status the status to filter by
     * @return a list of bookings with the specified status
     */
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = getAllBookings();
        
        return bookings.stream()
                .filter(b -> b.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    
    /**
     * Checks if a vehicle is available for a given date range.
     * 
     * @param vehicleId the ID of the vehicle to check
     * @param startDate the start date of the period to check
     * @param endDate the end date of the period to check
     * @param excludeBookingId booking ID to exclude from the check (for updates)
     * @return true if the vehicle is available, false otherwise
     */
    public boolean isVehicleAvailable(String vehicleId, LocalDate startDate, LocalDate endDate, String excludeBookingId) {
        List<Booking> vehicleBookings = getBookingsByVehicle(vehicleId);
        
        for (Booking booking : vehicleBookings) {
            // Skip the booking being updated
            if (excludeBookingId != null && booking.getId().equals(excludeBookingId)) {
                continue;
            }
            
            // Skip cancelled bookings
            if (booking.getStatus().equalsIgnoreCase("CANCELLED")) {
                continue;
            }
            
            // Check for overlap
            if (!(endDate.isBefore(booking.getStartDate()) || startDate.isAfter(booking.getEndDate()))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets a linked list of rentals for a vehicle.
     * 
     * @param vehicleId the ID of the vehicle
     * @return a linked list of bookings for the vehicle
     */
    public LinkedList<Booking> getVehicleRentalHistory(String vehicleId) {
        List<Booking> vehicleBookings = getBookingsByVehicle(vehicleId);
        
        // Sort by start date
        vehicleBookings.sort((b1, b2) -> b1.getStartDate().compareTo(b2.getStartDate()));
        
        // Convert to linked list
        LinkedList<Booking> rentalHistory = new LinkedList<>(vehicleBookings);
        
        return rentalHistory;
    }
    
    /**
     * Cancels a booking.
     * 
     * @param id the ID of the booking to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelBooking(String id) {
        Booking booking = getBookingById(id);
        
        if (booking == null) {
            return false;
        }
        
        booking.setStatus("CANCELLED");
        
        return updateBooking(booking);
    }
    
    /**
     * Gets all active bookings (not cancelled or completed).
     * 
     * @return a list of active bookings
     */
    public List<Booking> getActiveBookings() {
        List<Booking> bookings = getAllBookings();
        
        return bookings.stream()
                .filter(b -> !b.getStatus().equalsIgnoreCase("CANCELLED") && 
                             !b.getStatus().equalsIgnoreCase("COMPLETED"))
                .collect(Collectors.toList());
    }
}
