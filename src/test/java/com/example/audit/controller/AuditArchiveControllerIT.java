package com.example.audit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditArchiveControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void archiveWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(post("/audit/archive"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanArchive() throws Exception {
        mockMvc.perform(post("/audit/archive"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void auditorCannotArchive() throws Exception {
        mockMvc.perform(post("/audit/archive"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "system", roles = "SYSTEM")
    void systemCannotArchive() throws Exception {
        mockMvc.perform(post("/audit/archive"))
                .andExpect(status().isForbidden());
    }
}
