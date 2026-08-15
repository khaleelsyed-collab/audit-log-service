package com.example.audit.controller;

import com.example.audit.dto.PayloadResponse;
import com.example.audit.service.AuditPayloadViewService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * Controller that exposes read-only payload retrieval APIs for audit records.
 */
@RestController
@RequestMapping("/audit")
@Validated
@Tag(name = "Payload", description = "Retrieve audit record payloads")
@SecurityRequirement(name = "basicAuth")
public class AuditPayloadController {

    private final AuditPayloadViewService payloadViewService;

    public AuditPayloadController(AuditPayloadViewService payloadViewService) {
        this.payloadViewService = payloadViewService;
    }

    /**
     * GET /audit/{id}/payload
     *
     * @param id      record id
     * @param redacted optional query param; if true, return redacted payload if available
     * @return PayloadResponse with the selected payload
     */
    @Operation(summary = "Get payload for an audit record", description = "Retrieve the original or redacted payload for a given audit record id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/{id}/payload")
    public ResponseEntity<PayloadResponse> getPayload(
            @Parameter(description = "Record id", example = "100") @Positive(message = "id must be a positive number") @PathVariable("id") Long id,
            @Parameter(description = "Return redacted payload if available", example = "false") @RequestParam(value = "redacted", required = false, defaultValue = "false") boolean redacted
    ) {
        String payload = payloadViewService.getPayload(id, redacted);

        boolean suppliedRedacted =
                redacted && payloadViewService.hasRedactedPayload(id);

        PayloadResponse response =
                new PayloadResponse(id, suppliedRedacted, payload);

        return ResponseEntity.ok(response);
    }
}
