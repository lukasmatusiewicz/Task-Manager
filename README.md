# 🚀 Modern Task Manager - Portfolio Project

A comprehensive, full-stack Task Management application built with **Spring Boot 3** and **Java 21**. This project serves as a showcase of modern software engineering practices, featuring a layered architecture, robust security, and a responsive, interactive UI.

---

## 🧪 How to Test

This project is designed to be easily testable by anyone, whether they are a developer, a recruiter, or a manual tester.

### **1. The Full Experience (Web UI)**
*   **Register a New Account**: Navigate to `http://localhost:8080/register` and create your own user.
*   **Manage Your Tasks**: Add, edit, and delete tasks. Notice the **AJAX-powered deletion** that happens instantly without a page reload.
*   **Security Check**: Try to access `http://localhost:8080/tasks` without logging in—you'll be automatically redirected to the secure login page.

### **2. Interactive API Testing (Swagger)**
*   Navigate to `http://localhost:8080/swagger-ui.html`.
*   Explore all documented REST endpoints.
*   Click **"Try it out"** on any endpoint (like `POST /api/tasks`) to send real JSON requests and see the live responses directly in your browser.

### **3. Database Inspection (H2 Console)**
*   While running in development mode, visit `http://localhost:8080/h2-console`.
*   **JDBC URL**: `jdbc:h2:mem:testdb`
*   **Username**: `sa` | **Password**: `password`
*   Run `SELECT * FROM TASKS;` to see how your data is being mapped by Hibernate.

### **4. Containerized Portability (Docker)**
*   No Java installed? No problem. Run `docker-compose up --build` to spin up both the application and a **PostgreSQL** database in isolated containers.

---

## 🛠️ Tech Stack & Skills Showcased

### **Backend (Java/Spring Boot)**
*   **Java 21**: Leveraging the latest LTS features.
*   **Spring Boot 3.4.3**: High-performance backend framework.
*   **Spring Security**: Robust authentication and role-based access control (USER/ADMIN), with CSRF protection and secure password hashing (BCrypt).
*   **Spring Data JPA (Hibernate)**: Advanced ORM for seamless database interaction.
*   **Spring AOP**: Aspect-Oriented Programming for cross-cutting concerns (e.g., custom `LoggingAspect`).
*   **Validation**: Implementation of the **Bean Validation API** (`@Valid`, `@NotBlank`, `@Size`) for data integrity.
*   **Global Error Handling**: Centralized exception management using `@ControllerAdvice` for a consistent user experience.

### **Database & Persistence**
*   **PostgreSQL**: Production-grade persistent database.
*   **H2 Database**: In-memory database used for local development and rapid testing.
*   **Dockerized Storage**: Database persistence maintained via Docker Volumes.

### **Frontend & UX**
*   **Thymeleaf**: Modern server-side Java template engine.
*   **Bootstrap 5**: Responsive, mobile-first design for a professional look.
*   **AJAX (Fetch API)**: Interactive, "snappy" UI features like task deletion without full-page reloads.

### **DevOps & Documentation**
*   **Docker & Docker Compose**: Full containerization for one-command environment setup.
*   **SpringDoc OpenAPI / Swagger**: Automated API documentation and interactive testing environment.
*   **Maven**: Dependency management and build automation.

---

## ✨ Key Features
*   **Full CRUD Cycle**: Create, Read, Update, and Delete tasks.
*   **User Lifecycle**: Public registration and secure login system.
*   **Task Metadata**: Categorize tasks by **Status** (TODO, DONE) and **Priority** (HIGH, MEDIUM, LOW).
*   **Interactive UI**: Smooth, JavaScript-powered deletions with real-time DOM updates.
*   **RESTful API**: Comprehensive API endpoints for programmatic task management, fully documented with Swagger.

---

## 🚀 Quick Start (Docker)

1.  **Clone and build the application**:
    ```bash
    ./mvnw clean package -DskipTests
    ```
2.  **Spin up the environment**:
    ```bash
    docker-compose up --build
    ```
3.  **Access the application**:
    *   **UI**: `http://localhost:8080/tasks`
    *   **API Docs (Swagger)**: `http://localhost:8080/swagger-ui.html`
    *   **Database (H2 Console)**: `http://localhost:8080/h2-console` (Username: `sa`, Password: `password`)

---

## 🏗️ Architectural Overview
This project follows a clean **Layered Architecture**:
*   **Controller Layer**: Handles HTTP requests and manages UI/REST transitions.
*   **Service Layer**: Encapsulates business logic and security checks.
*   **Repository Layer**: Interacts with the data source using JPA.
*   **Model Layer**: Defines the core entities and validation rules.

---
*Note: This is a portfolio project designed to demonstrate clean code, security best practices, and a modern Java/Spring stack.*
