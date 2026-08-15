package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class AuditRecordServiceTest {

    @Autowired
    AuditRecordService auditRecordService;

    @Autowired
    AuditRecordRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void appendShouldAssignSequenceAndComputeHash() {
        AuditRecord r1 = auditRecordService.appendRecord("USER_LOGIN", "alice", "ACCOUNT", "1001", "{\"ip\":\"1.2.3.4\"}", null);
        AuditRecord r2 = auditRecordService.appendRecord("USER_LOGOUT", "alice", "ACCOUNT", "1001", "{\"ip\":\"1.2.3.4\"}", null);

        assertThat(r1.getSequenceNumber()).isEqualTo(1L);
        assertThat(r2.getSequenceNumber()).isEqualTo(2L);
        assertThat(r1.getHash()).isNotNull().isNotEmpty();
        assertThat(r2.getHash()).isNotNull().isNotEmpty();
        assertThat(r2.getPreviousHash()).isEqualTo(r1.getHash());
    }
}
