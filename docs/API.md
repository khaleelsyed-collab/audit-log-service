# Audit Log Service API

## Base URL

```
http://localhost:8080
```

---

# 1. Create Audit Record

## POST /audit

### Request

```json
{
  "eventType": "USER_CREATED",
  "actorId": "admin",
  "resourceType": "USER",
  "resourceId": "1001",
  "payload": "{\"name\":\"John\"}"
}
```

---

# 2. Verify Entire Audit Chain

## GET /audit/verify

Verifies the integrity of the complete audit chain.

---

# 3. Verify Single Audit Record

## GET /audit/verify/{id}

Example

```
GET /audit/verify/1
```

---

# 4. View Original Payload

## GET /audit/{id}/payload

Example

```
GET /audit/1/payload
```

---

# 5. View Redacted Payload

## GET /audit/{id}/payload?redacted=true

Example

```
GET /audit/1/payload?redacted=true
```

---

# 6. Archive Old Records

## POST /audit/archive

### Request

```json
{
  "days": 90
}
```

---

# 7. Export Audit Records

## GET /audit/export

Returns all audit records.

---

# 8. Merkle Root

## GET /audit/merkle/root

Returns the Merkle Root for the current audit chain.

---

# 9. Audit Statistics

## GET /audit/stats

Returns

- totalRecords
- activeRecords
- archivedRecords
- latestSequenceNumber
- firstRecordTimestamp
- lastRecordTimestamp