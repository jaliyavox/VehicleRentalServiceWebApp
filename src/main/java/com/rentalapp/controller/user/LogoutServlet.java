package com.rentalapp.controller.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet for handling user logout.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session (if it exists)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Log the user out
            String username = (String) session.getAttribute("username");
            LOGGER.info("User logged out: " + (username != null ? username : "unknown"));
            
            // Invalidate the session
            session.invalidate();
        }
        
        // Redirect to the login page with a logout message
        response.sendRedirect(request.getContextPath() + "/login?logout=true");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Post requests are handled the same way as get requests for this servlet
        doGet(request, response);
    }
}
