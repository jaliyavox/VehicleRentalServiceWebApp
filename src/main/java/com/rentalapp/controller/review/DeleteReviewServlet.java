package com.rentalapp.controller.review;

import com.rentalapp.dao.ReviewDAO;
import com.rentalapp.dao.UserDAO;
import com.rentalapp.model.Review;
import com.rentalapp.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet for deleting reviews.
 */
@javax.servlet.annotation.WebServlet("/reviews/delete")
public class DeleteReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DeleteReviewServlet.class.getName());
    
    private ReviewDAO reviewDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        reviewDAO = new ReviewDAO();
        userDAO = new UserDAO();
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
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Get review ID from request
        String reviewId = request.getParameter("id");
        
        if (reviewId == null || reviewId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Get the review
        Review review = reviewDAO.getReviewById(reviewId);
        
        if (review == null) {
            session.setAttribute("errorMessage", "Review not found");
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Check if the user is the author of the review or an admin
        if (!review.getUserId().equals(userId) && !isAdmin) {
            session.setAttribute("errorMessage", "You do not have permission to delete this review");
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Get user info for display
        User user = userDAO.getUserById(review.getUserId());
        request.setAttribute("reviewer", user);
        
        // Set the review as an attribute for the JSP
        request.setAttribute("review", review);
        
        // Forward to the delete confirmation page
        request.getRequestDispatcher("/review/delete.jsp").forward(request, response);
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
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Get review ID and confirmation from request
        String reviewId = request.getParameter("id");
        String confirmed = request.getParameter("confirmed");
        
        if (reviewId == null || reviewId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Check if the deletion was confirmed
        if (!"true".equals(confirmed)) {
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Get the review
        Review review = reviewDAO.getReviewById(reviewId);
        
        if (review == null) {
            session.setAttribute("errorMessage", "Review not found");
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Check if the user is the author of the review or an admin
        if (!review.getUserId().equals(userId) && !isAdmin) {
            session.setAttribute("errorMessage", "You do not have permission to delete this review");
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        
        // Delete the review
        boolean success = reviewDAO.deleteReview(reviewId);
        
        if (success) {
            session.setAttribute("successMessage", "Review deleted successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to delete review");
        }
        
        // Redirect to the vehicle details or reviews page
        String vehicleId = review.getVehicleId();
        if (vehicleId != null && !vehicleId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vehicles/details?id=" + vehicleId);
        } else {
            response.sendRedirect(request.getContextPath() + "/reviews");
        }
    }
}
