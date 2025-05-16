<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Manage Payments" />
</jsp:include>

<!-- Admin Payments Management Header -->
<div class="container-fluid bg-warning text-dark py-3">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1><i class="fas fa-money-check-alt me-2"></i> Payment Management</h1>
                <p class="lead mb-0">Approve or reject customer payments</p>
            </div>
            <div class="col-md-4 text-md-end">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-dark me-2">
                    <i class="fas fa-tachometer-alt"></i> Dashboard
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Payment Management Content -->
<div class="container my-4">
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    
    <!-- Payment Status Cards -->
    <div class="row mb-4">
        <div class="col-md-3 mb-3">
            <div class="card ${activeFilter == 'all' ? 'border-primary' : ''} h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">All Payments</h6>
                            <h2 class="my-2">${totalCount}</h2>
                        </div>
                        <i class="fas fa-file-invoice-dollar fa-2x text-secondary opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer bg-light">
                    <a href="${pageContext.request.contextPath}/admin/payments" class="text-decoration-none">
                        View All Payments <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card ${activeFilter == 'pending' ? 'border-warning' : ''} h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Pending</h6>
                            <h2 class="my-2">${pendingCount}</h2>
                            <p class="mb-0">Need approval</p>
                        </div>
                        <i class="fas fa-clock fa-2x text-warning opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer bg-light">
                    <a href="${pageContext.request.contextPath}/admin/payments?status=pending" class="text-decoration-none">
                        View Pending <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card ${activeFilter == 'approved' ? 'border-success' : ''} h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Approved</h6>
                            <h2 class="my-2">${approvedCount}</h2>
                            <p class="mb-0">Successful payments</p>
                        </div>
                        <i class="fas fa-check-circle fa-2x text-success opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer bg-light">
                    <a href="${pageContext.request.contextPath}/admin/payments?status=approved" class="text-decoration-none">
                        View Approved <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card ${activeFilter == 'rejected' ? 'border-danger' : ''} h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Rejected</h6>
                            <h2 class="my-2">${rejectedCount}</h2>
                            <p class="mb-0">Failed payments</p>
                        </div>
                        <i class="fas fa-times-circle fa-2x text-danger opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer bg-light">
                    <a href="${pageContext.request.contextPath}/admin/payments?status=rejected" class="text-decoration-none">
                        View Rejected <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Payments List -->
    <div class="card shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
                <i class="fas fa-credit-card me-2"></i> 
                <c:choose>
                    <c:when test="${activeFilter == 'pending'}">Pending Payments</c:when>
                    <c:when test="${activeFilter == 'approved'}">Approved Payments</c:when>
                    <c:when test="${activeFilter == 'rejected'}">Rejected Payments</c:when>
                    <c:otherwise>All Payments</c:otherwise>
                </c:choose>
            </h5>
            <span class="badge bg-primary">${payments.size()} Payments</span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>User</th>
                            <th>Booking ID</th>
                            <th>Amount</th>
                            <th>Method</th>
                            <th>Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty payments}">
                            <tr>
                                <td colspan="8" class="text-center py-4">
                                    <p class="mb-0 text-muted">No payments found.</p>
                                </td>
                            </tr>
                        </c:if>
                        
                        <c:forEach items="${payments}" var="payment">
                            <tr>
                                <td><small class="text-muted">${payment.id.substring(0, 8)}...</small></td>
                                <td>
                                    <c:set var="user" value="${usersMap[payment.userId]}" />
                                    <c:choose>
                                        <c:when test="${not empty user}">
                                            ${user.fullName}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Unknown User</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/bookings/view?id=${payment.bookingId}" class="text-decoration-none">
                                        <small>${payment.bookingId.substring(0, 8)}...</small>
                                    </a>
                                </td>
                                <td>
                                    <span class="badge bg-success">
                                        $<fmt:formatNumber value="${payment.amount}" pattern="#,##0.00" />
                                    </span>
                                </td>
                                <td>${payment.paymentMethod}</td>
                                <td>
                                    <fmt:parseDate value="${payment.paymentDate}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="MM/dd/yyyy HH:mm" />
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${payment.status eq 'PENDING'}">
                                            <span class="badge bg-warning text-dark">Pending</span>
                                        </c:when>
                                        <c:when test="${payment.status eq 'APPROVED'}">
                                            <span class="badge bg-success">Approved</span>
                                        </c:when>
                                        <c:when test="${payment.status eq 'REJECTED'}">
                                            <span class="badge bg-danger">Rejected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${payment.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="btn-group" role="group">
                                        <a href="${pageContext.request.contextPath}/admin/payments/view?id=${payment.id}" class="btn btn-sm btn-outline-primary" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        
                                        <c:if test="${payment.status eq 'PENDING'}">
                                            <button type="button" class="btn btn-sm btn-outline-success" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#approvePaymentModal" 
                                                    data-payment-id="${payment.id}"
                                                    title="Approve">
                                                <i class="fas fa-check"></i>
                                            </button>
                                            
                                            <button type="button" class="btn btn-sm btn-outline-danger" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#rejectPaymentModal" 
                                                    data-payment-id="${payment.id}"
                                                    title="Reject">
                                                <i class="fas fa-times"></i>
                                            </button>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
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
                    
                    <input type="hidden" id="approvePaymentId" name="paymentId" value="">
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
                    
                    <input type="hidden" id="rejectPaymentId" name="paymentId" value="">
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

<script>
    // Handle payment modals
    document.addEventListener('DOMContentLoaded', function() {
        // Approve Payment Modal
        const approveModal = document.getElementById('approvePaymentModal');
        if (approveModal) {
            approveModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const paymentId = button.getAttribute('data-payment-id');
                document.getElementById('approvePaymentId').value = paymentId;
            });
        }
        
        // Reject Payment Modal
        const rejectModal = document.getElementById('rejectPaymentModal');
        if (rejectModal) {
            rejectModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const paymentId = button.getAttribute('data-payment-id');
                document.getElementById('rejectPaymentId').value = paymentId;
            });
        }
    });
</script>

<jsp:include page="/includes/footer.jsp" />