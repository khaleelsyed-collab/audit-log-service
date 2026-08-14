package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.util.CanonicalRecordUtil;
import com.example.audit.util.HashUtil;
import org.springframework.stereotype.Service;
import com.example.audit.controller.RecordVerificationResponse;

/**
 * Service to verify a single audit record's integrity by recomputing its hash
 * and comparing it to the stored hash. This does not alter any stored data.
 */
@Service
public class AuditRecordVerificationService {

    private final AuditRecordRepository repository;

    public AuditRecordVerificationService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Verify a single record by id.
     *
     * @param id record id to verify
     * @return RecordVerificationResult describing stored and computed hashes and validity
     * @throws RuntimeException if the record is not found
     */
    public RecordVerificationResponse verifyRecord(Long id) {
        AuditRecord record = repository.findById(id).orElseThrow(() -> new RuntimeException("AuditRecord not found: " + id));

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

        String computedHash = HashUtil.sha256Hex(canonical);
        String storedHash = record.getHash();
        boolean valid = computedHash.equals(storedHash);
        String message = valid ? "Record hash matches (valid)" : "Hash mismatch";

        return new RecordVerificationResponse(id, valid, storedHash, computedHash, message);
    }
}
