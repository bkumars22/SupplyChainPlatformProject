/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.thymeleaf.autoconfigure.ThymeleafAutoConfiguration;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke tests for the {@link PcmApplication} entry point.
 *
 * <p>The original {@code @SpringBootTest} context-load test required a real Oracle
 * datasource (production application.properties leaves {@code spring.datasource.url}
 * empty) and no embedded DB is on the test classpath, so it could not run in CI.
 * These metadata checks verify the application bootstrap configuration without
 * starting a Spring context.
 */
class PcmApplicationTests {

    @Test
    void applicationClassIsAnnotatedWithSpringBootApplication() {
        SpringBootApplication ann = PcmApplication.class.getAnnotation(SpringBootApplication.class);
        assertNotNull(ann, "PcmApplication must be annotated with @SpringBootApplication");
    }

    @Test
    void thymeleafAutoConfigurationIsExcluded() {
        SpringBootApplication ann = PcmApplication.class.getAnnotation(SpringBootApplication.class);
        assertNotNull(ann);
        assertTrue(Arrays.asList(ann.exclude()).contains(ThymeleafAutoConfiguration.class),
                "ThymeleafAutoConfiguration should be excluded (project uses JSP via CascadingJspViewResolver)");
    }

    @Test
    void mainMethodIsDeclared() throws NoSuchMethodException {
        // Spring Boot requires a public static void main(String[]) entry point.
        assertNotNull(PcmApplication.class.getDeclaredMethod("main", String[].class));
    }
}
