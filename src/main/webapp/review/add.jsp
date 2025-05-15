<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Add Review" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0"><i class="fas fa-star me-2"></i> Write a Review</h4>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            ${errorMessage}
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty vehicleIdError}">
                        <div class="alert alert-danger" role="alert">
                            ${vehicleIdError}
                        </div>
                    </c:if>
                    
                    <div class="mb-4">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <c:choose>
                                    <c:when test="${not empty vehicle.imageUrl}">
                                        <img src="${vehicle.imageUrl}" class="rounded" style="width: 120px; height: 90px; object-fit: cover;" alt="${vehicle.make} ${vehicle.model}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="bg-light rounded d-flex align-items-center justify-content-center" style="width: 120px; height: 90px;">
                                            <i class="fas fa-car fa-3x text-secondary"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h5 class="mb-1">${vehicle.make} ${vehicle.model} (${vehicle.year})</h5>
                                <p class="mb-0 text-muted">Type: ${vehicle.type}</p>
                                <p class="mb-0 text-muted">Daily Rate: $${vehicle.dailyRate}</p>
                            </div>
                        </div>
                    </div>
                    
                    <form action="${pageContext.request.contextPath}/reviews/add" method="post">
                        <input type="hidden" name="vehicleId" value="${vehicle.id}">
                        
                        <div class="mb-3">
                            <label class="form-label">Rating*</label>
                            <div class="rating-input">
                                <div class="d-flex">
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating1" value="1" ${rating == '1' ? 'checked' : ''}>
                                        <label class="form-check-label" for="rating1">
                                            <i class="fas fa-star"></i>
                                            <span class="ms-1">Poor</span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating2" value="2" ${rating == '2' ? 'checked' : ''}>
                                        <label class="form-check-label" for="rating2">
                                            <i class="fas fa-star"></i>
                                            <span class="ms-1">Fair</span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating3" value="3" ${rating == '3' ? 'checked' : ''}>
                                        <label class="form-check-label" for="rating3">
                                            <i class="fas fa-star"></i>
                                            <span class="ms-1">Good</span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating4" value="4" ${rating == '4' ? 'checked' : ''}>
                                        <label class="form-check-label" for="rating4">
                                            <i class="fas fa-star"></i>
                                            <span class="ms-1">Very Good</span>
                                        </label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating5" value="5" ${empty rating || rating == '5' ? 'checked' : ''}>
                                        <label class="form-check-label" for="rating5">
                                            <i class="fas fa-star"></i>
                                            <span class="ms-1">Excellent</span>
                                        </label>
                                    </div>
                                </div>
                                <c:if test="${not empty ratingError}">
                                    <div class="text-danger mt-1">${ratingError}</div>
                                </c:if>
                            </div>
                        </div>
                        
                        <div class="mb-4">
                            <label for="comment" class="form-label">Your Review*</label>
                            <textarea class="form-control ${not empty commentError ? 'is-invalid' : ''}" 
                                      id="comment" name="comment" rows="5" 
                                      placeholder="Share your experience with this vehicle...">${comment}</textarea>
                            <div class="invalid-feedback">${commentError}</div>
                            <div class="form-text">Minimum 10 characters required.</div>
                        </div>
                        
                        <div class="d-flex justify-content-end">
                            <a href="${pageContext.request.contextPath}/vehicles/details?id=${vehicle.id}" class="btn btn-secondary me-2">Cancel</a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-paper-plane me-2"></i> Submit Review
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />