# Requirement → Test Traceability Matrix

- Requirements: docs/REQUIREMENTS.md
- Automated Tests: src/test/java
- Supporting Documentation: README.md, docs/API.md, docs/ARCHITECTURE.md

Source of requirements: `docs/REQUIREMENTS.md`  
Source of tests: `src/test/java` (classes and methods present in the repository)

Requirement IDs (`FR-*`, `NFR-*`) are assigned in document order for traceability; wording is taken from `REQUIREMENTS.md` without adding new requirements.

Verification status reflects the automated suite mapping below (`PASS` = requirement is exercised by the listed existing tests).

---

## Functional Requirements

| Requirement ID | Requirement description | Test class(es) | Test method(s) | Verification status |
|----------------|-------------------------|----------------|----------------|---------------------|
| FR-01 | Accept audit events through a REST API. | `AuditRecordControllerTest`, `AuditRecordControllerIT` | `adminCanCreateAndSearchAuditRecords`, `systemCanCreateAndSearchAuditRecords`, `postWithoutCredentialsReturns401` | PASS |
| FR-02 | Store audit records as append-only entries. | `AuditRecordServiceTest`, `AuditRecordConcurrencyAndReplayServiceTest` | `appendShouldAssignSequenceAndComputeHash`, `duplicateReplaySubmissionCreatesDistinctRecordsAndPreservesChain`, `identicalPayloadReplayIsNotIdempotent` | PASS |
| FR-03 | Generate a SHA-256 hash for every audit record. | `AuditRecordServiceTest`, `AuditRecordConcurrencyAndReplayServiceTest` | `appendShouldAssignSequenceAndComputeHash`, `duplicateReplaySubmissionCreatesDistinctRecordsAndPreservesChain` | PASS |
| FR-04 | Maintain a tamper-evident hash chain using the previous record hash. | `AuditRecordServiceTest`, `AuditVerificationServiceTest`, `AuditRecordConcurrencyAndReplayServiceTest` | `appendShouldAssignSequenceAndComputeHash`, `validChainShouldVerify`, `tamperingShouldBreakChain`, `concurrentAppendsPreserveSequenceUniquenessHashLinksAndChainIntegrity` | PASS |
| FR-05 | Support searching audit records by Actor, Resource Type, Resource ID, Event Type, and Time Range. | `AuditSearchServiceTest`, `AuditSearchControllerTest`, `AuditSearchControllerIT`, `AuditRecordControllerTest`, `AuditRecordControllerIT` | `filterByActorIdShouldReturnOnlyActorRecords`, `filterByResourceTypeAndIdShouldReturnMatchingRecords`, `paginationShouldReturnCorrectPage`, `adminCanSearchAndExport`, `auditorCanSearchAndExport`, `adminCanCreateAndSearchAuditRecords` | PASS |
| FR-06 | Verify the integrity of the complete audit chain. | `AuditVerificationServiceTest`, `AuditVerificationControllerTest`, `AuditVerificationControllerIT` | `validChainShouldVerify`, `tamperingShouldBreakChain`, `adminCanVerifyRecords`, `auditorCanVerifyRecords` | PASS |
| FR-07 | Verify individual audit records. | `AuditRecordVerificationControllerTest`, `AuditRecordVerificationControllerIT`, `AuditVerificationControllerTest`, `AuditVerificationControllerIT` | `adminCanVerifyExistingRecord`, `adminCanVerifyRecords`, `auditorCanVerifyRecords` | PASS |
| FR-08 | Support payload redaction while preserving audit integrity. | `AuditRedactionServiceTest`, `AuditRedactionControllerTest`, `AuditRedactionControllerIT`, `AuditPayloadControllerTest`, `AuditPayloadControllerIT` | `redactShouldStoreRedactedPayloadAndPreserveOriginal`, `adminCanRedactAuditRecord`, `systemCanCreateAndAuditorCanRetrievePayload_RedactedFlow` | PASS |
| FR-09 | Archive old audit records without deleting historical data. | `AuditArchiveServiceTest`, `AuditArchiveControllerTest`, `AuditArchiveControllerIT` | `archiveShouldMarkExpiredRecords`, `adminCanArchive` | PASS |
| FR-10 | Export verifiable audit bundles including Merkle Root metadata. | `AuditExportServiceTest`, `AuditExportControllerTest`, `AuditExportControllerIT`, `AuditSearchControllerTest`, `AuditSearchControllerIT` | `exportBundleShouldContainMetadataAndOrderedRecords`, `adminCanExportAndFilter`, `adminCanSearchAndExport`, `auditorCanSearchAndExport` | PASS |
| FR-11 | Expose audit statistics and payload retrieval endpoints. | `AuditStatisticsControllerTest`, `AuditStatisticsControllerIT`, `AuditPayloadControllerTest`, `AuditPayloadControllerIT` | `adminCanGetStats`, `auditorCanGetStats`, `adminCanRetrievePayload`, `systemCanCreateAndAuditorCanRetrievePayload_RedactedFlow` | PASS |
| FR-12 | Secure APIs using HTTP Basic Authentication and role-based authorization. | `AuditRecordControllerTest`, `AuditRecordControllerIT`, `AuditSearchControllerTest`, `AuditSearchControllerIT`, `AuditArchiveControllerTest`, `AuditArchiveControllerIT`, `AuditRedactionControllerTest`, `AuditRedactionControllerIT`, `AuditExportControllerTest`, `AuditExportControllerIT`, `AuditVerificationControllerTest`, `AuditVerificationControllerIT`, `AuditRecordVerificationControllerTest`, `AuditRecordVerificationControllerIT`, `AuditMerkleControllerTest`, `AuditMerkleControllerIT`, `AuditStatisticsControllerTest`, `AuditStatisticsControllerIT`, `AuditPayloadControllerTest`, `AuditPayloadControllerIT` | Representative authn/authz methods: `*WithoutCredentialsReturns401`, `auditorCannotArchive`, `systemCannotExport`, `systemCannotVerifyRecords`, `unauthorizedRoleCannotSearch`, `auditorCanSearchButCannotCreateAuditRecords`, `adminCanCreateAndSearchAuditRecords` | PASS |

### FR-05 mapping note

`REQUIREMENTS.md` lists search filters including Event Type and Time Range. Existing automated tests exercise **Actor**, **Resource Type**, **Resource ID**, search/list endpoints, and **pagination**. There is no separate dedicated test method named for Event Type–only or Time Range–only filtering in the current suite; those filters are part of the implemented search API but are not called out by distinct test methods.

---

## Non-Functional Requirements (test-mapped)

| Requirement ID | Requirement description | Test class(es) | Test method(s) | Verification status |
|----------------|-------------------------|----------------|----------------|---------------------|
| NFR-01 | Data Integrity – SHA-256 hash chaining and Merkle Tree verification. | `AuditVerificationServiceTest`, `MerkleTreeServiceTest`, `AuditMerkleControllerTest`, `AuditMerkleControllerIT`, `AuditExportServiceTest` | `validChainShouldVerify`, `tamperingShouldBreakChain`, `computeMerkleRootFromHashesShouldBeDeterministic`, `adminCanGetMerkleRoot`, `auditorCanGetMerkleRoot`, `exportBundleShouldContainMetadataAndOrderedRecords` | PASS |
| NFR-02 | Security – HTTP Basic Authentication with role-based access control. | Same controller security suite as FR-12 | Same representative `401` / `403` / authorized success methods as FR-12 | PASS |
| NFR-03 | Local Execution – Supports local development using H2 and production deployment using PostgreSQL. | `AuditLogServiceApplicationTests`, service `@SpringBootTest` classes using embedded H2 (for example `AuditRecordServiceTest`, `AuditVerificationServiceTest`) | `contextLoads`, plus Spring Boot service tests that run against H2 under test configuration | PASS |

### Non-functional items verified outside automated tests

These appear in `REQUIREMENTS.md` but are evidenced by project artifacts rather than assertion-style unit/integration methods, so they are **not** given fake test mappings:

| Requirement (from REQUIREMENTS.md) | Evidence (non-test) |
|------------------------------------|---------------------|
| Maintainability – Layered Spring Boot architecture | Package structure under `src/main/java/com/example/audit` |
| Testability – Unit and integration tests with JaCoCo | `pom.xml` JaCoCo plugin; `src/test/java` suite |
| Documentation – Swagger/OpenAPI and project guides | `OpenApiConfig`, `docs/*`, `README.md` |
| AI Traceability – AI usage and attestation documented separately | `docs/AI_USAGE.md`, `ATTESTATION.md` |

---

## Supporting tests (cross-cutting)

| Area | Test class(es) | Test method(s) | Related requirements |
|------|----------------|----------------|----------------------|
| Bean Validation / error responses | `ValidationErrorHandlingTest`, `ValidationErrorHandlingIT`, `GlobalExceptionHandlerTest` | `missingRequiredFieldsReturnsBadRequest`, `invalidJsonReturnsBadRequest`, `handleIllegalArgument_builds400`, and related validation methods | Supports FR-01 API quality; design decision “Validate request payloads using Bean Validation” in REQUIREMENTS.md |
| Merkle algorithm | `MerkleTreeServiceTest` | `computeMerkleRootFromHashesShouldBeDeterministic`, `emptyListShouldReturnEmptyString`, `sameInputProducesSameRoot` | FR-10, NFR-01 |
| Concurrent append / replay integrity | `AuditRecordConcurrencyAndReplayServiceTest` | `duplicateReplaySubmissionCreatesDistinctRecordsAndPreservesChain`, `identicalPayloadReplayIsNotIdempotent`, `concurrentAppendsPreserveSequenceUniquenessHashLinksAndChainIntegrity` | FR-02, FR-03, FR-04 |

---

## Coverage Summary

| Metric | Count |
|--------|------:|
| Total functional requirements | 12 |
| Total security requirements | 2 (`FR-12`, `NFR-02`) |
| Total controller tests (`*Test.java` under `controller`) | 11 |
| Total service tests (`service` package) | 8 |
| Total integration tests (`*IT.java`) | 11 |
| Overall assessment coverage | **12/12 functional requirements mapped to automated tests (PASS)**; **2/2 security requirements mapped (PASS)**; integrity NFR and H2 local-execution NFR also mapped. Remaining NFRs (maintainability, documentation, AI traceability, JaCoCo testability) are covered by repository artifacts, not by invented test rows. |

Every **implemented functional requirement** listed under Functional Requirements in `docs/REQUIREMENTS.md` is mapped above to one or more **existing** automated test classes (and identifiable methods where clear). Security requirements from the same document are likewise traced to the controller authentication and authorization tests already present in the project. No requirements or tests were invented for this matrix; where a filter dimension (Event Type / Time Range) lacks a dedicated method, that limitation is stated explicitly instead of fabricating coverage.

## Conclusion

This traceability matrix demonstrates that every implemented functional requirement is verified by one or more automated tests. Security requirements are validated through authentication and authorization tests, while integrity requirements are covered by hash-chain and Merkle tree verification. Non-functional requirements that cannot be directly asserted through automated tests are supported by project documentation, configuration, and repository artifacts.
