<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Payment Details" />
</jsp:include>

<!-- Payment Details Header -->
<div class="container-fluid bg-warning text-dark py-3">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1><i class="fas fa-receipt me-2"></i> Payment Details</h1>
                <p class="lead mb-0">View and manage payment information</p>
            </div>
            <div class="col-md-4 text-md-end">
                <a href="${pageContext.request.contextPath}/admin/payments" class="btn btn-outline-dark me-2">
                    <i class="fas fa-arrow-left"></i> Back to Payments
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Payment Details Content -->
<div class="container my-4">
    <div class="row">
        <div class="col-lg-8">
            <!-- Main Payment Information -->
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0">
                        <i class="fas fa-info-circle me-2"></i> Payment Information
                        <c:choose>
                            <c:when test="${payment.status eq 'PENDING'}">
                                <span class="badge bg-warning text-dark float-end">Pending</span>
                            </c:when>
                            <c:when test="${payment.status eq 'APPROVED'}">
                                <span class="badge bg-success float-end">Approved</span>
                            </c:when>
                            <c:when test="${payment.status eq 'REJECTED'}">
                                <span class="badge bg-danger float-end">Rejected</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary float-end">${payment.status}</span>
                            </c:otherwise>
                        </c:choose>
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <p><strong>Payment ID:</strong><br><span class="text-muted">${payment.id}</span></p>
                            <p><strong>Amount:</strong><br><span class="text-success fw-bold">$<fmt:formatNumber value="${payment.amount}" pattern="#,##0.00" /></span></p>
                            <p><strong>Payment Method:</strong><br><span class="text-muted">${payment.paymentMethod}</span></p>
                        </div>
                        <div class="col-md-6">
                            <p>
                                <strong>Payment Date:</strong><br>
                                <span class="text-muted">
                                    <fmt:parseDate value="${payment.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="MMMM dd, yyyy HH:mm" />
                                </span>
                            </p>
                            <c:if test="${not empty payment.processedDate}">
                                <p>
                                    <strong>Processed Date:</strong><br>
                                    <span class="text-muted">
                                        <fmt:parseDate value="${payment.processedDate}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedProcDate" type="both" />
                                        <fmt:formatDate value="${parsedProcDate}" pattern="MMMM dd, yyyy HH:mm" />
                                    </span>
                                </p>
                            </c:if>
                            <p>
                                <strong>Processed By:</strong><br>
                                <span class="text-muted">
                                    <c:choose>
                                        <c:when test="${not empty adminUsername}">
                                            ${adminUsername}
                                        </c:when>
                                        <c:otherwise>
                                            <em>Not processed yet</em>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Payment Notes/Transaction ID -->
                    <div class="mb-4">
                        <h6 class="border-bottom pb-2">Notes/Transaction Information</h6>
                        <c:choose>
                            <c:when test="${not empty payment.notes}">
                                <p>${payment.notes}</p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted fst-italic">No notes or transaction information provided.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- Payment Receipt/Slip Image -->
                    <div>
                        <h6 class="border-bottom pb-2">Payment Receipt/Slip</h6>
                        <c:choose>
                            <c:when test="${not empty payment.slipImagePath}">
                                <div class="text-center mt-3">
                                    <img src="${pageContext.request.contextPath}/${payment.slipImagePath}" 
                                         alt="Payment Receipt" class="img-fluid img-thumbnail" 
                                         style="max-height: 400px;">
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted fst-italic">No payment slip provided.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Action Buttons -->
                <c:if test="${payment.status eq 'PENDING'}">
                    <div class="card-footer bg-white">
                        <div class="d-flex justify-content-end">
                            <button type="button" class="btn btn-success me-2" 
                                    data-bs-toggle="modal" 
                                    data-bs-target="#approvePaymentModal" 
                                    data-payment-id="${payment.id}">
                                <i class="fas fa-check me-2"></i> Approve Payment
                            </button>
                            
                            <button type="button" class="btn btn-danger" 
                                    data-bs-toggle="modal" 
                                    data-bs-target="#rejectPaymentModal" 
                                    data-payment-id="${payment.id}">
                                <i class="fas fa-times me-2"></i> Reject Payment
                            </button>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        
        <div class="col-lg-4">
            <!-- Customer Information -->
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-user me-2"></i> Customer Information</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty user}">
                            <p><strong>Name:</strong><br>${user.fullName}</p>
                            <p><strong>Email:</strong><br>${user.email}</p>
                            <p><strong>Phone:</strong><br>${user.phone != null ? user.phone : 'N/A'}</p>
                            
                            <div class="d-grid mt-3">
                                <a href="${pageContext.request.contextPath}/admin/users/view?id=${user.id}" class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-user me-2"></i> View Customer Profile
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-center text-muted py-3">No customer information available</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Booking Information -->
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-calendar-alt me-2"></i> Booking Information</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty booking}">
                            <p>
                                <strong>Booking ID:</strong><br>
                                <span class="text-muted">${booking.id}</span>
                            </p>
                            <p>
                                <strong>Vehicle:</strong><br>
                                <span class="text-muted">${booking.vehicleName}</span>
                            </p>
                            <p>
                                <strong>Dates:</strong><br>
                                <span class="text-muted">
                                    <fmt:parseDate value="${booking.startDate}" pattern="yyyy-MM-dd" var="startDate" type="date" />
                                    <fmt:parseDate value="${booking.endDate}" pattern="yyyy-MM-dd" var="endDate" type="date" />
                                    <fmt:formatDate value="${startDate}" pattern="MMM dd, yyyy" /> - 
                                    <fmt:formatDate value="${endDate}" pattern="MMM dd, yyyy" />
                                </span>
                            </p>
                            <p>
                                <strong>Status:</strong><br>
                                <span class="badge bg-${booking.status eq 'PAID' ? 'success' : 
                                            booking.status eq 'CONFIRMED' ? 'info' : 
                                            booking.status eq 'PENDING' ? 'warning' : 
                                            booking.status eq 'CANCELLED' ? 'danger' : 'secondary'}">
                                    ${booking.status}
                                </span>
                            </p>
                            <p>
                                <strong>Total Cost:</strong><br>
                                <span class="text-success fw-bold">
                                    $<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00" />
                                </span>
                            </p>
                            
                            <div class="d-grid mt-3">
                                <a href="${pageContext.request.contextPath}/admin/bookings/view?id=${booking.id}" class="btn btn-outline-primary btn-sm">
                                    <i class="fas fa-eye me-2"></i> View Booking Details
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-center text-muted py-3">No booking information available</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Approve Payment Modal -->
<div class="modal fade" id="approvePaymentModal" tabindex="-1" aria-labelledby="approvePaymentModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="approvePaymentModalLabel">
                    <i class="fas fa-check-circle me-2"></i> Approve Payment
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="approvePaymentForm" action="${pageContext.request.contextPath}/admin/payments/process" method="post">
                <div class="modal-body">
                    <p>Are you sure you want to approve this payment?</p>
                    <p>This will mark the payment as approved and update the booking status.</p>
                    
                    <input type="hidden" id="approvePaymentId" name="paymentId" value="${payment.id}">
                    <input type="hidden" name="action" value="approve">
                    
                    <div class="mb-3">
                        <label for="approveNotes" class="form-label">Notes (Optional)</label>
                        <textarea class="form-control" id="approveNotes" name="notes" rows="3" 
                            placeholder="Add any notes or comments about this approval"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">
                        <i class="fas fa-check me-2"></i> Approve Payment
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Reject Payment Modal -->
<div class="modal fade" id="rejectPaymentModal" tabindex="-1" aria-labelledby="rejectPaymentModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="rejectPaymentModalLabel">
                    <i class="fas fa-times-circle me-2"></i> Reject Payment
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="rejectPaymentForm" action="${pageContext.request.contextPath}/admin/payments/process" method="post">
                <div class="modal-body">
                    <p>Are you sure you want to reject this payment?</p>
                    <p class="text-danger">This will mark the payment as rejected. Please provide a reason for rejection.</p>
                    
                    <input type="hidden" id="rejectPaymentId" name="paymentId" value="${payment.id}">
                    <input type="hidden" name="action" value="reject">
                    
                    <div class="mb-3">
                        <label for="rejectNotes" class="form-label">Reason for Rejection*</label>
                        <textarea class="form-control" id="rejectNotes" name="notes" rows="3" required
                            placeholder="Explain why this payment is being rejected"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-times me-2"></i> Reject Payment
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />