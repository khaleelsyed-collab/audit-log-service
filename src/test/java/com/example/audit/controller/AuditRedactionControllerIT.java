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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditRedactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void redactWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(post("/audit/redact/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fields\":[\"email\"]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanRedactAuditRecord() throws Exception {
        long id = createAuditRecordAsAdmin();

        mockMvc.perform(post("/audit/redact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fields\":[\"email\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void auditorCannotRedactAuditRecord() throws Exception {
        long id = createAuditRecordAsAdmin();

        mockMvc.perform(post("/audit/redact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fields\":[\"email\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "system", roles = "SYSTEM")
    void systemCannotRedactAuditRecord() throws Exception {
        long id = createAuditRecordAsAdmin();

        mockMvc.perform(post("/audit/redact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fields\":[\"email\"]}"))
                .andExpect(status().isForbidden());
    }

    private long createAuditRecordAsAdmin() throws Exception {
        AuditRecordRequest request = new AuditRecordRequest();
        request.setEventType("UPDATE");
        request.setActorId("user-15");
        request.setResourceType("profile");
        request.setResourceId("profile-77");
        request.setPayload("{\"email\":\"user@example.com\",\"name\":\"Alice\"}");

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
