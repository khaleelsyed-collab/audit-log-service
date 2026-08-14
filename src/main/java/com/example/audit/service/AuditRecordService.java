package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.util.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import com.example.audit.util.CanonicalRecordUtil;

/**
 * Service responsible for appending immutable audit records.
 *
 * Responsibilities:
 * - Determine next sequence number
 * - Determine previous hash (or use genesis)
 * - Compute current record hash using HashUtil
 * - Persist the new AuditRecord within a transaction
 *
 * This service intentionally exposes a minimal API to support future REST
 * integration. No update/delete operations are provided.
 */
@Service
public class AuditRecordService {

    private final AuditRecordRepository repository;
    private final String genesisValue;

    public AuditRecordService(AuditRecordRepository repository,
                              @Value("${audit.log.genesis-value:audit-log-genesis-v1}") String genesisValue) {
        this.repository = repository;
        this.genesisValue = genesisValue;
    }

    /**
     * Append a new immutable audit record.
     *
     * If timestamp is null, server-received time (Instant.now()) will be used.
     * The method determines the next sequence number and the previous hash
     * value, computes the current content hash, and persists the record.
     *
     * @param eventType   type of the event (required)
     * @param actorId     actor identifier (required)
     * @param resourceType resource type (required)
     * @param resourceId  resource identifier (required)
     * @param payload     payload as JSON/text (required)
     * @param timestamp   event timestamp; if null, server time is used
     * @return persisted AuditRecord with assigned id and sequence
     */
    @Transactional
    public AuditRecord appendRecord(String eventType,
                                    String actorId,
                                    String resourceType,
                                    String resourceId,
                                    String payload,
                                    Instant timestamp) {
        Instant effectiveTimestamp = (timestamp == null) ? Instant.now() : timestamp;

        // Determine next sequence number and previous hash atomically (within transaction)
        Optional<AuditRecord> latestOpt = repository.findTopByOrderBySequenceNumberDesc();
        long nextSequence = latestOpt.map(r -> r.getSequenceNumber() + 1).orElse(1L);
        String previousHash = latestOpt.map(AuditRecord::getHash).orElse(genesisValue);

        // Create a deterministic canonical representation for hashing.
        // Note: higher-level canonicalization (consistent JSON ordering) should be applied by callers
        // before passing payload; here we delegate to CanonicalRecordUtil for a fixed-format string.
        String canonical = CanonicalRecordUtil.buildCanonicalString(eventType, actorId, resourceType, resourceId, payload, effectiveTimestamp, nextSequence, previousHash);

        String currentHash = HashUtil.sha256Hex(canonical);

        AuditRecord record = new AuditRecord(
                eventType,
                actorId,
                resourceType,
                resourceId,
                payload,
                effectiveTimestamp,
                nextSequence,
                previousHash,
                currentHash
        );

        return repository.save(record);
    }
}
