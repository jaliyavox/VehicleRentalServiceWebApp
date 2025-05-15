<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Server Error" />
</jsp:include>

<div class="container text-center py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="error-template">
                <h1 class="display-1">500</h1>
                <h2>Internal Server Error</h2>
                <div class="error-details mb-4">
                    Sorry, something went wrong on our end. We're working to fix the issue.
                </div>
                <div class="error-actions">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">
                        <i class="fas fa-home me-2"></i> Back to Home
                    </a>
                    <a href="${pageContext.request.contextPath}/contact" class="btn btn-secondary btn-lg ms-3">
                        <i class="fas fa-envelope me-2"></i> Contact Support
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />