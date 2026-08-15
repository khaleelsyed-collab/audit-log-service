package com.example.audit.controller;

import com.example.audit.dto.RedactionRequest;
import com.example.audit.dto.RedactionResponse;
import com.example.audit.service.AuditRedactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing redaction API for audit records.
 */
@RestController
@RequestMapping("/audit")
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
    @PostMapping("/redact/{id}")
    public ResponseEntity<RedactionResponse> redact(@PathVariable("id") Long id, @Valid @RequestBody RedactionRequest request) {
        String redacted = redactionService.redactFields(id, request.getFields());
        RedactionResponse resp = new RedactionResponse(id, redacted, "Redaction applied");
        return ResponseEntity.ok(resp);
    }
}
