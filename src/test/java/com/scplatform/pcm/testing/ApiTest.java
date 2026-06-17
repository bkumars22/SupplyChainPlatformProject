/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.testing;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.*;

/**
 * Marks an API / HTTP integration test (no browser, no UI).
 *
 * <p>Convention: place files under {@code src/apitest/java/} and end the
 * class name in {@code ApiTest}. Run with {@code ./mvnw -P api-tests test}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Tag("api")
public @interface ApiTest {
}
