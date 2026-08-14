package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.specification.AuditRecordSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.audit.controller.AuditSearchResponse;

/**
 * Service providing search functionality for audit records using dynamic Specifications.
 * Supports optional filters and returns a Page of results ordered as requested.
 */
@Service
public class AuditSearchService {

    private final AuditRecordRepository repository;

    public AuditSearchService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Search audit records using optional filters. Any combination of filters may be supplied.
     * If no filters are provided, all records are returned ordered by the pageable's sort.
     *
     * @param actorId      optional actor id
     * @param eventType    optional event type
     * @param resourceType optional resource type
     * @param resourceId   optional resource id
     * @param pageable     paging and sorting information
     * @return page of matching records mapped to AuditSearchResponse DTOs
     */
    public Page<AuditSearchResponse> search(String actorId,
                                            String eventType,
                                            String resourceType,
                                            String resourceId,
                                            Pageable pageable) {
        // Reuse existing specification builder; pass null for timestamp range
        org.springframework.data.jpa.domain.Specification<AuditRecord> spec = AuditRecordSpecification.byFilters(actorId, resourceType, resourceId, eventType, null, null);

        Page<AuditRecord> page = repository.findAll(spec, pageable);

        return page.map(r -> new AuditSearchResponse(
                r.getSequenceNumber(),
                r.getEventType(),
                r.getActorId(),
                r.getResourceType(),
                r.getResourceId(),
                r.getTimestamp(),
                r.getHash()
        ));
    }
}
