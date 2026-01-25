# Mini Drive — Secure Multi-Tenant Cloud File Storage Platform

A backend-first cloud file storage system inspired by Google Drive, built with Spring Boot, JWT authentication, and AWS S3.  
The platform supports multiple users with strict data isolation and secure file access.

---

## Tech Stack

- **Backend:** Spring Boot (Java 17)
- **Authentication:** JWT (Stateless)
- **Security:** Spring Security, BCrypt
- **Database:** H2 (dev), PostgreSQL/MySQL (planned)
- **Storage:** AWS S3
- **Build Tool:** Maven
- **API Testing:** Postman / curl

---

## High-Level Architecture

**Components:**
- Client (Postman / Browser)
- Spring Boot REST API
- Authentication & Security Layer
- Database (User + File Metadata)
- AWS S3 (File Storage)

> _A simple architecture diagram will be added later_

---

## Authentication & Security

### Overview
JWT is generated for a user when they log in to the application.  
This JWT is then used to authenticate every subsequent request.

JWT is **stateless**, meaning the token is stored on the client side and not persisted on the server.

---

### Registration Flow
A user sends an HTTP request to register with an email and password.

- If the email does not already exist, the user is registered successfully
- If the email already exists, a **"User Already Exists"** error is returned

**Details:**
- **Endpoint:** `/auth/register`
- **Input:** Email and password
- **Password handling:**  
  Passwords are hashed using BCrypt, which automatically adds a unique salt before hashing.  
  This ensures that even identical passwords produce different hashes.
- **Database interaction:**  
  - A unique `userId` is generated as the primary key  
  - Email is stored with a unique constraint  
  - Password is stored in the format:  
    ```
    $bcryptVersion$costFactor$salt$hash(password + salt)
    ```

---

### Login Flow
After registration, a user can log in using their credentials.

- If the email exists, the password is verified
- If the email does not exist, a **"User Not Found"** error is returned

**Details:**
- **Credential verification:**  
  The stored hash already contains the salt.  
  During login, BCrypt extracts the salt, hashes the entered password with it, and compares the result.
- **JWT contents:**  
  On successful login, a JWT is generated and returned to the client.
- **Token expiry strategy:**  
  Access tokens expire every **15 minutes**.

---

### JWT Structure
A JWT consists of three parts:

- **Header:** Metadata about the token, including the signing algorithm
- **Payload:** User data such as:
  - User ID
  - Email
  - Issued time
  - Expiration time
- **Signature:**  
  Ensures the token has not been tampered with,
  The secret key is never sent to the client. If any part of the token is modified, signature verification fails.
  ``` 
  HMAC-SHA256(SECRET + HEADER + PAYLOAD) = JWT SIGNATURE
  ```

---

### JWT Validation & Filter
`JwtAuthenticationFilter` intercepts every incoming HTTP request before it reaches the controller.

- Extracts the JWT from the `Authorization` header
- Validates the token signature and expiry
- Extracts the user ID
- Stores authentication data in `SecurityContext`

This filter is the **single point of trust** in the application.

If a request is rejected here:
- Controllers are never reached
- Business logic never runs
- Data remains untouched

---

### Protected Endpoints
- Authentication endpoints (`/auth/register`, `/auth/login`) are **public**
- All other endpoints require a valid JWT

---

## API Endpoints

### Auth APIs

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and receive JWT |

### User APIs

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/me` | Get authenticated user ID |

> _File APIs will be added later_

---

## How to Run Locally

```
mvn spring-boot:run
```
Application runs on:
```
http://localhost:8080
```



