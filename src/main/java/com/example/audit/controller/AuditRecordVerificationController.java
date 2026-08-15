package com.example.audit.controller;

import com.example.audit.dto.RecordVerificationResponse;
import com.example.audit.service.AuditRecordVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing verification for a single audit record.
 */
@RestController
@RequestMapping("/audit/verify")
public class AuditRecordVerificationController {

    private final AuditRecordVerificationService verificationService;

    public AuditRecordVerificationController(AuditRecordVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * GET /audit/verify/{id}
     *
     * Verify a single audit record's stored hash by recomputing it.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecordVerificationResponse> verify(@PathVariable("id") Long id) {
        RecordVerificationResponse resp = verificationService.verifyRecord(id);
        return ResponseEntity.ok(resp);
    }
}
