<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Browse Vehicles" />
</jsp:include>

<div class="row mb-4">
    <div class="col-md-8">
        <h2><i class="fas fa-car me-2"></i> Browse Vehicles</h2>
    </div>
    <div class="col-md-4 text-end">
        <c:if test="${sessionScope.isAdmin == true}">
            <a href="${pageContext.request.contextPath}/admin/vehicles/add" class="btn btn-success">
                <i class="fas fa-plus me-2"></i> Add New Vehicle
            </a>
        </c:if>
    </div>
</div>

<!-- Filter Form -->
<div class="card mb-4">
    <div class="card-header bg-light">
        <h5 class="mb-0"><i class="fas fa-filter me-2"></i> Filter Vehicles</h5>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/vehicles" method="get" id="vehicleFilterForm">
            <div class="row">
                <div class="col-md-3 mb-3">
                    <label for="type" class="form-label">Vehicle Type</label>
                    <select class="form-select" id="type" name="type">
                        <option value="">All Types</option>
                        <c:forEach var="vehicleType" items="${vehicleTypes}">
                            <option value="${vehicleType}" ${type eq vehicleType ? 'selected' : ''}>
                                ${vehicleType}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3 mb-3">
                    <label for="make" class="form-label">Make</label>
                    <select class="form-select" id="make" name="make">
                        <option value="">All Makes</option>
                        <c:forEach var="vehicleMake" items="${vehicleMakes}">
                            <option value="${vehicleMake}" ${make eq vehicleMake ? 'selected' : ''}>
                                ${vehicleMake}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3 mb-3">
                    <label for="model" class="form-label">Model</label>
                    <input type="text" class="form-control" id="model" name="model" value="${model}" 
                           placeholder="Any model">
                </div>
                <div class="col-md-3 mb-3">
                    <label for="sortBy" class="form-label">Sort By</label>
                    <select class="form-select" id="sortBy" name="sortBy">
                        <option value="" ${empty sortBy ? 'selected' : ''}>Default</option>
                        <option value="price" ${sortBy eq 'price' ? 'selected' : ''}>Price</option>
                        <option value="availability" ${sortBy eq 'availability' ? 'selected' : ''}>Availability</option>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="col-md-3 mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="availableOnly" name="availableOnly" 
                               value="true" ${availableOnly ? 'checked' : ''}>
                        <label class="form-check-label" for="availableOnly">
                            Available vehicles only
                        </label>
                    </div>
                </div>
                <div class="col-md-3 mb-3">
                    <select class="form-select" id="sortOrder" name="sortOrder">
                        <option value="asc" ${sortOrder eq 'asc' || empty sortOrder ? 'selected' : ''}>Ascending</option>
                        <option value="desc" ${sortOrder eq 'desc' ? 'selected' : ''}>Descending</option>
                    </select>
                </div>
                <div class="col-md-6 mb-3 text-end">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search me-2"></i> Apply Filters
                    </button>
                    <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-secondary ms-2">
                        <i class="fas fa-redo me-2"></i> Reset
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>

<!-- Vehicles Grid -->
<div class="row">
    <c:choose>
        <c:when test="${empty vehicles}">
            <div class="col-12">
                <div class="alert alert-info">
                    <i class="fas fa-info-circle me-2"></i> No vehicles found matching your criteria. Please try different filters.
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach var="vehicle" items="${vehicles}">
                <div class="col-md-4 mb-4">
                    <div class="card vehicle-card h-100">
                        <div class="position-relative">
                            <c:choose>
                                <c:when test="${not empty vehicle.imageUrl}">
                                    <img src="${vehicle.imageUrl}" class="card-img-top" alt="${vehicle.make} ${vehicle.model}">
                                </c:when>
                                <c:otherwise>
                                    <div class="card-img-top d-flex align-items-center justify-content-center bg-light" style="height: 200px;">
                                        <svg width="100" height="100" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg">
                                            <path fill="#6c757d" d="M499.99 176h-59.87l-16.64-41.6C406.38 91.63 365.57 64 319.5 64h-127c-46.06 0-86.88 27.63-103.99 70.4L71.87 176H12.01C4.2 176-1.53 183.34.37 190.91l6 24C7.7 220.25 12.5 224 18.01 224h20.07C24.65 235.73 16 252.78 16 272v48c0 16.12 6.16 30.67 16 41.93V416c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-32h256v32c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-54.07c9.84-11.25 16-25.8 16-41.93v-48c0-19.22-8.65-36.27-22.07-48H494c5.51 0 10.31-3.75 11.64-9.09l6-24c1.89-7.57-3.84-14.91-11.65-14.91zm-352.06-17.83c7.29-18.22 24.94-30.17 44.57-30.17h127c19.63 0 37.28 11.95 44.57 30.17L384 208H128l19.93-49.83zM96 319.8c-19.2 0-32-12.76-32-31.9S76.8 256 96 256s48 28.71 48 47.85-28.8 15.95-48 15.95zm320 0c-19.2 0-48 3.19-48-15.95S396.8 256 416 256s32 12.76 32 31.9-12.8 31.9-32 31.9z"/>
                                        </svg>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <span class="position-absolute top-0 end-0 m-2 badge ${vehicle.available ? 'bg-success' : 'bg-danger'}">
                                ${vehicle.available ? 'Available' : 'Unavailable'}
                            </span>
                        </div>
                        <div class="card-body">
                            <h5 class="card-title">${vehicle.make} ${vehicle.model} (${vehicle.year})</h5>
                            <p class="vehicle-price mb-2">$<fmt:formatNumber value="${vehicle.dailyRate}" pattern="#,##0.00"/> / day</p>
                            <p class="vehicle-info mb-2"><i class="fas fa-car-side me-2"></i> ${vehicle.type}</p>
                            <p class="vehicle-info mb-2"><i class="fas fa-id-card me-2"></i> ${vehicle.registrationNumber}</p>
                            <p class="card-text">${vehicle.description.length() > 100 ? vehicle.description.substring(0, 100).concat('...') : vehicle.description}</p>
                        </div>
                        <div class="card-footer bg-white d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/vehicles/details?id=${vehicle.id}" class="btn btn-primary">
                                <i class="fas fa-info-circle me-2"></i> Details
                            </a>
                            <c:if test="${vehicle.available && sessionScope.userId != null}">
                                <a href="${pageContext.request.contextPath}/bookings/create?vehicleId=${vehicle.id}" class="btn btn-success">
                                    <i class="fas fa-calendar-plus me-2"></i> Book Now
                                </a>
                            </c:if>
                            <c:if test="${sessionScope.isAdmin == true}">
                                <div class="dropdown">
                                    <button class="btn btn-secondary dropdown-toggle" type="button" id="adminActions${vehicle.id}" 
                                            data-bs-toggle="dropdown" aria-expanded="false">
                                        <i class="fas fa-cog"></i>
                                    </button>
                                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="adminActions${vehicle.id}">
                                        <li>
                                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/vehicles/edit?id=${vehicle.id}">
                                                <i class="fas fa-edit me-2"></i> Edit
                                            </a>
                                        </li>
                                        <li>
                                            <a class="dropdown-item text-danger delete-confirm" 
                                               href="${pageContext.request.contextPath}/admin/vehicles/delete?id=${vehicle.id}">
                                                <i class="fas fa-trash me-2"></i> Delete
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/includes/footer.jsp" />
