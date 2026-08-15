# Audit Log Service API

Base URL: http://{host}:{port}

---

## POST /audit

- Purpose: Append a new audit record (append-only).
- Request body (JSON):
  - eventType (string, required)
  - actorId (string, required)
  - resourceType (string, required)
  - resourceId (string, required)
  - payload (string or JSON, optional)
  - timestamp (ISO-8601 string, optional — if omitted server assigns current time)

- Sample request:
```
POST /audit
{
  "eventType":"USER_LOGIN",
  "actorId":"alice",
  "resourceType":"ACCOUNT",
  "resourceId":"1001",
  "payload":"{\"ip\":\"1.2.3.4\"}"
}
```
- Sample response: 201 Created
```
{
  "sequenceNumber": 1,
  "eventType": "USER_LOGIN",
  "actorId": "alice",
  "resourceType": "ACCOUNT",
  "resourceId": "1001",
  "timestamp": "2026-08-10T12:00:00Z",
  "hash": "..."
}
```

- Status codes: 201 Created, 400 Bad Request

---

## GET /audit/search

- Purpose: Search audit records using optional filters and pagination.
- Query parameters:
  - actorId (optional)
  - eventType (optional)
  - resourceType (optional)
  - resourceId (optional)
  - page (optional, default 0)
  - size (optional, default 10)

- Returns: Page of AuditSearchResponse ordered by sequenceNumber ascending.

- Sample request:
```
GET /audit/search?actorId=alice&page=0&size=10
```
- Sample response: 200 OK (Page with content)
```
{
  "content": [
    {
      "sequenceNumber": 1,
      "eventType": "USER_LOGIN",
      "actorId": "alice",
      "resourceType": "ACCOUNT",
      "resourceId": "1001",
      "timestamp": "2026-08-10T12:00:00Z",
      "hash": "..."
    }
  ],
  "pageable": { /* omitted */ },
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

- Status codes: 200 OK

---

## GET /audit/verify

- Purpose: Verify the full hash chain across all stored records and report whether it is intact.
- Sample request:
```
GET /audit/verify
```
- Sample response: 200 OK
```
{
  "chainIntact": true,
  "brokenAtSequence": null,
  "message": "Chain intact"
}
```

- Status codes: 200 OK

---

## GET /audit/verify/{id}

- Purpose: Verify a single audit record by its database id.
- Path parameter: id (long)
- Sample request:
```
GET /audit/verify/1
```
- Sample response: 200 OK
```
{
  "id": 1,
  "valid": true,
  "storedHash": "...",
  "computedHash": "...",
  "message": "Record hash matches"
}
```

- Status codes: 200 OK, 500 RuntimeException if record not found

---

## POST /audit/archive

- Purpose: Run retention policy and mark expired records as archived (soft-delete).
- Sample request:
```
POST /audit/archive
```
- Sample response: 200 OK
```
{
  "archivedCount": 5
}
```

- Status codes: 200 OK

---

## POST /audit/redact/{sequenceNumber}

- Purpose: Redact specified top-level fields in a record payload. Stores redactedPayload but does not change hashes.
- Path parameter: sequenceNumber (long)
- Request body (JSON): { "fields": ["ssn","accountNumber"] }

- Sample request:
```
POST /audit/redact/1
{
  "fields":["ssn","accountNumber"]
}
```
- Sample response: 200 OK
```
{
  "sequenceNumber": 1,
  "redactedPayload": "{\"ssn\":\"********\",\"accountNumber\":\"********\"}"
}
```

- Status codes: 200 OK, 400 Bad Request

---

## GET /audit/export

- Purpose: Export a self-contained, verifiable bundle of audit records. Supports optional filters.
- Query parameters:
  - actorId (optional)
  - resourceId (optional)

- Behavior: If no filters provided, exports all records. If actorId provided, exports records for that actor. If resourceId provided, exports records for that resource. Results ordered by sequenceNumber ascending.

- Sample requests:
```
GET /audit/export
GET /audit/export?actorId=alice
GET /audit/export?resourceId=1001
```
- Sample response: 200 OK
```
{
  "actorId": null,
  "resourceId": "1001",
  "totalRecords": 10,
  "firstSequence": 1,
  "lastSequence": 10,
  "firstHash": "...",
  "lastHash": "...",
  "merkleRoot": "...",
  "generatedAt": "2026-08-10T12:00:00Z",
  "records": [ /* ExportRecordResponse[] */ ]
}
```

- Status codes: 200 OK

---

## GET /audit/merkle/root

- Purpose: Compute and return the Merkle root over all record hashes (ordered by sequenceNumber).
- Sample request:
```
GET /audit/merkle/root
```
- Sample response: 200 OK
```
{
  "merkleRoot": "..."
}
```

- Status codes: 200 OK

---

## GET /audit/stats

- Purpose: Return summary statistics about the audit store.
- Response fields:
  - totalRecords
  - activeRecords
  - archivedRecords
  - latestSequenceNumber
  - firstRecordTimestamp
  - lastRecordTimestamp

- Sample request:
```
GET /audit/stats
```
- Sample response: 200 OK
```
{
  "totalRecords": 123,
  "activeRecords": 120,
  "archivedRecords": 3,
  "latestSequenceNumber": 123,
  "firstRecordTimestamp": "2026-01-01T00:00:00Z",
  "lastRecordTimestamp": "2026-08-10T12:00:00Z"
}
```

- Status codes: 200 OK

---

For more details, refer to the source DTOs and controller JavaDocs in the codebase.

### Endpoint

GET /audit/search

### Query Parameters (optional)

- actorId
- eventType
- resourceType
- resourceId

### Example

GET /audit/search?actorId=admin

### Response

Returns audit records matching the supplied filters, ordered by sequenceNumber.

## Export Audit Bundle

### GET /audit/export

Exports audit records together with verification metadata.

### Query Parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| actorId | No | Export records for an actor |
| resourceType | No | Resource type filter |
| resourceId | No | Resource id filter |


## GET /audit/search

Description

Search audit records using optional filters.

Query Parameters

- actorId
- eventType
- resourceType
- resourceId
- page
- size

Example

GET /audit/search?actorId=khaleel&page=0&size=10

## GET /audit/export

Description

Exports a verifiable audit bundle.

Optional query parameters

- actorId
- resourceType
- resourceId

Example

GET /audit/export

GET /audit/export?actorId=khaleel

GET /audit/export?resourceType=USER&resourceId=123