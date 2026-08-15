package com.example.audit.controller;

import com.example.audit.dto.AuditRecordRequest;
import com.example.audit.dto.AuditRecordResponse;
import com.example.audit.dto.AuditRecordSearchResponse;
import com.example.audit.entity.AuditRecord;
import com.example.audit.service.AuditRecordService;
import jakarta.validation.Valid;

import java.net.URI;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'SYSTEM')")
    @GetMapping
    public ResponseEntity<Page<AuditRecordSearchResponse>> search(
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            Pageable pageable
    ) {
        Page<AuditRecord> page = service.search(
                actorId,
                resourceType,
                resourceId,
                eventType,
                from,
                to,
                pageable
        );

        Page<AuditRecordSearchResponse> dtoPage = page.map(r ->
                new AuditRecordSearchResponse(
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
