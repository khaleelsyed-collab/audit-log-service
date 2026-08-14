

package com.example.audit.controller;

/**
 * DTO describing the result of verifying a single audit record.
 */
public class RecordVerificationResponse {

    private Long id;
    private boolean valid;
    private String storedHash;
    private String computedHash;
    private String message;

    public RecordVerificationResponse() {
    }

    public RecordVerificationResponse(Long id, boolean valid, String storedHash, String computedHash, String message) {
        this.id = id;
        this.valid = valid;
        this.storedHash = storedHash;
        this.computedHash = computedHash;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getStoredHash() {
        return storedHash;
    }

    public void setStoredHash(String storedHash) {
        this.storedHash = storedHash;
    }

    public String getComputedHash() {
        return computedHash;
    }

    public void setComputedHash(String computedHash) {
        this.computedHash = computedHash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
