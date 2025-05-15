<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Payment Confirmation" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0"><i class="fas fa-check-circle me-2"></i> Payment Submitted Successfully</h4>
                </div>
                <div class="card-body">
                    <div class="text-center mb-4">
                        <div class="display-1 text-success mb-3">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <h2>Thank You!</h2>
                        <p class="lead">Your payment has been submitted and is pending approval.</p>
                    </div>
                    
                    <div class="payment-details border p-3 rounded mb-4">
                        <h5 class="border-bottom pb-2 mb-3">Payment Details</h5>
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Payment ID:</strong> ${payment.id}</p>
                                <p><strong>Booking ID:</strong> ${payment.bookingId}</p>
                                <p><strong>Date:</strong> 
                                    <fmt:formatDate value="${payment.paymentDate}" pattern="MMM dd, yyyy HH:mm" />
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Amount:</strong> 
                                    <span class="text-success fw-bold">
                                        $<fmt:formatNumber value="${payment.amount}" pattern="#,##0.00" />
                                    </span>
                                </p>
                                <p><strong>Method:</strong> ${payment.paymentMethod}</p>
                                <p><strong>Status:</strong> 
                                    <span class="badge bg-warning">Pending Approval</span>
                                </p>
                            </div>
                        </div>
                    </div>
                    
                    <div class="alert alert-info" role="alert">
                        <h5 class="alert-heading"><i class="fas fa-info-circle me-2"></i> What happens next?</h5>
                        <p>Our administrators will verify your payment and update your booking status accordingly. This typically takes 1-2 business days.</p>
                        <hr>
                        <p class="mb-0">You will receive an email notification once your payment has been approved. You can also check the status of your booking anytime from the bookings page.</p>
                    </div>
                    
                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/bookings" class="btn btn-primary me-2">
                            <i class="fas fa-list me-2"></i> View My Bookings
                        </a>
                        <a href="${pageContext.request.contextPath}/bookings/details?id=${payment.bookingId}" class="btn btn-outline-primary">
                            <i class="fas fa-eye me-2"></i> View Booking Details
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />