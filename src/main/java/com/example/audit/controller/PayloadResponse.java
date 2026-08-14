package com.example.audit.controller;

/**
 * DTO representing a payload retrieval response. Indicates whether the payload
 * returned is a redacted view.
 */
public class PayloadResponse {

    private Long id;
    private boolean redacted;
    private String payload;

    public PayloadResponse() {
    }

    public PayloadResponse(Long id, boolean redacted, String payload) {
        this.id = id;
        this.redacted = redacted;
        this.payload = payload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRedacted() {
        return redacted;
    }

    public void setRedacted(boolean redacted) {
        this.redacted = redacted;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
