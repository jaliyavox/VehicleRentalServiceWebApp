package com.rentalapp.controller.booking;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;
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
 * Servlet for cancelling bookings.
 */
@WebServlet("/bookings/cancel")
public class CancelBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CancelBookingServlet.class.getName());
    
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    private PaymentDAO paymentDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        bookingDAO = new BookingDAO();
        vehicleDAO = new VehicleDAO();
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
            session.setAttribute("errorMessage", "You do not have permission to cancel this booking");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the booking is already cancelled or completed
        if (booking.getStatus().equalsIgnoreCase("CANCELLED")) {
            session.setAttribute("errorMessage", "This booking is already cancelled");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        if (booking.getStatus().equalsIgnoreCase("COMPLETED")) {
            session.setAttribute("errorMessage", "This booking is already completed and cannot be cancelled");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        request.setAttribute("vehicle", vehicle);
        
        // Set the booking as an attribute
        request.setAttribute("booking", booking);
        
        // Forward to the cancel confirmation page
        request.getRequestDispatcher("/booking/cancel.jsp").forward(request, response);
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
        
        // Get booking ID from request
        String bookingId = request.getParameter("id");
        String confirmed = request.getParameter("confirmed");
        
        if (bookingId == null || bookingId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the cancellation was confirmed
        if (!"true".equals(confirmed)) {
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
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
            session.setAttribute("errorMessage", "You do not have permission to cancel this booking");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the booking is already cancelled or completed
        if (booking.getStatus().equalsIgnoreCase("CANCELLED")) {
            session.setAttribute("errorMessage", "This booking is already cancelled");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        if (booking.getStatus().equalsIgnoreCase("COMPLETED")) {
            session.setAttribute("errorMessage", "This booking is already completed and cannot be cancelled");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        // Cancel the booking
        boolean success = bookingDAO.cancelBooking(bookingId);
        
        if (success) {
            // Make the vehicle available again
            Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
            if (vehicle != null) {
                vehicle.setAvailable(true);
                vehicleDAO.updateVehicle(vehicle);
            }
            
            // Process refunds for any approved payments
            List<Payment> bookingPayments = paymentDAO.getPaymentsByBooking(bookingId);
            for (Payment payment : bookingPayments) {
                if (payment.isApproved()) {
                    paymentDAO.processRefund(payment.getId());
                }
            }
            
            // Redirect to the booking details page
            session.setAttribute("successMessage", "Booking cancelled successfully");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
        } else {
            // Redirect with error message
            session.setAttribute("errorMessage", "Failed to cancel booking. Please try again.");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
        }
    }
}
