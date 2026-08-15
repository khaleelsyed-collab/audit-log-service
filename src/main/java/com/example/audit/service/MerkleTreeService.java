package com.example.audit.service;

import com.example.audit.entity.AuditRecord;
import com.example.audit.repository.AuditRecordRepository;
import com.example.audit.util.HashUtil;
import org.springframework.stereotype.Service;
import com.example.audit.dto.MerkleRootResponse;
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
        List<String> leaves = new ArrayList<>(total);
        for (AuditRecord r : records) {
            leaves.add(r.getHash());
        }

        String merkleRoot = computeMerkleRootFromHashes(leaves);
        return new MerkleRootResponse(total, merkleRoot, generatedAt);
    }

    /**
     * Compute Merkle root given a list of hex-encoded hash strings.
     * If the list is empty returns empty string.
     *
     * @param hashes list of hex-encoded hash strings
     * @return merkle root hex string
     */
    public String computeMerkleRootFromHashes(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return "";
        }
        List<String> layer = new ArrayList<>(hashes);
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
        return layer.get(0);
    }
}
