<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Edit Vehicle" />
</jsp:include>

<div class="row">
    <div class="col-md-12 mb-4">
        <h2><i class="fas fa-edit me-2"></i> Edit Vehicle</h2>
    </div>
</div>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Edit Vehicle Information</h4>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/admin/vehicles/edit" method="post">
                    <input type="hidden" name="id" value="${vehicle.id}">
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="type" class="form-label required">Vehicle Type</label>
                            <input type="text" class="form-control ${not empty typeError ? 'is-invalid' : ''}" 
                                   id="type" name="type" value="${not empty type ? type : vehicle.type}" required
                                   placeholder="e.g., Sedan, SUV, Van">
                            <c:if test="${not empty typeError}">
                                <div class="invalid-feedback">
                                    ${typeError}
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label for="make" class="form-label required">Make</label>
                            <input type="text" class="form-control ${not empty makeError ? 'is-invalid' : ''}" 
                                   id="make" name="make" value="${not empty make ? make : vehicle.make}" required
                                   placeholder="e.g., Toyota, Honda, Ford">
                            <c:if test="${not empty makeError}">
                                <div class="invalid-feedback">
                                    ${makeError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="model" class="form-label required">Model</label>
                            <input type="text" class="form-control ${not empty modelError ? 'is-invalid' : ''}" 
                                   id="model" name="model" value="${not empty model ? model : vehicle.model}" required
                                   placeholder="e.g., Camry, Civic, Explorer">
                            <c:if test="${not empty modelError}">
                                <div class="invalid-feedback">
                                    ${modelError}
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label for="year" class="form-label required">Year</label>
                            <input type="number" class="form-control ${not empty yearError ? 'is-invalid' : ''}" 
                                   id="year" name="year" value="${not empty year ? year : vehicle.year}" required
                                   min="1900" max="2100" placeholder="e.g., 2023">
                            <c:if test="${not empty yearError}">
                                <div class="invalid-feedback">
                                    ${yearError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="registrationNumber" class="form-label required">Registration Number</label>
                            <input type="text" class="form-control ${not empty registrationNumberError ? 'is-invalid' : ''}" 
                                   id="registrationNumber" name="registrationNumber" 
                                   value="${not empty registrationNumber ? registrationNumber : vehicle.registrationNumber}" required
                                   placeholder="e.g., ABC-123">
                            <c:if test="${not empty registrationNumberError}">
                                <div class="invalid-feedback">
                                    ${registrationNumberError}
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label for="dailyRate" class="form-label required">Daily Rate ($)</label>
                            <input type="number" class="form-control ${not empty dailyRateError ? 'is-invalid' : ''}" 
                                   id="dailyRate" name="dailyRate" 
                                   value="${not empty dailyRate ? dailyRate : vehicle.dailyRate}" required step="0.01" min="0"
                                   placeholder="e.g., 99.99">
                            <c:if test="${not empty dailyRateError}">
                                <div class="invalid-feedback">
                                    ${dailyRateError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="imageUrl" class="form-label">Image URL</label>
                            <input type="url" class="form-control" 
                                   id="imageUrl" name="imageUrl" 
                                   value="${not empty imageUrl ? imageUrl : vehicle.imageUrl}"
                                   placeholder="https://example.com/car-image.jpg">
                            <div class="form-text">
                                Provide a URL to an image of the vehicle (optional).
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label required">Availability</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" id="availableYes" 
                                       name="available" value="true" 
                                       ${not empty available ? (available ? 'checked' : '') : (vehicle.available ? 'checked' : '')}>
                                <label class="form-check-label" for="availableYes">
                                    Available for Rental
                                </label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" id="availableNo" 
                                       name="available" value="false" 
                                       ${not empty available ? (available == false ? 'checked' : '') : (vehicle.available == false ? 'checked' : '')}>
                                <label class="form-check-label" for="availableNo">
                                    Not Available for Rental
                                </label>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="description" class="form-label required">Description</label>
                        <textarea class="form-control ${not empty descriptionError ? 'is-invalid' : ''}" 
                                  id="description" name="description" rows="5" required
                                  placeholder="Enter a detailed description of the vehicle, including features and condition...">${not empty description ? description : vehicle.description}</textarea>
                        <c:if test="${not empty descriptionError}">
                            <div class="invalid-feedback">
                                ${descriptionError}
                            </div>
                        </c:if>
                    </div>
                    
                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i> Update Vehicle
                        </button>
                        <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-secondary">
                            <i class="fas fa-times me-2"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
