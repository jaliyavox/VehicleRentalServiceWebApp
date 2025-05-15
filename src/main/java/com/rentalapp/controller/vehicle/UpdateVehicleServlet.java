package com.rentalapp.controller.vehicle;

import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Vehicle;
import com.rentalapp.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet for updating an existing vehicle in the system.
 */
@javax.servlet.annotation.WebServlet("/admin/vehicles/edit")
public class UpdateVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UpdateVehicleServlet.class.getName());
    
    private VehicleDAO vehicleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleDAO = new VehicleDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is logged in as an admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get the vehicle ID from the request
        String vehicleId = request.getParameter("id");
        
        if (vehicleId == null || vehicleId.isEmpty()) {
            // Redirect to the vehicle list page if no ID is provided
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Get the vehicle from the data store
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        
        if (vehicle == null) {
            // Redirect to the vehicle list page if the vehicle doesn't exist
            session.setAttribute("errorMessage", "Vehicle not found");
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Set the vehicle as an attribute for the JSP
        request.setAttribute("vehicle", vehicle);
        
        // Forward to the edit vehicle form
        request.getRequestDispatcher("/vehicle/edit.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is logged in as an admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get form parameters
        String id = request.getParameter("id");
        String type = ValidationUtil.sanitizeString(request.getParameter("type"));
        String make = ValidationUtil.sanitizeString(request.getParameter("make"));
        String model = ValidationUtil.sanitizeString(request.getParameter("model"));
        String yearStr = ValidationUtil.sanitizeString(request.getParameter("year"));
        String registrationNumber = ValidationUtil.sanitizeString(request.getParameter("registrationNumber"));
        String dailyRateStr = ValidationUtil.sanitizeString(request.getParameter("dailyRate"));
        String availableStr = request.getParameter("available");
        String imageUrl = ValidationUtil.sanitizeString(request.getParameter("imageUrl"));
        String description = ValidationUtil.sanitizeString(request.getParameter("description"));
        
        // Check if the vehicle exists
        Vehicle existingVehicle = vehicleDAO.getVehicleById(id);
        
        if (existingVehicle == null) {
            // Redirect to the vehicle list page if the vehicle doesn't exist
            session.setAttribute("errorMessage", "Vehicle not found");
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Validate input
        boolean isValid = true;
        
        if (type == null || type.isEmpty()) {
            request.setAttribute("typeError", "Vehicle type is required");
            isValid = false;
        }
        
        if (make == null || make.isEmpty()) {
            request.setAttribute("makeError", "Vehicle make is required");
            isValid = false;
        }
        
        if (model == null || model.isEmpty()) {
            request.setAttribute("modelError", "Vehicle model is required");
            isValid = false;
        }
        
        int year = 0;
        if (yearStr == null || yearStr.isEmpty()) {
            request.setAttribute("yearError", "Vehicle year is required");
            isValid = false;
        } else {
            try {
                year = Integer.parseInt(yearStr);
                if (year < 1900 || year > 2100) {
                    request.setAttribute("yearError", "Vehicle year must be between 1900 and 2100");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("yearError", "Vehicle year must be a number");
                isValid = false;
            }
        }
        
        if (registrationNumber == null || registrationNumber.isEmpty()) {
            request.setAttribute("registrationNumberError", "Registration number is required");
            isValid = false;
        }
        
        double dailyRate = 0.0;
        if (dailyRateStr == null || dailyRateStr.isEmpty()) {
            request.setAttribute("dailyRateError", "Daily rate is required");
            isValid = false;
        } else {
            try {
                dailyRate = Double.parseDouble(dailyRateStr);
                if (dailyRate <= 0) {
                    request.setAttribute("dailyRateError", "Daily rate must be greater than zero");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("dailyRateError", "Daily rate must be a number");
                isValid = false;
            }
        }
        
        boolean available = availableStr != null && availableStr.equals("true");
        
        if (description == null || description.isEmpty()) {
            request.setAttribute("descriptionError", "Description is required");
            isValid = false;
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            // Recreate the vehicle object with the submitted data
            Vehicle vehicle = new Vehicle();
            vehicle.setId(id);
            vehicle.setType(type);
            vehicle.setMake(make);
            vehicle.setModel(model);
            vehicle.setYear(year);
            vehicle.setRegistrationNumber(registrationNumber);
            vehicle.setDailyRate(dailyRate);
            vehicle.setAvailable(available);
            vehicle.setImageUrl(imageUrl);
            vehicle.setDescription(description);
            
            request.setAttribute("vehicle", vehicle);
            request.getRequestDispatcher("/vehicle/edit.jsp").forward(request, response);
            return;
        }
        
        // Update the vehicle object
        existingVehicle.setType(type);
        existingVehicle.setMake(make);
        existingVehicle.setModel(model);
        existingVehicle.setYear(year);
        existingVehicle.setRegistrationNumber(registrationNumber);
        existingVehicle.setDailyRate(dailyRate);
        existingVehicle.setAvailable(available);
        existingVehicle.setImageUrl(imageUrl);
        existingVehicle.setDescription(description);
        
        // Save to the data store
        boolean success = vehicleDAO.updateVehicle(existingVehicle);
        
        if (success) {
            // Redirect to the vehicle list page with a success message
            session.setAttribute("successMessage", "Vehicle updated successfully");
            response.sendRedirect(request.getContextPath() + "/vehicles");
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to update vehicle. Please try again.");
            request.setAttribute("vehicle", existingVehicle);
            request.getRequestDispatcher("/vehicle/edit.jsp").forward(request, response);
        }
    }
}
