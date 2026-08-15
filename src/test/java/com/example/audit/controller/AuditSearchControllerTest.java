package com.example.audit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSearchWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanSearchAndExport() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/audit/export"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void auditorCanSearchAndExport() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/audit/export"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "system", roles = "SYSTEM")
    void systemCanSearchButCannotExport() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/audit/export"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "viewer", roles = "USER")
    void unauthorizedRoleCannotSearch() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isForbidden());
    }
}
