<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Admin Login" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header bg-dark text-white">
                <h4 class="mb-0"><i class="fas fa-user-shield me-2"></i> Administrator Login</h4>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/admin/login" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label required">Username</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                            <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" 
                                   id="username" name="username" value="${username}" required>
                            <c:if test="${not empty usernameError}">
                                <div class="invalid-feedback">
                                    ${usernameError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label required">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                            <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                                   id="password" name="password" required>
                            <c:if test="${not empty passwordError}">
                                <div class="invalid-feedback">
                                    ${passwordError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-dark">
                            <i class="fas fa-sign-in-alt me-2"></i> Login as Administrator
                        </button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center">
                <p class="mb-0">Not an administrator? <a href="${pageContext.request.contextPath}/login">User Login</a></p>
                <p class="mt-2 mb-0 text-muted">
                    <small>Default admin credentials: username: admin, password: admin123</small>
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
