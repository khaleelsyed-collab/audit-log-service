package com.example.audit.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO representing a self-contained export bundle for a resource or actor.
 */
public class ExportBundleResponse {

    private String actorId;
    private String resourceType;
    private String resourceId;
    private long totalRecords;
    private Long firstSequence;
    private Long lastSequence;
    private String firstHash;
    private String lastHash;
    private String merkleRoot;
    private Instant generatedAt;
    private List<ExportRecordResponse> records;

    public ExportBundleResponse() {
    }

    public ExportBundleResponse(String actorId, String resourceType, String resourceId, long totalRecords, Long firstSequence, Long lastSequence, String firstHash, String lastHash, String merkleRoot, Instant generatedAt, List<ExportRecordResponse> records) {
        this.actorId = actorId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.totalRecords = totalRecords;
        this.firstSequence = firstSequence;
        this.lastSequence = lastSequence;
        this.firstHash = firstHash;
        this.lastHash = lastHash;
        this.merkleRoot = merkleRoot;
        this.generatedAt = generatedAt;
        this.records = records;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Long getFirstSequence() {
        return firstSequence;
    }

    public void setFirstSequence(Long firstSequence) {
        this.firstSequence = firstSequence;
    }

    public Long getLastSequence() {
        return lastSequence;
    }

    public void setLastSequence(Long lastSequence) {
        this.lastSequence = lastSequence;
    }

    public String getFirstHash() {
        return firstHash;
    }

    public void setFirstHash(String firstHash) {
        this.firstHash = firstHash;
    }

    public String getLastHash() {
        return lastHash;
    }

    public void setLastHash(String lastHash) {
        this.lastHash = lastHash;
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

    public List<ExportRecordResponse> getRecords() {
        return records;
    }

    public void setRecords(List<ExportRecordResponse> records) {
        this.records = records;
    }
}
