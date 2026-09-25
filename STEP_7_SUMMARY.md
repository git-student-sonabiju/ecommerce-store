# Step 7: Exception Handling and Validation

## What We Did

1. **Created Custom Exceptions**: We created two custom exception classes to handle specific error scenarios:
   - `ResourceNotFoundException`: thrown when a requested resource (e.g., user, product, order) is not found.
   - `DuplicateResourceException`: thrown when attempting to create a resource that already exists (e.g., duplicate username or email).

2. **Updated Service Classes**: We modified the service classes to throw the appropriate exceptions:
   - UserService: throws `DuplicateResourceException` for duplicate username/email, and `ResourceNotFoundException` for missing role.
   - ProductService: throws `ResourceNotFoundException` for missing category or product.
   - CartService: throws `ResourceNotFoundException` for missing user, product, or cart.
   - OrderService: throws `ResourceNotFoundException` for missing user, product, order, or invalid order status.

3. **Created Global Exception Handler**: We created a `@RestControllerAdvice` class (`GlobalExceptionHandler`) that handles exceptions globally and returns appropriate HTTP responses:
   - `ResourceNotFoundException` → HTTP 404 (Not Found)
   - `DuplicateResourceException` → HTTP 409 (Conflict)
   - `MethodArgumentNotValidException` (triggered by validation annotations in DTOs) → HTTP 400 (Bad Request) with field-level error messages
   - Any other unhandled exception → HTTP 500 (Internal Server Error)

## How to Test

The exception handling can be tested by sending invalid requests to the endpoints, such as:
   - Trying to register a user with an existing username or email (should return 409).
   - Trying to retrieve a non-existent product by ID (should return 404).
   - Sending a request with invalid data (e.g., missing required fields, invalid email format) (should return 400 with validation errors).

## Current Backend Structure (Added in this step)
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── ecommerce/
│   │   │               ├── exception/
│   │   │               │   ├── DuplicateResourceException.java
│   │   │               │   └── ResourceNotFoundException.java
│   │   │               └── controller/
│   │   │                   ├── AuthController.java
│   │   │                   ├── CartController.java
│   │   │                   ├── GlobalExceptionHandler.java
│   │   │                   ├── OrderController.java
│   │   │                   ├── ProductController.java
│   │   │                   └── AdminController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── ecommerce/
│                       └── EcommerceBackendApplicationTests.java
├── pom.xml
```

## Next Steps
In Step 8, we will:
- Implement Spring Security and JWT to secure the endpoints.
- Configure authentication and authorization for the endpoints.
- Create a utility class for JWT token generation and validation.
- Update the AuthController to return a JWT token upon successful login.
- Secure the endpoints based on roles (USER and ADMIN).