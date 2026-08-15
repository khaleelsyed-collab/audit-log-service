# Scenario C — Compliance Reporting

Problem statement

Regulators require the ability to audit access to client account data. The requirement is intentionally ambiguous: it does not specify which events, what time range, what level of detail, or who may request the report. The goal is to define a clear, defensible scope for a compliance reporting feature and provide an implementation that satisfies the likely regulatory need while limiting scope to what can be implemented safely in the assessment.

Ambiguities identified

- Which events count as "access to client account data" (reads, writes, metadata-only)?
- Who may request the report (internal auditors, external regulators)?
- What retention, redaction, or anonymization policies apply to reports? 
- Whether the regulator needs raw payloads or summarized metadata.

Assumptions made

- "Access" includes any event with resourceType set to `ACCOUNT` (reads/writes/permission changes).
- Exported reports are requested by an authorized internal auditor; handling external regulator authentication/authorization is out of scope for this implementation.
- Reports must preserve tamper evidence: exported bundles include chain metadata and Merkle root so recipients can verify integrity.
- Sensitive values in payloads should be redacted before export if a redactedPayload is available; otherwise original payload is included (the system stores both to preserve chain integrity).

Design decisions

- Scope: implement a verifiable export bundle for a given resourceId (account) that includes ordered records, first/last sequence numbers and hashes, and a Merkle root computed over the exported record hashes.
- Verification: the recipient can recompute the Merkle root from exported hashes to verify integrity. Full chain verification is available via the `/audit/verify` endpoint on the origin system.
- Redaction: redactedPayload is used when available to avoid leaking sensitive fields; redaction does not alter stored hashes.
- Access control: this assessment does not implement regulator authentication/authorization — in a production system this would be required (audit trail for report requests, secure transmission, and access control enforcement).

Why this satisfies the scenario

- The export bundle supplies ordered events affecting an account (resourceId) and includes the cryptographic anchors (first/last hashes and Merkle root) required for recipients to verify integrity.
- The approach balances regulator needs (verifiable records) and privacy (redaction support and avoidance of unauthorized raw payload distribution).

Trade-offs and limitations

- The current export includes payload by default. For sensitive production use, exports should by default use redactedPayload and only include raw payloads under strict controls.
- The system assumes authorized requesters; enforcement and audit logging of who requested a report are out of scope and should be added in production.
- Large exports are returned in-memory; for production, streaming or chunked export formats are recommended.

Future improvements

- Implement RBAC for export requests and maintain an audit trail for report generation requests.
- Support streaming exports and signed bundle artifacts.
- Provide configurable redaction policies and explainability for what was redacted in each export.
- Allow export of payloads encrypted for recipient with key management and access controls.


