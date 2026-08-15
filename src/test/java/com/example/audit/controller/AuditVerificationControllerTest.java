package com.example.audit.controller;

import com.example.audit.dto.AuditRecordRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void verifyWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/verify"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/audit/verify/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanVerifyRecords() throws Exception {
        long id = createAuditRecord();

        mockMvc.perform(get("/audit/verify"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/audit/verify/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void auditorCanVerifyRecords() throws Exception {
        long id = createAuditRecord();

        mockMvc.perform(get("/audit/verify"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/audit/verify/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "system", roles = "SYSTEM")
    void systemCannotVerifyRecords() throws Exception {
        mockMvc.perform(get("/audit/verify"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/audit/verify/1"))
                .andExpect(status().isForbidden());
    }

    private long createAuditRecord() throws Exception {
        AuditRecordRequest request = new AuditRecordRequest();
        request.setEventType("LOGIN");
        request.setActorId("user-7");
        request.setResourceType("session");
        request.setResourceId("session-101");
        request.setPayload("{\"action\":\"login\"}");

        MvcResult result = mockMvc.perform(post("/audit")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }
}
