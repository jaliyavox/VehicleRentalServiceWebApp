<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Page Not Found" />
</jsp:include>

<div class="container text-center py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="error-template">
                <h1 class="display-1">404</h1>
                <h2>Page Not Found</h2>
                <div class="error-details mb-4">
                    Sorry, the page you requested could not be found.
                </div>
                <div class="error-actions">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg">
                        <i class="fas fa-home me-2"></i> Back to Home
                    </a>
                    <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-secondary btn-lg ms-3">
                        <i class="fas fa-car me-2"></i> Browse Vehicles
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />