package com.rentalapp.controller.review;

import com.rentalapp.dao.ReviewDAO;
import com.rentalapp.dao.UserDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Review;
import com.rentalapp.model.User;
import com.rentalapp.model.Vehicle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet for viewing reviews.
 */
@javax.servlet.annotation.WebServlet({"/reviews", "/vehicles/reviews"})
public class ViewReviewsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewReviewsServlet.class.getName());
    
    private ReviewDAO reviewDAO;
    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        reviewDAO = new ReviewDAO();
        userDAO = new UserDAO();
        vehicleDAO = new VehicleDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/reviews".equals(path)) {
            // List all reviews (or filtered by user if not admin)
            handleAllReviews(request, response);
        } else if ("/vehicles/reviews".equals(path)) {
            // List reviews for a specific vehicle
            handleVehicleReviews(request, response);
        }
    }
    
    /**
     * Handles the listing of all reviews.
     */
    private void handleAllReviews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String userId = (String) session.getAttribute("userId");
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        List<Review> reviews;
        
        // If admin, can see all reviews; otherwise, only user's reviews
        if (isAdmin) {
            reviews = reviewDAO.getAllReviews();
        } else {
            reviews = reviewDAO.getReviewsByUser(userId);
        }
        
        // Load user and vehicle details for each review
        for (Review review : reviews) {
            User user = userDAO.getUserById(review.getUserId());
            Vehicle vehicle = vehicleDAO.getVehicleById(review.getVehicleId());
            
            request.setAttribute("user_" + review.getId(), user);
            request.setAttribute("vehicle_" + review.getId(), vehicle);
        }
        
        // Set attributes for the JSP
        request.setAttribute("reviews", reviews);
        
        // Forward to the review list page
        request.getRequestDispatcher("/review/list.jsp").forward(request, response);
    }
    
    /**
     * Handles the listing of reviews for a specific vehicle.
     */
    private void handleVehicleReviews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get vehicle ID from request
        String vehicleId = request.getParameter("id");
        
        if (vehicleId == null || vehicleId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        
        if (vehicle == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("errorMessage", "Vehicle not found");
            }
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Get reviews for the vehicle
        List<Review> reviews = reviewDAO.getReviewsByVehicle(vehicleId);
        double averageRating = reviewDAO.getAverageRatingForVehicle(vehicleId);
        
        // Load user details for each review
        for (Review review : reviews) {
            User user = userDAO.getUserById(review.getUserId());
            request.setAttribute("user_" + review.getId(), user);
        }
        
        // Set attributes for the JSP
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("reviews", reviews);
        request.setAttribute("averageRating", averageRating);
        
        // Forward to the vehicle reviews page
        request.getRequestDispatcher("/review/vehicle-reviews.jsp").forward(request, response);
    }
}
