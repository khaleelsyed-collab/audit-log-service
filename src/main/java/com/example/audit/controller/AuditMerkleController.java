package com.example.audit.controller;

import com.example.audit.dto.MerkleRootResponse;
import com.example.audit.service.MerkleTreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing Merkle-tree related endpoints for the audit log.
 */
@RestController
@RequestMapping("/audit")
public class AuditMerkleController {

    private final MerkleTreeService merkleTreeService;

    public AuditMerkleController(MerkleTreeService merkleTreeService) {
        this.merkleTreeService = merkleTreeService;
    }

    /**
     * GET /audit/merkle/root
     *
     * Computes the Merkle root over stored record.hash values and returns a summary.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/merkle/root")
    public ResponseEntity<MerkleRootResponse> getMerkleRoot() {
        MerkleRootResponse resp = merkleTreeService.computeMerkleRoot();
        return ResponseEntity.ok(resp);
    }
}
