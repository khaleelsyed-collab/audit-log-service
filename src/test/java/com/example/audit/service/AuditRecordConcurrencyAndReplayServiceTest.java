package com.example.audit.service;

import com.example.audit.dto.ChainVerificationResponse;
import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Service-level coverage for replay/duplicate submission and concurrent append behavior.
 *
 * Assessment alignment:
 * - Append-only audit log (duplicate/replay creates distinct records, not in-place updates)
 * - Unique ordered sequence numbers and previousHash linkage under concurrency
 * - Tamper-evident hash chain remains verifiable after concurrent inserts
 * - Database integrity (unique ids/sequences, no orphaned/broken links)
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class AuditRecordConcurrencyAndReplayServiceTest {

    private static final int CONCURRENT_WRITERS = 25;
    private static final String REPLAY_PAYLOAD = "{\"action\":\"transfer\",\"amount\":100}";

    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private AuditVerificationService auditVerificationService;

    @Autowired
    private AuditRecordRepository repository;

    @BeforeEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Duplicate/replay submission creates distinct append-only records and keeps chain intact")
    void duplicateReplaySubmissionCreatesDistinctRecordsAndPreservesChain() {
        AuditRecord first = auditRecordService.appendRecord(
                "FUNDS_TRANSFER", "actor-replay", "ACCOUNT", "acct-1", REPLAY_PAYLOAD, null);
        AuditRecord replay = auditRecordService.appendRecord(
                "FUNDS_TRANSFER", "actor-replay", "ACCOUNT", "acct-1", REPLAY_PAYLOAD, null);

        assertThat(replay.getId()).isNotEqualTo(first.getId());
        assertThat(replay.getSequenceNumber()).isEqualTo(first.getSequenceNumber() + 1);
        assertThat(replay.getPreviousHash()).isEqualTo(first.getHash());
        assertThat(replay.getHash()).isNotEqualTo(first.getHash());

        List<AuditRecord> stored = repository.findAllByOrderBySequenceNumberAsc();
        assertThat(stored).hasSize(2);
        assertThat(stored.get(0).getId()).isEqualTo(first.getId());
        assertThat(stored.get(1).getId()).isEqualTo(replay.getId());

        ChainVerificationResponse verification = auditVerificationService.verifyChain();
        assertThat(verification.isChainIntact()).isTrue();
        assertThat(verification.getFirstBrokenSequence()).isNull();
    }

    @Test
    @DisplayName("Identical payload replay is not idempotent: each call appends a new record")
    void identicalPayloadReplayIsNotIdempotent() {
        int replayCount = 5;
        Set<Long> ids = new HashSet<>();
        Set<Long> sequences = new HashSet<>();

        for (int i = 0; i < replayCount; i++) {
            AuditRecord record = auditRecordService.appendRecord(
                    "LOGIN", "actor-idem", "SESSION", "sess-9", "{\"ip\":\"10.0.0.1\"}", null);
            ids.add(record.getId());
            sequences.add(record.getSequenceNumber());
        }

        assertThat(ids).hasSize(replayCount);
        assertThat(sequences).hasSize(replayCount);
        assertThat(repository.count()).isEqualTo(replayCount);

        ChainVerificationResponse verification = auditVerificationService.verifyChain();
        assertThat(verification.isChainIntact()).isTrue();
    }

    @Test
    @DisplayName("Concurrent appends keep unique ordered sequences, previousHash links, and a valid chain")
    void concurrentAppendsPreserveSequenceUniquenessHashLinksAndChainIntegrity() throws Exception {
        CountDownLatch ready = new CountDownLatch(CONCURRENT_WRITERS);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger failures = new AtomicInteger();
        List<Future<AuditRecord>> futures = new ArrayList<>(CONCURRENT_WRITERS);

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_WRITERS);
        try {
            for (int i = 0; i < CONCURRENT_WRITERS; i++) {
                final int worker = i;
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(10, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Timed out waiting for concurrent start");
                    }
                    try {
                        return auditRecordService.appendRecord(
                                "CONCURRENT_EVENT",
                                "actor-" + worker,
                                "ACCOUNT",
                                "acct-" + worker,
                                "{\"worker\":" + worker + "}",
                                null);
                    } catch (RuntimeException ex) {
                        failures.incrementAndGet();
                        throw ex;
                    }
                }));
            }

            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            List<AuditRecord> created = new ArrayList<>(CONCURRENT_WRITERS);
            for (Future<AuditRecord> future : futures) {
                created.add(future.get(30, TimeUnit.SECONDS));
            }

            assertThat(failures.get()).isZero();
            assertThat(created).hasSize(CONCURRENT_WRITERS);
            assertThat(repository.count()).isEqualTo(CONCURRENT_WRITERS);

            assertChainIntegrityAndDatabaseConstraints(created);
        } finally {
            executor.shutdownNow();
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        }
    }

    private void assertChainIntegrityAndDatabaseConstraints(List<AuditRecord> createdInMemory) {
        Set<Long> ids = new HashSet<>();
        Set<Long> sequences = new HashSet<>();
        for (AuditRecord record : createdInMemory) {
            assertThat(record.getId()).isNotNull();
            assertThat(record.getSequenceNumber()).isPositive();
            assertThat(ids.add(record.getId()))
                    .as("duplicate primary key detected: %s", record.getId())
                    .isTrue();
            assertThat(sequences.add(record.getSequenceNumber()))
                    .as("duplicate sequenceNumber detected: %s", record.getSequenceNumber())
                    .isTrue();
        }

        List<AuditRecord> persisted = repository.findAllByOrderBySequenceNumberAsc();
        assertThat(persisted).hasSize(CONCURRENT_WRITERS);

        for (int i = 0; i < persisted.size(); i++) {
            AuditRecord current = persisted.get(i);
            assertThat(current.getSequenceNumber()).isEqualTo(i + 1L);

            if (i == 0) {
                assertThat(current.getPreviousHash()).isEqualTo("audit-log-genesis-v1");
            } else {
                AuditRecord previous = persisted.get(i - 1);
                assertThat(current.getPreviousHash())
                        .as("previousHash must link sequence %d to sequence %d",
                                current.getSequenceNumber(), previous.getSequenceNumber())
                        .isEqualTo(previous.getHash());
            }
        }

        Set<Long> persistedIds = new HashSet<>();
        Set<Long> persistedSequences = new HashSet<>();
        for (AuditRecord record : persisted) {
            assertThat(persistedIds.add(record.getId())).isTrue();
            assertThat(persistedSequences.add(record.getSequenceNumber())).isTrue();
        }
        assertThat(persistedIds).hasSize(CONCURRENT_WRITERS);
        assertThat(persistedSequences).hasSize(CONCURRENT_WRITERS);

        ChainVerificationResponse verification = auditVerificationService.verifyChain();
        assertThat(verification.isChainIntact()).isTrue();
        assertThat(verification.getMessage()).isEqualTo("Chain intact");
        assertThat(verification.getFirstBrokenSequence()).isNull();
        assertThat(verification.getViolation()).isNull();
    }
}
