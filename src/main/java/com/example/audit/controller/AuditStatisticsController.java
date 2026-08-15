package com.example.audit.controller;

import com.example.audit.service.AuditStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller exposing audit statistics endpoints.
 */
@RestController
@RequestMapping("/audit")
@Tag(name = "Statistics", description = "Audit statistics endpoints")
@SecurityRequirement(name = "basicAuth")
public class AuditStatisticsController {

    private final AuditStatisticsService statisticsService;

    public AuditStatisticsController(AuditStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * GET /audit/stats
     *
     * Returns aggregated statistics about audit records.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/stats")
    public ResponseEntity<AuditStatisticsResponse> stats() {
        AuditStatisticsResponse resp = statisticsService.getStatistics();
        return ResponseEntity.ok(resp);
    }
}
