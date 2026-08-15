package com.example.audit.controller;

import com.example.audit.dto.RedactionRequest;
import com.example.audit.dto.RedactionResponse;
import com.example.audit.service.AuditRedactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller exposing redaction API for audit records.
 */
@RestController
@RequestMapping("/audit")
@Validated
@Tag(name = "Redaction", description = "Redaction operations for audit records")
@SecurityRequirement(name = "basicAuth")
public class AuditRedactionController {

    private final AuditRedactionService redactionService;

    public AuditRedactionController(AuditRedactionService redactionService) {
        this.redactionService = redactionService;
    }

    /**
     * POST /audit/redact/{id}
     *
     * Redact specified top-level fields and store the result in redactedPayload.
     */
    @Operation(summary = "Redact fields from an audit record")
    @RequestBody(description = "Fields to redact", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/redact/{id}")
        public ResponseEntity<RedactionResponse> redact(@Parameter(description = "Record id", example = "100") @Positive(message = "id must be a positive number") @PathVariable("id") Long id, @Valid @org.springframework.web.bind.annotation.RequestBody RedactionRequest request) {
        String redacted = redactionService.redactFields(id, request.getFields());
        RedactionResponse resp = new RedactionResponse(id, redacted, "Redaction applied");
        return ResponseEntity.ok(resp);
    }
}
