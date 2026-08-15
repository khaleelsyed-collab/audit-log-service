# Audit Log Service

A Spring Boot 3.5.6 application built with Java 17 and Maven that implements a **tamper-evident Audit Log Service**. The system provides immutable audit logging using SHA-256 hash chaining and Merkle Tree verification, along with search, archival, redaction, export, and verification capabilities.

---

## Features

- Append-only immutable audit log storage
- SHA-256 hash chain for tamper detection
- Merkle Tree generation and verification
- Audit record verification
- Search with filtering and pagination
- Soft archival of audit records
- Payload redaction
- Export verifiable audit bundles
- HTTP Basic Authentication
- REST APIs documented using Swagger / OpenAPI
- Comprehensive Unit and Integration Tests
- JaCoCo Code Coverage Reports

---

## Technology Stack

- Java 17
- Spring Boot 3.5.6
- Spring MVC
- Spring Data JPA
- Spring Validation
- Spring Security
- Spring Boot Actuator
- PostgreSQL
- H2 Database (Testing)
- Maven
- Swagger / OpenAPI
- JaCoCo

---

## Architecture

The application follows a layered architecture:

```
Client
   │
   ▼
Controllers
   │
   ▼
Services
   │
   ▼
Repositories
   │
   ▼
PostgreSQL / H2
```

Security is implemented using Spring Security with HTTP Basic Authentication.

Data integrity is maintained using:

- SHA-256 Hash Chaining
- Merkle Tree Verification
- Immutable Audit Records

---

## Project Structure

```
src
├── main
│   ├── java
│   │   └── com.example.audit
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── entity
│   │       ├── exception
│   │       ├── repository
│   │       ├── service
│   │       ├── specification
│   │       └── util
│   └── resources
│
└── test
    ├── controller
    ├── service
    └── exception

docs
├── API.md
├── REQUIREMENTS.md
├── AI_USAGE.md
└── SCENARIO_C.md
```

---

## Prerequisites

- Java 17
- Maven 3.8+
- PostgreSQL (for production)
- H2 Database (for testing)

---

## Running the Application

Start the application:

```bash
./mvnw spring-boot:run
```

Application URL

```
http://localhost:8081
```

---

## Swagger / OpenAPI

Swagger UI

```
http://localhost:8081/swagger-ui.html
```

OpenAPI Specification

```
http://localhost:8081/v3/api-docs
```

---

## Build

Build the application

```bash
./mvnw clean package
```

Build without tests

```bash
./mvnw -DskipTests package
```

---

## Running Tests

Execute all tests

```bash
./mvnw test
```

Generate JaCoCo coverage report

```bash
./mvnw clean verify
```

Coverage report

```
target/site/jacoco/index.html
```

---

## Test Coverage

Current Project Coverage

- 58 Automated Tests
- Instruction Coverage: **82%**
- Branch Coverage: **64%**

Coverage reports are generated automatically using JaCoCo.

---

## Authentication

The application uses HTTP Basic Authentication.

| Username | Password | Role |
|----------|----------|------|
| admin | adminpass | ROLE_ADMIN |
| auditor | auditorpass | ROLE_AUDITOR |
| system | systempass | ROLE_SYSTEM |

---

## Sample APIs

### Create Audit Record

```bash
curl -u admin:adminpass \
-X POST http://localhost:8081/audit \
-H "Content-Type: application/json" \
-d '{
  "eventType":"USER_LOGIN",
  "actorId":"alice",
  "resourceType":"ACCOUNT",
  "resourceId":"1001",
  "payload":"{\"ip\":\"1.2.3.4\"}"
}'
```

---

### Search Records

```bash
curl -u auditor:auditorpass \
"http://localhost:8081/audit/search?actorId=alice&page=0&size=10"
```

---

### Verify Audit Chain

```bash
curl -u auditor:auditorpass \
http://localhost:8081/audit/verify
```

---

### Verify Single Record

```bash
curl -u auditor:auditorpass \
http://localhost:8081/audit/verify/1
```

---

### Archive Records

```bash
curl -u admin:adminpass \
-X POST http://localhost:8081/audit/archive
```

---

### Redact Payload

```bash
curl -u admin:adminpass \
-X POST http://localhost:8081/audit/redact/1 \
-H "Content-Type: application/json" \
-d '{
  "fields":[
      "ssn",
      "accountNumber"
  ]
}'
```

---

### Export Audit Bundle

```bash
curl -u auditor:auditorpass \
http://localhost:8081/audit/export
```

---

### Generate Merkle Root

```bash
curl -u auditor:auditorpass \
http://localhost:8081/audit/merkle/root
```

---

### Statistics

```bash
curl -u auditor:auditorpass \
http://localhost:8081/audit/stats
```

---

## Documentation

Additional documentation is available under the **docs/** directory.

- API.md
- REQUIREMENTS.md
- AI_USAGE.md
- SCENARIO_C.md

Additional project documentation:

- ATTESTATION.md

---

## Assumptions

- Audit records are immutable.
- Only payload redaction is permitted.
- Archive operations are soft deletes.
- SHA-256 is used for hash generation.
- Merkle Tree verifies exported bundles.

---

## Future Enhancements

- JWT/OAuth2 Authentication
- Docker Support
- Kubernetes Deployment
- Metrics Dashboard
- Distributed Tracing
- Database Partitioning

---

## AI Usage

AI assistance (GitHub Copilot and ChatGPT) was used to assist with:

- Documentation
- Unit Tests
- Integration Tests
- Swagger/OpenAPI configuration
- JaCoCo configuration

All generated code was manually reviewed, validated, and tested before acceptance.

---

## License

This project was developed for assessment purposes.