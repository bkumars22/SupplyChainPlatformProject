/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.auditlog;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String entityType() default "";
    String action() default "";
}
