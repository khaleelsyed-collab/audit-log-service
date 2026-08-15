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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanCreateAndSearchAuditRecords() throws Exception {
        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/audit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void auditorCanSearchButCannotCreateAuditRecords() throws Exception {
        mockMvc.perform(get("/audit"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "system", roles = "SYSTEM")
    void systemCanCreateAndSearchAuditRecords() throws Exception {
        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/audit"))
                .andExpect(status().isOk());
    }

    private AuditRecordRequest validRequest() {
        AuditRecordRequest request = new AuditRecordRequest();
        request.setEventType("LOGIN");
        request.setActorId("user-42");
        request.setResourceType("account");
        request.setResourceId("acct-900");
        request.setPayload("{\"status\":\"success\"}");
        return request;
    }
}
