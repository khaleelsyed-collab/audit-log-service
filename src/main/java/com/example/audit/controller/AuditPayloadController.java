package com.example.audit.controller;

import com.example.audit.dto.PayloadResponse;
import com.example.audit.service.AuditPayloadViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that exposes read-only payload retrieval APIs for audit records.
 */
@RestController
@RequestMapping("/audit")
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
    @GetMapping("/{id}/payload")
    public ResponseEntity<PayloadResponse> getPayload(
            @PathVariable("id") Long id,
            @RequestParam(value = "redacted", required = false, defaultValue = "false") boolean redacted
    ) {
        String payload = payloadViewService.getPayload(id, redacted);

        boolean suppliedRedacted =
                redacted && payloadViewService.hasRedactedPayload(id);

        PayloadResponse response =
                new PayloadResponse(id, suppliedRedacted, payload);

        return ResponseEntity.ok(response);
    }
}
