/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config;

import com.scplatform.api.jwt.JwtAuthFilter;
import com.scplatform.api.security.DemoGuardFilter;
import com.scplatform.api.security.ContentSizeLimitFilter;
import com.scplatform.api.security.HttpsRedirectFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${security.xss.content-security-policy:default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';}")
    private String contentSecurityPolicy;

    @Value("${api.cors.allowed-origins:http://localhost:3000,http://localhost:8081}")
    private String allowedOrigins;

    // Wildcard patterns for Railway and Vercel preview deployments.
    // These are always added in addition to the explicit list above.
    private static final java.util.List<String> ORIGIN_PATTERNS = java.util.List.of(
        "https://*.railway.app",
        "https://*.vercel.app",
        "http://localhost:*"
    );

    @Value("${server.railway.https-redirect:false}")
    private boolean httpsRedirectEnabled;

    private final JwtAuthFilter jwtAuthFilter;
    private final DemoGuardFilter demoGuardFilter;
    private final HttpsRedirectFilter httpsRedirectFilter;
    private final ContentSizeLimitFilter contentSizeLimitFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, DemoGuardFilter demoGuardFilter,
                          HttpsRedirectFilter httpsRedirectFilter,
                          ContentSizeLimitFilter contentSizeLimitFilter) {
        this.jwtAuthFilter          = jwtAuthFilter;
        this.demoGuardFilter        = demoGuardFilter;
        this.httpsRedirectFilter    = httpsRedirectFilter;
        this.contentSizeLimitFilter = contentSizeLimitFilter;
    }

    // ── Chain 1: Mobile REST API (/api/**) — stateless JWT ──────────────────
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/supchain/api/**", "/api/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/login").permitAll()
                    .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(contentSizeLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(demoGuardFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // ── Chain 2: Web App routes — permit all (existing behaviour) ───────────
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(contentSecurityPolicy))
                        .xssProtection(xss -> xss.disable())
                        .frameOptions(frame -> frame.deny())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .referrerPolicy(rp -> rp
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "nosniff"))
                        .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                                "camera=(), microphone=(), geolocation=()"))
                );
        if (httpsRedirectEnabled) {
            http.addFilterBefore(httpsRedirectFilter, UsernamePasswordAuthenticationFilter.class);
        }
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Explicit origins from env var (covers localhost variants for local dev)
        java.util.Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .forEach(config::addAllowedOrigin);
        // Wildcard patterns for Railway preview URLs and Vercel deployments
        config.setAllowedOriginPatterns(ORIGIN_PATTERNS);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-ID"));
        config.setExposedHeaders(List.of("X-Request-ID"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
