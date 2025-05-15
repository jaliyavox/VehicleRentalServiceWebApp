package com.rentalapp.controller.admin;

import com.rentalapp.dao.AdminDAO;
import com.rentalapp.model.Admin;
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
 * Servlet for handling admin login.
 */
@javax.servlet.annotation.WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminLoginServlet.class.getName());
    
    private AdminDAO adminDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        adminDAO = new AdminDAO();
        
        // Create a default admin account if needed
        adminDAO.createDefaultAdminIfNeeded();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminId") != null) {
            // Admin is already logged in, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        // Forward to the admin login page
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String username = ValidationUtil.sanitizeString(request.getParameter("username"));
        String password = request.getParameter("password");
        
        // Validate input
        boolean isValid = true;
        
        if (username == null || username.isEmpty()) {
            request.setAttribute("usernameError", "Username is required");
            isValid = false;
        }
        
        if (password == null || password.isEmpty()) {
            request.setAttribute("passwordError", "Password is required");
            isValid = false;
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("username", username);
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate the admin
        Admin admin = adminDAO.authenticateAdmin(username, password);
        
        if (admin != null) {
            // Create a new session and store admin details
            HttpSession session = request.getSession(true);
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("adminUsername", admin.getUsername());
            session.setAttribute("adminFullName", admin.getFullName());
            session.setAttribute("isAdmin", true);
            
            // Set session timeout to 30 minutes
            session.setMaxInactiveInterval(30 * 60);
            
            // Redirect to the admin dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            // Authentication failed, redisplay the form with an error message
            request.setAttribute("errorMessage", "Invalid username or password");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
}
