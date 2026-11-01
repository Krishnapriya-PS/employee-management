# Employee Management REST API

A Spring Boot REST API for managing employees, built with a layered architecture.

## Tech stack
- Java 21, Spring Boot 4.1.2, Maven
- Spring Web MVC, Spring Data JPA, Bean Validation
- MySQL 8

## Features
- Create, view all, view by ID, update and delete employees
- Pagination and optional status filter on the list endpoint
- Input validation with clear messages
- Centralized exception handling with database-driven error codes

## Project structure
```
com.codenzic.employee_management
├── controller   REST endpoints
├── service      business logic
├── repository   Spring Data JPA repositories
├── entity       JPA entities (Employee, ErrorCode, EmployeeStatus)
├── dto          request and response objects
├── exception    AppException and GlobalExceptionHandler
└── util         ErrorCodeConstants
```

## Prerequisites
- JDK 21
- MySQL 8 running on port 3306
- Git

## Setup
1. Clone the repository:
   ```
   git clone <your-repo-url>
   cd employee-management
   ```
2. Create the database:
   ```sql
   CREATE DATABASE employee_db;
   ```
3. Set the database credentials (Windows Command Prompt):
   ```
   set DB_USERNAME=root
   set DB_PASSWORD=your_password
   ```
4. Run the application:
   ```
   mvnw spring-boot:run
   ```
   The API starts at `http://localhost:8080`.

## Database configuration
| Setting | Environment variable | Default |
|---|---|---|
| JDBC URL | `DB_URL` | `jdbc:mysql://localhost:3306/employee_db` |
| Username | `DB_USERNAME` | `root` |
| Password | `DB_PASSWORD` | `change_me` |

- Tables `employees` and `error_codes` are created automatically by Hibernate on startup.
- Error codes are inserted from `src/main/resources/data.sql` and cached in memory at startup.
- Numeric codes are defined in `src/main/resources/errorcode.properties`.

## API endpoints
Base path: `/employee-management/employees`

| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/employee-management/employees` | Create employee | 201 |
| GET | `/employee-management/employees` | List employees (paginated, optional status filter) | 200 |
| GET | `/employee-management/employees/{id}` | Get employee by ID | 200 |
| PUT | `/employee-management/employees/{id}` | Update employee | 200 |
| DELETE | `/employee-management/employees/{id}` | Delete employee | 204 |

### Query parameters for the list endpoint
| Param | Default | Description |
|---|---|---|
| `status` | none | `ACTIVE` or `INACTIVE` |
| `page` | 0 | Page number, starting at 0 |
| `size` | 10 | Page size (maximum 100) |

## Validation rules
| Field | Rule |
|---|---|
| firstName, lastName | required, max 50 characters |
| email | required, valid format, max 100, unique |
| phone | optional, exactly 10 digits |
| department, designation | required, max 50 characters |
| salary | required, greater than 0 |
| status | optional, `ACTIVE` (default) or `INACTIVE` |

## Sample requests and responses

### Create employee
`POST /employee-management/employees`
```json
{
  "firstName": "Asha",
  "lastName": "Nair",
  "email": "asha.nair@example.com",
  "phone": "9876543210",
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 55000.00
}
```
Response `201 Created`:
```json
{
  "id": 1,
  "firstName": "Asha",
  "lastName": "Nair",
  "email": "asha.nair@example.com",
  "phone": "9876543210",
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 55000.00,
  "status": "ACTIVE"
}
```

### List employees
`GET /employee-management/employees?status=ACTIVE&page=0&size=10`

Response `200 OK`:
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Asha",
      "lastName": "Nair",
      "email": "asha.nair@example.com",
      "phone": "9876543210",
      "department": "Engineering",
      "designation": "Software Engineer",
      "salary": 55000.00,
      "status": "ACTIVE"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

### Get by ID, update, delete
- `GET /employee-management/employees/1` returns `200` with one employee
- `PUT /employee-management/employees/1` with the create body returns `200` with the updated employee
- `DELETE /employee-management/employees/1` returns `204 No Content` with an empty body

### Validation error (400)
```json
{
  "errors": {
    "code": 4400,
    "systemMessage": "Request validation failed",
    "userMessage": "Phone must be exactly 10 digits"
  }
}
```

### Not found (404)
`GET /employee-management/employees/999`
```json
{
  "errors": {
    "code": 4404,
    "systemMessage": "No employee exists for the given id",
    "userMessage": "Employee not found"
  }
}
```

### Duplicate email (409)
```json
{
  "errors": {
    "code": 4409,
    "systemMessage": "Unique constraint on email violated",
    "userMessage": "An employee with this email already exists"
  }
}
```

## Error handling
All errors use the same format. The HTTP status, system message and default user message come from the `error_codes` table. For validation errors, the user message is the specific field message.

| Code | HTTP | Meaning |
|---|---|---|
| 4400 | 400 | Validation failed |
| 4401 | 400 | Malformed request or invalid value |
| 4404 | 404 | Employee not found |
| 4409 | 409 | Duplicate email |
| 5000 | 500 | Unexpected error |
| 5500 | 500 | Database error |


## Postman collection
Import `postman/Employee-Management.postman_collection.json` into Postman. It uses a `baseUrl` variable.

## Design decisions
- DTOs keep the API contract separate from the database model.
- Business rules (duplicate email, not found) are in the service layer.
- One `@RestControllerAdvice` handles all exceptions, and the original exception is logged once.
- Error messages are stored in a table and cached