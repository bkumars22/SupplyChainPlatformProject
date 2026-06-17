/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.testing;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.*;

/**
 * Marks a fast, in-process unit test. Equivalent to {@code @Tag("unit")} but
 * preferred because the IDE autocompletes it and a typo is a compile error.
 *
 * <p>Convention: tests under {@code src/test/java/} are unit tests by source
 * root, so this annotation is only needed when a test in a non-standard
 * location must be explicitly classified.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Tag("unit")
public @interface UnitTest {
}
