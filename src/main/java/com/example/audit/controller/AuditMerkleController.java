package com.example.audit.controller;

import com.example.audit.dto.MerkleRootResponse;
import com.example.audit.service.MerkleTreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller exposing Merkle-tree related endpoints for the audit log.
 */
@RestController
@RequestMapping("/audit")
@Tag(name = "Merkle", description = "Merkle tree endpoints")
@SecurityRequirement(name = "basicAuth")
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
    @Operation(summary = "Get Merkle root summary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    @GetMapping("/merkle/root")
    public ResponseEntity<MerkleRootResponse> getMerkleRoot() {
        MerkleRootResponse resp = merkleTreeService.computeMerkleRoot();
        return ResponseEntity.ok(resp);
    }
}
