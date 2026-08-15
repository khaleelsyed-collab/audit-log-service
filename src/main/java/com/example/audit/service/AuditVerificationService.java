package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.util.CanonicalRecordUtil;
import com.example.audit.util.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.audit.dto.ChainVerificationResponse;

import java.util.List;

/**
 * Service to verify the integrity of the audit record hash chain.
 *
 * Verification approach:
 * - Load all records ordered by sequenceNumber ascending.
 * - Start with a configured genesis value as the expected previous hash.
 * - For each record:
 *   - Rebuild canonical string (using the record's stored previousHash so the
 *     canonicalization matches the original computation).
 *   - Recompute SHA-256 over the canonical string.
 *   - Compare recomputed hash with the stored hash on the record.
 *   - Compare the record's stored previousHash with the expected previous hash
 *     (the hash of the previous record, or genesis for the first record).
 * - On first mismatch, stop and return a ChainVerificationResponse describing
 *   the violation.
 */
@Service
public class AuditVerificationService {

    private final AuditRecordRepository repository;
    private final String genesisValue;

    public AuditVerificationService(AuditRecordRepository repository,
                                    @Value("${audit.log.genesis-value:audit-log-genesis-v1}") String genesisValue) {
        this.repository = repository;
        this.genesisValue = genesisValue;
    }

    /**
     * Verify the full audit chain and return a ChainVerificationResponse.
     *
     * @return ChainVerificationResponse describing whether the chain is intact and
     *         where it breaks if not.
     */
    public ChainVerificationResponse verifyChain() {
        List<AuditRecord> records = repository.findAllByOrderBySequenceNumberAsc();

        String expectedPreviousHash = genesisValue;

        for (AuditRecord record : records) {
            // Rebuild canonical string using the stored previousHash so the
            // recomputed hash matches the original computation inputs.
            String canonical = CanonicalRecordUtil.buildCanonicalString(
                    record.getEventType(),
                    record.getActorId(),
                    record.getResourceType(),
                    record.getResourceId(),
                    record.getPayload(),
                    record.getTimestamp(),
                    record.getSequenceNumber(),
                    record.getPreviousHash()
            );


            String recomputedHash = HashUtil.sha256Hex(canonical);


            if (!recomputedHash.equals(record.getHash())) {
                String msg = String.format("Recomputed hash mismatch at sequence %d", record.getSequenceNumber());
                return new ChainVerificationResponse(false, record.getSequenceNumber(), "HASH_MISMATCH", msg);
            }

            if (!record.getPreviousHash().equals(expectedPreviousHash)) {
                String msg = String.format("Previous hash mismatch at sequence %d: expected=%s stored=%s", record.getSequenceNumber(), expectedPreviousHash, record.getPreviousHash());
                return new ChainVerificationResponse(false, record.getSequenceNumber(), "PREVIOUS_HASH_MISMATCH", msg);
            }

            // advance expected previous hash to this record's stored hash
            expectedPreviousHash = record.getHash();
        }

        return new ChainVerificationResponse(true, null, null, "Chain intact");
    }
}
