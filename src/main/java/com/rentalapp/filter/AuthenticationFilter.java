package com.rentalapp.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter to protect resources that require user authentication.
 * Redirects to login page if user is not logged in.
 */
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        
        String loginURI = httpRequest.getContextPath() + "/login";
        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
        boolean isLoginPage = httpRequest.getRequestURI().endsWith("login.jsp");
        
        if (isLoggedIn || isLoginRequest || isLoginPage) {
            // User is authenticated or accessing login page, proceed
            chain.doFilter(request, response);
        } else {
            // Not logged in, redirect to login page
            logger.info("Unauthorized access attempt to {}, redirecting to login", httpRequest.getRequestURI());
            // Store the requested URL for redirect after login
            session = httpRequest.getSession();
            session.setAttribute("redirectUrl", httpRequest.getRequestURI());
            session.setAttribute("errorMessage", "Please log in to access this resource");
            httpResponse.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}