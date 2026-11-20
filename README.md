# Task Manager

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

A full-stack task management web application and REST API built with **Spring Boot 3.4.3** and **Java 21**.

---

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.4.3, Spring Security 6, Spring Data JPA (Hibernate), Spring AOP (`LoggingAspect`), Bean Validation, Spring Boot Actuator
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js, Fetch API, `localStorage` dark-mode toggle
- **Database**: PostgreSQL 16 (runtime / Docker), H2 in-memory in PostgreSQL mode (tests / `test` profile)
- **API Docs**: SpringDoc OpenAPI 2.8.5 (`/swagger-ui.html`, `/v3/api-docs`)
- **Build & CI**: Maven Wrapper (`mvnw` / `mvnw.cmd`), JUnit 5, Mockito, `MockMvc`, JaCoCo 0.8.12, GitHub Actions

---

## Architecture & Security Overview

The application follows a layered structure (`Controller` -> `Service` -> `Repository` -> `Model`/`DTO`):

- **Authentication & Authorization (`SecurityConfig`)**: Session-based authentication (`formLogin` at `/login`, defaulting to `/tasks`, with `/` redirecting to `/tasks`). Public endpoints are limited to `/login`, `/register/**`, `/styles.css`, `/favicon.ico`, `/error`, `/actuator/health`, `/swagger-ui/**`, and `/v3/api-docs/**`. `/tasks/**`, `/dashboard/**`, and `/api/tasks/**` require `USER` or `ADMIN`; `/users/**` requires `ADMIN`; all other requests require authentication.
- **CSRF Protection**: Enabled across both UI forms and `/api/**` endpoints using `CookieCsrfTokenRepository.withHttpOnlyFalse()` and `CsrfTokenRequestAttributeHandler`.
- **DTO Contracts & Registration**: REST endpoints (`/api/tasks`, `/users`) accept and return explicit DTOs (`TaskRequest`, `TaskResponse`, `AdminCreateUserRequest`, `UserResponse`) so JPA entities and password hashes are never serialized. Public registration (`/register`) binds `RegisterRequest` (without a `role` field), always assigns `Role.USER` server-side, and reports duplicate usernames as a field validation error.
- **Separated Error Handling**: `RestExceptionHandler` (`@RestControllerAdvice` scoped to `TaskController` and `UserController`) returns structured JSON `ApiError` responses (`400`, `403`, `404`, `409`, `500`), while `GlobalExceptionHandler` (`@ControllerAdvice` scoped to UI controllers) sets the HTTP status code and renders `templates/error.html`.
- **Task Features**: Create, edit, delete, and quick-toggle (`TODO` / `DONE`) tasks with priority (`LOW`, `MEDIUM`, `HIGH`), optional description (up to 255 characters), optional due date, and automatic `completionDate` tracking. The `/dashboard` view displays pending, overdue, and weekly completion counts alongside Chart.js status/priority charts.

---

## Running Locally

### Option 1: Quick Start with In-Memory H2 (`test` profile)

No PostgreSQL installation is required. The database starts empty; create an account at `http://localhost:8080/register`.

- **Windows**:
  ```powershell
  .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=test"
  ```
- **Linux / macOS**:
  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=test
  ```

### Option 2: Local PostgreSQL (`local` profile, default)

By default, `application.properties` activates the `local` profile and connects to `jdbc:postgresql://localhost:5432/taskmanager` as `postgres` using `${DB_PASSWORD}`.

1. Set `DB_PASSWORD` in your shell (or create a gitignored `src/main/resources/application-local.properties` to override datasource settings):
   ```powershell
   $env:DB_PASSWORD="your_password"
   .\mvnw.cmd spring-boot:run
   ```
   *(On Linux/macOS: `DB_PASSWORD=your_password ./mvnw spring-boot:run`)*
2. In the `local` and `dev` profiles, `DataLoader` seeds sample accounts when the `users` table is empty:
   - `user` / `user123` (`USER`)
   - `admin` / `admin123` (`ADMIN`)

### Option 3: Docker Compose

1. Build the JAR:
   - Windows: `.\mvnw.cmd clean package -DskipTests`
   - Linux / macOS: `./mvnw clean package -DskipTests`
2. Start PostgreSQL 16 and the application container:
   ```bash
   docker-compose up --build
   ```
   The `app` container healthcheck probes `http://localhost:8080/actuator/health` using BusyBox `wget`.

### Useful URLs

- **Web UI**: `http://localhost:8080/` (redirects to `/tasks` after login)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`

---

## Testing & CI/CD

Run the full test suite (35 tests across 5 test classes using H2 in PostgreSQL mode):

- **Windows**: `.\mvnw.cmd test`
- **Linux / macOS**: `./mvnw test`

### Test Classes

- `TaskManagerApplicationTests`: Spring Boot `contextLoads` smoke test (`test` profile).
- `TaskServiceTest`: Mockito unit tests for task CRUD, user ownership enforcement (`TaskNotFoundException`, `TaskAccessDeniedException`), `completionDate` transitions, and dashboard stats.
- `UserServiceTest`: Mockito unit tests for user registration, duplicate-username rejection (`DuplicateUsernameException`), BCrypt password encoding, and `UserDetailsService`.
- `SecurityAndControllerIntegrationTest`: `MockMvc` integration tests for unauthenticated redirects, `USER`/`ADMIN` role enforcement, registration privilege-escalation prevention, DTO response filtering (no `password` or `User` entity leakage), cross-user `403` JSON responses, CSRF enforcement on `/api/tasks/**`, and unauthenticated `/actuator/health` access.
- `TaskUIAndDashboardIntegrationTest`: `MockMvc` integration tests for REST `400`/`404`/`409` JSON error responses, `TaskUIController` CRUD and error views, `DashboardController` model attributes, `/` and `/favicon.ico` routing, and optional task descriptions.

### GitHub Actions CI

`.github/workflows/maven.yml` runs on pushes and pull requests to `main`:
- `build-and-test` (`contents: read`): builds and tests with `./mvnw -B package -Dspring.profiles.active=test --file pom.xml` on JDK 21.
- `update-badges` (`contents: write`, runs only on `push` to `main`): regenerates `.github/badges/*.svg` from the JaCoCo CSV artifact.

---

## Current Scope & Known Limitations

- **Single task list per user**: Tasks belong directly to a user without separate named lists or categories; multiple task lists (`TaskList`) are a planned future enhancement.
- **Stateful session authentication**: Authentication uses Spring Security `formLogin` sessions and CSRF tokens rather than stateless JWT or OAuth2 bearer tokens.
- **Profile-gated seed accounts**: Default `admin` and `user` accounts are seeded only in `local` and `dev` profiles when the database is empty.
