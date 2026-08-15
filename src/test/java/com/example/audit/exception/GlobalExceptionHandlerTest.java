package com.example.audit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_builds400() throws Exception {
        IllegalArgumentException ex = new IllegalArgumentException("invalid arg");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/test/illegal");

        ResponseEntity<?> resp = handler.handleIllegalArgument(ex, req);
        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
    }
}
