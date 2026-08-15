

package com.example.audit.dto;

/**
 * DTO describing the result of verifying a single audit record.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of verifying a single audit record")
public class RecordVerificationResponse {

    @Schema(description = "Primary identifier of the record", example = "100")
    private Long id;

    @Schema(description = "Whether the stored hash matches the computed hash", example = "true")
    private boolean valid;

    @Schema(description = "Hash stored in the DB", example = "e3b0c442...")
    private String storedHash;

    @Schema(description = "Hash recomputed from stored fields", example = "e3b0c442...")
    private String computedHash;

    @Schema(description = "Descriptive message", example = "Record is valid")
    private String message;

    public RecordVerificationResponse() {
    }

    public RecordVerificationResponse(Long id, boolean valid, String storedHash, String computedHash, String message) {
        this.id = id;
        this.valid = valid;
        this.storedHash = storedHash;
        this.computedHash = computedHash;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getStoredHash() {
        return storedHash;
    }

    public void setStoredHash(String storedHash) {
        this.storedHash = storedHash;
    }

    public String getComputedHash() {
        return computedHash;
    }

    public void setComputedHash(String computedHash) {
        this.computedHash = computedHash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
