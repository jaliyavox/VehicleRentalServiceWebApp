package com.rentalapp.controller.admin;

import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Vehicle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to handle admin vehicle management
 */
@WebServlet("/admin/vehicles")
public class ManageVehiclesServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ManageVehiclesServlet.class);
    private VehicleDAO vehicleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleDAO = new VehicleDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Get all vehicles
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            
            // Get filter parameters
            String type = request.getParameter("type");
            String make = request.getParameter("make");
            String model = request.getParameter("model");
            String availableOnly = request.getParameter("availableOnly");
            String sortBy = request.getParameter("sortBy");
            String sortOrder = request.getParameter("sortOrder");
            
            boolean filterAvailableOnly = "true".equals(availableOnly);
            
            // Apply filters if provided
            if (type != null || make != null || model != null || filterAvailableOnly) {
                vehicles = vehicleDAO.searchVehicles(type, make, model, filterAvailableOnly);
            }
            
            // Sort vehicles if requested
            if ("price".equals(sortBy)) {
                vehicles = vehicleDAO.sortByDailyRate(vehicles, "asc".equals(sortOrder));
            } else if ("availability".equals(sortBy)) {
                vehicles = vehicleDAO.sortByAvailability(vehicles);
            }
            
            // Get filter options for the form
            List<String> vehicleTypes = vehicleDAO.getVehicleTypes();
            List<String> vehicleMakes = vehicleDAO.getVehicleMakes();
            
            // Set attributes for the JSP
            request.setAttribute("vehicles", vehicles);
            request.setAttribute("vehicleTypes", vehicleTypes);
            request.setAttribute("vehicleMakes", vehicleMakes);
            request.setAttribute("type", type);
            request.setAttribute("make", make);
            request.setAttribute("model", model);
            request.setAttribute("availableOnly", filterAvailableOnly);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);
            
            // Forward to admin vehicle management page
            request.getRequestDispatcher("/admin/vehicles.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error loading vehicles for admin management", e);
            request.setAttribute("errorMessage", "Error loading vehicles. Please try again.");
            request.getRequestDispatcher("/admin/vehicles.jsp").forward(request, response);
        }
    }
}