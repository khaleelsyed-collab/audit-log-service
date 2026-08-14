package com.example.audit.controller;

import com.example.audit.service.AuditArchiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing retention/archival endpoints.
 */
@RestController
@RequestMapping("/audit")
public class AuditArchiveController {

    private final AuditArchiveService archiveService;

    public AuditArchiveController(AuditArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    /**
     * POST /audit/archive - execute retention policy and soft-archive expired records.
     */
    @PostMapping("/archive")
    public ResponseEntity<ArchiveResponse> archive() {
        int archived = archiveService.archiveExpiredRecords();
        ArchiveResponse resp = new ArchiveResponse(archived, "Retention policy executed successfully");
        return ResponseEntity.ok(resp);
    }
}
