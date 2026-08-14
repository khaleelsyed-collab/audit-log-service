package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service to perform retention-based soft archival of audit records.
 *
 * This service marks records older than the configured retention window as archived
 * by setting the archived flag and archivedAt timestamp. Records are not deleted.
 */
@Service
public class AuditArchiveService {

    private final AuditRecordRepository repository;
    private final long retentionDays;

    public AuditArchiveService(AuditRecordRepository repository,
                               @Value("${audit.retention.days:365}") long retentionDays) {
        this.repository = repository;
        this.retentionDays = retentionDays;
    }

    /**
     * Archive records older than the retention cutoff. This method does not physically
     * delete records; it marks them archived and sets archivedAt.
     *
     * @return number of records archived
     */
    @Transactional
    public int archiveExpiredRecords() {
        // Records older than the configured retention period are eligible for archival.
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        List<AuditRecord> expired = repository.findByTimestampLessThanEqualAndArchivedFalse(cutoff);
        if (expired.isEmpty()) {
            return 0;
        }
        Instant now = Instant.now();

        // Soft archive records without modifying any hash-chain fields.
        for (AuditRecord r : expired) {
            r.setArchived(true);
            r.setArchivedAt(now);
        }

        return expired.size();
    }
}
