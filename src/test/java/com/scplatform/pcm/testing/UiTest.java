/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.testing;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.*;

/**
 * Marks a browser-driven Selenium / Cucumber test.
 *
 * <p>Convention: place files under {@code src/uitest/java/} and end the
 * class name in {@code UiTest}. Run with {@code ./mvnw -P uitest test}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Tag("ui")
public @interface UiTest {
}
