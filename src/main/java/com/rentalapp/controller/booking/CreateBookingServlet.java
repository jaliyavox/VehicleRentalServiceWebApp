package com.rentalapp.controller.booking;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Vehicle;
import com.rentalapp.util.FileUtil;
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
 * Servlet for creating new bookings.
 */
@javax.servlet.annotation.WebServlet("/bookings/create")
public class CreateBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateBookingServlet.class.getName());
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
        
        // Get vehicle ID from request (if provided)
        String vehicleId = request.getParameter("vehicleId");
        
        if (vehicleId != null && !vehicleId.isEmpty()) {
            Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
            if (vehicle != null) {
                request.setAttribute("vehicle", vehicle);
            }
        }
        
        // Set default dates
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);
        
        request.setAttribute("minStartDate", today.format(DATE_FORMATTER));
        request.setAttribute("defaultStartDate", tomorrow.format(DATE_FORMATTER));
        request.setAttribute("defaultEndDate", dayAfterTomorrow.format(DATE_FORMATTER));
        
        // Forward to the booking creation page
        request.getRequestDispatcher("/booking/create.jsp").forward(request, response);
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
        String vehicleId = ValidationUtil.sanitizeString(request.getParameter("vehicleId"));
        String startDateStr = ValidationUtil.sanitizeString(request.getParameter("startDate"));
        String endDateStr = ValidationUtil.sanitizeString(request.getParameter("endDate"));
        
        // Validate input
        boolean isValid = true;
        
        // Vehicle validation
        if (vehicleId == null || vehicleId.isEmpty()) {
            request.setAttribute("vehicleIdError", "Vehicle selection is required");
            isValid = false;
        } else {
            Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
            if (vehicle == null) {
                request.setAttribute("vehicleIdError", "Invalid vehicle selection");
                isValid = false;
            } else if (!vehicle.isAvailable()) {
                request.setAttribute("vehicleIdError", "Selected vehicle is not available for rental");
                isValid = false;
            } else {
                request.setAttribute("vehicle", vehicle);
            }
        }
        
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
        
        // Check if vehicle is available for the selected dates
        if (isValid && !bookingDAO.isVehicleAvailable(vehicleId, startDate, endDate, null)) {
            request.setAttribute("dateRangeError", "Vehicle is not available for the selected dates");
            isValid = false;
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            // Set default dates for the form
            LocalDate today = LocalDate.now();
            request.setAttribute("minStartDate", today.format(DATE_FORMATTER));
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            
            request.getRequestDispatcher("/booking/create.jsp").forward(request, response);
            return;
        }
        
        // Create a new booking
        Booking booking = new Booking();
        booking.setId(FileUtil.generateUniqueId());
        booking.setUserId(userId);
        booking.setVehicleId(vehicleId);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus("PENDING");
        booking.setBookingDate(LocalDate.now());
        
        // Calculate total cost
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        booking.calculateTotalCost(vehicle.getDailyRate());
        
        // Save to the data store
        boolean success = bookingDAO.addBooking(booking);
        
        if (success) {
            // Mark vehicle as unavailable
            vehicle.setAvailable(false);
            vehicleDAO.updateVehicle(vehicle);
            
            // Redirect to the booking details page
            response.sendRedirect(request.getContextPath() + "/bookings/details?id=" + booking.getId());
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to create booking. Please try again.");
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("vehicle", vehicle);
            
            // Set default dates for the form
            LocalDate today = LocalDate.now();
            request.setAttribute("minStartDate", today.format(DATE_FORMATTER));
            
            request.getRequestDispatcher("/booking/create.jsp").forward(request, response);
        }
    }
}
