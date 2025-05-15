<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="${sessionScope.isAdmin ? 'All Bookings' : 'My Bookings'}" />
</jsp:include>

<div class="row mb-4">
    <div class="col-md-8">
        <h2><i class="fas fa-bookmark me-2"></i> ${sessionScope.isAdmin ? 'All Bookings' : 'My Bookings'}</h2>
    </div>
    <div class="col-md-4 text-end">
        <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-primary">
            <i class="fas fa-plus me-2"></i> New Booking
        </a>
    </div>
</div>

<!-- Filter Options -->
<div class="card mb-4">
    <div class="card-header bg-light">
        <h5 class="mb-0"><i class="fas fa-filter me-2"></i> Filter Bookings</h5>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/bookings" method="get">
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label for="status" class="form-label">Status</label>
                    <select class="form-select" id="status" name="status">
                        <option value="" ${empty statusFilter ? 'selected' : ''}>All Statuses</option>
                        <option value="PENDING" ${statusFilter eq 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="CONFIRMED" ${statusFilter eq 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                        <option value="CANCELLED" ${statusFilter eq 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                        <option value="COMPLETED" ${statusFilter eq 'COMPLETED' ? 'selected' : ''}>Completed</option>
                    </select>
                </div>
                <div class="col-md-8 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary me-2">
                        <i class="fas fa-search me-2"></i> Apply Filter
                    </button>
                    <a href="${pageContext.request.contextPath}/bookings" class="btn btn-secondary">
                        <i class="fas fa-redo me-2"></i> Reset
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Bookings List -->
<div class="booking-list">
    <c:choose>
        <c:when test="${empty bookings}">
            <div class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i> No bookings found.
                <c:if test="${empty statusFilter}">
                    You haven't made any bookings yet.
                    <a href="${pageContext.request.contextPath}/vehicles">Browse vehicles</a> to make a booking.
                </c:if>
                <c:if test="${not empty statusFilter}">
                    No bookings with status "${statusFilter}" found.
                </c:if>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-striped table-hover admin-table">
                    <thead>
                        <tr>
                            <th>Booking ID</th>
                            <c:if test="${sessionScope.isAdmin}">
                                <th>Customer</th>
                            </c:if>
                            <th>Vehicle</th>
                            <th>Dates</th>
                            <th>Status</th>
                            <th>Total Cost</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="booking" items="${bookings}">
                            <c:set var="vehicle" value="${requestScope['vehicle_'.concat(booking.id)]}" />
                            <c:set var="user" value="${requestScope['user_'.concat(booking.id)]}" />
                            
                            <tr>
                                <td>${booking.id.substring(0, 8)}...</td>
                                <c:if test="${sessionScope.isAdmin}">
                                    <td>
                                        <c:if test="${not empty user}">
                                            ${user.fullName}
                                        </c:if>
                                    </td>
                                </c:if>
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
                                    <span class="booking-status ${booking.status.toLowerCase()}">
                                        ${booking.status}
                                    </span>
                                </td>
                                <td>$<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/bookings/details?id=${booking.id}" 
                                       class="btn btn-sm btn-info" title="View Details">
                                        <i class="fas fa-eye"></i> Details
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/includes/footer.jsp" />
