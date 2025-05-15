<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Create Booking" />
</jsp:include>

<div class="row">
    <div class="col-md-12 mb-4">
        <h2><i class="fas fa-calendar-plus me-2"></i> Create a New Booking</h2>
    </div>
</div>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Booking Information</h4>
            </div>
            <div class="card-body">
                <c:if test="${not empty vehicle}">
                    <div class="alert alert-info mb-4">
                        <div class="d-flex align-items-center">
                            <div>
                                <h5 class="mb-1">Selected Vehicle: ${vehicle.make} ${vehicle.model} (${vehicle.year})</h5>
                                <p class="mb-0"><strong>Daily Rate:</strong> $<fmt:formatNumber value="${vehicle.dailyRate}" pattern="#,##0.00"/> / day</p>
                            </div>
                        </div>
                    </div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/bookings/create" method="post">
                    <c:if test="${not empty vehicle}">
                        <input type="hidden" name="vehicleId" value="${vehicle.id}">
                    </c:if>
                    
                    <c:if test="${empty vehicle}">
                        <div class="mb-3">
                            <label for="vehicleId" class="form-label required">Select Vehicle</label>
                            <select class="form-select ${not empty vehicleIdError ? 'is-invalid' : ''}" 
                                    id="vehicleId" name="vehicleId" required>
                                <option value="">-- Select a Vehicle --</option>
                                <c:forEach var="availableVehicle" items="${availableVehicles}">
                                    <option value="${availableVehicle.id}" ${vehicleId eq availableVehicle.id ? 'selected' : ''}>
                                        ${availableVehicle.make} ${availableVehicle.model} (${availableVehicle.year}) - 
                                        $<fmt:formatNumber value="${availableVehicle.dailyRate}" pattern="#,##0.00"/> / day
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty vehicleIdError}">
                                <div class="invalid-feedback">
                                    ${vehicleIdError}
                                </div>
                            </c:if>
                        </div>
                    </c:if>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="startDate" class="form-label required">Start Date</label>
                            <input type="date" class="form-control ${not empty startDateError ? 'is-invalid' : ''}" 
                                   id="startDate" name="startDate" value="${startDate}" required
                                   data-min-date="${minStartDate}" min="${minStartDate}">
                            <c:if test="${not empty startDateError}">
                                <div class="invalid-feedback">
                                    ${startDateError}
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-6">
                            <label for="endDate" class="form-label required">End Date</label>
                            <input type="date" class="form-control ${not empty endDateError ? 'is-invalid' : ''}" 
                                   id="endDate" name="endDate" value="${endDate}" required>
                            <c:if test="${not empty endDateError}">
                                <div class="invalid-feedback">
                                    ${endDateError}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <c:if test="${not empty dateRangeError}">
                        <div class="alert alert-danger">
                            ${dateRangeError}
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty vehicle}">
                        <div class="card bg-light mb-4">
                            <div class="card-body">
                                <h5 class="card-title">Booking Summary</h5>
                                <input type="hidden" id="dailyRate" value="${vehicle.dailyRate}">
                                <p class="mb-1"><strong>Daily Rate:</strong> $<fmt:formatNumber value="${vehicle.dailyRate}" pattern="#,##0.00"/></p>
                                <p class="mb-1"><strong>Rental Duration:</strong> <span id="rentalDuration">0</span> days</p>
                                <p class="mb-0"><strong>Estimated Total:</strong> $<span id="totalCost">0.00</span></p>
                            </div>
                        </div>
                    </c:if>
                    
                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-calendar-check me-2"></i> Create Booking
                        </button>
                        <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-secondary">
                            <i class="fas fa-times me-2"></i> Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <div class="col-md-4">
        <div class="card">
            <div class="card-header bg-info text-white">
                <h4 class="mb-0"><i class="fas fa-info-circle me-2"></i> Booking Information</h4>
            </div>
            <div class="card-body">
                <h5>Important Notes:</h5>
                <ul>
                    <li>Bookings must be made at least 1 day in advance.</li>
                    <li>The minimum rental period is 1 day.</li>
                    <li>The rental period includes both the start and end dates.</li>
                    <li>Payment is required to confirm your booking.</li>
                    <li>Cancellations must be made at least 24 hours before the start date for a full refund.</li>
                </ul>
                
                <h5 class="mt-4">Required Documents:</h5>
                <ul>
                    <li>Valid driver's license</li>
                    <li>Proof of identity (ID card or passport)</li>
                    <li>Credit/debit card for payment</li>
                </ul>
                
                <div class="alert alert-warning mt-4">
                    <i class="fas fa-exclamation-triangle me-2"></i> By creating a booking, you agree to the terms and conditions of our rental service.
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Calculate booking cost function
        function calculateBookingCost() {
            const startDateEl = document.getElementById('startDate');
            const endDateEl = document.getElementById('endDate');
            const dailyRateEl = document.getElementById('dailyRate');
            const totalCostEl = document.getElementById('totalCost');
            const rentalDurationEl = document.getElementById('rentalDuration');
            
            if (startDateEl && endDateEl && dailyRateEl && totalCostEl) {
                const startDate = new Date(startDateEl.value);
                const endDate = new Date(endDateEl.value);
                const dailyRate = parseFloat(dailyRateEl.value);
                
                if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime()) && !isNaN(dailyRate)) {
                    // Calculate difference in days
                    const timeDiff = endDate - startDate;
                    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1; // +1 to include both start and end dates
                    
                    if (daysDiff > 0) {
                        rentalDurationEl.textContent = daysDiff;
                        const totalCost = daysDiff * dailyRate;
                        totalCostEl.textContent = totalCost.toFixed(2);
                    } else {
                        rentalDurationEl.textContent = "0";
                        totalCostEl.textContent = "0.00";
                    }
                }
            }
        }
        
        // Add event listeners to date inputs
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        
        if (startDateInput && endDateInput) {
            startDateInput.addEventListener('change', function() {
                // Set minimum end date to be the same as start date
                endDateInput.min = startDateInput.value;
                
                // If end date is earlier than start date, set it to start date
                if (endDateInput.value && endDateInput.value < startDateInput.value) {
                    endDateInput.value = startDateInput.value;
                }
                
                calculateBookingCost();
            });
            
            endDateInput.addEventListener('change', calculateBookingCost);
            
            // Calculate on page load
            calculateBookingCost();
        }
    });
</script>

<jsp:include page="/includes/footer.jsp" />
