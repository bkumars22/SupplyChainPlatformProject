/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.scheduler.controller;

import com.scplatform.pcm.scheduler.service.JobLoaderService;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class QuartzStartupListenerTest {

    @Test
    void hasComponentAnnotation() {
        assertNotNull(QuartzStartupListener.class.getAnnotation(Component.class));
    }

    @Test
    void contextDestroyed_swallowsExceptionWhenSchedulerNull() {
        QuartzStartupListener listener = new QuartzStartupListener(mock(JobLoaderService.class));
        assertDoesNotThrow(listener::contextDestroyed);
    }

    @Test
    void contextInitialized_throwsRuntimeOnQuartzPropsMissing() {
        QuartzStartupListener listener = new QuartzStartupListener(mock(JobLoaderService.class));
        // quartz.properties is not on the test classpath -> StdSchedulerFactory throws,
        // and the listener wraps it into RuntimeException.
        assertThrows(RuntimeException.class, listener::contextInitialized);
    }
}
