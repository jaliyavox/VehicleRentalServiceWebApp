# Vehicle Rental Web Application

A comprehensive Java servlet-based vehicle rental application that uses text files for data storage instead of databases.

## Project Overview

This application allows users to rent vehicles with complete management of the rental process including vehicle information, user accounts, bookings, payments, and reviews.

### Core Components

1. **Vehicle Management**
   - Add, update, delete, and view vehicle information
   - Search vehicles by various criteria
   - Manage vehicle availability status

2. **User Management**
   - User registration and authentication
   - User profile management
   - Role-based access control

3. **Booking and Rental Management**
   - Create, update, and cancel bookings
   - Track rental status and history
   - Manage rental periods and availability

4. **Admin Management**
   - Admin dashboard for system oversight
   - User management capabilities
   - System configuration settings

5. **Payment Management**
   - Process rental payments
   - Track payment history
   - Handle refunds for cancellations

6. **Review System**
   - Allow users to leave reviews for vehicles
   - Rate rental experiences
   - Manage and display reviews

## File Structure

```
VehicleRentalApp/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── rentalapp/
│       │           ├── model/
│       │           │   ├── Vehicle.java
│       │           │   ├── User.java
│       │           │   ├── Booking.java
│       │           │   ├── Admin.java
│       │           │   ├── Payment.java
│       │           │   └── Review.java
│       │           │
│       │           ├── dao/
│       │           │   ├── VehicleDAO.java
│       │           │   ├── UserDAO.java
│       │           │   ├── BookingDAO.java
│       │           │   ├── AdminDAO.java
│       │           │   ├── PaymentDAO.java
│       │           │   └── ReviewDAO.java
│       │           │
│       │           ├── controller/
│       │           │   ├── vehicle/
│       │           │   │   ├── AddVehicleServlet.java
│       │           │   │   ├── UpdateVehicleServlet.java
│       │           │   │   ├── DeleteVehicleServlet.java
│       │           │   │   └── GetVehicleServlet.java
│       │           │   │
│       │           │   ├── user/
│       │           │   │   ├── RegisterServlet.java
│       │           │   │   ├── LoginServlet.java
│       │           │   │   ├── LogoutServlet.java
│       │           │   │   └── UpdateUserServlet.java
│       │           │   │
│       │           │   ├── booking/
│       │           │   │   ├── CreateBookingServlet.java
│       │           │   │   ├── UpdateBookingServlet.java
│       │           │   │   ├── CancelBookingServlet.java
│       │           │   │   └── ViewBookingServlet.java
│       │           │   │
│       │           │   ├── admin/
│       │           │   │   ├── AdminLoginServlet.java
│       │           │   │   ├── AdminDashboardServlet.java
│       │           │   │   └── ManageUsersServlet.java
│       │           │   │
│       │           │   ├── payment/
│       │           │   │   ├── ProcessPaymentServlet.java
│       │           │   │   ├── ViewPaymentServlet.java
│       │           │   │   ├── RefundServlet.java
│       │           │   │   ├── ApprovePaymentServlet.java
│       │           │   │   └── RejectPaymentServlet.java
│       │           │   │
│       │           │   └── review/
│       │           │       ├── AddReviewServlet.java
│       │           │       ├── DeleteReviewServlet.java
│       │           │       └── ViewReviewsServlet.java
│       │           │
│       │           └── util/
│       │               ├── FileUtil.java
│       │               └── ValidationUtil.java
│       │
│       ├── webapp/
│       │   ├── WEB-INF/
│       │   │   ├── web.xml
│       │   │   └── lib/
│       │   │
│       │   ├── index.jsp
│       │   ├── login.jsp
│       │   ├── register.jsp
│       │   ├── admin/
│       │   │   ├── login.jsp
│       │   │   └── dashboard.jsp
│       │   │
│       │   ├── vehicle/
│       │   │   ├── list.jsp
│       │   │   ├── add.jsp
│       │   │   ├── edit.jsp
│       │   │   └── details.jsp
│       │   │
│       │   ├── booking/
│       │   │   ├── create.jsp
│       │   │   ├── list.jsp
│       │   │   └── details.jsp
│       │   │
│       │   ├── payment/
│       │   │   ├── upload.jsp
│       │   │   ├── confirmation.jsp
│       │   │   ├── approval-list.jsp
│       │   │   └── details.jsp
│       │   │
│       │   ├── review/
│       │   │   ├── add.jsp
│       │   │   └── list.jsp
│       │   │
│       │   ├── css/
│       │   │   └── style.css
│       │   │
│       │   └── js/
│       │       └── main.js
│       │
│       └── resources/
│           ├── data/
│           │   ├── vehicles.txt
│           │   ├── users.txt
│           │   ├── bookings.txt
│           │   ├── admins.txt
│           │   ├── payments.txt
│           │   └── reviews.txt
│           └── uploads/
│               └── payment-slips/
│
├── pom.xml
└── README.md
```

## Step-by-Step Development Guide

### Step 1: Set Up Project Environment

1. **Install Required Software:**
   - JDK (Java Development Kit) 8 or later
   - Apache Tomcat 9.x
   - Maven 3.x
   - IDE (Eclipse/IntelliJ IDEA)

2. **Create Maven Project:**
   - Create a new Maven webapp project
   - Configure pom.xml with necessary dependencies:
     - Servlet API
     - JSTL (JSP Standard Tag Library)
     - Jackson (for JSON processing)
     - Commons FileUpload (for handling payment slip uploads)
   - Commons IO (for file operations)
     - Logging framework (SLF4J + Logback)

3. **Configure web.xml:**
   - Set up servlet mappings for all components
   - Configure welcome files
   - Set session timeout

### Step 2: Create Model Classes

Define data models for each component:

1. **Vehicle.java**
   - Properties: id, type, make, model, year, registrationNumber, dailyRate, available, imageUrl, description
   - Methods: getters, setters, toString, equals, hashCode

2. **User.java**
   - Properties: id, username, password, fullName, email, phone, address, role, registrationDate
   - Methods: getters, setters, authentication methods

3. **Booking.java**
   - Properties: id, userId, vehicleId, startDate, endDate, totalCost, status, bookingDate
   - Methods: getters, setters, cost calculation logic

4. **Admin.java**
   - Properties: id, username, password, fullName, email, role, permissions
   - Methods: getters, setters, permission validation methods

5. **Payment.java**
   - Properties: id, bookingId, amount, paymentDate, paymentMethod, status, transactionId
   - Methods: getters, setters, payment validation methods

6. **Review.java**
   - Properties: id, userId, vehicleId, rating, comment, reviewDate
   - Methods: getters, setters

### Step 3: Implement Data Access Layer

Create DAO (Data Access Object) classes for file-based persistence:

1. **FileUtil.java**
   - Core file operations: read, write, append
   - Locking mechanisms for concurrent access

2. **DAO Implementation**
   - For each component (Vehicle, User, Booking, etc.)
   - Methods: create, read, update, delete
   - File format: Define a consistent format (CSV or custom format)
   - Implement data validation and error handling

### Step 4: Develop Controller Layer (Servlets)

Implement servlets for each component's CRUD operations:

1. **Vehicle Management**
   - AddVehicleServlet: Add new vehicles to the system
   - UpdateVehicleServlet: Update existing vehicle information
   - DeleteVehicleServlet: Remove vehicles from inventory
   - GetVehicleServlet: Retrieve vehicle details

2. **User Management**
   - RegisterServlet: Handle user registration
   - LoginServlet: Authenticate users
   - LogoutServlet: End user sessions
   - UpdateUserServlet: Modify user profiles

3. **Booking Management**
   - CreateBookingServlet: Create new rental bookings
   - UpdateBookingServlet: Modify booking details
   - CancelBookingServlet: Cancel existing bookings
   - ViewBookingServlet: Display booking information

4. **Admin Management**
   - AdminLoginServlet: Authenticate administrators
   - AdminDashboardServlet: Display system overview
   - ManageUsersServlet: Handle user administration

5. **Payment Management**
   - ProcessPaymentServlet: Handle payment slip uploads
   - ViewPaymentServlet: Display payment history
   - RefundServlet: Process refunds for cancellations
   - ApprovePaymentServlet: For admin approval of payments
   - RejectPaymentServlet: For admin rejection of payments

6. **Review System**
   - AddReviewServlet: Allow users to submit reviews
   - DeleteReviewServlet: Remove inappropriate reviews
   - ViewReviewsServlet: Display reviews for vehicles

### Step 5: Create View Layer (JSP Pages)

Develop JSP pages for user interface:

1. **Common Pages**
   - index.jsp: Landing page
   - login.jsp: User login form
   - register.jsp: User registration form
   - header.jsp & footer.jsp: Common layout elements

2. **Vehicle Pages**
   - list.jsp: Display all vehicles
   - add.jsp: Form to add new vehicles
   - edit.jsp: Form to edit vehicle details
   - details.jsp: Show vehicle information

3. **Booking Pages**
   - create.jsp: Form to create bookings
   - list.jsp: Show user's bookings
   - details.jsp: Display booking information

4. **Admin Pages**
   - login.jsp: Admin login form
   - dashboard.jsp: System overview
   - users.jsp: User management interface
   - vehicles.jsp: Vehicle management interface

5. **Payment Pages**
   - upload.jsp: Payment slip upload form
   - confirmation.jsp: Payment submission confirmation
   - approval-list.jsp: Admin payment approval dashboard
   - details.jsp: Payment details with approval status

6. **Review Pages**
   - add.jsp: Form to add reviews
   - list.jsp: Display vehicle reviews

### Step 6: Implement Authentication & Authorization

1. **User Authentication**
   - Login/logout functionality
   - Session management
   - Password encryption

2. **Role-based Access Control**
   - User roles: Regular user, Admin
   - Permission-based access to features
   - Filter implementation for protected resources

### Step 7: Develop Data Storage Structure

Create text file storage system:

1. **File Structure**
   - Separate text files for each entity type
   - Define consistent data format (CSV or custom format)
   - Implement proper file locking for concurrent access

2. **Data Format Example**
   - vehicles.txt: id,type,make,model,year,registrationNumber,dailyRate,available,imageUrl,description
   - users.txt: id,username,password,fullName,email,phone,address,role,registrationDate
   - bookings.txt: id,userId,vehicleId,startDate,endDate,totalCost,status,bookingDate
   - etc.

### Step 8: Implement Business Logic

1. **Booking Logic**
   - Check vehicle availability
   - Calculate rental costs
   - Apply discounts if applicable
   - Handle booking confirmation

2. **Payment Processing**
   - Handle payment slip uploads
   - Store uploaded slip file references
   - Admin payment verification workflow
   - Payment status tracking
   - Generate receipts after approval
   - Handle refunds

3. **Review System Logic**
   - Validate review submissions
   - Calculate average ratings
   - Filter inappropriate content

### Step 9: Add Validation and Error Handling

1. **Input Validation**
   - Form data validation
   - Data type checking
   - Required field validation

2. **Error Handling**
   - Custom error pages
   - User-friendly error messages
   - Logging of exceptions

### Step 10: Testing and Deployment

1. **Testing**
   - Unit testing for components
   - Integration testing for workflows
   - End-to-end testing for user journeys

2. **Deployment**
   - Package as WAR file
   - Deploy to Tomcat server
   - Configure server settings

## Key Implementation Details

### File Storage Implementation

For each entity type, implement a consistent file format:

```
// Example format for vehicles.txt
id|type|make|model|year|registrationNumber|dailyRate|available|imageUrl|description

// Example format for users.txt
id|username|password|fullName|email|phone|address|role|registrationDate

// Example format for bookings.txt
id|userId|vehicleId|startDate|endDate|totalCost|status|bookingDate
```

### Data Access Layer Example

The DAO classes should handle:
- Reading data from text files
- Writing updates back to files
- Maintaining data consistency
- Handling concurrent access

### Session Management

Implement session tracking to:
- Maintain user authentication status
- Store shopping cart information
- Track user activities

### Security Considerations

- Store passwords with proper hashing
- Implement input validation to prevent injection attacks
- Use proper session management
- Apply authorization checks for protected resources

## Development Tips

1. **Start with Core Components**
   - Begin with model classes and basic file operations
   - Implement Vehicle and User management first
   - Build other components incrementally

2. **Use MVC Pattern**
   - Model: Java beans for data representation
   - View: JSP pages for user interface
   - Controller: Servlets for request handling

3. **Implement File Locking**
   - Use Java's File locking mechanisms to prevent data corruption
   - Consider implementing a simple transaction system

4. **Error Handling Strategy**
   - Create a consistent error handling approach
   - Log errors to a file for debugging
   - Show user-friendly messages in the UI

## Technologies Used

- Java Servlet API
- JSP (JavaServer Pages)
- JSTL (JSP Standard Tag Library)
- HTML/CSS/JavaScript
- Maven (for project management)
- Apache Tomcat (servlet container)

## Prerequisites

- JDK 8 or higher
- Apache Tomcat 9.x
- Maven 3.6.x
- Basic knowledge of Java, Servlets, JSP, and MVC architecture

## Getting Started

1. Clone the repository
2. Run `mvn clean install` to build the project
3. Deploy the generated WAR file to Tomcat
4. Access the application at http://localhost:8080/VehicleRentalApp/
