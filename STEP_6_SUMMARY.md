# Step 6: REST Controllers

## What We Did

1. **Created REST Controllers**: We created controller classes to handle HTTP requests and map them to service methods. The controllers are located in the `com.example.ecommerce.controller` package and include:
   - AuthController: handles user registration and login.
   - ProductController: handles CRUD operations for products, product listing by category, and product search.
   - CartController: handles cart operations (get cart, add item, update item, remove item, clear cart).
   - OrderController: handles order operations (get order by id, get orders by user, get all orders, update order status).
   - AdminController: handles admin-specific operations (get all products, get all orders, update order status).

2. **Used Dependency Injection**: Each controller is initialized with the necessary service classes via constructor injection.

3. **Mapped HTTP Methods to Service Methods**: We used the appropriate HTTP verbs (GET, POST, PUT, DELETE) and mapped them to the corresponding service operations.

4. **Returned Appropriate Response Types**: We used ResponseEntity to return appropriate HTTP status codes and response bodies.

## How to Test

The controllers can be tested by running the application and using a tool like Bruno, Postman, or curl to send HTTP requests to the endpoints. However, note that we have not yet implemented Spring Security and JWT, so the endpoints are not secured. We will add security in Step 8.

## Current Backend Structure (Added in this step)
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── ecommerce/
│   │   │               └── controller/
│   │   │                   ├── AuthController.java
│   │   │                   ├── ProductController.java
│   │   │                   ├── CartController.java
│   │   │                   ├── OrderController.java
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
In Step 7, we will:
- Implement global exception handling to handle validation errors, entity not found exceptions, and other runtime exceptions.
- Create a @ControllerAdvice class that catches exceptions and returns appropriate error responses.

In Step 8, we will:
- Implement Spring Security and JWT to secure the endpoints.
- Configure authentication and authorization for the endpoints.