/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.loader;

/**
 * Exception thrown when a preprocessor cannot be instantiated.
 */
public class ProcessorInstantiationException extends Exception {

    public ProcessorInstantiationException(String message) {
        super(message);
    }

    public ProcessorInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
