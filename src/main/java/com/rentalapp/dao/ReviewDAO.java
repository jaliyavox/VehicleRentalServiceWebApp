package com.rentalapp.dao;

import com.rentalapp.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for Review operations
 */
public class ReviewDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewDAO.class);
    private static final String REVIEWS_FILE_PATH = "src/main/resources/data/reviews.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;
    
    /**
     * Default constructor
     */
    public ReviewDAO() {
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
    }
    
    /**
     * Get all reviews
     */
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(REVIEWS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Review review = parseReviewFromLine(line);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading reviews file", e);
        }
        
        return reviews;
    }
    
    /**
     * Get review by ID
     */
    public Review getById(String id) {
        try {
            List<Review> reviews = getAllReviews();
            
            for (Review review : reviews) {
                if (review.getId().equals(id)) {
                    return review;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting review by ID", e);
        }
        
        return null;
    }
    
    /**
     * Get review by ID (alias for backward compatibility)
     */
    public Review getReviewById(String id) {
        return getById(id);
    }
    
    /**
     * Get reviews by vehicle ID
     */
    public List<Review> getReviewsByVehicleId(String vehicleId) {
        List<Review> vehicleReviews = new ArrayList<>();
        
        for (Review review : getAllReviews()) {
            if (review.getVehicleId().equals(vehicleId)) {
                vehicleReviews.add(review);
            }
        }
        
        return vehicleReviews;
    }
    
    /**
     * Get reviews by vehicle (alias for backward compatibility)
     */
    public List<Review> getReviewsByVehicle(String vehicleId) {
        return getReviewsByVehicleId(vehicleId);
    }
    
    /**
     * Get average rating for a vehicle
     */
    public double getAverageRatingForVehicle(String vehicleId) {
        List<Review> reviews = getReviewsByVehicleId(vehicleId);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double totalRating = 0.0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        
        return totalRating / reviews.size();
    }
    
    /**
     * Get reviews by user ID
     */
    public List<Review> getReviewsByUserId(String userId) {
        List<Review> userReviews = new ArrayList<>();
        
        for (Review review : getAllReviews()) {
            if (review.getUserId().equals(userId)) {
                userReviews.add(review);
            }
        }
        
        return userReviews;
    }
    
    /**
     * Get reviews by user (alias for backward compatibility)
     */
    public List<Review> getReviewsByUser(String userId) {
        return getReviewsByUserId(userId);
    }
    
    /**
     * Check if a user has already reviewed a vehicle
     */
    public boolean hasUserReviewedVehicle(String userId, String vehicleId) {
        for (Review review : getAllReviews()) {
            if (review.getUserId().equals(userId) && review.getVehicleId().equals(vehicleId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add a new review
     */
    public boolean addReview(Review review) {
        if (review.getId() == null || review.getId().trim().isEmpty()) {
            review.setId(UUID.randomUUID().toString());
        }
        
        if (review.getReviewDate() == null) {
            review.setReviewDate(new Date());
        }
        
        try {
            String reviewRecord = formatReviewToLine(review);
            Path path = Paths.get(REVIEWS_FILE_PATH);
            Files.write(path, (reviewRecord + System.lineSeparator()).getBytes(), 
                    Files.exists(path) ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE);
            
            // Update vehicle average rating
            vehicleDAO.getById(review.getVehicleId()).updateRating(review.getRating());
            vehicleDAO.updateVehicle(vehicleDAO.getById(review.getVehicleId()));
            
            return true;
        } catch (IOException e) {
            logger.error("Error adding review", e);
            return false;
        }
    }
    
    /**
     * Update an existing review
     */
    public boolean updateReview(Review review) {
        try {
            List<Review> reviews = getAllReviews();
            List<String> lines = new ArrayList<>();
            
            for (Review existingReview : reviews) {
                if (existingReview.getId().equals(review.getId())) {
                    lines.add(formatReviewToLine(review));
                } else {
                    lines.add(formatReviewToLine(existingReview));
                }
            }
            
            Files.write(Paths.get(REVIEWS_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error updating review", e);
            return false;
        }
    }
    
    /**
     * Delete a review by ID
     */
    public boolean deleteReview(String reviewId) {
        try {
            List<Review> reviews = getAllReviews();
            List<String> lines = new ArrayList<>();
            
            for (Review review : reviews) {
                if (!review.getId().equals(reviewId)) {
                    lines.add(formatReviewToLine(review));
                }
            }
            
            Files.write(Paths.get(REVIEWS_FILE_PATH), String.join(System.lineSeparator(), lines).getBytes());
            
            return true;
        } catch (IOException e) {
            logger.error("Error deleting review", e);
            return false;
        }
    }
    
    /**
     * Parse a review from a line in the data file
     */
    private Review parseReviewFromLine(String line) {
        String[] parts = line.split("\\|");
        
        if (parts.length >= 7) {
            Review review = new Review();
            
            try {
                review.setId(parts[0]);
                review.setUserId(parts[1]);
                review.setVehicleId(parts[2]);
                review.setRating(Integer.parseInt(parts[3]));
                review.setComment(parts[4]);
                review.setReviewDate(DATE_FORMAT.parse(parts[5]));
                review.setVerified(Boolean.parseBoolean(parts[6]));
                
                if (parts.length > 7) review.setUserName(parts[7]);
                if (parts.length > 8) review.setVehicleName(parts[8]);
                if (parts.length > 9) review.setBookingId(parts[9]);
                
                return review;
            } catch (NumberFormatException | ParseException e) {
                logger.error("Error parsing review data: " + line, e);
            }
        }
        
        return null;
    }
    
    /**
     * Format a review as a line for the data file
     */
    private String formatReviewToLine(Review review) {
        StringBuilder sb = new StringBuilder();
        
        // Essential fields
        sb.append(review.getId()).append("|")
          .append(review.getUserId()).append("|")
          .append(review.getVehicleId()).append("|")
          .append(review.getRating()).append("|")
          .append(review.getComment()).append("|")
          .append(DATE_FORMAT.format(review.getReviewDate())).append("|")
          .append(review.isVerified());
        
        // Optional fields
        sb.append("|").append(review.getUserName() != null ? review.getUserName() : "");
        sb.append("|").append(review.getVehicleName() != null ? review.getVehicleName() : "");
        sb.append("|").append(review.getBookingId() != null ? review.getBookingId() : "");
        
        return sb.toString();
    }
}