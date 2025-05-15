<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp">
    <jsp:param name="title" value="Home" />
</jsp:include>

<!-- Hero Section -->
<div class="jumbotron bg-dark text-white py-5 mb-5 rounded">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-7">
                <h1 class="display-4">Find Your Perfect Rental Vehicle</h1>
                <p class="lead">Browse our wide selection of vehicles and find the perfect one for your needs. From compact cars to luxury SUVs, we have it all.</p>
                <hr class="my-4">
                <p>Already know what you need? Start browsing now or register to make a booking.</p>
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-primary btn-lg me-2">
                        <i class="fas fa-car me-2"></i> Browse Vehicles
                    </a>
                    <c:if test="${sessionScope.userId == null}">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-light btn-lg">
                            <i class="fas fa-user-plus me-2"></i> Register
                        </a>
                    </c:if>
                </div>
            </div>
            <div class="col-md-5 d-none d-md-block">
                <div class="text-center">
                    <svg width="400" height="300" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg">
                        <path fill="#0d6efd" d="M499.99 176h-59.87l-16.64-41.6C406.38 91.63 365.57 64 319.5 64h-127c-46.06 0-86.88 27.63-103.99 70.4L71.87 176H12.01C4.2 176-1.53 183.34.37 190.91l6 24C7.7 220.25 12.5 224 18.01 224h20.07C24.65 235.73 16 252.78 16 272v48c0 16.12 6.16 30.67 16 41.93V416c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-32h256v32c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-54.07c9.84-11.25 16-25.8 16-41.93v-48c0-19.22-8.65-36.27-22.07-48H494c5.51 0 10.31-3.75 11.64-9.09l6-24c1.89-7.57-3.84-14.91-11.65-14.91zm-352.06-17.83c7.29-18.22 24.94-30.17 44.57-30.17h127c19.63 0 37.28 11.95 44.57 30.17L384 208H128l19.93-49.83zM96 319.8c-19.2 0-32-12.76-32-31.9S76.8 256 96 256s48 28.71 48 47.85-28.8 15.95-48 15.95zm320 0c-19.2 0-48 3.19-48-15.95S396.8 256 416 256s32 12.76 32 31.9-12.8 31.9-32 31.9z"/>
                    </svg>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Features Section -->
<div class="container">
    <div class="row text-center mb-5">
        <div class="col-md-4 mb-4">
            <div class="card h-100">
                <div class="card-body">
                    <div class="mb-3">
                        <i class="fas fa-car fa-3x text-primary"></i>
                    </div>
                    <h3 class="card-title">Wide Selection</h3>
                    <p class="card-text">Choose from our extensive fleet of vehicles ranging from economy to luxury models. We have the perfect vehicle for every occasion.</p>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-4">
            <div class="card h-100">
                <div class="card-body">
                    <div class="mb-3">
                        <i class="fas fa-money-bill-wave fa-3x text-success"></i>
                    </div>
                    <h3 class="card-title">Competitive Pricing</h3>
                    <p class="card-text">Enjoy the best rates in the market with our transparent pricing structure. No hidden fees or surprises at checkout.</p>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-4">
            <div class="card h-100">
                <div class="card-body">
                    <div class="mb-3">
                        <i class="fas fa-shield-alt fa-3x text-danger"></i>
                    </div>
                    <h3 class="card-title">Safe & Secure</h3>
                    <p class="card-text">All our vehicles are regularly maintained and thoroughly cleaned to ensure your safety and comfort during your rental period.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- How It Works Section -->
<div class="bg-light py-5 my-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="display-5">How It Works</h2>
            <p class="lead">Renting a vehicle with us is quick and easy</p>
        </div>
        
        <div class="row">
            <div class="col-md-3 text-center">
                <div class="mb-3">
                    <i class="fas fa-search fa-3x text-primary"></i>
                </div>
                <h4>Browse</h4>
                <p>Search through our collection of vehicles and find the one that meets your needs.</p>
            </div>
            <div class="col-md-3 text-center">
                <div class="mb-3">
                    <i class="fas fa-calendar-alt fa-3x text-primary"></i>
                </div>
                <h4>Book</h4>
                <p>Select your pickup and return dates, and book your vehicle in just a few clicks.</p>
            </div>
            <div class="col-md-3 text-center">
                <div class="mb-3">
                    <i class="fas fa-credit-card fa-3x text-primary"></i>
                </div>
                <h4>Pay</h4>
                <p>Make your payment securely through our simple payment system.</p>
            </div>
            <div class="col-md-3 text-center">
                <div class="mb-3">
                    <i class="fas fa-car-side fa-3x text-primary"></i>
                </div>
                <h4>Drive</h4>
                <p>Pick up your vehicle and enjoy your journey with peace of mind.</p>
            </div>
        </div>
    </div>
</div>

<!-- Call to Action -->
<div class="container text-center my-5">
    <div class="card bg-primary text-white p-5">
        <div class="card-body">
            <h2 class="card-title">Ready to hit the road?</h2>
            <p class="card-text">Browse our vehicles and book your rental today. It's quick, easy, and hassle-free.</p>
            <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-light btn-lg mt-3">Find Your Vehicle Now</a>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
