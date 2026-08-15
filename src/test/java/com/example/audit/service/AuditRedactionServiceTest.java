package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
class AuditRedactionServiceTest {

    @Autowired
    AuditRecordService auditRecordService;

    @Autowired
    AuditRedactionService redactionService;

    @Autowired
    AuditRecordRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void redactShouldStoreRedactedPayloadAndPreserveOriginal() {
        AuditRecord r = auditRecordService.appendRecord("E1", "a1", "T", "r1", "{\"ssn\":\"1234\",\"name\":\"Bob\"}", null);

        String redacted = redactionService.redactFields(r.getId(), List.of("ssn"));

        AuditRecord stored = repository.findById(r.getId()).get();
        assertThat(stored.getRedactedPayload()).isNotNull();
        assertThat(stored.getRedactedPayload()).contains("********");

        // original payload remains unchanged
        assertThat(stored.getPayload()).contains("1234");

        // hash remains unchanged
        assertThat(stored.getHash()).isEqualTo(r.getHash());
    }
}
