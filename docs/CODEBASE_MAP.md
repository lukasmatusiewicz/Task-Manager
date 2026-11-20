# Codebase Map — Task Manager

## 1. Header & Scope Note

- **Last Verified**: `2026-11-20`
- **Branch / Base Commit**: `docs/codebase-map` (branched from `main` at `c80efe8`)
- **Stack**: Java 21, Spring Boot 3.4.3, Spring Security 6, Spring Data JPA, Spring AOP, Thymeleaf, SpringDoc OpenAPI 2.8.5, PostgreSQL / H2 (test), Maven, Docker Compose.
- **Scope Exclusions**: Generated, VCS, and wrapper-internal directories (`target/`, `.git/`, `.mvn/wrapper/`) as well as local/secret files (`.env*`, `src/main/resources/application-local.properties`) are intentionally omitted.

---

## 2. Annotated Project File Tree

```text
.
├── .github/
│   ├── badges/
│   │   ├── branches.svg                # JaCoCo branch-coverage badge updated by CI
│   │   └── jacoco.svg                  # JaCoCo line-coverage badge updated by CI
│   └── workflows/
│       └── maven.yml                   # GitHub Actions CI: build-and-test (read-only) & main-only JaCoCo badge update
├── docs/
│   └── CODEBASE_MAP.md                 # This living codebase map
├── src/
│   ├── main/
│   │   ├── java/com/lucc/taskmanager/
│   │   │   ├── TaskManagerApplication.java       # Spring Boot entry point & OpenAPI metadata bean
│   │   │   ├── aspect/
│   │   │   │   └── LoggingAspect.java            # AOP @Before / @AfterReturning logging for controllers
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java           # BCrypt encoder, HTTP auth rules, form login, CSRF & frame headers
│   │   │   ├── controller/
│   │   │   │   ├── DashboardController.java      # GET /dashboard — summary stats & chart counts
│   │   │   │   ├── GlobalExceptionHandler.java   # UI @ControllerAdvice mapping exceptions to templates/error.html
│   │   │   │   ├── LoginController.java          # GET /login — renders login view
│   │   │   │   ├── RegistrationController.java   # GET/POST /register — user sign-up flow via RegisterRequest
│   │   │   │   ├── RestExceptionHandler.java     # REST @RestControllerAdvice returning JSON ApiError (400/403/404/409/500)
│   │   │   │   ├── TaskController.java           # REST API /api/tasks (DTO CRUD + PATCH /{taskId}/toggle)
│   │   │   │   ├── TaskUIController.java         # Thymeleaf UI /tasks (list, add, edit, delete)
│   │   │   │   └── UserController.java           # ADMIN-only REST API /users (DTO list, create)
│   │   │   ├── dto/
│   │   │   │   ├── AdminCreateUserRequest.java   # Validated ADMIN user-creation request DTO
│   │   │   │   ├── RegisterRequest.java          # Validated public registration DTO (no role field)
│   │   │   │   ├── TaskRequest.java              # Validated REST task input DTO
│   │   │   │   ├── TaskResponse.java             # REST task output DTO (excludes User entity)
│   │   │   │   └── UserResponse.java             # REST user output DTO (excludes password)
│   │   │   ├── exception/
│   │   │   │   ├── DuplicateUsernameException.java # 409 Conflict exception for duplicate usernames
│   │   │   │   ├── TaskAccessDeniedException.java  # 403 Forbidden exception for cross-user task access
│   │   │   │   └── TaskNotFoundException.java      # 404 Not Found exception for missing tasks
│   │   │   ├── init/
│   │   │   │   └── DataLoader.java               # CommandLineRunner seeding default users & sample tasks
│   │   │   ├── model/
│   │   │   │   ├── Priority.java                 # Enum: LOW, MEDIUM, HIGH
│   │   │   │   ├── Role.java                     # Enum: USER, ADMIN
│   │   │   │   ├── Status.java                   # Enum: TODO, DONE
│   │   │   │   ├── Task.java                     # JPA entity for tasks with Bean Validation & user ManyToOne
│   │   │   │   └── User.java                     # JPA entity (table "users") implementing UserDetails
│   │   │   ├── repository/
│   │   │   │   ├── TaskRepository.java           # JpaRepository<Task, Integer> + user/status/date count queries
│   │   │   │   └── UserRepository.java           # JpaRepository<User, Integer> + findByUsername
│   │   │   └── service/
│   │   │       ├── TaskService.java              # Task business logic, ownership checks, stats & toggle
│   │   │       └── UserService.java              # UserDetailsService impl, password encoding, user CRUD
│   │   └── resources/
│   │       ├── application.properties            # Active profile (local), PostgreSQL & JPA/Hibernate config
│   │       ├── static/
│   │       │   └── styles.css                    # Custom CSS & dark-mode styling
│   │       └── templates/
│   │           ├── dashboard.html                # Dashboard view (stats & Chart.js charts)
│   │           ├── edit-task.html                # Task edit form view
│   │           ├── error.html                    # Error view rendered by GlobalExceptionHandler
│   │           ├── login.html                    # Login form view
│   │           ├── register.html                 # User registration form view
│   │           └── tasks.html                    # Main task list, add form, quick-toggle & delete UI
│   └── test/
│       ├── java/com/lucc/taskmanager/
│       │   ├── TaskManagerApplicationTests.java  # Spring Boot contextLoads smoke test (@ActiveProfiles("test"))
│       │   ├── controller/
│       │   │   ├── SecurityAndControllerIntegrationTest.java # MockMvc security, CSRF, DTO, & authz tests
│       │   │   └── TaskUIAndDashboardIntegrationTest.java    # MockMvc REST error & Thymeleaf UI/Dashboard tests
│       │   └── service/
│       │       ├── TaskServiceTest.java          # Mockito unit tests for TaskService
│       │       └── UserServiceTest.java          # Mockito unit tests for UserService
│       └── resources/
│           └── application-test.properties       # In-memory H2 (PostgreSQL mode) config for tests
├── CONTRIBUTING.md                     # Contributor guidelines, security contracts, and PR summary standards
├── Dockerfile                          # Single-stage runtime image: eclipse-temurin:21-jdk-alpine running app.jar
├── docker-compose.yml                  # PostgreSQL 16 + app services with wget Actuator healthcheck & volume
├── pom.xml                             # Maven build, Java 21, Spring Boot 3.4.3, JaCoCo 0.8.12
├── mvnw / mvnw.cmd                     # Maven Wrapper scripts (Unix / Windows)
└── README.md                           # Project overview, setup, and test instructions
```

---

## 3. Task-to-File Routing Table ("Where to Look First")

| Task Area | Read First (Exact Minimal Files) |
| :--- | :--- |
| **REST API endpoints** | `src/main/java/com/lucc/taskmanager/controller/TaskController.java`, `UserController.java`, `RestExceptionHandler.java`, `src/main/java/com/lucc/taskmanager/dto/*.java` |
| **Thymeleaf UI & controllers** | `src/main/java/com/lucc/taskmanager/controller/TaskUIController.java`, `DashboardController.java`, `RegistrationController.java`, `LoginController.java`, `src/main/java/com/lucc/taskmanager/dto/RegisterRequest.java`, `src/main/resources/templates/*.html` |
| **Security, auth & CSRF** | `src/main/java/com/lucc/taskmanager/config/SecurityConfig.java`, `src/main/java/com/lucc/taskmanager/model/User.java`, `src/main/java/com/lucc/taskmanager/service/UserService.java` |
| **Services & business logic** | `src/main/java/com/lucc/taskmanager/service/TaskService.java`, `src/main/java/com/lucc/taskmanager/service/UserService.java`, `src/main/java/com/lucc/taskmanager/exception/*.java` |
| **JPA entities & repositories** | `src/main/java/com/lucc/taskmanager/model/Task.java`, `User.java`, `Status.java`, `Priority.java`, `Role.java`, `src/main/java/com/lucc/taskmanager/repository/TaskRepository.java`, `UserRepository.java` |
| **Seed data & AOP logging** | `src/main/java/com/lucc/taskmanager/init/DataLoader.java`, `src/main/java/com/lucc/taskmanager/aspect/LoggingAspect.java` |
| **Docker & DB configuration** | `docker-compose.yml`, `Dockerfile`, `src/main/resources/application.properties`, `src/test/resources/application-test.properties` |
| **Tests & CI/CD** | `src/test/java/com/lucc/taskmanager/controller/SecurityAndControllerIntegrationTest.java`, `TaskUIAndDashboardIntegrationTest.java`, `service/TaskServiceTest.java`, `service/UserServiceTest.java`, `TaskManagerApplicationTests.java`, `.github/workflows/maven.yml`, `pom.xml` |
| **Docs & project overview** | `README.md`, `CONTRIBUTING.md`, `docs/CODEBASE_MAP.md` |

---

## 4. Entry Points & Architecture Wiring

- **Application Boot**: `src/main/java/com/lucc/taskmanager/TaskManagerApplication.java` (`@SpringBootApplication`) boots Spring Boot and defines the `OpenAPI` bean (`/swagger-ui.html`, `/v3/api-docs/**`).
- **Layered Request Flow**:
  - **UI Controllers** (`TaskUIController`, `DashboardController`, `RegistrationController`, `LoginController`) and **REST Controllers** (`TaskController` at `/api/tasks`, `UserController` at `/users`) receive HTTP requests and resolve `@AuthenticationPrincipal User`.
  - **Service Layer** (`TaskService`, `UserService`) enforces task ownership checks (`task.getUser().getUsername().equals(user.getUsername())`), manages `completionDate` transitions, encodes passwords via `PasswordEncoder`, and implements `UserDetailsService`.
  - **Repository Layer** (`TaskRepository`, `UserRepository`) extends `JpaRepository` to persist `Task` and `User` (`users` table) in PostgreSQL (runtime) or H2 (test profile).
- **Exception Handling**: `RestExceptionHandler` (`@RestControllerAdvice` for `TaskController` and `UserController`, highest precedence) returns structured JSON `ApiError` responses (`400`, `403`, `404`, `409`, `500`), while `GlobalExceptionHandler` (`@ControllerAdvice` scoped to UI controllers) sets the HTTP status code and renders `src/main/resources/templates/error.html`.
- **Cross-Cutting AOP**: `LoggingAspect` (`@Aspect`) intercepts all methods in `com.lucc.taskmanager.controller.*` with `@Before` and `@AfterReturning` advice.
- **Test & CI Roots**: `./mvnw test` runs JUnit 5 + Mockito unit tests (`TaskServiceTest`, `UserServiceTest`), `MockMvc` integration tests (`SecurityAndControllerIntegrationTest`, `TaskUIAndDashboardIntegrationTest`), and `TaskManagerApplicationTests` (`@ActiveProfiles("test")`); `.github/workflows/maven.yml` runs `./mvnw -B package -Dspring.profiles.active=test --file pom.xml` and updates `.github/badges/*.svg` only on `push` to `main`.

---

## 5. Restricted, Isolated & Sensitive Zones

- **`src/main/java/com/lucc/taskmanager/config/SecurityConfig.java`**:
  - Enforces `.anyRequest().authenticated()` with `permitAll()` restricted to `/login`, `/register/**`, `/styles.css`, `/favicon.ico`, `/error`, `/actuator/health`, `/swagger-ui/**`, and `/v3/api-docs/**`.
  - CSRF protection is enabled on `/api/**` using `CookieCsrfTokenRepository.withHttpOnlyFalse()` and `CsrfTokenRequestAttributeHandler`; `frameOptions` is set to `sameOrigin()`.
- **`src/main/java/com/lucc/taskmanager/init/DataLoader.java`**:
  - Annotated with `@Profile({"local", "dev"})`; seeds default accounts (`admin`, `user`) only in `local`/`dev` profiles when `userRepository.count() == 0`.
- **`src/main/resources/application.properties`**:
  - Sets `spring.profiles.active=local`, `spring.jpa.hibernate.ddl-auto=update`, `spring.jpa.show-sql=false`, `management.endpoints.web.exposure.include=health`, and reads `spring.datasource.password=${DB_PASSWORD}`. Do not hardcode credentials here.
- **`docker-compose.yml`**:
  - Falls back to `${DB_PASSWORD:-postgres}` for both `db` (`POSTGRES_PASSWORD`) and `app` (`SPRING_DATASOURCE_PASSWORD`); `app` healthcheck probes `/actuator/health` with BusyBox `wget`.
- **`.github/workflows/maven.yml`**:
  - Uses least-privilege permissions (`contents: read` on `build-and-test`, `contents: write` scoped strictly to `update-badges` on `push` to `main`), workflow `concurrency`, and `timeout-minutes`.
- **`.env*` & Local Overrides**:
  - `.env*` files and `src/main/resources/application-local.properties` (listed in `.gitignore`) hold local secrets/overrides and must never be committed or read into shared docs.
