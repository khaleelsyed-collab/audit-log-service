package com.example.audit.controller;

import com.example.audit.dto.RecordVerificationResponse;
import com.example.audit.service.AuditRecordVerificationService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller exposing verification for a single audit record.
 */
@RestController
@RequestMapping("/audit/verify")
@Validated
@Tag(name = "Record Verification", description = "Verify individual audit records")
@SecurityRequirement(name = "basicAuth")
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
    @Operation(summary = "Verify an audit record by id", description = "Verify a single audit record's stored hash by recomputing it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/{id}")
    public ResponseEntity<RecordVerificationResponse> verify(@Parameter(description = "Record id", example = "100") @Positive(message = "id must be a positive number") @PathVariable("id") Long id) {
        RecordVerificationResponse resp = verificationService.verifyRecord(id);
        return ResponseEntity.ok(resp);
    }
}
