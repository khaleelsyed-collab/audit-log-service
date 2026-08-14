package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service providing read-only statistics about stored audit records.
 */
@Service
public class AuditStatisticsService {

    private final AuditRecordRepository repository;

    public AuditStatisticsService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Compute current audit statistics.
     *
     * @return AuditStatisticsResponse DTO with aggregated values
     */
    public com.example.audit.controller.AuditStatisticsResponse getStatistics() {
        long total = repository.count();
        long active = repository.countByArchivedFalse();
        long archived = repository.countByArchivedTrue();

        Optional<AuditRecord> latestOpt = repository.findTopByOrderBySequenceNumberDesc();
        Optional<AuditRecord> firstOpt = repository.findTopByOrderBySequenceNumberAsc();

        Long latestSeq = latestOpt.map(AuditRecord::getSequenceNumber).orElse(null);
        java.time.Instant firstTs = firstOpt.map(AuditRecord::getTimestamp).orElse(null);
        java.time.Instant lastTs = latestOpt.map(AuditRecord::getTimestamp).orElse(null);

        return new com.example.audit.controller.AuditStatisticsResponse(total, active, archived, latestSeq, firstTs, lastTs);
    }
}
