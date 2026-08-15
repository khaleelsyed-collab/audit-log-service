package com.example.audit.service;

import com.example.audit.dto.AuditSearchResponse;
import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuditSearchServiceTest {

    @Autowired
    AuditRecordService auditRecordService;

    @Autowired
    AuditSearchService searchService;

    @Autowired
    AuditRecordRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void filterByActorIdShouldReturnOnlyActorRecords() {
        auditRecordService.appendRecord("E1", "actorX", "T", "r1", "p1", null);
        auditRecordService.appendRecord("E2", "actorY", "T", "r1", "p2", null);

        Page<AuditSearchResponse> page = searchService.search("actorX", null, null, null, PageRequest.of(0, 10, Sort.by("sequenceNumber").ascending()));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getActorId()).isEqualTo("actorX");
    }

    @Test
    void filterByResourceTypeAndIdShouldReturnMatchingRecords() {
        auditRecordService.appendRecord("E1", "a1", "ACCOUNT", "1001", "p1", null);
        auditRecordService.appendRecord("E2", "a2", "ACCOUNT", "1002", "p2", null);

        Page<AuditSearchResponse> page = searchService.search(null, null, "ACCOUNT", "1001", PageRequest.of(0, 10, Sort.by("sequenceNumber").ascending()));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getResourceId()).isEqualTo("1001");
    }

    @Test
    void paginationShouldReturnCorrectPage() {
        for (int i = 0; i < 15; i++) {
            auditRecordService.appendRecord("E" + i, "actorP", "T", "rP", "p", null);
        }

        Page<AuditSearchResponse> page0 = searchService.search("actorP", null, null, null, PageRequest.of(0, 10, Sort.by("sequenceNumber").ascending()));
        Page<AuditSearchResponse> page1 = searchService.search("actorP", null, null, null, PageRequest.of(1, 10, Sort.by("sequenceNumber").ascending()));

        assertThat(page0.getContent().size()).isEqualTo(10);
        assertThat(page1.getContent().size()).isEqualTo(5);
        assertThat(page0.getContent().get(0).getSequenceNumber()).isLessThan(page1.getContent().get(0).getSequenceNumber());
    }
}
