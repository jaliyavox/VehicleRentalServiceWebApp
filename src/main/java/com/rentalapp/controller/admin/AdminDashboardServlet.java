package com.rentalapp.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rentalapp.dao.AdminDAO;
import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.dao.ReviewDAO;
import com.rentalapp.dao.UserDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Admin;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;
import com.rentalapp.model.Review;
import com.rentalapp.model.User;
import com.rentalapp.model.Vehicle;

import java.io.IOException;
import java.util.List;

/**
 * Servlet to handle admin dashboard functionality
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServlet.class);
    
    private VehicleDAO vehicleDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    private PaymentDAO paymentDAO;
    private ReviewDAO reviewDAO;
    private AdminDAO adminDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleDAO = new VehicleDAO();
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
        paymentDAO = new PaymentDAO();
        reviewDAO = new ReviewDAO();
        adminDAO = new AdminDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get counts for dashboard statistics
        try {
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            List<Booking> bookings = bookingDAO.getAllBookings();
            List<User> users = userDAO.getAllUsers();
            List<Payment> payments = paymentDAO.getAllPayments();
            List<Review> reviews = reviewDAO.getAllReviews();
            
            // Filter for pending payments
            long pendingPayments = payments.stream()
                    .filter(p -> "pending".equalsIgnoreCase(p.getStatus()))
                    .count();
            
            // Set attribute for dashboard statistics
            request.setAttribute("vehicleCount", vehicles.size());
            request.setAttribute("bookingCount", bookings.size());
            request.setAttribute("userCount", users.size());
            request.setAttribute("pendingPaymentCount", pendingPayments);
            request.setAttribute("reviewCount", reviews.size());
            
            // Get recent items for display
            request.setAttribute("recentBookings", 
                    bookings.size() > 5 ? bookings.subList(0, 5) : bookings);
            request.setAttribute("recentReviews", 
                    reviews.size() > 5 ? reviews.subList(0, 5) : reviews);
            
        } catch (Exception e) {
            logger.error("Error loading dashboard data", e);
            request.setAttribute("errorMessage", "Error loading dashboard data");
        }
        
        // Forward to dashboard JSP
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}