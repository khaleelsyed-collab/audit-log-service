package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuditArchiveServiceTest {

    @Autowired
    AuditRecordService auditRecordService;

    @Autowired
    AuditArchiveService archiveService;

    @Autowired
    AuditRecordRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void archiveShouldMarkExpiredRecords() {
        Instant old = Instant.now().minus(400, ChronoUnit.DAYS);
        Instant recent = Instant.now();

        auditRecordService.appendRecord("E1", "a1", "T", "r1", "p1", old);
        auditRecordService.appendRecord("E2", "a1", "T", "r1", "p2", recent);

        int archived = archiveService.archiveExpiredRecords();
        assertThat(archived).isEqualTo(1);

        AuditRecord first = repository.findAllByOrderBySequenceNumberAsc().get(0);
        AuditRecord second = repository.findAllByOrderBySequenceNumberAsc().get(1);

        assertThat(first.isArchived()).isTrue();
        assertThat(second.isArchived()).isFalse();
    }
}
