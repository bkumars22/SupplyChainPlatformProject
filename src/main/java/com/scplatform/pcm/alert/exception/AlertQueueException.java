/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.exception;

import java.io.Serial;

/**
 * Custom exception for alert queue operations.
 * Alert failures are non-fatal — the business operation should continue
 * even if alert queuing fails.
 */
public class AlertQueueException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public AlertQueueException(String message) {
        super(message);
    }

    public AlertQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertQueueException(Throwable cause, String message) {
        super(message, cause);
    }
}

