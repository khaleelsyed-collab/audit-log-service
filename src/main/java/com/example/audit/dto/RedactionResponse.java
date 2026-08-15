package com.example.audit.dto;

/**
 * Response DTO for redaction operations.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after a redaction operation")
public class RedactionResponse {

    @Schema(description = "Primary identifier of the record", example = "100")
    private Long id;

    @Schema(description = "Redacted payload (stringified JSON)", example = "{\"name\":\"REDACTED\"}")
    private String redactedPayload;

    @Schema(description = "Operation status message", example = "Redaction applied")
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
