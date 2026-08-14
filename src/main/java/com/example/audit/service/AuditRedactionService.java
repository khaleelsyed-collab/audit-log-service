package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Service responsible for applying structured redaction to audit record payloads.
 *
 * This service does NOT modify the original payload, the record hash, or previousHash.
 * Instead it stores a separate redactedPayload field on the record containing the
 * masked JSON for safe consumption.
 */
@Service
public class AuditRedactionService {

    private final AuditRecordRepository repository;
    private final ObjectMapper objectMapper;
    private static final String MASK = "********";

    public AuditRedactionService(AuditRecordRepository repository,ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Redact the specified top-level fields for the record with the given id.
     * Only top-level JSON properties are masked; nested properties are not traversed.
     * The original payload, hash, and previousHash are left unchanged.
     *
     * @param id     audit record id to redact
     * @param fields list of top-level fields to mask
     * @return the redacted JSON string stored on the record
     * @throws RuntimeException if the record is not found or payload is invalid JSON
     */
    @Transactional
    public String redactFields(Long id, List<String> fields) {
        AuditRecord record = repository.findById(id).orElseThrow(() -> new RuntimeException("AuditRecord not found: " + id));

        String payload = record.getPayload();
        if (payload == null) {
            throw new RuntimeException("AuditRecord payload is null for id: " + id);
        }

        try {
            JsonNode node = objectMapper.readTree(payload);
            if (!node.isObject()) {
                throw new RuntimeException("AuditRecord payload is not a JSON object: " + id);
            }
            ObjectNode obj = (ObjectNode) node;

            for (String f : fields) {
                if (obj.has(f)) {
                    obj.put(f, MASK);
                }
            }

            String redacted = objectMapper.writeValueAsString(obj);
            record.setRedactedPayload(redacted);
            return redacted;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON payload for record: " + id, e);
        }
    }
}
