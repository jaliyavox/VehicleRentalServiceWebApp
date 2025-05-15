package com.rentalapp.controller.admin;

import com.rentalapp.dao.BookingDAO;
import com.rentalapp.dao.PaymentDAO;
import com.rentalapp.dao.UserDAO;
import com.rentalapp.dao.VehicleDAO;
import com.rentalapp.model.Booking;
import com.rentalapp.model.Payment;
import com.rentalapp.model.User;
import com.rentalapp.model.Vehicle;

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
 * Servlet for displaying the admin dashboard.
 */
@javax.servlet.annotation.WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardServlet.class.getName());
    
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;
    private PaymentDAO paymentDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        bookingDAO = new BookingDAO();
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        paymentDAO = new PaymentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get counts for the dashboard
        int totalUsers = userDAO.getAllUsers().size();
        int totalVehicles = vehicleDAO.getAllVehicles().size();
        int availableVehicles = vehicleDAO.getAvailableVehicles().size();
        
        List<Booking> activeBookings = bookingDAO.getActiveBookings();
        int totalActiveBookings = activeBookings.size();
        
        List<Payment> pendingPayments = paymentDAO.getPendingPayments();
        int totalPendingPayments = pendingPayments.size();
        
        // Get recent bookings for the dashboard
        List<Booking> recentBookings = activeBookings;
        if (recentBookings.size() > 5) {
            recentBookings = recentBookings.subList(0, 5);
        }
        
        // Get vehicles with recent bookings
        for (Booking booking : recentBookings) {
            Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
            User user = userDAO.getUserById(booking.getUserId());
            
            request.setAttribute("vehicle_" + booking.getId(), vehicle);
            request.setAttribute("user_" + booking.getId(), user);
        }
        
        // Set attributes for the JSP
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalVehicles", totalVehicles);
        request.setAttribute("availableVehicles", availableVehicles);
        request.setAttribute("totalActiveBookings", totalActiveBookings);
        request.setAttribute("totalPendingPayments", totalPendingPayments);
        request.setAttribute("recentBookings", recentBookings);
        request.setAttribute("pendingPayments", pendingPayments);
        
        // Forward to the admin dashboard page
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}
