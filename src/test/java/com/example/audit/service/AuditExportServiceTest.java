package com.example.audit.service;

import com.example.audit.dto.ExportBundleResponse;
import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class AuditExportServiceTest {

    @Autowired
    AuditRecordService auditRecordService;

    @Autowired
    AuditExportService exportService;

    @Autowired
    AuditRecordRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void exportBundleShouldContainMetadataAndOrderedRecords() {
        auditRecordService.appendRecord("E1", "a1", "T", "r1", "p1", null);
        auditRecordService.appendRecord("E2", "a1", "T", "r1", "p2", null);
        auditRecordService.appendRecord("E3", "a2", "T", "r2", "p3", null);

        ExportBundleResponse bundle = exportService.export(null, null, null);

        assertThat(bundle.getTotalRecords()).isEqualTo(3L);
        assertThat(bundle.getFirstSequence()).isEqualTo(1L);
        assertThat(bundle.getLastSequence()).isEqualTo(3L);
        assertThat(bundle.getMerkleRoot()).isNotNull().isNotEmpty();
        assertThat(bundle.getRecords()).hasSize(3);

        List<AuditRecord> persisted = repository.findAllByOrderBySequenceNumberAsc();
        long prev = -1;
        for (int i = 0; i < bundle.getRecords().size(); i++) {
            assertThat(bundle.getRecords().get(i).getSequenceNumber()).isEqualTo(persisted.get(i).getSequenceNumber());
            long seq = bundle.getRecords().get(i).getSequenceNumber();
            assertThat(seq).isGreaterThan(prev);
            prev = seq;
        }
    }
}
