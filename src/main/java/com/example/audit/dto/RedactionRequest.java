package com.example.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO specifying top-level fields to redact from a record's payload.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request listing fields to redact from an audit record payload")
public class RedactionRequest {

    @Schema(description = "Top-level JSON fields to redact", example = "[\"email\", \"ssn\"]")
    @NotNull(message = "fields is required")
    @NotEmpty(message = "fields must not be empty")
    @Size(min = 1, max = 20, message = "fields must contain between 1 and 20 entries")
    private List<@NotBlank(message = "field names must not be blank") @Size(max = 64, message = "field names must be at most 64 characters") String> fields;

    public RedactionRequest() {
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
