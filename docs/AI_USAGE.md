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

## Commit 14 – Single Record Verification

### AI Tool Used

GitHub Copilot

### AI Prompt

Implement verification for a single audit record by recomputing its hash and exposing a REST endpoint. Do not modify existing verification logic or stored audit records.

### AI Suggestions Accepted

- Generated verification service.
- Generated verification controller.
- Generated response DTO.
- Used existing hashing utilities.

### Engineer Modifications

- Reviewed generated implementation.
- Verified canonical hash computation.
- Ensured existing APIs continued working.
- Performed end-to-end testing.

### AI Suggestions Rejected

- None.

## Commit 15 – Audit Record Export

### AI Tool Used

GitHub Copilot

### AI Prompt

Implement a read-only audit export feature that returns all audit records ordered by sequence number. Use a DTO instead of exposing the entity directly and do not expose internal database fields.

### AI Suggestions Accepted

- Generated export service.
- Generated export controller.
- Generated response DTO.
- Ordered records by sequence number.

### Engineer Modifications

- Reviewed generated implementation.
- Simplified DTO imports.
- Verified exported fields.
- Performed end-to-end testing.

### AI Suggestions Rejected

- None.

## Commit 16 – Audit Statistics Feature

### AI Assistance

GitHub Copilot was used to generate the initial implementation for:

- AuditStatisticsService
- AuditStatisticsController
- AuditStatisticsResponse

The generated code was manually reviewed, compiled, tested, and refined before being committed.

### Commit 17
### Audit Search

GitHub Copilot assisted with:

- AuditSearchService
- AuditSearchController
- AuditSearchResponse DTO

The generated code was reviewed, compiled, manually tested, and adjusted before committing.

### Commit 18 - Export Bundle

**Prompt**

> Implement a verifiable audit export bundle supporting optional actor and resource filters. Reuse the existing Merkle tree implementation and preserve existing verification behavior.

**Engineer Review**

- Verified exported records remain ordered by sequence number.
- Verified actor and resource filters.
- Verified bundle includes first/last hashes and Merkle root.
- Confirmed read-only implementation.

### Commit 19 - Pagination feature

## Feature
Export Verifiable Audit Bundle

### Prompt
Implemented a verifiable export bundle supporting optional actorId, resourceType and resourceId filters.
The bundle should include metadata, exported records, and a Merkle root while reusing the existing Merkle implementation.
Do not modify hashing, archive, verification or redaction logic.

### AI Response Summary
GitHub Copilot suggested:
- ExportBundleResponse DTO
- AuditExportController updates
- AuditExportService implementation
- Reuse of MerkleTreeService
- Repository methods for filtered export

### Review Performed
Reviewed every generated class and verified:
- Constructor injection
- Existing endpoints were not broken
- Merkle computation reused existing implementation
- Export ordering preserved by sequenceNumber
- Read-only behaviour maintained

### Changes Accepted
- ExportBundleResponse DTO
- Reuse of MerkleTreeService
- Controller implementation
- Service implementation
- Export metadata generation

### Changes Modified
Changed export filtering to support:

- actorId
- resourceType + resourceId

instead of only actorId/resourceId.

Updated repository accordingly.

### Changes Rejected
Did not use:
- actorId precedence logic
- resourceId-only filtering

Implemented resourceType + resourceId filtering to match assessment requirements.

### Validation
Manually tested:

- GET /audit/export
- GET /audit/export?actorId=khaleel
- GET /audit/export?resourceType=USER&resourceId=123

Verified:

- exported records
- Merkle root
- metadata
- ordering

## Commit 20 - Documentation & Tests (Latest)

**Purpose**

Finalize documentation updates (README, API reference, Scenario C) and add automated tests covering core services for the final submission.

**AI Tool Used**

GitHub Copilot + ChatGPT

**Prompts Given to AI**

- Generate README additions: prerequisites, run instructions, sample curl examples.
- Create concise SCENARIO_C.md explaining compliance reporting ambiguities, assumptions and design decisions.
- Expand API.md to document all endpoints with examples and status codes.
- Append the latest AI usage notes to AI_USAGE.md and ATTESTATION.md.
- Generate JUnit 5 + Mockito test classes for services: AuditRecordService, AuditVerificationService, AuditExportService, AuditSearchService, AuditArchiveService, AuditRedactionService.

**AI Response Summary**

The AI produced draft documentation text and suggested test scaffolding. For tests it suggested a mix of unit tests with Mockito and light-weight integration tests using @SpringBootTest and an embedded database.

**What was accepted**

- README additions (prerequisites, run, build, sample curl commands).
- SCENARIO_C.md concise design and assumptions.
- API.md expanded endpoint documentation.
- AI usage log entries documenting these steps.
- A set of tests that exercise append, verification, search (filters + pagination), export bundle, merkle root, archive and redaction operations using the application context and an embedded DB.

**What was modified**

- Tests were adapted to use `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)` so they run against an in-memory H2 instance regardless of the default datasource.
- Test data creation was centralized within each test to keep tests independent and readable.
- Assertions were written to verify high-level contract behavior (sequence numbers, hash presence, chain intact/broken, export metadata and merkle root) rather than internal implementation details.

**What was rejected**

- Any AI suggestion that modified hashing or verification logic.
- Suggestion to change REST contracts or database schema.

**Engineer Reasoning**

- Documentation and tests demonstrate the acceptance criteria for the assessment and provide reproducible steps for reviewers to run and validate the system locally.
- Tests rely on the same hashing and Merkle helpers used in production code to ensure exported bundle integrity checks are aligned with the runtime behavior.

**Validation Performed**

- Ran `./mvnw -DskipTests package` to build the project.
- Executed `./mvnw test` to run the new unit and integration tests locally (tests use embedded DB).
- Manually exercised key endpoints with curl where necessary to validate examples used in the README.

### Objective
Implement authentication and role-based authorization to secure the audit log REST APIs according to the assessment requirements.

### AI Assistance
GitHub Copilot was used to:

- Generate an initial Spring Security configuration.
- Configure HTTP Basic Authentication.
- Create three in-memory users (ADMIN, AUDITOR, SYSTEM).
- Generate BCrypt password encoding configuration.
- Add @PreAuthorize annotations to controller endpoints.
- Suggest role mappings for REST APIs.
- Generate JavaDoc comments for new security configuration.

### Human Review & Changes
I manually:

- Reviewed all generated security code.
- Verified authentication using Postman.
- Tested unauthorized (401) and forbidden (403) scenarios.
- Adjusted endpoint role mappings to match the required access policy.
- Confirmed application startup and existing tests passed after the changes.

### Validation Performed

- Application starts successfully.
- Maven build successful.
- Existing unit tests pass.
- Manual API testing using Basic Authentication.
- Verified role-based authorization with Admin, Auditor and System users.

### Commit 
## AI Tool Used

GitHub Copilot

## Purpose

GitHub Copilot was used as a coding assistant to accelerate development and improve code quality.

## Areas Where AI Assisted

- Spring Security configuration
- Role-based authorization using Spring Security
- OpenAPI (Swagger) documentation
- Integration test generation
- Validation annotations
- Global exception handling
- JavaDoc generation
- General code refactoring
- Import optimization

## Human Contributions

The project was not generated entirely by AI.

I personally:

- Designed and understood the application structure
- Reviewed every AI-generated change before accepting it
- Modified generated code where required
- Fixed compilation issues
- Fixed dependency/version compatibility issues
- Verified endpoint behaviour manually using Postman and Swagger UI
- Executed and validated all automated tests
- Ensured the final implementation satisfies the assessment requirements

## Verification

Every AI-generated change was manually reviewed, compiled, executed, and tested before being committed.

AI assistance was used only as a productivity aid. Final implementation decisions, debugging, validation, and testing were performed manually.


### Commit 
### AI Tools
- GitHub Copilot
- ChatGPT

### AI Assistance
- Generated JaCoCo configuration
- Generated integration and unit tests
- Added exception handler tests
- Improved test coverage and documentation

### Human Validation
- Reviewed all generated code
- Executed `mvn clean verify`
- Verified BUILD SUCCESS
- Validated JaCoCo and Surefire reports

### Outcome
- 58 tests passed
- 82% instruction coverage
- 64% branch coverage

## Session: Replay & Concurrency Testing

| Item | Details |
|------|---------|
| Feature | Replay and concurrency test coverage |
| AI Tool Used | Cursor AI |
| AI Response | Generated replay and concurrent append test scenarios and suggested concurrency handling. |
| Engineer Decision | Reviewed, refined, and integrated the tests. Restored synchronized append implementation after validating concurrency behavior. |
| Trade-off | Retained synchronized locking for single-instance consistency. Distributed locking or database-level coordination identified as a production enhancement. |

---

## Session: Documentation Improvements

| Item | Details |
|------|---------|
| Feature | README, API, Requirements and Scenario C updates |
| AI Tool Used | ChatGPT + Cursor AI |
| AI Response | Suggested documentation improvements, endpoint descriptions and assessment clarifications. |
| Engineer Decision | Reviewed and edited all documentation to match the implemented application before updating. |
| Trade-off | Documentation reflects the current implementation only; future enhancements are documented separately. |

## Session: Security Authorization Testing

| Item | Details |
|------|---------|
| Feature | Authentication and authorization test coverage |
| AI Tool Used | Cursor AI |
| AI Response | Generated 401/403 authorization tests for controller endpoints. |
| Engineer Decision | Reviewed, corrected failing integration tests, and verified the complete test suite before accepting changes. |
| Trade-off | Existing application behavior was preserved; tests were aligned with implementation instead of modifying production code. |

## Security Hardening

### AI Tool
Cursor AI

### Prompt Summary
Review the Spring Security configuration and suggest production-oriented improvements for HTTP security headers and CORS without changing the application's functional behavior.

### AI Response
Suggested adding explicit security headers (X-Frame-Options, X-Content-Type-Options, Referrer-Policy, HSTS) and tightening CORS configuration for production.

### Engineer Review
Reviewed every recommendation manually.
Accepted security headers.
Modified the CORS recommendation to remain compatible with assessment requirements and local testing.
Removed unsupported API (`permissionsPolicy`) because it was not available in the project's Spring Security version.

### Trade-offs
Kept permissive origins for local assessment while documenting that production deployments should restrict trusted origins.