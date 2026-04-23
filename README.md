# 🚀 Modern Task Manager - Portfolio Project

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

A comprehensive, full-stack Task Management application built with **Spring Boot 3** and **Java 21**. This project serves as a showcase of modern software engineering practices, featuring a layered architecture, robust security, and a responsive, interactive UI.

---

## 🧪 How to Test

This project is designed to be easily testable by anyone, whether they are a developer, a recruiter, or a manual tester.

### **1. The Full Experience (Web UI)**
*   **Register a New Account**: Navigate to `http://localhost:8080/register` and create your own user.
*   **Explore the Dashboard**: Visit the new **Dashboard** to see live statistics like pending tasks, overdue items, and your weekly completion rate. Check out the interactive charts for status and priority distribution.
*   **Manage Your Tasks**: Add tasks with **Due Dates**. Use the new **Quick Toggle** (Done/Undone) to update task status instantly from the list.
*   **Security Check**: Try to access `http://localhost:8080/tasks` without logging in—you'll be automatically redirected to the secure login page.

### **2. Interactive API Testing (Swagger)**
*   Navigate to `http://localhost:8080/swagger-ui.html`.
*   Explore all documented REST endpoints.
*   Click **"Try it out"** on any endpoint (like `POST /api/tasks`) to send real JSON requests and see the live responses directly in your browser.

### **3. Database Inspection (PostgreSQL)**
*   Use a tool like **pgAdmin 4** or **DBeaver** to connect to your local database.
*   **Host**: `localhost` | **Port**: `5432`
*   **Database**: `taskmanager` | **User**: `postgres`
*   Run `SELECT * FROM users;` or `SELECT * FROM task;` to see your persistent data.

### **4. Containerized Portability (Docker)**
*   No Java installed? No problem. Run `docker-compose up --build` to spin up both the application and a **PostgreSQL** database in isolated containers.

### **5. CI/CD with GitHub Actions**
*   The project includes a GitHub Actions workflow (`maven.yml`) that automatically builds the project and runs all tests on every push or pull request to the `master`, `main`, or `dockerization` branches.

---

## 🧪 Testing

The project includes a suite of unit tests for the service layer, ensuring business logic correctness.

*   **TaskServiceTest**: Tests CRUD operations, user-based task access, and validation logic.
*   **UserServiceTest**: Tests user registration, password encryption, and Spring Security's `UserDetailsService` implementation.

To run the tests locally, use:
```bash
./mvnw test
```

---

## 🛠️ Tech Stack & Skills Showcased

### **Backend (Java/Spring Boot)**
*   **Java 21**
*   **Spring Boot 3.4.3**: High-performance backend framework.
*   **Spring Security**: Robust authentication and role-based access control (USER/ADMIN), with CSRF protection and secure password hashing (BCrypt).
*   **Spring Data JPA (Hibernate)**: Advanced ORM for seamless database interaction.
*   **Spring AOP**: Aspect-Oriented Programming for cross-cutting concerns (e.g., custom `LoggingAspect`).
*   **Validation**: Implementation of the **Bean Validation API** (`@Valid`, `@NotBlank`, `@Size`) for data integrity.
*   **Global Error Handling**: Centralized exception management using `@ControllerAdvice` for a consistent user experience.

### **Database & Persistence**
*   **PostgreSQL**: Production-grade persistent database.
*   **Dockerized Storage**: Database persistence maintained via Docker Volumes.

### **Frontend & UX**
*   **Thymeleaf**: Modern server-side Java template engine.
*   **Bootstrap 5**: Responsive, mobile-first design for a professional look.
*   **Chart.js**: Interactive data visualization for task statistics.
*   **AJAX (Fetch API)**: Interactive, "snappy" UI features like task deletion and status toggling without full-page reloads.

### **DevOps & Documentation**
*   **Docker & Docker Compose**: Full containerization for one-command environment setup.
*   **SpringDoc OpenAPI / Swagger**: Automated API documentation and interactive testing environment.
*   **Maven**: Dependency management and build automation.

---

## ✨ Key Features
*   **Interactive Dashboard**: Real-time summary of pending, overdue, and recently completed tasks with visual charts.
*   **Dark Mode Support**: Full dark theme support with persistence across sessions using `localStorage`.
*   **Full CRUD Cycle**: Create, Read, Update, and Delete tasks.
*   **Due Date Tracking**: Set deadlines for tasks and track overdue items.
*   **Quick Status Toggle**: Instantly mark tasks as Done or Undone from the main list view.
*   **User Lifecycle**: Public registration and secure login system.
*   **Task Metadata**: Categorize tasks by **Status** (TODO, DONE) and **Priority** (HIGH, MEDIUM, LOW).
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
    *   **Database (PostgreSQL)**: Use pgAdmin 4 or your preferred SQL client at `localhost:5432`.

---

## 🏗️ Architectural Overview
This project follows a clean **Layered Architecture**:
*   **Controller Layer**: Handles HTTP requests and manages UI/REST transitions.
*   **Service Layer**: Encapsulates business logic and security checks.
*   **Repository Layer**: Interacts with the data source using JPA.
*   **Model Layer**: Defines the core entities and validation rules.

---
*Note: This is a portfolio project designed to demonstrate clean code, security best practices, and a modern Java/Spring stack.*
