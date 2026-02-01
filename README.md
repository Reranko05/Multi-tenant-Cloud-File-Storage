# Mini Drive — Secure Multi-Tenant Cloud File Storage Platform

A full-stack cloud file storage system inspired by Google Drive, built with Spring Boot, JWT authentication, and AWS S3.  
The platform supports multiple users with strict data isolation and secure file access through a browser-based UI.

---

## Tech Stack

- **Backend:** Spring Boot 3.5.10 (Java 17)
- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Authentication:** JWT (Stateless)
- **Security:** Spring Security, BCrypt password hashing
- **Database:** PostgreSQL
- **Storage:** AWS S3 (Presigned URLs for secure uploads)
- **Build Tool:** Maven
- **API Testing:** Browser UI, Postman, or curl

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│         Frontend (HTML/CSS/JavaScript)                  │
│    ├─ Authentication Pages (Register/Login)             │
│    └─ File Management UI (Upload/List)                  │
└────────────────────────┬────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────┐
│        Spring Boot REST API (Java 17)                   │
│    ├─ JWT Authentication Filter                         │
│    ├─ Auth Controller (Register/Login)                  │
│    ├─ User Controller (Profile)                         │
│    └─ File Controller (Upload/List)                     │
└──────────────┬───────────────────────────┬──────────────┘
               │                           │
               ↓                           ↓
        ┌──────────────┐          ┌──────────────┐
        │ PostgreSQL   │          │   AWS S3     │
        │ Database     │          │   Storage    │
        │ (Metadata)   │          │ (File Data)  │
        └──────────────┘          └──────────────┘
```

---

## Authentication & Security

### Overview
JWT (JSON Web Tokens) provide stateless authentication. The token is generated upon login and stored on the client side.  
Every subsequent request includes the JWT in the `Authorization` header, verified by the `JwtAuthenticationFilter`.

JWT is **stateless**, meaning no session data is stored on the server—the token contains all necessary information.

---

### Registration Flow
A user registers with an email and password via the frontend.

1. Frontend sends POST request to `/auth/register`
2. Backend validates email uniqueness
3. Password is hashed using **BCrypt** with a random salt
4. User record is stored in PostgreSQL
5. Frontend redirects to login page

**Details:**
- **Endpoint:** `POST /auth/register`
- **Request body:** `{ "email": "user@example.com", "password": "password123" }`
- **Password storage:**  
  BCrypt automatically generates a unique salt and hashes the password.  
  Format: `$2a$10$salt$hash`  
  Even identical passwords produce different hashes.

---

### Login Flow
A user authenticates with email and password.

1. Frontend sends POST request to `/auth/login`
2. Backend validates email exists
3. BCrypt verifies password against stored hash
4. JWT token is generated with user ID as payload
5. Token is returned to frontend and stored in `localStorage`
6. Frontend redirects to file management page

**Details:**
- **Endpoint:** `POST /auth/login`
- **Request body:** `{ "email": "user@example.com", "password": "password123" }`
- **Response:** JWT token (string)
- **Token expiry:** 900,000 ms (15 minutes)
- **Token payload contains:** User ID, issued time, expiration time

---

### JWT Structure
A JWT consists of three parts:

```
Header.Payload.Signature
```

- **Header:** Algorithm and token type  
  `{ "alg": "HS256", "typ": "JWT" }`

- **Payload:** User claims (signed but not encrypted)  
  `{ "userId": 123, "iat": 1706847000, "exp": 1706847900 }`

- **Signature:** HMAC-SHA256 hash of header + payload with secret key  
  `HMAC-SHA256(SECRET + HEADER + PAYLOAD)`

The secret key (`jwt.secret` in `application.yaml`) is never sent to the client.  
If any part of the token is modified, signature verification fails.

---

### JWT Validation & Filter
`JwtAuthenticationFilter` intercepts all incoming HTTP requests.

**Flow:**
1. Extract JWT from `Authorization: Bearer <token>` header
2. Validate token signature using secret key
3. Validate token expiry
4. Extract user ID from token payload
5. Store authentication in `SecurityContext`
6. Forward request to controller

**If validation fails:**
- Request is rejected immediately (401/403)
- Controllers are never reached
- Business logic never runs
- Data remains untouched

---

### Protected Endpoints
- **Public:** `/auth/register`, `/auth/login`
- **Protected:** All other endpoints require a valid JWT in `Authorization` header

---

## API Endpoints

### Authentication APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user with email & password |
| POST | `/auth/login` | Authenticate and receive JWT token |

### User APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/me` | Get authenticated user ID (test endpoint) |

### File APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/files/upload-intent` | Create upload intent and get S3 presigned URL |
| POST | `/files/{fileId}/complete` | Mark file upload as complete |
| GET | `/files` | List all files owned by authenticated user |

---

## File Upload Flow (Multi-Step Process)

The file upload uses a presigned URL approach for direct S3 uploads:

1. **Create Upload Intent**
   - Frontend sends file metadata (name, size, content type)
   - Backend generates unique S3 object key
   - Backend stores file metadata in PostgreSQL with `PENDING` status
   - Backend generates presigned URL using AWS S3 API
   - Returns `fileId` and `uploadUrl` to frontend

2. **Upload to S3**
   - Frontend uploads file directly to S3 using presigned URL
   - Backend is not involved in the actual file transfer

3. **Complete Upload**
   - Frontend calls completion endpoint with `fileId`
   - Backend verifies file exists in S3
   - Backend updates file status to `UPLOADED`

**Benefits:**
- Backend doesn't handle large file payloads
- Direct S3 upload reduces latency
- Presigned URLs expire (security)
- User data isolated by `userId` prefix in S3 key

---

## Data Isolation & Security

**Multi-tenancy Implementation:**
- Each file is tagged with `ownerUserId`
- File queries filter by authenticated user ID
- S3 keys use pattern: `user-{userId}/{uuid}`
- User can only access their own files

**JWT Security:**
- Extracted user ID from token is used for all data access
- No user ID parameter in API (extracted from JWT)
- Prevents ID tampering or unauthorized access

---

## Frontend Structure

The frontend is split across multiple HTML pages:

- **index.html** - Landing page with register/login form
- **login.html** - Login-only page (if user not authenticated)
- **drive.html** - Main file management page (requires JWT)
- **app.js** - Shared JavaScript logic for all pages
- **style.css, auth.css, drive.css** - Page styling

**Frontend Flow:**
1. User visits `index.html` or `login.html`
2. Register or login
3. JWT stored in `localStorage`
4. Redirected to `drive.html`
5. Logout clears token and redirects to login

---

## How to Run Locally

### Prerequisites
- Java 17+
- Maven
- PostgreSQL running locally
- AWS S3 credentials (configured locally)

### Setup

1. **Configure Database** (application.yaml)
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/mini_drive
       username: postgres
       password: 12345
   ```

2. **Configure JWT Secret** (application.yaml)
   ```yaml
   jwt:
     secret: 9f8e7d6c5b4a3a2f1e0d9c8b7a6f5e4d3c2b1a0f
     expiration: 900000  # 15 minutes
   ```

3. **Configure AWS S3 Credentials**  
   Use AWS CLI or environment variables:
   ```bash
   aws configure
   # or set AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_REGION
   ```

### Run Backend
```bash
mvn spring-boot:run
```
Backend starts at: `http://localhost:8080`

### Run Frontend
Open in browser:
```
file:///path/to/mini-drive/frontend/index.html
```
Or serve with a simple HTTP server:
```bash
cd frontend
python -m http.server 8000
# Visit http://localhost:8000/index.html
```

---

## Project Structure

```
mini-drive/
├── frontend/                          # Browser UI
│   ├── index.html                    # Landing page
│   ├── login.html                    # Login page
│   ├── drive.html                    # File management page
│   ├── app.js                        # Shared JavaScript logic
│   ├── style.css                     # Global styles
│   ├── auth.css                      # Auth pages styling
│   └── drive.css                     # Drive page styling
│
├── src/main/java/com/reranko/cloud_storage/mini_drive/
│   ├── MiniDriveApplication.java     # Spring Boot entry point
│   ├── auth/                         # Authentication module
│   │   ├── controller/
│   │   │   ├── AuthController.java   # Register/Login endpoints
│   │   │   └── UserController.java   # User info endpoints
│   │   ├── service/
│   │   │   └── AuthService.java      # Auth business logic
│   │   ├── jwt/
│   │   │   └── JwtService.java       # JWT token generation/validation
│   │   └── dto/
│   │       ├── RegisterRequest.java
│   │       └── LoginRequest.java
│   │
│   ├── file/                         # File management module
│   │   ├── controller/
│   │   │   └── FileController.java   # Upload/List endpoints
│   │   ├── service/
│   │   │   └── S3UploadService.java  # AWS S3 integration
│   │   ├── entity/
│   │   │   ├── FileMetadata.java
│   │   │   └── FileStatus.java
│   │   ├── repository/
│   │   │   └── FileMetadataRepository.java
│   │   └── dto/
│   │       ├── CreateFileRequest.java
│   │       ├── CreateFileResponse.java
│   │       └── FileListItem.java
│   │
│   ├── user/                         # User module
│   │   ├── entity/
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       └── UserService.java
│   │
│   ├── security/                     # Security configuration
│   │   └── JwtAuthenticationFilter.java
│   │
│   ├── config/                       # Application configuration
│   │   └── SecurityConfig.java
│   │
│   ├── exception/                    # Custom exceptions
│   │   └── ...
│   │
│   ├── storage/                      # AWS S3 utilities
│   │   └── ...
│   │
│   └── common/                       # Shared utilities
│       └── ...
│
├── src/main/resources/
│   ├── application.yaml              # Configuration (DB, JWT, S3)
│   └── static/                       # Static files (if needed)
│
├── src/test/java/                    # Integration tests
│   └── FileFlowIntegrationTest.java
│   └── MiniDriveApplicationTests.java
│
├── pom.xml                           # Maven dependencies
├── mvnw & mvnw.cmd                   # Maven wrapper
└── README.md                         # This file
```

---

## Testing

Run integration tests:
```bash
mvn test
```

Test results are generated in `target/surefire-reports/`

---

## Key Dependencies

From `pom.xml`:
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-security` - Authentication & authorization
- `spring-boot-starter-data-jpa` - Database ORM
- `spring-boot-starter-validation` - Input validation
- `spring-security-jwt` - JWT support (if added)
- `org.postgresql` - PostgreSQL driver
- `software.amazon.awssdk:s3` - AWS S3 SDK (if added)



