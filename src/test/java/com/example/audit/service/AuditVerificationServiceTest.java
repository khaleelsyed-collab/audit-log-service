package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.dto.ChainVerificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class AuditVerificationServiceTest {

    @Autowired
    AuditRecordService auditRecordService;

    @Autowired
    AuditVerificationService auditVerificationService;

    @Autowired
    AuditRecordRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void validChainShouldVerify() {
        auditRecordService.appendRecord("EVT1", "actor1", "TYPE", "r1", "{\"k\":1}", null);
        auditRecordService.appendRecord("EVT2", "actor1", "TYPE", "r1", "{\"k\":2}", null);

        ChainVerificationResponse resp = auditVerificationService.verifyChain();
        assertThat(resp.isChainIntact()).isTrue();
    }

    @Test
    void tamperingShouldBreakChain() {
        AuditRecord created = auditRecordService.appendRecord("EVT1", "actor1", "TYPE", "r1", "{\"k\":1}", null);
        auditRecordService.appendRecord("EVT2", "actor1", "TYPE", "r1", "{\"k\":2}", null);

        // Tamper the stored payload directly via JDBC to simulate out-of-band modification
        String tamperedPayload = "{\"k\":999}";
        jdbcTemplate.update("UPDATE audit_records SET payload = ? WHERE id = ?", tamperedPayload, created.getId());

        // Clear persistence context so subsequent repository reads reflect DB state
        entityManager.clear();

        ChainVerificationResponse resp = auditVerificationService.verifyChain();
        assertThat(resp.isChainIntact()).isFalse();
    }
}
