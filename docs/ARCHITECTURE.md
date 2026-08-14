# Architecture Overview

## 1. System Overview

The Audit Log Service is implemented as a Spring Boot application that provides immutable audit logging with tamper-evident verification.

The system exposes REST APIs for:

- Creating audit records
- Querying audit records
- Verifying the integrity of the audit chain

Audit records are stored in PostgreSQL (production) and H2 (local development).

---

## 2. Architecture Components

### Controller Layer

- Receives HTTP requests
- Performs request validation
- Returns API responses

### Service Layer

- Implements business logic
- Generates hashes
- Maintains the hash chain
- Coordinates persistence

### Repository Layer

- Uses Spring Data JPA
- Persists immutable audit records
- Supports filtering and pagination

### Database

- PostgreSQL (Production)
- H2 (Development)

### Verification Component

Responsible for validating the integrity of the complete audit chain.

---

## 3. Data Model

AuditRecord contains:

- id
- eventType
- actorId
- resourceType
- resourceId
- payload
- timestamp
- sequenceNumber
- previousHash
- hash

---

## 4. API Overview

POST /audit

Create an immutable audit record.

GET /audit

Retrieve audit records using filters.

GET /audit/verify

Verify the integrity of the audit chain.

---

## 5. Hash Chain Design

Each record contains:

- Current record hash
- Previous record hash

The first record references a predefined Genesis Hash.

The verification process recomputes hashes and validates the complete chain.

---

## 6. Hash Algorithm

Algorithm:
SHA-256

Reason:

- Industry standard
- Strong collision resistance
- Widely supported
- Deterministic

---

## 7. Engineering Decisions

- Immutable audit records
- Append-only architecture
- Server-managed sequence numbers
- SHA-256 hashing
- Spring Data JPA
- Externalized configuration

---

## 8. Trade-offs

Advantages

- Strong tamper detection
- Simple implementation
- Easy verification

Limitations

- Full verification is O(n)
- Payload storage increases database size

---

## 9. Assumptions

- PostgreSQL is the production database.
- H2 is used for local development.
- SHA-256 is sufficient for tamper detection.
- Update and delete operations are not exposed.