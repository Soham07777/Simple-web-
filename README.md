# Student Attendance Management System

A Java and Spring Boot web application for administering school classes, student and teacher records, daily attendance, and attendance reports. The application stores its data in MySQL and uses Spring Security for an administrator sign-in.

## Features

- Administrator sign-in and protected pages.
- Dashboard with student, teacher, class, and today's attendance totals.
- Student and teacher directory management, including duplicate-ID checks.
- Class creation and student enrollment.
- Date- and class-filtered daily register; mark or update students as present, absent, or late.
- Monthly summaries, attendance percentages, and per-student attendance history.
- MySQL persistence, local Docker Compose setup, and H2-backed automated integration tests.

The initial local demo login is `admin` / `Admin123!`. Set `APP_ADMIN_PASSWORD` (and optionally `APP_ADMIN_USERNAME`) to replace it before starting the application. Do not use the demo password or sample records outside a local demonstration.

## Run the application and MySQL

Install Docker Desktop, start it, then from the repository root run:

```powershell
docker compose up --build
```

Open [http://localhost:8080](http://localhost:8080) and sign in with the local demo credentials above. Compose starts MySQL, waits for its health check, builds the Spring Boot application, and stores database files in a persistent Docker volume. The web port is bound to this computer's loopback interface.

To set your own administrator password in PowerShell before starting Compose:

```powershell
$env:APP_ADMIN_PASSWORD = "choose-a-local-demo-password"
docker compose up --build
```

Stop the services with `Ctrl+C`, or run `docker compose down` in another terminal. `docker compose down -v` also removes the persistent demo database.

## Run with a local MySQL server

Requires Java 17 or newer, Maven 3.9 or newer, and MySQL 8.0+. Create a database and configure its connection with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. Configure the local admin login with `APP_ADMIN_USERNAME` and `APP_ADMIN_PASSWORD`, then start the app:

```powershell
mvn spring-boot:run
```

By default, the app connects to `jdbc:mysql://localhost:3306/attendance` with the development settings in `src/main/resources/application.properties`. Set real credentials through environment variables rather than committing them to source control.

## Automated tests

```powershell
mvn -B -ntp clean verify
```

The Spring Boot integration tests use an isolated in-memory H2 database, so they do not need a running MySQL server or Docker.

## GitHub collaboration with pull requests

1. Create a GitHub repository and push this project to its `main` branch.
2. Protect `main` and require pull-request review and the Jenkins status check.
3. Give each contributor their own feature branch, such as `feature/attendance` or `feature/reports`.
4. Make a focused change, run `mvn clean verify`, commit it, and push the branch.
5. Open a pull request against `main`, complete the repository pull-request template, and request a review.
6. Merge only after approval and a successful Jenkins build.

The `CONTRIBUTING.md` guide explains the contributor workflow and Jenkins setup. The template is in `.github/pull_request_template.md`.

## Jenkins pull-request builds

Create a **Multibranch Pipeline** job in Jenkins for the GitHub repository, configure its GitHub Branch Source to discover branches and origin pull requests, and set the script path to `Jenkinsfile`. The included pipeline runs `mvn -B -ntp clean verify`, publishes JUnit results, and archives the packaged JAR.

The Jenkins agent needs JDK 17+ and Maven 3.9+ on `PATH`. Install the GitHub Branch Source and Pipeline plugins; configure a GitHub webhook to the reachable, secured Jenkins `/github-webhook/` endpoint (or enable periodic multibranch indexing). Require the resulting Jenkins check in GitHub branch protection. Never expose an unauthenticated Jenkins controller to the public internet.

## Project structure

```text
src/main/java/com/attendance/       Spring Boot controllers, services, entities, and repositories
src/main/resources/templates/       Thymeleaf pages
src/main/resources/static/css/      Application styles
src/test/java/com/attendance/       Spring MVC and database integration tests
Jenkinsfile                         Pull-request integration build
docker-compose.yml                  Local MySQL and application services
```

## Demo data and security

On its first run against an empty database, the app creates fictional demo classes, students, a teacher, and today's sample attendance. The demo admin account is in-memory and its password is supplied through environment-backed configuration. This project is an educational example, not a production-ready student-record system: production use needs persistent identity management, role-based access, secret management, database migrations, backups, audit logging, and appropriate student-privacy safeguards.
