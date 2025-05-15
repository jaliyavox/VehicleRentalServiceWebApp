<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="${vehicle.make} ${vehicle.model}" />
</jsp:include>

<div class="row vehicle-details">
    <div class="col-md-8">
        <!-- Vehicle Details -->
        <div class="card mb-4">
            <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                <h3 class="mb-0">${vehicle.make} ${vehicle.model} (${vehicle.year})</h3>
                <span class="badge ${vehicle.available ? 'bg-success' : 'bg-danger'}">${vehicle.available ? 'Available' : 'Unavailable'}</span>
            </div>
            <div class="card-body">
                <div class="row mb-4">
                    <div class="col-12">
                        <c:choose>
                            <c:when test="${not empty vehicle.imageUrl}">
                                <img src="${vehicle.imageUrl}" class="vehicle-image img-fluid w-100" alt="${vehicle.make} ${vehicle.model}">
                            </c:when>
                            <c:otherwise>
                                <div class="vehicle-image d-flex align-items-center justify-content-center bg-light w-100" style="height: 400px;">
                                    <svg width="150" height="150" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg">
                                        <path fill="#6c757d" d="M499.99 176h-59.87l-16.64-41.6C406.38 91.63 365.57 64 319.5 64h-127c-46.06 0-86.88 27.63-103.99 70.4L71.87 176H12.01C4.2 176-1.53 183.34.37 190.91l6 24C7.7 220.25 12.5 224 18.01 224h20.07C24.65 235.73 16 252.78 16 272v48c0 16.12 6.16 30.67 16 41.93V416c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-32h256v32c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-54.07c9.84-11.25 16-25.8 16-41.93v-48c0-19.22-8.65-36.27-22.07-48H494c5.51 0 10.31-3.75 11.64-9.09l6-24c1.89-7.57-3.84-14.91-11.65-14.91zm-352.06-17.83c7.29-18.22 24.94-30.17 44.57-30.17h127c19.63 0 37.28 11.95 44.57 30.17L384 208H128l19.93-49.83zM96 319.8c-19.2 0-32-12.76-32-31.9S76.8 256 96 256s48 28.71 48 47.85-28.8 15.95-48 15.95zm320 0c-19.2 0-48 3.19-48-15.95S396.8 256 416 256s32 12.76 32 31.9-12.8 31.9-32 31.9z"/>
                                    </svg>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="row mt-4">
                    <div class="col-md-6">
                        <h4>Vehicle Specifications</h4>
                        <div class="vehicle-spec"><strong>Type:</strong> ${vehicle.type}</div>
                        <div class="vehicle-spec"><strong>Make:</strong> ${vehicle.make}</div>
                        <div class="vehicle-spec"><strong>Model:</strong> ${vehicle.model}</div>
                        <div class="vehicle-spec"><strong>Year:</strong> ${vehicle.year}</div>
                        <div class="vehicle-spec"><strong>Registration Number:</strong> ${vehicle.registrationNumber}</div>
                    </div>
                    <div class="col-md-6">
                        <h4>Rental Information</h4>
                        <div class="vehicle-price mb-2">
                            <strong>Daily Rate:</strong> $<fmt:formatNumber value="${vehicle.dailyRate}" pattern="#,##0.00"/> / day
                        </div>
                        <div class="vehicle-spec"><strong>Status:</strong> 
                            <span class="badge ${vehicle.available ? 'bg-success' : 'bg-danger'}">
                                ${vehicle.available ? 'Available for Rental' : 'Currently Unavailable'}
                            </span>
                        </div>
                        <div class="vehicle-spec"><strong>Average Rating:</strong>
                            <div class="d-inline-block ms-2">
                                <c:forEach begin="1" end="5" var="star">
                                    <i class="fas fa-star ${star <= averageRating ? 'text-warning' : 'text-muted'}"></i>
                                </c:forEach>
                                (${reviews.size()} reviews)
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="mt-4">
                    <h4>Description</h4>
                    <p>${vehicle.description}</p>
                </div>
                
                <div class="d-flex gap-2 mt-4">
                    <c:if test="${vehicle.available && sessionScope.userId != null}">
                        <a href="${pageContext.request.contextPath}/bookings/create?vehicleId=${vehicle.id}" class="btn btn-success">
                            <i class="fas fa-calendar-plus me-2"></i> Book Now
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-2"></i> Back to Vehicles
                    </a>
                    <c:if test="${sessionScope.isAdmin == true}">
                        <a href="${pageContext.request.contextPath}/admin/vehicles/edit?id=${vehicle.id}" class="btn btn-primary">
                            <i class="fas fa-edit me-2"></i> Edit Vehicle
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/vehicles/delete?id=${vehicle.id}" 
                           class="btn btn-danger delete-confirm">
                            <i class="fas fa-trash me-2"></i> Delete Vehicle
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <!-- Reviews Section -->
        <div class="card mb-4">
            <div class="card-header bg-info text-white d-flex justify-content-between align-items-center">
                <h4 class="mb-0"><i class="fas fa-star me-2"></i> Reviews</h4>
                <span class="badge bg-light text-dark">${reviews.size()} Reviews</span>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty reviews}">
                        <div class="alert alert-info">
                            No reviews yet. Be the first to review this vehicle!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="mb-3">
                            <div class="d-flex align-items-center">
                                <h5 class="mb-0 me-2">Average Rating:</h5>
                                <div>
                                    <c:forEach begin="1" end="5" var="star">
                                        <i class="fas fa-star ${star <= averageRating ? 'text-warning' : 'text-muted'}"></i>
                                    </c:forEach>
                                    <span class="ms-2">${averageRating}/5</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="review-list">
                            <c:forEach var="review" items="${reviews}" varStatus="status">
                                <c:set var="reviewer" value="${requestScope['user_'.concat(review.id)]}" />
                                
                                <div class="review-card ${!status.last ? 'border-bottom pb-3 mb-3' : ''}">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <div class="review-author">
                                            <i class="fas fa-user-circle me-2"></i> 
                                            ${not empty reviewer ? reviewer.fullName : 'Anonymous User'}
                                        </div>
                                        <div class="review-date">
                                            <small><fmt:formatDate value="${review.reviewDate.toDate()}" pattern="yyyy-MM-dd"/></small>
                                        </div>
                                    </div>
                                    <div class="review-rating mb-2">
                                        <c:forEach begin="1" end="5" var="star">
                                            <i class="fas fa-star ${star <= review.rating ? 'text-warning' : 'text-muted'}"></i>
                                        </c:forEach>
                                    </div>
                                    <div class="review-comment">
                                        ${review.comment}
                                    </div>
                                    <c:if test="${sessionScope.userId == review.userId || sessionScope.isAdmin}">
                                        <div class="mt-2 text-end">
                                            <a href="${pageContext.request.contextPath}/reviews/delete?id=${review.id}" 
                                               class="btn btn-sm btn-outline-danger delete-confirm">
                                                <i class="fas fa-trash-alt"></i> Delete
                                            </a>
                                        </div>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <c:if test="${userCanReview}">
                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/reviews/add?vehicleId=${vehicle.id}" class="btn btn-primary w-100">
                            <i class="fas fa-edit me-2"></i> Write a Review
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
