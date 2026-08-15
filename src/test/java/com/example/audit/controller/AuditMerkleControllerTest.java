package com.example.audit.controller;

import com.example.audit.dto.AuditRecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditMerkleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMerkleWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/merkle/root")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SYSTEM")
    void systemCannotAccessMerkle() throws Exception {
        mockMvc.perform(get("/audit/merkle/root")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void auditorCanGetMerkleRoot() throws Exception {
        mockMvc.perform(get("/audit/merkle/root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merkleRoot").isString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanGetMerkleRoot() throws Exception {
        // create several records so merkle root can be computed
        AuditRecordRequest r1 = makeRequest("a1");
        AuditRecordRequest r2 = makeRequest("a2");

        mockMvc.perform(post("/audit").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/audit").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/audit/merkle/root")).andExpect(status().isOk())
                .andExpect(jsonPath("$.merkleRoot").isString());
    }

    private AuditRecordRequest makeRequest(String suffix) {
        AuditRecordRequest r = new AuditRecordRequest();
        r.setEventType("LOGIN");
        r.setActorId("user-" + suffix);
        r.setResourceType("ACCOUNT");
        r.setResourceId("acct-" + suffix);
        r.setPayload("{\"status\":\"ACTIVE\"}");
        return r;
    }
}
