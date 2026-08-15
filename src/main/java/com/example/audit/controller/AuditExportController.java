package com.example.audit.controller;

import com.example.audit.dto.ExportBundleResponse;
import com.example.audit.service.AuditExportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing an export endpoint for audit records.
 */
@RestController
@RequestMapping("/audit")
public class AuditExportController {

    private final AuditExportService exportService;

    public AuditExportController(AuditExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * GET /audit/export
     *
     * Returns all audit records ordered by sequenceNumber as export-friendly DTOs.
     * This endpoint is read-only and does not modify any data.
     */

    @GetMapping("/export")
    public ResponseEntity<ExportBundleResponse> exportAll(
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId
    ) {

        ExportBundleResponse bundle =
                exportService.export(actorId, resourceType, resourceId);

        return ResponseEntity.ok(bundle);
    }
}
