package com.example.audit.controller;

import com.example.audit.dto.AuditSearchResponse;
import com.example.audit.service.AuditSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
 * Controller exposing search APIs for audit records.
 */
@RestController
@RequestMapping("/audit")
@Validated
@Tag(name = "Audit Search", description = "Search and export audit records")
@SecurityRequirement(name = "basicAuth")
public class AuditSearchController {

    private final AuditSearchService searchService;

    public AuditSearchController(AuditSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /audit/search
     *
     * Supports optional filters: actorId, eventType, resourceType, resourceId.
     * Supports pagination via page and size query parameters. Results are always
     * ordered by sequenceNumber ascending.
     */
    @Operation(summary = "Search audit records")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'SYSTEM')")
    @GetMapping("/search")
    public ResponseEntity<Page<AuditSearchResponse>> search(
            @RequestParam(value = "actorId", required = false) @Size(max = 128, message = "actorId must be at most 128 characters") String actorId,
            @RequestParam(value = "eventType", required = false) @Size(max = 128, message = "eventType must be at most 128 characters") String eventType,
            @RequestParam(value = "resourceType", required = false) @Size(max = 128, message = "resourceType must be at most 128 characters") String resourceType,
            @RequestParam(value = "resourceId", required = false) @Size(max = 128, message = "resourceId must be at most 128 characters") String resourceId,
            @RequestParam(value = "page", required = false, defaultValue = "0") @Min(value = 0, message = "page must be greater than or equal to 0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Min(value = 1, message = "size must be at least 1") @Max(value = 200, message = "size must be at most 200") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sequenceNumber"));
        Page<AuditSearchResponse> results = searchService.search(actorId, eventType, resourceType, resourceId, pageable);
        return ResponseEntity.ok(results);
    }
}
