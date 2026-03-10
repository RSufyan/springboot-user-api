# Task Tracking API

A RESTful backend service for managing tasks, built with Java 17 and Spring Boot 3.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data JPA**
- **H2 In-Memory Database**
- **Maven**

## Project Structure

```
src/main/java/com/example/taskapi/
├── TaskApiApplication.java          # Spring Boot entry point
├── controller/
│   └── TaskController.java          # REST endpoints
├── service/
│   └── TaskService.java             # Business logic
├── repository/
│   └── TaskRepository.java          # Spring Data JPA repository
├── entity/
│   ├── Task.java                    # JPA entity
│   └── TaskStatus.java              # Enum: OPEN, IN_PROGRESS, COMPLETE
├── dto/
│   ├── TaskRequest.java             # Request DTO (with validation)
│   └── TaskResponse.java            # Response DTO
└── exception/
    ├── TaskNotFoundException.java   # 404 exception
    └── GlobalExceptionHandler.java  # Centralized error handling
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Run the application

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`.

### H2 Console

The H2 in-memory database console is available at:

```
http://localhost:8080/h2-console
```

- **JDBC URL**: `jdbc:h2:mem:taskdb`
- **Username**: `sa`
- **Password**: *(leave blank)*

## API Endpoints

| Method | Endpoint       | Description         |
|--------|----------------|---------------------|
| GET    | /tasks         | List all tasks      |
| GET    | /tasks/{id}    | Get task by ID      |
| POST   | /tasks         | Create a new task   |
| PUT    | /tasks/{id}    | Update a task       |
| DELETE | /tasks/{id}    | Delete a task       |

### Task Fields

| Field       | Type                          | Notes                        |
|-------------|-------------------------------|------------------------------|
| id          | Long                          | Auto-generated               |
| title       | String                        | Required                     |
| description | String                        | Optional                     |
| status      | Enum (OPEN/IN_PROGRESS/COMPLETE) | Required                  |
| createdAt   | LocalDateTime                 | Set automatically on create  |

### Example Requests

**Create a task:**
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Fix login bug","description":"Users cannot log in on Safari","status":"OPEN"}'
```

**Get all tasks:**
```bash
curl http://localhost:8080/tasks
```

**Get a task by ID:**
```bash
curl http://localhost:8080/tasks/1
```

**Update a task:**
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Fix login bug","description":"Fixed","status":"COMPLETE"}'
```

**Delete a task:**
```bash
curl -X DELETE http://localhost:8080/tasks/1
```

## Running Tests

```bash
mvn test
```
