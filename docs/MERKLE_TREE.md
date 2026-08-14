# Merkle Tree Verification

## Overview

A Merkle Tree is a binary hash tree used to efficiently verify the integrity of a collection of audit records.

Each audit record hash forms a leaf node.

Adjacent hashes are concatenated and hashed using SHA-256 until a single root hash remains.

## Algorithm

1. Read audit records ordered by sequence number.
2. Use each record hash as a leaf node.
3. Pair adjacent hashes.
4. Concatenate the left and right hashes.
5. Compute SHA-256.
6. Repeat until one hash remains.
7. The final hash is the Merkle Root.

If the number of hashes is odd, the last hash is duplicated before hashing.

## API

```
GET /audit/merkle/root
```

## Sample Response

```json
{
  "totalRecords": 10,
  "merkleRoot": "<sha256-root>",
  "generatedAt": "2026-08-14T18:00:00Z"
}
```

## Benefits

- Efficient integrity verification
- Tamper detection
- Cryptographic proof of audit log consistency
- Scalable for large audit logs