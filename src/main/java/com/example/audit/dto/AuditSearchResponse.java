package com.example.audit.dto;

import java.time.Instant;

/**
 * DTO for audit search results. Does not expose payload or internal metadata.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search result entry for an audit record")
public class AuditSearchResponse {

    @Schema(description = "Monotonic sequence number assigned to the record", example = "42")
    private long sequenceNumber;

    @Schema(description = "Type of event", example = "LOGIN")
    private String eventType;

    @Schema(description = "Actor identifier", example = "user-123")
    private String actorId;

    @Schema(description = "Resource type", example = "ACCOUNT")
    private String resourceType;

    @Schema(description = "Resource identifier", example = "account-100")
    private String resourceId;

    @Schema(description = "Timestamp of the event", example = "2026-08-15T12:00:00Z")
    private Instant timestamp;

    @Schema(description = "Hash of the record", example = "e3b0c442...")
    private String hash;

    public AuditSearchResponse() {
    }

    public AuditSearchResponse(long sequenceNumber, String eventType, String actorId, String resourceType, String resourceId, Instant timestamp, String hash) {
        this.sequenceNumber = sequenceNumber;
        this.eventType = eventType;
        this.actorId = actorId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.timestamp = timestamp;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
