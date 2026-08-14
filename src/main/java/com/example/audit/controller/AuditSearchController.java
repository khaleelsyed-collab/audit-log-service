package com.example.audit.controller;

import com.example.audit.service.AuditSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * Returns results ordered by sequenceNumber ascending.
     */
    @GetMapping("/search")
    public ResponseEntity<List<AuditSearchResponse>> search(
            @RequestParam(value = "actorId", required = false) String actorId,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "resourceType", required = false) String resourceType,
            @RequestParam(value = "resourceId", required = false) String resourceId
    ) {
        List<AuditSearchResponse> results = searchService.search(actorId, eventType, resourceType, resourceId);
        return ResponseEntity.ok(results);
    }
}
