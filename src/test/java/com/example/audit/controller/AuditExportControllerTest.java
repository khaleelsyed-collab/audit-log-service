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
class AuditExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getExportWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/export"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SYSTEM")
    void systemCannotExport() throws Exception {
        mockMvc.perform(get("/audit/export")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanExportAndFilter() throws Exception {
        // create two records with different actorId/resourceType
        AuditRecordRequest r1 = makeRequest("user-A", "ACCOUNT", "acct-A");
        AuditRecordRequest r2 = makeRequest("user-B", "ACCOUNT", "acct-B");

        String c1 = mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String c2 = mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode resp1 = objectMapper.readTree(c1);
        JsonNode resp2 = objectMapper.readTree(c2);
        assertThat(resp1.get("id").asLong()).isGreaterThan(0);
        assertThat(resp2.get("id").asLong()).isGreaterThan(0);

        // export without filters
        String bundle = mockMvc.perform(get("/audit/export"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").isNumber())
                .andReturn().getResponse().getContentAsString();

        JsonNode bundleJson = objectMapper.readTree(bundle);
        assertThat(bundleJson.get("totalRecords").asInt()).isGreaterThanOrEqualTo(2);

        // export with actor filter
        String filtered = mockMvc.perform(get("/audit/export").param("actorId", "user-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value("user-A"))
                .andReturn().getResponse().getContentAsString();

        JsonNode filteredJson = objectMapper.readTree(filtered);
        assertThat(filteredJson.get("totalRecords").asInt()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidFilterValidation() throws Exception {
        // actorId exceeding size (max 128) should trigger 400
        String longActor = "a".repeat(300);
        mockMvc.perform(get("/audit/export").param("actorId", longActor))
                .andExpect(status().isBadRequest());
    }

    private AuditRecordRequest makeRequest(String actor, String rtype, String rid) {
        AuditRecordRequest r = new AuditRecordRequest();
        r.setEventType("LOGIN");
        r.setActorId(actor);
        r.setResourceType(rtype);
        r.setResourceId(rid);
        r.setPayload("{\"status\":\"ACTIVE\"}");
        return r;
    }
}
