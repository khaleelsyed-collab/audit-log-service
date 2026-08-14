package com.example.audit.dto;

import java.time.Instant;

/**
 * Response DTO for created audit records. Exposes only safe, required fields.
 */
public class AuditRecordResponse {

    private Long id;
    private long sequenceNumber;
    private String hash;
    private String previousHash;
    private Instant timestamp;

    public AuditRecordResponse() {
    }

    public AuditRecordResponse(Long id, long sequenceNumber, String hash, String previousHash, Instant timestamp) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
