<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Admin Login" />
</jsp:include>

<div class="row justify-content-center my-5">
    <div class="col-md-6">
        <div class="card shadow">
            <div class="card-header bg-dark text-white">
                <h4 class="mb-0"><i class="fas fa-user-shield me-2"></i> Administrator Login</h4>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/admin/login" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                            <input type="text" class="form-control" id="username" name="username" 
                                   value="${username}" required placeholder="Enter admin username">
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password" 
                                   required placeholder="Enter admin password">
                        </div>
                    </div>
                    
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-dark btn-lg">
                            <i class="fas fa-sign-in-alt me-2"></i> Login as Administrator
                        </button>
                    </div>
                </form>
                
                <div class="text-center mt-4">
                    <p>
                        <a href="${pageContext.request.contextPath}/admin/register">Register New Admin</a> | 
                        <a href="${pageContext.request.contextPath}/login">Back to User Login</a> | 
                        <a href="${pageContext.request.contextPath}/">Back to Home</a>
                    </p>
                </div>
            </div>
        </div>
        
        <div class="card mt-4">
            <div class="card-body">
                <h5 class="card-title">Default Admin Credentials</h5>
                <p class="card-text">Username: <code>admin</code><br>Password: <code>admin123</code></p>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle me-2"></i> Change these default credentials after first login for security purposes.
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />