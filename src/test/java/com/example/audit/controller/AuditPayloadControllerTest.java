package com.example.audit.controller;

import com.example.audit.dto.AuditRecordRequest;
import com.example.audit.dto.RedactionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuditPayloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/audit/1/payload"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanRetrievePayload() throws Exception {
        AuditRecordRequest req = makeRequest();
        String create = mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(create).get("id").asLong();

        mockMvc.perform(get("/audit/" + id + "/payload"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.payload").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postWithoutProperRoleForPayloadAccess() throws Exception {
        // create a record as ADMIN (allowed) then try to access payload as SYSTEM (forbidden)
        AuditRecordRequest req = makeRequest();
        String create = mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode resp = objectMapper.readTree(create);
        Long id = resp.get("id").asLong();

        // SYSTEM should be forbidden to read payload (only ADMIN/AUDITOR allowed)
        mockMvc.perform(get("/audit/" + id + "/payload").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("sys").roles("SYSTEM")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SYSTEM")
    void systemCanCreateAndAuditorCanRetrievePayload_RedactedFlow() throws Exception {
        AuditRecordRequest req = makeRequestWithEmail();

        String created = mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode createdJson = objectMapper.readTree(created);
        Long id = createdJson.get("id").asLong();

        // redact the "email" field as ADMIN
        RedactionRequest redaction = new RedactionRequest();
        redaction.setFields(List.of("email"));

        mockMvc.perform(post("/audit/redact/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(redaction))
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        // Auditor reads redacted payload
        mockMvc.perform(get("/audit/" + id + "/payload?redacted=true").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("aud").roles("AUDITOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.redacted").value(true))
                .andExpect(jsonPath("$.payload").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void invalidIdReturnsServerError() throws Exception {
        // Non-existent id currently causes the service to throw RuntimeException.
        // Assert that performing the request results in an exception with expected message.
        org.junit.jupiter.api.function.Executable exec = () -> mockMvc.perform(get("/audit/99999999/payload")).andReturn();
        Exception ex = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, exec);
        org.assertj.core.api.Assertions.assertThat(ex.getMessage()).contains("AuditRecord not found");
    }

    private AuditRecordRequest makeRequest() {
        AuditRecordRequest r = new AuditRecordRequest();
        r.setEventType("LOGIN");
        r.setActorId("user-1");
        r.setResourceType("ACCOUNT");
        r.setResourceId("acct-1");
        r.setPayload("{\"status\":\"ACTIVE\"}");
        return r;
    }

    private AuditRecordRequest makeRequestWithEmail() {
        AuditRecordRequest r = makeRequest();
        r.setPayload("{\"email\":\"user@example.com\",\"status\":\"ACTIVE\"}");
        return r;
    }
}
