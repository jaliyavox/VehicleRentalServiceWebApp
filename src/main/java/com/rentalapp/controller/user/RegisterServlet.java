package com.rentalapp.controller.user;

import com.rentalapp.dao.UserDAO;
import com.rentalapp.model.User;
import com.rentalapp.util.FileUtil;
import com.rentalapp.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * Servlet for handling user registration.
 */
@javax.servlet.annotation.WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the registration page
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String username = ValidationUtil.sanitizeString(request.getParameter("username"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = ValidationUtil.sanitizeString(request.getParameter("fullName"));
        String email = ValidationUtil.sanitizeString(request.getParameter("email"));
        String phone = ValidationUtil.sanitizeString(request.getParameter("phone"));
        String address = ValidationUtil.sanitizeString(request.getParameter("address"));
        
        // Validate input
        boolean isValid = true;
        
        // Username validation
        if (username == null || username.isEmpty()) {
            request.setAttribute("usernameError", "Username is required");
            isValid = false;
        } else if (!ValidationUtil.isValidUsername(username)) {
            request.setAttribute("usernameError", "Username must be 4-20 characters and contain only letters and numbers");
            isValid = false;
        } else if (userDAO.isUsernameTaken(username)) {
            request.setAttribute("usernameError", "Username is already taken");
            isValid = false;
        }
        
        // Password validation
        if (password == null || password.isEmpty()) {
            request.setAttribute("passwordError", "Password is required");
            isValid = false;
        } else if (!ValidationUtil.isValidPassword(password)) {
            request.setAttribute("passwordError", "Password must be at least 8 characters and contain at least one letter and one number");
            isValid = false;
        }
        
        // Confirm password validation
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("confirmPasswordError", "Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Passwords do not match");
            isValid = false;
        }
        
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
        
        // If validation fails, redisplay the form with error messages
        if (!isValid) {
            request.setAttribute("username", username);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Create a new user
        User user = new User();
        user.setId(FileUtil.generateUniqueId());
        user.setUsername(username);
        user.setPassword(password);  // In a real app, this should be hashed
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole("USER");
        user.setRegistrationDate(LocalDate.now());
        
        // Save to the data store
        boolean success = userDAO.addUser(user);
        
        if (success) {
            // Auto-login the user
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("isAdmin", false);
            
            // Redirect to the home page with a success message
            session.setAttribute("successMessage", "Registration successful! Welcome, " + user.getFullName() + ".");
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            // Redisplay the form with an error message
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.setAttribute("username", username);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
