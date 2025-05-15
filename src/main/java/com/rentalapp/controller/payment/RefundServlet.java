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
 * Servlet for processing refunds.
 */
@javax.servlet.annotation.WebServlet("/admin/payments/refund")
public class RefundServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RefundServlet.class.getName());
    
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
        
        // Check if the payment is approved (only approved payments can be refunded)
        if (!payment.isApproved()) {
            session.setAttribute("errorMessage", "Only approved payments can be refunded");
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
        
        // Set attributes for the JSP
        request.setAttribute("payment", payment);
        request.setAttribute("booking", booking);
        
        // Forward to the refund confirmation page
        request.getRequestDispatcher("/payment/refund.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get payment ID and confirmation from request
        String paymentId = request.getParameter("id");
        String confirmed = request.getParameter("confirmed");
        
        if (paymentId == null || paymentId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Check if the refund was confirmed
        if (!"true".equals(confirmed)) {
            response.sendRedirect(request.getContextPath() + "/payments/details?id=" + paymentId);
            return;
        }
        
        // Get the payment
        Payment payment = paymentDAO.getPaymentById(paymentId);
        
        if (payment == null) {
            session.setAttribute("errorMessage", "Payment not found");
            response.sendRedirect(request.getContextPath() + "/payments");
            return;
        }
        
        // Check if the payment is approved
        if (!payment.isApproved()) {
            session.setAttribute("errorMessage", "Only approved payments can be refunded");
            response.sendRedirect(request.getContextPath() + "/payments/details?id=" + paymentId);
            return;
        }
        
        // Process the refund
        boolean success = paymentDAO.processRefund(paymentId);
        
        if (success) {
            session.setAttribute("successMessage", "Refund processed successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to process refund");
        }
        
        // Redirect to the payment details page
        response.sendRedirect(request.getContextPath() + "/payments/details?id=" + paymentId);
    }
}
