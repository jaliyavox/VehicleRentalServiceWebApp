<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Admin Dashboard" />
</jsp:include>

<!-- Dashboard Header -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <h2><i class="fas fa-tachometer-alt me-2"></i> Admin Dashboard</h2>
    <div>
        <a href="${pageContext.request.contextPath}/admin/vehicles/add" class="btn btn-success">
            <i class="fas fa-plus me-2"></i> Add New Vehicle
        </a>
    </div>
</div>

<!-- Stats Summary -->
<div class="row mb-4">
    <div class="col-md-3 mb-3">
        <div class="dashboard-stat bg-primary text-white">
            <div class="stat-icon"><i class="fas fa-users"></i></div>
            <div class="stat-value">${totalUsers}</div>
            <div class="stat-label">Total Users</div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="dashboard-stat bg-success text-white">
            <div class="stat-icon"><i class="fas fa-car"></i></div>
            <div class="stat-value">${availableVehicles} / ${totalVehicles}</div>
            <div class="stat-label">Available Vehicles</div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="dashboard-stat bg-info text-white">
            <div class="stat-icon"><i class="fas fa-bookmark"></i></div>
            <div class="stat-value">${totalActiveBookings}</div>
            <div class="stat-label">Active Bookings</div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="dashboard-stat bg-warning text-dark">
            <div class="stat-icon"><i class="fas fa-money-check"></i></div>
            <div class="stat-value">${totalPendingPayments}</div>
            <div class="stat-label">Pending Payments</div>
        </div>
    </div>
</div>

<!-- Pending Payments Section -->
<div class="card mb-4">
    <div class="card-header bg-warning text-dark">
        <h5 class="mb-0"><i class="fas fa-money-check me-2"></i> Pending Payments</h5>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty pendingPayments}">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle me-2"></i> No pending payments to approve.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped table-hover admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Booking</th>
                                <th>Amount</th>
                                <th>Date</th>
                                <th>Method</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="payment" items="${pendingPayments}">
                                <c:set var="booking" value="${requestScope['booking_'.concat(payment.id)]}" />
                                <c:set var="user" value="${requestScope['user_'.concat(payment.id)]}" />
                                <c:set var="vehicle" value="${requestScope['vehicle_'.concat(payment.id)]}" />
                                
                                <tr>
                                    <td>${payment.id.substring(0, 8)}...</td>
                                    <td>
                                        <c:if test="${not empty booking and not empty user and not empty vehicle}">
                                            ${user.fullName} - ${vehicle.make} ${vehicle.model}
                                        </c:if>
                                    </td>
                                    <td>$<fmt:formatNumber value="${payment.amount}" pattern="#,##0.00"/></td>
                                    <td><fmt:formatDate value="${payment.paymentDate.toDate()}" pattern="yyyy-MM-dd"/></td>
                                    <td>${payment.paymentMethod}</td>
                                    <td class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/payments/details?id=${payment.id}" 
                                           class="btn btn-sm btn-info" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/payments/approve?id=${payment.id}" 
                                           class="btn btn-sm btn-success" title="Approve">
                                            <i class="fas fa-check"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/payments/reject?id=${payment.id}" 
                                           class="btn btn-sm btn-danger" title="Reject">
                                            <i class="fas fa-times"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="text-end">
                    <a href="${pageContext.request.contextPath}/payments?status=PENDING" class="btn btn-outline-warning">
                        View All Pending Payments
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Recent Bookings Section -->
<div class="card">
    <div class="card-header bg-info text-white">
        <h5 class="mb-0"><i class="fas fa-bookmark me-2"></i> Recent Bookings</h5>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty recentBookings}">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle me-2"></i> No recent bookings.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped table-hover admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Customer</th>
                                <th>Vehicle</th>
                                <th>Dates</th>
                                <th>Status</th>
                                <th>Amount</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="booking" items="${recentBookings}">
                                <c:set var="vehicle" value="${requestScope['vehicle_'.concat(booking.id)]}" />
                                <c:set var="user" value="${requestScope['user_'.concat(booking.id)]}" />
                                
                                <tr>
                                    <td>${booking.id.substring(0, 8)}...</td>
                                    <td>
                                        <c:if test="${not empty user}">
                                            ${user.fullName}
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${not empty vehicle}">
                                            ${vehicle.make} ${vehicle.model}
                                        </c:if>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${booking.startDate.toDate()}" pattern="yyyy-MM-dd"/> to 
                                        <fmt:formatDate value="${booking.endDate.toDate()}" pattern="yyyy-MM-dd"/>
                                    </td>
                                    <td>
                                        <span class="badge bg-${booking.status eq 'PENDING' ? 'warning' : 
                                                                  (booking.status eq 'CONFIRMED' ? 'primary' : 
                                                                  (booking.status eq 'CANCELLED' ? 'danger' : 'success'))}">
                                            ${booking.status}
                                        </span>
                                    </td>
                                    <td>$<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></td>
                                    <td class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/bookings/details?id=${booking.id}" 
                                           class="btn btn-sm btn-info" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="text-end">
                    <a href="${pageContext.request.contextPath}/bookings" class="btn btn-outline-info">
                        View All Bookings
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
