package com.rentalapp.controller.booking;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.dao.UserDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;
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
 * Servlet for viewing bookings.
 */
@WebServlet({"/bookings", "/bookings/details"})
public class ViewBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewBookingServlet.class.getName());
    
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;
    private PaymentDAO paymentDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        bookingDAO = new BookingDAO();
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        paymentDAO = new PaymentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/bookings".equals(path)) {
            // List all bookings for the user
            handleBookingList(request, response);
        } else if ("/bookings/details".equals(path)) {
            // Show details for a specific booking
            handleBookingDetails(request, response);
        }
    }
    
    /**
     * Handles the listing of bookings.
     */
    private void handleBookingList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        
        List<Booking> bookings;
        
        // If admin, can see all bookings; otherwise, only user's bookings
        if (isAdmin) {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                bookings = bookingDAO.getBookingsByStatus(statusFilter);
            } else {
                bookings = bookingDAO.getAllBookings();
            }
        } else {
            bookings = bookingDAO.getBookingsByUser(userId);
            
            if (statusFilter != null && !statusFilter.isEmpty()) {
                bookings.removeIf(b -> !b.getStatus().equalsIgnoreCase(statusFilter));
            }
        }
        
        // Load vehicle details for each booking
        for (Booking booking : bookings) {
            Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
            request.setAttribute("vehicle_" + booking.getId(), vehicle);
            
            if (isAdmin) {
                User user = userDAO.getUserById(booking.getUserId());
                request.setAttribute("user_" + booking.getId(), user);
            }
        }
        
        // Set attributes for the JSP
        request.setAttribute("bookings", bookings);
        request.setAttribute("statusFilter", statusFilter);
        
        // Forward to the booking list page
        request.getRequestDispatcher("/booking/list.jsp").forward(request, response);
    }
    
    /**
     * Handles the display of details for a specific booking.
     */
    private void handleBookingDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Get booking ID from request
        String bookingId = request.getParameter("id");
        
        if (bookingId == null || bookingId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Get the booking
        Booking booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null) {
            session.setAttribute("errorMessage", "Booking not found");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the user owns this booking or is an admin
        if (!booking.getUserId().equals(userId) && !isAdmin) {
            session.setAttribute("errorMessage", "You do not have permission to view this booking");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        
        // Get the user (for admin view)
        User user = null;
        if (isAdmin) {
            user = userDAO.getUserById(booking.getUserId());
        }
        
        // Get payments for this booking
        List<Payment> payments = paymentDAO.getPaymentsByBooking(bookingId);
        
        // Set attributes for the JSP
        request.setAttribute("booking", booking);
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("user", user);
        request.setAttribute("payments", payments);
        
        // Check if there's a pending payment
        boolean hasPendingPayment = payments.stream().anyMatch(Payment::isPending);
        boolean hasApprovedPayment = payments.stream().anyMatch(Payment::isApproved);
        
        request.setAttribute("hasPendingPayment", hasPendingPayment);
        request.setAttribute("hasApprovedPayment", hasApprovedPayment);
        
        // Forward to the booking details page
        request.getRequestDispatcher("/booking/details.jsp").forward(request, response);
    }
}
