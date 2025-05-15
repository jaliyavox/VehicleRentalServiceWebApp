<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Register" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0"><i class="fas fa-user-plus me-2"></i> User Registration</h4>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/register" method="post">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="username" class="form-label required">Username</label>
                            <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" 
                                   id="username" name="username" value="${username}" required>
                            <c:if test="${not empty usernameError}">
                                <div class="invalid-feedback">
                                    ${usernameError}
                                </div>
                            </c:if>
                            <div class="form-text">
                                Username must be 4-20 characters and contain only letters and numbers.
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label for="fullName" class="form-label required">Full Name</label>
                            <input type="text" class="form-control ${not empty fullNameError ? 'is-invalid' : ''}" 
                                   id="fullName" name="fullName" value="${fullName}" required>
                            <c:if test="${not empty fullNameError}">
                                <div class="invalid-feedback">
                                    ${fullNameError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="password" class="form-label required">Password</label>
                            <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                                   id="password" name="password" required>
                            <c:if test="${not empty passwordError}">
                                <div class="invalid-feedback">
                                    ${passwordError}
                                </div>
                            </c:if>
                            <div class="form-text">
                                Password must be at least 8 characters and contain at least one letter and one number.
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label for="confirmPassword" class="form-label required">Confirm Password</label>
                            <input type="password" class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}" 
                                   id="confirmPassword" name="confirmPassword" required>
                            <c:if test="${not empty confirmPasswordError}">
                                <div class="invalid-feedback">
                                    ${confirmPasswordError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="email" class="form-label required">Email</label>
                            <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                   id="email" name="email" value="${email}" required>
                            <c:if test="${not empty emailError}">
                                <div class="invalid-feedback">
                                    ${emailError}
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label for="phone" class="form-label required">Phone Number</label>
                            <input type="tel" class="form-control ${not empty phoneError ? 'is-invalid' : ''}" 
                                   id="phone" name="phone" value="${phone}" required>
                            <c:if test="${not empty phoneError}">
                                <div class="invalid-feedback">
                                    ${phoneError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="address" class="form-label required">Address</label>
                        <textarea class="form-control ${not empty addressError ? 'is-invalid' : ''}" 
                                  id="address" name="address" rows="3" required>${address}</textarea>
                        <c:if test="${not empty addressError}">
                            <div class="invalid-feedback">
                                ${addressError}
                            </div>
                        </c:if>
                    </div>
                    
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-user-plus me-2"></i> Register
                        </button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center">
                <p class="mb-0">Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a></p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
