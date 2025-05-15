package com.rentalapp.controller.vehicle;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet for deleting a vehicle from the system.
 */
@javax.servlet.annotation.WebServlet("/admin/vehicles/delete")
public class DeleteVehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DeleteVehicleServlet.class.getName());
    
    private VehicleDAO vehicleDAO;
    private BookingDAO bookingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        vehicleDAO = new VehicleDAO();
        bookingDAO = new BookingDAO();
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
        
        // Check if the vehicle has any active bookings
        List<Booking> vehicleBookings = bookingDAO.getBookingsByVehicle(vehicleId);
        boolean hasActiveBookings = vehicleBookings.stream()
                .anyMatch(b -> !b.getStatus().equalsIgnoreCase("CANCELLED") && 
                               !b.getStatus().equalsIgnoreCase("COMPLETED"));
        
        if (hasActiveBookings) {
            // Cannot delete a vehicle with active bookings
            session.setAttribute("errorMessage", "Cannot delete a vehicle with active bookings");
            response.sendRedirect(request.getContextPath() + "/vehicles");
            return;
        }
        
        // Delete the vehicle
        boolean success = vehicleDAO.deleteVehicle(vehicleId);
        
        if (success) {
            session.setAttribute("successMessage", "Vehicle deleted successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to delete vehicle");
        }
        
        // Redirect to the vehicle list page
        response.sendRedirect(request.getContextPath() + "/vehicles");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Post requests are handled the same way as get requests for this servlet
        doGet(request, response);
    }
}
