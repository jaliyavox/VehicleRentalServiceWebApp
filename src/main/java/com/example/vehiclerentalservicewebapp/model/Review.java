package com.example.vehiclerentalservicewebapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Review class represents a user review for a vehicle in the rental system.
 * It stores information about the user who wrote the review, the vehicle being reviewed,
 * the rating given, comments, and when the review was submitted.
 */
public class Review {
    private String id;
    private String userId;
    private String vehicleId;
    private int rating;
    private String comment;
    private LocalDateTime reviewDate;

    // Default constructor
    public Review() {
        this.reviewDate = LocalDateTime.now();
    }

    /**
     * Constructor with all fields
     *
     * @param id Unique identifier for the review
     * @param userId ID of the user who submitted the review
     * @param vehicleId ID of the vehicle being reviewed
     * @param rating Rating given by the user (typically 1-5)
     * @param comment Text comment provided by the user
     * @param reviewDate Date and time when the review was submitted
     */
    public Review(String id, String userId, String vehicleId, int rating, String comment, LocalDateTime reviewDate) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    /**
     * Constructor with essential fields, generates current timestamp for reviewDate
     *
     * @param id Unique identifier for the review
     * @param userId ID of the user who submitted the review
     * @param vehicleId ID of the vehicle being reviewed
     * @param rating Rating given by the user (typically 1-5)
     * @param comment Text comment provided by the user
     */
    public Review(String id, String userId, String vehicleId, int rating, String comment) {
        this(id, userId, vehicleId, rating, comment, LocalDateTime.now());
    }

    // Getters and Setters

    /**
     * Get the review's unique identifier
     *
     * @return The review ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set the review's unique identifier
     *
     * @param id The review ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the ID of the user who submitted this review
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the ID of the user who submitted this review
     *
     * @param userId The user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the ID of the vehicle being reviewed
     *
     * @return The vehicle ID
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * Set the ID of the vehicle being reviewed
     *
     * @param vehicleId The vehicle ID to set
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * Get the rating given by the user
     *
     * @return The rating value
     */
    public int getRating() {
        return rating;
    }

    /**
     * Set the rating given by the user
     *
     * @param rating The rating value to set (typically 1-5)
     * @throws IllegalArgumentException if rating is less than 1 or greater than 5
     */
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    /**
     * Get the comment provided by the user
     *
     * @return The comment text
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment provided by the user
     *
     * @param comment The comment text to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Get the date and time when the review was submitted
     *
     * @return The review date
     */
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    /**
     * Set the date and time when the review was submitted
     *
     * @param reviewDate The review date to set
     */
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    /**
     * Convert the review object to a string representation for storage in a text file
     * Uses pipe character as delimiter for easy parsing
     *
     * @return String representation of the review
     */
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.join("|",
                id,
                userId,
                vehicleId,
                String.valueOf(rating),
                comment,
                reviewDate.format(formatter)
        );
    }

    /**
     * Create a Review object from a string representation (read from file)
     *
     * @param fileString The string representation of a review from the text file
     * @return A new Review object
     */
    public static Review fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid review data format");
        }

        Review review = new Review();
        review.setId(parts[0]);
        review.setUserId(parts[1]);
        review.setVehicleId(parts[2]);
        review.setRating(Integer.parseInt(parts[3]));
        review.setComment(parts[4]);
        review.setReviewDate(LocalDateTime.parse(parts[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return review;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return rating == review.rating &&
                Objects.equals(id, review.id) &&
                Objects.equals(userId, review.userId) &&
                Objects.equals(vehicleId, review.vehicleId) &&
                Objects.equals(comment, review.comment) &&
                Objects.equals(reviewDate, review.reviewDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, vehicleId, rating, comment, reviewDate);
    }
}