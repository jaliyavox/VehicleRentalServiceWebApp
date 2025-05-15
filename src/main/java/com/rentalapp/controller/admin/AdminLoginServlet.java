package com.rentalapp.controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rentalapp.dao.AdminDAO;
import com.rentalapp.model.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Servlet to handle admin login functionality
 */
@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin/login"})
public class AdminLoginServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminLoginServlet.class);
    private AdminDAO adminDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        adminDAO = new AdminDAO();
    }
    
    /**
     * Display the admin login form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if already logged in as admin
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("isAdmin") != null 
                && (Boolean)session.getAttribute("isAdmin")) {
            // Already logged in, redirect to admin dashboard
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }
    
    /**
     * Process admin login attempt
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Input validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Username and password are required");
            request.setAttribute("username", username); // Preserve username for form
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Authenticate admin
            Admin admin = adminDAO.authenticate(username, password);
            
            if (admin != null) {
                // Successful login, create admin session
                HttpSession session = request.getSession();
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminUsername", admin.getUsername());
                session.setAttribute("adminRole", admin.getRole());
                session.setAttribute("isAdmin", true);
                
                logger.info("Admin login successful: {}", username);
                
                // Redirect to admin dashboard
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                // Failed login
                logger.warn("Admin login failed for username: {}", username);
                request.setAttribute("errorMessage", "Invalid username or password");
                request.setAttribute("username", username); // Preserve username for form
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            logger.error("Error during admin login", e);
            request.setAttribute("errorMessage", "An error occurred during login. Please try again.");
            request.setAttribute("username", username); // Preserve username for form
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
}