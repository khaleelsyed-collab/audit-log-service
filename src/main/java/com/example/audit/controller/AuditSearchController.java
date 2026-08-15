package com.example.audit.controller;

import com.example.audit.dto.AuditSearchResponse;
import com.example.audit.service.AuditSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing search APIs for audit records.
 */
@RestController
@RequestMapping("/audit")
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
    @GetMapping("/search")
    public ResponseEntity<Page<AuditSearchResponse>> search(
            @RequestParam(value = "actorId", required = false) String actorId,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "resourceType", required = false) String resourceType,
            @RequestParam(value = "resourceId", required = false) String resourceId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sequenceNumber"));
        Page<AuditSearchResponse> results = searchService.search(actorId, eventType, resourceType, resourceId, pageable);
        return ResponseEntity.ok(results);
    }
}
