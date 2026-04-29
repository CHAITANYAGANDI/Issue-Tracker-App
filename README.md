# Issue Tracker API

A production-style beginner/intermediate **Spring Boot REST API** for managing software issues/bugs.  
This project focuses on clean backend architecture, DTOs, validation, exception handling, pagination, sorting, filtering, Flyway migrations, Swagger documentation, and automated testing.

---

## Project Status

This project is completed as a strong Spring Boot backend learning project.

Completed features:

- CRUD operations for issues
- DTO-based request/response structure
- Input validation
- Global exception handling
- Custom error responses
- Enums for issue status and priority
- Pagination and sorting
- Allowed sort-field validation
- JPA auditing with `createdAt` and `updatedAt`
- PATCH endpoints for status and priority updates
- Due date field
- Assignee and reporter fields
- Advanced filtering using Spring Data JPA Specifications
- Flyway database migration
- Swagger/OpenAPI documentation
- SLF4J logging
- Service unit tests
- Controller tests
- Repository tests
- Integration tests with Testcontainers and PostgreSQL

---

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Bean Validation
- Swagger/OpenAPI using springdoc-openapi
- JUnit 5
- Mockito
- MockMvc
- H2 Database for repository tests
- Testcontainers with PostgreSQL for integration tests
- Maven

---

## Project Structure

```text
src/main/java/org/example/issuetracker
│
├── IssueTrackerApplication.java
│
├── config
│   ├── JpaAuditingConfig.java
│   └── OpenApiConfig.java
│
├── controller
│   └── IssueController.java
│
├── service
│   └── IssueService.java
│
├── repository
│   └── IssueRepository.java
│
├── entity
│   └── Issue.java
│
├── dto
│   ├── IssueRequestDTO.java
│   └── IssueResponseDTO.java
│
├── enums
│   ├── IssueStatus.java
│   └── IssuePriority.java
│
├── exception
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── IssueNotFoundException.java
│
└── specification
    └── IssueSpecification.java
```

---

## Main Features

### Issue Management

The API supports:

- Create issue
- Get all issues with pagination and sorting
- Get issue by ID
- Update full issue
- Delete issue
- Update only issue status
- Update only issue priority
- Search issue by title
- Filter issues by status, priority, and assignee

---

## Issue Fields

Each issue contains:

```text
id
title
description
status
priority
dueDate
assignee
reporter
createdAt
updatedAt
```

---

## Enums

### Issue Status

```java
OPEN
IN_PROGRESS
RESOLVED
```

### Issue Priority

```java
LOW
MEDIUM
HIGH
```

---

## API Base URL

```text
http://localhost:8081/api/v1/issues
```

---

## API Endpoints

### Create Issue

```http
POST /api/v1/issues
```

Example request:

```json
{
  "title": "Login bug",
  "description": "User cannot log in",
  "status": "OPEN",
  "priority": "HIGH",
  "dueDate": "2026-05-05",
  "assignee": "Sai",
  "reporter": "Admin"
}
```

---

### Get All Issues with Pagination and Sorting

```http
GET /api/v1/issues?page=0&size=5&sortBy=id&sortDir=asc
```

Allowed `sortBy` fields:

```text
id
title
status
priority
```

Allowed `sortDir` values:

```text
asc
desc
```

---

### Get Issue by ID

```http
GET /api/v1/issues/{id}
```

Example:

```http
GET /api/v1/issues/1
```

---

### Update Issue

```http
PUT /api/v1/issues/{id}
```

Example request:

```json
{
  "title": "Login bug updated",
  "description": "User login issue is being checked",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2026-05-10",
  "assignee": "Sai",
  "reporter": "Admin"
}
```

---

### Delete Issue

```http
DELETE /api/v1/issues/{id}
```

Expected response:

```text
204 No Content
```

---

### Filter by Status

```http
GET /api/v1/issues/status/{status}
```

Examples:

```http
GET /api/v1/issues/status/OPEN
GET /api/v1/issues/status/IN_PROGRESS
GET /api/v1/issues/status/RESOLVED
```

---

### Filter by Priority

```http
GET /api/v1/issues/priority/{priority}
```

Examples:

```http
GET /api/v1/issues/priority/HIGH
GET /api/v1/issues/priority/MEDIUM
GET /api/v1/issues/priority/LOW
```

---

### Search by Title

```http
GET /api/v1/issues/search/title?keyword=login
```

---

### Update Only Status

```http
PATCH /api/v1/issues/{id}/status?status=RESOLVED
```

Example:

```http
PATCH /api/v1/issues/1/status?status=RESOLVED
```

---

### Update Only Priority

```http
PATCH /api/v1/issues/{id}/priority?priority=HIGH
```

Example:

```http
PATCH /api/v1/issues/1/priority?priority=HIGH
```

---

### Advanced Filter

```http
GET /api/v1/issues/filter
```

Examples:

```http
GET /api/v1/issues/filter?status=OPEN
GET /api/v1/issues/filter?priority=HIGH
GET /api/v1/issues/filter?assignee=Sai
GET /api/v1/issues/filter?status=OPEN&priority=HIGH
GET /api/v1/issues/filter?status=OPEN&priority=HIGH&assignee=Sai
```

---

## Validation Rules

### Title

- Required
- Maximum 100 characters

### Description

- Required
- Maximum 500 characters

### Status

- Required
- Must be one of:
  - `OPEN`
  - `IN_PROGRESS`
  - `RESOLVED`

### Priority

- Required
- Must be one of:
  - `LOW`
  - `MEDIUM`
  - `HIGH`

### Assignee

- Optional
- Maximum 100 characters

### Reporter

- Optional
- Maximum 100 characters

### Due Date

- Optional
- Format: `yyyy-MM-dd`

---

## Error Handling

The application uses a centralized global exception handler.

### Issue Not Found

Example response:

```json
{
  "timestamp": "2026-04-29T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Issue not found with id: 100",
  "path": "/api/v1/issues/100"
}
```

### Invalid Sort Field

Example response:

```json
{
  "timestamp": "2026-04-29T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid sort field: randomField. Allowed fields are: [id, title, status, priority]",
  "path": "/api/v1/issues"
}
```

### Invalid Enum Value

Example response:

```json
{
  "timestamp": "2026-04-29T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid value for parameter 'status': DONE. Allowed values are: [OPEN, IN_PROGRESS, RESOLVED]",
  "path": "/api/v1/issues/1/status"
}
```

### Validation Error

Example response:

```json
{
  "title": "Title is required"
}
```

---

## Database Setup

This project uses PostgreSQL.

Create a database:

```sql
CREATE DATABASE issue_tracker_flyway_db;
```

Update `src/main/resources/application.properties`:

```properties
spring.application.name=Issue-Tracker
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/issue_tracker_flyway_db
spring.datasource.username=postgres
spring.datasource.password=your_actual_password

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Replace:

```text
your_actual_password
```

with your PostgreSQL password.

---

## Flyway Migration

The project uses Flyway for database schema management.

Migration folder:

```text
src/main/resources/db/migration
```

Initial migration file:

```text
V1__create_issues_table.sql
```

Flyway creates and manages the database schema. Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means Hibernate validates the schema instead of creating or updating it automatically.

---

## Swagger/OpenAPI

After starting the application, open Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

or:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```

---

## Running the Application

### 1. Start PostgreSQL

Make sure PostgreSQL is running locally.

### 2. Create the database

```sql
CREATE DATABASE issue_tracker_flyway_db;
```

### 3. Update database credentials

Edit:

```text
src/main/resources/application.properties
```

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main class from IntelliJ:

```text
IssueTrackerApplication.java
```

Application runs on:

```text
http://localhost:8081
```

---

## Running Tests

Run all tests:

```bash
mvn test
```

---

## Test Coverage

This project includes four levels of backend testing.

### Level 1: Service Unit Tests

Uses:

- JUnit 5
- Mockito

Tests service-layer business logic without starting Spring or connecting to a database.

Covered examples:

- Create issue
- Get issue by ID
- Issue not found exception
- Update issue
- Delete issue
- PATCH status
- PATCH priority
- Pagination
- Invalid sort field
- Invalid sort direction
- Filtering

---

### Level 2: Controller Tests

Uses:

- `@WebMvcTest`
- MockMvc
- Mocked service layer

Tests REST API behavior without starting the full application.

Covered examples:

- POST create issue
- GET by ID
- 404 not found
- Validation error
- PUT update issue
- DELETE issue
- PATCH status
- PATCH priority
- Invalid enum
- Missing request parameter
- Filter endpoint

---

### Level 3: Repository Tests

Uses:

- `@DataJpaTest`
- H2 in-memory database

Tests repository query methods and JPA behavior.

Covered examples:

- Find by status
- Find by priority
- Search title ignoring case
- Specification filter with status, priority, and assignee

---

### Level 4: Integration Tests

Uses:

- `@SpringBootTest`
- MockMvc
- Testcontainers
- Real PostgreSQL container

Tests the full backend flow:

```text
Controller → Service → Repository → PostgreSQL
```

Covered examples:

- Create issue and fetch it
- Validation failure
- PATCH status
- Delete issue
- Advanced filtering

---

## Important Learning Concepts Practiced

This project helped practice:

- Layered architecture
- REST API design
- DTO pattern
- Entity vs DTO separation
- Bean validation
- Global exception handling
- Custom exceptions
- Spring Data JPA repositories
- JPA query methods
- JPA Specifications
- Pagination and sorting
- Transaction management
- JPA auditing
- Flyway migrations
- Swagger documentation
- SLF4J logging
- Unit testing
- Controller testing
- Repository testing
- Integration testing with Testcontainers

---


## Author

Chaitanya Sai Gandi

---

## Summary

This Issue Tracker API is a strong Spring Boot backend project that demonstrates practical backend development skills including clean architecture, database persistence, validation, exception handling, API documentation, migrations, logging, and automated testing.
