package com.example.audit.dto;

/**
 * Response DTO for redaction operations.
 */
public class RedactionResponse {

    private Long id;
    private String redactedPayload;
    private String message;

    public RedactionResponse() {
    }

    public RedactionResponse(Long id, String redactedPayload, String message) {
        this.id = id;
        this.redactedPayload = redactedPayload;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedactedPayload() {
        return redactedPayload;
    }

    public void setRedactedPayload(String redactedPayload) {
        this.redactedPayload = redactedPayload;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
