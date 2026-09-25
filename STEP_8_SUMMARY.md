# Step 8: Spring Security + JWT

## What We Did

1. **Added JWT Utility**: Created a `JwtUtil` class to handle JWT token generation and validation using the jjwt library.

2. **Created Custom UserDetails**: Created a `CustomUserDetails` class that implements Spring Security's `UserDetails` interface and wraps the `User` entity to provide user details and authorities (roles) for authentication.

3. **Created UserDetailsService Implementation**: Created a `UserDetailsServiceImpl` class that implements Spring Security's `UserDetailsService` interface and uses the `UserService` to load a user by username or email.

4. **Created JWT Request Filter**: Created a `JwtRequestFilter` class that extends `OncePerRequestFilter` to intercept incoming requests, extract the JWT token from the Authorization header, validate it, and set the authentication in the SecurityContext if the token is valid.

5. **Created Security Configuration**: Created a `SecurityConfig` class that configures HttpSecurity to:
   - Disable CSRF (since we are using JWT and stateless authentication).
   - Set session management to stateless.
   - Authorize requests based on roles:
        - `/api/auth/**` (registration and login) -> permitAll
        - `/api/products/**` (product listing, search, etc.) -> permitAll (so everyone can view products)
        - `/api/cart/**` -> authenticated
        - `/api/orders/**` -> authenticated
        - `/api/admin/**` -> hasRole("ADMIN")
   - Add the JWT request filter before the UsernamePasswordAuthenticationFilter.
   - Configure an authentication entry point to return 401 for unauthenticated requests.
   - Provide a PasswordEncoder bean (BCrypt) and AuthenticationManager bean.

6. **Updated AuthController**: Modified the login endpoint to:
   - Accept a username or email and password.
   - Use the UserService to find the user by username or email.
   - Validate the password using the PasswordEncoder.
   - If valid, generate a JWT token using the JwtUtil and return it in the response.
   - If invalid, return a 400 (Bad Request) with an error message.

7. **Updated Exception Handling**: Ensured that the global exception handler (from Step 7) works with the security configuration to handle exceptions appropriately.

## How to Test

To test the authentication and authorization:

   - Register a new user via POST /api/auth/register (should return the user details).
   - Login via POST /api/auth/login with the username or email and password (should return a JWT token).
   - Use the token in the Authorization header as "Bearer <token>" to access protected endpoints:
        - GET /api/cart (should return the cart for the logged-in user).
        - GET /api/orders (should return the orders for the logged-in user).
        - GET /api/admin/orders (should return 403 if the user is not an admin).
   - Try to access a protected endpoint without a token or with an invalid token (should return 401 or 403).

## Current Backend Structure (Added in this step)
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── ecommerce/
│   │   │               ├── security/
│   │   │               │   ├── CustomUserDetails.java
│   │   │               ├── JwtUtil.java
│   │   │               ├── JwtRequestFilter.java
│   │   │               ├── SecurityConfig.java
│   │   │               └── UserDetailsServiceImpl.java
│   │   │               └── exception/
│   │   │                   ├── DuplicateResourceException.java
│   │   │                   └── ResourceNotFoundException.java
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
In Step 9, we will:
- Write backend unit tests using JUnit 5 and Mockito for important service-layer functionality.
- Test user registration, login, product CRUD, cart operations, order creation, and error scenarios.

In Step 10, we will:
- Set up the React frontend project.
- Create the frontend components and pages.
- Implement API integration with Axios.
- Add authentication and protected routes in the frontend.