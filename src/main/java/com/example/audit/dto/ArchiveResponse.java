package com.example.audit.dto;

/**
 * Response DTO for archive operation.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after running the retention/archive policy")
public class ArchiveResponse {
    @Schema(description = "Number of records archived", example = "5")
    private int archivedRecords;

    @Schema(description = "Operation message", example = "Retention policy executed successfully")
    private String message;

    public ArchiveResponse() {
    }

    public ArchiveResponse(int archivedRecords, String message) {
        this.archivedRecords = archivedRecords;
        this.message = message;
    }

    public int getArchivedRecords() {
        return archivedRecords;
    }

    public void setArchivedRecords(int archivedRecords) {
        this.archivedRecords = archivedRecords;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
