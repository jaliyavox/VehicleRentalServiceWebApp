package com.rentalapp.controller.payment;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet for approving payments.
 */
@javax.servlet.annotation.WebServlet("/admin/payments/approve")
public class ApprovePaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ApprovePaymentServlet.class.getName());
    
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
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
        
        // Check if the payment is pending
        if (!payment.isPending()) {
            session.setAttribute("errorMessage", "This payment is already " + payment.getStatus().toLowerCase());
            response.sendRedirect(request.getContextPath() + "/payments/details?id=" + paymentId);
            return;
        }
        
        // Get the booking
        Booking booking = bookingDAO.getBookingById(payment.getBookingId());
        
        if (booking == null) {
            session.setAttribute("errorMessage", "Booking information not found");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Approve the payment
        boolean success = paymentDAO.approvePayment(paymentId);
        
        if (success) {
            // Update booking status to CONFIRMED if it's PENDING
            if (booking.getStatus().equalsIgnoreCase("PENDING")) {
                booking.setStatus("CONFIRMED");
                bookingDAO.updateBooking(booking);
            }
            
            session.setAttribute("successMessage", "Payment approved successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to approve payment");
        }
        
        // Redirect to the payment details page
        response.sendRedirect(request.getContextPath() + "/payments/details?id=" + paymentId);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Post requests are handled the same way as get requests for this servlet
        doGet(request, response);
    }
}
