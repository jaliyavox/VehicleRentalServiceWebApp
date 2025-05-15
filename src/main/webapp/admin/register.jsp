<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Registration - Vehicle Rental System</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Admin Registration</h4>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                ${errorMessage}
                            </div>
                        </c:if>
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success" role="alert">
                                ${successMessage}
                            </div>
                        </c:if>
                        
                        <form action="${pageContext.request.contextPath}/admin/register" method="post">
                            <div class="mb-3">
                                <label for="username" class="form-label">Username</label>
                                <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" 
                                    id="username" name="username" value="${username}" required>
                                <div class="invalid-feedback">${usernameError}</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="password" class="form-label">Password</label>
                                <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                                    id="password" name="password" required>
                                <div class="invalid-feedback">${passwordError}</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Confirm Password</label>
                                <input type="password" class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}" 
                                    id="confirmPassword" name="confirmPassword" required>
                                <div class="invalid-feedback">${confirmPasswordError}</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="fullName" class="form-label">Full Name</label>
                                <input type="text" class="form-control ${not empty fullNameError ? 'is-invalid' : ''}" 
                                    id="fullName" name="fullName" value="${fullName}" required>
                                <div class="invalid-feedback">${fullNameError}</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                    id="email" name="email" value="${email}" required>
                                <div class="invalid-feedback">${emailError}</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="role" class="form-label">Role</label>
                                <select class="form-select ${not empty roleError ? 'is-invalid' : ''}" 
                                    id="role" name="role">
                                    <option value="ADMIN" ${role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                                    <option value="SUPER_ADMIN" ${role == 'SUPER_ADMIN' ? 'selected' : ''}>Super Admin</option>
                                </select>
                                <div class="invalid-feedback">${roleError}</div>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">Register</button>
                                <a href="${pageContext.request.contextPath}/admin/login" class="btn btn-outline-secondary">Back to Login</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>