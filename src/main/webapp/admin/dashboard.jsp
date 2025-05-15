<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Admin Dashboard" />
</jsp:include>

<!-- Admin Dashboard Header -->
<div class="container-fluid bg-dark text-white py-3">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1><i class="fas fa-tachometer-alt me-2"></i> Admin Dashboard</h1>
                <p class="lead mb-0">Manage your vehicle rental business</p>
            </div>
            <div class="col-md-4 text-md-end">
                <span>Welcome, ${adminUsername}</span>
                <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-outline-light ms-3">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Dashboard Content -->
<div class="container my-4">
    <!-- Statistics Cards -->
    <div class="row mb-4">
        <div class="col-md-4 col-xl-3 mb-3">
            <div class="card bg-primary text-white h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Vehicles</h6>
                            <h2 class="my-2">${vehicleCount}</h2>
                            <p class="mb-0">Available for rent</p>
                        </div>
                        <i class="fas fa-car fa-3x opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer d-flex align-items-center justify-content-between">
                    <a href="${pageContext.request.contextPath}/admin/vehicles" class="text-white text-decoration-none">
                        Manage Vehicles
                    </a>
                    <i class="fas fa-arrow-circle-right text-white"></i>
                </div>
            </div>
        </div>
        
        <div class="col-md-4 col-xl-3 mb-3">
            <div class="card bg-success text-white h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Bookings</h6>
                            <h2 class="my-2">${bookingCount}</h2>
                            <p class="mb-0">Total bookings</p>
                        </div>
                        <i class="fas fa-calendar-check fa-3x opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer d-flex align-items-center justify-content-between">
                    <a href="${pageContext.request.contextPath}/admin/bookings" class="text-white text-decoration-none">
                        View Bookings
                    </a>
                    <i class="fas fa-arrow-circle-right text-white"></i>
                </div>
            </div>
        </div>
        
        <div class="col-md-4 col-xl-3 mb-3">
            <div class="card bg-warning text-dark h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Payments</h6>
                            <h2 class="my-2">${pendingPaymentCount}</h2>
                            <p class="mb-0">Pending approvals</p>
                        </div>
                        <i class="fas fa-credit-card fa-3x opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer d-flex align-items-center justify-content-between">
                    <a href="${pageContext.request.contextPath}/admin/payments" class="text-dark text-decoration-none">
                        Approve Payments
                    </a>
                    <i class="fas fa-arrow-circle-right text-dark"></i>
                </div>
            </div>
        </div>
        
        <div class="col-md-4 col-xl-3 mb-3">
            <div class="card bg-info text-white h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-0">Reviews</h6>
                            <h2 class="my-2">${reviewCount}</h2>
                            <p class="mb-0">Customer reviews</p>
                        </div>
                        <i class="fas fa-star fa-3x opacity-50"></i>
                    </div>
                </div>
                <div class="card-footer d-flex align-items-center justify-content-between">
                    <a href="${pageContext.request.contextPath}/admin/reviews" class="text-white text-decoration-none">
                        Manage Reviews
                    </a>
                    <i class="fas fa-arrow-circle-right text-white"></i>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Quick Actions -->
    <div class="row mb-4">
        <div class="col-md-12">
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-bolt me-2"></i> Quick Actions</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3 mb-3 mb-md-0">
                            <a href="${pageContext.request.contextPath}/admin/vehicles/add" class="btn btn-primary d-block">
                                <i class="fas fa-plus-circle me-2"></i> Add New Vehicle
                            </a>
                        </div>
                        <div class="col-md-3 mb-3 mb-md-0">
                            <a href="${pageContext.request.contextPath}/admin/payments" class="btn btn-warning d-block">
                                <i class="fas fa-check-circle me-2"></i> Approve Payments
                            </a>
                        </div>
                        <div class="col-md-3 mb-3 mb-md-0">
                            <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-success d-block">
                                <i class="fas fa-chart-line me-2"></i> View Reports
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-info d-block">
                                <i class="fas fa-users me-2"></i> Manage Users
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Recent Activities -->
    <div class="row">
        <!-- Recent Bookings -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-calendar-alt me-2"></i> Recent Bookings</h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Booking ID</th>
                                    <th>User</th>
                                    <th>Vehicle</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty recentBookings}">
                                    <tr>
                                        <td colspan="5" class="text-center py-3">No bookings found</td>
                                    </tr>
                                </c:if>
                                <c:forEach items="${recentBookings}" var="booking">
                                    <tr>
                                        <td><small class="text-muted">${booking.id.substring(0, 8)}</small></td>
                                        <td>${booking.userName}</td>
                                        <td>${booking.vehicleName}</td>
                                        <td>
                                            <span class="badge bg-${booking.status eq 'confirmed' ? 'success' : booking.status eq 'pending' ? 'warning' : booking.status eq 'cancelled' ? 'danger' : 'secondary'}">
                                                ${booking.status}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/bookings/view?id=${booking.id}" class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer bg-white text-end">
                    <a href="${pageContext.request.contextPath}/admin/bookings" class="text-decoration-none">
                        View All Bookings <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
        
        <!-- Recent Reviews -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-white">
                    <h5 class="mb-0"><i class="fas fa-star me-2"></i> Recent Reviews</h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>User</th>
                                    <th>Vehicle</th>
                                    <th>Rating</th>
                                    <th>Comment</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty recentReviews}">
                                    <tr>
                                        <td colspan="5" class="text-center py-3">No reviews found</td>
                                    </tr>
                                </c:if>
                                <c:forEach items="${recentReviews}" var="review">
                                    <tr>
                                        <td>${review.userName}</td>
                                        <td>${review.vehicleName}</td>
                                        <td>
                                            <div class="text-warning">
                                                <c:forEach begin="1" end="5" var="i">
                                                    <i class="fas fa-star${i <= review.rating ? '' : '-o'}"></i>
                                                </c:forEach>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="d-inline-block text-truncate" style="max-width: 150px;">
                                                ${review.comment}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/reviews/view?id=${review.id}" class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer bg-white text-end">
                    <a href="${pageContext.request.contextPath}/admin/reviews" class="text-decoration-none">
                        View All Reviews <i class="fas fa-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />