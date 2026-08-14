package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.springframework.stereotype.Service;

/**
 * Service that provides read-only access to an audit record's payload.
 * It returns either the original payload or the redacted payload (if requested
 * and available) without modifying the record or any hash fields.
 */
@Service
public class AuditPayloadViewService {

    private final AuditRecordRepository repository;

    public AuditPayloadViewService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieve the payload for the given record id.
     *
     * @param id      audit record id
     * @param redacted whether to prefer the redacted payload if available
     * @return payload string (redacted or original)
     * @throws RuntimeException if the record is not found
     */
    public String getPayload(Long id, boolean redacted) {
        AuditRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AuditRecord not found: " + id));

        if (redacted) {
            String rp = record.getRedactedPayload();
            return rp != null ? rp : record.getPayload();
        }
        return record.getPayload();
    }
    public boolean hasRedactedPayload(Long id) {
        AuditRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AuditRecord not found: " + id));

        return record.getRedactedPayload() != null;
    }
}
