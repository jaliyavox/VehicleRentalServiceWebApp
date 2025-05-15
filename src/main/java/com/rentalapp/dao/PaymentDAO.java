package com.rentalapp.dao;

import com.rentalapp.model.Payment;
import com.rentalapp.util.FileUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Data Access Object for Payment entity.
 */
public class PaymentDAO {
    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());
    private static final String PAYMENTS_FILE = "payments.txt";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                Payment payment = parsePaymentFromLine(line);
                if (payment != null) {
                    payments.add(payment);
                }
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
        if (id == null || id.isEmpty()) {
            return null;
        }
        
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
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        String paymentLine = formatPaymentToLine(payment);
        return FileUtil.appendLine(PAYMENTS_FILE, paymentLine);
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
        refund.setId(UUID.randomUUID().toString());
        refund.setBookingId(payment.getBookingId());
        refund.setAmount(payment.getAmount().negate()); // Negative amount for refund
        refund.setPaymentDate(LocalDateTime.now());
        refund.setPaymentMethod("REFUND");
        refund.setStatus("APPROVED"); // Auto-approve refunds
        
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
    
    /**
     * Parse payment data from a line in the data file
     */
    private Payment parsePaymentFromLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split("\\|");
        if (parts.length < 8) { // Check for minimum required fields
            LOGGER.warning("Invalid payment data format: " + line);
            return null;
        }
        
        try {
            Payment payment = new Payment();
            payment.setId(parts[0]);
            payment.setBookingId(parts[1]);
            payment.setUserId(parts[2]);
            payment.setAmount(new BigDecimal(parts[3]));
            payment.setPaymentMethod(parts[4]);
            payment.setSlipImagePath(parts[5]);
            
            try {
                payment.setPaymentDate(LocalDateTime.parse(parts[6], DATETIME_FORMATTER));
            } catch (DateTimeParseException e) {
                LOGGER.warning("Invalid payment date format: " + parts[6]);
                payment.setPaymentDate(LocalDateTime.now());
            }
            
            payment.setStatus(parts[7]);
            
            // Optional fields
            if (parts.length > 8) {
                payment.setNotes(parts[8]);
            }
            
            if (parts.length > 9) {
                payment.setAdminId(parts[9]);
            }
            
            if (parts.length > 10) {
                try {
                    payment.setProcessedDate(LocalDateTime.parse(parts[10], DATETIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    // Optional field, ignore parsing error
                }
            }
            
            return payment;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing payment data: " + line, e);
            return null;
        }
    }
    
    /**
     * Format a payment object to a string for storage
     */
    private String formatPaymentToLine(Payment payment) {
        if (payment == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(payment.getId()).append("|");
        sb.append(payment.getBookingId()).append("|");
        sb.append(payment.getUserId()).append("|");
        sb.append(payment.getAmount()).append("|");
        sb.append(payment.getPaymentMethod()).append("|");
        sb.append(payment.getSlipImagePath() != null ? payment.getSlipImagePath() : "").append("|");
        
        // Format the payment date
        sb.append(payment.getPaymentDate() != null ? 
                payment.getPaymentDate().format(DATETIME_FORMATTER) : 
                LocalDateTime.now().format(DATETIME_FORMATTER))
          .append("|");
        
        sb.append(payment.getStatus()).append("|");
        sb.append(payment.getNotes() != null ? payment.getNotes() : "").append("|");
        sb.append(payment.getAdminId() != null ? payment.getAdminId() : "").append("|");
        
        // Format the processed date if available
        if (payment.getProcessedDate() != null) {
            sb.append(payment.getProcessedDate().format(DATETIME_FORMATTER));
        }
        
        return sb.toString();
    }
}
