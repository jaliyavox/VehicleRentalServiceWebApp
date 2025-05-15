<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Manage Vehicles" />
</jsp:include>

<!-- Admin Vehicles Management Header -->
<div class="container-fluid bg-primary text-white py-3">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1><i class="fas fa-car me-2"></i> Vehicle Management</h1>
                <p class="lead mb-0">Manage the vehicles in your rental fleet</p>
            </div>
            <div class="col-md-4 text-md-end">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-light me-2">
                    <i class="fas fa-tachometer-alt"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/vehicles/add" class="btn btn-light">
                    <i class="fas fa-plus-circle"></i> Add Vehicle
                </a>
            </div>
        </div>
    </div>
</div>

<!-- Vehicle Management Content -->
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
    
    <!-- Filter Controls -->
    <div class="card shadow-sm mb-4">
        <div class="card-header bg-white">
            <h5 class="mb-0"><i class="fas fa-filter me-2"></i> Filter Vehicles</h5>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/vehicles" method="get" class="row g-3">
                <div class="col-md-3">
                    <label for="type" class="form-label">Type</label>
                    <select class="form-select" id="type" name="type">
                        <option value="">All Types</option>
                        <c:forEach items="${vehicleTypes}" var="vehicleType">
                            <option value="${vehicleType}" ${type == vehicleType ? 'selected' : ''}>${vehicleType}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="col-md-3">
                    <label for="make" class="form-label">Make</label>
                    <select class="form-select" id="make" name="make">
                        <option value="">All Makes</option>
                        <c:forEach items="${vehicleMakes}" var="vehicleMake">
                            <option value="${vehicleMake}" ${make == vehicleMake ? 'selected' : ''}>${vehicleMake}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="col-md-3">
                    <label for="model" class="form-label">Model</label>
                    <input type="text" class="form-control" id="model" name="model" value="${model}" placeholder="Any model">
                </div>
                
                <div class="col-md-3 d-flex align-items-end">
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" id="availableOnly" name="availableOnly" value="true" ${availableOnly ? 'checked' : ''}>
                        <label class="form-check-label" for="availableOnly">
                            Show available vehicles only
                        </label>
                    </div>
                </div>
                
                <div class="col-md-4">
                    <label for="sortBy" class="form-label">Sort By</label>
                    <select class="form-select" id="sortBy" name="sortBy">
                        <option value="">Default</option>
                        <option value="price" ${sortBy == 'price' ? 'selected' : ''}>Price</option>
                        <option value="availability" ${sortBy == 'availability' ? 'selected' : ''}>Availability</option>
                    </select>
                </div>
                
                <div class="col-md-4">
                    <label for="sortOrder" class="form-label">Sort Order</label>
                    <select class="form-select" id="sortOrder" name="sortOrder">
                        <option value="asc" ${sortOrder == 'asc' ? 'selected' : ''}>Ascending</option>
                        <option value="desc" ${sortOrder == 'desc' ? 'selected' : ''}>Descending</option>
                    </select>
                </div>
                
                <div class="col-md-4 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary me-2">
                        <i class="fas fa-search me-2"></i> Apply Filters
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/vehicles" class="btn btn-secondary">
                        <i class="fas fa-redo me-2"></i> Reset
                    </a>
                </div>
            </form>
        </div>
    </div>
    
    <!-- Vehicles List -->
    <div class="card shadow-sm">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0"><i class="fas fa-car me-2"></i> Vehicle List</h5>
            <span class="badge bg-primary">${vehicles.size()} Vehicles</span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Image</th>
                            <th>Type</th>
                            <th>Make/Model</th>
                            <th>Year</th>
                            <th>Registration</th>
                            <th>Daily Rate</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty vehicles}">
                            <tr>
                                <td colspan="9" class="text-center py-4">
                                    <p class="mb-0 text-muted">No vehicles found. <a href="${pageContext.request.contextPath}/admin/vehicles/add" class="text-primary">Add a new vehicle</a></p>
                                </td>
                            </tr>
                        </c:if>
                        
                        <c:forEach items="${vehicles}" var="vehicle">
                            <tr>
                                <td><small class="text-muted">${vehicle.id.substring(0, 8)}...</small></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty vehicle.imageUrl}">
                                            <img src="${vehicle.imageUrl}" alt="${vehicle.make} ${vehicle.model}" class="img-thumbnail" style="height: 40px; width: auto;">
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted"><i class="fas fa-car fa-2x"></i></span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${vehicle.type}</td>
                                <td>${vehicle.make} ${vehicle.model}</td>
                                <td>${vehicle.year}</td>
                                <td>${vehicle.registrationNumber}</td>
                                <td>
                                    <span class="badge bg-success">
                                        $<fmt:formatNumber value="${vehicle.dailyRate}" pattern="#,##0.00" />
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${vehicle.available}">
                                            <span class="badge bg-success">Available</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">Not Available</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="btn-group" role="group">
                                        <a href="${pageContext.request.contextPath}/vehicles/details?id=${vehicle.id}" class="btn btn-sm btn-outline-primary" title="View">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/vehicles/edit?id=${vehicle.id}" class="btn btn-sm btn-outline-primary" title="Edit">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button type="button" class="btn btn-sm btn-outline-danger" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#deleteVehicleModal" 
                                                data-vehicle-id="${vehicle.id}"
                                                data-vehicle-name="${vehicle.make} ${vehicle.model}"
                                                title="Delete">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
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

<!-- Delete Vehicle Modal -->
<div class="modal fade" id="deleteVehicleModal" tabindex="-1" aria-labelledby="deleteVehicleModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="deleteVehicleModalLabel">
                    <i class="fas fa-exclamation-triangle me-2"></i> Confirm Delete
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete the vehicle: <strong id="vehicleNameToDelete"></strong>?</p>
                <p class="mb-0 text-danger">This action cannot be undone!</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteVehicleForm" action="${pageContext.request.contextPath}/admin/vehicles/delete" method="post">
                    <input type="hidden" id="vehicleIdToDelete" name="id" value="">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash-alt me-2"></i> Delete Vehicle
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    // Handle delete vehicle modal
    document.addEventListener('DOMContentLoaded', function() {
        const deleteModal = document.getElementById('deleteVehicleModal');
        if (deleteModal) {
            deleteModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const vehicleId = button.getAttribute('data-vehicle-id');
                const vehicleName = button.getAttribute('data-vehicle-name');
                
                const vehicleIdInput = document.getElementById('vehicleIdToDelete');
                const vehicleNameElement = document.getElementById('vehicleNameToDelete');
                
                vehicleIdInput.value = vehicleId;
                vehicleNameElement.textContent = vehicleName;
            });
        }
    });
</script>

<jsp:include page="/includes/footer.jsp" />