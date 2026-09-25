# ecommerce-store

A simple online store where customers can browse products, add them to a cart, place orders, and see or cancel eligible orders.

## What you can do

- Create an account and sign in.
- Browse products and add them to your cart.
- Change quantities or remove items before checkout.
- Place an order and view its items and status under **My Orders**.
- Cancel an order while its status is **PLACED** or **CONFIRMED**.
- Store administrators can manage products and update order statuses.

## Run the store

The easiest way to start the website is with Docker Desktop.

### Before you start

Install and open [Docker Desktop](https://www.docker.com/products/docker-desktop/). The first start needs an internet connection so Docker can download the required components.

### Configure the login service

For security, the backend settings file is not included in the repository. Before starting the store, make a copy of `backend/src/main/resources/application.example.properties` and name the copy `application.properties` in the same folder. Open the copy and replace the example JWT secret with a private random value at least 32 characters long. Keep `application.properties` private; it is excluded from Git.

### Start the website

Open a terminal in the project folder and run:

```bash
docker compose up --build -d
```

When the containers have started, open [http://localhost:3000](http://localhost:3000). Create an account with **Register**, sign in, and start shopping.

To stop the website, run this from the project folder:

```bash
docker compose down
```

## Project folders

- `frontend/` contains the website customers use.
- `backend/` contains the services that manage accounts, products, carts, and orders.
- `docker-compose.yml` starts the website, backend, and database together.

## Technologies

The website uses React. The backend uses Java and Spring Boot. PostgreSQL stores product, account, cart, and order data. Docker runs the parts together.
