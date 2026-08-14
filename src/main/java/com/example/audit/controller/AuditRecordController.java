package com.example.audit.controller;

import com.example.audit.dto.AuditRecordRequest;
import com.example.audit.dto.AuditRecordResponse;
import com.example.audit.dto.AuditRecordSearchResponse;
import com.example.audit.entity.AuditRecord;
import com.example.audit.service.AuditRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST controller exposing endpoints to create audit records.
 */
@RestController
@RequestMapping("/audit")
public class AuditRecordController {

    private final AuditRecordService service;

    public AuditRecordController(AuditRecordService service) {
        this.service = service;
    }

    /**
     * Append a new immutable audit record.
     *
     * POST /audit
     *
     * Validates the incoming payload, delegates to AuditRecordService, and
     * returns a 201 Created with a minimal response body.
     *
     * Exception handling and input sanitization are intentionally left for
     * later tasks.
     */
    @PostMapping
    public ResponseEntity<AuditRecordResponse> create(@Valid @RequestBody AuditRecordRequest request) {
        AuditRecord saved = service.appendRecord(
                request.getEventType(),
                request.getActorId(),
                request.getResourceType(),
                request.getResourceId(),
                request.getPayload(),
                request.getTimestamp()
        );

        AuditRecordResponse resp = new AuditRecordResponse(
                saved.getId(),
                saved.getSequenceNumber(),
                saved.getHash(),
                saved.getPreviousHash(),
                saved.getTimestamp()
        );

        // Optionally set Location header to the created resource URI (/audit/{id})
        URI location = URI.create("/audit/" + saved.getId());
        return ResponseEntity.created(location).body(resp);
    }

    /**
     * Search API for audit records with optional filters and pagination.
     * GET /audit
     */
    @org.springframework.web.bind.annotation.GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<AuditRecordSearchResponse>> search(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String actorId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String resourceType,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String resourceId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String eventType,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.Instant from,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.Instant to,
            org.springframework.data.domain.Pageable pageable
    ) {
        org.springframework.data.domain.Page<com.example.audit.entity.AuditRecord> page = service.search(actorId, resourceType, resourceId, eventType, from, to, pageable);
        org.springframework.data.domain.Page<AuditRecordSearchResponse> dtoPage = page.map(r -> new AuditRecordSearchResponse(
                r.getId(),
                r.getSequenceNumber(),
                r.getEventType(),
                r.getActorId(),
                r.getResourceType(),
                r.getResourceId(),
                r.getTimestamp(),
                r.getHash()
        ));
        return ResponseEntity.ok(dtoPage);
    }
}
