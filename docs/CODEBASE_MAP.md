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
│       └── maven.yml                   # GitHub Actions CI: JDK 21, Maven package (test profile), JaCoCo badges
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
│   │   │   │   ├── GlobalExceptionHandler.java   # @ControllerAdvice mapping exceptions to templates/error.html
│   │   │   │   ├── LoginController.java          # GET /login — renders login view
│   │   │   │   ├── RegistrationController.java   # GET/POST /register — user sign-up flow
│   │   │   │   ├── TaskController.java           # REST API /api/tasks (CRUD + PATCH /{taskId}/toggle)
│   │   │   │   ├── TaskUIController.java         # Thymeleaf UI /tasks (list, add, edit, delete)
│   │   │   │   └── UserController.java           # ADMIN-only REST API /users (list, create)
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
│       │   └── service/
│       │       ├── TaskServiceTest.java          # Mockito unit tests for TaskService
│       │       └── UserServiceTest.java          # Mockito unit tests for UserService
│       └── resources/
│           └── application-test.properties       # In-memory H2 (PostgreSQL mode) config for tests
├── Dockerfile                          # Multi-stage/runtime image: eclipse-temurin:21-jdk-alpine running app.jar
├── docker-compose.yml                  # PostgreSQL 16 + app services with healthchecks & volume
├── pom.xml                             # Maven build, Java 21, Spring Boot 3.4.3, JaCoCo 0.8.12
├── mvnw / mvnw.cmd                     # Maven Wrapper scripts (Unix / Windows)
└── README.md                           # Project overview, setup, and test instructions
```

---

## 3. Task-to-File Routing Table ("Where to Look First")

| Task Area | Read First (Exact Minimal Files) |
| :--- | :--- |
| **REST API endpoints** | `src/main/java/com/lucc/taskmanager/controller/TaskController.java`, `src/main/java/com/lucc/taskmanager/controller/UserController.java` |
| **Thymeleaf UI & controllers** | `src/main/java/com/lucc/taskmanager/controller/TaskUIController.java`, `DashboardController.java`, `RegistrationController.java`, `LoginController.java`, `src/main/resources/templates/*.html` |
| **Security, auth & CSRF** | `src/main/java/com/lucc/taskmanager/config/SecurityConfig.java`, `src/main/java/com/lucc/taskmanager/model/User.java`, `src/main/java/com/lucc/taskmanager/service/UserService.java` |
| **Services & business logic** | `src/main/java/com/lucc/taskmanager/service/TaskService.java`, `src/main/java/com/lucc/taskmanager/service/UserService.java` |
| **JPA entities & repositories** | `src/main/java/com/lucc/taskmanager/model/Task.java`, `User.java`, `Status.java`, `Priority.java`, `Role.java`, `src/main/java/com/lucc/taskmanager/repository/TaskRepository.java`, `UserRepository.java` |
| **Seed data & AOP logging** | `src/main/java/com/lucc/taskmanager/init/DataLoader.java`, `src/main/java/com/lucc/taskmanager/aspect/LoggingAspect.java` |
| **Docker & DB configuration** | `docker-compose.yml`, `Dockerfile`, `src/main/resources/application.properties`, `src/test/resources/application-test.properties` |
| **Tests & CI/CD** | `src/test/java/com/lucc/taskmanager/service/TaskServiceTest.java`, `UserServiceTest.java`, `TaskManagerApplicationTests.java`, `.github/workflows/maven.yml`, `pom.xml` |
| **Docs & project overview** | `README.md`, `docs/CODEBASE_MAP.md` |

---

## 4. Entry Points & Architecture Wiring

- **Application Boot**: `src/main/java/com/lucc/taskmanager/TaskManagerApplication.java` (`@SpringBootApplication`) boots Spring Boot and defines the `OpenAPI` bean (`/swagger-ui.html`, `/v3/api-docs/**`).
- **Layered Request Flow**:
  - **UI Controllers** (`TaskUIController`, `DashboardController`, `RegistrationController`, `LoginController`) and **REST Controllers** (`TaskController` at `/api/tasks`, `UserController` at `/users`) receive HTTP requests and resolve `@AuthenticationPrincipal User`.
  - **Service Layer** (`TaskService`, `UserService`) enforces task ownership checks (`task.getUser().getUsername().equals(user.getUsername())`), manages `completionDate` transitions, encodes passwords via `PasswordEncoder`, and implements `UserDetailsService`.
  - **Repository Layer** (`TaskRepository`, `UserRepository`) extends `JpaRepository` to persist `Task` and `User` (`users` table) in PostgreSQL (runtime) or H2 (test profile).
- **Exception Handling**: `GlobalExceptionHandler` (`@ControllerAdvice`) catches `IllegalArgumentException` and generic `Exception`, populating `errorMessage` and rendering `src/main/resources/templates/error.html`.
- **Cross-Cutting AOP**: `LoggingAspect` (`@Aspect`) intercepts all methods in `com.lucc.taskmanager.controller.*` with `@Before` and `@AfterReturning` advice.
- **Test & CI Roots**: `./mvnw test` runs JUnit 5 + Mockito unit tests (`TaskServiceTest`, `UserServiceTest`) and `TaskManagerApplicationTests` (`@ActiveProfiles("test")`); `.github/workflows/maven.yml` runs `mvn -B package -Dspring.profiles.active=test --file pom.xml` and updates `.github/badges/*.svg` via JaCoCo.

---

## 5. Restricted, Isolated & Sensitive Zones

- **`src/main/java/com/lucc/taskmanager/config/SecurityConfig.java`**:
  - `.anyRequest().permitAll()` is configured as the fallback rule; `/api/**` and `/dashboard` are not explicitly restricted in `authorizeHttpRequests` (review carefully during security audits).
  - CSRF protection is explicitly disabled for `/h2-console/**`, `/api/**`, `/v3/api-docs/**`, and `/swagger-ui/**`, and `frameOptions` is set to `sameOrigin()`.
- **`src/main/java/com/lucc/taskmanager/init/DataLoader.java`**:
  - Seeds hardcoded default accounts (`admin`/`admin123` with `Role.ADMIN` and `user`/`user123` with `Role.USER`) whenever `userRepository.count() == 0` on startup.
- **`src/main/resources/application.properties`**:
  - Sets `spring.profiles.active=local`, `spring.jpa.hibernate.ddl-auto=update`, `spring.jpa.show-sql=true`, and reads `spring.datasource.password=${DB_PASSWORD}`. Do not hardcode credentials here.
- **`docker-compose.yml`**:
  - Falls back to `${DB_PASSWORD:-postgres}` for both `db` (`POSTGRES_PASSWORD`) and `app` (`SPRING_DATASOURCE_PASSWORD`); `app` healthcheck targets `/actuator/health`.
- **`.github/workflows/maven.yml`**:
  - Grants `contents: write` to `GITHUB_TOKEN` and auto-commits updated `*.svg` coverage badges on `master`, `main`, and `dockerization`.
- **`.env*` & Local Overrides**:
  - `.env*` files and `src/main/resources/application-local.properties` (listed in `.gitignore`) hold local secrets/overrides and must never be committed or read into shared docs.
