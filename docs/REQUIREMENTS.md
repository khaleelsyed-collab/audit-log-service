# Requirement Analysis

## Functional Requirements

The application implements the following functional requirements:

- Accept audit events through a REST API.
- Store audit records as append-only entries.
- Generate a SHA-256 hash for every audit record.
- Maintain a tamper-evident hash chain using the previous record hash.
- Support searching audit records by:
    - Actor
    - Resource Type
    - Resource ID
    - Event Type
    - Time Range
- Verify the integrity of the complete audit chain.
- Verify individual audit records.
- Support payload redaction while preserving audit integrity.
- Archive old audit records without deleting historical data.
- Export verifiable audit bundles including Merkle Root metadata.
- Expose audit statistics and payload retrieval endpoints.
- Secure APIs using HTTP Basic Authentication and role-based authorization.

---

## Non-Functional Requirements

The implementation focuses on the following quality attributes:

- **Data Integrity** – SHA-256 hash chaining and Merkle Tree verification.
- **Security** – HTTP Basic Authentication with role-based access control.
- **Maintainability** – Layered Spring Boot architecture with separation of concerns.
- **Testability** – Unit and integration tests with JaCoCo coverage reporting.
- **Documentation** – Swagger/OpenAPI documentation and project guides.
- **AI Traceability** – AI usage and attestation documented separately.
- **Local Execution** – Supports local development using H2 and production deployment using PostgreSQL.

---

## Assumptions

The following assumptions were made during implementation:

- Java 17 and Spring Boot 3.x are used as the technology stack.
- PostgreSQL is the production database.
- H2 is used for local development and automated testing.
- SHA-256 provides sufficient cryptographic hashing for tamper detection.
- Audit records are immutable after creation.
- Export requests are performed only by authorized users.
- Payload redaction does not modify the original audit hash.

---

## Constraints

The implementation intentionally follows these constraints:

- Audit records cannot be updated or deleted.
- Business logic is implemented incrementally.
- Human review is performed for AI-assisted contributions.
- The implementation focuses on the assessment scope and excludes enterprise-scale deployment features.

---

## Design Decisions

The following design decisions were made:

- Use an append-only audit log.
- Preserve tamper evidence through hash chaining.
- Generate a Merkle Root for exported audit bundles.
- Separate business logic into controller, service, repository, and utility layers.
- Validate request payloads using Bean Validation.
- Secure endpoints using Spring Security and role-based authorization.
- Document APIs using OpenAPI (Swagger).

---

## Out of Scope

The following capabilities are intentionally excluded:

- Distributed audit storage.
- Multi-tenant support.
- Digital signatures for exported bundles.
- Streaming export of very large datasets.
- External identity providers (OAuth2, LDAP, SSO).
- Long-term archival storage.

---

## Implementation Roadmap

The project was implemented in the following stages:

1. Project setup and dependency configuration.
2. Domain model and persistence layer.
3. Hash generation and tamper-evident chain.
4. REST API implementation.
5. Search, verification, archive, and export features.
6. Security configuration.
7. Integration and unit testing.
8. OpenAPI documentation.
9. Project documentation and AI traceability.