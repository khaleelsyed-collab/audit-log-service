package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.specification.AuditRecordSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.audit.controller.AuditSearchResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Service providing search functionality for audit records using dynamic Specifications.
 * Supports optional filters and always returns results ordered by sequenceNumber ascending.
 */
@Service
public class AuditSearchService {

    private final AuditRecordRepository repository;

    public AuditSearchService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Search audit records using optional filters. Any combination of filters may be supplied.
     * If no filters are provided, all records are returned ordered by sequenceNumber ascending.
     *
     * @param actorId      optional actor id
     * @param eventType    optional event type
     * @param resourceType optional resource type
     * @param resourceId   optional resource id
     * @return list of matching records mapped to AuditSearchResponse DTOs
     */
    public List<AuditSearchResponse> search(String actorId,
                                                                         String eventType,
                                                                         String resourceType,
                                                                         String resourceId) {
        // Reuse existing specification builder; pass null for timestamp range
        org.springframework.data.jpa.domain.Specification<AuditRecord> spec = AuditRecordSpecification.byFilters(actorId, resourceType, resourceId, eventType, null, null);

        List<AuditRecord> records = repository.findAll(spec, Sort.by(Sort.Direction.ASC, "sequenceNumber"));

        List<AuditSearchResponse> out = new ArrayList<>(records.size());
        for (AuditRecord r : records) {
            out.add(new AuditSearchResponse(
                    r.getSequenceNumber(),
                    r.getEventType(),
                    r.getActorId(),
                    r.getResourceType(),
                    r.getResourceId(),
                    r.getTimestamp(),
                    r.getHash()
            ));
        }
        return out;
    }
}
