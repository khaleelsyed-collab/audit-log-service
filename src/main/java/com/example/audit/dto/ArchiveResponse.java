package com.example.audit.dto;

/**
 * Response DTO for archive operation.
 */
public class ArchiveResponse {
    private int archivedRecords;
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
