package com.example.audit.dto;

/**
 * Response DTO for chain verification results.
 */
public class ChainVerificationResponse {

    private boolean chainIntact;
    private Long firstBrokenSequence;
    private String violation;
    private String message;

    public ChainVerificationResponse() {
    }

    public ChainVerificationResponse(boolean chainIntact, Long firstBrokenSequence, String violation, String message) {
        this.chainIntact = chainIntact;
        this.firstBrokenSequence = firstBrokenSequence;
        this.violation = violation;
        this.message = message;
    }

    public boolean isChainIntact() {
        return chainIntact;
    }

    public void setChainIntact(boolean chainIntact) {
        this.chainIntact = chainIntact;
    }

    public Long getFirstBrokenSequence() {
        return firstBrokenSequence;
    }

    public void setFirstBrokenSequence(Long firstBrokenSequence) {
        this.firstBrokenSequence = firstBrokenSequence;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
