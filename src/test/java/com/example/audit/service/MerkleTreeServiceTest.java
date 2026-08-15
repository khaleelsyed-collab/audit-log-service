package com.example.audit.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MerkleTreeServiceTest {

    @Test
    void computeMerkleRootFromHashesShouldBeDeterministic() {
        MerkleTreeService svc = new MerkleTreeService(null);

        List<String> hashes = List.of(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
        );

        String root1 = svc.computeMerkleRootFromHashes(hashes);
        String root2 = svc.computeMerkleRootFromHashes(hashes);

        assertThat(root1).isNotBlank();
        assertThat(root1).isEqualTo(root2);
    }

    @Test
    void emptyListShouldReturnEmptyString() {
        MerkleTreeService svc = new MerkleTreeService(null);
        String root = svc.computeMerkleRootFromHashes(List.of());
        assertThat(root).isEmpty();
    }

    @Test
    void sameInputProducesSameRoot() {
        MerkleTreeService svc = new MerkleTreeService(null);
        List<String> hashes = List.of("aa","aa","aa");
        String r1 = svc.computeMerkleRootFromHashes(hashes);
        String r2 = svc.computeMerkleRootFromHashes(List.of("aa","aa","aa"));
        assertThat(r1).isEqualTo(r2);
    }
}
