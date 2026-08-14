# ATTESTATION

## Candidate Information

**Full Name:** Khaleel Syed

**Email Address:** khaleelsyed1786@gmail.com

**Assignment Title:** AI-Assisted Software Engineering Assessment – Audit Log Service

**Started On:** 13-Aug-2026

**Submitted On:** 14-Aug-2026

## Attestation

I, Khaleel Syed, attest that this submission is my own individual work, completed on my own machine and accounts, and that it honestly reflects my development process and use of AI.

## Commit 1 – Project Initialization

**Objective**
- Initialized the Spring Boot project baseline and repository documentation.

**AI Tool**
- GitHub Copilot (Agent Mode)
- ChatGPT

**AI Prompt**
- Reviewed the Spring Boot project scaffold and repository documentation without generating implementation code.

**Accepted**
- Repository structure validation.
- Documentation improvement suggestions.

**Modified**
- Refined README wording.
- Improved documentation structure.

**Rejected**
- Future implementation suggestions outside the current project scope.

**Validation**
- Application starts successfully.
- Maven project builds successfully.
- Repository baseline verified.


## Commit 2 – Requirement Analysis

**Objective**
- Reviewed the assessment requirements and prepared an implementation roadmap.

**AI Tool**
- GitHub Copilot Agent
- ChatGPT

**AI Prompt**
- Requested a concise requirement analysis and incremental implementation plan.

**Engineer Review**

**Accepted**
- Requirement summary.
- Implementation roadmap.

**Modified**
- Simplified the roadmap.
- Reordered implementation tasks.

**Rejected**
- Suggestions beyond the assessment scope.

**Validation**
- Reviewed against the assessment document.

## Commit 3 – Architecture Overview

**Objective**
- Designed the application architecture before implementation.

**AI Tool**
- GitHub Copilot Agent
- ChatGPT

**AI Prompt**
- Requested a concise architecture overview covering components, APIs, data model, hash chain, and engineering decisions.

**Engineer Review**

**Accepted**
- High-level architecture.
- Component responsibilities.
- API overview.
- Hash chain design.
- Engineering decisions.

**Modified**
- Simplified architecture documentation.
- Removed implementation details planned for later commits.

**Rejected**
- Checkpoint optimization and implementation-specific recommendations outside the current scope.

**Validation**
- Reviewed against the assignment requirements.

## Commit 4 – AuditRecord Domain Model

**Objective**
- Implemented the immutable `AuditRecord` JPA entity.

**AI Tool**
- GitHub Copilot Agent
- 
**AI Prompt**
- Requested generation of an immutable JPA entity for audit records using Spring Data JPA.

**Engineer Review**

**Accepted**
- Entity structure
- JPA annotations
- Constructors
- Getter-only design

**Modified**
- Moved entity to the `entity` package.
- Changed `sequenceNumber` to `Long`.
- Added `unique = true` for `sequenceNumber`.
- Updated hash column length to 64 characters.

**Rejected**
- Repository, service, and business logic generation.

**Validation**
- Project compiled successfully using `mvn clean test`.
- Spring Boot application started successfully.
- Entity reviewed against the architecture and requirement documents.

**Engineer Sign-off**
- Reviewed and approved for commit.

## Commit 5 – AuditRecord Repository

- Added the `AuditRecordRepository` using Spring Data JPA.
- AI generated an initial repository interface with additional query methods.
- I reviewed the implementation, removed repository methods not required at this stage, and retained only the methods supporting append operations and chain verification.
- I verified the repository aligns with the current implementation plan and follows incremental development.
- I reviewed the generated repository, corrected the entity import to match the project's package structure, removed repository methods not required at this stage, and retained only the methods supporting append operations and chain verification.

## Commit 6 – SHA-256 Hash Utility

- Added a reusable SHA-256 hashing utility for deterministic hash generation.
- AI generated the initial utility implementation using Java's `MessageDigest`.
- I reviewed the implementation, verified thread safety and deterministic behavior, confirmed UTF-8 encoding and lowercase hexadecimal output, and adjusted the exception handling to better reflect application state.

### Commit 7 – Audit service implementation

- Implemented AuditRecordService for immutable audit record creation.
- Added CanonicalRecordUtil to build deterministic canonical strings for hashing.
- Configured a genesis hash value for the first record in the chain.
- Reviewed and refined the AI-generated implementation before committing.

## Commit 8 - Audit Search API


Implemented a search API for audit records with optional filtering and pagination.

Changes:
- Added JPA Specifications for dynamic filtering.
- Extended repository with JpaSpecificationExecutor.
- Added search service method.
- Implemented GET /audit endpoint.
- Added search response DTO excluding payload for security.
- Supports filtering by actorId, resourceType, resourceId, eventType, and timestamp range.

## Commit 9 – Audit Chain Verification Endpoint

### AI Assistance
- Used GitHub Copilot to scaffold the verification service, response DTO, and REST controller.
- AI-generated code was manually reviewed and refined before acceptance.

### Human Contribution
- Reviewed the verification algorithm.
- Fixed timestamp precision mismatch causing hash verification failures by truncating timestamps to microsecond precision before hashing.
- Verified canonical string generation.
- Tested hash recomputation against persisted records.
- Validated successful chain verification using the `/audit/verify` endpoint.
- Removed temporary debugging statements after verification.

### Verification Performed
- Created a new audit record.
- Invoked `GET /audit/verify`.
- Confirmed the response:

```json
{
  "chainIntact": true,
  "firstBrokenSequence": null,
  "violation": null,
  "message": "Chain intact"
}

```

## Commit 10 – Retention Policy (Soft Archival)

### AI Tools
- GitHub Copilot

### Engineer Changes
Implemented the first phase of retention management by introducing soft archival for audit records.

Changes include:
- Added `archived` and `archivedAt` fields to `AuditRecord`.
- Added configurable retention period using `audit.retention.days`.
- Implemented repository query to retrieve expired, non-archived records.
- Created `AuditArchiveService` to archive expired records.
- Added `/audit/archive` endpoint for manual archive execution.
- Records are marked archived instead of being deleted.
- Existing verification logic remains unchanged.

### Testing Performed
- Started application successfully.
- Verified new database columns were created.
- Executed:

POST /audit/archive

- Verified successful response:

```json
{
  "archivedRecords": 0,
  "message": "Retention policy executed successfully"
}