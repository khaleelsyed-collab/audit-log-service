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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditRecordVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getVerifyWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/verify/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanVerifyExistingRecord() throws Exception {
        AuditRecordRequest r = makeRequest();
        String created = mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode c = objectMapper.readTree(created);
        Long id = c.get("id").asLong();

        mockMvc.perform(get("/audit/verify/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.valid").isBoolean());
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void invalidIdTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit/verify/abc")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void recordNotFoundReturnsServerError() throws Exception {
        org.junit.jupiter.api.function.Executable exec = () -> mockMvc.perform(get("/audit/verify/9999999")).andReturn();
        Exception ex = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, exec);
        org.assertj.core.api.Assertions.assertThat(ex.getMessage()).contains("AuditRecord not found");
    }

    private AuditRecordRequest makeRequest() {
        AuditRecordRequest r = new AuditRecordRequest();
        r.setEventType("LOGIN");
        r.setActorId("user-verify");
        r.setResourceType("ACCOUNT");
        r.setResourceId("acct-v");
        r.setPayload("{\"status\":\"ACTIVE\"}");
        return r;
    }
}
