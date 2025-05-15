/**
 * Main JavaScript functionality for the Vehicle Rental Application
 */
document.addEventListener('DOMContentLoaded', function() {
    // Initialize all components
    initializeDatepickers();
    setupEventListeners();
    
    // Auto-dismiss alerts after 5 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
});

/**
 * Initialize datepicker fields with appropriate configurations
 */
function initializeDatepickers() {
    // If using native date inputs, add constraints
    const startDateInputs = document.querySelectorAll('input[type="date"][id="startDate"]');
    
    startDateInputs.forEach(function(input) {
        // Set min date to today
        if (input.dataset.minDate) {
            input.min = input.dataset.minDate;
        } else {
            const today = new Date();
            const tomorrow = new Date(today);
            tomorrow.setDate(tomorrow.getDate() + 1);
            input.min = tomorrow.toISOString().split('T')[0];
        }
        
        // When start date changes, update end date minimum
        input.addEventListener('change', function() {
            const endDateInput = document.getElementById('endDate');
            if (endDateInput) {
                endDateInput.min = this.value;
                
                // If end date is now invalid, update it
                if (endDateInput.value && endDateInput.value < this.value) {
                    endDateInput.value = this.value;
                }
                
                // Recalculate booking cost if applicable
                calculateBookingCost();
            }
        });
    });
}

/**
 * Set up event listeners for interactive elements
 */
function setupEventListeners() {
    // Confirmation for delete actions
    const deleteButtons = document.querySelectorAll('.delete-confirm');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
                event.preventDefault();
            }
        });
    });
    
    // File upload preview
    const fileInputs = document.querySelectorAll('input[type="file"][data-preview]');
    fileInputs.forEach(function(input) {
        input.addEventListener('change', function() {
            const previewElement = document.getElementById(this.dataset.preview);
            if (previewElement) {
                previewImage(this, previewElement);
            }
        });
    });
    
    // Vehicle filter form submission
    const filterForm = document.getElementById('vehicleFilterForm');
    if (filterForm) {
        const formInputs = filterForm.querySelectorAll('select, input:not([type="submit"])');
        formInputs.forEach(function(input) {
            input.addEventListener('change', function() {
                if (input.type === 'checkbox') {
                    // For checkboxes, submit immediately on change
                    filterForm.submit();
                }
            });
        });
    }
    
    // Star rating inputs
    const ratingInputs = document.querySelectorAll('.star-rating input[type="radio"]');
    ratingInputs.forEach(function(input) {
        input.addEventListener('change', function() {
            const container = this.closest('.star-rating');
            const stars = container.querySelectorAll('label i');
            const rating = parseInt(this.value);
            
            stars.forEach(function(star, index) {
                if (index < rating) {
                    star.classList.add('text-warning');
                } else {
                    star.classList.remove('text-warning');
                }
            });
        });
    });
    
    // End date change for booking cost calculation
    const endDateInput = document.getElementById('endDate');
    if (endDateInput) {
        endDateInput.addEventListener('change', calculateBookingCost);
    }
}

/**
 * Calculate and display booking cost based on dates and daily rate
 */
function calculateBookingCost() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const dailyRateInput = document.getElementById('dailyRate');
    const totalCostElement = document.getElementById('totalCost');
    const rentalDurationElement = document.getElementById('rentalDuration');
    
    if (startDateInput && endDateInput && dailyRateInput && totalCostElement && rentalDurationElement) {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        const dailyRate = parseFloat(dailyRateInput.value);
        
        if (!isNaN(startDate.getTime()) && !isNaN(endDate.getTime()) && !isNaN(dailyRate)) {
            // Calculate difference in days (include both start and end days)
            const timeDiff = endDate - startDate;
            const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1;
            
            if (daysDiff > 0) {
                rentalDurationElement.textContent = daysDiff;
                const totalCost = daysDiff * dailyRate;
                totalCostElement.textContent = totalCost.toFixed(2);
            } else {
                rentalDurationElement.textContent = "0";
                totalCostElement.textContent = "0.00";
            }
        }
    }
}

/**
 * Display star rating based on numeric rating value
 */
function displayStarRating(elementId, rating) {
    const container = document.getElementById(elementId);
    if (container) {
        container.innerHTML = '';
        const starCount = 5;
        
        for (let i = 1; i <= starCount; i++) {
            const star = document.createElement('i');
            star.classList.add('fas', 'fa-star');
            
            if (i <= rating) {
                star.classList.add('text-warning');
            } else {
                star.classList.add('text-muted');
            }
            
            container.appendChild(star);
        }
    }
}

/**
 * Preview an image selected via file input
 */
function previewImage(input, previewElement) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            previewElement.src = e.target.result;
            previewElement.style.display = 'block';
        };
        
        reader.readAsDataURL(input.files[0]);
    }
}