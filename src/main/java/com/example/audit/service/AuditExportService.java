package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.springframework.stereotype.Service;
import com.example.audit.controller.ExportRecordResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service providing read-only export of audit records.
 */
@Service
public class AuditExportService {

    private final AuditRecordRepository repository;

    public AuditExportService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Export all audit records ordered by sequenceNumber ascending.
     * Returns a list of DTOs suitable for external consumption. Does not
     * modify any stored records.
     *
     * @return list of export-ready audit record DTOs
     */
    public List<ExportRecordResponse> exportAll() {
        List<AuditRecord> records = repository.findAllByOrderBySequenceNumberAsc();
        List<ExportRecordResponse> out = new ArrayList<>(records.size());
        for (AuditRecord r : records) {
            ExportRecordResponse dto = new ExportRecordResponse(
                    r.getSequenceNumber(),
                    r.getEventType(),
                    r.getActorId(),
                    r.getResourceType(),
                    r.getResourceId(),
                    r.getTimestamp(),
                    r.getPayload(),
                    r.getPreviousHash(),
                    r.getHash()
            );
            out.add(dto);
        }
        return out;
    }
}
