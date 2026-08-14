package com.example.audit.dto;

import java.time.Instant;

/**
 * DTO for search response results. Does not expose payload.
 */
public class AuditRecordSearchResponse {

    private Long id;
    private long sequenceNumber;
    private String eventType;
    private String actorId;
    private String resourceType;
    private String resourceId;
    private Instant timestamp;
    private String hash;

    public AuditRecordSearchResponse() {
    }

    public AuditRecordSearchResponse(Long id, long sequenceNumber, String eventType, String actorId, String resourceType, String resourceId, Instant timestamp, String hash) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.eventType = eventType;
        this.actorId = actorId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.timestamp = timestamp;
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
