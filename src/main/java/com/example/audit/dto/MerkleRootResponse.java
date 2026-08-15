package com.example.audit.dto;

import java.time.Instant;

/**
 * DTO representing the Merkle root computation result.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Merkle root computation result summary")
public class MerkleRootResponse {

    @Schema(description = "Total number of records considered", example = "123")
    private int totalRecords;

    @Schema(description = "Computed Merkle root (hex)", example = "a1b2c3d4...")
    private String merkleRoot;

    @Schema(description = "Timestamp when the root was generated", example = "2026-08-15T12:00:00Z")
    private Instant generatedAt;

    public MerkleRootResponse() {
    }

    public MerkleRootResponse(int totalRecords, String merkleRoot, Instant generatedAt) {
        this.totalRecords = totalRecords;
        this.merkleRoot = merkleRoot;
        this.generatedAt = generatedAt;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
