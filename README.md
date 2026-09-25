# ecommerce-store

E-Commerce Full Stack Application

This is a full-stack e-commerce application built with Java Spring Boot (backend) and React (frontend).

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Web / REST APIs
- Spring Data JPA
- Hibernate
- Spring Security
- JWT authentication
- Maven
- JUnit 5 + Mockito

### Frontend
- React
- JavaScript (ES6+)
- HTML5
- CSS3
- Axios
- React Router

### Database
- PostgreSQL

### Tools
- Git/GitHub
- Bruno or Postman
- Docker

## Project Structure

```
ecommerce-fullstack/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── frontend/
│   ├── public/
│   │   └── index.html
│   └── src/
│       ├── components/
│       ├── pages/
│       ├── services/
│       ├── contexts/
│       ├── hooks/
│       ├── utils/
│       ├── styles/
│       └── assets/
│           ├── index.js
│           └── index.css
├── docker-compose.yml
└── README.md
```

## Getting Started

### Prerequisites
- Java 21
- Node.js (v16 or higher)
- PostgreSQL
- Docker and Docker Compose

### Backend Setup
1. Navigate to the `backend` directory
2. Configure PostgreSQL connection in `src/main/resources/application.properties`
3. Build and run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup
1. Navigate to the `frontend` directory
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   ```

### Docker Setup
1. Ensure Docker and Docker Compose are installed
2. From the root directory, run:
   ```bash
   docker-compose up --build
   ```

## Features
- User registration and login
- JWT-based authentication
- Role-based authorization (USER and ADMIN)
- Product CRUD operations
- Product search and filtering
- Product categories
- Shopping cart
- Add/update/remove cart items
- Place orders
- View order history
- View order details
- Admin product management
- Admin order management
- Update order status (PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

## API Documentation
API endpoints are documented in the backend controller classes.

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.
