package com.example.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * DTO for incoming audit record creation requests.
 * Validation annotations enforce required fields at the API boundary.
 */
public class AuditRecordRequest {

    @NotBlank
    private String eventType;

    @NotBlank
    private String actorId;

    @NotBlank
    private String resourceType;

    @NotBlank
    private String resourceId;

    @NotBlank
    private String payload;

    // Optional: caller-supplied timestamp. If null, server will assign receive-time.
    private Instant timestamp;

    public AuditRecordRequest() {
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

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
