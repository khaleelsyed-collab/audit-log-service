package com.example.audit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration: defines the HTTP Basic security scheme used by the API.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Audit Log Service API", version = "1.0"))
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {
    // Intentionally left empty — annotations configure OpenAPI generation.
}
