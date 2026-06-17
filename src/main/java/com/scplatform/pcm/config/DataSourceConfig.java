/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.sql.DataSource;

/**
 * Converts Railway's postgresql:// DATABASE_URL to a valid jdbc:postgresql:// URL.
 * Only activates when the DATABASE_URL environment variable is set.
 * When inactive, Spring Boot auto-configures H2 from application.properties (local dev).
 */
@Configuration
public class DataSourceConfig {

    static class DatabaseUrlPresent implements Condition {
        @Override
        public boolean matches(ConditionContext ctx, AnnotatedTypeMetadata metadata) {
            String url = System.getenv("DATABASE_URL");
            return url != null && !url.isBlank();
        }
    }

    @Bean
    @Primary
    @Conditional(DatabaseUrlPresent.class)
    public DataSource railwayDataSource() {
        String raw = System.getenv("DATABASE_URL");
        // Railway supplies postgresql://user:pass@host:port/db — JDBC needs jdbc:postgresql://
        String jdbcUrl;
        if (raw.startsWith("postgresql://")) {
            jdbcUrl = "jdbc:postgresql://" + raw.substring("postgresql://".length());
        } else if (raw.startsWith("postgres://")) {
            jdbcUrl = "jdbc:postgresql://" + raw.substring("postgres://".length());
        } else {
            jdbcUrl = raw; // already jdbc:..., use as-is
        }
        return DataSourceBuilder.create()
            .url(jdbcUrl)
            .driverClassName("org.postgresql.Driver")
            .build();
    }
}
