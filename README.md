# Audit Log Service

A Spring Boot 3.5.6 application built with Java 17 and Maven. This repository is an assessment prototype for a tamper-evident audit log service. It implements append-only audit recording, chain verification, search, retention (soft-archive), structured redaction, export bundles (verifiable via Merkle root), and supporting tooling.

## Stack

- Java 17
- Spring Boot 3.5.6
- Maven
- Spring Web MVC
- Spring Data JPA
- Validation
- Actuator
- PostgreSQL (production target)
- H2 database (development / in-memory for tests)

## Prerequisites

- Java 17 SDK
- Maven 3.8+ (or use the included Maven wrapper)

## Run locally

The application can be run with the bundled Maven wrapper. By default the project is configured to use PostgreSQL in `src/main/resources/application.properties`. For local development and tests the embedded H2 database is supported and tests will use an in-memory database.

Start the application:

```bash
./mvnw spring-boot:run
```

Run tests:

```bash
./mvnw test
```

If you want to run the application against H2 locally, set the following environment property or create `src/main/resources/application-local.properties` and enable the `local` profile (example not included):

```properties
spring.datasource.url=jdbc:h2:mem:auditlog;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

## Build

Create a production jar:

```bash
./mvnw -DskipTests package
```

## Authentication

This service is protected with HTTP Basic authentication. Three in-memory users are provided for the assessment:

- admin / adminpass (ROLE_ADMIN)
- auditor / auditorpass (ROLE_AUDITOR)
- system / systempass (ROLE_SYSTEM)

Use credentials with curl examples below: e.g. `curl -u auditor:auditorpass ...`

## Sample HTTP examples

Replace `localhost:8080` with your configured server/port.

- Create (append) an audit record

```bash
curl -X POST http://localhost:8080/audit \
  -H "Content-Type: application/json" \
  -d '{"eventType":"USER_LOGIN","actorId":"alice","resourceType":"ACCOUNT","resourceId":"1001","payload":"{\"ip\":\"1.2.3.4\"}","timestamp":null}'
```

- Search (paged) with filters

```bash
curl "http://localhost:8080/audit/search?actorId=alice&page=0&size=10"
```

- Full chain verification

```bash
curl http://localhost:8080/audit/verify
```

- Single record verification by id

```bash
curl http://localhost:8080/audit/verify/1
```

- Run retention archival (soft-archive)

```bash
curl -X POST http://localhost:8080/audit/archive
```

- Redact top-level fields from a record payload (replace {id})

```bash
curl -X POST http://localhost:8080/audit/redact/1 \
  -H "Content-Type: application/json" \
  -d '{"fields":["ssn","accountNumber"]}'
```

- Export verifiable bundle

```bash
curl "http://localhost:8080/audit/export"
curl "http://localhost:8080/audit/export?actorId=alice"
curl "http://localhost:8080/audit/export?resourceId=1001"
```

- Get Merkle root for all records

```bash
curl http://localhost:8080/audit/merkle/root
```

- Get statistics

```bash
curl http://localhost:8080/audit/stats
```

## Documentation

Additional docs are available under `docs/`:

- `docs/REQUIREMENTS.md` — requirement analysis and assumptions
- `docs/AI_USAGE.md` — AI usage traceability log
- `docs/SCENARIO_C.md` — Compliance reporting clarification and design
- `docs/API.md` — API reference for implemented endpoints

## Notes

- All APIs are read-only where required by the assessment (no update/delete endpoints are exposed).
- The system stores an immutable audit record and maintains a hash-chain and Merkle root for tamper-evidence.
- See `ATTESTATION.md` for the attestation and a summary of AI assistance used in the project.
