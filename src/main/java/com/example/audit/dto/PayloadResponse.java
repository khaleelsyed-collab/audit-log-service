package com.example.audit.dto;

/**
 * DTO representing a payload retrieval response. Indicates whether the payload
 * returned is a redacted view.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload returned for a given audit record")
public class PayloadResponse {

    @Schema(description = "Primary identifier of the record", example = "100")
    private Long id;

    @Schema(description = "Whether the returned payload is redacted", example = "false")
    private boolean redacted;

    @Schema(description = "Record payload (stringified JSON)", example = "{\"status\":\"ACTIVE\"}")
    private String payload;

    public PayloadResponse() {
    }

    public PayloadResponse(Long id, boolean redacted, String payload) {
        this.id = id;
        this.redacted = redacted;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRedacted() {
        return redacted;
    }

    public void setRedacted(boolean redacted) {
        this.redacted = redacted;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
