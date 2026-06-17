/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PanelFormTest {

    @Test
    void testDefaultConstructor() {
        PanelForm obj = new PanelForm();
        assertNotNull(obj);
    }
}
