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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * REST controller exposing endpoints to create audit records.
 */
@RestController
@RequestMapping("/audit")
@Validated
@Tag(name = "Audit Records", description = "Create and query audit records")
@SecurityRequirement(name = "basicAuth")
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
     */
    @Operation(summary = "Create an audit record", description = "Append a new immutable audit record. Requires ROLE_ADMIN or ROLE_SYSTEM.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Audit record payload", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Audit record created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to create audit records")
    })
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
    @Operation(summary = "Search audit records", description = "Search audit records with optional filters and pagination. Results sorted by sequenceNumber ascending.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'SYSTEM')")
    @GetMapping
    public ResponseEntity<Page<AuditRecordSearchResponse>> search(
            @Parameter(description = "Filter by actorId", example = "user-123") @RequestParam(required = false) String actorId,
            @Parameter(description = "Filter by resourceType", example = "ACCOUNT") @RequestParam(required = false) String resourceType,
            @Parameter(description = "Filter by resourceId", example = "account-100") @RequestParam(required = false) String resourceId,
            @Parameter(description = "Filter by eventType", example = "LOGIN") @RequestParam(required = false) String eventType,
            @Parameter(description = "Start time (ISO-8601)", example = "2026-08-15T00:00:00Z") @RequestParam(required = false) Instant from,
            @Parameter(description = "End time (ISO-8601)", example = "2026-08-15T23:59:59Z") @RequestParam(required = false) Instant to,
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
