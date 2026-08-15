package com.example.audit.controller;

import com.example.audit.dto.ChainVerificationResponse;
import com.example.audit.service.AuditVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing chain verification endpoint.
 */
@RestController
@RequestMapping("/audit")
@Validated
public class AuditVerificationController {

    private final AuditVerificationService verificationService;

    public AuditVerificationController(AuditVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * GET /audit/verify
     *
     * Delegates chain verification to AuditVerificationService and returns
     * a ChainVerificationResponse describing the result.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/verify")
    public ResponseEntity<ChainVerificationResponse> verify() {
        ChainVerificationResponse resp = verificationService.verifyChain();
        return ResponseEntity.ok(resp);
    }
}
