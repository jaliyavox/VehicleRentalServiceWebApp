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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for registering new administrators
 */
@WebServlet("/admin/register")
public class AdminRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminRegisterServlet.class.getName());
    
    private AdminDAO adminDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        adminDAO = new AdminDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the register page
        request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        
        // Validate input
        boolean isValid = true;
        
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("usernameError", "Username is required");
            isValid = false;
        } else if (username.length() < 3 || username.length() > 20) {
            request.setAttribute("usernameError", "Username must be between 3 and 20 characters");
            isValid = false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("passwordError", "Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            request.setAttribute("passwordError", "Password must be at least 6 characters");
            isValid = false;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("confirmPasswordError", "Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Passwords do not match");
            isValid = false;
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("fullNameError", "Full name is required");
            isValid = false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("emailError", "Email is required");
            isValid = false;
        } else if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("emailError", "Invalid email format");
            isValid = false;
        }
        
        if (role == null || role.trim().isEmpty()) {
            role = "ADMIN"; // Default role
        }
        
        // Check if username is already taken
        if (isValid) {
            boolean usernameExists = adminDAO.getAllAdmins().stream()
                    .anyMatch(admin -> admin.getUsername().equalsIgnoreCase(username));
            
            if (usernameExists) {
                request.setAttribute("usernameError", "Username already exists");
                isValid = false;
            }
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("username", username);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("role", role);
            
            request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
            return;
        }
        
        // Create a new admin
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setFullName(fullName);
        admin.setEmail(email);
        admin.setRole(role);
        
        // Save to the database
        boolean success = adminDAO.addAdmin(admin);
        
        if (success) {
            // Log the admin in
            HttpSession session = request.getSession();
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("adminUsername", admin.getUsername());
            session.setAttribute("adminRole", admin.getRole());
            
            // Redirect to dashboard
            LOGGER.log(Level.INFO, "New admin registered and logged in: {0}", username);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            request.setAttribute("errorMessage", "Failed to register. Please try again.");
            request.getRequestDispatcher("/admin/register.jsp").forward(request, response);
        }
    }
}