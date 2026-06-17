/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.authentication.dto;

/**
 * Exception thrown when a valid user context cannot be located in the session.
 * 
 * <p>This exception is thrown in the following scenarios:
 * <ul>
 *   <li>No session exists for the request</li>
 *   <li>No ApplicationContext found in the session</li>
 *   <li>No authenticated user found in the ApplicationContext</li>
 * </ul>
 * 
 * @author PCM Team
 */
public class InvalidUserContext extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidUserContext() {
        super();
    }

    public InvalidUserContext(String message) {
        super(message);
    }

    public InvalidUserContext(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserContext(Throwable cause) {
        super(cause);
    }
}
