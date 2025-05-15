package com.rentalapp.controller.payment;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;
import com.rentalapp.util.FileUtil;
import com.rentalapp.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for processing payment submissions.
 */
@javax.servlet.annotation.WebServlet("/payments/process")
@javax.servlet.annotation.MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 5 * 1024 * 1024,    // 5 MB
    maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class ProcessPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProcessPaymentServlet.class.getName());
    
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
        
        // Initialize directories for file uploads
        FileUtil.initializeDirectories();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get booking ID from request
        String bookingId = request.getParameter("bookingId");
        
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
        
        // Check if the user owns this booking
        String userId = (String) session.getAttribute("userId");
        if (!booking.getUserId().equals(userId)) {
            session.setAttribute("errorMessage", "You do not have permission to make payments for this booking");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if booking is in a state that accepts payments
        if (!booking.getStatus().equalsIgnoreCase("PENDING") && !booking.getStatus().equalsIgnoreCase("CONFIRMED")) {
            session.setAttribute("errorMessage", "This booking is " + booking.getStatus().toLowerCase() + " and cannot accept payments");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        // Set the booking as an attribute
        request.setAttribute("booking", booking);
        
        // Forward to the payment upload page
        request.getRequestDispatcher("/payment/upload.jsp").forward(request, response);
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
        
        // Get form parameters
        String bookingId = ValidationUtil.sanitizeString(request.getParameter("bookingId"));
        String paymentMethodStr = ValidationUtil.sanitizeString(request.getParameter("paymentMethod"));
        String amountStr = ValidationUtil.sanitizeString(request.getParameter("amount"));
        String transactionId = ValidationUtil.sanitizeString(request.getParameter("transactionId"));
        
        // Get the file part (payment slip)
        Part filePart = request.getPart("paymentSlip");
        
        // Validate input
        boolean isValid = true;
        
        // Booking validation
        Booking booking = null;
        if (bookingId == null || bookingId.isEmpty()) {
            request.setAttribute("bookingIdError", "Booking ID is required");
            isValid = false;
        } else {
            booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                request.setAttribute("bookingIdError", "Invalid booking");
                isValid = false;
            } else if (!booking.getUserId().equals(userId)) {
                request.setAttribute("bookingIdError", "You do not have permission to make payments for this booking");
                isValid = false;
            } else if (!booking.getStatus().equalsIgnoreCase("PENDING") && !booking.getStatus().equalsIgnoreCase("CONFIRMED")) {
                request.setAttribute("bookingIdError", "This booking is " + booking.getStatus().toLowerCase() + " and cannot accept payments");
                isValid = false;
            }
        }
        
        // Payment method validation
        if (paymentMethodStr == null || paymentMethodStr.isEmpty()) {
            request.setAttribute("paymentMethodError", "Payment method is required");
            isValid = false;
        }
        
        // Amount validation
        BigDecimal amount = BigDecimal.ZERO;
        if (amountStr == null || amountStr.isEmpty()) {
            request.setAttribute("amountError", "Amount is required");
            isValid = false;
        } else {
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    request.setAttribute("amountError", "Amount must be greater than zero");
                    isValid = false;
                } else if (booking != null && amount.compareTo(booking.getTotalCost()) < 0) {
                    request.setAttribute("amountError", "Amount must be at least the total cost of the booking");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("amountError", "Amount must be a valid number");
                isValid = false;
            }
        }
        
        // File validation
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("paymentSlipError", "Payment slip is required");
            isValid = false;
        } else {
            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("paymentSlipError", "Invalid file");
                isValid = false;
            } else {
                // Check file extension
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                if (!extension.equals("pdf") && !extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png")) {
                    request.setAttribute("paymentSlipError", "Only PDF, JPG, JPEG and PNG files are allowed");
                    isValid = false;
                }
            }
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("booking", booking);
            request.setAttribute("paymentMethod", paymentMethodStr);
            request.setAttribute("amount", amountStr);
            request.setAttribute("transactionId", transactionId);
            
            request.getRequestDispatcher("/payment/upload.jsp").forward(request, response);
            return;
        }
        
        // Process the file upload
        String paymentSlipPath = "";
        try {
            String fileName = filePart.getSubmittedFileName();
            paymentSlipPath = FileUtil.saveUploadedFile(filePart.getInputStream(), fileName, "payment-slips/");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving payment slip", e);
            request.setAttribute("errorMessage", "Failed to upload payment slip. Please try again.");
            request.setAttribute("booking", booking);
            request.setAttribute("paymentMethod", paymentMethodStr);
            request.setAttribute("amount", amountStr);
            request.setAttribute("transactionId", transactionId);
            
            request.getRequestDispatcher("/payment/upload.jsp").forward(request, response);
            return;
        }
        
        // Create a new payment
        Payment payment = new Payment();
        payment.setId(FileUtil.generateUniqueId());
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethodStr);
        payment.setStatus("PENDING");
        payment.setTransactionId(transactionId);
        payment.setPaymentSlipPath(paymentSlipPath);
        
        // Save to the data store
        boolean success = paymentDAO.addPayment(payment);
        
        if (success) {
            // Update booking status to CONFIRMED if it was PENDING
            if (booking.getStatus().equalsIgnoreCase("PENDING")) {
                booking.setStatus("CONFIRMED");
                bookingDAO.updateBooking(booking);
            }
            
            // Redirect to the payment confirmation page
            response.sendRedirect(request.getContextPath() + "/payments/confirmation?id=" + payment.getId());
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to process payment. Please try again.");
            request.setAttribute("booking", booking);
            request.setAttribute("paymentMethod", paymentMethodStr);
            request.setAttribute("amount", amountStr);
            request.setAttribute("transactionId", transactionId);
            
            request.getRequestDispatcher("/payment/upload.jsp").forward(request, response);
        }
    }
}
