package com.example.audit.service;

import com.example.audit.controller.ExportBundleResponse;
import com.example.audit.controller.ExportRecordResponse;
import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service providing read-only export of audit records as verifiable bundles.
 */
@Service
public class AuditExportService {

    private final AuditRecordRepository repository;
    private final MerkleTreeService merkleTreeService;

    public AuditExportService(AuditRecordRepository repository,
                              MerkleTreeService merkleTreeService) {
        this.repository = repository;
        this.merkleTreeService = merkleTreeService;
    }

    /**
     * Export audit records filtered by actor or resource.
     * If no filters are provided, exports all records.
     *
     * @param actorId optional actor id
     * @param resourceType optional resource type
     * @param resourceId optional resource id
     * @return verifiable export bundle
     */
    public ExportBundleResponse export(String actorId,
                                       String resourceType,
                                       String resourceId) {

        List<AuditRecord> records;

        if (actorId != null && !actorId.isBlank()) {

            records = repository.findByActorIdOrderBySequenceNumberAsc(actorId);

        } else if (resourceType != null
                && !resourceType.isBlank()
                && resourceId != null
                && !resourceId.isBlank()) {

            records = repository.findByResourceTypeAndResourceIdOrderBySequenceNumberAsc(
                    resourceType,
                    resourceId);

        } else {

            records = repository.findAllByOrderBySequenceNumberAsc();
        }

        List<ExportRecordResponse> dtoList = new ArrayList<>(records.size());
        List<String> hashes = new ArrayList<>(records.size());

        for (AuditRecord record : records) {

            dtoList.add(new ExportRecordResponse(
                    record.getSequenceNumber(),
                    record.getEventType(),
                    record.getActorId(),
                    record.getResourceType(),
                    record.getResourceId(),
                    record.getTimestamp(),
                    record.getPayload(),
                    record.getPreviousHash(),
                    record.getHash()
            ));

            hashes.add(record.getHash());
        }

        long total = records.size();

        Long firstSequence = total > 0
                ? records.get(0).getSequenceNumber()
                : null;

        Long lastSequence = total > 0
                ? records.get(records.size() - 1).getSequenceNumber()
                : null;

        String firstHash = total > 0
                ? records.get(0).getHash()
                : null;

        String lastHash = total > 0
                ? records.get(records.size() - 1).getHash()
                : null;

        String merkleRoot = merkleTreeService.computeMerkleRootFromHashes(hashes);

        return new ExportBundleResponse(
                actorId,
                resourceType,
                resourceId,
                total,
                firstSequence,
                lastSequence,
                firstHash,
                lastHash,
                merkleRoot,
                Instant.now(),
                dtoList
        );
    }
}