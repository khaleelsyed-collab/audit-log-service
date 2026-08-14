package com.example.audit.controller;

import java.time.Instant;

/**
 * DTO containing audit statistics summary.
 */
public class AuditStatisticsResponse {

    private long totalRecords;
    private long activeRecords;
    private long archivedRecords;
    private Long latestSequenceNumber;
    private Instant firstRecordTimestamp;
    private Instant lastRecordTimestamp;

    public AuditStatisticsResponse() {
    }

    public AuditStatisticsResponse(long totalRecords, long activeRecords, long archivedRecords, Long latestSequenceNumber, Instant firstRecordTimestamp, Instant lastRecordTimestamp) {
        this.totalRecords = totalRecords;
        this.activeRecords = activeRecords;
        this.archivedRecords = archivedRecords;
        this.latestSequenceNumber = latestSequenceNumber;
        this.firstRecordTimestamp = firstRecordTimestamp;
        this.lastRecordTimestamp = lastRecordTimestamp;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public long getActiveRecords() {
        return activeRecords;
    }

    public void setActiveRecords(long activeRecords) {
        this.activeRecords = activeRecords;
    }

    public long getArchivedRecords() {
        return archivedRecords;
    }

    public void setArchivedRecords(long archivedRecords) {
        this.archivedRecords = archivedRecords;
    }

    public Long getLatestSequenceNumber() {
        return latestSequenceNumber;
    }

    public void setLatestSequenceNumber(Long latestSequenceNumber) {
        this.latestSequenceNumber = latestSequenceNumber;
    }

    public Instant getFirstRecordTimestamp() {
        return firstRecordTimestamp;
    }

    public void setFirstRecordTimestamp(Instant firstRecordTimestamp) {
        this.firstRecordTimestamp = firstRecordTimestamp;
    }

    public Instant getLastRecordTimestamp() {
        return lastRecordTimestamp;
    }

    public void setLastRecordTimestamp(Instant lastRecordTimestamp) {
        this.lastRecordTimestamp = lastRecordTimestamp;
    }
}
