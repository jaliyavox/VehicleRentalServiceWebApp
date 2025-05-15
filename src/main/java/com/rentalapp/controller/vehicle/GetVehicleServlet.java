package com.rentalapp.controller.vehicle;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.ReviewDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Review;
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
 * Servlet for retrieving and displaying vehicle information.
 */
@javax.servlet.annotation.WebServlet({"/vehicles", "/vehicles/details"})
public class GetVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GetVehicleServlet.class.getName());
    
    private VehicleDAO vehicleDAO;
    private ReviewDAO reviewDAO;
    private BookingDAO bookingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleDAO = new VehicleDAO();
        reviewDAO = new ReviewDAO();
        bookingDAO = new BookingDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/vehicles".equals(path)) {
            // List all vehicles
            handleVehicleList(request, response);
        } else if ("/vehicles/details".equals(path)) {
            // Show details for a specific vehicle
            handleVehicleDetails(request, response);
        }
    }
    
    /**
     * Handles the listing of vehicles.
     */
    private void handleVehicleList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get filter parameters
        String type = request.getParameter("type");
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String availableOnly = request.getParameter("availableOnly");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        boolean filterAvailableOnly = "true".equals(availableOnly);
        
        // Get vehicles based on filter criteria
        List<Vehicle> vehicles = vehicleDAO.searchVehicles(type, make, model, filterAvailableOnly);
        
        // Sort vehicles if requested
        if ("price".equals(sortBy)) {
            vehicles = vehicleDAO.sortByDailyRate(vehicles, "asc".equals(sortOrder));
        } else if ("availability".equals(sortBy)) {
            vehicles = vehicleDAO.sortByAvailability(vehicles);
        }
        
        // Get filter options for the form
        List<String> vehicleTypes = vehicleDAO.getVehicleTypes();
        List<String> vehicleMakes = vehicleDAO.getVehicleMakes();
        
        // Set attributes for the JSP
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("vehicleTypes", vehicleTypes);
        request.setAttribute("vehicleMakes", vehicleMakes);
        request.setAttribute("type", type);
        request.setAttribute("make", make);
        request.setAttribute("model", model);
        request.setAttribute("availableOnly", filterAvailableOnly);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        // Forward to the vehicle list page
        request.getRequestDispatcher("/vehicle/list.jsp").forward(request, response);
    }
    
    /**
     * Handles the display of details for a specific vehicle.
     */
    private void handleVehicleDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the vehicle ID from the request
        String vehicleId = request.getParameter("id");
        
        if (vehicleId == null || vehicleId.isEmpty()) {
            // Redirect to the vehicle list page if no ID is provided
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Get the vehicle from the data store
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        
        if (vehicle == null) {
            // Redirect to the vehicle list page if the vehicle doesn't exist
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
        
        // Check if the user has already reviewed this vehicle
        HttpSession session = request.getSession(false);
        boolean userCanReview = false;
        
        if (session != null && session.getAttribute("userId") != null) {
            String userId = (String) session.getAttribute("userId");
            
            // Check if user has any completed bookings for this vehicle
            List<Booking> userBookings = bookingDAO.getBookingsByUser(userId);
            boolean hasCompletedBooking = userBookings.stream()
                    .anyMatch(b -> b.getVehicleId().equals(vehicleId) && 
                               b.getStatus().equalsIgnoreCase("COMPLETED"));
            
            // Check if user has already reviewed this vehicle
            boolean hasReviewed = reviewDAO.hasUserReviewedVehicle(userId, vehicleId);
            
            // User can review if they have a completed booking and haven't already reviewed
            userCanReview = hasCompletedBooking && !hasReviewed;
        }
        
        // Set attributes for the JSP
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("reviews", reviews);
        request.setAttribute("averageRating", averageRating);
        request.setAttribute("userCanReview", userCanReview);
        
        // Forward to the vehicle details page
        request.getRequestDispatcher("/vehicle/details.jsp").forward(request, response);
    }
}
