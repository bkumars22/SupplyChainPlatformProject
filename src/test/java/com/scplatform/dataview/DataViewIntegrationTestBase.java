/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.dataview;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Base for DataView tests that must run against real Postgres, not H2 —
 * the SQL-whitelisting logic in QueryValidator is Postgres-specific
 * (real identifier quoting, real query execution), so testing it against
 * H2 would validate the wrong database's behavior.
 *
 * Uses an embedded (in-process) Postgres engine rather than Testcontainers,
 * so no Docker daemon is required to run these tests.
 *
 * Runs Flyway migrations for real via the "prod"-like Flyway-enabled
 * setup, so V7-V10 (DataView's catalog/access/audit/readonly-role
 * tables) are actually created before each test class runs.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dataview-it")
public abstract class DataViewIntegrationTestBase {

    private static EmbeddedPostgres embeddedPostgres;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        try {
            embeddedPostgres = EmbeddedPostgres.builder().start();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start embedded Postgres", e);
        }

        registry.add("spring.datasource.url",
                () -> embeddedPostgres.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");

        // Force Flyway on for this profile regardless of application.properties'
        // per-profile gating (Flyway is normally only enabled under "prod").
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @AfterAll
    static void stopEmbeddedPostgres() throws IOException {
        if (embeddedPostgres != null) {
            embeddedPostgres.close();
        }
    }
}
