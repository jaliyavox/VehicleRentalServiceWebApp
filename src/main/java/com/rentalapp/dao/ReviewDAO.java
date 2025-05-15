package com.rentalapp.dao;

import com.rentalapp.model.Review;
import com.rentalapp.util.FileUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Review entity.
 */
public class ReviewDAO {
    private static final Logger LOGGER = Logger.getLogger(ReviewDAO.class.getName());
    private static final String REVIEWS_FILE = "reviews.txt";
    
    /**
     * Retrieves all reviews from the data store.
     * 
     * @return a list of all reviews
     */
    public List<Review> getAllReviews() {
        List<String> lines = FileUtil.readAllLines(REVIEWS_FILE);
        List<Review> reviews = new ArrayList<>();
        
        for (String line : lines) {
            try {
                reviews.add(Review.fromString(line));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing review line: " + line, e);
            }
        }
        
        return reviews;
    }
    
    /**
     * Retrieves a review by ID.
     * 
     * @param id the ID of the review to retrieve
     * @return the review with the specified ID, or null if not found
     */
    public Review getReviewById(String id) {
        List<Review> reviews = getAllReviews();
        
        for (Review review : reviews) {
            if (review.getId().equals(id)) {
                return review;
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new review to the data store.
     * 
     * @param review the review to add
     * @return true if successful, false otherwise
     */
    public boolean addReview(Review review) {
        if (review == null || review.getId() == null || review.getId().isEmpty()) {
            return false;
        }
        
        // Check if review already exists
        if (getReviewById(review.getId()) != null) {
            return false;
        }
        
        // Set review date if not already set
        if (review.getReviewDate() == null) {
            review.setReviewDate(LocalDate.now());
        }
        
        return FileUtil.appendLine(REVIEWS_FILE, review.toString());
    }
    
    /**
     * Updates an existing review in the data store.
     * 
     * @param review the review to update
     * @return true if successful, false otherwise
     */
    public boolean updateReview(Review review) {
        if (review == null || review.getId() == null || review.getId().isEmpty()) {
            return false;
        }
        
        List<Review> reviews = getAllReviews();
        boolean found = false;
        
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(review.getId())) {
                reviews.set(i, review);
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        List<String> lines = reviews.stream()
                .map(Review::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(REVIEWS_FILE, lines);
    }
    
    /**
     * Deletes a review from the data store.
     * 
     * @param id the ID of the review to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteReview(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        
        List<Review> reviews = getAllReviews();
        boolean removed = reviews.removeIf(r -> r.getId().equals(id));
        
        if (!removed) {
            return false;
        }
        
        List<String> lines = reviews.stream()
                .map(Review::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(REVIEWS_FILE, lines);
    }
    
    /**
     * Gets all reviews for a vehicle.
     * 
     * @param vehicleId the ID of the vehicle
     * @return a list of reviews for the vehicle
     */
    public List<Review> getReviewsByVehicle(String vehicleId) {
        List<Review> reviews = getAllReviews();
        
        return reviews.stream()
                .filter(r -> r.getVehicleId().equals(vehicleId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all reviews by a user.
     * 
     * @param userId the ID of the user
     * @return a list of reviews by the user
     */
    public List<Review> getReviewsByUser(String userId) {
        List<Review> reviews = getAllReviews();
        
        return reviews.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates the average rating for a vehicle.
     * 
     * @param vehicleId the ID of the vehicle
     * @return the average rating, or 0 if there are no reviews
     */
    public double getAverageRatingForVehicle(String vehicleId) {
        List<Review> vehicleReviews = getReviewsByVehicle(vehicleId);
        
        if (vehicleReviews.isEmpty()) {
            return 0;
        }
        
        OptionalDouble average = vehicleReviews.stream()
                .mapToInt(Review::getRating)
                .average();
        
        return average.isPresent() ? average.getAsDouble() : 0;
    }
    
    /**
     * Checks if a user has already reviewed a vehicle.
     * 
     * @param userId the ID of the user
     * @param vehicleId the ID of the vehicle
     * @return true if the user has already reviewed the vehicle, false otherwise
     */
    public boolean hasUserReviewedVehicle(String userId, String vehicleId) {
        List<Review> reviews = getAllReviews();
        
        return reviews.stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.getVehicleId().equals(vehicleId));
    }
    
    /**
     * Gets the total number of reviews for a vehicle.
     * 
     * @param vehicleId the ID of the vehicle
     * @return the total number of reviews
     */
    public int getReviewCountForVehicle(String vehicleId) {
        return getReviewsByVehicle(vehicleId).size();
    }
}
