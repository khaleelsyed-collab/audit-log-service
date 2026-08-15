# Scenario C — Compliance Reporting

## Problem Statement

Regulators require the ability to audit access to client account data. The requirement is intentionally broad and does not define:

- Which audit events must be included.
- Who is authorized to request reports.
- The reporting period.
- Whether reports should contain full payloads or only metadata.
- How exported reports should be verified for integrity.

This implementation defines a practical scope that satisfies the assessment requirements while remaining aligned with the implemented functionality.

---

## Identified Ambiguities

The following requirements are not explicitly defined:

- Which event types represent access to client account data.
- Whether reports are intended for internal auditors or external regulators.
- Applicable retention and redaction policies.
- Whether exported reports require cryptographic verification.
- Expected report size and delivery mechanism.

---

## Assumptions

The implementation makes the following assumptions:

- Records with `resourceType = ACCOUNT` represent client account activity.
- Reports are requested only by authenticated and authorized users.
- Exported reports must preserve audit integrity.
- If a `redactedPayload` exists, it is preferred over the original payload during export.
- Existing audit records remain immutable after creation.

---

## Design Decisions

The compliance reporting feature is implemented using the existing audit export capability.

The exported bundle contains:

- Audit records in sequence order.
- Record hashes.
- First and last sequence information.
- Merkle Root for integrity verification.
- Export metadata.

Integrity is preserved by:

- SHA-256 hash chaining between audit records.
- Merkle Root generation across exported record hashes.
- Independent verification using the exported cryptographic data.
- Full chain verification through the `/audit/verify` endpoint.

Sensitive information is protected by exporting `redactedPayload` whenever available while preserving the original record hash.

---

## Why This Solution Meets the Requirement

The implementation provides:

- Ordered audit records for the requested account.
- Tamper-evident verification using hash chaining.
- Independent verification through the exported Merkle Root.
- Support for payload redaction.
- Immutable audit history.

This provides regulators or auditors with verifiable evidence without modifying the original audit records.

---

## Limitations

The current implementation intentionally limits scope.

- Export requests assume authorized users.
- Audit logging of report generation requests is not implemented.
- Reports are generated in memory and are not streamed.
- Raw payloads may be included when no redacted version exists.
- Digital signatures for exported bundles are not implemented.

These items would normally be required in a production environment.

---

## Future Enhancements

Possible production improvements include:

- Role-based access control for compliance report generation.
- Audit logging of report requests.
- Streaming support for large exports.
- Digitally signed export bundles.
- Configurable redaction policies.
- Encrypted report delivery.
- Scheduled compliance report generation.

---

## Implemented Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/audit/export` | Export verifiable audit records |
| `/audit/verify` | Verify the complete audit chain |
| `/audit/verify/{id}` | Verify an individual audit record |
| `/audit/merkle/root` | Retrieve the current Merkle Root |