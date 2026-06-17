/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.writter.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SCPlatformApplicationContextAwareTest {

    @Test
    void isInterface() {
        assertTrue(SCPlatformApplicationContextAware.class.isInterface());
    }

    @Test
    void exposesSetApplicationContextMethod() throws NoSuchMethodException {
        assertNotNull(SCPlatformApplicationContextAware.class.getMethod(
                "setApplicationContext",
                com.scplatform.pcm.authentication.dto.ApplicationContext.class));
    }
}
