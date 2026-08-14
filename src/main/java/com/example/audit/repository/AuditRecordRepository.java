package com.example.audit.repository;

import com.example.audit.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AuditRecord persistence operations.
 *
 * Minimal, focused API to support appending new records, full-chain verification,
 * and simple exports/queries. More flexible filtering can be implemented via
 * JpaSpecificationExecutor in the service layer as needed.
 */
public interface AuditRecordRepository
        extends JpaRepository<AuditRecord, Long>, JpaSpecificationExecutor<AuditRecord> {

    /**
     * Find the latest persisted record by sequenceNumber.
     * Used when assigning the next sequence number for a new append.
     */
    Optional<AuditRecord> findTopByOrderBySequenceNumberDesc();

    /**
     * Retrieve all records ordered by sequenceNumber ascending for full-chain verification.
     * Returning a List is intentional; callers may stream or page as needed.
     */
    List<AuditRecord> findAllByOrderBySequenceNumberAsc();

    /**
     * Find records with timestamp <= cutoff and not yet archived.
     */
    List<AuditRecord> findByTimestampLessThanEqualAndArchivedFalse(java.time.Instant cutoff);

    /**
     * Count of non-archived records.
     */
    long countByArchivedFalse();

    /**
     * Count of archived records.
     */
    long countByArchivedTrue();

    /**
     * Find the first (earliest) record by sequence number.
     */
    java.util.Optional<AuditRecord> findTopByOrderBySequenceNumberAsc();

    /**
     * Retrieve all records for a given actor ordered by sequenceNumber ascending.
     * Useful for actor-scoped exports or verification across a single actor's events.
     */
  //  List<AuditRecord> findByActorIdOrderBySequenceNumberAsc(String actorId);

    /**
     * Retrieve all records for a specific resource (type+id) ordered by sequenceNumber ascending.
     * Useful for resource-scoped exports or verification.
     */
  //  List<AuditRecord> findByResourceTypeAndResourceIdOrderBySequenceNumberAsc(String resourceType, String resourceId);
}
