package com.rentalapp.controller.booking;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Vehicle;
import com.rentalapp.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Servlet for updating existing bookings.
 */
@javax.servlet.annotation.WebServlet("/bookings/update")
public class UpdateBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UpdateBookingServlet.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        bookingDAO = new BookingDAO();
        vehicleDAO = new VehicleDAO();
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
            session.setAttribute("errorMessage", "You do not have permission to edit this booking");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the booking is in a state that allows updates (not CANCELLED or COMPLETED)
        if (booking.getStatus().equalsIgnoreCase("CANCELLED") || booking.getStatus().equalsIgnoreCase("COMPLETED")) {
            session.setAttribute("errorMessage", "This booking cannot be updated because it is " + booking.getStatus().toLowerCase());
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        request.setAttribute("vehicle", vehicle);
        
        // Set the booking as an attribute
        request.setAttribute("booking", booking);
        
        // Set min date for the form (today or booking start date, whichever is later)
        LocalDate today = LocalDate.now();
        LocalDate minDate = booking.getStartDate().isBefore(today) ? today : booking.getStartDate();
        request.setAttribute("minStartDate", minDate.format(DATE_FORMATTER));
        
        // Forward to the booking edit page
        request.getRequestDispatcher("/booking/edit.jsp").forward(request, response);
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
        
        // Get form parameters
        String bookingId = request.getParameter("id");
        String startDateStr = ValidationUtil.sanitizeString(request.getParameter("startDate"));
        String endDateStr = ValidationUtil.sanitizeString(request.getParameter("endDate"));
        
        // Get the booking
        Booking booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null) {
            session.setAttribute("errorMessage", "Booking not found");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the user owns this booking or is an admin
        if (!booking.getUserId().equals(userId) && !isAdmin) {
            session.setAttribute("errorMessage", "You do not have permission to edit this booking");
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }
        
        // Check if the booking is in a state that allows updates
        if (booking.getStatus().equalsIgnoreCase("CANCELLED") || booking.getStatus().equalsIgnoreCase("COMPLETED")) {
            session.setAttribute("errorMessage", "This booking cannot be updated because it is " + booking.getStatus().toLowerCase());
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + bookingId);
            return;
        }
        
        // Get the vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        
        // Validate input
        boolean isValid = true;
        
        // Date validation
        LocalDate startDate = null;
        LocalDate endDate = null;
        
        if (startDateStr == null || startDateStr.isEmpty()) {
            request.setAttribute("startDateError", "Start date is required");
            isValid = false;
        } else if (!ValidationUtil.isValidDate(startDateStr)) {
            request.setAttribute("startDateError", "Invalid date format");
            isValid = false;
        } else {
            startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            
            if (startDate.isBefore(today)) {
                request.setAttribute("startDateError", "Start date cannot be in the past");
                isValid = false;
            }
        }
        
        if (endDateStr == null || endDateStr.isEmpty()) {
            request.setAttribute("endDateError", "End date is required");
            isValid = false;
        } else if (!ValidationUtil.isValidDate(endDateStr)) {
            request.setAttribute("endDateError", "Invalid date format");
            isValid = false;
        } else {
            endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
        }
        
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            request.setAttribute("endDateError", "End date must be after start date");
            isValid = false;
        }
        
        // Check if vehicle is available for the selected dates (excluding this booking)
        if (isValid && !bookingDAO.isVehicleAvailable(booking.getVehicleId(), startDate, endDate, bookingId)) {
            request.setAttribute("dateRangeError", "Vehicle is not available for the selected dates");
            isValid = false;
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            // Set the booking and vehicle as attributes
            request.setAttribute("booking", booking);
            request.setAttribute("vehicle", vehicle);
            
            // Set min date for the form
            LocalDate today = LocalDate.now();
            LocalDate minDate = booking.getStartDate().isBefore(today) ? today : booking.getStartDate();
            request.setAttribute("minStartDate", minDate.format(DATE_FORMATTER));
            
            request.getRequestDispatcher("/booking/edit.jsp").forward(request, response);
            return;
        }
        
        // Update the booking
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        
        // Recalculate total cost
        booking.calculateTotalCost(vehicle.getDailyRate());
        
        // Save to the data store
        boolean success = bookingDAO.updateBooking(booking);
        
        if (success) {
            // Redirect to the booking details page
            session.setAttribute("successMessage", "Booking updated successfully");
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + booking.getId());
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to update booking. Please try again.");
            request.setAttribute("booking", booking);
            request.setAttribute("vehicle", vehicle);
            
            // Set min date for the form
            LocalDate today = LocalDate.now();
            LocalDate minDate = booking.getStartDate().isBefore(today) ? today : booking.getStartDate();
            request.setAttribute("minStartDate", minDate.format(DATE_FORMATTER));
            
            request.getRequestDispatcher("/booking/edit.jsp").forward(request, response);
        }
    }
}
