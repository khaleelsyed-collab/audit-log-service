package com.example.audit.dto;

import java.time.Instant;

/**
 * Response DTO for created audit records. Exposes only safe, required fields.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after creating an audit record")
public class AuditRecordResponse {

    @Schema(description = "Primary identifier of the record", example = "100")
    private Long id;

    @Schema(description = "Monotonic sequence number assigned to the record", example = "42")
    private long sequenceNumber;

    @Schema(description = "SHA-256 hash of the record contents", example = "e3b0c44298fc1c149afbf4c8996fb924...")
    private String hash;

    @Schema(description = "Previous record hash in the chain", example = "000000...")
    private String previousHash;

    @Schema(description = "Server timestamp when record was appended", example = "2026-08-15T12:00:00Z")
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
