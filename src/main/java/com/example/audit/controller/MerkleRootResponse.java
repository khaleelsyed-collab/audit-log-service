package com.example.audit.controller;

import java.time.Instant;

/**
 * DTO representing the Merkle root computation result.
 */
public class MerkleRootResponse {

    private int totalRecords;
    private String merkleRoot;
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
