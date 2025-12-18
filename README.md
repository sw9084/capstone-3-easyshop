# Capstone 3 – EasyShop (Backend + Frontend)

This project contains a Java Spring Boot backend and a static HTML/CSS/JavaScript frontend.

## 📌 Overview

EasyShop is a full-stack e-commerce web application built as part of the Year Up / Pluralsight Capstone 3 project.

The application allows users to browse products, register and log in securely, manage a user profile, add items to a shopping cart, and place orders.

This project demonstrates real-world backend and frontend integration using **Java Spring Boot**, **MySQL**, and a **vanilla JavaScript frontend**.

---

                                                    ## 🛠️ Technologies Used

               ### Backend

  ➡️Java 17

- Spring Boot

- Spring Security (JWT Authentication)

- Maven

- MySQL

- JDBC / DAO pattern

                ### Frontend

- HTML

- CSS

- JavaScript (Vanilla JS)

- Live Server (VS Code)

               ### Tools

- IntelliJ IDEA

- Visual Studio Code

- MySQL Workbench

- Insomnia (API testing)

---

             ## 🧱 Project Structure

   capstone-3
│

     ├── backend-api

│   ├── controllers

│   ├── data (DAOs)

│   ├── models

│   ├── security

│   └── EasyshopApplication.java

│

├── frontend-ui

│   ├── css

│   ├── images

│   ├── js

│   ├── templates

│   └── index.html

│

└── README.md
 
---

             ## 🔐 Authentication & Security

   - User authentication is handled using **JWT (JSON Web Tokens)**

  - Passwords are securely hashed

  - Protected endpoints require a valid token

  - Spring Security ensures role-based access control

          ### Auth Endpoints

                | Method | Endpoint | Description |

                 |------|---------|-------------|

           | POST | `/register` | Create a new user |

           | POST | `/login` | Authenticate user & receive JWT |

---

         ## 👤 User Features

- Register a new account

- Log in and receive a JWT token

- View and update user profile

- Secure session handling on the frontend

---

## 🛒 Shopping Features

- View all products

- Filter products by category and price

- Add products to shopping cart

- View cart

- Clear cart

- Create an order from cart items

---

## 🔁 API Endpoints (Core)

### Categories

GET /categories

### Products

GET /products

GET /products/{id}

### Cart

GET    /cart

POST   /cart/products/{productId}

DELETE /cart

### Orders

POST /orders
 
---

## 🧪 Testing

- All backend endpoints were tested using **Insomnia**

- Authentication, authorization, and error handling were validated

- Frontend behavior was tested through the browser UI

---

## 🚀 Running the Application

### Backend

1. Open `backend-api` in IntelliJ

2. Ensure MySQL is running

3. Update `application.properties` with database credentials

4. Run `EasyshopApplication`

Backend runs on:
 
---

### Frontend

1. Open `frontend-ui` in VS Code

2. Right-click `index.html`

3. Select **Open with Live Server**

Frontend runs on:
http://127.0.0.1:5500
 
---

## 🧠 What I Learned

- How to build a REST API using Spring Boot

- Implementing JWT authentication

- Using the DAO pattern for database access

- Connecting frontend JavaScript to a secured backend

- Managing user sessions and authorization

- Debugging full-stack issues across frontend and backend

---

## 🌟 Author

**Bethlehem**

Year Up United – Application Development Track

Capstone 3 Project
 
