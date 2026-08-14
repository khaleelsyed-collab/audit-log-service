# Commit 1

## Intent

Initialize the repository baseline.

## Constraints

- No business logic
- No APIs
- No entities
- No repositories
- No services
- Documentation only

## Acceptance Criteria

- Spring Boot project builds successfully
- Repository structure verified
- README reflects the current project state
- No unsupported functionality introduced

## AI Tool

GitHub Copilot Chat (Agent Mode)

## Prompt

Review the current project baseline.

Verify:

- Maven project structure
- Java configuration
- Spring Boot configuration
- README accuracy

Do not generate implementation code.
Do not invent future functionality.

## AI Response Summary

GitHub Copilot verified the project structure, confirmed the repository baseline, reviewed the README, and suggested documentation improvements only.

## Engineer Validation

Accepted:
- Repository verification
- Documentation review

Modified:
- None

Rejected:
- None

## Validation

- Application starts successfully.
- Project structure verified.
- Documentation reviewed.

## Commit 2 – Requirement Analysis

- **Purpose:** Analyze assessment requirements and create an implementation roadmap.
- **AI Tool:** GitHub Copilot Agent + ChatGPT
- **Prompt:** Review the assessment and prepare an incremental implementation plan without generating code.
- **Accepted:** Requirement summary, assumptions, constraints, and roadmap.
- **Modified:** Simplified and reordered implementation tasks.
- **Rejected:** Features and recommendations beyond the assignment scope.
- **Validated:** Cross-checked with the assessment document.


## Commit 3 – Architecture Overview

- **Purpose:** Design the system architecture before implementation.
- **AI Tool:** GitHub Copilot Agent + ChatGPT
- **Prompt:** Create a concise architecture overview for the Audit Log Service.
- **Accepted:** Architecture, components, APIs, data model, and hash chain design.
- **Modified:** Simplified documentation and aligned it with the implementation roadmap.
- **Rejected:** Checkpoint optimization and implementation details beyond the current scope.
- **Validated:** Cross-checked with the assessment requirements.

## Commit 4 – AuditRecord Domain Model

**Purpose**
Implement the immutable `AuditRecord` entity.

**AI Tool**
GitHub Copilot Agent 

**Prompt**
Generate a JPA entity for immutable audit records using Spring Data JPA.

**Accepted**
- Entity structure
- Field definitions
- JPA annotations

**Modified**
- Package structure
- Column constraints
- Hash length
- Sequence number type

**Rejected**
- Repository generation
- Service generation
- Business logic

**Validation**
- Executed `mvn clean test`.
- Application started successfully.
- Manual review completed.

## Commit 5 – AuditRecord Repository

### Intent
Introduce the persistence layer for the `AuditRecord` entity.

### Constraints
- Use Spring Data JPA.
- Support only the current implementation stage.
- Avoid unnecessary repository methods.

### Acceptance Criteria
- Repository compiles successfully.
- Supports append operations.
- Supports future chain verification.

### AI Prompt
Requested implementation of the persistence layer for the existing `AuditRecord` entity using Spring Data JPA.

### AI Response Summary
Generated a repository interface extending `JpaRepository` with multiple derived query methods.

### Human Review
- Corrected the generated `AuditRecord` import to use the project's `entity` package.
- Removed repository methods that were not required for the current implementation stage.
- Retained only the repository methods supporting append operations and future chain verification.

### Validation
- Repository compiled successfully.
- Confirmed compatibility with the existing entity.

## Commit 6 – SHA-256 Hash Utility

### Intent
Implement a reusable utility for deterministic SHA-256 hash generation.

### Constraints
- Use Java standard library only.
- Ensure thread safety.
- Produce deterministic lowercase hexadecimal hashes.
- Avoid business logic.

### Acceptance Criteria
- Utility compiles successfully.
- Uses SHA-256.
- Returns deterministic results.
- Suitable for reuse by future services.

### AI Prompt
Requested implementation of a reusable SHA-256 hashing utility using Java's built-in `MessageDigest`.

### AI Response Summary
Generated a thread-safe utility class with a reusable hashing method and manual hexadecimal conversion.

### Human Review
Reviewed the generated implementation, verified deterministic hashing behavior, confirmed UTF-8 encoding and thread safety, and ensured the utility aligns with the project architecture.

### Validation
- Project compiled successfully.
- Utility reviewed for correctness and future reuse.

## Commit 7 – AuditRecordService

**Intent**
Implement the service responsible for appending immutable audit records.

**AI Prompt**
Generate a Spring service that computes sequence numbers, previous hashes, canonical record content, SHA-256 hashes, and persists immutable audit records.

**AI Response Summary**
Generated AuditRecordService and later refactored canonical string generation into CanonicalRecordUtil.

**My Review / Modifications**
- Reviewed the generated implementation.
- Extracted canonical string generation into a reusable utility.
- Verified package structure and imports.
- Confirmed transactional behavior and hash-chain logic.

## Validation

- Project builds successfully using `./mvnw clean test`.
- Reviewed generated code before committing.
- Refactored generated code where needed to improve readability and maintainability.

## Commit 8 - Search API

### AI Assistance
AI was used to:
- Generate the initial structure for JPA Specification.
- Suggest repository integration with JpaSpecificationExecutor.
- Generate boilerplate DTO classes.
- Draft controller and service implementations.

### Developer Review
Reviewed and validated:
- Specification predicates.
- Service-layer separation.
- Controller request mapping.
- DTO field exposure.
- Endpoint behavior using Postman.

### Manual Verification
- Executed `./mvnw clean test`
- Started application locally.
- Tested:
    - POST /audit
    - GET /audit
    - Filtering by actorId
    - Filtering by eventType
    - Pagination

## Commit 9 – Hash Chain Verification

### Task
Implement the chain verification endpoint required by Scenario A.

### AI Assistance
GitHub Copilot generated the initial implementation for:

- AuditVerificationService
- ChainVerificationResponse DTO
- AuditVerificationController

### Human Review
The generated code was reviewed and validated.

A timestamp precision issue was discovered during testing because PostgreSQL stores timestamps with microsecond precision while Java `Instant.now()` provides nanosecond precision.

The implementation was corrected by truncating timestamps before hashing:

```java
Instant effectiveTimestamp =
        (timestamp == null ? Instant.now() : timestamp)
                .truncatedTo(ChronoUnit.MICROS);
```

## Commit 10

### Prompt Summary

Requested implementation of the first phase of Scenario B:

- retention policy
- soft archival
- configurable retention days
- archive endpoint
- preserve verification logic

### AI Suggestions Used

Generated:

- AuditArchiveService
- AuditArchiveController
- ArchiveResponse DTO
- Repository query
- AuditRecord entity updates
- application.properties configuration

### Engineer Modifications

Reviewed all generated code.

Corrected entity annotations.

Verified repository query.

Resolved database schema issues.

Restarted application after schema update.

Verified archive endpoint.

Verified hash chain integrity remained unchanged.

### Verification

POST /audit/archive

Result:

200 OK

POST /audit/verify

Result:

{
"chainIntact": true
}

### Final Decision

Accepted with manual review and testing.

## Commit 11 – Structured Redaction

**AI Tool Used**
- GitHub Copilot
- ChatGPT

**Prompt Used**
Implement structured redaction for audit records by masking selected top-level JSON fields while preserving the original payload and hash chain.

**AI Suggestions Considered**
- Store a separate `redactedPayload`.
- Create a dedicated redaction service.
- Expose a REST endpoint for redaction.
- Use Jackson for JSON processing.

**Engineer Modifications**
- Changed masking value to `"********"`.
- Fixed Jackson imports to match the project.
- Added required validation dependency.
- Corrected Maven dependencies and build issues.
- Verified hash chain remains intact after redaction.

**Suggestions Rejected**
- Recomputing audit hashes after redaction.
- Modifying the original payload.
- Redacting nested JSON fields.

## AI Prompt Used

```
Implement a read-only payload retrieval API.

Requirements:
- Add GET /audit/{id}/payload endpoint.
- Support optional query parameter redacted=true.
- Return redactedPayload if available, otherwise original payload.
- Do not modify payload, hash or previousHash.
- Keep verification logic unchanged.
- Use service layer and DTOs.
```


## Commit 12 – Payload Retrieval API
---

## AI Tool Used

GitHub Copilot

---

## AI Suggestions

### Considered

- Added a dedicated service layer for payload retrieval.
- Created separate controller and response DTO.
- Used optional `redacted=true` query parameter.
- Used constructor injection.

### Modified

- Simplified controller implementation.
- Verified endpoint behavior manually.
- Updated project documentation.
- Ensured project coding conventions were followed.

### Rejected

- Returning entity objects directly.
- Modifying the original payload.
- Recomputing hashes after redaction.
- Changing existing verification logic.


## AI Prompt Used

```
Implement Merkle Tree verification for the audit log system.

Requirements:
- Build a Merkle Tree using stored audit record hashes.
- Use SHA-256 for node hashing.
- Duplicate the last node when the number of hashes is odd.
- Add GET /audit/merkle/root endpoint.
- Return totalRecords, merkleRoot and generatedAt.
- Do not modify existing audit records.
- Keep verification logic unchanged.
```

---

## AI Tool Used

GitHub Copilot

---

## AI Suggestions

### Considered

- Created a dedicated MerkleTreeService.
- Added a response DTO.
- Added a REST controller.
- Ordered records by sequence number.
- Used SHA-256 for node hashing.

### Modified

- Simplified implementation for readability.
- Verified all endpoints manually.
- Added project documentation.
- Maintained existing coding conventions.

### Rejected

- Modifying stored hashes.
- Updating audit records during Merkle generation.
- Recomputing existing record hashes.
- Changing audit verification logic.