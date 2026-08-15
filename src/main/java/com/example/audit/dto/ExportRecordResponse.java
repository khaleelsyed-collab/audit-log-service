package com.example.audit.dto;

import java.time.Instant;

/**
 * DTO representing an exportable audit record. Contains the fields necessary
 * for review and independent verification. Intentionally omits internal DB-only
 * fields such as primary key id, archival flags, and redaction metadata.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Exportable representation of an audit record")
public class ExportRecordResponse {

    @Schema(description = "Monotonic sequence number", example = "42")
    private long sequenceNumber;

    @Schema(description = "Type of event", example = "LOGIN")
    private String eventType;

    @Schema(description = "Actor identifier", example = "user-123")
    private String actorId;

    @Schema(description = "Resource type", example = "ACCOUNT")
    private String resourceType;

    @Schema(description = "Resource identifier", example = "account-100")
    private String resourceId;

    @Schema(description = "Event timestamp", example = "2026-08-15T12:00:00Z")
    private Instant timestamp;

    @Schema(description = "Event payload (stringified JSON)", example = "{\"status\":\"ACTIVE\"}")
    private String payload;

    @Schema(description = "Previous record hash", example = "000abc...")
    private String previousHash;

    @Schema(description = "This record's hash", example = "e3b0c442...")
    private String hash;

    public ExportRecordResponse() {
    }

    public ExportRecordResponse(long sequenceNumber, String eventType, String actorId, String resourceType, String resourceId, Instant timestamp, String payload, String previousHash, String hash) {
        this.sequenceNumber = sequenceNumber;
        this.eventType = eventType;
        this.actorId = actorId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.timestamp = timestamp;
        this.payload = payload;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
