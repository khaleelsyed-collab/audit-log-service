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
class AuditStatisticsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getStatsWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/stats")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SYSTEM")
    void systemCannotAccessStats() throws Exception {
        mockMvc.perform(get("/audit/stats")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanGetStats() throws Exception {
        AuditRecordRequest r = makeRequest();
        mockMvc.perform(post("/audit").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/audit/stats")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").isNumber());
    }

    private AuditRecordRequest makeRequest() {
        AuditRecordRequest r = new AuditRecordRequest();
        r.setEventType("LOGIN");
        r.setActorId("user-stats");
        r.setResourceType("ACCOUNT");
        r.setResourceId("acct-stats");
        r.setPayload("{\"status\":\"ACTIVE\"}");
        return r;
    }
}
