package com.example.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Immutable audit record stored in the audit chain.
 */
@Entity
@Table(name = "audit_records")
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String actorId;

    @Column(nullable = false)
    private String resourceType;

    @Column(nullable = false)
    private String resourceId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false,unique = true)
    private Long sequenceNumber;

    @Column(nullable = false, length = 64)
    private String previousHash;

    @Column(nullable = false, length = 64)
    private String hash;

    // Protected no-arg constructor required by JPA; visibility keeps entity effectively immutable for callers
    protected AuditRecord() {
    }

    // Full constructor for creating new records (id assigned by JPA)
    public AuditRecord(String eventType,
                       String actorId,
                       String resourceType,
                       String resourceId,
                       String payload,
                       Instant timestamp,
                       long sequenceNumber,
                       String previousHash,
                       String hash) {
        this.eventType = eventType;
        this.actorId = actorId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.payload = payload;
        this.timestamp = timestamp;
        this.sequenceNumber = sequenceNumber;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    // Getters only to maintain immutability from API/service callers
    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActorId() {
        return actorId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }
}
