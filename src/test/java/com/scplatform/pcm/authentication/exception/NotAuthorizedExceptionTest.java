/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.authentication.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotAuthorizedExceptionTest {

    @Test
    void constructor_buildsHumanReadableMessage() {
        NotAuthorizedException ex = new NotAuthorizedException("REQUEST", "Save");
        assertEquals("Operation Save not permitted on REQUEST", ex.getMessage());
    }

    @Test
    void constructor_acceptsNullEntityType() {
        NotAuthorizedException ex = new NotAuthorizedException(null, "Read");
        assertEquals("Operation Read not permitted on null", ex.getMessage());
    }

    @Test
    void isCheckedException() {
        assertTrue(Exception.class.isAssignableFrom(NotAuthorizedException.class));
        assertTrue(!RuntimeException.class.isAssignableFrom(NotAuthorizedException.class));
    }
}
