package com.rentalapp.controller.admin;

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
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet for managing users in the admin panel.
 */
@javax.servlet.annotation.WebServlet({"/admin/users", "/admin/users/edit", "/admin/users/delete"})
public class ManageUsersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ManageUsersServlet.class.getName());
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/admin/users".equals(path)) {
            // List all users
            handleUserList(request, response);
        } else if ("/admin/users/edit".equals(path)) {
            // Edit a user
            handleUserEdit(request, response);
        } else if ("/admin/users/delete".equals(path)) {
            // Delete a user
            handleUserDelete(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/admin/users/edit".equals(path)) {
            // Process user edit form
            handleUserEditSubmit(request, response);
        } else if ("/admin/users/delete".equals(path)) {
            // Process user delete form
            handleUserDeleteSubmit(request, response);
        }
    }
    
    /**
     * Handles the listing of users.
     */
    private void handleUserList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get all users
        List<User> users = userDAO.getAllUsers();
        
        // Set users as an attribute for the JSP
        request.setAttribute("users", users);
        
        // Forward to the user list page
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
    
    /**
     * Handles the user edit form display.
     */
    private void handleUserEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get user ID from request
        String userId = request.getParameter("id");
        
        if (userId == null || userId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Get the user
        User user = userDAO.getUserById(userId);
        
        if (user == null) {
            HttpSession session = request.getSession(false);
            session.setAttribute("errorMessage", "User not found");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Set the user as an attribute for the JSP
        request.setAttribute("user", user);
        
        // Forward to the user edit page
        request.getRequestDispatcher("/admin/user-edit.jsp").forward(request, response);
    }
    
    /**
     * Handles the user edit form submission.
     */
    private void handleUserEditSubmit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Get form parameters
        String userId = request.getParameter("id");
        String fullName = ValidationUtil.sanitizeString(request.getParameter("fullName"));
        String email = ValidationUtil.sanitizeString(request.getParameter("email"));
        String phone = ValidationUtil.sanitizeString(request.getParameter("phone"));
        String address = ValidationUtil.sanitizeString(request.getParameter("address"));
        String role = ValidationUtil.sanitizeString(request.getParameter("role"));
        String newPassword = request.getParameter("newPassword");
        
        // Get the user
        User user = userDAO.getUserById(userId);
        
        if (user == null) {
            session.setAttribute("errorMessage", "User not found");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Validate input
        boolean isValid = true;
        
        if (fullName == null || fullName.isEmpty()) {
            request.setAttribute("fullNameError", "Full name is required");
            isValid = false;
        }
        
        if (email == null || email.isEmpty()) {
            request.setAttribute("emailError", "Email is required");
            isValid = false;
        } else if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("emailError", "Invalid email format");
            isValid = false;
        }
        
        if (phone == null || phone.isEmpty()) {
            request.setAttribute("phoneError", "Phone number is required");
            isValid = false;
        } else if (!ValidationUtil.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Invalid phone number format");
            isValid = false;
        }
        
        if (address == null || address.isEmpty()) {
            request.setAttribute("addressError", "Address is required");
            isValid = false;
        }
        
        if (role == null || role.isEmpty() || (!role.equals("USER") && !role.equals("ADMIN"))) {
            request.setAttribute("roleError", "Valid role is required (USER or ADMIN)");
            isValid = false;
        }
        
        // Validate new password if provided
        if (newPassword != null && !newPassword.isEmpty() && !ValidationUtil.isValidPassword(newPassword)) {
            request.setAttribute("newPasswordError", "Password must be at least 8 characters and contain at least one letter and one number");
            isValid = false;
        }
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("user", user);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("role", role);
            
            request.getRequestDispatcher("/admin/user-edit.jsp").forward(request, response);
            return;
        }
        
        // Update the user object
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        
        // Update password if provided
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        
        // Save to the data store
        boolean success = userDAO.updateUser(user);
        
        if (success) {
            // Redirect to the user list page with a success message
            session.setAttribute("successMessage", "User updated successfully");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Failed to update user. Please try again.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/admin/user-edit.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles the user delete confirmation page.
     */
    private void handleUserDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get user ID from request
        String userId = request.getParameter("id");
        
        if (userId == null || userId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Get the user
        User user = userDAO.getUserById(userId);
        
        if (user == null) {
            HttpSession session = request.getSession(false);
            session.setAttribute("errorMessage", "User not found");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Set the user as an attribute for the JSP
        request.setAttribute("user", user);
        
        // Forward to the user delete confirmation page
        request.getRequestDispatcher("/admin/user-delete.jsp").forward(request, response);
    }
    
    /**
     * Handles the user delete form submission.
     */
    private void handleUserDeleteSubmit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Get form parameters
        String userId = request.getParameter("id");
        String confirmed = request.getParameter("confirmed");
        
        if (userId == null || userId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Check if the deletion was confirmed
        if (!"true".equals(confirmed)) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Check if user is trying to delete themselves
        String adminId = (String) session.getAttribute("adminId");
        if (userId.equals(adminId)) {
            session.setAttribute("errorMessage", "You cannot delete your own admin account");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        // Delete the user
        boolean success = userDAO.deleteUser(userId);
        
        if (success) {
            session.setAttribute("successMessage", "User deleted successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to delete user. The user may have active bookings.");
        }
        
        // Redirect to the user list page
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
