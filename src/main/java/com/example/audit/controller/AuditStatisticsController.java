package com.example.audit.controller;

import com.example.audit.service.AuditStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing audit statistics endpoints.
 */
@RestController
@RequestMapping("/audit")
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
    @GetMapping("/stats")
    public ResponseEntity<AuditStatisticsResponse> stats() {
        AuditStatisticsResponse resp = statisticsService.getStatistics();
        return ResponseEntity.ok(resp);
    }
}
