package com.example.audit.controller;

import com.example.audit.dto.ArchiveResponse;
import com.example.audit.service.AuditArchiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller exposing retention/archival endpoints.
 */
@RestController
@RequestMapping("/audit")
@Tag(name = "Archive", description = "Retention and archival endpoints")
@SecurityRequirement(name = "basicAuth")
public class AuditArchiveController {

    private final AuditArchiveService archiveService;

    public AuditArchiveController(AuditArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    /**
     * POST /audit/archive - execute retention policy and soft-archive expired records.
     */
    @Operation(summary = "Execute retention policy and archive expired records", description = "Run retention policy to soft-archive expired audit records. Requires ROLE_ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/archive")
    public ResponseEntity<ArchiveResponse> archive() {
        int archived = archiveService.archiveExpiredRecords();
        ArchiveResponse resp = new ArchiveResponse(archived, "Retention policy executed successfully");
        return ResponseEntity.ok(resp);
    }
}
