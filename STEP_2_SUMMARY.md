# Step 2: Spring Boot Backend Setup

## What We Did

1. **Created Maven Project Structure**: Set up the standard Maven directory layout under `backend/`.
2. **Configured pom.xml**: Added dependencies for:
   - Spring Boot Starter Web
   - Spring Boot Starter Data JPA
   - Spring Boot Starter Security
   - PostgreSQL Driver
   - Lombok (optional)
   - Spring Boot Starter Validation
   - Spring Boot Starter Test
   - JWT (jjwt)
3. **Created Main Application Class**: `EcommerceBackendApplication.java` in package `com.example.ecommerce`.
4. **Configured application.properties**: Set up:
   - Server port (8080)
   - PostgreSQL connection details (database `ecommerce_db` - to be created)
   - JPA/Hibernate properties
   - JWT secret and expiration
   - Logging levels

## How to Test

1. Ensure you have Java 21 and Maven installed.
2. Navigate to the `backend` directory.
3. Run `mvn spring-boot:run`.
4. The application should start and embed Tomcat on port 8080.
5. You will see a log similar to:
   ```
   :: Spring Boot ::                (v3.2.0)
   ...
   Tomcat started on port 8080 (http) with context path ''
   Started EcommerceBackendApplication in X seconds
   ```
6. Note: The application will fail to connect to the PostgreSQL database if it doesn't exist. You'll see an error like `FATAL: database "ecommerce_db" does not exist`. However, the Spring Boot framework still initializes and starts the web server. For full functionality, create the PostgreSQL database `ecommerce_db` with username `postgres` and password `postgres` (or update the credentials in application.properties).

## Current Backend Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── ecommerce/
│   │   │               └── EcommerceBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── ecommerce/
│       │               └── EcommerceBackendApplicationTests.java
│       └── resources/
└── pom.xml
```

## Next Steps
In Step 3, we will:
- Design the database schema
- Create JPA entities for User, Product, Category, Cart, CartItem, Order, OrderItem
- Set up relationships between entities
- Create repositories for each entity