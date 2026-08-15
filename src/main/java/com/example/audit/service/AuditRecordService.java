package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.util.CanonicalRecordUtil;
import com.example.audit.util.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
    private final TransactionTemplate transactionTemplate;
    private final Object appendLock = new Object();

    public AuditRecordService(AuditRecordRepository repository,
                              @Value("${audit.log.genesis-value:audit-log-genesis-v1}") String genesisValue,
                              PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.genesisValue = genesisValue;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Append a new immutable audit record.
     *
     * If timestamp is null, server-received time (Instant.now()) will be used.
     * The method determines the next sequence number and the previous hash
     * value, computes the current content hash, and persists the record.
     *
     * Concurrent appends are serialized so sequence assignment and previousHash
     * linkage remain consistent; the transaction commits while the append lock
     * is still held to avoid lost updates under interleaving threads.
     *
     * @param eventType   type of the event (required)
     * @param actorId     actor identifier (required)
     * @param resourceType resource type (required)
     * @param resourceId  resource identifier (required)
     * @param payload     payload as JSON/text (required)
     * @param timestamp   event timestamp; if null, server time is used
     * @return persisted AuditRecord with assigned id and sequence
     */
    public AuditRecord appendRecord(String eventType,
                                    String actorId,
                                    String resourceType,
                                    String resourceId,
                                    String payload,
                                    Instant timestamp) {
        synchronized (appendLock) {
            return transactionTemplate.execute(status ->
                    doAppendRecord(eventType, actorId, resourceType, resourceId, payload, timestamp));
        }
    }

    private AuditRecord doAppendRecord(String eventType,
                                       String actorId,
                                       String resourceType,
                                       String resourceId,
                                       String payload,
                                       Instant timestamp) {
        Instant effectiveTimestamp =
                (timestamp == null ? Instant.now() : timestamp)
                        .truncatedTo(ChronoUnit.MICROS);

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

    /**
     * Search audit records with optional filters. Uses Specifications to build predicates
     * only for provided filter values.
     *
     * @param actorId      optional actor id filter
     * @param resourceType optional resource type filter
     * @param resourceId   optional resource id filter
     * @param eventType    optional event type filter
     * @param from         optional from timestamp (inclusive)
     * @param to           optional to timestamp (inclusive)
     * @param pageable     paging information
     * @return page of AuditRecord matching filters
     */
    public org.springframework.data.domain.Page<AuditRecord> search(
            String actorId,
            String resourceType,
            String resourceId,
            String eventType,
            java.time.Instant from,
            java.time.Instant to,
            org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<AuditRecord> spec = com.example.audit.specification.AuditRecordSpecification.byFilters(actorId, resourceType, resourceId, eventType, from, to);
        return repository.findAll(spec, pageable);
    }
}
