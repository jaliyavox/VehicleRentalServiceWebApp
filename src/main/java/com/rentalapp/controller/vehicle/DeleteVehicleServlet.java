package com.rentalapp.controller.vehicle;

import com.rentalapp.dao.VehicleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to handle vehicle deletion by admin
 */
@WebServlet("/admin/vehicles/delete")
public class DeleteVehicleServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteVehicleServlet.class);
    private VehicleDAO vehicleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleDAO = new VehicleDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is authenticated as admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("isAdmin") == null || 
                !(Boolean)session.getAttribute("isAdmin")) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get vehicle ID from request
        String vehicleId = request.getParameter("id");
        
        if (vehicleId == null || vehicleId.isEmpty()) {
            session.setAttribute("errorMessage", "Vehicle ID is required");
            response.sendRedirect(request.getContextPath() + "/admin/vehicles");
            return;
        }
        
        try {
            boolean deleted = vehicleDAO.deleteVehicle(vehicleId);
            
            if (deleted) {
                logger.info("Vehicle deleted successfully: {}", vehicleId);
                session.setAttribute("successMessage", "Vehicle deleted successfully");
            } else {
                logger.warn("Failed to delete vehicle: {}", vehicleId);
                session.setAttribute("errorMessage", "Failed to delete vehicle. It may have been already deleted or doesn't exist.");
            }
            
        } catch (Exception e) {
            logger.error("Error deleting vehicle: {}", vehicleId, e);
            session.setAttribute("errorMessage", "An error occurred while deleting the vehicle. Please try again.");
        }
        
        // Redirect back to vehicle management page
        response.sendRedirect(request.getContextPath() + "/admin/vehicles");
    }
}