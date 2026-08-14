package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.util.HashUtil;
import org.springframework.stereotype.Service;
import com.example.audit.controller.MerkleRootResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that builds a Merkle tree over audit record hashes and produces the Merkle root.
 *
 * Implementation notes:
 * - Uses the stored record.hash values (assumed to be hex-encoded SHA-256 strings).
 * - Uses SHA-256 to combine child nodes: nodeHash = SHA-256(left || right) where left and right
 *   are the lowercase hex strings of child hashes concatenated.
 * - If a layer has an odd number of nodes, the last node is duplicated to form a pair.
 */
@Service
public class MerkleTreeService {

    private final AuditRecordRepository repository;

    public MerkleTreeService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Compute the Merkle root for all audit records ordered by sequenceNumber ascending.
     *
     * @return MerkleRootResult containing totalRecords, merkleRoot (empty string if no records), and generatedAt timestamp
     */
    public MerkleRootResponse computeMerkleRoot() {
        List<AuditRecord> records = repository.findAllByOrderBySequenceNumberAsc();
        int total = records.size();
        Instant generatedAt = Instant.now();

        if (total == 0) {
            return new MerkleRootResponse(total, "", generatedAt);
        }

        // Initialize leaves as the stored hash values
        List<String> layer = new ArrayList<>(total);
        for (AuditRecord r : records) {
            layer.add(r.getHash());
        }

        // Build tree upward until a single root remains
        while (layer.size() > 1) {
            List<String> nextLayer = new ArrayList<>((layer.size() + 1) / 2);
            int i = 0;
            while (i < layer.size()) {
                String left = layer.get(i);
                String right = (i + 1 < layer.size()) ? layer.get(i + 1) : left; // duplicate last if odd
                String combined = left + right;
                String nodeHash = HashUtil.sha256Hex(combined);
                nextLayer.add(nodeHash);
                i += 2;
            }
            layer = nextLayer;
        }

        String merkleRoot = layer.get(0);
        return new com.example.audit.controller.MerkleRootResponse(total, merkleRoot, generatedAt);
    }
}
