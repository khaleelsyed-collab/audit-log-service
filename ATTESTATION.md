# ATTESTATION

## Candidate Information

**Full Name:** Khaleel Syed

**Email Address:** khaleelsyed1786@gmail.com

**Assignment Title:** AI-Assisted Software Engineering Assessment – Audit Log Service

**Started On:** 14-Aug-2026

**Submitted On:** 15-Aug-2026

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
```

## Commit 11 – Structured Redaction

### Engineer Changes

Implemented structured payload redaction while preserving audit chain integrity.

Changes include:
- Added `redactedPayload` field to store a sanitized copy of the payload.
- Implemented `AuditRedactionService` to redact selected top-level JSON fields.
- Added `POST /audit/redact/{id}` endpoint.
- Original payload, hash, previousHash and sequence number remain unchanged.
- Verification endpoint continues validating the original audit chain.

### Testing Performed

- Created an audit record.
- Executed:

POST /audit/redact/{id}

- Verified selected fields were masked.
- Verified original payload remained unchanged.
- Executed:

GET /audit/verify

- Verified response:

```json
{
  "chainIntact": true,
  "message": "Chain intact"
}

```

## Commit 12 – Payload Retrieval API

### Engineer Changes

Implemented a read-only payload retrieval API that supports returning either the original audit payload or a previously redacted payload without modifying immutable audit records.

Changes include:

- Added `AuditPayloadViewService` for payload retrieval.
- Added `AuditPayloadController`.
- Added `PayloadResponse` DTO.
- Added `GET /audit/{id}/payload` endpoint.
- Added optional `redacted=true` query parameter.
- Returns the redacted payload when available; otherwise returns the original payload.
- Original payload, hash, previousHash, and verification logic remain unchanged.

### Testing Performed

- Started the application successfully.
- Verified:

```http
GET /audit/1/payload
```

Returns the original payload.

- Verified:

```http
GET /audit/1/payload?redacted=true
```

Returns the redacted payload when available.

- Confirmed original payload remains unchanged.
- Confirmed audit verification continues to succeed.


## Commit 13 – Merkle Tree Verification

### Engineer Changes

Implemented Merkle Tree root generation to provide efficient integrity verification for the audit log.

Changes include:

- Added `MerkleTreeService`.
- Added `AuditMerkleController`.
- Added `MerkleRootResponse`.
- Added `GET /audit/merkle/root` endpoint.
- Built a Merkle Tree using stored audit record hashes.
- Used SHA-256 to compute parent nodes.
- Duplicated the last node when a layer contains an odd number of hashes.
- Returned the Merkle Root, total record count, and generation timestamp.
- Existing audit records and verification logic remain unchanged.

### Testing Performed

Successfully verified:

- `GET /audit/verify`
- `GET /audit/{id}/payload`
- `GET /audit/{id}/payload?redacted=true`
- `POST /audit/archive`
- `GET /audit/merkle/root`

Confirmed that the Merkle Root is generated successfully without modifying existing audit records.

## Commit 14 – Single Record Verification

### Engineer Changes

Implemented verification for an individual audit record.

Changes include:

- Added `AuditRecordVerificationService`.
- Recomputes the hash for a selected audit record.
- Compares computed hash with stored hash.
- Added `GET /audit/verify/{id}` endpoint.
- Returns verification status together with stored and computed hashes.
- Existing audit records remain unchanged.

### Testing Performed

- Started the application successfully.
- Verified existing APIs continue to work.
- Called:

GET /audit/verify/1

- Verified response contains:

```json
{
  "valid": true
}
```

- Verified stored hash matches recomputed hash.

## Commit 15 – Audit Record Export

### Engineer Changes

Implemented a read-only audit export feature.

Changes include:

- Added `AuditExportService`.
- Added `ExportRecordResponse` DTO.
- Added `GET /audit/export` endpoint.
- Returns all audit records ordered by sequence number.
- Exposes only export-related fields.
- Internal database fields are excluded.
- Existing audit records remain unchanged.

### Testing Performed

- Started the application successfully.
- Verified existing APIs continue to work.
- Called:

GET /audit/export

- Verified audit records were returned in sequence order.
- Verified internal fields (`id`, `archived`, `archivedAt`, `redactedPayload`) are not exposed.

### Commit 16 – Audit Statistics Feature

The Audit Statistics feature was implemented with AI-assisted code generation.

Manual verification included:

- Source code review
- Successful Maven build
- Endpoint testing
- Validation of repository queries

### Commit 17

**AI Prompt Used:**
Implemented a dynamic audit search endpoint using Spring Data JPA Specifications with optional filters (actorId, eventType, resourceType, resourceId), ordered by sequenceNumber, and returned results using a response DTO.

**AI Response:**
Generated:
- AuditSearchController
- AuditSearchService
- AuditSearchResponse DTO
- Reused existing AuditRecordSpecification for filtering.

**Engineer Validation:**
- Reviewed generated code.
- Verified filtering by actorId, eventType, resourceType, and resourceId.
- Tested combined filters and empty results.
- Confirmed results are ordered by sequenceNumber.

### Commit 18 - Export Bundle
## Feature: Verifiable Export Bundle

Implemented a verifiable export bundle for audit records.

Features:
- Export all audit records or filter by actorId or resourceType + resourceId.
- Includes chain metadata:
    - First sequence
    - Last sequence
    - First hash
    - Last hash
    - Merkle root
    - Generated timestamp
- Export preserves sequence order.
- Merkle root is generated from exported record hashes.
- Read-only implementation with no impact to existing hash-chain verification.

## Commit 19 

AI assistance (GitHub Copilot and ChatGPT) was used to generate implementation suggestions.

All generated code was:

- reviewed manually
- modified where required
- compiled successfully
- validated using REST endpoints
- committed only after verification

Examples of manual review:

- Modified export filters to support resourceType + resourceId
- Reused existing MerkleTreeService instead of duplicating logic
- Preserved existing hashing implementation
- Verified pagination behaviour

## Final Implementation Summary (Documentation & Tests)

- Export bundle enhancements: Added `ExportBundleResponse` and updated the `/audit/export` endpoint to accept optional `actorId` and `resourceId` filters. Export returns ordered records and bundle metadata (first/last sequence and hashes, merkleRoot, generatedAt).
- Search pagination: Enhanced `/audit/search` to support `page` and `size` query parameters and return a `Page<AuditSearchResponse>` ordered by `sequenceNumber`.
- Documentation updates: README expanded with prerequisites, run/build instructions, and sample curl commands. Added `docs/SCENARIO_C.md` and expanded `docs/API.md`.
- Tests: Added automated JUnit 5 tests covering append operations, hash chain generation, full chain verification, single-record verification, search filters and pagination, archive operation, redaction operation, export bundle generation, merkle root computation, and statistics aggregation.

## Manual validation performed

- Built project: `./mvnw -DskipTests package`
- Ran test suite: `./mvnw test` (tests use embedded H2 database)
- Manually exercised critical endpoints with curl and verified expected responses: POST /audit, GET /audit/search, GET /audit/verify, GET /audit/verify/{id}, POST /audit/archive, POST /audit/redact/{id}, GET /audit/export, GET /audit/merkle/root, GET /audit/stats

## Engineering decisions

- Maintained all existing hashing, verification, archive, and Merkle logic. No changes were made to cryptographic or verification algorithms.
- Tests are written to validate external behaviors (API contracts and service outputs) and rely on the same utilities used in production code so they remain accurate with implementation.
- For verification semantics concerning archived records, the system continues to store records as archived (soft-delete) and verification continues to consider archived records as part of the chain; this approach preserves full historical tamper-evidence. Alternate policies (skip archived ranges) are documented in `docs/SCENARIO_C.md`.

## AI usage review

- AI was used extensively to accelerate boilerplate code generation (services, DTOs, controllers) and to draft test scaffolding and documentation.
- Every AI-generated suggestion was reviewed and either accepted, modified, or rejected by the engineer; modifications and rationale are recorded in `docs/AI_USAGE.md`.
- No AI-generated code was merged without manual validation, unit/integration testing and engineer sign-off.

**Engineer Sign-off**

I attest that the work integrated in this repository is my own, and that I have documented AI usage and review decisions as required by the assessment.

Khaleel Syed
- Verified search filters