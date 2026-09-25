# Step 3: Database and Entities

## What We Did

1. **Designed the Database Schema**: Identified the core entities for the e-commerce application:
   - User
   - Role
   - Category
   - Product
   - Cart
   - CartItem
   - Order
   - OrderItem

2. **Created JPA Entities**: Created entity classes for each of the above, complete with:
   - Appropriate annotations (@Entity, @Table, @Id, @Column, @ManyToOne, @OneToMany, etc.)
   - Relationships between entities:
     - User ↔ Role (ManyToMany)
     - User → Cart (OneToOne)
     - Cart → CartItem (OneToMany)
     - Product ←→ Category (ManyToOne)
     - Product ←→ OrderItem (OneToMany)
     - User → Order (OneToMany)
     - Order → OrderItem (OneToMany)
   - Common fields: id, timestamps (createdAt, updatedAt)
   - Enums for RoleName and OrderStatus
   - Used Lombok to reduce boilerplate (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
   - Added pre-persist and pre-update callbacks for automatic timestamp setting

3. **Created Spring Data JPA Repositories**: For each entity, created a repository interface extending JpaRepository, with custom query methods where needed:
   - UserRepository: find by username/email, existence checks
   - RoleRepository: find by role name
   - CategoryRepository: find by name
   - ProductRepository: find by name, by category, search by keyword
   - CartRepository: find by user ID
   - CartItemRepository: basic CRUD
   - OrderRepository: find by ID, by user ID, by status
   - OrderItemRepository: basic CRUD

## How to Test

1. Ensure you have PostgreSQL installed and running.
2. Create a database named `ecommerce_db` (or update the credentials in `application.properties`).
3. Update `application.properties` with your PostgreSQL username and password if different.
4. Run the application: `mvn spring-boot:run`.
5. The application will start and create the tables automatically (due to `spring.jpa.hibernate.ddl-auto=update`).
6. You can verify the tables were created by connecting to the database and checking the schema.

## Current Backend Structure (Added in this step)
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── ecommerce/
│   │   │               ├── entity/
│   │   │               │   ├── User.java
│   │   │               │   ├── Role.java
│   │   │               │   ├── RoleName.java
│   │   │               │   ├── Category.java
│   │   │               │   ├── Product.java
│   │   │               │   ├── Cart.java
│   │   │               │   ├── CartItem.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderStatus.java
│   │   │               │   └── OrderItem.java
│   │   │               └── repository/
│   │   │                   ├── UserRepository.java
│   │   │                   ├── RoleRepository.java
│   │   │                   ├── CategoryRepository.java
│   │   │                   ├── ProductRepository.java
│   │   │                   ├── CartRepository.java
│   │   │                   ├── CartItemRepository.java
│   │   │                   ├── OrderRepository.java
│   │   │                   └── OrderItemRepository.java
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
In Step 4, we will:
- Create DTOs (Data Transfer Objects) to avoid exposing entities directly through REST APIs
- Create service layers that encapsulate business logic
- Implement the service classes for each entity