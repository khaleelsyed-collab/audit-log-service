package com.example.audit.controller;

import com.example.audit.dto.ExportBundleResponse;
import com.example.audit.service.AuditExportService;
import jakarta.validation.constraints.Size;
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
 * Controller exposing an export endpoint for audit records.
 */
@RestController
@RequestMapping("/audit")
@Validated
@Tag(name = "Export", description = "Export audit records")
@SecurityRequirement(name = "basicAuth")
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

    @Operation(summary = "Export audit records", description = "Export audit records filtered by actor/resource and packaged with verification metadata.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/export")
    public ResponseEntity<ExportBundleResponse> exportAll(
            @Parameter(description = "Filter by actorId", example = "user-123") @RequestParam(required = false) @Size(max = 128, message = "actorId must be at most 128 characters") String actorId,
            @Parameter(description = "Filter by resourceType", example = "ACCOUNT") @RequestParam(required = false) @Size(max = 128, message = "resourceType must be at most 128 characters") String resourceType,
            @Parameter(description = "Filter by resourceId", example = "account-100") @RequestParam(required = false) @Size(max = 128, message = "resourceId must be at most 128 characters") String resourceId
    ) {

        ExportBundleResponse bundle =
                exportService.export(actorId, resourceType, resourceId);

        return ResponseEntity.ok(bundle);
    }
}
