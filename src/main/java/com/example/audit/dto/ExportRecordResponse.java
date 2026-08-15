package com.example.audit.dto;

import java.time.Instant;

/**
 * DTO representing an exportable audit record. Contains the fields necessary
 * for review and independent verification. Intentionally omits internal DB-only
 * fields such as primary key id, archival flags, and redaction metadata.
 */
public class ExportRecordResponse {

    private long sequenceNumber;
    private String eventType;
    private String actorId;
    private String resourceType;
    private String resourceId;
    private Instant timestamp;
    private String payload;
    private String previousHash;
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
