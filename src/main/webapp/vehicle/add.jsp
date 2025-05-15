<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Add New Vehicle" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0"><i class="fas fa-car me-2"></i> Add New Vehicle</h4>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            ${errorMessage}
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/admin/vehicles/add" method="post" class="row g-3">
                        <div class="col-md-6">
                            <label for="type" class="form-label">Vehicle Type*</label>
                            <select id="type" name="type" class="form-select ${not empty typeError ? 'is-invalid' : ''}">
                                <option value="" selected disabled>Select vehicle type</option>
                                <option value="SEDAN" ${type == 'SEDAN' ? 'selected' : ''}>Sedan</option>
                                <option value="SUV" ${type == 'SUV' ? 'selected' : ''}>SUV</option>
                                <option value="TRUCK" ${type == 'TRUCK' ? 'selected' : ''}>Truck</option>
                                <option value="VAN" ${type == 'VAN' ? 'selected' : ''}>Van</option>
                                <option value="COUPE" ${type == 'COUPE' ? 'selected' : ''}>Coupe</option>
                                <option value="MOTORCYCLE" ${type == 'MOTORCYCLE' ? 'selected' : ''}>Motorcycle</option>
                            </select>
                            <div class="invalid-feedback">${typeError}</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="make" class="form-label">Make*</label>
                            <input type="text" class="form-control ${not empty makeError ? 'is-invalid' : ''}" 
                                   id="make" name="make" value="${make}" placeholder="e.g. Toyota, Honda">
                            <div class="invalid-feedback">${makeError}</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="model" class="form-label">Model*</label>
                            <input type="text" class="form-control ${not empty modelError ? 'is-invalid' : ''}" 
                                   id="model" name="model" value="${model}" placeholder="e.g. Camry, Civic">
                            <div class="invalid-feedback">${modelError}</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="year" class="form-label">Year*</label>
                            <input type="number" class="form-control ${not empty yearError ? 'is-invalid' : ''}" 
                                   id="year" name="year" value="${year}" min="1900" max="2100" placeholder="e.g. 2022">
                            <div class="invalid-feedback">${yearError}</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="registrationNumber" class="form-label">Registration Number*</label>
                            <input type="text" class="form-control ${not empty registrationNumberError ? 'is-invalid' : ''}" 
                                   id="registrationNumber" name="registrationNumber" value="${registrationNumber}">
                            <div class="invalid-feedback">${registrationNumberError}</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="dailyRate" class="form-label">Daily Rate ($)*</label>
                            <input type="number" step="0.01" class="form-control ${not empty dailyRateError ? 'is-invalid' : ''}" 
                                   id="dailyRate" name="dailyRate" value="${dailyRate}" min="0">
                            <div class="invalid-feedback">${dailyRateError}</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="imageUrl" class="form-label">Image URL</label>
                            <input type="url" class="form-control ${not empty imageUrlError ? 'is-invalid' : ''}" 
                                   id="imageUrl" name="imageUrl" value="${imageUrl}" 
                                   placeholder="https://example.com/car-image.jpg">
                            <div class="invalid-feedback">${imageUrlError}</div>
                            <div class="form-text">Direct link to vehicle image (optional)</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="form-label d-block">Availability</label>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="available" 
                                       id="availableYes" value="true" ${empty available || available == 'true' ? 'checked' : ''}>
                                <label class="form-check-label" for="availableYes">Available</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="available" 
                                       id="availableNo" value="false" ${available == 'false' ? 'checked' : ''}>
                                <label class="form-check-label" for="availableNo">Not Available</label>
                            </div>
                        </div>
                        
                        <div class="col-12">
                            <label for="description" class="form-label">Description*</label>
                            <textarea class="form-control ${not empty descriptionError ? 'is-invalid' : ''}" 
                                      id="description" name="description" rows="4">${description}</textarea>
                            <div class="invalid-feedback">${descriptionError}</div>
                        </div>
                        
                        <div class="col-12 mt-4 d-flex justify-content-end">
                            <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-secondary me-2">Cancel</a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i> Add Vehicle
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />