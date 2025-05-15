package com.rentalapp.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter to protect admin-only routes
 */
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
        // Get current URI
        String uri = request.getRequestURI();
        
        // Allow access to login and register pages without authentication
        if (uri.endsWith("/admin/login") || uri.endsWith("/admin/register")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in as admin
        HttpSession session = request.getSession(false);
        Boolean isAdmin = (session != null) ? (Boolean) session.getAttribute("isAdmin") : null;
        
        if (isAdmin != null && isAdmin) {
            // User is authenticated as admin, proceed
            chain.doFilter(request, response);
        } else {
            // Not authenticated as admin, redirect to login
            logger.info("Unauthorized access attempt to admin page: {}", uri);
            response.sendRedirect(request.getContextPath() + "/admin/login");
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup code
    }
}