package com.example.audit.controller;

import com.example.audit.dto.ChainVerificationResponse;
import com.example.audit.service.AuditVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller exposing chain verification endpoint.
 */
@RestController
@RequestMapping("/audit")
@Validated
@Tag(name = "Audit Verification", description = "Chain verification endpoints")
@SecurityRequirement(name = "basicAuth")
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
    @Operation(summary = "Verify the audit chain")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/verify")
    public ResponseEntity<ChainVerificationResponse> verify() {
        ChainVerificationResponse resp = verificationService.verifyChain();
        return ResponseEntity.ok(resp);
    }
}
