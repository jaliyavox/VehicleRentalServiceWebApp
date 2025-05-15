package com.rentalapp.controller.admin;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.dao.UserDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;
import com.rentalapp.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for admin payment management
 */
@WebServlet("/admin/payments")
public class AdminPaymentsServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminPaymentsServlet.class);
    
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null || 
                !(Boolean)session.getAttribute("isAdmin")) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        try {
            // Get filter parameter
            String statusFilter = request.getParameter("status");
            List<Payment> payments;
            
            if ("pending".equalsIgnoreCase(statusFilter)) {
                payments = paymentDAO.getPaymentsByStatus("PENDING");
                request.setAttribute("activeFilter", "pending");
            } else if ("approved".equalsIgnoreCase(statusFilter)) {
                payments = paymentDAO.getPaymentsByStatus("APPROVED");
                request.setAttribute("activeFilter", "approved");
            } else if ("rejected".equalsIgnoreCase(statusFilter)) {
                payments = paymentDAO.getPaymentsByStatus("REJECTED");
                request.setAttribute("activeFilter", "rejected");
            } else {
                // Default: show all payments
                payments = paymentDAO.getAllPayments();
                request.setAttribute("activeFilter", "all");
            }
            
            // Get related bookings and users for display
            Map<String, Booking> bookingsMap = new HashMap<>();
            Map<String, User> usersMap = new HashMap<>();
            
            for (Payment payment : payments) {
                // Get booking if not already in map
                if (!bookingsMap.containsKey(payment.getBookingId())) {
                    Booking booking = bookingDAO.getBookingById(payment.getBookingId());
                    if (booking != null) {
                        bookingsMap.put(payment.getBookingId(), booking);
                    }
                }
                
                // Get user if not already in map
                if (!usersMap.containsKey(payment.getUserId())) {
                    User user = userDAO.getUserById(payment.getUserId());
                    if (user != null) {
                        usersMap.put(payment.getUserId(), user);
                    }
                }
            }
            
            // Set attributes
            request.setAttribute("payments", payments);
            request.setAttribute("bookingsMap", bookingsMap);
            request.setAttribute("usersMap", usersMap);
            
            // Count payments by status
            int pendingCount = paymentDAO.getPaymentsByStatus("PENDING").size();
            int approvedCount = paymentDAO.getPaymentsByStatus("APPROVED").size();
            int rejectedCount = paymentDAO.getPaymentsByStatus("REJECTED").size();
            
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("approvedCount", approvedCount);
            request.setAttribute("rejectedCount", rejectedCount);
            request.setAttribute("totalCount", pendingCount + approvedCount + rejectedCount);
            
            // Forward to admin payments page
            request.getRequestDispatcher("/admin/payments.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error loading admin payments page", e);
            request.setAttribute("errorMessage", "An error occurred while loading payments");
            request.getRequestDispatcher("/admin/payments.jsp").forward(request, response);
        }
    }
}