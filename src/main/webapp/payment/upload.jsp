<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Upload Payment" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0"><i class="fas fa-money-check-alt me-2"></i> Payment Submission</h4>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            ${errorMessage}
                        </div>
                    </c:if>
                    
                    <div class="booking-details mb-4">
                        <h5 class="border-bottom pb-2 mb-3">Booking Details</h5>
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Booking ID:</strong> ${booking.id}</p>
                                <p><strong>Vehicle:</strong> ${booking.vehicleName}</p>
                                <p><strong>Status:</strong> 
                                    <span class="badge bg-${booking.status eq 'CONFIRMED' ? 'success' : 
                                                          booking.status eq 'PENDING' ? 'warning' : 
                                                          'secondary'}">
                                        ${booking.status}
                                    </span>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Date Range:</strong> 
                                    <fmt:parseDate value="${booking.startDate}" pattern="yyyy-MM-dd" var="parsedStartDate" type="date" />
                                    <fmt:parseDate value="${booking.endDate}" pattern="yyyy-MM-dd" var="parsedEndDate" type="date" />
                                    <fmt:formatDate value="${parsedStartDate}" pattern="MMM dd, yyyy" /> - 
                                    <fmt:formatDate value="${parsedEndDate}" pattern="MMM dd, yyyy" />
                                </p>
                                <p><strong>Total Amount:</strong> 
                                    <span class="text-primary fw-bold">
                                        $<fmt:formatNumber value="${booking.totalCost}" pattern="#,##0.00" />
                                    </span>
                                </p>
                            </div>
                        </div>
                    </div>
                    
                    <h5 class="border-bottom pb-2 mb-3">Payment Information</h5>
                    <form action="${pageContext.request.contextPath}/payments/process" method="post" enctype="multipart/form-data" class="row g-3">
                        <input type="hidden" name="bookingId" value="${booking.id}" />
                        
                        <div class="col-md-6">
                            <label for="paymentMethod" class="form-label">Payment Method*</label>
                            <select class="form-select ${not empty paymentMethodError ? 'is-invalid' : ''}" 
                                id="paymentMethod" name="paymentMethod" required>
                                <option value="" selected disabled>Select payment method</option>
                                <option value="BANK_TRANSFER" ${paymentMethod == 'BANK_TRANSFER' ? 'selected' : ''}>Bank Transfer</option>
                                <option value="CASH_DEPOSIT" ${paymentMethod == 'CASH_DEPOSIT' ? 'selected' : ''}>Cash Deposit</option>
                                <option value="ONLINE_PAYMENT" ${paymentMethod == 'ONLINE_PAYMENT' ? 'selected' : ''}>Online Payment</option>
                            </select>
                            <div class="invalid-feedback">${paymentMethodError}</div>
                            <div class="form-text">
                                <small>For bank transfers, please use the following account:</small><br/>
                                <small>Bank: Example Bank</small><br/>
                                <small>Account No: 123-456-7890</small><br/>
                                <small>Name: Vehicle Rental Services</small>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="amount" class="form-label">Amount Paid*</label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" step="0.01" class="form-control ${not empty amountError ? 'is-invalid' : ''}" 
                                    id="amount" name="amount" value="${amount}" 
                                    required min="0" placeholder="0.00">
                                <div class="invalid-feedback">${amountError}</div>
                            </div>
                            <div class="form-text">Amount should match the total cost of your booking</div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="transactionId" class="form-label">Transaction ID/Reference</label>
                            <input type="text" class="form-control" 
                                id="transactionId" name="transactionId" value="${transactionId}" 
                                placeholder="Optional reference number">
                            <div class="form-text">
                                If you made a bank transfer or online payment, enter the reference number
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <label for="paymentSlip" class="form-label">Payment Slip/Receipt*</label>
                            <input type="file" class="form-control ${not empty paymentSlipError ? 'is-invalid' : ''}" 
                                id="paymentSlip" name="paymentSlip" accept="image/*" required>
                            <div class="invalid-feedback">${paymentSlipError}</div>
                            <div class="form-text">
                                Upload a photo or scan of your payment receipt/slip (JPG, PNG or PDF)
                            </div>
                        </div>
                        
                        <div class="col-12 mt-4 d-flex justify-content-end">
                            <a href="${pageContext.request.contextPath}/bookings/details?id=${booking.id}" class="btn btn-secondary me-2">
                                <i class="fas fa-arrow-left me-2"></i> Back to Booking
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-check-circle me-2"></i> Submit Payment
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />