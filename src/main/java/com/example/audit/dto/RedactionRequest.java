package com.example.audit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request DTO specifying top-level fields to redact from a record's payload.
 */
public class RedactionRequest {

    @NotNull
    @NotEmpty
    private List<String> fields;

    public RedactionRequest() {
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
