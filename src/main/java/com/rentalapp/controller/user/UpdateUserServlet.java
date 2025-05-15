package com.rentalapp.controller.user;

import com.rentalapp.dao.UserDAO;
import com.rentalapp.model.User;
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
 * Servlet for updating user profiles.
 */
@javax.servlet.annotation.WebServlet("/profile/edit")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UpdateUserServlet.class.getName());
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
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
        User user = userDAO.getUserById(userId);
        
        if (user == null) {
            // This should not happen unless the user was deleted during the session
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Set the user as an attribute for the JSP
        request.setAttribute("user", user);
        
        // Forward to the profile edit page
        request.getRequestDispatcher("/profile/edit.jsp").forward(request, response);
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
        User user = userDAO.getUserById(userId);
        
        if (user == null) {
            // This should not happen unless the user was deleted during the session
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get form parameters
        String fullName = ValidationUtil.sanitizeString(request.getParameter("fullName"));
        String email = ValidationUtil.sanitizeString(request.getParameter("email"));
        String phone = ValidationUtil.sanitizeString(request.getParameter("phone"));
        String address = ValidationUtil.sanitizeString(request.getParameter("address"));
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        boolean isValid = true;
        boolean isChangingPassword = currentPassword != null && !currentPassword.isEmpty();
        
        // Full name validation
        if (fullName == null || fullName.isEmpty()) {
            request.setAttribute("fullNameError", "Full name is required");
            isValid = false;
        }
        
        // Email validation
        if (email == null || email.isEmpty()) {
            request.setAttribute("emailError", "Email is required");
            isValid = false;
        } else if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("emailError", "Invalid email format");
            isValid = false;
        }
        
        // Phone validation
        if (phone == null || phone.isEmpty()) {
            request.setAttribute("phoneError", "Phone number is required");
            isValid = false;
        } else if (!ValidationUtil.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Invalid phone number format");
            isValid = false;
        }
        
        // Address validation
        if (address == null || address.isEmpty()) {
            request.setAttribute("addressError", "Address is required");
            isValid = false;
        }
        
        // Password validation if user is changing it
        if (isChangingPassword) {
            // Validate current password
            if (!user.authenticate(currentPassword)) {
                request.setAttribute("currentPasswordError", "Current password is incorrect");
                isValid = false;
            }
            
            // Validate new password
            if (newPassword == null || newPassword.isEmpty()) {
                request.setAttribute("newPasswordError", "New password is required");
                isValid = false;
            } else if (!ValidationUtil.isValidPassword(newPassword)) {
                request.setAttribute("newPasswordError", "Password must be at least 8 characters and contain at least one letter and one number");
                isValid = false;
            }
            
            // Validate password confirmation
            if (confirmPassword == null || confirmPassword.isEmpty()) {
                request.setAttribute("confirmPasswordError", "Please confirm your new password");
                isValid = false;
            } else if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("confirmPasswordError", "Passwords do not match");
                isValid = false;
            }
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("user", user);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            
            request.getRequestDispatcher("/profile/edit.jsp").forward(request, response);
            return;
        }
        
        // Update the user object
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        
        // Update password if requested
        if (isChangingPassword) {
            user.setPassword(newPassword);
        }
        
        // Save to the data store
        boolean success = userDAO.updateUser(user);
        
        if (success) {
            // Update session attributes
            session.setAttribute("fullName", user.getFullName());
            
            // Redirect to profile view with a success message
            session.setAttribute("successMessage", "Profile updated successfully");
            response.sendRedirect(request.getContextPath() + "/profile");
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to update profile. Please try again.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/profile/edit.jsp").forward(request, response);
        }
    }
}
