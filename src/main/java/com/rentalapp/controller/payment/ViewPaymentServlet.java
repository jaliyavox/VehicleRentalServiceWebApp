package com.rentalapp.controller.payment;

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
 * Servlet for viewing payments.
 */
@WebServlet({"/payments", "/payments/confirmation", "/payments/details"})
public class ViewPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewPaymentServlet.class.getName());
    
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
        vehicleDAO = new VehicleDAO();
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
        
        String path = request.getServletPath();
        
        if ("/payments".equals(path)) {
            // List payments
            handlePaymentList(request, response);
        } else if ("/payments/confirmation".equals(path)) {
            // Show payment confirmation
            handlePaymentConfirmation(request, response);
        } else if ("/payments/details".equals(path)) {
            // Show payment details
            handlePaymentDetails(request, response);
        }
    }
    
    /**
     * Handles the listing of payments.
     */
    private void handlePaymentList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        
        List<Payment> payments;
        
        // If admin, can see all payments; otherwise, only payments related to user's bookings
        if (isAdmin) {
            if (statusFilter != null && !statusFilter.isEmpty()) {
                payments = paymentDAO.getPaymentsByStatus(statusFilter);
            } else {
                payments = paymentDAO.getAllPayments();
            }
        } else {
            // Get the user's bookings
            List<Booking> userBookings = bookingDAO.getBookingsByUser(userId);
            
            // Create a list of payments from those bookings
            payments = new java.util.ArrayList<>();
            for (Booking booking : userBookings) {
                List<Payment> bookingPayments = paymentDAO.getPaymentsByBooking(booking.getId());
                payments.addAll(bookingPayments);
            }
            
            // Filter by status if requested
            if (statusFilter != null && !statusFilter.isEmpty()) {
                payments.removeIf(p -> !p.getStatus().equalsIgnoreCase(statusFilter));
            }
        }
        
        // Load booking details for each payment
        for (Payment payment : payments) {
            Booking booking = bookingDAO.getBookingById(payment.getBookingId());
            request.setAttribute("booking_" + payment.getId(), booking);
            
            if (booking != null) {
                Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
                request.setAttribute("vehicle_" + payment.getId(), vehicle);
                
                User user = userDAO.getUserById(booking.getUserId());
                request.setAttribute("user_" + payment.getId(), user);
            }
        }
        
        // Set attributes for the JSP
        request.setAttribute("payments", payments);
        request.setAttribute("statusFilter", statusFilter);
        
        // Forward to the appropriate page
        if (isAdmin) {
            request.getRequestDispatcher("/payment/approval-list.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/payment/list.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles the payment confirmation page.
     */
    private void handlePaymentConfirmation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");
        
        // Get payment ID from request
        String paymentId = request.getParameter("id");
        
        if (paymentId == null || paymentId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Get the payment
        Payment payment = paymentDAO.getPaymentById(paymentId);
        
        if (payment == null) {
            session.setAttribute("errorMessage", "Payment not found");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Get the booking
        Booking booking = bookingDAO.getBookingById(payment.getBookingId());
        
        if (booking == null) {
            session.setAttribute("errorMessage", "Booking information not found");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Check if the user owns this booking
        if (!booking.getUserId().equals(userId)) {
            session.setAttribute("errorMessage", "You do not have permission to view this payment");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        
        // Set attributes for the JSP
        request.setAttribute("payment", payment);
        request.setAttribute("booking", booking);
        request.setAttribute("vehicle", vehicle);
        
        // Forward to the payment confirmation page
        request.getRequestDispatcher("/payment/confirmation.jsp").forward(request, response);
    }
    
    /**
     * Handles the payment details page.
     */
    private void handlePaymentDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String userId = (String) session.getAttribute("userId");
        boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Get payment ID from request
        String paymentId = request.getParameter("id");
        
        if (paymentId == null || paymentId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Get the payment
        Payment payment = paymentDAO.getPaymentById(paymentId);
        
        if (payment == null) {
            session.setAttribute("errorMessage", "Payment not found");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Get the booking
        Booking booking = bookingDAO.getBookingById(payment.getBookingId());
        
        if (booking == null) {
            session.setAttribute("errorMessage", "Booking information not found");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Check if the user owns this booking or is an admin
        if (!booking.getUserId().equals(userId) && !isAdmin) {
            session.setAttribute("errorMessage", "You do not have permission to view this payment");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        
        // Get the user
        User user = userDAO.getUserById(booking.getUserId());
        
        // Set attributes for the JSP
        request.setAttribute("payment", payment);
        request.setAttribute("booking", booking);
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("user", user);
        
        // Forward to the payment details page
        request.getRequestDispatcher("/payment/details.jsp").forward(request, response);
    }
}
