# Audit Log Service API

Base URL: `http://<host>:<port>`

## Authentication

All endpoints use HTTP Basic authentication.

The application configures three in-memory users:

- `admin` / `admin123` with role `ROLE_ADMIN`
- `auditor` / `auditor123` with role `ROLE_AUDITOR`
- `system` / `system123` with role `ROLE_SYSTEM`

Example:

```bash
curl -u admin:admin123 http://localhost:8080/audit
```

## Common status codes

- `200 OK`
- `201 Created`
- `400 Bad Request`
- `401 Unauthorized` (missing or invalid Basic credentials)
- `403 Forbidden` (authenticated but insufficient role)
- `404 Not Found` (resource not found in endpoints that accept IDs)
- `500 Internal Server Error` (can occur when a missing record causes a runtime exception)

## Common error response

Validation and request parsing failures are returned as JSON in the following shape:

```json
{
  "timestamp": "2026-08-15T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "eventType is required",
  "path": "/audit"
}
```

This is produced by the global `GlobalExceptionHandler` for validation errors such as `MethodArgumentNotValidException`, `ConstraintViolationException`, `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`, and `IllegalArgumentException`.

---

## POST /audit

Create a new audit record.

- Authentication: `ROLE_ADMIN`, `ROLE_SYSTEM`
- Content-Type: `application/json`
- Request body: `AuditRecordRequest`

### Fields

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `eventType` | string | yes | Event category such as `LOGIN` |
| `actorId` | string | yes | User or service acting on the resource |
| `resourceType` | string | yes | Resource category such as `ACCOUNT` |
| `resourceId` | string | yes | Resource identifier |
| `payload` | string | yes | Payload as a JSON string, max 5000 chars |
| `timestamp` | ISO-8601 string | no | Optional caller-supplied timestamp |

### Example request

```http
POST /audit
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json
```

```json
{
  "eventType": "LOGIN",
  "actorId": "user-123",
  "resourceType": "ACCOUNT",
  "resourceId": "account-100",
  "payload": "{\"status\":\"ACTIVE\"}",
  "timestamp": "2026-08-15T12:00:00Z"
}
```

### Example response

```json
{
  "id": 100,
  "sequenceNumber": 42,
  "hash": "e3b0c44298fc1c149afbf4c8996fb924...",
  "previousHash": "0000000000000000000000000000000000000000000000000000000000000000",
  "timestamp": "2026-08-15T12:00:00Z"
}
```

### Status codes

- `201 Created`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`

---

## GET /audit

Retrieve audit records with optional filters and pagination.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`, `ROLE_SYSTEM`
- Query parameters:
  - `actorId` (optional, string)
  - `resourceType` (optional, string)
  - `resourceId` (optional, string)
  - `eventType` (optional, string)
  - `from` (optional, ISO-8601 instant)
  - `to` (optional, ISO-8601 instant)
  - `page` (optional, default `0`)
  - `size` (optional, default `10`, min `1`, max `200`)

### Example request

```http
GET /audit?actorId=user-123&resourceType=ACCOUNT&page=0&size=10
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "content": [
    {
      "id": 100,
      "sequenceNumber": 42,
      "eventType": "LOGIN",
      "actorId": "user-123",
      "resourceType": "ACCOUNT",
      "resourceId": "account-100",
      "timestamp": "2026-08-15T12:00:00Z",
      "hash": "e3b0c44298fc1c149afbf4c8996fb924..."
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

### Status codes

- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`

---

## GET /audit/search

Search audit records with optional filters and pagination.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`, `ROLE_SYSTEM`
- Query parameters:
  - `actorId` (optional, max 128)
  - `eventType` (optional, max 128)
  - `resourceType` (optional, max 128)
  - `resourceId` (optional, max 128)
  - `page` (optional, default `0`, min `0`)
  - `size` (optional, default `10`, min `1`, max `200`)

### Example request

```http
GET /audit/search?eventType=LOGIN&page=0&size=10
Authorization: Basic YW5hbHlzdDp0ZXN0
```

### Example response

```json
{
  "content": [
    {
      "sequenceNumber": 42,
      "eventType": "LOGIN",
      "actorId": "user-123",
      "resourceType": "ACCOUNT",
      "resourceId": "account-100",
      "timestamp": "2026-08-15T12:00:00Z",
      "hash": "e3b0c44298fc1c149afbf4c8996fb924..."
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "unsorted": false,
      "sorted": true,
      "empty": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0,
  "empty": false
}
```

### Status codes

- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`

---

## GET /audit/verify

Verify the hash chain across all stored audit records.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`
- No request body.

### Example request

```http
GET /audit/verify
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "chainIntact": true,
  "firstBrokenSequence": null,
  "violation": null,
  "message": "Audit chain is intact"
}
```

### Status codes

- `200 OK`
- `401 Unauthorized`
- `403 Forbidden`

---

## GET /audit/verify/{id}

Verify a single audit record by database ID.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`
- Path parameter:
  - `id` (required, positive long)

### Example request

```http
GET /audit/verify/100
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "id": 100,
  "valid": true,
  "storedHash": "e3b0c44298fc1c149afbf4c8996fb924...",
  "computedHash": "e3b0c44298fc1c149afbf4c8996fb924...",
  "message": "Record hash matches stored value"
}
```

### Status codes

- `200 OK`
- `400 Bad Request` (invalid ID)
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found` (if the service returns not found)
- `500 Internal Server Error` (missing record can trigger an exception in current implementation)

---

## GET /audit/export

Export audit records as a bundle with verification metadata.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`
- Query parameters:
  - `actorId` (optional, max 128)
  - `resourceType` (optional, max 128)
  - `resourceId` (optional, max 128)

### Example request

```http
GET /audit/export?actorId=user-123&resourceType=ACCOUNT
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "actorId": "user-123",
  "resourceType": "ACCOUNT",
  "resourceId": null,
  "totalRecords": 2,
  "firstSequence": 40,
  "lastSequence": 41,
  "firstHash": "000abc...",
  "lastHash": "def456...",
  "merkleRoot": "a1b2c3...",
  "generatedAt": "2026-08-15T12:00:00Z",
  "records": [
    {
      "sequenceNumber": 40,
      "eventType": "LOGIN",
      "actorId": "user-123",
      "resourceType": "ACCOUNT",
      "resourceId": "account-100",
      "timestamp": "2026-08-15T11:45:00Z",
      "payload": "{\"status\":\"ACTIVE\"}",
      "previousHash": "000abc...",
      "hash": "def456..."
    }
  ]
}
```

### Status codes

- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`

---

## GET /audit/merkle/root

Compute the Merkle root for the current set of audit records.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`
- No request body.

### Example request

```http
GET /audit/merkle/root
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "totalRecords": 42,
  "merkleRoot": "a1b2c3d4...",
  "generatedAt": "2026-08-15T12:00:00Z"
}
```

### Status codes

- `200 OK`
- `401 Unauthorized`
- `403 Forbidden`

---

## GET /audit/{id}/payload

Return the payload for a specific audit record, optionally as a redacted view.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`
- Path parameter:
  - `id` (required, positive long)
- Query parameter:
  - `redacted` (optional, boolean, default `false`)

### Example request

```http
GET /audit/100/payload?redacted=true
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "id": 100,
  "redacted": true,
  "payload": "{\"email\":\"REDACTED\"}"
}
```

### Status codes

- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found` (if the record is missing)
- `500 Internal Server Error` (can occur when no record is found)

---

## POST /audit/archive

Run the retention policy and archive expired records.

- Authentication: `ROLE_ADMIN`
- No request body.

### Example request

```http
POST /audit/archive
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "archivedRecords": 5,
  "message": "Retention policy executed successfully"
}
```

### Status codes

- `200 OK`
- `401 Unauthorized`
- `403 Forbidden`

---

## POST /audit/redact/{id}

Apply redaction to a record's payload by specifying top-level field names.

- Authentication: `ROLE_ADMIN`
- Path parameter:
  - `id` (required, positive long)
- Request body: `RedactionRequest`

### Request body

```json
{
  "fields": ["email", "ssn"]
}
```

### Example request

```http
POST /audit/redact/100
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Content-Type: application/json
```

```json
{
  "fields": ["email", "ssn"]
}
```

### Example response

```json
{
  "id": 100,
  "redactedPayload": "{\"email\":\"REDACTED\",\"ssn\":\"REDACTED\"}",
  "message": "Redaction applied"
}
```

### Status codes

- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found` (if the record is missing)
- `500 Internal Server Error` (can occur when no record is found)

---

## GET /audit/stats

Return aggregate statistics for the audit store.

- Authentication: `ROLE_ADMIN`, `ROLE_AUDITOR`
- No request body.

### Example request

```http
GET /audit/stats
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Example response

```json
{
  "totalRecords": 123,
  "activeRecords": 120,
  "archivedRecords": 3,
  "latestSequenceNumber": 123,
  "firstRecordTimestamp": "2026-01-01T00:00:00Z",
  "lastRecordTimestamp": "2026-08-15T12:00:00Z"
}
```

### Status codes

- `200 OK`
- `401 Unauthorized`
- `403 Forbidden`

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