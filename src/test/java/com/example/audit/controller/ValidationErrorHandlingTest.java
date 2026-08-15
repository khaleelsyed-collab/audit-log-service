package com.example.audit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ValidationErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void missingRequiredFieldsReturnsBadRequest() throws Exception {
        String json = "{\"eventType\":\"LOGIN\"}";

        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/audit"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void blankStringsReturnBadRequest() throws Exception {
        String json = "{\"eventType\":\" \",\"actorId\":\"user-1\",\"resourceType\":\"account\",\"resourceId\":\"acct-1\",\"payload\":\"ok\"}";

        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void invalidJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void invalidPathVariableReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit/verify/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void payloadExceedingSizeLimitReturnsBadRequest() throws Exception {
        String payload = "x".repeat(5001);
        String json = "{\"eventType\":\"LOGIN\",\"actorId\":\"user-1\",\"resourceType\":\"account\",\"resourceId\":\"acct-1\",\"payload\":\"" + payload + "\"}";

        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void invalidPageParametersReturnBadRequest() throws Exception {
        mockMvc.perform(get("/audit/search?page=-1&size=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void malformedTimestampReturnsBadRequest() throws Exception {
        String json = "{"
                + "\"eventType\":\"LOGIN\","
                + "\"actorId\":\"user-1\","
                + "\"resourceType\":\"account\","
                + "\"resourceId\":\"acct-1\","
                + "\"payload\":\"{\\\"ok\\\":true}\","
                + "\"timestamp\":\"not-a-timestamp\""
                + "}";

        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void malformedSearchTimeQueryReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit").param("from", "yesterday"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void eventTypeExceedingSizeLimitReturnsBadRequest() throws Exception {
        String eventType = "E".repeat(129);
        String json = "{\"eventType\":\"" + eventType + "\",\"actorId\":\"user-1\",\"resourceType\":\"account\",\"resourceId\":\"acct-1\",\"payload\":\"ok\"}";

        mockMvc.perform(post("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void searchPageSizeAboveMaximumReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit/search").param("page", "0").param("size", "201"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void searchFilterExceedingSizeLimitReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit/search").param("actorId", "a".repeat(129)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void emptyRedactionFieldsReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/audit/redact/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fields\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void blankRedactionFieldNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/audit/redact/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fields\":[\" \"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void nonPositivePayloadPathVariableReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit/0/payload"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void invalidRedactedQueryParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/audit/1/payload").param("redacted", "not-a-boolean"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unsupportedHttpMethodReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(delete("/audit"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(put("/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"LOGIN\",\"actorId\":\"user-1\",\"resourceType\":\"account\",\"resourceId\":\"acct-1\",\"payload\":\"ok\"}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void unsupportedMediaTypeReturnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/audit")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("eventType=LOGIN"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
