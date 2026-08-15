package com.example.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the Audit Log Service.
 *
 * Features:
 * - HTTP Basic authentication
 * - BCrypt password encoding
 * - Stateless session management
 * - CSRF disabled for REST APIs
 * - Explicit HTTP security headers
 * - Method-level authorization using @PreAuthorize
 *
 * Note:
 * This assessment uses HTTP Basic authentication for simplicity.
 * Production deployments should enforce HTTPS, use external identity
 * management (OAuth2/OIDC), and restrict CORS to trusted origins.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build());

        manager.createUser(User.withUsername("auditor")
                .password(passwordEncoder.encode("auditor123"))
                .roles("AUDITOR")
                .build());

        manager.createUser(User.withUsername("system")
                .password(passwordEncoder.encode("system123"))
                .roles("SYSTEM")
                .build());

        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headers -> {

                    headers.contentTypeOptions(Customizer.withDefaults());

                    headers.frameOptions(frame -> frame.deny());

                    headers.referrerPolicy(referrer ->
                            referrer.policy(
                                    ReferrerPolicyHeaderWriter.ReferrerPolicy
                                            .STRICT_ORIGIN_WHEN_CROSS_ORIGIN));


                    // HSTS headers are sent only for HTTPS requests.
                    headers.httpStrictTransportSecurity(hsts ->
                            hsts
                                    .includeSubDomains(true)
                                    .maxAgeInSeconds(31536000));
                })

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        .anyRequest()
                        .authenticated())

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Assessment configuration:
         * CORS is intentionally permissive for local testing.
         *
         * Production deployments should:
         * - restrict allowed origins
         * - restrict allowed headers
         * - configure allowed methods as required
         */
        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(
                List.of("GET", "POST", "OPTIONS"));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}