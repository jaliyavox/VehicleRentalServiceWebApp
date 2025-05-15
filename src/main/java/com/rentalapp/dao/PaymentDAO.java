package com.rentalapp.dao;

import com.rentalapp.model.Payment;
import com.rentalapp.util.FileUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Payment entity.
 */
public class PaymentDAO {
    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());
    private static final String PAYMENTS_FILE = "payments.txt";
    
    /**
     * Retrieves all payments from the data store.
     * 
     * @return a list of all payments
     */
    public List<Payment> getAllPayments() {
        List<String> lines = FileUtil.readAllLines(PAYMENTS_FILE);
        List<Payment> payments = new ArrayList<>();
        
        for (String line : lines) {
            try {
                payments.add(Payment.fromString(line));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing payment line: " + line, e);
            }
        }
        
        return payments;
    }
    
    /**
     * Retrieves a payment by ID.
     * 
     * @param id the ID of the payment to retrieve
     * @return the payment with the specified ID, or null if not found
     */
    public Payment getPaymentById(String id) {
        List<Payment> payments = getAllPayments();
        
        for (Payment payment : payments) {
            if (payment.getId().equals(id)) {
                return payment;
            }
        }
        
        return null;
    }
    
    /**
     * Adds a new payment to the data store.
     * 
     * @param payment the payment to add
     * @return true if successful, false otherwise
     */
    public boolean addPayment(Payment payment) {
        if (payment == null || payment.getId() == null || payment.getId().isEmpty()) {
            return false;
        }
        
        // Check if payment already exists
        if (getPaymentById(payment.getId()) != null) {
            return false;
        }
        
        // Set payment date if not already set
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        
        return FileUtil.appendLine(PAYMENTS_FILE, payment.toString());
    }
    
    /**
     * Updates an existing payment in the data store.
     * 
     * @param payment the payment to update
     * @return true if successful, false otherwise
     */
    public boolean updatePayment(Payment payment) {
        if (payment == null || payment.getId() == null || payment.getId().isEmpty()) {
            return false;
        }
        
        List<Payment> payments = getAllPayments();
        boolean found = false;
        
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getId().equals(payment.getId())) {
                payments.set(i, payment);
                found = true;
                break;
            }
        }
        
        if (!found) {
            return false;
        }
        
        List<String> lines = payments.stream()
                .map(Payment::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(PAYMENTS_FILE, lines);
    }
    
    /**
     * Deletes a payment from the data store.
     * 
     * @param id the ID of the payment to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePayment(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        
        List<Payment> payments = getAllPayments();
        boolean removed = payments.removeIf(p -> p.getId().equals(id));
        
        if (!removed) {
            return false;
        }
        
        List<String> lines = payments.stream()
                .map(Payment::toString)
                .collect(Collectors.toList());
        
        return FileUtil.writeAllLines(PAYMENTS_FILE, lines);
    }
    
    /**
     * Gets all payments for a booking.
     * 
     * @param bookingId the ID of the booking
     * @return a list of payments for the booking
     */
    public List<Payment> getPaymentsByBooking(String bookingId) {
        List<Payment> payments = getAllPayments();
        
        return payments.stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all payments with a specific status.
     * 
     * @param status the status to filter by
     * @return a list of payments with the specified status
     */
    public List<Payment> getPaymentsByStatus(String status) {
        List<Payment> payments = getAllPayments();
        
        return payments.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    
    /**
     * Approves a payment.
     * 
     * @param id the ID of the payment to approve
     * @return true if successful, false otherwise
     */
    public boolean approvePayment(String id) {
        Payment payment = getPaymentById(id);
        
        if (payment == null) {
            return false;
        }
        
        payment.setStatus("APPROVED");
        
        return updatePayment(payment);
    }
    
    /**
     * Rejects a payment.
     * 
     * @param id the ID of the payment to reject
     * @return true if successful, false otherwise
     */
    public boolean rejectPayment(String id) {
        Payment payment = getPaymentById(id);
        
        if (payment == null) {
            return false;
        }
        
        payment.setStatus("REJECTED");
        
        return updatePayment(payment);
    }
    
    /**
     * Processes a refund for a payment.
     * 
     * @param id the ID of the payment to refund
     * @return true if successful, false otherwise
     */
    public boolean processRefund(String id) {
        Payment payment = getPaymentById(id);
        
        if (payment == null || !payment.isApproved()) {
            return false;
        }
        
        // Create a new payment record for the refund
        Payment refund = new Payment();
        refund.setId(FileUtil.generateUniqueId());
        refund.setBookingId(payment.getBookingId());
        refund.setAmount(payment.getAmount().negate()); // Negative amount for refund
        refund.setPaymentDate(LocalDate.now());
        refund.setPaymentMethod("REFUND");
        refund.setStatus("APPROVED"); // Auto-approve refunds
        refund.setTransactionId("REF-" + payment.getTransactionId());
        refund.setPaymentSlipPath("");
        
        return addPayment(refund);
    }
    
    /**
     * Gets all pending payments that need admin approval.
     * 
     * @return a list of pending payments
     */
    public List<Payment> getPendingPayments() {
        return getPaymentsByStatus("PENDING");
    }
}
