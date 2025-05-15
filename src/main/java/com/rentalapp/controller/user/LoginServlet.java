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
 * Servlet for handling user login.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // User is already logged in, redirect to home page
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Forward to the login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
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
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate the user
        User user = userDAO.authenticateUser(username, password);
        
        if (user != null) {
            // Create a new session and store user details
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("isAdmin", user.isAdmin());
            
            // Set session timeout to 30 minutes
            session.setMaxInactiveInterval(30 * 60);
            
            // Redirect to the home page
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            // Authentication failed, redisplay the form with an error message
            request.setAttribute("errorMessage", "Invalid username or password");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
