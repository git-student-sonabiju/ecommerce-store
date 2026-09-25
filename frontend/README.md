# E-Commerce Frontend

This is the React frontend for the E-Commerce Full Stack Web Application.

## Overview
This frontend will consume the backend APIs to provide a complete shopping experience.

## Backend API Endpoints
The backend provides the following APIs (running on http://localhost:8080):

### Authentication
- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login and receive JWT token

### Products
- GET `/api/products` - Get all products
- GET `/api/products/{id}` - Get product by ID
- POST `/api/products` - Create product (admin only)
- PUT `/api/products/{id}` - Update product (admin only)
- DELETE `/api/products/{id}` - Delete product (admin only)
- GET `/api/products/category/{categoryId}` - Get products by category
- GET `/api/products/search?keyword={term}` - Search products

### Cart
- GET `/api/cart` - Get current user's cart
- POST `/api/cart/items` - Add item to cart
- PUT `/api/cart/items/{itemId}` - Update cart item quantity
- DELETE `/api/cart/items/{itemId}` - Remove item from cart
- DELETE `/api/cart` - Clear cart

### Orders
- POST `/api/orders` - Create order from cart
- GET `/api/orders` - Get user's order history
- GET `/api/orders/{id}` - Get order by ID
- PUT `/api/orders/{id}/status` - Update order status (admin only)

### Admin
- GET `/api/admin/orders` - Get all orders (admin only)

## Getting Started

### Prerequisites
- Node.js (v18 or higher)
- npm (v9 or higher)

### Installation
```bash
npm install
```

### Development Server
```bash
npm start
```
Runs the app in development mode at http://localhost:3000

### Building for Production
```bash
npm run build
```
Bundles the app into static files in the `build` folder.

## Project Structure
```
src/
├── components/     # Reusable components
├── pages/          # Page components
├── services/       # API service calls
├── styles/         # CSS files
├── utils/          # Utility functions
├── contexts/       # React Context for state management
├── hooks/          # Custom React hooks
└── App.js          # Main application component
```

## Recommended Implementation Order

1. **Setup**
   - Configure API service with base URL and interceptors for JWT
   - Set up routing (react-router-dom)
   - Implement state management (Context API or Redux)

2. **Authentication**
   - Login/Register pages
   - JWT token storage (localStorage or cookies)
   - Auth context/provider
   - Protected routes

3. **Core Features**
   - Product listing page
   - Product detail page
   - Shopping cart
   - Checkout process

4. **User Features**
   - Profile page
   - Order history

5. **Admin Features**
   - Admin dashboard
   - Product management
   - Order management

6. **Polishing**
   - Responsive design
   - Loading states and error handling
   - Form validation
   - Styling and UI/UX improvements

## Available Scripts

In the project directory, you can run:

- `npm start` - Runs the app in development mode
- `npm run build` - Builds the app for production
- `npm test` - Launches the test runner
- `npm run eject` - Removes the build tool and copies dependencies (not recommended)

## Environment Variables
Create a `.env` file in the root directory:
```
REACT_APP_API_URL=http://localhost:8080/api
```

## Dependencies to Consider Installing
- `axios` - For HTTP requests
- `react-router-dom` - For routing
- `react-redux` or context API - For state management
- `formik` or `react-hook-form` - For form handling
- `bootstrap` or `material-ui` - For UI components (optional)