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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for admin to view payment details
 */
@WebServlet("/admin/payments/view")
public class ViewPaymentServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ViewPaymentServlet.class);
    
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
        
        // Get payment ID
        String paymentId = request.getParameter("id");
        
        if (paymentId == null || paymentId.isEmpty()) {
            session.setAttribute("errorMessage", "Invalid payment ID");
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
            
            // Get the related booking
            Booking booking = bookingDAO.getBookingById(payment.getBookingId());
            
            // Get the user who made the payment
            User user = userDAO.getUserById(payment.getUserId());
            
            // Set attributes
            request.setAttribute("payment", payment);
            request.setAttribute("booking", booking);
            request.setAttribute("user", user);
            
            // If payment was processed, get the admin who processed it
            if (payment.getAdminId() != null && !payment.getAdminId().isEmpty()) {
                // This is mock because we're just showing admin username in the view
                request.setAttribute("adminUsername", "Admin #" + payment.getAdminId());
            }
            
            // Forward to payment details page
            request.getRequestDispatcher("/admin/payment-details.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error viewing payment details", e);
            session.setAttribute("errorMessage", "An error occurred while loading payment details");
            response.sendRedirect(request.getContextPath() + "/admin/payments");
        }
    }
}