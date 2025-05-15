package com.rentalapp.controller.review;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.ReviewDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Review;
import com.rentalapp.model.Vehicle;
import com.rentalapp.util.FileUtil;
import com.rentalapp.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet for adding vehicle reviews.
 */
@javax.servlet.annotation.WebServlet("/reviews/add")
public class AddReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AddReviewServlet.class.getName());
    
    private ReviewDAO reviewDAO;
    private VehicleDAO vehicleDAO;
    private BookingDAO bookingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        reviewDAO = new ReviewDAO();
        vehicleDAO = new VehicleDAO();
        bookingDAO = new BookingDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        
        // Get vehicle ID from request
        String vehicleId = request.getParameter("vehicleId");
        
        if (vehicleId == null || vehicleId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        
        if (vehicle == null) {
            session.setAttribute("errorMessage", "Vehicle not found");
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Check if the user has completed a booking for this vehicle
        List<Booking> userBookings = bookingDAO.getBookingsByUser(userId);
        boolean hasCompletedBooking = userBookings.stream()
                .anyMatch(b -> b.getVehicleId().equals(vehicleId) && 
                         b.getStatus().equalsIgnoreCase("COMPLETED"));
        
        if (!hasCompletedBooking) {
            session.setAttribute("errorMessage", "You must complete a booking for this vehicle before leaving a review");
            response.sendRedirect(request.getContextPath() + "/vehicles/details?id=" + vehicleId);
            return;
        }
        
        // Check if the user has already reviewed this vehicle
        boolean hasReviewed = reviewDAO.hasUserReviewedVehicle(userId, vehicleId);
        
        if (hasReviewed) {
            session.setAttribute("errorMessage", "You have already reviewed this vehicle");
            response.sendRedirect(request.getContextPath() + "/vehicles/details?id=" + vehicleId);
            return;
        }
        
        // Set the vehicle as an attribute for the JSP
        request.setAttribute("vehicle", vehicle);
        
        // Forward to the add review page
        request.getRequestDispatcher("/review/add.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        
        // Get form parameters
        String vehicleId = ValidationUtil.sanitizeString(request.getParameter("vehicleId"));
        String ratingStr = ValidationUtil.sanitizeString(request.getParameter("rating"));
        String comment = ValidationUtil.sanitizeString(request.getParameter("comment"));
        
        // Validate input
        boolean isValid = true;
        
        // Vehicle validation
        Vehicle vehicle = null;
        if (vehicleId == null || vehicleId.isEmpty()) {
            request.setAttribute("vehicleIdError", "Vehicle selection is required");
            isValid = false;
        } else {
            vehicle = vehicleDAO.getVehicleById(vehicleId);
            if (vehicle == null) {
                request.setAttribute("vehicleIdError", "Invalid vehicle selection");
                isValid = false;
            }
        }
        
        // Check if the user has completed a booking for this vehicle
        List<Booking> userBookings = bookingDAO.getBookingsByUser(userId);
        boolean hasCompletedBooking = userBookings.stream()
                .anyMatch(b -> b.getVehicleId().equals(vehicleId) && 
                         b.getStatus().equalsIgnoreCase("COMPLETED"));
        
        if (!hasCompletedBooking) {
            request.setAttribute("vehicleIdError", "You must complete a booking for this vehicle before leaving a review");
            isValid = false;
        }
        
        // Check if the user has already reviewed this vehicle
        boolean hasReviewed = reviewDAO.hasUserReviewedVehicle(userId, vehicleId);
        
        if (hasReviewed) {
            request.setAttribute("vehicleIdError", "You have already reviewed this vehicle");
            isValid = false;
        }
        
        // Rating validation
        int rating = 0;
        if (ratingStr == null || ratingStr.isEmpty()) {
            request.setAttribute("ratingError", "Rating is required");
            isValid = false;
        } else {
            try {
                rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    request.setAttribute("ratingError", "Rating must be between 1 and 5");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("ratingError", "Invalid rating");
                isValid = false;
            }
        }
        
        // Comment validation
        if (comment == null || comment.isEmpty()) {
            request.setAttribute("commentError", "Comment is required");
            isValid = false;
        } else if (comment.length() < 10) {
            request.setAttribute("commentError", "Comment must be at least 10 characters long");
            isValid = false;
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("vehicle", vehicle);
            request.setAttribute("rating", ratingStr);
            request.setAttribute("comment", comment);
            
            request.getRequestDispatcher("/review/add.jsp").forward(request, response);
            return;
        }
        
        // Create a new review
        Review review = new Review();
        review.setId(FileUtil.generateUniqueId());
        review.setUserId(userId);
        review.setVehicleId(vehicleId);
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDate.now());
        
        // Save to the data store
        boolean success = reviewDAO.addReview(review);
        
        if (success) {
            // Redirect to the vehicle details page
            session.setAttribute("successMessage", "Review submitted successfully");
            response.sendRedirect(request.getContextPath() + "/vehicles/details?id=" + vehicleId);
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to submit review. Please try again.");
            request.setAttribute("vehicle", vehicle);
            request.setAttribute("rating", ratingStr);
            request.setAttribute("comment", comment);
            
            request.getRequestDispatcher("/review/add.jsp").forward(request, response);
        }
    }
}
