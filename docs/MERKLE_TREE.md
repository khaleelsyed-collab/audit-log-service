# Merkle Tree Verification

## Overview

The Audit Log Service uses a **Merkle Tree** to efficiently verify the integrity of a collection of audit records.

Each audit record contributes its SHA-256 hash as a leaf node. Pairs of hashes are concatenated and hashed repeatedly until a single hash remains. This final hash is the **Merkle Root**, which provides a cryptographic fingerprint of the entire audit log or exported record set.

Unlike full hash-chain verification, a Merkle Root enables efficient integrity verification without processing every individual record.

---

## Algorithm

1. Load audit records ordered by `sequenceNumber`.
2. Use each record's stored SHA-256 hash as a leaf node.
3. Pair adjacent hashes.
4. Concatenate `leftHash + rightHash`.
5. Compute the SHA-256 hash of the concatenated value.
6. Repeat until a single hash remains.
7. Return the final hash as the **Merkle Root**.

If the number of hashes at any level is odd, the final hash is duplicated before computing the next level.

---

## API

```
GET /audit/merkle/root
```

---

## Sample Response

```json
{
  "totalRecords": 10,
  "merkleRoot": "<sha256-root>",
  "generatedAt": "2026-08-14T18:00:00Z"
}
```

---

## Use Cases

- Verify exported audit bundles
- Detect unauthorized modification of audit records
- Provide a cryptographic integrity proof
- Support regulator and auditor verification
- Enable efficient validation without replaying the complete audit chain

---

## Limitations

- The Merkle Root verifies record integrity but does not replace full hash-chain verification.
- Any modification to a record hash changes the Merkle Root.
- An empty audit log returns an empty Merkle Root.

---

## Related Documentation

- `docs/ARCHITECTURE.md`
- `docs/API.md`
- `docs/SCENARIO_C.md`