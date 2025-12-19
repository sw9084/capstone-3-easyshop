# Capstone 3 – EasyShop (Backend + Frontend)

This project contains a Java Spring Boot backend and a static HTML/CSS/JavaScript frontend.

## 📌 Overview



                       ## Project Description

This project is a Spring Boot REST API for an e-commerce application called EasyShop. The goal of this project was to 
build the backend services that support user authentication, shopping cart functionality, and order checkout.

Users can register and log in to receive a JWT token, which is required to access protected endpoints.
Authenticated users can add products to their shopping cart, view the cart, and create an order from the cart. 
Once an order is created, the shopping cart is cleared. The project uses a MySQL database and follows a DAO-based 
architecture to separate database logic from controllers. All endpoints were tested using Insomnia.


                     ## Technologies Used

    - Java

    - Spring Boot

    - Spring Security (JWT Authentication)

    - MySQL

    - JDBC / DAO Pattern

    - Maven

    - Insomnia

---

## Features

- User registration and login

- JWT-based authentication

- Add products to shopping cart

- View shopping cart

- Create orders from shopping cart

- Clear cart after successful checkout

- Secure endpoints using authentication

---

## Authentication

- Users log in using the `/login` endpoint

- A JWT token is returned on successful login

- Protected endpoints require the following header:

Authorization: Bearer <token>

The authenticated user is accessed using `Principal`.

---

## API Endpoints

### Authentication

POST /register

POST /login

### Shopping Cart

POST /cart/products/{productId}

GET /cart

DELETE /cart

### Orders

POST /orders

GET /orders

---

## Implementation Notes

- JWT authentication is required for cart and order endpoints

- Orders cannot be created if the shopping cart is empty

- Order creation process:

    - Create the order

    - Insert order line items

    - Clear the shopping cart

- SQL queries use prepared statements

- Reserved SQL keywords are handled correctly

- Error handling uses appropriate HTTP status codes

---

## Testing

All endpoints were tested using Insomnia.

Testing flow:

1. Register or log in

2. Store JWT token in the Insomnia environment

3. Add products to the shopping cart

4. Create an order

---

## How to Run the Project

1. Clone the repository

2. Create a MySQL database

3. Configure database credentials in application.properties

4. Run the Spring Boot application

5. Test endpoints using Insomnia

---

## What I Learned

- How to build a REST API using Spring Boot

- How JWT authentication works in a backend application

- How to secure endpoints using Spring Security

- How to use the DAO pattern with JDBC and MySQL

- How to debug authentication issues versus server or SQL errors

- How to test API endpoints using Insomnia

---

## Challenges Faced

One of the biggest challenges was debugging errors during order creation.

At first, authentication and cart functionality were working, but order creation

failed with a server error. This required tracing the request through the controller,

DAO, and SQL queries.

I learned how important it is to carefully debug SQL syntax and understand how

database errors can surface only after authentication succeeds. This project also

helped me become more confident in troubleshooting full-stack backend issues.

---

## Project Status

Capstone 3 requirements completed and fully tested.
 