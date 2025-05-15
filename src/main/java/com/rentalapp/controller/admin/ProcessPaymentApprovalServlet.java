package com.rentalapp.controller.admin;

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
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for processing payment approval/rejection by admin
 */
@WebServlet("/admin/payments/process")
public class ProcessPaymentApprovalServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentApprovalServlet.class);
    
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null || 
                !(Boolean)session.getAttribute("isAdmin")) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get parameters
        String paymentId = request.getParameter("paymentId");
        String action = request.getParameter("action"); // approve or reject
        String notes = request.getParameter("notes");
        
        if (paymentId == null || paymentId.isEmpty() || action == null || action.isEmpty()) {
            session.setAttribute("errorMessage", "Invalid request. Missing required parameters.");
            response.sendRedirect(request.getContextPath() + "/admin/payments");
            return;
        }
        
        try {
            // Get the payment
            Payment payment = paymentDAO.getPaymentById(paymentId);
            
            if (payment == null) {
                session.setAttribute("errorMessage", "Payment not found");
                response.sendRedirect(request.getContextPath() + "/admin/payments");
                return;
            }
            
            // Only pending payments can be processed
            if (!"PENDING".equalsIgnoreCase(payment.getStatus())) {
                session.setAttribute("errorMessage", 
                        "This payment has already been " + payment.getStatus().toLowerCase());
                response.sendRedirect(request.getContextPath() + "/admin/payments");
                return;
            }
            
            // Get the booking
            Booking booking = bookingDAO.getBookingById(payment.getBookingId());
            
            if (booking == null) {
                session.setAttribute("errorMessage", "Booking not found");
                response.sendRedirect(request.getContextPath() + "/admin/payments");
                return;
            }
            
            String adminId = (String) session.getAttribute("adminId");
            boolean success = false;
            
            if ("approve".equals(action)) {
                // Approve payment
                payment.setStatus("APPROVED");
                payment.setAdminId(adminId);
                payment.setProcessedDate(LocalDateTime.now());
                payment.setNotes(notes);
                
                success = paymentDAO.updatePayment(payment);
                
                if (success) {
                    // Update booking status to PAID
                    booking.setStatus("PAID");
                    bookingDAO.updateBooking(booking);
                    
                    session.setAttribute("successMessage", "Payment approved successfully");
                    logger.info("Payment {} approved by admin {}", paymentId, adminId);
                }
            } else if ("reject".equals(action)) {
                // Reject payment
                payment.setStatus("REJECTED");
                payment.setAdminId(adminId);
                payment.setProcessedDate(LocalDateTime.now());
                payment.setNotes(notes);
                
                success = paymentDAO.updatePayment(payment);
                
                if (success) {
                    session.setAttribute("successMessage", "Payment rejected");
                    logger.info("Payment {} rejected by admin {}", paymentId, adminId);
                }
            } else {
                session.setAttribute("errorMessage", "Invalid action");
            }
            
            if (!success) {
                session.setAttribute("errorMessage", "Failed to process payment. Please try again.");
            }
            
        } catch (Exception e) {
            logger.error("Error processing payment approval/rejection", e);
            session.setAttribute("errorMessage", "An error occurred while processing the payment");
        }
        
        // Redirect back to admin payments page
        response.sendRedirect(request.getContextPath() + "/admin/payments");
    }
}