package com.rentalapp.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter to protect resources that require administrator authentication.
 * Redirects to admin login page if admin is not logged in.
 */
public class AdminAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuthenticationFilter.class);

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
        
        boolean isAdmin = (session != null && session.getAttribute("isAdmin") != null && 
                           (Boolean) session.getAttribute("isAdmin"));
        
        String adminLoginURI = httpRequest.getContextPath() + "/admin/login";
        boolean isAdminLoginRequest = httpRequest.getRequestURI().equals(adminLoginURI);
        boolean isAdminLoginPage = httpRequest.getRequestURI().endsWith("admin/login.jsp");
        
        if (isAdmin || isAdminLoginRequest || isAdminLoginPage) {
            // Admin is authenticated or accessing admin login page, proceed
            chain.doFilter(request, response);
        } else {
            // Not admin, redirect to admin login page
            logger.info("Unauthorized admin access attempt to {}, redirecting to admin login", 
                        httpRequest.getRequestURI());
            session = httpRequest.getSession();
            session.setAttribute("errorMessage", "Administrator access required");
            httpResponse.sendRedirect(adminLoginURI);
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}