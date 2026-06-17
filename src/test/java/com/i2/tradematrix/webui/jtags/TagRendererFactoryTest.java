/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * TagRendererFactory is a utility class with a private constructor and only static methods.
 */
class TagRendererFactoryTest {

    @Test
    void test_setRendererGroup_unknownClass_throwsTagRendererException() {
        assertThrows(TagRendererException.class,
                () -> TagRendererFactory.setRendererGroup("com.nonexistent.RendererGroup"));
    }

    @Test
    void test_getRenderer_unknownRenderer_returnsNull() throws TagRendererException {
        // Default renderer group loads successfully; unknown renderer name returns null
        TagRenderer result = TagRendererFactory.getRenderer("unknownRenderer");
        assertNull(result);
    }
}
